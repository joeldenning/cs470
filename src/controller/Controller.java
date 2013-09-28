package controller;

import agent.AgentInterface;
import agent.DumbAgent;
import communicator.Communicator;
import environment.Action;
import environment.Environment;

import java.util.List;

public class Controller {

	private Communicator communicator;
	private AgentInterface agent;
	
	public static void main(String[] args) {
		Controller controller = new Controller("", new DumbAgent());
		while(true) {
			controller.goToGoal();
		}
	}

	public Controller(String url, AgentInterface agent) {
		communicator = new Communicator(url);
		this.agent = agent;
	}

	private void goToGoal() {
		Environment environment = communicator.getEnvironment();
		List<Action> actions = agent.getActions(environment);
		for( Action action : actions ) {
			communicator.doAction(action);
		}
	}
	
}
