package com.cardio_generator.outputs;

/**
 * Interface defining a strategy for outputting patient data.
 * 
 * Implementations of this interface determine how and where
 * the generated data is sent (e.g., console, file, TCP, WebSocket).
 */
public interface OutputStrategy {

    /**
     * Outputs patient data using a specific strategy.
     *
     * @param patientId the ID of the patient
     * @param timestamp the time at which the data was generated
     * @param label the type/category of the data (e.g., ECG, Alert)
     * @param data the actual data value to be output
     */
    void output(int patientId, long timestamp, String label, String data);
}
