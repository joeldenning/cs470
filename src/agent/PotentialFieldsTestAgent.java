package agent;

import agent.AbstractAgent;
import agent.PotentialFieldsAgent;
import agent.pdcontroller.PDController;
import agent.potentialfields.PotentialField;
import agent.potentialfields.TankVector;
import environment.*;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: jbdennin
 * Date: 10/9/13
 * Time: 7:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class PotentialFieldsTestAgent extends PotentialFieldsAgent {
    private static Map<Environment.Component, Collection<String>> desiredEnvironment = new HashMap<Environment.Component, Collection<String>>();

    static {
        //my state and my team are automatically added
        desiredEnvironment.put(Environment.Component.FLAGS, null);
        desiredEnvironment.put(Environment.Component.OBSTACLES, null);
        desiredEnvironment.put(Environment.Component.BASES, null);
        desiredEnvironment.put(Environment.Component.OTHER_TANKS, null);
        desiredEnvironment.put(Environment.Component.OTHER_TEAMS, null);
    }

    public PotentialFieldsTestAgent(int tankIndex) {
        super(tankIndex);
    }

    @Override
    public List<Action> getActions(Environment environment) {
        PDController pdController = new PDController(this);
        int step = 15;
        try {
            PrintWriter pw = new PrintWriter("pf.gpi");

            pw.write("set xrange [-400.0: 400.0]\n" +
                    "set yrange [-400.0: 400.0]\n" +
                    "unset key\n" +
                    "set size square\n" +
                    "\n" +
                    "unset arrow\n");

            for( Obstacle obstacle : environment.getObstacles() ) {
                Point2D lastPoint = obstacle.getCorner(0);
                for( int i=1; i<obstacle.getNumOfCorners(); i++ ) {
                    pw.write("set arrow from "+lastPoint.getX()+", "+lastPoint.getY()+" to "
                            +obstacle.getCorner(i).getX()+", "+obstacle.getCorner(i).getY()
                    +" nohead lt 3\n");
                    lastPoint = obstacle.getCorner(i);
                }
                pw.write("set arrow from "+lastPoint.getX()+", "+lastPoint.getY()+" to "+
                    obstacle.getCorner(0).getX()+", "+obstacle.getCorner(0).getY()
                    +" nohead lt 3\n");
            }

            pw.write("\n" +
                    "plot '-' with vectors head\n");

            for( int x=-400; x<=400; x+=step ) {
                for( int y=-400; y<=400; y+=step ) {
                    Tank testTank = new Tank();
                    environment.setMyState(testTank);
                    testTank.setX(x);
                    testTank.setY(y);
                    List<PotentialField> potentialFields = super.generatePotentialFields(environment);
                    TankVector tankVector = sum(potentialFields);
                    double scaledMagnitude = .05 * tankVector.getMagnitude();
//                    scaledMagnitude = Math.min(20, scaledMagnitude);
                    tankVector = new TankVector(scaledMagnitude, tankVector.getAngle());

                    pw.printf("%10d %10d %10f %10f\n", x, y, tankVector.getXVector(), tankVector.getYVector());
                }

            }
            pw.write("e\n");
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.exit(0);
        return null;
    }

    @Override
    public void processAttemptedActions(List<AttemptedAction> attemptedActions) {}

    @Override
    public Map<Environment.Component, Collection<String>> desiredEnvironment() {
        return desiredEnvironment;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
