package clock;

import queuemanager.QueueUnderflowException;
import java.awt.*;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Observer;
import java.util.Observable;

public class View implements Observer {
    
    private ClockPanel panel;
    private JFrame frame;

    public View(Model model) {
        frame = new JFrame();
        panel = new ClockPanel(model);
        frame.setTitle("Java Clock");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Event listener that prompts user to save the alarms when he exits the program.
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                AlarmClock.saveICalendar();
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
     * When an Add Alarm item is clicked a dialog box appears to add an alarm.
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

        JMenuItem editItem = new JMenuItem("Edit");
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

        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.setMnemonic('s');
        saveItem.addActionListener(e ->
                AlarmClock.saveICalendar()
        );
        alarmMenu.add(saveItem);

        menuBar.add(alarmMenu);
    }

    /**
     * A dialogue box allowing users to add an alarm to the queue.
     * If user selects a wrong hour or minute value it will be replaced with a 0.
     */
    private void editAlarmDialogue() throws QueueUnderflowException {
        SpinnerNumberModel modelHours = new SpinnerNumberModel(0, 0, 23, 1);
        SpinnerNumberModel modelMinutes = new SpinnerNumberModel(0, 0, 59, 1);

        Long[] priorityArray = AlarmClock.getPriorityArray();
        String[] labels = new String[priorityArray.length];

        for (int i = 0; i < priorityArray.length; i++) {
            Long dateInMilliseconds = priorityArray[i];
            int hour = (int) (dateInMilliseconds / (1000 * 60 * 60)) % 24 + 1;
            int minute = (int) (dateInMilliseconds / (1000 * 60)) % 60;

            if (hour == 24) {
                hour = 0;
            }

            String label = String.format("%02d:%02d", hour, minute);
            labels[i] = label;
        }

        JComboBox alarmList = new JComboBox(labels);

        JSpinner hours = new JSpinner(modelHours);
        JSpinner minutes = new JSpinner(modelMinutes);

        alarmList.addActionListener(e -> {
                int position = alarmList.getSelectedIndex();
                Long dateInMilliseconds = priorityArray[position];
                int hour = (int) (dateInMilliseconds / (1000 * 60 * 60)) % 24 + 1;
                int minute = (int) (dateInMilliseconds / (1000 * 60)) % 60;

                if (hour == 24) {
                    hour = 0;
                }

                hours.setValue(hour);
                minutes.setValue(minute);
        });

        if (!AlarmClock.isEmpty()) {
            alarmList.setSelectedIndex(0); // runs the event listener for first item
        }

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
        if (result == JOptionPane.YES_OPTION) {
            AlarmClock.remove(alarmList.getSelectedIndex());
        } else if (result == JOptionPane.NO_OPTION) {
            AlarmClock.remove(alarmList.getSelectedIndex());

            int hour = (int) hours.getValue();
            int minute = (int) minutes.getValue();

            AlarmClock.addAlarm(hour, minute);
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
    }
}
