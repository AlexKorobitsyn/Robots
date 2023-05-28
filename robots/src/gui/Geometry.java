package gui;

public class Geometry {
    public static double asNormalizedRadians(double angle) {
        if (angle < 0) {
            angle += 2 * Math.PI;
        }
        if (angle >= 2* Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }
    public static double distance(Position from, Position to) {
        double diffX = from.x - to.x;
        double diffY = from.y - to.y;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    public static double angleTo(Position from, Position to) {
        double diffX = to.x - from.x;
        double diffY = to.y - from.y;
        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }
}
