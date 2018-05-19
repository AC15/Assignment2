package clock;

import queuemanager.PriorityQueue;
import queuemanager.QueueUnderflowException;
import queuemanager.SortedLinkedPriorityQueue;

import javax.swing.*;
import java.util.Calendar;
import java.util.Observable;

public class Model extends Observable {
    int hour = 0;
    int minute = 0;
    int second = 0;
    
    private int oldSecond = 0;
    private int oldMinute = 0;
    
    public Model() {
        update();
    }
    
    void update() {
        Calendar date = Calendar.getInstance();
        hour = date.get(Calendar.HOUR_OF_DAY);

        oldMinute = minute;
        minute = date.get(Calendar.MINUTE);
        if (oldMinute != minute) {
            try {
                AlarmClock.checkAlarms(hour, minute);
            } catch (QueueUnderflowException e) {
                e.printStackTrace();
            }
        }

        oldSecond = second;
        second = date.get(Calendar.SECOND);
        if (oldSecond != second) {
            setChanged();
            notifyObservers();
        }
    }
}