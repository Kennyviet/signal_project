package com.data_management;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

/**
 * WebSocket client that connects to the signal generator's WebSocket server,
 * receives real-time patient data messages, parses them, and stores them
 * in DataStorage.
 *
 * Expected message format: "patientId,timestamp,recordType,measurementValue"
 */
public class WebSocketClientImpl extends WebSocketClient implements DataReader {

    private DataStorage dataStorage;

    /**
     * Constructs the WebSocket client with the given server URI.
     *
     * @param serverUri the URI of the WebSocket server to connect to
     */
    public WebSocketClientImpl(URI serverUri) {
        super(serverUri);
    }

    /**
     * Called when the WebSocket connection is established.
     */
    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("WebSocket connection opened: " + getURI());
    }

    /**
     * Called when a message is received from the server.
     * Parses the message and stores the data in DataStorage.
     *
     * @param message the raw data message from the server
     */
    @Override
    public void onMessage(String message) {
        if (dataStorage == null) {
            System.err.println("DataStorage not set — cannot store message.");
            return;
        }
        parseAndStore(message);
    }

    /**
     * Called when the WebSocket connection is closed.
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("WebSocket connection closed. Reason: " + reason);
    }

    /**
     * Called when an error occurs on the WebSocket connection.
     */
    @Override
    public void onError(Exception ex) {
        System.err.println("WebSocket error: " + ex.getMessage());
    }

    /**
     * Implements DataReader — connects to the WebSocket server
     * and starts receiving real-time data continuously.
     *
     * @param dataStorage the storage system to write incoming data into
     * @throws IOException if the connection fails
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        this.dataStorage = dataStorage;
        try {
            // Connect and block until connection closes
            this.connectBlocking();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("WebSocket connection interrupted", e);
        }
    }

    /**
     * Disconnects the WebSocket client cleanly.
     */
    @Override
    public void disconnect() {
        this.close();
        System.out.println("WebSocket client disconnected.");
    }

    /**
     * Parses a raw message string and stores it in DataStorage.
     * Expected format: "patientId,timestamp,recordType,measurementValue"
     * Skips malformed messages gracefully.
     *
     * @param message the raw message string
     */
    private void parseAndStore(String message) {
        try {
            String[] parts = message.split(",");
            if (parts.length != 4) {
                System.err.println("Malformed message (wrong number of fields): "
                    + message);
                return;
            }
            int patientId        = Integer.parseInt(parts[0].trim());
            long timestamp       = Long.parseLong(parts[1].trim());
            String recordType    = parts[2].trim();
            double measureValue  = Double.parseDouble(parts[3].trim());

            // Store in DataStorage — appends to existing patient records
            dataStorage.addPatientData(patientId, measureValue,
                                       recordType, timestamp);

        } catch (NumberFormatException e) {
            // Corrupted or unexpected data — log and skip
            System.err.println("Could not parse message: " + message
                + " | Error: " + e.getMessage());
        }
    }

    /**
     * Factory method — creates and returns a connected WebSocketClientImpl.
     *
     * @param serverAddress e.g. "ws://localhost:8080"
     * @param dataStorage   the storage to write data into
     * @return a connected WebSocketClientImpl instance
     * @throws URISyntaxException if the address is invalid
     * @throws IOException        if connection fails
     */
    public static WebSocketClientImpl connect(String serverAddress,
                                               DataStorage dataStorage)
            throws URISyntaxException, IOException {
        WebSocketClientImpl client =
            new WebSocketClientImpl(new URI(serverAddress));
        client.readData(dataStorage);
        return client;
    }
}