package agent;

import agent.gridvisualization.GridVisualizationThread;
import environment.Action;
import environment.AttemptedAction;
import environment.Environment;
import environment.Obstacle;
import environment.Flag;

import java.awt.geom.Point2D;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jbdennin
 * Date: 11/15/13
 * Time: 9:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class GridFilterControllerAgent extends GridFilterAgent {

    private static final int RECTANGLE_THRESHOLD = 15;
    protected Set<Rect> obstacles = new HashSet<Rect>();
    private static double BEL_THRESH = 0.75;
    private static int WAIT_CYCLE = 10;
    private static int BLIND_REACH = 90;
	private GridVisualizationThread gridVisualizationThread;
	private int cycleCount;

    public GridFilterControllerAgent(int tankIndex, GridVisualizationThread gridVisualizationThread) {
        super(tankIndex);
	    this.gridVisualizationThread = gridVisualizationThread;
	    cycleCount = 0;
	    obstaclesF = new ArrayList<Obstacle>();
	    toExplore = new ArrayList<Flag>();
    }

    @Override
    public List<Action> getActions(Environment environment) {
        if( grid == null ) {
            int size = Integer.parseInt(environment.getConstant("worldsize"));
            grid = new double[size][size];
            for (int x = 0; x < size; x++)
            {
            	for (int y = 0; y < size; y++)
                    grid[x][y]=.5;
            }
            truePositive = Double.parseDouble(environment.getConstant("truepositive"));
            trueNegative = Double.parseDouble(environment.getConstant("truenegative"));
        }
        return super.getActions(environment);
    }

    @Override
    public void processAttemptedActions(List<AttemptedAction> attemptedActions) {
        
        if (cycleCount == WAIT_CYCLE) {
        	analyzeForObstacles();
        	cycleCount = 0;
        }
        else {
        	cycleCount++;
        }
        updateVisualization();
    }

    private void analyzeForObstacles() {
        //TODO Brian
    	//This may need to run through twice to catch leftover rectangles
    	
    	//find largest rectangle, algorithm from http://www.drdobbs.com/database/the-maximal-rectangle-problem/184410529
    	//extended to find all rectangles
    	
    	
    	Stack<Rect> bestFound = new Stack<Rect>();//This is a stack as the algorithm moves along x linearly
    	boolean[][] blackWhiteGrid = getBlackWhiteGrid();
    	int[] cache = new int[grid.length];
    	
    	while (true) {
			boolean noneFound = true;
			
			for (int i = 0; i<cache.length; i++)
				cache[i] = 0;
			for (int llx = grid.length -1; llx >= 0; llx--) {
				update_cache(blackWhiteGrid,cache, llx);
				for (int lly = 0; lly < grid.length; lly++) {
					Rect bestOfRound = new Rect(llx,lly,llx,lly);
					int y = lly;
					int x_max = 9999;
					int x = llx;
					while (y+1<grid.length && blackWhiteGrid[llx][y]) {
						y++;
						x = Math.min(llx+cache[y]-1, x_max);
						x_max = x;
						Rect tryRect = new Rect(llx,lly-1,x,y);
						if (tryRect.area() > bestOfRound.area()) {
							bestOfRound = tryRect;
						}
					}
					if (bestOfRound.area() > 40) {
						if (noneFound) {
							bestFound.push(bestOfRound);
							noneFound = false;
						}
						else {
							Rect lastRect = bestFound.peek();
							if (lastRect.area() < bestOfRound.area()) {
								bestFound.pop();
								bestFound.push(bestOfRound);
							}
						}
					}
				}
			}
			if (noneFound)
				break;
			
			clearFoundRectangle(blackWhiteGrid, bestFound.peek());
    	}
    	
    	//add found rectanlges
    	obstacles.clear();
    	obstacles.addAll(bestFound);
    	System.out.println("Sweep done:");
    	for (Rect r : bestFound){
    		System.out.println("Rect: llx=" + (r.llx-400) + "\tlly=" + (r.lly-400) + "\turx=" + (r.urx-400) + "\tury=" + (r.ury-400));
    		List<Point2D.Double> corners = new ArrayList<Point2D.Double>();
    		corners.add(new Point2D.Double((r.llx-400),(r.lly-400)));
    		corners.add(new Point2D.Double((r.llx-400),(r.ury-400)));
    		corners.add(new Point2D.Double((r.urx-400),(r.lly-400)));
    		corners.add(new Point2D.Double((r.urx-400),(r.ury-400)));
    		obstaclesF.add(new Obstacle(corners));
    	}
    	
    	toExplore.clear();
    	int i = 0;
    	Random rand = new Random();
    	while (toExplore.size() < 10 && i < 1000) {
    		int x = rand.nextInt(grid.length);
    		int y = rand.nextInt(grid.length);
    		if (grid[x][y] == .5) {
    			toExplore.add(new Flag("blue",x,y));
    		}
    	}
    }
    /*
     * private void analyzeForObstacles() {
        //TODO Brian
    	//This may need to run through twice to catch leftover rectangles
    	
    	//find largest rectangle, algorithm from http://www.drdobbs.com/database/the-maximal-rectangle-problem/184410529
    	//extended to find all rectangles
    	
    	
    	Stack<Rect> bestFound = new Stack<Rect>();//This is a stack as the algorithm moves along x linearly
    	boolean[][] blackWhiteGrid = getBlackWhiteGrid();
    	int[] cache = new int[grid.length];
    	
    	while (true) {
			boolean noneFound = true;
			Stack<NewRect> partail_rect = new Stack<NewRect>();
			
			for (int i = 0; i<cache.length; i++)
				cache[i] = 0;
			for (int x = grid.length -1; x >= 0; x--) {
				update_cache(blackWhiteGrid,cache, x);
				int width = 0;
				for (int y = 0; y < grid.length; y++) {
					if (cache[y] > width) { //open rectangle?
						width = cache[y];
						partail_rect.push(new NewRect(y, width));
						
					}
					if (cache[y] < width) { //close rectangle?
						NewRect popped;
						do {
		                    if( partail_rect.size() == 0 ) {
		                    	popped = new NewRect(y,0);
		                        break;
		                    }
							popped = partail_rect.pop();
							Rect newRect = new Rect(x,popped.y,x+width,y-1);
							
		                    if( newRect.area() < RECTANGLE_THRESHOLD || newRect.urx - newRect.llx == 1 || newRect.ury - newRect.lly == 1 )
		                        break;
							if (noneFound) {
								if ((y-1)-popped.y > 200 || newRect.area() > 300) {
									int b = 0;
								}
								bestFound.push(newRect);
								noneFound = false;
							}
							else {
		    					Rect lastRect = bestFound.peek();
		//	    					if (lastRect.interesect(newRect)) { //does this take any cells used by the last rectangle we found?
		    						if (lastRect.area() < newRect.area()) {
		    							if ((y-1)-popped.y > 200) {
											int b = 0;
										}
		    							bestFound.pop();
		    							bestFound.push(newRect);
		    						}
		//	    					}
		//	    					else {
		//	    						bestFound.push(newRect);//we can always push
		//	    					}
							}
							width = popped.width;
						} while (cache[y] < width);
						
						width = cache[y];
						if (width != 0) {
							partail_rect.push(new NewRect(popped.y,width));//re-push, should not be last popped.y, but the one before that
						}
					}
				}
				while (!partail_rect.empty())//??
				{
					partail_rect.pop();
				}
			}
			if (noneFound)
				break;
			
			clearFoundRectangle(blackWhiteGrid, bestFound.peek());
    	}
    	
    	//add found rectanlges
    	obstacles.clear();
    	obstacles.addAll(bestFound);
    	
    }
*/
    @Override
    public long getNextStateChange() {
        return System.currentTimeMillis();
    }

     
    
    private void clearFoundRectangle(boolean[][] blackWhiteGrid, Rect rect) {
		for (int x = rect.llx; x < rect.urx+1; x++)
			for (int y = rect.lly; y < rect.ury+1; y++)
				if (x > -1 && y > -1)
				blackWhiteGrid[x][y] = false;
		
	}

	private boolean[][] getBlackWhiteGrid(){
    	boolean[][] toReturn = new boolean[grid.length][grid.length];
    	for (int x = 0; x < grid.length; x++) {
    		for (int y = 0; y < grid.length; y++) {
    			if (grid[x][y] > BEL_THRESH) {
    				toReturn[x][y] = true;
    			}
    			else {
    				double sumX = 0;
    				double sumY = 0;
    				for (int xx = Math.max(x-5,0); xx < Math.min(x+6,grid.length); xx++) {
    					sumX += grid[xx][y]>BEL_THRESH?1:0;
    				}
    				for (int j = Math.max(y-5,0); j < Math.min(y+6,grid.length); j++) {
						sumY += grid[x][j]>BEL_THRESH?1:0;
    				}
    				toReturn[x][y] = BEL_THRESH < (sumX/2) && BEL_THRESH < (sumY/2);
    				
    				//We assume if we are surrounded by black, then we are black (if unexplored)
    				if (grid[x][y] == .5 && !toReturn[x][y]) {
    					for (int i = 1; i < Math.min(BLIND_REACH,grid.length-(x+1)); i++) {
    						if (grid[x+i][y] > BEL_THRESH || toReturn[x+i][y])
    							for (int i2 = 1; i2< Math.min(BLIND_REACH,1+x); i2++) {
    								if (grid[x-i2][y] > BEL_THRESH || toReturn[x-i2][y])
    									for (int i3 = 1; i3< Math.min(BLIND_REACH,grid.length-(y+1)); i3++) {
    										if (grid[x][y+i3] > BEL_THRESH || toReturn[x][y+i3])
    											for (int i4 = 1; i4< Math.min(BLIND_REACH,1+y); i4++) {
    												if (grid[x][y-i4] > BEL_THRESH || toReturn[x][y-i4]) {
    													toReturn[x][y] = true;
    													i = BLIND_REACH;
    	    	    	    							i2 = BLIND_REACH;
    	    	    	    							i3 = BLIND_REACH;
    	    	    	    							i4 = BLIND_REACH;
    												}
    												else if (grid[x][y-i4] != .5) {
    													i = BLIND_REACH;
    	    	    	    							i2 = BLIND_REACH;
    	    	    	    							i3 = BLIND_REACH;
    	    	    	    							i4 = BLIND_REACH;
    												}
    											}
    										else if (grid[x][y+i3] != .5) {
    	    	    							i = BLIND_REACH;
    	    	    							i2 = BLIND_REACH;
    	    	    							i3 = BLIND_REACH;
    										}
    									}
    								else if (grid[x-i2][y] != .5) {
    	    							i = BLIND_REACH;
    	    							i2 = BLIND_REACH;
    								}
    							}
    							
    						else if (grid[x+i][y] != .5) {
    							i = BLIND_REACH;
    						}
    					}
    				}
    			}
    		}
    	}
//    	for (int y = 400-70; y < 400+70; y++){
//    		 for (int x = 400-20; x < 20+400; x++) {
//    			System.out.print(toReturn[x][y]?"1":"0");
//    		}
//    		System.out.println();
//    	}
    	
    	return toReturn;
    }

    private void update_cache(boolean[][] grid, int[] cache, int x) {
		for (int y = 0; y < grid.length; y++) {
			if (grid[x][y]) {
				cache[y] += 1;
			}
			else {
				cache[y] = 0;
			}
		}
	}

	private void updateVisualization() {
        gridVisualizationThread.update(grid, obstacles);
    }
    
}
