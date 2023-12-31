package isaiah.maze_website.models;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

/**
 * Maze model. Contains maze generation methods, maze information, and solution
 * checker.
 * 
 * @author Isaiah
 *
 */
public class Maze {

	private static final int DEFAULT_DIMENSIONS = 9;
	private static final int MIN_DIMENSIONS = 5;
	private static final int MAX_DIMENSIONS = 20;

	/**
	 * Max random value used in maze generation. (4 directions)
	 */
	private static final int MAX_RAND = 4;

	/**
	 * Value for west wall section. Can vary based on how displayed.
	 */
	private static final int WEST_WALL_SECTION = 1;

	/**
	 * Value for east wall section. Can vary based on how displayed.
	 */
	private static final int EAST_WALL_SECTION = 2;

	/**
	 * Value for north wall section. Can vary based on how displayed.
	 */
	private static final int NORTH_WALL_SECTION = 3;

	/**
	 * Value for south wall section. Can vary based on how displayed.
	 */
	private static final int SOUTH_WALL_SECTION = 4;

	private static final int DIR1 = 1;
	private static final int DIR2 = 2;
	private static final int DIR3 = 3;
	private static final int DIR4 = 4;

	/**
	 * Value representing a target position in maze.
	 */
	private static final int TARGET_POSITION = 4;

	/**
	 * Value representing a selected position in maze.
	 */
	private static final int SELECTED_POSITION = 3;

	/**
	 * Value representing an unvisited position in maze.
	 */
	private static final int UNVISITED_POSITION = 2;

	/**
	 * Value representing a wall in maze.
	 */
	private static final int WALL = 1;

	/**
	 * Value representing an empty position in maze.
	 */
	private static final int EMPTY = 0;

	/**
	 * Born section of rulestring for cellular automata generation.
	 */
	private static final List<Integer> BORN = Arrays.asList(3);

	/**
	 * Survive section of rulestring for cellular automata generation.
	 */
	private static final List<Integer> SURVIVE = Arrays.asList(1, 2, 3, 4, 5);

	/**
	 * Variable for specified dimensions.
	 */
	private int dimensions;

	/**
	 * Stores maze representation.
	 */
	private int[][] values;

	/**
	 * Stores steps in maze generation.
	 */
	private List<int[][]> steps;

	private Random r = new Random();

	public Maze() {
		dimensions = DEFAULT_DIMENSIONS;
		setValues(new int[dimensions][dimensions]);
		steps = new ArrayList<int[][]>();
	}

	/**
	 * Maze constructor, which allows specified dimensions. Dimensions limited to
	 * between 5 and 20.
	 * 
	 * @param dimensions dimensions of square maze (5 to 20 inclusive)
	 */
	public Maze(int dimensions) {
		// validate input dimensions
		if (dimensions < MIN_DIMENSIONS || dimensions > MAX_DIMENSIONS) {
			throw new IllegalArgumentException("Dimensions must be between 5 and 20.");
		}
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

	/**
	 * Sets up first step, parameters, and target positions for recursive division
	 * generation.
	 */
	public void recursiveDivisionGenerationStart() {
		// add initial values to steps array
		int[][] valuesCurrent = new int[dimensions][dimensions];
		copyValues(valuesCurrent);
		steps.add(valuesCurrent);

		recursiveDivisionGeneration(0, dimensions - 1, 0, dimensions - 1);

		values[0][0] = TARGET_POSITION;
		values[values.length - 1][values.length - 1] = TARGET_POSITION;
	}

	/**
	 * Generates maze through repeatedly dividing open space.
	 * 
	 * @param xStart starting x value for subsection of maze to generate walls for
	 * @param xEnd   ending x value for subsection of maze to generate walls for
	 * @param yStart starting y value for subsection of maze to generate walls for
	 * @param yEnd   ending y value for subsection of maze to generate walls for
	 */
	private void recursiveDivisionGeneration(final int xStart, final int xEnd, final int yStart, final int yEnd) {

		// wall position, one space buffer for x and y values
		int x = r.nextInt(xEnd - (xStart + 1)) + (xStart + 1);
		int y = r.nextInt(yEnd - (yStart + 1)) + (yStart + 1);
		// wall creation
		for (int i = xStart; i <= xEnd; i++) {
			values[i][y] = WALL;
		}
		for (int i = yStart; i <= yEnd; i++) {
			values[x][i] = WALL;
		}

		// create openings in walls
		int exclude = r.nextInt(MAX_RAND) + 1;

		if (exclude != WEST_WALL_SECTION) {
			int[] opening1 = { x, r.nextInt(y - yStart) + yStart };
			values[opening1[0]][opening1[1]] = EMPTY;
		}
		if (exclude != EAST_WALL_SECTION) {
			int[] opening2 = { x, r.nextInt((yEnd + 1) - (y + 1)) + (y + 1) };
			values[opening2[0]][opening2[1]] = EMPTY;
		}
		if (exclude != NORTH_WALL_SECTION) {
			int[] opening3 = { r.nextInt(x - xStart) + xStart, y };
			values[opening3[0]][opening3[1]] = EMPTY;
		}
		if (exclude != SOUTH_WALL_SECTION) {
			int[] opening4 = { r.nextInt((xEnd + 1) - (x + 1)) + (x + 1), y };
			values[opening4[0]][opening4[1]] = EMPTY;
		}

		// make sure walls don't make unsolvable
		if (xStart > 0 && values[xStart - 1][y] == EMPTY) {
			values[xStart][y] = EMPTY;
		}
		if (xEnd < values.length - 1 && values[xEnd + 1][y] == EMPTY) {
			values[xEnd][y] = EMPTY;
		}
		if (yStart > 0 && values[x][yStart - 1] == EMPTY) {
			values[x][yStart] = EMPTY;
		}
		if (yEnd < values.length - 1 && values[x][yEnd + 1] == EMPTY) {
			values[x][yEnd] = EMPTY;
		}

		int[][] valuesCurrent = new int[dimensions][dimensions];
		copyValues(valuesCurrent);
		steps.add(valuesCurrent);

		// repeat on each new section unless too small (2 or less on either dimension)
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

	/**
	 * Sets up initial values, first step, starting position, and target positions
	 * for DFS generation.
	 */
	public void dfsGenerationStart() {

		/*
		 * set up for dfs generation:
		 * 
		 * 2 - unvisited 1 - wall 0 - visited/empty
		 */

		int nextValue = UNVISITED_POSITION;
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values[0].length; j++) {

				// every other loop gets all walls so unvisited stay surrounded
				if (i % 2 == 0) {
					values[j][i] = nextValue++;
					if (nextValue > 2) {
						nextValue = WALL;
					}
				} else {
					values[j][i] = WALL;
				}

			}

			// start each loop through y at 2 so unvisited stay in line
			nextValue = UNVISITED_POSITION;
		}

		// set starting spot to unvisited
		if (dimensions % 2 == 0) {
			values[dimensions - 1][dimensions - 1] = UNVISITED_POSITION;
			values[dimensions - 2][dimensions - 1] = UNVISITED_POSITION;
		}

		// add initial values to steps array
		int[][] valuesCurrent = new int[dimensions][dimensions];
		copyValues(valuesCurrent);
		steps.add(valuesCurrent);

		int[] startingPosition = { r.nextInt((dimensions + 1) / 2) * 2, r.nextInt((dimensions + 1) / 2) * 2 };
		dfsGeneration(startingPosition);

		values[0][0] = TARGET_POSITION;
		values[values.length - 1][values.length - 1] = TARGET_POSITION;
	}

	/**
	 * Generates maze using DFS by clearing walls between visited positions. Uses
	 * DFS + recursion.
	 * 
	 * @param startPos position on maze to start at
	 */
	private void dfsGeneration(int[] startPos) {

		List<Integer> dirOptions = new ArrayList<Integer>(Arrays.asList(DIR1, DIR2, DIR3, DIR4));

		int[] newPos = null;

		int direction;

		// looks for unvisited spot until no directions are left
		while (dirOptions.size() > 0) {
			// pick a random direction
			direction = dirOptions.get(r.nextInt(dirOptions.size()));

			// reset newPos
			newPos = startPos.clone();

			// find unvisited spot in random direction if exists; then remove from options
			if (direction == DIR1) {
				while (newPos[1] - 1 >= 0 && values[newPos[0]][newPos[1] - 1] != EMPTY
						&& values[newPos[0]][--newPos[1]] != UNVISITED_POSITION) {
					/*
					 * north: decrements newPos[1] additionally: cannot go past end of array or go
					 * back over visited/empty areas
					 */
				}
				// set values from startPos to newPos equal to zero
				if (values[newPos[0]][newPos[1]] == UNVISITED_POSITION) {
					for (int i = startPos[1]; i >= newPos[1]; i--) {
						values[newPos[0]][i] = EMPTY;
					}
					// check not at same spot, then search at new position
					if (newPos[0] != startPos[0] || newPos[1] != startPos[1]) {
						int[][] valuesCurrent = new int[dimensions][dimensions];
						copyValues(valuesCurrent);
						steps.add(valuesCurrent);
						dfsGeneration(newPos);
					}
				}
				// remove direction from options
				for (int i = 0; i < dirOptions.size(); i++) {
					if (dirOptions.get(i) == direction) {
						dirOptions.remove(i);
					}
				}
			} else if (direction == DIR2) {
				while (newPos[0] + 1 < dimensions && values[newPos[0] + 1][newPos[1]] != EMPTY
						&& values[++newPos[0]][newPos[1]] != UNVISITED_POSITION) {
					// east: increments newPos[0]
				}
				// set values from startPos to newPos equal to zero
				if (values[newPos[0]][newPos[1]] == UNVISITED_POSITION) {
					for (int i = startPos[0]; i <= newPos[0]; i++) {
						values[i][newPos[1]] = EMPTY;
					}
					// check not at same spot, then search at new position
					if (newPos[0] != startPos[0] || newPos[1] != startPos[1]) {
						int[][] valuesCurrent = new int[dimensions][dimensions];
						copyValues(valuesCurrent);
						steps.add(valuesCurrent);
						dfsGeneration(newPos);
					}
				}
				// remove direction from options
				for (int i = 0; i < dirOptions.size(); i++) {
					if (dirOptions.get(i) == direction) {
						dirOptions.remove(i);
					}
				}
			} else if (direction == DIR3) {
				while (newPos[1] + 1 < dimensions && values[newPos[0]][newPos[1] + 1] != EMPTY
						&& values[newPos[0]][++newPos[1]] != UNVISITED_POSITION) {
					// south: increments newPos[1]
				}
				// set values from startPos to newPos equal to zero
				if (values[newPos[0]][newPos[1]] == UNVISITED_POSITION) {
					for (int i = startPos[1]; i <= newPos[1]; i++) {
						values[newPos[0]][i] = EMPTY;
					}
					// check not at same spot, then search at new position
					if (newPos[0] != startPos[0] || newPos[1] != startPos[1]) {
						int[][] valuesCurrent = new int[dimensions][dimensions];
						copyValues(valuesCurrent);
						steps.add(valuesCurrent);
						dfsGeneration(newPos);
					}
				}
				// remove direction from options
				for (int i = 0; i < dirOptions.size(); i++) {
					if (dirOptions.get(i) == direction) {
						dirOptions.remove(i);
					}
				}
			} else if (direction == DIR4) {
				while (newPos[0] - 1 >= 0 && values[newPos[0] - 1][newPos[1]] != EMPTY
						&& values[--newPos[0]][newPos[1]] != UNVISITED_POSITION) {
					// west: decrements newPos[0]
				}
				// set values from startPos to newPos equal to zero
				if (values[newPos[0]][newPos[1]] == UNVISITED_POSITION) {
					for (int i = startPos[0]; i >= newPos[0]; i--) {
						values[i][newPos[1]] = EMPTY;
					}
					// check not at same spot, then search at new position
					if (newPos[0] != startPos[0] || newPos[1] != startPos[1]) {
						int[][] valuesCurrent = new int[dimensions][dimensions];
						copyValues(valuesCurrent);
						steps.add(valuesCurrent);
						dfsGeneration(newPos);
					}
				}
				// remove direction from options
				for (int i = 0; i < dirOptions.size(); i++) {
					if (dirOptions.get(i) == direction) {
						dirOptions.remove(i);
					}
				}
			}
		}

	}

	/**
	 * Starting point for automata generation. Sets random-ish initial values,
	 * target positions, and ensures solvable.
	 */
	public void automataGenerationStart() {

		// randomly set each position to one or zero
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values[0].length; j++) {
				values[i][j] = r.nextInt(2);
			}
		}

		// call recursive function
		automataGeneration();

		// set goals to target position
		values[0][0] = TARGET_POSITION;
		values[values.length - 1][values.length - 1] = TARGET_POSITION;

		// update steps - adds automata maze with target positions
		int[][] valuesCurrent = new int[dimensions][dimensions];
		copyValues(valuesCurrent);
		steps.add(valuesCurrent);

		// ensures maze can be solved
		ensureSolvable();

		// update steps - adds completed maze without selected positions
		valuesCurrent = new int[dimensions][dimensions];
		copyValues(valuesCurrent);
		steps.add(valuesCurrent);
	}

	/**
	 * Generates a maze recursively by running a cellular automata with rulestring
	 * B3S12345. Ends on repeated values to cover static values and oscillation.
	 */
	private void automataGeneration() {

		// update steps
		int[][] valuesCurrent = new int[dimensions][dimensions];
		copyValues(valuesCurrent);
		steps.add(valuesCurrent);

		// temporary array to store updated values discretely
		int[][] tempArray = new int[dimensions][dimensions];

		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values[0].length; j++) {
				// check nearest eight cells' state
				int liveCellCount = 0;
				for (int x = -1; x < 2; x++) {
					for (int y = -1; y < 2; y++) {
						// bound edges instead of wrapping
						if (i + x >= 0 && j + y >= 0 && i + x < values.length && j + y < values[0].length
								&& values[i + x][j + y] == 1) {
							liveCellCount++;
						}
					}
				}
				// set live cells, default for remaining is zero
				if (BORN.contains(liveCellCount)) {
					tempArray[i][j] = 1;
				} else if (SURVIVE.contains(liveCellCount) && values[i][j] == 1) {
					tempArray[i][j] = 1;
				}
			}
		}

		// update values
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values[0].length; j++) {
				values[i][j] = tempArray[i][j];
			}
		}

		// base case: steps contains current maze
		if (!stepsContains(values)) {
			automataGeneration();
		}
	}

	// TODO: javadoc & generate
	public void binaryTreeGeneration() {

		// TODO: north or west for each position in grid

	}

	/**
	 * Checks whether attempt is correct or not. Uses DFS + Stack.
	 *
	 * @param attempt int array with information about solution.
	 * @return returns true if correct
	 */
	public boolean checkSolution(int[][] attempt) {
		Stack<int[]> dfsStack = new Stack<int[]>();
		int[] start = { 0, 0 };
		dfsStack.push(start);

		while (!dfsStack.isEmpty()) {
			int[] currentPos = dfsStack.pop();
			if (attempt[currentPos[0]][currentPos[1]] == TARGET_POSITION
					&& (currentPos[0] != start[0] || currentPos[1] != start[1])) {
				return true;
			}
			// mark position as visited
			attempt[currentPos[0]][currentPos[1]] = -1;

			if (currentPos[0] - 1 >= 0 && (attempt[currentPos[0] - 1][currentPos[1]] == SELECTED_POSITION
					|| attempt[currentPos[0] - 1][currentPos[1]] == TARGET_POSITION)) {
				int[] newPos = { currentPos[0] - 1, currentPos[1] };
				dfsStack.push(newPos);
			}

			if (currentPos[1] - 1 >= 0 && (attempt[currentPos[0]][currentPos[1] - 1] == SELECTED_POSITION
					|| attempt[currentPos[0]][currentPos[1] - 1] == TARGET_POSITION)) {
				int[] newPos = { currentPos[0], currentPos[1] - 1 };
				dfsStack.push(newPos);
			}

			if (currentPos[0] + 1 < dimensions && (attempt[currentPos[0] + 1][currentPos[1]] == SELECTED_POSITION
					|| attempt[currentPos[0] + 1][currentPos[1]] == TARGET_POSITION)) {
				int[] newPos = { currentPos[0] + 1, currentPos[1] };
				dfsStack.push(newPos);
			}

			if (currentPos[1] + 1 < dimensions && (attempt[currentPos[0]][currentPos[1] + 1] == SELECTED_POSITION
					|| attempt[currentPos[0]][currentPos[1] + 1] == TARGET_POSITION)) {
				int[] newPos = { currentPos[0], currentPos[1] + 1 };
				dfsStack.push(newPos);
			}
		}

		return false;
	}

	public static void main(String[] args) {

		// testing area

	}

	/**
	 * Helper function used when adding maze steps. Copies maze values to dest
	 * array.
	 * 
	 * @param dest destination array
	 */
	private void copyValues(int[][] dest) {
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values[0].length; j++) {
				dest[j][i] = values[j][i];
			}
		}
	}

	/**
	 * Helper function used to check if steps contains a set of values.
	 * 
	 * @param val values potentially contained in steps.
	 * @return t/f values exist in steps
	 */
	private boolean stepsContains(int[][] val) {
		boolean output = false;
		for (int x = 0; x < steps.size(); x++) {
			if (Arrays.deepToString(steps.get(x)).equals(Arrays.deepToString(val))) {
				output = true;
				break;
			}
		}
		return output;
	}

	/**
	 * Helper method that ensures maze is solvable. Randomly connects two sections
	 * of maze until solvable.
	 */
	private void ensureSolvable() {

		Map<Integer, int[]> hmSelected = new HashMap<Integer, int[]>();
		Map<Integer, int[]> hmTarget = new HashMap<Integer, int[]>();

		// set empty positions to selected for check solution method; also add selected
		// and target positions to separate hash maps
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values.length; j++) {
				if (values[i][j] == EMPTY) {
					values[i][j] = SELECTED_POSITION;
					int[] hmValue = { i, j };
					hmSelected.put(hmSelected.size(), hmValue);
				} else if (values[i][j] == TARGET_POSITION) {
					int[] hmValue = { i, j };
					hmTarget.put(hmTarget.size(), hmValue);
				}
			}
		}

		// temporary values array for checkSolution() to modify
		int[][] valuesTemp = new int[dimensions][dimensions];
		copyValues(valuesTemp);

		// repeat until solvable
		while (!checkSolution(valuesTemp)) {
			// choose random selected position in hash map
			int hmKeyRandom = r.nextInt(hmSelected.size());
			int[] pos = hmSelected.get(hmKeyRandom);
			// choose random target position in hash map
			hmKeyRandom = r.nextInt(hmTarget.size());
			int[] pos2 = hmTarget.get(hmKeyRandom);

			// sets right or left for each axis based on positions
			int dir[] = { pos[0] - pos2[0] != 0 ? (pos[0] - pos2[0]) / Math.abs(pos[0] - pos2[0]) : 0,
					pos[1] - pos2[1] != 0 ? (pos[1] - pos2[1]) / Math.abs(pos[1] - pos2[1]) : 0 };

			// clear walls between positions
			while (pos[0] != pos2[0] && pos[1] != pos2[1]) {
				if (Math.abs(pos[0] - pos2[0]) > Math.abs(pos[1] - pos2[1])) {
					pos2[0] += dir[0];
				} else {
					pos2[1] += dir[1];
				}
				values[pos2[0]][pos2[1]] = SELECTED_POSITION;
			}

			// reset temp values
			copyValues(valuesTemp);

			// add values to steps
			int[][] valuesCurrent = new int[dimensions][dimensions];
			copyValues(valuesCurrent);
			steps.add(valuesCurrent);
		}

		// set selected positions back to empty
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values.length; j++) {
				if (values[i][j] == SELECTED_POSITION) {
					values[i][j] = EMPTY;
				}
			}
		}
	}

}
