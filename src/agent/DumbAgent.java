package agent;

import environment.AttemptedAction;
import environment.Action;
import environment.Environment;

import java.util.*;

public class DumbAgent extends AbstractAgent {

    private static final int TURN_DURATION = 800;

    private long nextShot, nextStateChange;
    private Random random = new Random();
    private State state;

    private static Map<Environment.Component, Collection<String>> desiredEnvironment = new HashMap<Environment.Component, Collection<String>>();
    static {
        desiredEnvironment.put(Environment.Component.OTHER_TEAMS, null);
    }

    public DumbAgent(int tankIndex) {
        super(tankIndex);
        shotFired();
        state = State.TURNING;
        nextStateChange = System.currentTimeMillis();
    }

    @Override
	public List<Action> getActions(Environment environment) {
        List<Action> actions = new ArrayList<Action>();

        long currentTime = System.currentTimeMillis();

        if( currentTime >= nextShot )
            actions.add(createAction(Action.Type.SHOOT, ""));

        if( currentTime >= nextStateChange ) {
            switch (state) {
                case MOVING_FORWARD:
                    actions.add(createAction(Action.Type.SPEED, "0"));
                    actions.add(createAction(Action.Type.ANGVEL, "-50"));
                    break;
                case TURNING:
                    actions.add(createAction(Action.Type.ANGVEL, "0"));
                    actions.add(createAction(Action.Type.SPEED, "1"));
                    break;
            }
        }

        return actions;
	}

    @Override
    public void processAttemptedActions(List<AttemptedAction> attemptedActions) {
        for( AttemptedAction action : attemptedActions) {
            switch (action.getType()) {
                case SHOOT:
                    if( action.wasSuccessful() )
                        shotFired();
                    break;
                case SPEED:
                    if( action.wasSuccessful() ) {
                        if( action.getValue().equals("0") )
                            slowingDown();
                        else if( action.getValue().equals("1") )
                            speedingUp();
                    }
                    break;
                case ANGVEL:
                    if( action.wasSuccessful() ) {
                        if( action.getValue().equals("-50") )
                            startingRotation();
                        else if( action.getValue().equals("0") )
                            slowingRotation();
                    }
                    break;
            }
        }
    }

	@Override
	public Map<Environment.Component, Collection<String>> desiredEnvironment() {
		return desiredEnvironment;
	}

	private void slowingRotation() {
        state = State.MOVING_FORWARD;
        nextStateChange = System.currentTimeMillis() + 3000l + random.nextLong() % 5000;
    }

    private void startingRotation() {
        state = State.TURNING;
        nextStateChange = System.currentTimeMillis() + TURN_DURATION;
    }

    private void speedingUp() {
        state = State.MOVING_FORWARD;
        nextStateChange = System.currentTimeMillis() + 3000l + random.nextLong() % 5000;
    }

    private void slowingDown() {
        state = State.TURNING;
        nextStateChange = System.currentTimeMillis() + TURN_DURATION;
    }

    private void shotFired() {
        nextShot = System.currentTimeMillis() + 1500l + random.nextLong() % 1000;
    }

    private enum State {
        MOVING_FORWARD, TURNING
    }

}
