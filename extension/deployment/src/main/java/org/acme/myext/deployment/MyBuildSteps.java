package org.acme.myext.deployment;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.GET;

import org.acme.myext.ResourceIntercepted;
import org.acme.myext.ResourceMethodInterceptor;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget.Kind;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.MethodInfo;
import org.jboss.logging.Logger;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.AnnotationsTransformerBuildItem;
import io.quarkus.arc.deployment.BeanArchiveIndexBuildItem;
import io.quarkus.arc.processor.AnnotationsTransformer;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

public class MyBuildSteps {

    private static final Logger LOGGER = Logger.getLogger(MyBuildSteps.class.getName());

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
    void collectResouceMethods(BeanArchiveIndexBuildItem beanArchive, BuildProducer<ResourceMethodBuildItem> resourceMethods) {

        IndexView index = beanArchive.getIndex();

        Collection<AnnotationInstance> annotations = index.getAnnotations(GET);
        for (AnnotationInstance annotation : annotations) {
            if (annotation.target().kind() == Kind.METHOD) {
                resourceMethods.produce(new ResourceMethodBuildItem(annotation.target().asMethod()));
            }
        }
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
                    LOGGER.infof("Transforming annotations of a resource method %s", context.getTarget());
                    context.transform().add(RESOURCE_INTERCEPTED).done();
                }
            }
        });
    }

}
