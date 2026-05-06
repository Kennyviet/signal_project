package com.alerts;

import java.util.List;
import java.util.stream.Collectors;

import com.data_management.Patient;
import com.data_management.PatientRecord;

public class OxygenSaturationStrategy implements AlertStrategy {

    @Override
    public void checkAlert(Patient patient, AlertFactory factory) {
        List<PatientRecord> records = patient.getRecords(0, Long.MAX_VALUE);
        List<PatientRecord> sat = records.stream()
            .filter(r -> r.getRecordType().equalsIgnoreCase("Saturation"))
            .collect(Collectors.toList());

        for (int i = 0; i < sat.size(); i++) {
            // Low saturation
            if (sat.get(i).getMeasurementValue() < 92) {
                Alert alert = factory.createAlert(
                    String.valueOf(patient.getPatientId()),
                    "Low Saturation", sat.get(i).getTimestamp());
                System.out.println("Alert: " + alert.getCondition());
            }
            // Rapid drop within 10 minutes
            for (int j = i + 1; j < sat.size(); j++) {
                long timeDiff = sat.get(j).getTimestamp()
                              - sat.get(i).getTimestamp();
                if (timeDiff > 600000) break;
                double drop = sat.get(i).getMeasurementValue()
                            - sat.get(j).getMeasurementValue();
                if (drop >= 5) {
                    Alert alert = factory.createAlert(
                        String.valueOf(patient.getPatientId()),
                        "Rapid Saturation Drop", sat.get(j).getTimestamp());
                    System.out.println("Alert: " + alert.getCondition());
                }
            }
        }
    }
}