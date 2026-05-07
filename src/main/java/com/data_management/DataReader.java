package com.data_management;

import java.io.IOException;

/**
 * Interface for reading patient data into DataStorage.
 * Supports both file-based and real-time WebSocket data sources.
 */
public interface DataReader {
    /**
     * Reads data into the provided DataStorage instance.
     * For file-based readers this reads once.
     * For WebSocket readers this connects and listens continuously.
     *
     * @param dataStorage the storage system to write data into
     * @throws IOException if connection or reading fails
     */
    void readData(DataStorage dataStorage) throws IOException;

    /**
     * Disconnects or stops the data reader.
     * Default implementation does nothing — override for WebSocket.
     */
    default void disconnect() {}
}