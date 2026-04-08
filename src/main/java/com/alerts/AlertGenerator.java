package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met.
 * This class relies on a {@link DataStorage} instance to access patient data.
 */
public class AlertGenerator {

    // Added 'final' since this reference should not change after initialization (Google Java Style)
    private final DataStorage dataStorage;

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     *
     * @param dataStorage the data storage system that provides access to patient data
     */
    public AlertGenerator(DataStorage dataStorage) {
        // No change needed (already camelCase and correct style)
        this.dataStorage = dataStorage;
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions are met.
     * If a condition is met, an alert is triggered via the {@link #triggerAlert(Alert)} method.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        if (dataStorage == null || patient == null) {
            throw new IllegalArgumentException("DataStorage and patient must not be null");
        }

        Alert alert;
        try {
            alert = Alert.class.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to create an Alert instance", e);
        }
        triggerAlert(alert);
    }

    /**
     * Triggers an alert for the monitoring system.
     * This method can be extended to notify medical staff, log the alert,
     * or perform other actions.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        if (alert == null) {
            throw new IllegalArgumentException("alert must not be null");
        }

        // Implementation might involve logging the alert or notifying staff
        System.out.println("Alert triggered: " + alert);
    }
}