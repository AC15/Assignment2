package clock;

import java.awt.*;
import javax.swing.*;
import java.util.Observer;
import java.util.Observable;

public class View implements Observer {
    
    private ClockPanel panel;
    private JFrame frame;

    public View(Model model) {
        frame = new JFrame();
        panel = new ClockPanel(model);
        //frame.setContentPane(panel);
        frame.setTitle("Java Clock");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

        addAboutMenu(menuBar);
    }

    /**
     * Adds an About menu to the menu bar.
     * Once clicked it displays a dialog box with author's name.
     *
     * @param menuBar Menu bar to which the about menu will be attached.
     */
    private void addAboutMenu(JMenuBar menuBar) {
        JMenu aboutMenu = new JMenu("About");
        aboutMenu.setMnemonic('a');

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
