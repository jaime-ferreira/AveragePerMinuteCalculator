package org.example;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class AveragePerMinuteCalculator {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String DURATION_KEY = "duration";

    public static void main(String[] args) {
        if (args.length != 2) {
            // Check if the correct number of command-line arguments is provided.
            System.err.println("Usage: java MovingAverageCalculator <input_file> <window_size>");
            System.exit(1);
        }

        String inputFile = args[0];
        int windowSize = Integer.parseInt(args[1]);
        // Calculate the start of the time window, rounded to the minute.
        LocalDateTime windowStartMinuteMark = LocalDateTime.now().minusMinutes(windowSize);

        try {
            calculateMovingAverage(inputFile, windowStartMinuteMark);
        } catch (Exception e) {
            // Handle and print any exceptions that occur during execution.
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Calculates the average duration of events in each minute until the current time for a pre-defined time window.
    private static void calculateMovingAverage(String inputFile, LocalDateTime windowSizeMinuteMark)
            throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            int minuteDurationSum = 0;
            int eventCounter = 0;
            LocalDateTime previousEventMinuteMark = null;
            String line;

            while ((line = br.readLine()) != null) {
                JSONObject eventJson = new JSONObject(line);
                long timestampMillis = DATE_FORMAT.parse(eventJson.getString(TIMESTAMP_KEY)).getTime();
                int duration = eventJson.getInt(DURATION_KEY);

                LocalDateTime eventMinuteMark = getMinuteMark(timestampMillis); // Calculate the event time rounded to the minute.

                if (previousEventMinuteMark == null) {
                    previousEventMinuteMark = eventMinuteMark;
                }

                if (eventMinuteMark.isAfter(windowSizeMinuteMark)) {
                    if (!eventMinuteMark.equals(previousEventMinuteMark)) {
                        // Print the average for the previous minute.
                        printAverage(previousEventMinuteMark, minuteDurationSum, eventCounter);
                        minuteDurationSum = 0;
                        eventCounter = 0;
                    }
                    previousEventMinuteMark = eventMinuteMark;
                    minuteDurationSum += duration;
                    eventCounter++;
                }
            }
            // Print the average for the last minute.
            printAverage(previousEventMinuteMark, minuteDurationSum, eventCounter);
        }
    }

    // Retrieves the LocalDateTime, rounded to the minute, for a Timestamp in milliseconds.
    private static LocalDateTime getMinuteMark(long timestampMillis){
        Instant instant = Instant.ofEpochMilli(timestampMillis);
        LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        return localDateTime.truncatedTo(ChronoUnit.MINUTES);
    }

    // Calculates and prints the average duration of events for a specific minute mark.
    private static void printAverage(LocalDateTime minuteMark, int minuteDurationSum, int eventCounter) {
        double average = (eventCounter > 0) ? (double) minuteDurationSum / eventCounter : 0.0;
        // Print the result in a JSON-like format.
        System.out.println("{\"date\": \"" + minuteMark + "\", \"average_delivery_time\": " + average + "}");
    }
}
