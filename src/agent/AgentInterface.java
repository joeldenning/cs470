package agent;

import environment.Action;
import environment.Environment;

import java.util.List;

public interface AgentInterface {

	public List<Action> getAction(Environment environment);
}
