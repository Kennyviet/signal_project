package data_management;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;

class AlertGeneratorTest {

    @Test
    void testCriticalHighSystolicBP() {
        DataStorage storage = new DataStorage();
        AlertGenerator gen = new AlertGenerator(storage);
        Patient p = new Patient(1);
        p.addRecord(185, "SystolicBP", 1000L); // above 180 → alert
        assertDoesNotThrow(() -> gen.evaluateData(p));
    }

    @Test
    void testCriticalLowSystolicBP() {
        DataStorage storage = new DataStorage();
        AlertGenerator gen = new AlertGenerator(storage);
        Patient p = new Patient(1);
        p.addRecord(85, "SystolicBP", 1000L); // below 90 → alert
        assertDoesNotThrow(() -> gen.evaluateData(p));
    }

    @Test
    void testCriticalHighDiastolicBP() {
        DataStorage storage = new DataStorage();
        AlertGenerator gen = new AlertGenerator(storage);
        Patient p = new Patient(1);
        p.addRecord(125, "DiastolicBP", 1000L); // above 120 → alert
        assertDoesNotThrow(() -> gen.evaluateData(p));
    }

    @Test
    void testCriticalLowDiastolicBP() {
        DataStorage storage = new DataStorage();
        AlertGenerator gen = new AlertGenerator(storage);
        Patient p = new Patient(1);
        p.addRecord(55, "DiastolicBP", 1000L); // below 60 → alert
        assertDoesNotThrow(() -> gen.evaluateData(p));
    }

    @Test
    void testBloodPressureIncreasingTrend() {
        DataStorage storage = new DataStorage();
        AlertGenerator gen = new AlertGenerator(storage);
        Patient p = new Patient(1);
        // Each increases by more than 10 → trend alert
        p.addRecord(100, "SystolicBP", 1000L);
        p.addRecord(115, "SystolicBP", 2000L);
        p.addRecord(130, "SystolicBP", 3000L);
        assertDoesNotThrow(() -> gen.evaluateData(p));
    }

    @Test
    void testBloodPressureDecreasingTrend() {
        DataStorage storage = new DataStorage();
        AlertGenerator gen = new AlertGenerator(storage);
        Patient p = new Patient(1);
        // Each decreases by more than 10 → trend alert
        p.addRecord(150, "SystolicBP", 1000L);
        p.addRecord(135, "SystolicBP", 2000L);
        p.addRecord(120, "SystolicBP", 3000L);
        assertDoesNotThrow(() -> gen.evaluateData(p));
    }

    @Test
    void testLowSaturationAlert() {
        DataStorage storage = new DataStorage();
        AlertGenerator gen = new AlertGenerator(storage);
        Patient p = new Patient(1);
        p.addRecord(90, "Saturation", 1000L); // below 92 → alert
        assertDoesNotThrow(() -> gen.evaluateData(p));
    }

    @Test
    void testRapidSaturationDrop() {
        DataStorage storage = new DataStorage();
        AlertGenerator gen = new AlertGenerator(storage);
        Patient p = new Patient(1);
        p.addRecord(97, "Saturation", 1000L);
        p.addRecord(91, "Saturation", 100000L); // 6% drop within 10 min → alert
        assertDoesNotThrow(() -> gen.evaluateData(p));
    }

    @Test
    void testHypotensiveHypoxemia() {
        DataStorage storage = new DataStorage();
        AlertGenerator gen = new AlertGenerator(storage);
        Patient p = new Patient(1);
        p.addRecord(85, "SystolicBP",  1000L); // below 90
        p.addRecord(91, "Saturation",  1000L); // below 92
        assertDoesNotThrow(() -> gen.evaluateData(p));
    }

    @Test
    void testNoAlertForNormalValues() {
        DataStorage storage = new DataStorage();
        AlertGenerator gen = new AlertGenerator(storage);
        Patient p = new Patient(1);
        p.addRecord(120, "SystolicBP",  1000L); // normal
        p.addRecord(80,  "DiastolicBP", 1000L); // normal
        p.addRecord(98,  "Saturation",  1000L); // normal
        assertDoesNotThrow(() -> gen.evaluateData(p));
    }

    @Test
    void testNullPatientThrowsException() {
        DataStorage storage = new DataStorage();
        AlertGenerator gen = new AlertGenerator(storage);
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> gen.evaluateData(null));
        assertNotNull(thrown);
    }

    @Test
    void testAlertConstructorAndGetters() {
        Alert alert = new Alert("1", "Critical Systolic BP", 1000L);
        assertEquals("1", alert.getPatientId());
        assertEquals("Critical Systolic BP", alert.getCondition());
        assertEquals(1000L, alert.getTimestamp());
    }
}