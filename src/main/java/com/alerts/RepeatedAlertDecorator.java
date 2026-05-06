package com.alerts;

// Adds repeat interval information to an alert
public class RepeatedAlertDecorator extends AlertDecorator {
    private final int repeatIntervalSeconds;

    public RepeatedAlertDecorator(Alert decoratedAlert, int repeatIntervalSeconds) {
        super(decoratedAlert);
        this.repeatIntervalSeconds = repeatIntervalSeconds;
    }

    @Override
    public String getCondition() {
        return decoratedAlert.getCondition()
            + " [Repeat every " + repeatIntervalSeconds + "s]";
    }

    public int getRepeatInterval() {
        return repeatIntervalSeconds;
    }
}