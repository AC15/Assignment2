package clock;

import queuemanager.PriorityQueue;
import queuemanager.SortedLinkedPriorityQueue;

import java.util.Calendar;
import java.util.Observable;

public class Model extends Observable {

    PriorityQueue priorityQueue = new SortedLinkedPriorityQueue();
    
    int hour = 0;
    int minute = 0;
    int second = 0;
    
    int oldSecond = 0;
    
    public Model() {
        update();
    }
    
    public void update() {
        Calendar date = Calendar.getInstance();
        hour = date.get(Calendar.HOUR);
        minute = date.get(Calendar.MINUTE);
        oldSecond = second;
        second = date.get(Calendar.SECOND);
        if (oldSecond != second) {
            setChanged();
            notifyObservers();
        }
    }
}