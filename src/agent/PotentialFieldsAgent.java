package agent;

import agent.pdcontroller.PDController;
import agent.potentialfields.AttractiveField;
import agent.potentialfields.PotentialField;
import agent.potentialfields.RepulsiveField;
import agent.potentialfields.TangentialField;
import agent.potentialfields.TankVector;
import environment.Action;
import environment.AttemptedAction;
import environment.Base;
import environment.Environment;
import environment.Flag;
import environment.Obstacle;
import environment.Tank;
import environment.Team;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Joel
 * Date: 9/28/13
 * Time: 2:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class PotentialFieldsAgent extends AbstractAgent {

    private static Map<Environment.Component, Collection<String>> desiredEnvironment = new HashMap<Environment.Component, Collection<String>>();

    static {
        //my state and my team are automatically added
        desiredEnvironment.put(Environment.Component.FLAGS, null);
        desiredEnvironment.put(Environment.Component.OBSTACLES, null);
        desiredEnvironment.put(Environment.Component.BASES, null);
        desiredEnvironment.put(Environment.Component.OTHER_TANKS, null);
        desiredEnvironment.put(Environment.Component.OTHER_TEAMS, null);
    }

    private State state;
    private long lastTimeActionsPerformed;
    private PDController pdController;

    public PotentialFieldsAgent(int tankIndex) {
        super(tankIndex);
        state = State.PURSUING;
        pdController = new PDController(this);
    }

    @Override
    public List<Action> getActions(Environment environment) {
        findState(environment);

        List<PotentialField> potentialFields = generatePotentialFields(environment);
        TankVector desiredVector = sum(potentialFields);

        List<Action> actions = pdController.getActions(desiredVector, environment, System.currentTimeMillis() - lastTimeActionsPerformed);

        lastTimeActionsPerformed = System.currentTimeMillis();
        return actions;
    }

    protected TankVector sum(List<PotentialField> potentialFields) {
        TankVector sum = new TankVector(0, 0);
        for( PotentialField potentialField : potentialFields )
            sum.add(potentialField.toTankVector());
        return sum;  //To change body of created methods use File | Settings | File Templates.
    }

    protected List<PotentialField> generatePotentialFields(Environment environment) {
        //TODO Brian - find the closest flag and create an attractive field
    	List<PotentialField> fields = new ArrayList<PotentialField>();
        Flag closestFlag = null;
        if( state == State.PURSUING ) {
            closestFlag = findClosestFlag(environment);
            fields.add(new AttractiveField(closestFlag, environment.getMyState()));
        } else {
            Base mybase = environment.getMyTeam().getBase();
            fields.add(new AttractiveField(mybase,environment.getMyState()));
        }
        
        for (Obstacle ob : environment.getObstacles()) {
        	fields.add(new RepulsiveField(ob,environment.getMyState()));
            if( state == State.PURSUING ) {
            	fields.add(new TangentialField(ob,environment.getMyState(), closestFlag.getX(), closestFlag.getY()));
            	fields.add(new RepulsiveField(ob, environment.getMyState()));
            }
            else {
                //returning
                double destX = 0, destY = 0;
                for( int i=0; i<environment.getMyTeam().getBase().getNumOfCorners(); i++ ) {
                    Point2D point = environment.getMyTeam().getBase().getCorner(i);
                    destX += point.getX();
                    destY += point.getY();
                }
                destX /= (double)environment.getMyTeam().getBase().getNumOfCorners();
                destY /= (double)environment.getMyTeam().getBase().getNumOfCorners();
                fields.add(new TangentialField(ob, environment.getMyState(), destX, destY));
                fields.add(new RepulsiveField(ob, environment.getMyState()));
            }
        }

        for (Team team : environment.getTeams()) {
        	for (Tank tank : team.getTanks()) {
        		fields.add(new RepulsiveField(tank,environment.getMyState()));
                fields.add(new TangentialField(tank, environment.getMyState()));
        	}
        }
        
        return fields;
    }

    private Flag findClosestFlag(Environment environment) {
    	double closestDistance = Double.MAX_VALUE;
    	Flag closestFlag = null;
    	double myX = environment.getMyState().getX();
    	double myY = environment.getMyState().getY();
		for (Flag flag : environment.getFlags()) {
            if( !flag.equals(environment.getMyTeam().getFlag()) ) {
                double distance = Math.sqrt(Math.pow(flag.getX()-myX, 2) + Math.pow(flag.getY()-myY, 2));
                if (closestDistance > distance) {
                    closestDistance = distance;
                    closestFlag = flag;
                }
            }
		}
//        System.out.println(closestFlag.getX()+", "+closestFlag.getY());
        return closestFlag;
	}

	@Override
    public void processAttemptedActions(List<AttemptedAction> attemptedActions) {}

	@Override
	public Map<Environment.Component, Collection<String>> desiredEnvironment() {
        return desiredEnvironment;
	}

    public void findState(Environment environment) {
        if( !environment.getMyState().getFlag().equalsIgnoreCase(environment.getMyTeamColor())
                && !environment.getMyState().getFlag().equalsIgnoreCase("-"))
            state = State.RETURNING;
        else
            state = State.PURSUING;
    }

    private enum State {
        PURSUING, RETURNING
    }
}
