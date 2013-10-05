package agent.potentialfields;

import environment.Obstacle;
import environment.Tank;

/**
 * Created with IntelliJ IDEA.
 * User: jbdennin
 * Date: 10/4/13
 * Time: 5:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class TangentialField extends PotentialField{

    public TangentialField(Obstacle obstacle, Tank myTank) {
        super(myTank);
    }

    public TangentialField(Tank tank, Tank myTank) {
        super(myTank);
    }

    @Override
    public TankVector toTankVector() {
        //TODO Brian
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
