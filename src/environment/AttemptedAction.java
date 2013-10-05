package environment;

import agent.AbstractAgent;
import environment.Action;

/**
 * Created with IntelliJ IDEA.
 * User: jbdennin
 * Date: 10/3/13
 * Time: 8:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class AttemptedAction extends Action {

    private boolean succeeded;

    public AttemptedAction(Action action, boolean succeeded) {
        super(action.agent, action.type, action.value);
        this.succeeded = succeeded;
    }

    public boolean wasSuccessful() {
        return succeeded;
    }
}
