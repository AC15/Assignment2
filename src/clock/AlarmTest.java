package clock;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Aleksander Czarnowski on 20/05/2018
 * 
 * Test for AlarmTest class.
 */
public class AlarmTest {
    Alarm alarm = new Alarm(1500000000L);

    /**
     * Test for getDateInMilliseconds method.
     * Should be equal to date in milliseconds.
     */
    @Test
    public void getDateInMilliseconds() {
        assertEquals(alarm.getDateInMilliseconds(), 1500000000L);
    }

    /**
     * Test for toString method.
     * Should bet equal a string with priority.
     */
    @Test
    public void toStringTest() {
        assertEquals(alarm.toString(), "1500000000");
    }
}