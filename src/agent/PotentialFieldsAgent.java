package agent;

import environment.Action;
import environment.AttemptedAction;
import environment.Environment;

import java.util.Collection;
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

    public PotentialFieldsAgent(int tankIndex) {
        super(tankIndex);
    }

    @Override
    public List<Action> getActions(Environment environment) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void processAttemptedActions(List<AttemptedAction> attemptedActions) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

	@Override
	public Map<Environment.Component, Collection<String>> desiredEnvironment() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}
}
