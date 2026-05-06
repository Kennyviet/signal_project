package com.alerts;

import java.util.List;
import java.util.stream.Collectors;

import com.data_management.Patient;
import com.data_management.PatientRecord;

public class HeartRateStrategy implements AlertStrategy {

    @Override
    public void checkAlert(Patient patient, AlertFactory factory) {
        List<PatientRecord> records = patient.getRecords(0, Long.MAX_VALUE);
        List<PatientRecord> ecg = records.stream()
            .filter(r -> r.getRecordType().equalsIgnoreCase("ECG"))
            .collect(Collectors.toList());

        int windowSize = 10;
        if (ecg.size() <= windowSize) return;

        for (int i = windowSize; i < ecg.size(); i++) {
            double avg = 0;
            for (int j = i - windowSize; j < i; j++) {
                avg += ecg.get(j).getMeasurementValue();
            }
            avg /= windowSize;
            double current = ecg.get(i).getMeasurementValue();
            if (avg != 0 && current > avg * 2) {
                Alert alert = factory.createAlert(
                    String.valueOf(patient.getPatientId()),
                    "Abnormal Heart Rate", ecg.get(i).getTimestamp());
                System.out.println("Alert: " + alert.getCondition());
            }
        }
    }
}