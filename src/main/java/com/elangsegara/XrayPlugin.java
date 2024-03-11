package com.elangsegara;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * The main plugin class for the XRAY Gradle Plugin.
 */
public class XrayPlugin implements Plugin<Project> {
    /**
     * Applies the XRAY Gradle Plugin to the specified project.
     *
     * @param project The project to apply the plugin to.
     */
    @Override
    public void apply(Project project) {
        project.getExtensions().create("xray", XrayExtension.class);
        project.getTasks().create("xray", XrayTask.class);
    }
}
