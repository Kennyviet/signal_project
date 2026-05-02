package com.alerts;
import java.util.List;
import java.util.stream.Collectors;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

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

        List<PatientRecord> records = patient.getRecords(0, Long.MAX_VALUE);

        List<PatientRecord> systolic  = filterByType(records, "SystolicBP");
        List<PatientRecord> diastolic = filterByType(records, "DiastolicBP");

        for (PatientRecord r : systolic) {
            if (r.getMeasurementValue() > 180 || r.getMeasurementValue() < 90) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                    "Critical Systolic BP", r.getTimestamp()));
            }
        }
        for (PatientRecord r : diastolic) {
            if (r.getMeasurementValue() > 120 || r.getMeasurementValue() < 60) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                    "Critical Diastolic BP", r.getTimestamp()));
            }
        }
        checkTrend(patient, systolic,  "Systolic BP Trend");
        checkTrend(patient, diastolic, "Diastolic BP Trend");

        List<PatientRecord> sat = filterByType(records, "Saturation");
        for (int i = 0; i < sat.size(); i++) {
            if (sat.get(i).getMeasurementValue() < 92) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                    "Low Blood Saturation", sat.get(i).getTimestamp()));
            }
            for (int j = i + 1; j < sat.size(); j++) {
                long timeDiff = sat.get(j).getTimestamp() - sat.get(i).getTimestamp();
                if (timeDiff > 600000) break;
                double drop = sat.get(i).getMeasurementValue() - sat.get(j).getMeasurementValue();
                if (drop >= 5) {
                    triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                        "Rapid Saturation Drop", sat.get(j).getTimestamp()));
                }
            }
        }

        boolean lowBP  = systolic.stream().anyMatch(r -> r.getMeasurementValue() < 90);
        boolean lowSat = sat.stream().anyMatch(r -> r.getMeasurementValue() < 92);
        if (lowBP && lowSat) {
            triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                "Hypotensive Hypoxemia Alert", System.currentTimeMillis()));
        }

        List<PatientRecord> ecg = filterByType(records, "ECG");
        int windowSize = 10;
        if (ecg.size() > windowSize) {
            for (int i = windowSize; i < ecg.size(); i++) {
                double avg = 0;
                for (int j = i - windowSize; j < i; j++) {
                    avg += ecg.get(j).getMeasurementValue();
                }
                avg /= windowSize;
                double current = ecg.get(i).getMeasurementValue();
                if (avg != 0 && current > avg * 2) {
                    triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                        "Abnormal ECG Peak", ecg.get(i).getTimestamp()));
                }
            }
        }
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

    private void checkTrend(Patient patient, List<PatientRecord> records, String alertName) {
        for (int i = 2; i < records.size(); i++) {
            double d1 = records.get(i-1).getMeasurementValue() - records.get(i-2).getMeasurementValue();
            double d2 = records.get(i).getMeasurementValue()   - records.get(i-1).getMeasurementValue();
            if ((d1 > 10 && d2 > 10) || (d1 < -10 && d2 < -10)) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                    alertName, records.get(i).getTimestamp()));
            }
        }
    }

    private List<PatientRecord> filterByType(List<PatientRecord> records, String type) {
        return records.stream()
            .filter(r -> r.getRecordType().equalsIgnoreCase(type))
            .collect(Collectors.toList());
    }
}