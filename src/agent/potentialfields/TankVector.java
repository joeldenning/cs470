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

    public void add(TankVector tankVector) {
        double x = this.getXVector() + tankVector.getXVector();
        double y = this.getYVector() + tankVector.getYVector();
        this.magnitude = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        this.angle = Math.atan2(y, x);
    }

    public double getXVector() {
        return magnitude * Math.cos(angle);
    }

    public double getYVector() {
        return magnitude * Math.sin(angle);
    }
}
