package agent.potentialfields;

import environment.Obstacle;
import environment.Tank;

/**
 * Created with IntelliJ IDEA.
 * User: jbdennin
 * Date: 10/4/13
 * Time: 5:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class RepulsiveField extends PotentialField {

    public RepulsiveField(Obstacle obstacle, Tank myself) {
        super(myself);
    }

    public RepulsiveField(Tank otherTank, Tank myself) {
        super(myself);
    }

    @Override
    public TankVector toTankVector() {
        //TODO Brian
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
