package environment;

import java.util.ArrayList;
import java.util.List;

public class Team {

	private List<Tank> tanks = new ArrayList<Tank>();
	private Flag flag;
	private Base base;
	private int playerCount;

	public void setPlayerCount(int playerCount) {
		this.playerCount = playerCount;
	}

	public int getPlayerCount() {
		return playerCount;
	}

	public void setBase(Base base) {
		this.base = base;
	}

	public Base getBase() {
		return base;
	}

	public void setFlag(Flag flag) {
		this.flag = flag;
	}

	public Flag getFlag() {
		return flag;
	}

	public void addTank(Tank tank) {
		this.tanks.add(tank);
	}
	
	public List<Tank> getTanks() {
		return new ArrayList<Tank>(tanks);
	}
}
