import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SudokuGenerator {
    public static final int GRID_SIZE = 9;

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    public static int[][] generatePuzzleBoard(Difficulty difficulty) {
        int cellsToRemove = getCellsToRemove(difficulty);
        int[][] board = new int[GRID_SIZE][GRID_SIZE];

        generateFullBoard(board);

        removeCells(board, cellsToRemove);

        return board;
    }

    public static boolean generateFullBoard(int[][] board) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (board[row][col] == 0) {
                    List<Integer> numbers = getShuffledNumbers();
                    for (int number : numbers) {
                        if (SudokuSolver.isValidPlacement(board, number, row, col)) {
                            board[row][col] = number;

                            if (generateFullBoard(board)) {
                                return true;
                            }

                            board[row][col] = 0; 
                        }
                    }
                    return false;
                }
            }
        }
        return true; 
    }

    private static List<Integer> getShuffledNumbers() {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= GRID_SIZE; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);
        return numbers;
    }

    private static void removeCells(int[][] board, int cellsToRemove) {
        Random rand = new Random();
        while (cellsToRemove > 0) {
            int row = rand.nextInt(GRID_SIZE);
            int col = rand.nextInt(GRID_SIZE);

            if (board[row][col] != 0) {
                board[row][col] = 0;
                cellsToRemove--;
            }
        }
    }

    private static int getCellsToRemove(Difficulty difficulty) {
        switch (difficulty) {
            case EASY:
                return 30; 
            case MEDIUM:
                return 40;
            case HARD:
                return 50; 
            default:
                return 40; 
        }
    }
}

