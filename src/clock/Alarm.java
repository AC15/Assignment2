package clock;

/**
 * Created by Aleksander Czarnowski on 24/04/2018
 *
 * An alarm object.
 */
public class Alarm {
    private long dateInMilliseconds;

    public Alarm(long dateInMilliseconds) {
        this.dateInMilliseconds = dateInMilliseconds;
    }

    public long getDateInMilliseconds() {
        return dateInMilliseconds;
    }

    @Override
    public String toString() {
        return String.valueOf(getDateInMilliseconds());
    }
}
