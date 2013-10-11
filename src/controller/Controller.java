package controller;

import agent.AbstractAgent;
import agent.DumbAgent;
import agent.PotentialFieldsAgent;
import agent.PotentialFieldsTestAgent;
import environment.AttemptedAction;
import communicator.Communicator;
import environment.Action;
import environment.Environment;
import environment.Tank;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Controller extends Thread {

	private static Communicator communicator;
	private AbstractAgent agent;

	public static void main(String[] args) throws FileNotFoundException {
        int numOfAgents = Integer.parseInt(args[3]);
        communicator = new Communicator(args[0], Integer.parseInt(args[1]), args[2]);
        init();
        for( int i=0; i<numOfAgents; i++ ) {
            Controller controller = new Controller(new PotentialFieldsAgent(i));
            controller.start();
        }
	}

    public Controller(AbstractAgent agent) throws FileNotFoundException {
		this.agent = agent;
        File loggingFile = new File(agent.getTankNumber()+".log");
        if( loggingFile.exists() )
            loggingFile.delete();
	}

    private static void init() {
        try {
            communicator.writeToSocketVerbose("");
        } catch (IOException e) {
            //expected, since the response is "bzrobots 1", which doesn't start with "ack"
            communicator.writeToSocketNoExpectedResponse("agent 1");
            System.out.println("agent 1");
        }
    }

	private void goToGoal(Environment environment) {
		List<Action> actions = agent.getActions(environment);
        List<AttemptedAction> resultsOfActions = new ArrayList<AttemptedAction>();
		for( Action action : actions ) {
			if( communicator.actionSucceeds(action) )
                resultsOfActions.add(new AttemptedAction(action, true));
            else
                resultsOfActions.add(new AttemptedAction(action, false));
		}
        agent.processAttemptedActions(resultsOfActions);
	}

    @Override
    public void run() {
        while(true) {
            Environment environment = communicator.getEnvironment(agent);
            if( environment.getMyState().getStatus() == Tank.Status.alive || true)
                goToGoal(environment);
            else
                break;
        }
    }

}
