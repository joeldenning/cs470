package agent;

import Jama.Matrix;
import agent.gridvisualization.GridVisualizationThread;
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
    private static final double deltaT = .5;
    private static final double friction = 0;
    private static Map<State, String> teamToDuckMap = new HashMap<State, String>();
    private static final long WAITING_FOR_PERFECT_SHOT_MAX_TIME = 1000;
    private static final double shot_v = 100;

    static {
        desiredEnvironment.put(Environment.Component.OTHER_TANKS, null);
        teamToDuckMap.put(State.SITTING_DUCK, "purple");
        teamToDuckMap.put(State.LINEAR, "green");
        teamToDuckMap.put(State.WILD, "red");
    }

    private final GridVisualizationThread visualization;

    private State state = State.SITTING_DUCK;
    private Matrix enemyState, sigmaSubX, sigmaSubZ, F, H, sigmaSubT;
    private int iteration;

    public KalmanAgent(int tankIndex) {
        super(tankIndex);
        final double position = 100, velocity = 100, acceleration = 100;
        double[][] dSigmaSubT = {
                {position,   0, 0,  0,   0,   0},
                {0,   velocity, 0,  0,   0,   0},
                {0,     0, acceleration,0,   0,   0},
                {0,     0, 0,  position, 0,   0},
                {0,     0, 0,  0,   velocity, 0},
                {0,     0, 0,  0,   0, acceleration},
        };
        double[][] dF = {
                {1,   deltaT,   Math.pow(deltaT, 2)/2,   0,   0,   0},
                {0,   1,        deltaT               ,   0,   0,   0},
                {0,   -friction, 1                   ,   0,   0,   0},
                {0,   0,        0                    ,   1,deltaT, Math.pow(deltaT, 2)/2},
                {0,   0,        0                    ,   0,   1,   deltaT},
                {0,   0,        0                    ,   0,   -friction,   1}
        };
        double[][] dH = {
                {1, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 0, 0}
        };
        double[][] dSigmaSubZ = {
                {25, 0 },
                {0 , 25}
        };
        double[][] dSigmaSubX = {
                {position,   0, 0,  0,   0,   0},
                {0,   velocity, 0,  0,   0,   0},
                {0,     0, acceleration,0,   0,   0},
                {0,     0, 0,  position, 0,   0},
                {0,     0, 0,  0,   velocity, 0},
                {0,     0, 0,  0,   0, acceleration},
        };
        sigmaSubX = new Matrix(dSigmaSubX);
        sigmaSubZ = new Matrix(dSigmaSubZ);
        sigmaSubT = new Matrix(dSigmaSubT);
        F = new Matrix(dF);
        H = new Matrix(dH);

        visualization = new GridVisualizationThread();
        visualization.setKalmanAgent(this);
        visualization.start();
    }

    @Override
    public List<Action> getActions(Environment environment) {

        if( enemyState == null ) {
            Tank enemyTank = environment.getTeam(teamToDuckMap.get(state)).getTanks().get(0);
            double[] dEnemyState = { enemyTank.getX(), 0, 0, enemyTank.getY(), 0, 0 };
            enemyState = new Matrix(dEnemyState, 6);
        }

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
        double intersection_result[] = calculateIntersection(environment.getMyState());
        time_to_delay = intersection_result[0];
        intersection_time = intersection_result[1];
//        if( millisUntilTankInCrosshairs < 0 || millisUntilTankInCrosshairs > WAITING_FOR_PERFECT_SHOT_MAX_TIME
//                || millisUntilBulletHitsTank < 0) {
//            actions.add(getTurningAction(environment.getMyState()));
//        } else if( millisUntilTankInCrosshairs - millisUntilBulletHitsTank < SHOOTING_THRESHOLD )
//            actions.add(new Action(this, Action.Type.SHOOT, ""));
        if(  intersection_time < 0 || time_to_delay > WAITING_FOR_PERFECT_SHOT_MAX_TIME )  {
//            actions.add(getTurningAction(environment.getMyState()));
        } else if( time_to_delay < SHOOTING_THRESHOLD ) {
            actions.add(new Action(this, Action.Type.SHOOT, ""));
        } else {
            actions.add(new Action(this, Action.Type.ANGVEL, "0"));
        }
        if( ++iteration % 1 == 0 ) {
            visualization.updateKalman();
            iteration = 0;
        }
        return actions;
    }

    private double[] calculateIntersection(Tank myState) {
        double toReturn[] = {-1, -1};
        double bullet_vy = Math.sin(myState.getAngle()) * shot_v;
        double bullet_vx = Math.cos(myState.getAngle()) * shot_v;
        double c = enemyState.get(3,0) - myState.getY() + (-1*bullet_vy/bullet_vx)*(enemyState.get(0,0)-myState.getX());
        double b = (-1*bullet_vy/bullet_vx) * (enemyState.get(1, 0)-bullet_vx) + enemyState.get(4, 0) - bullet_vy;
        double a = (-1*bullet_vy/bullet_vx) * enemyState.get(2,0) + enemyState.get(5,0);
        //quadratic formula
        if (b*b - 4 * a * c > 0)
        {
            double res1 = (-1*b + Math.sqrt(b*b - 4 * a * c))/(2*a);
            double res2 = (-1*b - Math.sqrt(b*b - 4 * a * c))/(2*a);
            double time = Math.max(res1,res2);
            if (time < 0)
                return toReturn;

            //substitute back in to solve for second variable
            double delay = ( (enemyState.get(0,0) - myState.getX()) + (time*(enemyState.get(1,0)-bullet_vx)) + (time*time*enemyState.get(2,0)) )/(-1*bullet_vx);
            if (delay < 0 || delay > time)
                return toReturn;

            toReturn[0] = delay;
            toReturn[1] = time;
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

    public synchronized Point getEnemyPosition(long millisIntoFuture) {
        if( enemyState == null )
            return new Point(0, 0);
        double secondsIntoTheFuture = (double)millisIntoFuture / 1000d;
        double x = enemyState.get(0,0) + enemyState.get(1,0)*secondsIntoTheFuture + enemyState.get(2,0)*secondsIntoTheFuture*secondsIntoTheFuture;
        double y = enemyState.get(3,0) + enemyState.get(4,0)*secondsIntoTheFuture + enemyState.get(5,0)*secondsIntoTheFuture*secondsIntoTheFuture;
        return new Point((int)x,(int)y);
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
        //update correction
        Matrix commonExpr = F.times(sigmaSubT).times(F.transpose()).plus(sigmaSubX);
        Matrix numerator = commonExpr.times(H.transpose());
        Matrix denominator = H.times(commonExpr).times(H.transpose()).plus(sigmaSubZ);
        Matrix kSubTPlus1 = numerator.times(denominator.inverse());

        //update mean
        Tank targetTank = environment.getTeam(teamToDuckMap.get(state)).getTanks().get(0);
        double[][] dZSubTPlus1 = {
                {targetTank.getX()},
                {targetTank.getY()}
        };
        Matrix zSubTPlus1 = new Matrix(dZSubTPlus1);
        Matrix mu_tPlus1 = F.times(enemyState);
        Matrix changeInObservation = zSubTPlus1.minus(H.times(F).times(enemyState));

//        System.out.println(Arrays.deepToString(F.times(enemyState).getArray()));


//        double[][] toPrint = kSubTPlus1.getArray();

//        for (int i =0; i < toPrint.length; i++) {
//            System.out.printf("%5f\t%f\n", toPrint[i][0], toPrint[i][1]);
//        }


        mu_tPlus1 = mu_tPlus1.plus(kSubTPlus1.times(changeInObservation));

        //update variance
        Matrix sigma_tPlus1 = Jama.Matrix.identity(6, 6).minus(kSubTPlus1.times(H)).times(commonExpr);

        enemyState = mu_tPlus1;
        sigmaSubT = sigma_tPlus1;

//        System.out.println(Arrays.deepToString(enemyState.getArray()));
//        System.out.println();
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
