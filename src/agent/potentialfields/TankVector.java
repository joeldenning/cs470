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
        this.magnitude += tankVector.magnitude;
        this.angle += tankVector.angle;
    }

    public double getXVector() {
        return 0;
    }

    public double getYVector() {
        return 0;
    }
}
