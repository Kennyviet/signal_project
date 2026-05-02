package data_management;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.data_management.Patient;
import com.data_management.PatientRecord;

class PatientTest {

    @Test
    void testGetRecordsReturnsCorrectRange() {
        Patient p = new Patient(1);
        p.addRecord(120, "SystolicBP", 1000L);
        p.addRecord(130, "SystolicBP", 2000L);
        p.addRecord(140, "SystolicBP", 5000L);
        List<PatientRecord> result = p.getRecords(1000L, 2000L);
        assertEquals(2, result.size());
    }

    @Test
    void testGetRecordsReturnsEmptyWhenNoMatch() {
        Patient p = new Patient(1);
        p.addRecord(120, "SystolicBP", 9000L);
        List<PatientRecord> result = p.getRecords(1000L, 2000L);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetRecordsWithNoData() {
        Patient p = new Patient(2);
        List<PatientRecord> result = p.getRecords(0L, Long.MAX_VALUE);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetRecordsIncludesBoundaryTimes() {
        Patient p = new Patient(1);
        p.addRecord(100, "HeartRate", 1000L);
        p.addRecord(110, "HeartRate", 5000L);
        List<PatientRecord> result = p.getRecords(1000L, 5000L);
        assertEquals(2, result.size());
    }
}