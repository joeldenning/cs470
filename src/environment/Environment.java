package environment;

import java.util.*;

public class Environment {

	private Tank myState;
	private Map<String, Team> teams = new HashMap<String, Team>();
	private List<Obstacle> obstacles = new ArrayList<Obstacle>();
	private List<Shot> shots = new ArrayList<Shot>();
	private String myTeamColor;
	private Map<String, Boolean> constants = new HashMap<String, Boolean>();
	private OccupancyGrid occupancyGrid = new OccupancyGrid();

	public Tank getMyState() {
		return myState;
	}

	public void setMyState(Tank myState) {
		this.myState = myState;
	}

	public Team getTeam(String name) {
		return teams.get(name);
	}

	public Collection<Team> getTeams() {
		return teams.values();
	}

	public void addTeam(String name, Team team) {
		teams.put(name, team);
	}

	public void addObstacle(Obstacle obstacle) {
		obstacles.add(obstacle);
	}

	public List<Obstacle> getObstacles() {
		return obstacles;
	}

	public void addShot(Shot shot) {
		shots.add(shot);
	}

	public List<Shot> getShots() {
		return shots;
	}

	public void setShots(List<Shot> shots) {
		this.shots = shots;
	}

	public String getMyTeamColor() {
		return myTeamColor;
	}

	public void setMyTeamColor(String myTeamColor) {
		this.myTeamColor = myTeamColor;
	}

	public Team getMyTeam() {
		return teams.get(myTeamColor);
	}

	public void putConstant(String key, boolean value) {
		constants.put(key, value);
	}

	public boolean getConstant(String key) {
		return constants.get(key);
	}

	public OccupancyGrid getOccupancyGrid() {
		return occupancyGrid;
	}
}
