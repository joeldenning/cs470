package agent;

import Jama.Matrix;
import agent.gridvisualization.GridVisualizationThread;
import agent.pdcontroller.PDController;
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

    private static final double SHOOTING_THRESHOLD = 3;
    private static Map<Environment.Component, Collection<String>> desiredEnvironment = new HashMap<Environment.Component, Collection<String>>();
    private static final double deltaT = .1;
    private static final double friction = 0;
    private static Map<State, String> teamToDuckMap = new HashMap<State, String>();
    private static final double WAITING_FOR_PERFECT_SHOT_MAX_TIME = 10;
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
    private Point bulletIntersection = new Point(-401, -401), pointTurningTowards = new Point(-401, -401);
    private Point ourPosition = new Point(-401, -401);

    public KalmanAgent(int tankIndex) {
        super(tankIndex);
        resetKalmanFilter();

        visualization = new GridVisualizationThread();
        visualization.setKalmanAgent(this);
        visualization.start();
    }

    private void resetKalmanFilter() {
        final double position = 1, velocity = .1, acceleration = .05;
        double[][] dSigmaSubT = {
                {position,   0, 0,  0,   0,   0},
                {0,   velocity, 0,  0,   0,   0},
                {0,     0, acceleration,0,   0,   0},
                {0,     0, 0,  position, 0,   0},
                {0,     0, 0,  0,   velocity, 0},
                {0,     0, 0,  0,   0, acceleration},
        };
        double[][] dF = {
                {1,   deltaT,   Math.pow(deltaT, 2)/2d,   0,   0,   0},
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
                {200, 0 },
                {0 , 200}
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
        actions.add(getTurningAction(environment.getMyState()));
//        System.out.println("time to delay = "+time_to_delay);
//        System.out.println(inte);
        if( intersection_time >= 0 && time_to_delay < SHOOTING_THRESHOLD ) {
            actions.add(new Action(this, Action.Type.SHOOT, ""));
        }
        if( ++iteration % 1 == 0 ) {
            visualization.updateKalman();
            iteration = 0;
        }
        return actions;
    }

    private double[] calculateIntersection(Tank myState) {
        bulletIntersection = new Point(-401, -401);
        double toReturn[] = {-1, -1};
        double bullet_vy = Math.sin(myState.getAngle()) * shot_v;
        double bullet_vx = Math.cos(myState.getAngle()) * shot_v;
        double c = enemyState.get(3,0) - myState.getY() + (-1*bullet_vy/bullet_vx)*(enemyState.get(0,0)-myState.getX());
        double b = (-1*bullet_vy/bullet_vx) * (enemyState.get(1, 0)-bullet_vx) + enemyState.get(4, 0) - bullet_vy;
        double a = (-1*bullet_vy/bullet_vx) * enemyState.get(2,0) + enemyState.get(5,0);
        a = a == 0 ? 1 : a;
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

            bulletIntersection.x = (int)(myState.getX() + bullet_vx * (time - delay));
            bulletIntersection.y = (int)(myState.getY() + bullet_vy * (time - delay));
//            System.out.println(bulletIntersection);

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
        double distance = Math.sqrt(Math.pow(myState.getX() - enemyState.get(0, 0), 2d) + Math.pow(myState.getY() - enemyState.get(3, 0), 2d));

        double intoFuture = ( distance / 800d ) * 7000d;
        pointTurningTowards = this.getEnemyPosition((long)intoFuture);
        ourPosition = new Point((int)myState.getX(), (int)myState.getY());
//        System.out.println(pointTurningTowards);
        double desiredAngle = Math.atan2((double)pointTurningTowards.y - myState.getY(), (double)pointTurningTowards.x - myState.getX());
//        System.out.println(desiredAngle);
        double desiredAngVel = PDController.getClosestAngleDiff(desiredAngle, myState.getAngle());
//        double weight = 10 * (Math.abs( desiredAngle - myState.getAngle() ) / Math.PI);
//        System.out.println("weight = "+weight);
        desiredAngVel = desiredAngVel < 0 ? -.8 : .8;
//        desiredAngVel *= weight;
        Random rand = new Random();
        if( Math.abs(desiredAngle - myState.getAngle()) < .5 ) {
            desiredAngVel = desiredAngVel - desiredAngVel / ((double)(2 + rand.nextInt(2)));
        }
        desiredAngVel = Math.min(1, Math.max(-1, desiredAngVel));
//        System.out.println("angvel = "+desiredAngVel);
        return createAction(Action.Type.ANGVEL, Double.toString(desiredAngVel));
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
        return environment.getTeam(teamToDuckMap.get(state)).getTanks().get(0).getStatus() == Tank.Status.dead;
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
                state = State.SITTING_DUCK;
                break;
        }
        resetKalmanFilter();
    }

    @Override
    public void processAttemptedActions(List<AttemptedAction> attemptedActions) {}

    @Override
    public Map<Environment.Component, Collection<String>> desiredEnvironment() {
        return desiredEnvironment;
    }

    @Override
    public long getNextStateChange() {
        return (long)(deltaT*1000) + System.currentTimeMillis();
    }

    public Point getBulletIntersection() {
        return bulletIntersection;
    }

    public Point getPointTurningTowards() {
        return pointTurningTowards;
    }

    public Point getMyPosition() {
        return ourPosition;
    }

    private enum State {
        SITTING_DUCK, LINEAR, WILD, DONE
    }
}
