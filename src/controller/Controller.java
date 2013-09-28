package controller;

import agent.AgentInterface;
import agent.DumbAgent;
import communicator.Communicator;
import environment.Environment;

public class Controller {

	private Communicator communicator;
	private AgentInterface agent;
	
	public static void main(String[] args) {
		Controller controller = new Controller("", new DumbAgent());
		while(true) {
			controller.goToGoal();
		}
	}
	
	private void goToGoal() {
		Environment environment = communicator.getEnvironment();
	}

	public Controller(String url, AgentInterface agent) {
		communicator = new Communicator(url);
		this.agent = agent;
	}
	
}
