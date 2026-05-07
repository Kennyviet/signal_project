package data_management;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;
import com.data_management.WebSocketClientImpl;

class WebSocketClientTest {

    /**
     * Test that a valid message is correctly parsed and stored.
     * We subclass WebSocketClientImpl to call parseAndStore directly
     * without needing a real server connection.
     */
    static class TestableClient extends WebSocketClientImpl {
        public TestableClient() throws Exception {
            super(new URI("ws://localhost:9999"));
        }

        // Expose parseAndStore for testing
        public void simulateMessage(String message, DataStorage storage)
                throws Exception {
            // Use readData to set storage, then call onMessage
            var field = WebSocketClientImpl.class
                .getDeclaredField("dataStorage");
            field.setAccessible(true);
            field.set(this, storage);
            this.onMessage(message);
        }
    }

    @Test
    void testValidMessageParsedAndStored() throws Exception {
        DataStorage storage = new DataStorage();
        TestableClient client = new TestableClient();

        // Valid message: patientId=1, timestamp=1000, type=SystolicBP, value=120
        client.simulateMessage("1,1000,SystolicBP,120.0", storage);

        List<PatientRecord> records = storage.getRecords(1, 0L, Long.MAX_VALUE);
        assertEquals(1, records.size());
        assertEquals(120.0, records.get(0).getMeasurementValue());
        assertEquals("SystolicBP", records.get(0).getRecordType());
    }

    @Test
    void testMalformedMessageDoesNotThrow() throws Exception {
        DataStorage storage = new DataStorage();
        TestableClient client = new TestableClient();

        // Wrong number of fields — should skip gracefully
        assertDoesNotThrow(() ->
            client.simulateMessage("baddata,only,three", storage));
    }

    @Test
    void testCorruptedNumberDoesNotThrow() throws Exception {
        DataStorage storage = new DataStorage();
        TestableClient client = new TestableClient();

        // patientId is not a number — should skip gracefully
        assertDoesNotThrow(() ->
            client.simulateMessage("abc,1000,HeartRate,75.0", storage));
    }

    @Test
    void testNullStorageDoesNotThrow() throws Exception {
        // dataStorage not set — onMessage should print error and return
        TestableClient client = new TestableClient();
        assertDoesNotThrow(() -> client.onMessage("1,1000,SystolicBP,120.0"));
    }

    @Test
    void testMultipleMessagesStoredCorrectly() throws Exception {
        DataStorage storage = new DataStorage();
        TestableClient client = new TestableClient();

        client.simulateMessage("1,1000,SystolicBP,120.0", storage);
        client.simulateMessage("1,2000,SystolicBP,130.0", storage);
        client.simulateMessage("1,3000,SystolicBP,140.0", storage);

        List<PatientRecord> records = storage.getRecords(1, 0L, Long.MAX_VALUE);
        assertEquals(3, records.size());
    }

    @Test
    void testDifferentPatientsStoredSeparately() throws Exception {
        DataStorage storage = new DataStorage();
        TestableClient client = new TestableClient();

        client.simulateMessage("1,1000,HeartRate,70.0", storage);
        client.simulateMessage("2,1000,HeartRate,80.0", storage);

        List<PatientRecord> p1Records = storage.getRecords(1, 0L, Long.MAX_VALUE);
        List<PatientRecord> p2Records = storage.getRecords(2, 0L, Long.MAX_VALUE);

        assertEquals(1, p1Records.size());
        assertEquals(1, p2Records.size());
        assertEquals(70.0, p1Records.get(0).getMeasurementValue());
        assertEquals(80.0, p2Records.get(0).getMeasurementValue());
    }

    @Test
    void testEmptyMessageDoesNotThrow() throws Exception {
        DataStorage storage = new DataStorage();
        TestableClient client = new TestableClient();
        assertDoesNotThrow(() ->
            client.simulateMessage("", storage));
    }

    @Test
    void testDisconnectDoesNotThrow() throws Exception {
        TestableClient client = new TestableClient();
        // disconnect without ever connecting — should not crash
        assertDoesNotThrow(() -> client.disconnect());
    }
}