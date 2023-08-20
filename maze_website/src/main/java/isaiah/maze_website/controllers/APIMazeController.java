package isaiah.maze_website.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import isaiah.maze_website.models.Maze;

@RestController
public class APIMazeController {

	/**
	 * Generates and returns maze based on requested type.
	 * 
	 * @param generationType specifies type of generation for maze
	 * @return 2d int array representing maze
	 */
	@GetMapping("/mazeinfo/defaultFinal/{generationType}")
	public ResponseEntity<int[][]> defaultGenerateMazeFinal(@PathVariable("generationType") String generationType) {
		Maze maze = new Maze();
		if ("dfs".equals(generationType)) {
			maze.dfsGenerationStart();
		}
		if ("recursive division".equals(generationType)) {
			maze.recursiveDivisionGenerationStart();
		}
		int[][] generatedMaze = maze.getValues();
		return new ResponseEntity<>(generatedMaze, HttpStatus.OK);
	}

	/**
	 * Generates and returns maze based on requested dimensions and type.
	 * 
	 * @param generationType type of maze to generate
	 * @param dimensions     dimension to use for generating maze
	 * @return maze
	 */
	@GetMapping("/mazeinfo/final/{generationType}/{dimensions}")
	public ResponseEntity<int[][]> generateMazeFinal(@PathVariable("generationType") String generationType,
			@PathVariable("dimensions") int dimensions) {
		try {
			Maze maze = new Maze(dimensions);
			if ("dfs".equals(generationType)) {
				maze.dfsGenerationStart();
			}
			if ("recursive division".equals(generationType)) {
				maze.recursiveDivisionGenerationStart();
			}
			int[][] generatedMaze = maze.getValues();
			return new ResponseEntity<>(generatedMaze, HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(null, HttpStatus.CONFLICT);
		}
	}

	/**
	 * Generates and returns maze generation steps based on requested type.
	 * 
	 * @param generationType specifies type of generation for maze
	 * @return list of steps for generation of maze
	 */
	@GetMapping("/mazeinfo/defaultFull/{generationType}")
	public ResponseEntity<List<int[][]>> defaultGenerateMazeFull(
			@PathVariable("generationType") String generationType) {
		Maze maze = new Maze();
		if ("dfs".equals(generationType)) {
			maze.dfsGenerationStart();
		}
		if ("recursive division".equals(generationType)) {
			maze.recursiveDivisionGenerationStart();
		}
		return new ResponseEntity<>(maze.getSteps(), HttpStatus.OK);
	}

	/**
	 * Checks solution to maze using Maze class' check solution method.
	 * 
	 * @param attempt attempt at solving maze
	 * @return true or false for correct solution
	 */
	@PostMapping("/mazeinfo/check")
	public ResponseEntity<Boolean> checkSolution(@RequestBody int[][] attempt) {
		Maze maze = new Maze(attempt.length);
		return new ResponseEntity<>(maze.checkSolution(attempt), HttpStatus.OK);
	}
}
