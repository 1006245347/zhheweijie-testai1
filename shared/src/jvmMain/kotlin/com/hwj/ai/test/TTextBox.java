package com.hwj.ai.test;

import mmarquee.automation.AutomationException;
import mmarquee.automation.controls.AutomationBase;
import mmarquee.automation.controls.ElementBuilder;
import mmarquee.automation.controls.ImplementsLegacyIAccessible;
import mmarquee.automation.controls.ImplementsValue;
import mmarquee.automation.pattern.LegacyIAccessible;
import mmarquee.automation.pattern.PatternNotFoundException;

/**
 * @author by jason-何伟杰，2025/4/17
 * des:试试自定义类型
 */
public class TTextBox extends AutomationBase
        implements ImplementsValue, ImplementsLegacyIAccessible {

    public TTextBox(ElementBuilder builder) {
        super(builder);
    }

    /**
     * The legacy IAccessible pattern.
     */
    private LegacyIAccessible accessiblePattern;

    /**
     * Gets the value from the Legacy IAccessible interface.
     *
     * @return The string value
     * @throws PatternNotFoundException Failed to find pattern
     * @throws AutomationException      Issue with automation library
     */
    public String getValueFromIAccessible()
            throws PatternNotFoundException, AutomationException {
        if (this.accessiblePattern == null) {
            try {
                this.accessiblePattern = this.requestAutomationPattern(LegacyIAccessible.class);
            } catch (NullPointerException ex) {
                getLogger().info("No value pattern available");
            }
        }

        try {
            return accessiblePattern.getCurrentValue();
        } catch (NullPointerException ex) {
            return "<Empty>";
        }
    }

    /**
     * Sets the value from the legacy IAccessible interface.
     *
     * @param value The value to set
     * @throws AutomationException Issue with automation library
     */
    public void setValueFromIAccessible(final String value)
            throws AutomationException {
        if (this.accessiblePattern == null) {
            try {
                this.accessiblePattern = this.requestAutomationPattern(LegacyIAccessible.class);
                this.accessiblePattern.setCurrentValue(value);
            } catch (NullPointerException ex) {
                getLogger().info("No value pattern available");
            }
        }
    }
}

