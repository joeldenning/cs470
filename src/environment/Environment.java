package environment;

import java.util.List;
import java.util.Map;

public class Environment {

	private TankState myState;
	private List<TankState> myTeam;
	private Map<Team, List<TankState>> otherTeams;
	private List<Obstacle> obstacles; 
	private List<Base> bases;
	private Map<Team, Flag> flags;
	private List<Shot> shots;
	
}
