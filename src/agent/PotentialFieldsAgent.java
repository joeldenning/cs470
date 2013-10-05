package agent;

import agent.pdcontroller.PDController;
import agent.potentialfields.PotentialField;
import agent.potentialfields.TankVector;
import environment.Action;
import environment.AttemptedAction;
import environment.Environment;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Joel
 * Date: 9/28/13
 * Time: 2:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class PotentialFieldsAgent extends AbstractAgent {

    private static Map<Environment.Component, Collection<String>> desiredEnvironment = new HashMap<Environment.Component, Collection<String>>();

    static {
        desiredEnvironment.put(Environment.Component.FLAGS, null);
        desiredEnvironment.put(Environment.Component.OBSTACLES, null);
        desiredEnvironment.put(Environment.Component.BASES, null);
        desiredEnvironment.put(Environment.Component.OTHER_TANKS, null);
    }

    private State state;
    private long lastTimeActionsPerformed;
    private PDController pdController;

    public PotentialFieldsAgent(int tankIndex) {
        super(tankIndex);
        state = State.PURSUING;
        pdController = new PDController();
    }

    @Override
    public List<Action> getActions(Environment environment) {

        findState(environment);

        List<PotentialField> potentialFields = generatePotentialFields(environment);
        TankVector desiredVector = sum(potentialFields);

        List<Action> actions = pdController.getActions(desiredVector, environment, System.currentTimeMillis() - lastTimeActionsPerformed);

        lastTimeActionsPerformed = System.currentTimeMillis();
        return actions;

    }

    private TankVector sum(List<PotentialField> potentialFields) {
        TankVector sum = new TankVector(0, 0);
        for( PotentialField potentialField : potentialFields )
            sum.add(potentialField.toTankVector());
        return sum;  //To change body of created methods use File | Settings | File Templates.
    }

    private List<PotentialField> generatePotentialFields(Environment environment) {
        //TODO Brian - find the closest flag and create an attractive field

        if( state == State.PURSUING ) {
//            String colorOfClosestFlag = findColorOfClosestFlag(environment);
        } else {
            //state == RETURNING
        }

        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    @Override
    public void processAttemptedActions(List<AttemptedAction> attemptedActions) {}

	@Override
	public Map<Environment.Component, Collection<String>> desiredEnvironment() {
        return desiredEnvironment;
	}

    public void findState(Environment environment) {
        if( environment.getMyState().getFlag() != null
                && !environment.getMyState().getFlag().equalsIgnoreCase(environment.getMyTeamColor()) )
            state = State.PURSUING;
        else
            state = State.RETURNING;
    }

    private enum State {
        PURSUING, RETURNING
    }
}
