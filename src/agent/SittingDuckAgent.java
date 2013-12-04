package agent;

import environment.AttemptedAction;
import environment.Action;
import environment.Environment;
import environment.Environment.Component;

import java.util.*;

public class SittingDuckAgent extends AbstractAgent {

    private static Map<Environment.Component, Collection<String>> desiredEnvironment = new HashMap<Environment.Component, Collection<String>>();
    static {
    }

    public SittingDuckAgent(int tankIndex) {
        super(tankIndex);
        
    }

	@Override
	public List<Action> getActions(Environment environment) {
		return new ArrayList<Action>();
	}

	@Override
	public void processAttemptedActions(List<AttemptedAction> attemptedActions) {
		
	}

	@Override
	public Map<Component, Collection<String>> desiredEnvironment() {
		return desiredEnvironment;
	}

	@Override
	public long getNextStateChange() {
		return 0;
	}


}
