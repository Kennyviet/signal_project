package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Generates simulated blood oxygen saturation data for patients.
 * 
 * The saturation value represents the percentage of oxygen in the blood
 * and is kept within a realistic range (90%–100%).
 */
public class BloodSaturationDataGenerator implements PatientDataGenerator {

    /** Random number generator for simulation */
    private static final Random random = new Random();

    /** Stores last known saturation values for each patient */
    private int[] lastSaturationValues;

    /**
     * Constructs the generator and initializes baseline saturation values.
     *
     * @param patientCount the number of patients
     */
    public BloodSaturationDataGenerator(int patientCount) {
        lastSaturationValues = new int[patientCount + 1];

        // Initialize baseline values between 95 and 100
        for (int i = 1; i <= patientCount; i++) {
            lastSaturationValues[i] = 95 + random.nextInt(6);
        }
    }

    /**
     * Generates a new blood saturation value for a patient.
     * 
     * The value fluctuates slightly from the previous value and
     * is constrained to a realistic range.
     *
     * @param patientId the ID of the patient
     * @param outputStrategy the strategy used to output the data
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            int variation = random.nextInt(3) - 1; // -1, 0, or 1
            int newSaturationValue = lastSaturationValues[patientId] + variation;

            // Keep value between 90 and 100
            newSaturationValue = Math.min(Math.max(newSaturationValue, 90), 100);
            lastSaturationValues[patientId] = newSaturationValue;

            outputStrategy.output(
                    patientId,
                    System.currentTimeMillis(),
                    "Saturation",
                    Double.toString(newSaturationValue) + "%"
            );

        } catch (Exception e) {
            System.err.println(
                    "An error occurred while generating blood saturation data for patient " + patientId
            );
            e.printStackTrace();
        }
    }
}