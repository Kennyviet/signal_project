package com.alerts;

// Adds priority tagging to an alert
public class PriorityAlertDecorator extends AlertDecorator {
    private final String priorityLevel;

    public PriorityAlertDecorator(Alert decoratedAlert, String priorityLevel) {
        super(decoratedAlert);
        this.priorityLevel = priorityLevel;
    }

    @Override
    public String getCondition() {
        return "[" + priorityLevel + "] " + decoratedAlert.getCondition();
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }
}