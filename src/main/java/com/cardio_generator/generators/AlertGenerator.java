package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Generates alert data for patients in the simulation.
 * 
 * Alerts can be either "triggered" or "resolved" based on probability.
 * Each patient has an internal alert state that determines whether
 * an alert is currently active.
 */
public class AlertGenerator implements PatientDataGenerator {

    /** Random number generator used for simulation */
    public static final Random RANDOM_GENERATOR = new Random();

    /** Stores alert state for each patient (true = active, false = resolved) */
    private final boolean[] alertStates;

    /**
     * Constructs an AlertGenerator and initializes alert states.
     *
     * @param patientCount the number of patients
     */
    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }

    /**
     * Generates alert data for a specific patient.
     * 
     * If an alert is active, there is a high probability it will be resolved.
     * If no alert is active, there is a small probability a new alert will be triggered.
     *
     * @param patientId the ID of the patient
     * @param outputStrategy the strategy used to output the generated alert data
     */
    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {

                if (RANDOM_GENERATOR.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false;

                    outputStrategy.output(
                            patientId,
                            System.currentTimeMillis(),
                            "Alert",
                            "resolved"
                    );
                }

            } else {

                double lambda = 0.1; // Average rate
                double p = -Math.expm1(-lambda);

                boolean alertTriggered = RANDOM_GENERATOR.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true;

                    outputStrategy.output(
                            patientId,
                            System.currentTimeMillis(),
                            "Alert",
                            "triggered"
                    );
                }
            }

        } catch (Exception e) {
            System.err.println(
                    "An error occurred while generating alert data for patient " + patientId
            );
            e.printStackTrace();
        }
    }
}