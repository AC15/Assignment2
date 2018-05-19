package clock;

public class Clock {
    
    public static void main(String[] args) {
        Model model = new Model();
        View view = new View(model);
        model.addObserver(view);
        Controller controller = new Controller(model, view);
        AlarmClock.loadICalendar(); // opens up s dialogue box to load the alarms when started
    }
}
