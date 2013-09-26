package agent;

import environment.Action;
import environment.Environment;

public interface AgentInterface {

	public Action getAction(Environment environment);
}
