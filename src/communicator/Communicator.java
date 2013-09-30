package communicator;

import environment.*;
import java.net.*;

import java.util.Scanner;

String LIST_START = "begin";
String LIST_END = "end";


public class Communicator {

	private String url;
	Socket comSocket;
    PrintWriter out;
    BufferedReader in;
    
	
	public Communicator(String url, in socket) {
		this.url = url;
		connectToSocket(url,socket)
	}

	public void doAction(Action action) {
		//TODO
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

	private String writeToSocket(String command) throws IOException {
		out.println(command);
		String ack =  in.readLine();
		if (!ack.startsWith("ack") || !ack.contains(command))
			throw IOException;
		String returned =  in.readLine();
		if (returned.equals(LIST_START))
		{
			
		}
		else
		{
			return returned;
		}
	}

	public Environment getEnvironment() {
		Environment environment = new Environment();

		loadTeams(environment);
		loadObstacles(environment);
		loadBases(environment);
		loadFlags(environment);
		loadMyTanks(environment);
		loadOtherTanks(environment);
		loadConstants(environment);
		loadOccupancyGrid(environment);

		return environment;
	}

	private void loadOccupancyGrid(Environment environment) {
		/*
			at 20,20
			size 5x4
			0110
			0111
			0111
			0001
			0100
		 */
		for( int index=0; index<environment.getMyTeam().getPlayerCount(); index++ ) {
			Scanner scan = new Scanner(writeToSocket("occgrid "+index));
			while( scan.hasNext() ) {
				scan.next(); //skip the word at
				int originX = scan.nextInt();
				scan.next(); //skip the comma
				int originY = scan.nextInt();
				scan.next(); //skip the word size
				int sizeX = scan.nextInt();
				scan.nextByte(); //skip the x
				int sizeY = scan.nextInt();
				for( int x=0; x<sizeX; x++ ) {
					for( int y=0; y<sizeY; y++ ) {
						environment.getOccupancyGrid().setOccupied(x, y, scan.nextBoolean());
					}
				}
			}
			scan.close();
		}
	}

	private void loadConstants(Environment environment) {
		Scanner scan = new Scanner(writeToSocket("constants"));
		while( scan.hasNext() ) {
			scan.next(); //skip the word constants
			environment.putConstant(scan.next(), scan.nextBoolean());
		}
		scan.close();
	}

	private void loadOtherTanks(Environment environment) {
		//othertank [callsign] [color] [status] [flag] [x] [y] [angle]
		Scanner scan = new Scanner(writeToSocket("othertanks"));
		while( scan.hasNext() ) {
			scan.next(); //skip the word othertank
			Tank tank = new Tank();
			tank.setCallSign(scan.next());
			String color = scan.next();
			tank.setStatus(scan.next());
			tank.setFlag(scan.next());
			tank.setX(scan.nextDouble());
			tank.setAngle(scan.nextDouble());
			environment.getTeam(color).addTank(tank);
		}
		scan.close();
	}

	private void loadMyTanks(Environment environment) {
//		mytank [index] [callsign] [status] [shots available] [time to reload] [flag] [x] [y] [angle] [vx] [vy] [angvel]
		String myTanks = writeToSocket("mytanks");
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
		scan.close();
	}

	private void loadFlags(Environment environment) {
		String flags = writeToSocket("flags");
		Scanner scan = new Scanner(flags);
		while( scan.hasNext() ) {
			scan.next(); //skip the word flag
			String color = scan.next();
			String possessingColor = scan.next();
			Flag flag = new Flag(possessingColor,
					scan.nextDouble(),
					scan.nextDouble(),
					scan.nextDouble(),
					scan.nextDouble());
			environment.getTeam(color).setFlag(flag);
		}
		scan.close();
	}

	private void loadBases(Environment environment) {
		String bases = writeToSocket("bases");
		Scanner scan = new Scanner(bases);
		while( scan.hasNext() ) {
			scan.next(); //skip the word base
			String color = scan.next();
			Team team = environment.getTeam(color);
			team.setBase(new Base(
					Double.parseDouble(scan.next()),
					Double.parseDouble(scan.next()),
					Double.parseDouble(scan.next()),
					Double.parseDouble(scan.next())
			));
		}
		scan.close();
	}

	private void loadObstacles(Environment environment) {
		String obstacles = writeToSocket("obstacles");
		Scanner scan = new Scanner(obstacles);
		while( scan.hasNext() ) {
			scan.next(); //skip the word obstacle
			environment.addObstacle(
					new Obstacle(
							Double.parseDouble(scan.next()),
							Double.parseDouble(scan.next()),
							Double.parseDouble(scan.next()),
							Double.parseDouble(scan.next())));
		}
		scan.close();
	}

	private void loadTeams(Environment environment) {
		String teams = writeToSocket("teams");
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

}
