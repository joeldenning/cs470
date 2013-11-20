package communicator;

import agent.AbstractAgent;
import environment.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.awt.geom.Point2D;


public class Communicator {

    private String color;
    String LIST_START = "begin";
    String LIST_END = "end";
    Socket comSocket;
    PrintWriter out;
    BufferedReader in;
	
	public Communicator(String url, int socket, String color) {
		connectToSocket(url,socket);
        this.color = color;
	}

	public boolean actionSucceeds(Action action) {
        try {
            writeToSocketSilent(action.toBZFlagString());
            return true;
        } catch (IOException e) {
            return false;
        }
    }
	
	private void connectToSocket(String host, int socket) {
		try {
            comSocket = new Socket(host, socket);
            out = new PrintWriter(comSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                                        comSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + host);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + host);
        }
	}

    /**
     * Write to socket without logging
     * @param command
     * @return
     * @throws IOException
     */
    public String writeToSocketSilent(String command) throws IOException {
        return writeToSocket(command, false);
    }

    public String writeToSocketVerbose(String command) throws IOException {
        return writeToSocket(command, true);
    }

	private synchronized String writeToSocket(String command, boolean verbose) throws IOException {
        if( verbose )
            System.out.println(command);
        out.println(command);
        String ack = in.readLine();
        if (!ack.startsWith("ack"))
            throw new IOException("Failed command '" + command + "' with error '" + ack + "'");
        if( verbose )
            System.out.println("\t"+ack);
        String returned = in.readLine();
        if (returned.equals(LIST_START)) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null && !line.equals(LIST_END)) {
                sb.append(line + "\n");
                if( verbose )
                    System.out.println("\t"+line);
            }
            return sb.toString();
        } else {
            return returned;
        }
    }

	public Environment getEnvironment(AbstractAgent agent) {
		Environment environment = new Environment(color);

		Map<Environment.Component, Collection<String>> desiredEnvironment = agent.desiredEnvironment();
//		if( desiredEnvironment.containsKey(Environment.Component.OTHER_TEAMS) )
			loadTeams(environment);
		if( desiredEnvironment.containsKey(Environment.Component.OBSTACLES) )
			loadObstacles(environment);
		if( desiredEnvironment.containsKey(Environment.Component.BASES) )
			loadBases(environment);
		if( desiredEnvironment.containsKey(Environment.Component.FLAGS) )
			loadFlags(environment);
        loadMyTanks(environment, agent);
        if (desiredEnvironment.containsKey(Environment.Component.OTHER_TANKS) )
			loadOtherTanks(environment);
		if( desiredEnvironment.containsKey(Environment.Component.CONSTANTS) )
			loadConstants(environment);
		if (desiredEnvironment.containsKey(Environment.Component.OCCUPANCY_GRID)) {
			for( String tankNumber : desiredEnvironment.get(Environment.Component.OCCUPANCY_GRID) ) {
				loadOccupancyGrid(environment, tankNumber);
			}
		}

		return environment;
	}

	private void loadOccupancyGrid(Environment environment, String tankNumber) {
		/*
			at 20,20
			size 5x4
			0110
			0111
			0111
			0001
			0100
		 */
		Scanner scan = null;
		String response;
		try {
			response = writeToSocketSilent("occgrid " + tankNumber);
			scan = new Scanner(response);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			return;
		}
		scan.next(); //skip the word at
		String dim = scan.next();
		String[] dimArr = dim.split(",");
		int originX = Integer.parseInt(dimArr[0]);
		int originY = Integer.parseInt(dimArr[1]);
		scan.next(); //skip the word size
		String size = scan.next();
		String[] sizeArr = size.split("x");
		int sizeX = Integer.parseInt(sizeArr[0]);
		int sizeY = Integer.parseInt(sizeArr[1]);
		scan.nextLine();
		for (int x = 0; x < sizeX; x++) {
			String line = scan.nextLine();
			for (int y = 0; y < sizeY; y++) {
				char occupied = line.charAt(y);
				environment.getOccupancyGrid().setOccupied(originX + x, originY + y, occupied != '0');
			}
		}
		scan.close();
	}

	private void loadConstants(Environment environment) {
        Scanner scan = null;
        try {
            scan = new Scanner(writeToSocketSilent("constants"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
			return;
        }
        while( scan.hasNext() ) {
			scan.next(); //skip the word constants
			environment.putConstant(scan.next(), scan.next());
		}
		scan.close();
	}

	private void loadOtherTanks(Environment environment) {
		//othertank [callsign] [color] [status] [flag] [x] [y] [angle]
        Scanner scan = null;
        try {
            scan = new Scanner(writeToSocketSilent("othertanks"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
			return;
        }
        while( scan.hasNext() ) {
			scan.next(); //skip the word othertank
			Tank tank = new Tank();
			tank.setCallSign(scan.next());
			String color = scan.next();
			tank.setStatus(scan.next());
			tank.setFlag(scan.next());
			tank.setX(scan.nextDouble());
			tank.setY(scan.nextDouble());
			tank.setAngle(scan.nextDouble());
			environment.getTeam(color).addTank(tank);
            //unknown value
            //scan.next();
		}
		scan.close();
	}

	private void loadMyTanks(Environment environment, AbstractAgent agent) {
//		mytank [index] [callsign] [status] [shots available] [time to reload] [flag] [x] [y] [angle] [vx] [vy] [angvel]
        String myTanks = null;
        try {
            myTanks = writeToSocketSilent("mytanks");
        } catch (IOException e) {
            System.err.println(e.getMessage());
			return;
        }
        Scanner scan = new Scanner(myTanks);
		while( scan.hasNext() ) {
			scan.next(); //skip the word my tank
			Tank tank = new Tank();
			tank.setIndex(Integer.parseInt(scan.next()));
			tank.setCallSign(scan.next());
			tank.setStatus(scan.next());
			tank.setShotsAvailable(scan.next());
			tank.setTimeToReload(scan.nextDouble());
			tank.setFlag(scan.next());
			tank.setX(scan.nextDouble());
			tank.setY(scan.nextDouble());
			tank.setAngle(scan.nextDouble());
			tank.setVx(scan.nextDouble());
			tank.setVy(scan.nextDouble());
			tank.setAngularVelocity(scan.nextDouble());
			environment.getMyTeam().addTank(tank);
		}
        environment.setMyState(environment.getMyTeam().getTanks().get(agent.getTankNumber()));
		scan.close();
	}

	private void loadFlags(Environment environment) {
        String flags = null;
        try {
            flags = writeToSocketSilent("flags");
        } catch (IOException e) {
            System.err.println(e.getMessage());
			return;
        }
        Scanner scan = new Scanner(flags);
		while( scan.hasNext() ) {
			scan.next(); //skip the word flag
			String color = scan.next();
			String possessingColor = scan.next();
			Flag flag = new Flag(possessingColor,
					scan.nextDouble(),
					scan.nextDouble());
			environment.getTeam(color).setFlag(flag);
			environment.addFlag(flag);
		}
		scan.close();
	}

	private void loadBases(Environment environment) {
        String bases = null;
        try {
            bases = writeToSocketSilent("bases");
        } catch (IOException e) {
            System.err.println(e.getMessage());
			return;
        }
        Scanner scan = new Scanner(bases);
		while( scan.hasNext() ) {
			scan.next(); //skip the word base
			String color = scan.next();
			Team team = environment.getTeam(color);
			
			List<Point2D.Double> corners = new ArrayList<Point2D.Double>();
			//I'm just going to assume bases always have four corners.
			for (int i = 0; i < 4; i++) {
				corners.add(new Point2D.Double(scan.nextDouble(),scan.nextDouble()));
			}
			
			team.setBase(new Base(corners));
			/*team.setBase(new Base(
                    scan.nextDouble(),
                    scan.nextDouble(),
                    scan.nextDouble(),
                    scan.nextDouble()));
            //unknown next four doubles
            scan.nextDouble();
            scan.nextDouble();
            scan.nextDouble();
            scan.nextDouble();*/
		}
		scan.close();
	}

	private void loadObstacles(Environment environment) {
        String obstacles = null;
        try {
            obstacles = writeToSocketSilent("obstacles");
        } catch (IOException e) {
            System.err.println(e.getMessage());
			return;
        }
        Scanner scan = new Scanner(obstacles);
		while( scan.hasNextLine() ) {
			String line = scan.nextLine();
			String splitted[] = line.split("\\s+");
			List<Point2D.Double> corners = new ArrayList<Point2D.Double>();
			
			for (int i = 1; i < splitted.length; i+=2) {
				corners.add(new Point2D.Double(Double.parseDouble(splitted[i]),Double.parseDouble(splitted[i+1])));
			}
			
			environment.addObstacle(new Obstacle(corners));
			/*
			environment.addObstacle(
					new Obstacle(
							scan.nextDouble(),
                            scan.nextDouble(),
                            scan.nextDouble(),
                            scan.nextDouble()));
            //unknown next four doubles
            scan.nextDouble();
            scan.nextDouble();
            scan.nextDouble();
            scan.nextDouble();*/
		}
		scan.close();
	}

	private void loadTeams(Environment environment) {
        String teams = null;
        try {
            teams = writeToSocketSilent("teams");
        } catch (IOException e) {
            System.err.println(e.getMessage());
			return;
        }
        Scanner scan = new Scanner(teams);
		while( scan.hasNext() ) {
			scan.next(); //skip the word team
			Team team = new Team();
			String name = scan.next();
			int playerCount = Integer.parseInt(scan.next());
			team.setPlayerCount(playerCount);
			environment.addTeam(name, team);
		}
		scan.close();
	}

    public void writeToSocketNoExpectedResponse(String command) {
        out.println(command);
    }
}
