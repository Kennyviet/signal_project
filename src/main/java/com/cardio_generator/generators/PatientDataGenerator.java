package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Interface for generating patient health data.
 * 
 * Implementations of this interface simulate different types of
 * patient data (e.g., ECG, blood pressure, alerts) and output
 * the generated data using a specified {@link OutputStrategy}.
 */
public interface PatientDataGenerator {

    /**
     * Generates data for a specific patient and sends it to the output strategy.
     *
     * @param patientId the ID of the patient for whom data is generated
     * @param outputStrategy the strategy used to output the generated data
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}