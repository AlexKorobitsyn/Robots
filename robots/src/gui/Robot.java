package gui;

public class Robot {
    Position position;
    public double m_robotDirection = 0;
    public double maxVelocity = 0.1;
    public double maxAngularVelocity = 0.001;
    public double angularVelocity = 0;
    Robot(Position x, double direction, double maxVelocity, double maxAngularVelocity, double angularVelocity){
        this.position = x;
        this.m_robotDirection = direction;
        this.maxVelocity = maxVelocity;
        this.maxAngularVelocity = maxAngularVelocity;
        this.angularVelocity = angularVelocity;
    }
}
