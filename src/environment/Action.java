package environment;

import agent.AbstractAgent;

public class Action {

	protected Type type;
    protected String value;
    protected AbstractAgent agent;

    public Action(AbstractAgent agent, Type type, String value) {
        this.type = type;
        this.value = value;
        this.agent = agent;
    }

    public String toBZFlagString() {
		return type.toString().toLowerCase()+" "+agent.getTankNumber()+" "+value;
	}

    public Type getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public static enum Type {
		SHOOT, SPEED, ANGVEL
	}
}
