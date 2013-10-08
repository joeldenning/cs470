package agent.potentialfields;

/**
 * Created with IntelliJ IDEA.
 * User: jbdennin
 * Date: 10/4/13
 * Time: 5:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class TankVector {
    private double desiredSpeed, desiredAngularVelocity;

    public TankVector(double desiredSpeed, double desiredAngularVelocity) {
        this.desiredSpeed = desiredSpeed;
        this.desiredAngularVelocity = desiredAngularVelocity;
    }

//    public static TankVector createVector(double angle, double magnitude) {
//        return new TankVector(magnitude*Math.sin(angle), magnitude*Math.cos(angle));
//    }

    public double getDesiredSpeed() {
        return desiredSpeed;
    }

    public void setDesiredSpeed(double desiredSpeed) {
        this.desiredSpeed = desiredSpeed;
    }

    public double getDesiredAngularVelocity() {
        return desiredAngularVelocity;
    }

    public void setDesiredAngularVelocity(double desiredAngularVelocity) {
        this.desiredAngularVelocity = desiredAngularVelocity;
    }

//    public double getMagnitude() {
//        return Math.sqrt(Math.pow(desiredSpeed, 2d) * Math.pow(desiredAngularVelocity, 2d));
//    }
//
//    public double getAngle() {
//        return Math.atan(desiredAngularVelocity / desiredSpeed);
//    }

    public void add(TankVector tankVector) {
        this.desiredSpeed += tankVector.desiredSpeed;
        this.desiredAngularVelocity += tankVector.desiredAngularVelocity;
    }
}
