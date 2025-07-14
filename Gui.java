import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Gui extends JFrame {
    private static final int GRID_SIZE = 9;
    private static final int SUBGRID_SIZE = 3;
    private static final int CELL_SIZE = 50;

    private int lives = 3;
    private int[][] puzzle;
    private int[][] solution;
    private JTextField[][] cells = new JTextField[GRID_SIZE][GRID_SIZE];
    private JLabel livesLabel;

    public Gui() {
        setTitle("Sudoku Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        puzzle = SudokuGenerator.generatePuzzleBoard(40); // Remove 40 cells (medium difficulty)

        // Create a solved version for validation
        solution = new int[GRID_SIZE][GRID_SIZE];
        copyBoard(puzzle, solution);
        SudokuGenerator.generateFullBoard(solution);

        JPanel boardPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Font font = new Font("Monospaced", Font.BOLD, 20);

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JTextField cell = new JTextField();
                cell.setHorizontalAlignment(JTextField.CENTER);
                cell.setFont(font);

                if (puzzle[row][col] != 0) {
                    cell.setText(String.valueOf(puzzle[row][col]));
                    cell.setEditable(false);
                    cell.setBackground(Color.LIGHT_GRAY);
                } else {
                    cell.setText("");
                    final int r = row, c = col;
                    cell.addActionListener(e -> checkInput(r, c));
                }

                int top = (row % SUBGRID_SIZE == 0) ? 4 : 1;
                int left = (col % SUBGRID_SIZE == 0) ? 4 : 1;
                int bottom = (row == GRID_SIZE - 1) ? 4 : 1;
                int right = (col == GRID_SIZE - 1) ? 4 : 1;

                cell.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));

                cells[row][col] = cell;
                boardPanel.add(cell);
            }
        }


        livesLabel = new JLabel("Lives: 3");
        livesLabel.setFont(new Font("Arial", Font.BOLD, 16));
        livesLabel.setHorizontalAlignment(SwingConstants.CENTER);

        add(boardPanel, BorderLayout.CENTER);
        add(livesLabel, BorderLayout.SOUTH);

        setSize(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE + 50);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void checkInput(int row, int col) {
        String text = cells[row][col].getText();
        if (text.isEmpty()) return;

        try {
            int number = Integer.parseInt(text);

            if (number < 1 || number > 9) {
                showError(row, col, "Invalid number. Enter 1-9.");
                return;
            }

            // Check against solution
            if (number == solution[row][col]) {
                cells[row][col].setForeground(Color.BLUE);
                cells[row][col].setEditable(false);
            } else {
                lives--;
                livesLabel.setText("Lives: " + lives);
                showError(row, col, "Incorrect!");

                if (lives == 0) {
                    JOptionPane.showMessageDialog(this, "Game Over! You lost all your lives.", "Game Over", JOptionPane.ERROR_MESSAGE);
                    disableAllCells();
                }
            }
        } catch (NumberFormatException ex) {
            showError(row, col, "Please enter a number.");
        }
    }

    private void showError(int row, int col, String message) {
        cells[row][col].setText("");
        cells[row][col].setBackground(Color.PINK);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.WARNING_MESSAGE);
        cells[row][col].setBackground(Color.WHITE);
    }

    private void disableAllCells() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                cells[row][col].setEditable(false);
            }
        }
    }

    private void copyBoard(int[][] src, int[][] dest) {
        for (int row = 0; row < GRID_SIZE; row++) {
            System.arraycopy(src[row], 0, dest[row], 0, GRID_SIZE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Gui::new);
    }
}
