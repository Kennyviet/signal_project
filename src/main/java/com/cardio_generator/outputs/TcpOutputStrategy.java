package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * Outputs patient data over a TCP connection.
 * 
 * This class starts a TCP server on a specified port and waits
 * for a client to connect. Once connected, it sends patient data
 * as comma-separated values.
 */
public class TcpOutputStrategy implements OutputStrategy {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;

    /**
     * Constructs a TCP output strategy and starts a server on the given port.
     * 
     * A separate thread is used to accept client connections so that the main
     * application thread is not blocked.
     *
     * @param port the port number on which the TCP server listens
     */
    public TcpOutputStrategy(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP Server started on port " + port);

            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends patient data to the connected TCP client.
     * 
     * Data is formatted as a comma-separated string:
     * patientId,timestamp,label,data
     *
     * @param patientId the ID of the patient
     * @param timestamp the time at which the data was generated
     * @param label the type/category of the data
     * @param data the actual data value
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (out != null) {
            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);
        }
    }
}