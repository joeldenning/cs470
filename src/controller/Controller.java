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
	private AbstractAgent agent;

	public static void main(String[] args) throws FileNotFoundException {
        int numOfAgents = Integer.parseInt(args[3]);
        communicator = new Communicator(args[0], Integer.parseInt(args[1]), args[2]);
        init();
        GridFilterControllerAgent controllerAgent = null;
        final int numOfControllerAgents = 1;
        if (numOfAgents > 0) {
            GridVisualizationThread gridVisualizationThread = new GridVisualizationThread();
            gridVisualizationThread.start();
            for (int i = 0; i < Math.min(numOfControllerAgents, numOfAgents); i++) {
                controllerAgent = new GridFilterControllerAgent(i, gridVisualizationThread);
                Controller controller = new Controller(controllerAgent);
                controller.start();
            }
        }
        for( int i=numOfControllerAgents; i<numOfAgents; i++ ) {
            Controller controller = new Controller(new GridFilterAgent(i));
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
        try {
            Thread.sleep(Math.max(0, agent.getNextStateChange() - System.currentTimeMillis()));
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
