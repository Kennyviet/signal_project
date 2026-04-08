package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

public class AlertGenerator implements PatientDataGenerator {

    // Constant should be static final and in UPPER_CASE (Google Java Style Guide)
    public static final Random RANDOM_GENERATOR = new Random();

    // Changed variable name to camelCase (Google Java Style Guide)
    private final boolean[] alertStates; // false = resolved, true = pressed

    public AlertGenerator(int patientCount) {
        // Changed variable name to camelCase (Google Java Style Guide)
        alertStates = new boolean[patientCount + 1];
    }

    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            // Changed variable name to camelCase (Google Java Style Guide)
            if (alertStates[patientId]) {

                // Added space after 'if' for readability (Google Java Style Guide)
                if (RANDOM_GENERATOR.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false;

                    // Output the alert
                    outputStrategy.output(
                            patientId,
                            System.currentTimeMillis(),
                            "Alert",
                            "resolved"
                    );
                }

            } else {

                // Changed variable name to camelCase (Google Java Style Guide)
                double lambda = 0.1; // Average rate (alerts per period)

                // Changed variable name to camelCase (Google Java Style Guide)
                double p = -Math.expm1(-lambda); // Probability of at least one alert

                // Changed variable name to camelCase (Google Java Style Guide)
                boolean alertTriggered = RANDOM_GENERATOR.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true;

                    // Output the alert
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