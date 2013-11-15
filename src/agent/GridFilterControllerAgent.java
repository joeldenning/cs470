package agent;

import environment.Action;
import environment.AttemptedAction;
import environment.Environment;
import environment.Obstacle;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jbdennin
 * Date: 11/15/13
 * Time: 9:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class GridFilterControllerAgent extends GridFilterAgent {

    protected Set<Obstacle> obstacles = new HashSet<Obstacle>();

    public GridFilterControllerAgent(int tankIndex) {
        super(tankIndex);
    }

    @Override
    public List<Action> getActions(Environment environment) {
        if( grid == null ) {
            int size = Integer.parseInt(environment.getConstant("worldsize"));
            grid = new double[size][size];
            truePositive = Double.parseDouble(environment.getConstant("truepositive"));
            trueNegative = Double.parseDouble(environment.getConstant("truenegative"));
        }
        return super.getActions(environment);
    }

    @Override
    public void processAttemptedActions(List<AttemptedAction> attemptedActions) {
        analyzeForObstacles();
        updateOpenGL();
    }

    private void analyzeForObstacles() {
        //TODO Brian
    }

    private void updateOpenGL() {
        //TODO Joel
    }

}
