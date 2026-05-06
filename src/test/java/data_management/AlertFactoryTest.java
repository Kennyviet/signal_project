package data_management;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.alerts.Alert;
import com.alerts.AlertFactory;
import com.alerts.AlertStrategy;
import com.alerts.BloodOxygenAlertFactory;
import com.alerts.BloodPressureAlertFactory;
import com.alerts.BloodPressureStrategy;
import com.alerts.ECGAlertFactory;
import com.alerts.HeartRateStrategy;
import com.alerts.OxygenSaturationStrategy;
import com.alerts.PriorityAlertDecorator;
import com.alerts.RepeatedAlertDecorator;

class AlertFactoryTest {

    @Test
    void testBloodPressureAlertFactory() {
        AlertFactory factory = new BloodPressureAlertFactory();
        Alert alert = factory.createAlert("1", "Critical BP", 1000L);
        assertEquals("1", alert.getPatientId());
        assertTrue(alert.getCondition().contains("BloodPressure"));
        assertEquals(1000L, alert.getTimestamp());
    }

    @Test
    void testBloodOxygenAlertFactory() {
        AlertFactory factory = new BloodOxygenAlertFactory();
        Alert alert = factory.createAlert("2", "Low Saturation", 2000L);
        assertEquals("2", alert.getPatientId());
        assertTrue(alert.getCondition().contains("BloodOxygen"));
    }

    @Test
    void testECGAlertFactory() {
        AlertFactory factory = new ECGAlertFactory();
        Alert alert = factory.createAlert("3", "Abnormal Peak", 3000L);
        assertEquals("3", alert.getPatientId());
        assertTrue(alert.getCondition().contains("ECG"));
    }

    @Test
    void testRepeatedAlertDecorator() {
        Alert base = new Alert("1", "Critical BP", 1000L);
        RepeatedAlertDecorator repeated = new RepeatedAlertDecorator(base, 30);
        assertTrue(repeated.getCondition().contains("Repeat every 30s"));
        assertEquals("1", repeated.getPatientId());
        assertEquals(30, repeated.getRepeatInterval());
    }

    @Test
    void testPriorityAlertDecorator() {
        Alert base = new Alert("1", "Critical BP", 1000L);
        PriorityAlertDecorator priority = new PriorityAlertDecorator(base, "HIGH");
        assertTrue(priority.getCondition().contains("[HIGH]"));
        assertEquals("HIGH", priority.getPriorityLevel());
    }

    @Test
    void testCombinedDecorators() {
        Alert base = new Alert("1", "Critical BP", 1000L);
        Alert repeated = new RepeatedAlertDecorator(base, 60);
        Alert priority = new PriorityAlertDecorator(repeated, "URGENT");
        assertTrue(priority.getCondition().contains("URGENT"));
        assertTrue(priority.getCondition().contains("Repeat every 60s"));
    }

    @Test
    void testDataStorageSingleton() {
        com.data_management.DataStorage s1 = com.data_management.DataStorage.getInstance();
        com.data_management.DataStorage s2 = com.data_management.DataStorage.getInstance();
        assertSame(s1, s2); // must be the exact same object
    }

    @Test
    void testBloodPressureStrategy() {
    AlertFactory factory = new BloodPressureAlertFactory();
    AlertStrategy strategy = new BloodPressureStrategy();
    com.data_management.Patient p = new com.data_management.Patient(1);
    p.addRecord(185, "SystolicBP", 1000L);
    assertDoesNotThrow(() -> strategy.checkAlert(p, factory));
    }

    @Test
    void testOxygenSaturationStrategy() {
    AlertFactory factory = new BloodOxygenAlertFactory();
    AlertStrategy strategy = new OxygenSaturationStrategy();
    com.data_management.Patient p = new com.data_management.Patient(1);
    p.addRecord(90, "Saturation", 1000L);
    assertDoesNotThrow(() -> strategy.checkAlert(p, factory));
    }

    @Test
    void testHeartRateStrategy() {
    AlertFactory factory = new ECGAlertFactory();
    AlertStrategy strategy = new HeartRateStrategy();
    com.data_management.Patient p = new com.data_management.Patient(1);
    for (int i = 0; i < 10; i++) {
        p.addRecord(1.0, "ECG", 1000L + i);
    }
    p.addRecord(10.0, "ECG", 2000L);
    assertDoesNotThrow(() -> strategy.checkAlert(p, factory));
    }
}