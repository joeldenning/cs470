package agent;

import environment.*;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jbdennin
 * Date: 11/15/13
 * Time: 9:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class GridFilterAgent extends AbstractAgent {

	private static final long MAX_STATE_DURATION = 5000l;
    private static final double VX_THRESHOLD = 0.1;
    private static final double VY_THRESHOLD = 0.1;
    protected static double[][] grid;//the probablilty that a cell is occupied

    private Map<Environment.Component, Collection<String>> desiredEnvironment = new HashMap<Environment.Component, Collection<String>>();
    protected static double truePositive, trueNegative;
	private Random rand = new Random();

	private State state = State.TURNING;
	private long nextStateChange = -1;
    private boolean moving = false;

    public GridFilterAgent(int tankIndex) {
        super(tankIndex);
        Collection<String> tankNumbers = new ArrayList<String>();
        tankNumbers.add(Integer.toString(tankIndex));
        desiredEnvironment.put(Environment.Component.OCCUPANCY_GRID, tankNumbers);
        desiredEnvironment.put(Environment.Component.CONSTANTS, null);
    }

    @Override
    public List<Action> getActions(Environment environment) {
        if( grid == null )
            return new ArrayList<Action>();
        updateGrid(environment);
	    List<Action> actions = new ArrayList<Action>();
	    long curTime = System.currentTimeMillis();
	    if( curTime >= nextStateChange ) {
		    switch( state ) {
			    case TURNING:
				    state = State.STRAIGHT;
				    actions.add(new Action(this, Action.Type.ANGVEL, "0"));
                    nextStateChange = curTime + (rand.nextLong() % MAX_STATE_DURATION);
				    break;
			    case STRAIGHT:
				    turn(actions, curTime);
                    break;
		    }
		    if( !moving ) {
			    //first time
			    actions.add(new Action(this, Action.Type.SPEED, "1.0"));
                moving = true;
		    }
	    } else if( environment.getMyState().getVx() < VX_THRESHOLD && environment.getMyState().getVy() < VY_THRESHOLD ) {
            turn(actions, curTime);
        }
        return actions;
    }

    private void turn(List<Action> actions, long curTime) {
        state = State.TURNING;
        String angVel;
        if( rand.nextBoolean() )
            angVel = "1.0";
        else
            angVel = "-1.0";
        actions.add(new Action(this, Action.Type.ANGVEL, angVel));
        nextStateChange = curTime + (rand.nextLong() % MAX_STATE_DURATION / 4);
    }

    private void updateGrid(Environment environment) {
        OccupancyGrid occupancyGrid = environment.getOccupancyGrid();
        Set<Map.Entry<OccupancyGrid.Coordinate, Boolean>> occGridEntries = occupancyGrid.entrySet();
        for (Map.Entry<OccupancyGrid.Coordinate, Boolean> entry: occGridEntries) {
            boolean reportedOccupied = entry.getValue();
            int x = entry.getKey().getX();
            int y = entry.getKey().getY();
            if (reportedOccupied) {
            	double bel_occ = truePositive * accessGrid(x,y);
            	double bel_not_occ = (1-trueNegative) * (1-accessGrid(x,y));
            	updateGrid(x,y,bel_occ/(bel_occ+bel_not_occ));
            }
            else {
            	double bel_occ = (1-truePositive) * accessGrid(x,y);
            	double bel_not_occ = trueNegative * (1-accessGrid(x,y));
            	updateGrid(x,y,bel_occ/(bel_occ+bel_not_occ));
            }
        }
    }
    
    protected synchronized double accessGrid(int x, int y) {
    	return grid[x + grid.length/2][y + grid.length/2];
    }
    protected synchronized void updateGrid(int x, int y, double probability) {
        grid[x + grid.length/2][y + grid.length/2] = probability;
    }

    @Override
    public void processAttemptedActions(List<AttemptedAction> attemptedActions) {
        //leave blank, we're not going to do anything
    }

    @Override
    public Map<Environment.Component, Collection<String>> desiredEnvironment() {
        return desiredEnvironment;
    }

	private enum State {
		STRAIGHT, TURNING
	}
}
