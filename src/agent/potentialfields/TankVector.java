package agent.potentialfields;

/**
 * Created with IntelliJ IDEA.
 * User: jbdennin
 * Date: 10/4/13
 * Time: 5:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class TankVector {
    private double magnitude, angle;

    public TankVector(double magnitude, double angle) {
        this.magnitude = magnitude;
        this.angle = angle;
    }

//    public static TankVector createVector(double angle, double magnitude) {
//        return new TankVector(magnitude*Math.sin(angle), magnitude*Math.cos(angle));
//    }

    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

//    public double getMagnitude() {
//        return Math.sqrt(Math.pow(magnitude, 2d) * Math.pow(angle, 2d));
//    }
//
//    public double getAngle() {
//        return Math.atan(angle / magnitude);
//    }

    public void add(TankVector tankVector) {
        this.magnitude += tankVector.magnitude;
        this.angle += tankVector.angle;
    }
}
