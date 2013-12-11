package controller;

import agent.*;
import agent.gridvisualization.GridVisualizationThread;
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
	private static AbstractAgent agent;

	public static void main(String[] args) throws FileNotFoundException {
        int numOfAgents = Integer.parseInt(args[3]);
        communicator = new Communicator(args[0], Integer.parseInt(args[1]), args[2]);
        init();
        switch (communicator.getColor()) {
        	case "purple":
        		Controller sitting = new Controller(new LinearAgent(0));
                sitting.start();
                break;
                
        	case "green":
        		Controller linear = new Controller(new LinearAgent(0));
                linear.start();
                break;
            
        	case "red":
        		Controller wild = new Controller(new DumbAgent(0));
                wild.start();
                break;
        	
        	case "blue":
        		Controller shooter = new Controller(new KalmanAgent(0));
                shooter.start();
        		break;
        
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
        try {
            Thread.sleep(agent.getNextStateChange());
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public void run() {
        while (true) {
            Environment environment = communicator.getEnvironment(agent);
            if (environment.getMyState().getStatus() == Tank.Status.alive || true)
                goToGoal(environment);
            else
                break;
        }
    }

}
