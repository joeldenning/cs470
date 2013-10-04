package agent;

import environment.AttemptedAction;
import environment.Action;
import environment.Environment;

import java.util.List;

import static environment.Action.Type;

public abstract class AbstractAgent {

    protected int tankIndex;

    protected AbstractAgent(int tankIndex) {
        this.tankIndex = tankIndex;
    }

    public abstract List<Action> getActions(Environment environment);

    public abstract void processAttemptedActions( List<AttemptedAction> attemptedActions );

    protected Action createAction(Type type, String value) {
        return new Action(this, type, value);
    }

    public int getTankNumber() {
        return tankIndex;
    }
}
