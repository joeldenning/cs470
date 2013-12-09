package agent;

import Jama.Matrix;
import environment.*;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jbdennin
 * Date: 12/3/13
 * Time: 7:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class KalmanAgent extends AbstractAgent {

    private static final long SHOOTING_THRESHOLD = 100;
    private static Map<Environment.Component, Collection<String>> desiredEnvironment = new HashMap<Environment.Component, Collection<String>>();
    private static final double deltaT = .1;
    private static final double friction = 0.1;
    private static Map<State, String> teamToDuckMap = new HashMap<State, String>();
    private static final long WAITING_FOR_PERFECT_SHOT_MAX_TIME = 1000;
    private static final double shot_v = 2;//TODO I have no idea what this should actually be

    static {
        desiredEnvironment.put(Environment.Component.OTHER_TANKS, null);
        teamToDuckMap.put(State.SITTING_DUCK, "purple");
        teamToDuckMap.put(State.LINEAR, "green");
        teamToDuckMap.put(State.WILD, "red");
    }

    private State state = State.SITTING_DUCK;
    private Matrix enemyState;
    private Matrix sigmaSubT;
    private Matrix F;

    protected KalmanAgent(int tankIndex) {
        super(tankIndex);
        double[] doubleState = { 0, 0, 0, 0, 0, 0 };
        enemyState = new Matrix(doubleState, 6);
        double[][] doubleSigmaSubT = {
                {100,   0, 0,  0,   0,   0},
                {0,   0.1, 0,  0,   0,   0},
                {0,     0, 0.1,0,   0,   0},
                {0,     0, 0,  100, 0,   0},
                {0,     0, 0,  0,   0.1, 0},
                {0,     0, 0,  0,   0, 0.1},
        };
        double[][] doubleF = {
                {1,   deltaT,   Math.pow(deltaT, 2)/2,   0,   0,   0},
                {0,   1,        deltaT               ,   0,   0,   0},
                {0,   -friction, 1                   ,   0,   0,   0},
                {0,   0,        0                    ,   1,deltaT, Math.pow(deltaT, 2)/2},
                {0,   0,        0                    ,   0,   1,   deltaT},
                {0,   0,        0                    ,   0,   -friction,   1}
        };
        sigmaSubT = new Matrix(doubleSigmaSubT);
        F = new Matrix(doubleF);
    }

    @Override
    public List<Action> getActions(Environment environment) {
        if( state == State.DONE )
            return new ArrayList<Action>();
        if( tankWasDestroyed(environment) ) {
            changeState();
        }
        updateKalmanFilter(environment);
        List<Action> actions = new ArrayList<Action>();
//        long millisUntilTankInCrosshairs = whenIsTankInCrosshair(environment.getMyState());
//        long millisUntilBulletHitsTank = whenWillBulletHitTank(environment.getMyState());
        double time_to_delay;
        double intersection_time;
        calculateIntersection(environment.getMyState(),&time_to_delay, &intersection_time);
//        if( millisUntilTankInCrosshairs < 0 || millisUntilTankInCrosshairs > WAITING_FOR_PERFECT_SHOT_MAX_TIME
//                || millisUntilBulletHitsTank < 0) {
//            actions.add(getTurningAction(environment.getMyState()));
//        } else if( millisUntilTankInCrosshairs - millisUntilBulletHitsTank < SHOOTING_THRESHOLD )
//            actions.add(new Action(this, Action.Type.SHOOT, ""));
        if( intersection_time < 0 && time_to_delay > WAITING_FOR_PERFECT_SHOT_MAX_TIME )  {
            actions.add(getTurningAction(environment.getMyState()));
        } else if( intersection_time-time_to_delay < SHOOTING_THRESHOLD )
        	actions.add(new Action(this, Action.Type.ANGVEL, "0"));
            actions.add(new Action(this, Action.Type.SHOOT, ""));
        return actions;
    }
    
    private void calculateIntersection(Tank myState, double* return_delay, double* return_intersection) {
    	*return_delay = -1;
    	*return_intersection = -1;
    	double bullet_vy = Math.sin(myState.getAngle()) * shot_v;
    	double bullet_vx = Math.cos(myState.getAngle()) * shot_v;
    	double c = enemyState.get(0,3) - myState.getY() + (-1*bullet_vy/bullet_vx)*(enemyState.get(0,0)-myState.getX());
    	double b = (-1*bullet_vy/bullet_vx) * (enemyState.get(0,1)-bullet_vx) + enemyState.get(0,4) - bullet_vy;
    	double a = (-1*bullet_vy/bullet_vx) * enemyState.get(0,2) + enemyState.get(0,5);
    	//quadratic formula
    	if (b*b - 4 * a * c > 0)
    	{
	    	double res1 = (-1*b + Math.sqrt(b*b - 4 * a * c))/(2*a);
	    	double res2 = (-1*b - Math.sqrt(b*b - 4 * a * c))/(2*a);
	    	double time = Math.max(res1,res2);
	    	if (time < 0)
	    		return toReturn;
	    	
	    	//substitute back in to solve for second variable
	    	double delay = ( (enemyState.get(0,0) - myState.getX()) + (time*(enemyState.get(0,1)-bullet_vx)) + (time*time*enemyState.get(0,2)) )/(-1*bullet_vx);
	    	if (delay < 0)
	    		return;
	    	
	    	*return_delay = delay;
	    	*return_intersection = time;
    	}
    	
    	return toReturn;
    }
    
    private long whenWillBulletHitTank(Tank myState) {
        //TODO Brian
        return 0;  //To change body of created methods use File | Settings | File Templates.
    }

    private long whenIsTankInCrosshair(Tank myState) {
        //TODO Brian
        return 0;  //To change body of created methods use File | Settings | File Templates.
    }

    public Point getEnemyPosition(long millisIntoFuture) {
        double x = enemyState.get(0,0) + enemyState.get(0,1)*millisIntoFuture + enemyState.get(0,2)*millisIntoFuture*millisIntoFuture;
        double y = enemyState.get(0,3) + enemyState.get(0,4)*millisIntoFuture + enemyState.get(0,5)*millisIntoFuture*millisIntoFuture;
        return new Point(x,y);
    }

    private Action getTurningAction(Tank myState) {
        boolean turnClockwise;

	    double otherX = myState.getY() / Math.atan(myState.getAngle());
	    double otherY = 0;

	    double slope = (myState.getY() - otherY) / (myState.getX() - otherX);
	    double yIntercept = myState.getY() - slope * myState.getX();

	    double enemyX = enemyState.get(0, 0);
	    double enemyY = enemyState.get(3, 0);

	    double enemyCalculatedY = slope * enemyX + yIntercept;
	    if( enemyCalculatedY > enemyY )
		    turnClockwise = true;
	    else
	        turnClockwise = false;

	    Action result;
	    if( turnClockwise )
		    result = createAction(Action.Type.ANGVEL, "-1.0");
	    else
	        result = createAction(Action.Type.ANGVEL, "1.0");
        return result;  //To change body of created methods use File | Settings | File Templates.
    }

    private void updateKalmanFilter(Environment environment) {
        //TODO Joel
    }

    private boolean tankWasDestroyed(Environment environment) {
        return environment.getTeam(teamToDuckMap.get(state)).getPlayerCount() == 0;
    }

    private void changeState() {
        switch( state ) {
            case SITTING_DUCK:
                state = State.LINEAR;
                break;
            case LINEAR:
                state = State.WILD;
                break;
            case WILD:
                state = State.DONE;
                break;
        }
    }

    @Override
    public void processAttemptedActions(List<AttemptedAction> attemptedActions) {}

    @Override
    public Map<Environment.Component, Collection<String>> desiredEnvironment() {
        return desiredEnvironment;
    }

    @Override
    public long getNextStateChange() {
        return (long)(deltaT*1000);
    }

    private enum State {
        SITTING_DUCK, LINEAR, WILD, DONE
    }
}
