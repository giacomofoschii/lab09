package it.unibo.oop.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Third experiment with reactive gui.
 */
public final class AnotherConcurrentGUI extends JFrame {
    private static final int SW_PROPORTION = 7;
    private static final int SH_PROPORTION = 14;
    public final JLabel display = new JLabel();
    public final JButton up = new JButton("up");
    public final JButton down = new JButton("down");
    final JButton stop = new JButton("stop");
    final Agent agent = new Agent();
    

    public AnotherConcurrentGUI() {
        /* Building the interface */
        super();
        final JPanel canvas = new JPanel();
        canvas.setLayout(new FlowLayout());
        display.setText("0");
        canvas.add(display);
        canvas.add(up);
        canvas.add(down);
        canvas.add(stop);

        /* Setting frame dimensions */
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screen.getWidth() / SW_PROPORTION), (int) (screen.getHeight() / SH_PROPORTION));
        this.setLocationByPlatform(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.setContentPane(canvas);
        this.setVisible(true);
        
        /* Threads */
        new Thread(agent).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                stop();
            }
        }).start();

        /* Listeners */
        up.addActionListener((e)->agent.up());
        down.addActionListener((e)->agent.down());
        stop.addActionListener((e)->agent.stop());
    }

    private void stop() {
        agent.stop();
        try {
            SwingUtilities.invokeAndWait(() -> {
                stop.setEnabled(false);
                up.setEnabled(false);
                down.setEnabled(false);
            });
        } catch (InvocationTargetException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class Agent implements Runnable {
        private volatile boolean stop;
        private volatile boolean down;
        private int counter;

        @Override
        public void run() {
            while(!this.stop) {
                try {
                    this.counter += this.down ? -1 : +1;
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(Integer.toString(counter)));
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
        }

        public void stop() {
            this.stop = true;
        }

        public void up() {
            this.down = false;
        }

        public void down() {
            this.down = true;
        }

    }
}

