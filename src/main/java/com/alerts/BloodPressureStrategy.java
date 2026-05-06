package com.alerts;

import java.util.List;
import java.util.stream.Collectors;

import com.data_management.Patient;
import com.data_management.PatientRecord;

public class BloodPressureStrategy implements AlertStrategy {

    @Override
    public void checkAlert(Patient patient, AlertFactory factory) {
        List<PatientRecord> records = patient.getRecords(0, Long.MAX_VALUE);
        List<PatientRecord> systolic = records.stream()
            .filter(r -> r.getRecordType().equalsIgnoreCase("SystolicBP"))
            .collect(Collectors.toList());

        // Critical threshold
        for (PatientRecord r : systolic) {
            if (r.getMeasurementValue() > 180 || r.getMeasurementValue() < 90) {
                Alert alert = factory.createAlert(
                    String.valueOf(patient.getPatientId()),
                    "Critical BP", r.getTimestamp());
                System.out.println("Alert: " + alert.getCondition());
            }
        }

        // Trend check — 3 consecutive changes > 10 mmHg
        for (int i = 2; i < systolic.size(); i++) {
            double d1 = systolic.get(i-1).getMeasurementValue()
                      - systolic.get(i-2).getMeasurementValue();
            double d2 = systolic.get(i).getMeasurementValue()
                      - systolic.get(i-1).getMeasurementValue();
            if ((d1 > 10 && d2 > 10) || (d1 < -10 && d2 < -10)) {
                Alert alert = factory.createAlert(
                    String.valueOf(patient.getPatientId()),
                    "BP Trend", systolic.get(i).getTimestamp());
                System.out.println("Alert: " + alert.getCondition());
            }
        }
    }
}