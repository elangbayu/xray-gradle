package com.elangsegara;

/**
 * Extension class for configuring the XRAY Gradle Plugin.
 */
public class XrayExtension {
    private String action;
    private String scenario;

    /**
     * Returns the action to be performed by the XRAY Gradle Plugin.
     *
     * @return The action.
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the action to be performed by the XRAY Gradle Plugin.
     *
     * @param action The action to set.
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Returns the scenario for the XRAY Gradle Plugin.
     *
     * @return The scenario.
     */
    public String getScenario() {
        return scenario;
    }

    /**
     * Sets the scenario for the XRAY Gradle Plugin.
     *
     * @param scenario The scenario to set.
     */
    public void setScenario(String scenario) {
        this.scenario = scenario;
    }
}
