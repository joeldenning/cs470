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
    protected static double[][] grid;

    private Map<Environment.Component, Collection<String>> desiredEnvironment = new HashMap<Environment.Component, Collection<String>>();
    protected double truePositive, trueNegative;

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
        //TODO Joel - add tank movement actions
        return null;
    }

    private void updateGrid(Environment environment) {
        OccupancyGrid occupancyGrid = environment.getOccupancyGrid();
        for( int x=(int)environment.getMyState().getX(); x<OccupancyGrid.SIZE_OF_GRID; x++ ) {
            for( int y=(int)environment.getMyState().getY(); y<OccupancyGrid.SIZE_OF_GRID; y++ ) {
                boolean reportedOccupied = occupancyGrid.isOccupied(x, y);
                //TODO Brian - Grid filter updating
                //look up helicopter example
            }
        }
    }

    protected synchronized void updateGrid(int x, int y, double probability) {
        grid[x][y] = probability;
    }

    @Override
    public void processAttemptedActions(List<AttemptedAction> attemptedActions) {
        //leave blank, we're not going to do anything
    }

    @Override
    public Map<Environment.Component, Collection<String>> desiredEnvironment() {
        return desiredEnvironment;
    }
}
