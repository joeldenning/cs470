package agent.pdcontroller;

import agent.AbstractAgent;
import agent.potentialfields.TankVector;
import environment.Action;
import environment.Environment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jbdennin
 * Date: 10/4/13
 * Time: 5:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class PDController {

	private static final double
			SPEED_PROPORTIONAL_CONSTANT = .01, SPEED_DERIVATIVE_CONSTANT = .02,
			ANGULAR_VELOCITY_PROPORTIONAL_CONSTANT = 1, ANGULAR_VELOCITY_DERIVATIVE_CONSTANT = .5;
	private final AbstractAgent agent;
    private double lastAngularVelocityError = 0, lastSpeedError = 0;

	public PDController(AbstractAgent agent) {
		this.agent = agent;
	}

    public List<Action> getActions(TankVector desiredVector, Environment environment, long timeElapsed) {
        List<Action> actions = new ArrayList<Action>();
	    actions.add(getSpeedAction(desiredVector, environment, timeElapsed));
	    actions.add(getAngleAction(desiredVector, environment, timeElapsed));
        if( environment.getMyState().getTimeToReload() <= 0 )
            actions.add(new Action(agent, Action.Type.SHOOT, ""));
	    return actions;
    }

	private Action getAngleAction(TankVector desiredVector, Environment environment, long timeElapsed) {
		double curAngVel = environment.getMyState().getAngularVelocity();
//        double counterClockwiseDifference = desiredVector.getAngle() - environment.getMyState().getAngle();
//        double clockwiseDifference = -2 * Math.PI - environment.getMyState().getAngle() - desiredVector.getAngle();
//        double clockwiseDifference = clockwiseAngle - environment.getMyState().getAngle();
        //double chosenDiff = Math.min(Math.abs(counterClockwiseDifference), Math.abs(clockwiseDifference));
        double desiredAngVel = getClosestAngleDiff(desiredVector.getAngle(),environment.getMyState().getAngle());//Math.max(-1d, chosenDiff);
        desiredAngVel = Math.min(1d, desiredAngVel);
		double curError = desiredAngVel - curAngVel;
        double derivativeTerm = curError - lastAngularVelocityError;
		double newAngVel =
				ANGULAR_VELOCITY_PROPORTIONAL_CONSTANT * curError +
				ANGULAR_VELOCITY_DERIVATIVE_CONSTANT * derivativeTerm / timeElapsed;

        newAngVel = Math.min(1, newAngVel);
        newAngVel = Math.max(-1, newAngVel);
        if (Math.abs(derivativeTerm) < 0.001)
        	newAngVel = 1;
        lastAngularVelocityError = curError;
		return new Action(agent, Action.Type.ANGVEL, Double.toString(newAngVel));
	}

	public static double getClosestAngleDiff(double desired, double current) {
		//We will pretend our current angle is 0 and normalize the desired angle to match this.
		double normalizedDesired = desired-current;
		//Account for -pi to pi range
		if (normalizedDesired < -1*Math.PI) {
			normalizedDesired += 2*Math.PI;
		}
		if (normalizedDesired > 1) {
			normalizedDesired = 1;
		}
		else if (normalizedDesired < -1) {
			normalizedDesired = -1;
		}
		return normalizedDesired;
	}

	private Action getSpeedAction(TankVector desiredVector, Environment environment, long timeElapsed) {
		double curSpeed = environment.getMyState().getVx() + environment.getMyState().getVy();
		double curError = desiredVector.getMagnitude() - curSpeed;
        double derivativeTerm = curError - lastSpeedError;
		double newSpeed =
				SPEED_PROPORTIONAL_CONSTANT * curError +
				SPEED_DERIVATIVE_CONSTANT * derivativeTerm / timeElapsed;

        newSpeed = Math.min(1, newSpeed);
        newSpeed = Math.max(-1, newSpeed);
        lastSpeedError = curError;
		return new Action(agent, Action.Type.SPEED, Double.toString(newSpeed));
	}
}
