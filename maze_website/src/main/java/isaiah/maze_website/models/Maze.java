package isaiah.maze_website.models;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.annotation.PostConstruct;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import org.json.*;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Maze {
	
	private static final int DEFAULT_DIMENSIONS = 9;
	
	private int dimensions;
	
	private int[][] values;
	
	private List<int[][]> steps;
	
	private Random r = new Random();
	
	public Maze() {
		dimensions = DEFAULT_DIMENSIONS;
		setValues(new int[dimensions][dimensions]);
		steps = new ArrayList<int[][]>();
	}
	
	public Maze(int dimensions) {
		this.dimensions = dimensions;
		setValues(new int[dimensions][dimensions]);
		steps = new ArrayList<int[][]>();
	}

	public int[][] getValues() {
		return values;
	}
	
	public void setValues(int[][] values) {
		this.values = values;
	}
	
	public List<int[][]> getSteps() {
		return steps;
	}
	
	public void recursiveDivisionGenerationStart() {
		//add initial values to steps array
		int[][] valuesCurrent = new int[dimensions][dimensions];
		copyValues(valuesCurrent);
		steps.add(valuesCurrent);
		
		recursiveDivisionGeneration(0, dimensions - 1, 0, dimensions - 1);
		
		values[0][0] = 4;
		values[values.length - 1][values.length - 1] = 4;
	}
	
	public void recursiveDivisionGeneration(int xStart, int xEnd, int yStart, int yEnd) {
		
		//wall position and creation, one space buffer for x and y values
		int x = r.nextInt(xEnd - (xStart + 1)) + (xStart + 1);
		int y = r.nextInt(yEnd - (yStart + 1)) + (yStart + 1);
		for (int i = xStart; i <= xEnd; i++) {
			values[i][y] = 1;
		}
		for (int i = yStart; i <= yEnd; i++) {
			values[x][i] = 1;
		}
		
		//create openings in walls
		int exclude = r.nextInt(4) + 1;

		if (exclude != 1) {
			int[] opening1 = {x, r.nextInt(y - yStart) + yStart};
			values[opening1[0]][opening1[1]] = 0;
		}
		if (exclude != 2) {
			int[] opening2 = {x, r.nextInt((yEnd + 1) - (y + 1)) + (y + 1)};
			values[opening2[0]][opening2[1]] = 0;
		}
		if (exclude != 3) {
			int[] opening3 = {r.nextInt(x - xStart) + xStart, y};
			values[opening3[0]][opening3[1]] = 0;
		}
		if (exclude != 4) {
			int[] opening4 = {r.nextInt((xEnd + 1) - (x + 1)) + (x + 1), y};
			values[opening4[0]][opening4[1]] = 0;
		}
		
		//make sure walls don't make unsolvable
		if (xStart > 0 && values[xStart - 1][y] == 0) {
			values[xStart][y] = 0;
		}
		if (xEnd < values.length - 1 && values[xEnd + 1][y] == 0) {
			values[xEnd][y] = 0;
		}
		if (yStart > 0 && values[x][yStart - 1] == 0) {
			values[x][yStart] = 0;
		}
		if (yEnd < values.length - 1 && values[x][yEnd + 1] == 0) {
			values[x][yEnd] = 0;
		}
		
		int[][] valuesCurrent = new int[dimensions][dimensions];
		copyValues(valuesCurrent);
		steps.add(valuesCurrent);
		//repeat on each new section unless too small (2 or less on either dimension)
		if (!(x - xStart <= 2 || y - yStart <= 2)) {
			recursiveDivisionGeneration(xStart, x - 1, yStart, y - 1);
		}
		if (!(xEnd - x <= 2 || y - yStart <= 2)) {
			recursiveDivisionGeneration(x + 1, xEnd, yStart, y - 1);
		}
		if (!(x - xStart <= 2 || yEnd - y <= 2)) {
			recursiveDivisionGeneration(xStart, x - 1, y + 1, yEnd);
		}
		if (!(xEnd - x <= 2 || yEnd - y <= 2)) {
			recursiveDivisionGeneration(x + 1, xEnd, y + 1, yEnd);
		}
		
	}
	
	public void dfsGenerationStart() {
		
		/*
		 * set up for dfs generation:
		 * 
		 * 2 - unvisited
		 * 1 - wall
		 * 0 - visited/empty
		 */
		
		int nextValue = 2;
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values[0].length; j++) {
				
				//every other loop gets all walls so unvisited stay surrounded
				if (i % 2 == 0) {
					values[j][i] = nextValue++;
					if (nextValue > 2) {
						nextValue = 1;
					}
				} else {
					values[j][i] = 1;
				}
				
			}
			
			//start each loop through y at 2 so unvisited stay in line
			nextValue = 2;
		}
		
		//set starting spot to unvisited
		if (dimensions % 2 == 0) {
			values[dimensions - 1][dimensions - 1] = 2;
			values[dimensions - 2][dimensions - 1] = 2;
		}
		
		//add initial values to steps array
		int[][] valuesCurrent = new int[dimensions][dimensions];
		copyValues(valuesCurrent);
		steps.add(valuesCurrent);
		
		int[] startingPosition = {r.nextInt((dimensions + 1) / 2) * 2, r.nextInt((dimensions + 1) / 2) * 2};
		dfsGeneration(startingPosition);
		
		values[0][0] = 4;
		values[values.length - 1][values.length - 1] = 4;
	}
	
	public void dfsGeneration(int[] startPos) {
		
		List<Integer> dirOptions = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4));
		
		int[] newPos = null;
		
		int direction;
		
		//continue looking for unvisited spot until no directions are left
		while (dirOptions.size() > 0) {
			//pick a random direction
			direction = dirOptions.get(r.nextInt(dirOptions.size()));
			
			//reset newPos
			newPos = startPos.clone();
			
			//find unvisited spot in random direction if exists; then remove from options
			if (direction == 1) {
				while (newPos[1] - 1 >= 0 
						&& values[newPos[0]][newPos[1] - 1] != 0
						&& values[newPos[0]][--newPos[1]] != 2) {
					/*
					 * north: decrements newPos[1]
					 * additionally: cannot go past end of array or
					 * go back over visited/empty areas
					 */
				}
				//set values from startPos to newPos equal to zero
				if (values[newPos[0]][newPos[1]] == 2) {
					for (int i = startPos[1]; i >= newPos[1]; i--) {
						values[newPos[0]][i] = 0;
					}
					//check not at same spot, then search at new position
					if (newPos[0] != startPos[0] || newPos[1] != startPos[1]) {
						int[][] valuesCurrent = new int[dimensions][dimensions];
						copyValues(valuesCurrent);
						steps.add(valuesCurrent);
						dfsGeneration(newPos);
					}
				}
				//remove direction from options
				for(int i = 0; i < dirOptions.size(); i++) {
					if (dirOptions.get(i) == direction) {
						dirOptions.remove(i);
					}
				}
			} else if (direction == 2) {
				while (newPos[0] + 1 < dimensions 
						&& values[newPos[0] + 1][newPos[1]] != 0
						&& values[++newPos[0]][newPos[1]] != 2) {
					//east: increments newPos[0]
				}
				//set values from startPos to newPos equal to zero
				if (values[newPos[0]][newPos[1]] == 2) {
					for (int i = startPos[0]; i <= newPos[0]; i++) {
						values[i][newPos[1]] = 0;
					}
					//check not at same spot, then search at new position
					if (newPos[0] != startPos[0] || newPos[1] != startPos[1]) {
						int[][] valuesCurrent = new int[dimensions][dimensions];
						copyValues(valuesCurrent);
						steps.add(valuesCurrent);
						dfsGeneration(newPos);
					}
				}
				//remove direction from options
				for(int i = 0; i < dirOptions.size(); i++) {
					if (dirOptions.get(i) == direction) {
						dirOptions.remove(i);
					}
				}
			} else if (direction == 3) {
				while (newPos[1] + 1 < dimensions 
						&& values[newPos[0]][newPos[1] + 1] != 0
						&& values[newPos[0]][++newPos[1]] != 2) {
					//south: increments newPos[1]
				}
				//set values from startPos to newPos equal to zero
				if (values[newPos[0]][newPos[1]] == 2) {
					for (int i = startPos[1]; i <= newPos[1]; i++) {
						values[newPos[0]][i] = 0;
					}
					//check not at same spot, then search at new position
					if (newPos[0] != startPos[0] || newPos[1] != startPos[1]) {
						int[][] valuesCurrent = new int[dimensions][dimensions];
						copyValues(valuesCurrent);
						steps.add(valuesCurrent);
						dfsGeneration(newPos);
					}
				}
				//remove direction from options
				for(int i = 0; i < dirOptions.size(); i++) {
					if (dirOptions.get(i) == direction) {
						dirOptions.remove(i);
					}
				}
			} else if (direction == 4) {
				while (newPos[0] - 1 >= 0 
						&& values[newPos[0] - 1][newPos[1]] != 0
						&& values[--newPos[0]][newPos[1]] != 2) {
					//west: decrements newPos[0]
				}
				//set values from startPos to newPos equal to zero
				if (values[newPos[0]][newPos[1]] == 2) {
					for (int i = startPos[0]; i >= newPos[0]; i--) {
						values[i][newPos[1]] = 0;
					}
					//check not at same spot, then search at new position
					if (newPos[0] != startPos[0] || newPos[1] != startPos[1]) {
						int[][] valuesCurrent = new int[dimensions][dimensions];
						copyValues(valuesCurrent);
						steps.add(valuesCurrent);
						dfsGeneration(newPos);
					}
				}
				//remove direction from options
				for(int i = 0; i < dirOptions.size(); i++) {
					if (dirOptions.get(i) == direction) {
						dirOptions.remove(i);
					}
				}
			}
		}
		
	}
	
	public void automataGeneration() {
		
		//maybe add this
		
	}
	
	/**
	 * Checks whether inputed attempt is correct or not.
	 * @param attempt Int array with information about solution.
	 * @return returns true if correct
	 */
	public boolean checkSolution(int[][] attempt) {
		Stack<int[]> dfsStack = new Stack<int[]>();
		int[] start = {0, 0};
		dfsStack.push(start);
		
		while(!dfsStack.isEmpty()) {
			int[] currentPos = dfsStack.pop();
			if (attempt[currentPos[0]][currentPos[1]] == 4 && (currentPos[0] != start[0] || 
					currentPos[1] != start[1])) {
				return true;
			}
			attempt[currentPos[0]][currentPos[1]] = -1;
			
			if (currentPos[0] - 1 >= 0 && 
					(attempt[currentPos[0] - 1][currentPos[1]] == 3 || 
					attempt[currentPos[0] - 1][currentPos[1]] == 4)) {
				int[] newPos = {currentPos[0] - 1, currentPos[1]};
				dfsStack.push(newPos);
			}
			
			if (currentPos[1] - 1 >= 0 && 
					(attempt[currentPos[0]][currentPos[1] - 1] == 3 || 
					attempt[currentPos[0]][currentPos[1] - 1] == 4)) {
				int[] newPos = {currentPos[0], currentPos[1] - 1};
				dfsStack.push(newPos);
			}
			
			if (currentPos[0] + 1 < dimensions && 
					(attempt[currentPos[0] + 1][currentPos[1]] == 3 || 
					attempt[currentPos[0] + 1][currentPos[1]] == 4)) {
				int[] newPos = {currentPos[0] + 1, currentPos[1]};
				dfsStack.push(newPos);
			}
			
			if (currentPos[1] + 1 < dimensions && 
					(attempt[currentPos[0]][currentPos[1] + 1] == 3 || 
					attempt[currentPos[0]][currentPos[1] + 1] == 4)) {
				int[] newPos = {currentPos[0], currentPos[1] + 1};
				dfsStack.push(newPos);
			}
		}
		
		return false;
	}

	public static void main(String[] args) {
		
		//testing area

	}
	
	/**
	 * TODO: purpose instead of what it does, eg used for duplicating array when copying to steps
	 * Helper function that copies values to destination array.
	 * @param dest destination array with type 2d int
	 */
	private void copyValues(int[][] dest) {
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values[0].length; j++) {
				dest[j][i] = values[j][i];
			}
		}
	}

}
