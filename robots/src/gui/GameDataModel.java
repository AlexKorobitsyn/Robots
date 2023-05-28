package gui;

import java.awt.*;
import java.util.Observable;

public class GameDataModel extends Observable {
    double angularVelocity = 0;
    static int counter = 0;
    Position targetPosition = new Position(250, 100);
    static Position robotPosition = new Position(100, 100);
    Target target = new Target(targetPosition);
    static Robot robot = new Robot(robotPosition, 0, 0.1, 0.001, 0);
    public static String KEY_COORDINATE_ROBOT = "robot changed";
    public static String KEY_COORDINATE_TARGET = "target changed";

    enum Turn{
        RIGHT,
        LEFT;

        public static boolean checkForLeftTurn(double angleToTarget){
            return angleToTarget < robot.m_robotDirection && robot.angularVelocity != -robot.maxAngularVelocity;
        }
        public static boolean checkForRightTurn(double angleToTarget) {
            return angleToTarget > robot.m_robotDirection && robot.angularVelocity != robot.maxAngularVelocity;
            }

        public void actionForRight() {
            counter++;
            robot.angularVelocity = robot.maxAngularVelocity;
        }
        public void actionForLeft(){
            counter++;
            robot.angularVelocity = -robot.maxAngularVelocity;
        }
    }


    protected void setTargetPosition(Point p) {
        target.position.x = p.x;
        target.position.y = p.y;
        setChanged();
        notifyObservers(KEY_COORDINATE_TARGET);
        clearChanged();
    }




    protected void onModelUpdateEvent() {

        double distance = Geometry.distance(target.position, robot.position);
        if (distance < 0.5) {
            counter = 0;
            return;
        }
        double velocity = robot.maxVelocity;
        double angleToTarget = Geometry.angleTo(robot.position, target.position);

        takeAngleForTurn(distance, angleToTarget);

        moveRobot(velocity, robot.angularVelocity, 10);
    }

    private static void takeAngleForTurn(double distance, double angleToTarget) {
        if (distance >=robot.angularVelocity*robot.maxVelocity&& angleToTarget !=robot.m_robotDirection) {
            if (counter<2) {
                if (Turn.checkForRightTurn(angleToTarget)) {
                    Turn turn = Turn.RIGHT;
                    turn.actionForRight();
                }
                if (Turn.checkForLeftTurn(angleToTarget)) {
                    Turn turn = Turn.LEFT;
                    turn.actionForLeft();
                }
            }else {
                if (Math.abs(angleToTarget - robot.m_robotDirection)<0.1) {
                    robot.angularVelocity = 0;
                    if (angleToTarget > robot.m_robotDirection)
                        robot.angularVelocity = robot.maxAngularVelocity;
                    if (angleToTarget < robot.m_robotDirection)
                        robot.angularVelocity = -robot.maxAngularVelocity;
                }
            }
        }
    }

    //enum как состояние поворота, Метод для onUpdate,
    private static double applyLimits(double value, double min, double max) {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    private void moveRobot(double velocity, double angularVelocity, double duration) {
        velocity = applyLimits(velocity, 0, robot.maxVelocity);
        angularVelocity = applyLimits(angularVelocity, -robot.maxAngularVelocity, robot.maxAngularVelocity);
        double newX = robot.position.x + velocity / angularVelocity *
                (Math.sin(robot.m_robotDirection + angularVelocity * duration) -
                        Math.sin(robot.m_robotDirection));
        if (!Double.isFinite(newX)) {
            newX = robot.position.x + velocity * duration * Math.cos(robot.m_robotDirection);
        }
        double newY = robot.position.y - velocity / angularVelocity *
                (Math.cos(robot.m_robotDirection + angularVelocity * duration) -
                        Math.cos(robot.m_robotDirection));
        if (!Double.isFinite(newY)) {
            newY = robot.position.y + velocity * duration * Math.sin(robot.m_robotDirection);
        }
        robot.position.x = newX;
        robot.position.y = newY;
        double newDirection = Geometry.asNormalizedRadians(robot.m_robotDirection + angularVelocity * duration );
        robot.m_robotDirection = newDirection;
        setChanged();
        notifyObservers(KEY_COORDINATE_ROBOT);
        clearChanged();
    }

}