package clock;

import queuemanager.PriorityQueue;
import queuemanager.QueueUnderflowException;
import queuemanager.SortedLinkedPriorityQueue;

import javax.swing.*;
import java.util.Calendar;
import java.util.Observable;

public class Model extends Observable {

    PriorityQueue priorityQueue = new SortedLinkedPriorityQueue();
    
    int hour = 0;
    int minute = 0;
    int second = 0;
    
    int oldSecond = 0;
    int oldMinute = 0;
    
    public Model() {
        update();
    }
    
    public void update() {
        Calendar date = Calendar.getInstance();
        hour = date.get(Calendar.HOUR_OF_DAY);

        oldMinute = minute;
        minute = date.get(Calendar.MINUTE);
        if (oldMinute != minute) {
            try {
                checkAlarms();
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

    private void checkAlarms() throws QueueUnderflowException {
        if (priorityQueue.isEmpty()) {
            return;
        }

        long dateInMilliseconds = Long.parseLong(priorityQueue.head().toString());
        int hour = (int) (dateInMilliseconds / (1000 * 60 * 60)) % 24 + 1;
        int minute = (int) (dateInMilliseconds / (1000 * 60)) % 60;

        if (hour == 24) {
            hour = 0;
        }

        if (this.hour == hour && this.minute == minute) {
            priorityQueue.remove();
            JOptionPane.showMessageDialog(null, "Alarm activated.",
                    "Alarm Activated", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}