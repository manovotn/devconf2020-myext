package org.acme.myext.deployment.config;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(phase = ConfigPhase.BUILD_TIME)
public class MyExtensionConfig {

    /**
     * Regex that defines what resources should be affected by this extension based on the package of the class to be included..
     * By default, asterisk(*) is used, meaning all resources will be modified.
     */
    @ConfigItem(defaultValue = ".*")
    public String regularExpression;

    /**
     * Controls the enablement of whole extension. If disabled, no action will be taken.
     * By default, false is present.
     */
    @ConfigItem(defaultValue = "false")
    public boolean disable;
}
