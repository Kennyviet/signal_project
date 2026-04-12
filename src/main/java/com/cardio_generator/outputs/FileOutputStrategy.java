package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Outputs patient data to files.
 * 
 * Each type of data (label) is written to a separate file
 * within a specified base directory.
 */
public class FileOutputStrategy implements OutputStrategy {

    // Changed variable name to camelCase (Google Java Style Guide)
    private final String baseDirectory;

    // Changed variable name to camelCase (Google Java Style Guide)
    public final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

    /**
     * Constructs a FileOutputStrategy with a specified base directory.
     *
     * @param baseDirectory the directory where output files will be stored
     */
    public FileOutputStrategy(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    /**
     * Outputs patient data to a file corresponding to the label.
     * 
     * If the directory or file does not exist, it will be created.
     * Data is appended to the file.
     *
     * @param patientId the ID of the patient
     * @param timestamp the time at which the data was generated
     * @param label the type/category of the data (used as filename)
     * @param data the actual data value to be written
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }

        String filePath = fileMap.computeIfAbsent(
                label,
                k -> Paths.get(baseDirectory, label + ".txt").toString()
        );

        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(
                        Paths.get(filePath),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND))) {

            out.printf(
                    "Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n",
                    patientId,
                    timestamp,
                    label,
                    data
            );

        } catch (Exception e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}