package clock;

import queuemanager.QueueUnderflowException;

import java.util.Calendar;
import java.util.Observable;

public class Model extends Observable {
    int hour = 0;
    int minute = 0;
    int second = 0;

    public Model() {
        update();
    }
    
    void update() {
        Calendar date = Calendar.getInstance();
        hour = date.get(Calendar.HOUR_OF_DAY);

        // checks every minute if alarm needs to be activated
        int oldMinute = minute;
        minute = date.get(Calendar.MINUTE);
        if (oldMinute != minute) {
            try {
                AlarmClock.checkAlarms(hour, minute);
            } catch (QueueUnderflowException e) {
                e.printStackTrace();
            }
        }

        int oldSecond = second;
        second = date.get(Calendar.SECOND);
        if (oldSecond != second) {
            setChanged();
            notifyObservers();
        }
    }
}