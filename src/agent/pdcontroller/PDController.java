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
			SPEED_PROPORTIONAL_CONSTANT = .01, SPEED_DERIVATIVE_CONSTANT = .5,
			ANGULAR_VELOCITY_PROPORTIONAL_CONSTANT = .01, ANGULAR_VELOCITY_DERIVATIVE_CONSTANT = .5;
	private final AbstractAgent agent;
    private double lastAngularVelocityError = 0, lastSpeedError = 0;

	public PDController(AbstractAgent agent) {
		this.agent = agent;
	}

    public List<Action> getActions(TankVector desiredVector, Environment environment, long timeElapsed) {
        List<Action> actions = new ArrayList<Action>();
	    actions.add(getSpeedAction(desiredVector, environment, timeElapsed));
	    actions.add(getAngleAction(desiredVector, environment, timeElapsed));
	    return actions;
    }

	private Action getAngleAction(TankVector desiredVector, Environment environment, long timeElapsed) {
		double curAngVel = environment.getMyState().getAngularVelocity();
        double desiredMinusActualAngle = desiredVector.getAngle() - environment.getMyState().getAngle();
        double desiredAngVel = Math.max(-1d, desiredMinusActualAngle);
        desiredAngVel = Math.min(1d, desiredAngVel);
		double curError = desiredAngVel - curAngVel;
        double derivativeTerm = curError - lastAngularVelocityError;
		double newAngVel =
				ANGULAR_VELOCITY_PROPORTIONAL_CONSTANT * curError +
				ANGULAR_VELOCITY_DERIVATIVE_CONSTANT * derivativeTerm / timeElapsed;

        lastAngularVelocityError = curError;
		return new Action(agent, Action.Type.ANGVEL, Double.toString(newAngVel));
	}

	private Action getSpeedAction(TankVector desiredVector, Environment environment, long timeElapsed) {
		double curSpeed = environment.getMyState().getVx() + environment.getMyState().getVy();
		double curError = desiredVector.getMagnitude() - curSpeed;
        double derivativeTerm = curError - lastSpeedError;
		double newSpeed =
				SPEED_PROPORTIONAL_CONSTANT * curError +
				SPEED_DERIVATIVE_CONSTANT * derivativeTerm / timeElapsed;

        lastSpeedError = curError;
		return new Action(agent, Action.Type.SPEED, Double.toString(newSpeed));
	}
}
