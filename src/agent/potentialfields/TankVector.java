package agent.potentialfields;

/**
 * Created with IntelliJ IDEA.
 * User: jbdennin
 * Date: 10/4/13
 * Time: 5:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class TankVector {
    private double vx, vy;

    public TankVector(double vx, double vy) {
        this.vx = vx;
        this.vy = vy;
    }

    public static TankVector createVector(double angle, double magnitude) {
        //TODO Joel
        return null;
    }

    public double getVx() {
        return vx;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public double getVy() {
        return vy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public double getMagnitude() {
        //TODO conversion Joel
        return 0d;
    }

    public double getAngle() {
        //TODO conversion Joel
        return 0d;
    }

    public void add(TankVector tankVector) {
        this.vx += tankVector.vx;
        this.vy += tankVector.vy;
    }
}
