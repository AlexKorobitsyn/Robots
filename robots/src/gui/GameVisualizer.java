package gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class GameVisualizer extends JPanel implements Observer {
    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100;
    private volatile double m_robotDirection = 0;
    private volatile int m_targetPositionX = 250;
    private volatile int m_targetPositionY = 100;
    private GameDataModel d_model = new GameDataModel();
    private final Timer m_timer = initTimer();

    private static Timer initTimer() {
        Timer timer = new Timer("events generator", true);
        return timer;
    }

    public static class Helpers {
        public static boolean areEqual(Object o1, Object o2) {
            if (o1 == null)
                return o2 == null;
            return o1.equals(o2);
        }
    }

    public GameVisualizer() {
        d_model.addObserver(this);

        JInternalFrame dialog = new JInternalFrame();
        dialog.setSize(480, 200);
        JLabel coordinate = new JLabel("Robot: x=" + m_robotPositionX + " y=" + m_robotPositionY);
        dialog.add(coordinate);
        dialog.setVisible(true);
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(() -> {
            String now = "Robot: x=" + Math.ceil(m_robotPositionX) + " y=" + Math.ceil(m_robotPositionY);
            SwingUtilities.invokeLater(() -> coordinate.setText(now));
        }, 0, 1, TimeUnit.MILLISECONDS);
        this.add(dialog);
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onRedrawEvent();
            }
        }, 0, 50);
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                d_model.onModelUpdateEvent();
            }
        }, 0, 10);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                d_model.setTargetPosition(e.getPoint());
                repaint();
            }
        });
        setDoubleBuffered(true);
    }


    protected void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        drawRobot(g2d, GameDataModel.round(m_robotPositionX), GameDataModel.round(m_robotPositionY), m_robotDirection);
        drawTarget(g2d, m_targetPositionX, m_targetPositionY);
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private void drawRobot(Graphics2D g, int x, int y, double direction) {
        int robotCenterX = GameDataModel.round(m_robotPositionX);
        int robotCenterY = GameDataModel.round(m_robotPositionY);
        AffineTransform t = AffineTransform.getRotateInstance(direction, robotCenterX, robotCenterY);
        g.setTransform(t);
        g.setColor(Color.MAGENTA);
        fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, robotCenterX + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX + 10, robotCenterY, 5, 5);
    }

    private void drawTarget(Graphics2D g, int x, int y) {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }

    @Override
    public void update(Observable o, Object key) {
        if (Helpers.areEqual(d_model, o))
        {
            if (Helpers.areEqual(GameDataModel.KEY_COORDINATE_ROBOT, key))
            {
                m_robotPositionX = d_model.getM_robotPositionX();
                m_robotPositionY = d_model.getM_robotPositionY();
                m_robotDirection = d_model.getM_robotDirection();
            }
            if (Helpers.areEqual(GameDataModel.KEY_COORDINATE_TARGET, key))
            {
                m_targetPositionX = d_model.getM_targetPositionX();
                m_targetPositionY = d_model.getM_targetPositionY();
            }
        }
    }
}