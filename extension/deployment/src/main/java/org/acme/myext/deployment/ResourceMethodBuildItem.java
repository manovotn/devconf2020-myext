package org.acme.myext.deployment;

import org.jboss.jandex.MethodInfo;

import io.quarkus.builder.item.MultiBuildItem;

/**
 * 
 *
 */
public final class ResourceMethodBuildItem extends MultiBuildItem {

    private final MethodInfo method;

    public ResourceMethodBuildItem(MethodInfo method) {
        this.method = method;
    }

    public MethodInfo getMethod() {
        return method;
    }

}
