package clock;

import org.junit.Before;
import org.junit.Test;
import queuemanager.QueueUnderflowException;
import queuemanager.SortedLinkedPriorityQueue;

import static org.junit.Assert.assertEquals;

/**
 * Created by Aleksander Czarnowski on 20/05/2018
 *
 * Test for AlarmClockTest class.
 * Methods strictly related to priority queue were not tested.
 */
public class AlarmClockTest {
    @Before
    public void setUp() {
        AlarmClock.priorityQueue = new SortedLinkedPriorityQueue<>();
    }

    /**
     * Test for checkAlarms method.
     * Should remove the head of the queue which will throw an exception.
     *
     * @throws QueueUnderflowException Thrown when queue is empty.
     */
    @Test(expected = QueueUnderflowException.class)
    public void checkAlarms_WhenTimeIsCorrect_ShouldThrowException() throws QueueUnderflowException {
        Alarm alarm = new Alarm(1526832268854L);
        AlarmClock.priorityQueue.add(alarm, 1526832268854L);

        // 1526831164033L equals 17:04

        AlarmClock.checkAlarms(17, 4);
        AlarmClock.priorityQueue.head();
    }

    /**
     * Test for checkAlarms method.
     * Since the time is incorrect the alarm should not be removed from the queue.
     */
    @Test
    public void checkAlarms_WhenTimeIsIncorrect_ShouldNotRemoveAlarmFromQueueHead() throws QueueUnderflowException {
        Alarm alarm = new Alarm(1526832268854L);
        AlarmClock.priorityQueue.add(alarm, 1526832268854L);

        AlarmClock.checkAlarms(17, 3);

        assertEquals("1526832268854", AlarmClock.priorityQueue.head().toString());
    }

    /**
     * Test for generateICalendar method.
     * Should generate a valid iCalendar file string.
     */
    @Test
    public void generateICalendar_WhenOneAlarm_ShouldReturnICalendarFile() {
        Alarm alarm = new Alarm(1526832268854L);
        AlarmClock.priorityQueue.add(alarm, 1526832268854L);

        String iCalendarFile = "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:Alarm Clock\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:0\r\n" +
                "DTSTAMP:" + AlarmClock.getDatestamp() + "Z\r\n" +
                "DTSTART:20180520T170428Z\r\n" +
                "DTEND:20180520T170428Z\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR";

        assertEquals(iCalendarFile, AlarmClock.generateICalendar());
    }

    /**
     * Test for millisecondsToHours method.
     * Should return hours from a date in milliseconds.
     */
    @Test
    public void millisecondsToHours(){
        assertEquals(17, AlarmClock.millisecondsToHours(1526832268854L));
    }

    /**
     * Test for millisecondsToMinutes method.
     * Should return minutes from a date in milliseconds.
     */
    @Test
    public void millisecondsToMinutes() {
        assertEquals(4, AlarmClock.millisecondsToMinutes(1526832268854L));
    }

}