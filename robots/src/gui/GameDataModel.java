package gui;

import java.awt.*;
import java.util.Observable;

public class GameDataModel extends Observable {
    double angularVelocity = 0;
    int counter = 0;
    private volatile double m_robotPositionX = 100;
    public static String KEY_COORDINATE_ROBOT = "robot changed";
    public static String KEY_COORDINATE_TARGET = "target changed";
    private volatile double m_robotPositionY = 100;
    private volatile double m_robotDirection = 0;

    private volatile int m_targetPositionX = 250;
    private volatile int m_targetPositionY = 100;

    private static final double maxVelocity = 0.1;
    private static final double maxAngularVelocity = 0.001;

    public double getM_robotPositionX() {
        return m_robotPositionX;
    }

    public double getM_robotPositionY() {
        return m_robotPositionY;
    }

    public double getM_robotDirection() {
        return m_robotDirection;
    }

    public int getM_targetPositionX() {
        return m_targetPositionX;
    }

    public int getM_targetPositionY() {
        return m_targetPositionY;
    }


    protected void setTargetPosition(Point p) {
        m_targetPositionX = p.x;
        m_targetPositionY = p.y;
        setChanged();
        notifyObservers(KEY_COORDINATE_TARGET);
        clearChanged();
    }


    private static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;
        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    protected void onModelUpdateEvent() {

        double distance = distance(m_targetPositionX, m_targetPositionY,
                m_robotPositionX, m_robotPositionY);
        if (distance < 0.5) {
            counter = 0;
            return;
        }
        double velocity = maxVelocity;
        double angleToTarget = angleTo(m_robotPositionX, m_robotPositionY, m_targetPositionX, m_targetPositionY);


//           System.out.println();
        System.out.println(counter);
        if (distance>=angularVelocity*maxVelocity&&angleToTarget!=m_robotDirection) {
            if (counter<2) {
                if (angleToTarget > m_robotDirection && angularVelocity != maxAngularVelocity) {
                    counter++;
                    angularVelocity = maxAngularVelocity;
                }
                if (angleToTarget < m_robotDirection && angularVelocity != -maxAngularVelocity) {
                    counter++;
                    angularVelocity = -maxAngularVelocity;
                }
            }else {
                if (Math.abs(angleToTarget - m_robotDirection)<0.1) {
                    angularVelocity = 0;
                    if (angleToTarget > m_robotDirection)
                        angularVelocity = maxAngularVelocity;
                    if (angleToTarget < m_robotDirection)
                        angularVelocity = -maxAngularVelocity;
                }
            }
        }

        moveRobot(velocity, angularVelocity, 10);
    }

    private static double asNormalizedRadians(double angle) {

        if (angle < 0) {
//            System.out.println("<");
            angle += 2 * Math.PI;
        }
        if (angle >= 2* Math.PI) {
//            System.out.println(">");
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    private static double applyLimits(double value, double min, double max) {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    private void moveRobot(double velocity, double angularVelocity, double duration) {
        velocity = applyLimits(velocity, 0, maxVelocity);
        angularVelocity = applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);
        double newX = m_robotPositionX + velocity / angularVelocity *
                (Math.sin(m_robotDirection + angularVelocity * duration) -
                        Math.sin(m_robotDirection));
        if (!Double.isFinite(newX)) {
            newX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
        }
        double newY = m_robotPositionY - velocity / angularVelocity *
                (Math.cos(m_robotDirection + angularVelocity * duration) -
                        Math.cos(m_robotDirection));
        if (!Double.isFinite(newY)) {
            newY = m_robotPositionY + velocity * duration * Math.sin(m_robotDirection);
        }
        m_robotPositionX = newX;
        m_robotPositionY = newY;
        double newDirection = asNormalizedRadians(m_robotDirection + angularVelocity * duration );
        m_robotDirection = newDirection;
        setChanged();
        notifyObservers(KEY_COORDINATE_ROBOT);
        clearChanged();
    }



    static int round(double value) {
        return (int) (value + 0.5);
    }
}