package clock;

/**
 * Created by AC15 on 24/04/2018
 */
public class Alarm {
    private long dateInMilliseconds;

    public Alarm(long dateInMilliseconds) {
        this.dateInMilliseconds = dateInMilliseconds;
    }

    public long getDateInMilliseconds() {
        return dateInMilliseconds;
    }
}
