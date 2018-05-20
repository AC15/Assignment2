package clock;

import queuemanager.QueueUnderflowException;
import java.awt.*;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observer;
import java.util.Observable;

public class View implements Observer {
    
    private ClockPanel panel;
    private JFrame frame;
    private JMenuItem editItem;
    private JMenuItem saveItem;

    public View(Model model) {
        frame = new JFrame();
        panel = new ClockPanel(model);
        frame.setTitle("Java Clock");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Event listener that prompts the user to save the alarms when he exits the program.
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!AlarmClock.isEmpty()) {
                    AlarmClock.saveICalendar();
                }
                frame.dispose();
                System.exit(0);
            }
        });

        Container pane = frame.getContentPane();

        panel.setPreferredSize(new Dimension(200, 200));
        pane.add(panel, BorderLayout.CENTER);
        addMenuBar();

        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Adds a menu bar to the top of the program.
     */
    private void addMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        addAlarmMenu(menuBar);
        addAboutMenu(menuBar);
    }

    /**
     * Adds an Alarm menu to the menu bar.
     * When an Add item is clicked a dialog box appears to add an alarm.
     * When an Edit item is clicked a dialog box appears to edit an alarm.
     * When a Load item is clicked a dialog box appears to load alarms from a file.
     * When a Save item is clicked a dialog box appears to save alarms to a file.
     *
     * @param menuBar Menu bar to which the alarm menu will be attached.
     */
    private void addAlarmMenu(JMenuBar menuBar) {
        JMenu alarmMenu = new JMenu("Alarm");
        alarmMenu.setMnemonic('1');

        JMenuItem aboutItem = new JMenuItem("Add");
        aboutItem.setMnemonic('a');
        aboutItem.addActionListener(e ->
                addAlarmDialogue()
        );
        alarmMenu.add(aboutItem);

        editItem = new JMenuItem("Edit");
        editItem.setMnemonic('e');
        editItem.addActionListener(e -> {
                try {
                    editAlarmDialogue();
                } catch (QueueUnderflowException e1) {
                    e1.printStackTrace();
                }
        });
        alarmMenu.add(editItem);

        JMenuItem loadItem = new JMenuItem("Load");
        loadItem.setMnemonic('l');
        loadItem.addActionListener(e ->
                AlarmClock.loadICalendar()
        );
        alarmMenu.add(loadItem);

        saveItem = new JMenuItem("Save");
        saveItem.setMnemonic('s');
        saveItem.addActionListener(e ->
                AlarmClock.saveICalendar()
        );
        alarmMenu.add(saveItem);

        menuBar.add(alarmMenu);
    }

    /**
     * A dialogue box allowing users to edit selected alarm from the queue.
     * If user selects a wrong hour or minute value, it will be replaced with a 0.
     */
    private void editAlarmDialogue() throws QueueUnderflowException {
        SpinnerNumberModel modelHours = new SpinnerNumberModel(0, 0, 23, 1);
        SpinnerNumberModel modelMinutes = new SpinnerNumberModel(0, 0, 59, 1);

        Long[] priorityArray = AlarmClock.getPriorityArray();
        String[] labels = new String[priorityArray.length];

        // create and add labels to the labels array
        for (int i = 0; i < priorityArray.length; i++) {
            long dateInMilliseconds = priorityArray[i];
            int hour = AlarmClock.millisecondsToHours(dateInMilliseconds);
            int minute = AlarmClock.millisecondsToMinutes(dateInMilliseconds);

            String label = String.format("%02d:%02d", hour, minute);
            labels[i] = label;
        }

        JComboBox alarmList = new JComboBox(labels);
        JSpinner hours = new JSpinner(modelHours);
        JSpinner minutes = new JSpinner(modelMinutes);

        // action listener to input hours and minutes of selected alarm to JSpinner fields
        alarmList.addActionListener(e -> {
                int position = alarmList.getSelectedIndex();
                long dateInMilliseconds = priorityArray[position];
                int hour = AlarmClock.millisecondsToHours(dateInMilliseconds);
                int minute = AlarmClock.millisecondsToMinutes(dateInMilliseconds);

                hours.setValue(hour);
                minutes.setValue(minute);
        });

        if (!AlarmClock.isEmpty()) {
            alarmList.setSelectedIndex(0); // runs the event listener for the first item
        }

        // custom names of the buttons
        Object[] options = {"Delete",
                "Edit",
                "Cancel"};

        final JComponent[] inputs = new JComponent[]{
                new JLabel("Select Alarm"),
                alarmList,
                new JLabel("Hours"),
                hours,
                new JLabel("Minutes"),
                minutes
        };

        int result = JOptionPane.showOptionDialog(null, inputs, "Edit Alarm",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, 0);

        if (result == JOptionPane.YES_OPTION) { // deletes the alarm when user clicks on Delete button
            AlarmClock.remove(alarmList.getSelectedIndex());
        } else if (result == JOptionPane.NO_OPTION) { // edits the alarm when user clicks on Edit button
            AlarmClock.remove(alarmList.getSelectedIndex()); // removes the chosen alarm

            int hour = (int) hours.getValue();
            int minute = (int) minutes.getValue();

            AlarmClock.addAlarm(hour, minute); // adds a new edited alarm
        }
    }

    /**
     * A dialogue box allowing users to add an alarm to the queue.
     * If user selects a wrong hour or minute value it will be replaced with 0.
     */
    private void addAlarmDialogue() {
        SpinnerNumberModel modelHours = new SpinnerNumberModel(0, 0, 23, 1);
        SpinnerNumberModel modelMinutes = new SpinnerNumberModel(0, 0, 59, 1);

        JSpinner hours = new JSpinner(modelHours);
        JSpinner minutes = new JSpinner(modelMinutes);

        final JComponent[] inputs = new JComponent[] {
                new JLabel("Hours"),
                hours,
                new JLabel("Minutes"),
                minutes
        };

        int result = JOptionPane.showConfirmDialog(null, inputs, "Add Alarm",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            int hour = (int) hours.getValue();
            int minute = (int) minutes.getValue();

            AlarmClock.addAlarm(hour, minute);
        }
    }

    /**
     * Adds an About menu to the menu bar.
     * Once clicked it displays a dialog box with author's name.
     *
     * @param menuBar Menu bar to which the about menu will be attached.
     */
    private void addAboutMenu(JMenuBar menuBar) {
        JMenu aboutMenu = new JMenu("About");
        aboutMenu.setMnemonic('2');

        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.setMnemonic('a');
        aboutItem.addActionListener(e ->
                JOptionPane.showMessageDialog(null,
                        "Java clock by Aleksander Czarnowski",
                        "About", JOptionPane.INFORMATION_MESSAGE)
        );
        aboutMenu.add(aboutItem);

        menuBar.add(aboutMenu);
    }
    
    public void update(Observable o, Object arg) {
        panel.repaint();

        // disables the save and edit menu items when there are no alarms in the queue
        if (AlarmClock.isEmpty()) {
            saveItem.setEnabled(false);
            editItem.setEnabled(false);
        } else {
            saveItem.setEnabled(true);
            editItem.setEnabled(true);
        }
    }
}
