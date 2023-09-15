package isaiah.maze_website.unit.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import isaiah.maze_website.models.Maze;

/**
 * Unit tests for maze model. Database must be running and connected for tests
 * to run.
 * 
 * @author Isaiah
 */
@SpringBootTest
public class MazeModelTest {

	private static final int DEFAULT_DIMENSIONS = 9;
	private static final int MIN_DIMENSIONS = 5;
	private static final int MAX_DIMENSIONS = 20;

	/** Value for selected maze position. */
	private static final int SELECTED_POSITION = 3;

	@Test
	public void testConstructor() {
		// default test
		Maze maze = new Maze();
		assertEquals(maze.getValues().length, DEFAULT_DIMENSIONS);
		// boundary tests
		maze = new Maze(MIN_DIMENSIONS);
		assertEquals(maze.getValues().length, MIN_DIMENSIONS);
		maze = new Maze(MAX_DIMENSIONS);
		assertEquals(maze.getValues().length, MAX_DIMENSIONS);
		assertThrows(IllegalArgumentException.class, () -> new Maze(MIN_DIMENSIONS - 1));
		assertThrows(IllegalArgumentException.class, () -> new Maze(MAX_DIMENSIONS + 1));
	}

	@Test
	public void testGeneration() {
		Maze maze = new Maze(MAX_DIMENSIONS);
		maze.dfsGenerationStart();
		assertFalse(maze.checkSolution(maze.getValues()));
		// set empty positions to selected
		for (int i = 0; i < maze.getValues().length; i++) {
			for (int j = 0; j < maze.getValues().length; j++) {
				if (maze.getValues()[i][j] == 0) {
					maze.getValues()[i][j] = SELECTED_POSITION;
				}
			}
		}
		assertTrue(maze.checkSolution(maze.getValues()));

		maze = new Maze(MIN_DIMENSIONS);
		maze.recursiveDivisionGenerationStart();
		assertFalse(maze.checkSolution(maze.getValues()));
		for (int i = 0; i < maze.getValues().length; i++) {
			for (int j = 0; j < maze.getValues().length; j++) {
				if (maze.getValues()[i][j] == 0) {
					maze.getValues()[i][j] = SELECTED_POSITION;
				}
			}
		}
		assertTrue(maze.checkSolution(maze.getValues()));
	}

}
