package agent;

import agent.gridvisualization.GridVisualizationThread;
import environment.Action;
import environment.AttemptedAction;
import environment.Environment;
import environment.Obstacle;

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
    private static double BEL_THRESH = 0.7;
    private static int WAIT_CYCLE = 10;
	private GridVisualizationThread gridVisualizationThread;
	private int cycleCount;

    public GridFilterControllerAgent(int tankIndex) {
        super(tankIndex);
	    gridVisualizationThread = new GridVisualizationThread();
	    gridVisualizationThread.start();
	    cycleCount = 0;
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
			Stack<NewRect> partail_rect = new Stack<NewRect>();
			
			for (int i = 0; i<cache.length; i++)
				cache[i] = 0;
			for (int x = grid.length -1; x >= 0; x--) {
				update_cache(blackWhiteGrid,cache, x);
				int width = 0;
				for (int y = 0; y < grid.length; y++) {
					if (cache[y] > width) { //open rectangle?
						partail_rect.push(new NewRect(y, width));
						width = cache[y];
					}
					if (cache[y] < width) { //close rectangle?
						NewRect popped;
						do {
		                    if( partail_rect.size() == 0 ) {
		                    	popped = new NewRect(y,width);
		                        break;
		                    }
							popped = partail_rect.pop();
							Rect newRect = new Rect(x,popped.y,x+width-1,y-1);
		                    if( newRect.area() < RECTANGLE_THRESHOLD || newRect.urx - newRect.llx == 1 || newRect.ury - newRect.lly == 1 )
		                        break;
							if (noneFound) {
								bestFound.push(newRect);
								noneFound = false;
							}
							else {
		    					Rect lastRect = bestFound.peek();
		//	    					if (lastRect.interesect(newRect)) { //does this take any cells used by the last rectangle we found?
		    						if (lastRect.area() < newRect.area()) {
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
//						if (width != 0) {
//							partail_rect.push(new NewRect(popped.y,width));//re-push
//						}
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
    	
    }
    
    private void clearFoundRectangle(boolean[][] blackWhiteGrid, Rect rect) {
		for (int x = rect.llx; x < rect.urx+1; x++)
			for (int y = rect.lly; y < rect.ury+1; y++)
				blackWhiteGrid[x][y] = false;
		
	}

	private boolean[][] getBlackWhiteGrid(){
    	boolean[][] toReturn = new boolean[grid.length][grid.length];
    	for (int x = 0; x < grid.length; x++)
    		for (int y = 0; y < grid.length; y++) {
    			if (grid[x][y] > BEL_THRESH) {
    				toReturn[x][y] = true;
    			}
    			else {
    				double sum = 0;
    				for (int i = Math.max(x-5,0); i < Math.min(x+6,grid.length); i++) {
    					sum += grid[i][y];
    				}
    				toReturn[x][y] = .6 < (sum/11);
    			}
    		}
    	
    	
//    	for (int x = 0; x < 400; x++) {
//    		for (int y = 0; y < 400; y++) {
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
