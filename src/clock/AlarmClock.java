package clock;

import queuemanager.PriorityQueue;
import queuemanager.QueueUnderflowException;
import queuemanager.SortedLinkedPriorityQueue;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

/**
 * Alarm Clock class contains all functionality related to managing the alarms.
 */
class AlarmClock {
    private static PriorityQueue priorityQueue = new SortedLinkedPriorityQueue();

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd'T'HHmmss");

    /**
     * Checks the head of the queue to see if there are any alarms that need to be activated.
     *
     * @throws QueueUnderflowException Thrown when queue is empty.
     */
    static void checkAlarms(int currentHour, int currentMinute) throws QueueUnderflowException {
        if (priorityQueue.isEmpty()) {
            return;
        }

        long dateInMilliseconds = Long.parseLong(priorityQueue.head().toString());
        int hour = (int) (dateInMilliseconds / (1000 * 60 * 60)) % 24 + 1;
        int minute = (int) (dateInMilliseconds / (1000 * 60)) % 60;

        if (hour == 24) {
            hour = 0;
        }

        if (currentHour == hour && currentMinute == minute) {
            priorityQueue.remove();
            JOptionPane.showMessageDialog(null, "Alarm activated.",
                    "Alarm Activated", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Generates the iCalendar file.
     */
    static String generateICalendar() {
        Long[] priorities = priorityQueue.getPriorityArray();

        StringBuilder content = new StringBuilder("BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:Alarm Clock\r\n");

        for (int i = 0; i < priorities.length; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(priorities[i]);
            String eventDate = formatter.format(calendar.getTime());

            content.append("BEGIN:VEVENT\r\n" +
                    "UID:" + i + "\r\n" +
                    "DTSTAMP:" + getDatestamp() + "Z\r\n" +
                    "DTSTART:" + eventDate + "Z\r\n" +
                    "DTEND:" + eventDate + "Z\r\n" +
                    "END:VEVENT\r\n");
        }

        content.append("END:VCALENDAR");

        return content.toString();
    }

    /**
     * Saves iCalendar file to the disk.
     */
    static void saveICalendar() {
        String iCalendar = generateICalendar();

        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("iCalendar files", "ics");
        chooser.setFileFilter(filter);
        chooser.setSelectedFile(new File(getDatestamp() + ".ics"));
        int retrieval = chooser.showSaveDialog(null);
        if (retrieval == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fw = new FileWriter(chooser.getSelectedFile())) {
                fw.write(iCalendar);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Generate priority queue from the loaded iCalendar file.
     * Alarms from the past are not loaded to the priority queue.
     */
    static void loadICalendar() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("iCalendar files", "ics");
        chooser.setFileFilter(filter);
        int retrieval = chooser.showOpenDialog(null);
        try {
            if (retrieval == JFileChooser.APPROVE_OPTION) {
                Scanner scanner = new Scanner(chooser.getSelectedFile());
                long datestamp = new Date().getTime();

                while (scanner.hasNextLine()) {
                    String line = scanner.next();
                    if (line.startsWith("DTSTART:")) {
                        Date date = formatter.parse(line.substring(8));
                        long dateInMilliseconds = date.getTime();

                        if (dateInMilliseconds > datestamp) {
                            Alarm alarm = new Alarm(dateInMilliseconds);
                            priorityQueue.add(alarm, dateInMilliseconds);
                        }
                    }
                }

                scanner.close();
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Invalid file!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds an alarm to the queue.
     *
     * @param hour Hour of the alarm.
     * @param minute Minute of the alarm.
     */
    static void addAlarm(int hour, int minute) {
        long dateInMilliseconds = AlarmClock.getDateInMillisecondsForAlarm(hour, minute);

        Alarm alarm = new Alarm(dateInMilliseconds);
        priorityQueue.add(alarm, dateInMilliseconds);
    }

    /**
     * Returns a date in milliseconds for the alarm.
     *
     * @param hour Alarm's hour.
     * @param minute Alarm's minute.
     * @return Date in milliseconds for the alarm.
     */
    static long getDateInMillisecondsForAlarm(int hour, int minute) {
        LocalDateTime currentDate = LocalDateTime.now();

        // if the alarm date will be set in the past, add one day to the date
        if (currentDate.getHour() >= hour && currentDate.getMinute() >= minute) {
            currentDate = LocalDateTime.from(currentDate).plusDays(1);
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String formattedDate = dtf.format(currentDate);
        String myDate = String.format("%s %02d:%02d:00", formattedDate, hour, minute);

        return LocalDateTime.parse(myDate, DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }

    /**
     * Returns current datestamp in iCalendar format.
     *
     * @return Current datestamp in iCalendar format.
     */
    static String getDatestamp() {
        return formatter.format(new Date());
    }

    /**
     * Returns a priority array from the queue.
     *
     * @return A priority array from the queue.
     */
    static Long[] getPriorityArray() {
        return priorityQueue.getPriorityArray();
    }

    /**
     * Check if queue is empty.
     *
     * @return A result of the check.
     */
    static boolean isEmpty() {
        return priorityQueue.isEmpty();
    }

    /**
     * Removes item from selected position from the queue.
     */
    static void remove(int position) throws QueueUnderflowException {
        priorityQueue.remove(position);
    }
}
