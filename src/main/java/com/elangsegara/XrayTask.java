package com.elangsegara;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;

/**
 * Task class for executing the XRAY Gradle Plugin.
 */
public class XrayTask extends DefaultTask {
    /**
     * Runs the XRAY Gradle Plugin task.
     *
     * @throws IOException If an I/O error occurs.
     */
    @TaskAction
    public void run() throws IOException {
        XrayExtension extension = getProject().getExtensions().findByType(XrayExtension.class);
        if (extension != null) {
            String action = extension.getAction();
            String scenario = extension.getScenario();
            XrayMain.main(new String[]{action, scenario});
        }
    }
}
