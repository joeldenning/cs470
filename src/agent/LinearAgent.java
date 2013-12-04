package agent;

import environment.AttemptedAction;
import environment.Action;
import environment.Environment;

import java.util.*;

public class LinearAgent extends AbstractAgent {

    private static final int MOVE_DURATION = 800;

    private long nextStateChange;
    private State state;

    private static Map<Environment.Component, Collection<String>> desiredEnvironment = new HashMap<Environment.Component, Collection<String>>();
    static {
        desiredEnvironment.put(Environment.Component.OTHER_TEAMS, null);
    }

    public LinearAgent(int tankIndex) {
    	super(tankIndex);
        state = State.MOVING_FORWARD;
        nextStateChange = System.currentTimeMillis();
    }

    @Override
	public List<Action> getActions(Environment environment) {
        List<Action> actions = new ArrayList<Action>();

        long currentTime = System.currentTimeMillis();

        if( currentTime >= nextStateChange ) {
        	nextStateChange = currentTime + MOVE_DURATION;
            switch (state) {
                case MOVING_FORWARD:
                    actions.add(createAction(Action.Type.SPEED, "1"));
                    break;
                case MOVING_BACKWARD:
                    actions.add(createAction(Action.Type.SPEED, "-1"));
                    break;
            }
        }

        return actions;
	}

    @Override
    public void processAttemptedActions(List<AttemptedAction> attemptedActions) {
    }

	@Override
	public Map<Environment.Component, Collection<String>> desiredEnvironment() {
		return desiredEnvironment;
	}

    @Override
    public long getNextStateChange() {
        return nextStateChange;
    }

    private enum State {
        MOVING_FORWARD, MOVING_BACKWARD
    }

}
