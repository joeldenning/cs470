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
			SPEED_PROPORTIONAL_CONSTANT = 1, SPEED_DERIVATIVE_CONSTANT = 1,
			ANGULAR_VELOCITY_PROPORTIONAL_CONSTANT = 1, ANGULAR_VELOCITY_DERIVATIVE_CONSTANT = 1;

	private final AbstractAgent agent;

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
		double currentError = desiredVector.getDesiredAngularVelocity() - curAngVel;
		double actualAngVel =
				ANGULAR_VELOCITY_PROPORTIONAL_CONSTANT * (desiredVector.getDesiredAngularVelocity()-curAngVel) +
				ANGULAR_VELOCITY_DERIVATIVE_CONSTANT * (currentError / timeElapsed);
		return new Action(agent, Action.Type.ANGVEL, Double.toString(actualAngVel));
	}

	private Action getSpeedAction(TankVector desiredVector, Environment environment, long timeElapsed) {
		double curSpeed = environment.getMyState().getVx() + environment.getMyState().getVy();
		double currentError = desiredVector.getDesiredSpeed() - curSpeed;
		double actualSpeed =
				SPEED_PROPORTIONAL_CONSTANT * (desiredVector.getDesiredSpeed()-curSpeed) +
				SPEED_DERIVATIVE_CONSTANT * (currentError / timeElapsed);
		return new Action(agent, Action.Type.SPEED, Double.toString(actualSpeed));
	}
}
