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
    protected static double[][] grid;//the probablilty that a cell is occupied

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
        return new ArrayList<Action>();
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
}
