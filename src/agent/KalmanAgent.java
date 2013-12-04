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
        long millisUntilTankInCrosshairs = whenIsTankInCrosshair(environment.getMyState());
        long millisUntilBulletHitsTank = whenWillBulletHitTank(environment.getMyState());
        if( millisUntilTankInCrosshairs < 0 || millisUntilTankInCrosshairs > WAITING_FOR_PERFECT_SHOT_MAX_TIME
                || millisUntilBulletHitsTank < 0) {
            actions.add(getTurningAction(environment.getMyState()));
        } else if( millisUntilTankInCrosshairs - millisUntilBulletHitsTank < SHOOTING_THRESHOLD )
            actions.add(new Action(this, Action.Type.SHOOT, ""));
        return actions;
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
        //TODO Brian
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    private Action getTurningAction(Tank myState) {
        //TODO Joel
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    private void updateKalmanFilter(Environment environment) {
        //TODO Joel
    }

    private boolean tankWasDestroyed(Environment environment) {
        //TODO Joel
        return false;  //To change body of created methods use File | Settings | File Templates.
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
