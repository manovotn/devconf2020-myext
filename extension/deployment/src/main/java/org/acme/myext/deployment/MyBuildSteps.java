package org.acme.myext.deployment;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.GET;

import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import org.acme.myext.ResourceIntercepted;
import org.acme.myext.ResourceMethodInterceptor;
import org.acme.myext.SummarizingRecorder;
import org.acme.myext.deployment.config.MyExtensionConfig;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget.Kind;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.MethodInfo;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.AnnotationsTransformerBuildItem;
import io.quarkus.arc.deployment.BeanArchiveIndexBuildItem;
import io.quarkus.arc.processor.AnnotationsTransformer;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

public class MyBuildSteps {

    private static final DotName GET = DotName.createSimple(GET.class.getName());
    private static final DotName RESOURCE_INTERCEPTED = DotName.createSimple(ResourceIntercepted.class.getName());

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem("my-1st-ext");
    }

    @BuildStep
    AdditionalBeanBuildItem beans() {
        return AdditionalBeanBuildItem.builder().addBeanClass(ResourceMethodInterceptor.class).build();
    }

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    void collectResourceMethods(BeanArchiveIndexBuildItem beanArchive, BuildProducer<ResourceMethodBuildItem> resourceMethods,
                                MyExtensionConfig config, SummarizingRecorder recorder) {

        if (config.disable) {
            recorder.summarizeBootstrap("Our custom extension is disabled.");
            return;
        }

        IndexView index = beanArchive.getIndex();
        Set<MethodInfo> affectedMethods = new HashSet<>();

        Collection<AnnotationInstance> annotations = index.getAnnotations(GET);
        for (AnnotationInstance annotation : annotations) {
            if (annotation.target().kind() == Kind.METHOD) {
                // filter out methods based on config expression
                MethodInfo methodInfo = annotation.target().asMethod();
                if (methodInfo.declaringClass().name().toString().matches(config.regularExpression)) {
                    resourceMethods.produce(new ResourceMethodBuildItem(methodInfo));
                    affectedMethods.add(methodInfo);
                }
            }
        }

        // use the recorder to summarize config and affected methods
        String msg = "Our custom extension is enabled, with the regular expression being: \"" + config.regularExpression
                + "\". Affected methods are: "
                + affectedMethods.stream().map(m -> m.declaringClass().toString() + "#" + m.name()).collect(Collectors.toSet());
        recorder.summarizeBootstrap(msg);
    }

    @BuildStep
    AnnotationsTransformerBuildItem transformAnnotations(List<ResourceMethodBuildItem> resourceMethods) {

        Set<MethodInfo> methods = resourceMethods.stream().map(ResourceMethodBuildItem::getMethod).collect(Collectors.toSet());

        return new AnnotationsTransformerBuildItem(new AnnotationsTransformer() {

            public boolean appliesTo(Kind kind) {
                return Kind.METHOD.equals(kind);
            }

            @Override
            public void transform(TransformationContext context) {
                if (methods.contains(context.getTarget())) {
                    context.transform().add(RESOURCE_INTERCEPTED).done();
                }
            }
        });
    }

}
