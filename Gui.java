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

    private JTextField selectedCell = null; 

    public Gui() {
        setTitle("Sudoku Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        puzzle = SudokuGenerator.generatePuzzleBoard(40);

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
                    cell.addMouseListener(new MouseAdapter() {
                        public void mouseClicked(MouseEvent e) {
                            if (selectedCell != null) {
                                selectedCell.setBackground(Color.WHITE);
                            }
                            selectedCell = cell;
                            cell.setBackground(Color.LIGHT_GRAY);
                        }
                    });
                } else {
                    cell.setEditable(true);
                    final int r = row, c = col;
                    cell.addMouseListener(new MouseAdapter() {
                        public void mouseClicked(MouseEvent e) {
                            if (selectedCell != null) {
                                selectedCell.setBackground(Color.WHITE);
                            }
                            selectedCell = cell;
                            cell.setBackground(Color.LIGHT_GRAY);
                        }
                    });
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

        JPanel buttonPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        for (int i = 1; i <= 9; i++) {
            int number = i;
            JButton btn = new JButton(String.valueOf(i));
            btn.setFont(new Font("Arial", Font.BOLD, 18));
            btn.addActionListener(e -> handleNumberButton(number));
            buttonPanel.add(btn);
        }

        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("Arial", Font.BOLD, 14));
        clearBtn.addActionListener(e -> clearSelectedCell());
        buttonPanel.add(clearBtn);

        livesLabel = new JLabel("Lives: 3");
        livesLabel.setFont(new Font("Arial", Font.BOLD, 16));
        livesLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.add(buttonPanel, BorderLayout.CENTER);
        rightPanel.add(livesLabel, BorderLayout.SOUTH);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(boardPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        setSize(700, 550);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void handleNumberButton(int number) {
        if (selectedCell == null) return;

        int row = -1, col = -1;
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                if (cells[r][c] == selectedCell) {
                    row = r;
                    col = c;
                    break;
                }
            }
        }
        if (row == -1 || col == -1) return;

        // Check if correct
        if (number == solution[row][col]) {
            selectedCell.setText(String.valueOf(number));
            selectedCell.setForeground(Color.BLUE);
            selectedCell.setEditable(false);
            selectedCell.setBackground(Color.WHITE);
            selectedCell = null;
        } else {
            lives--;
            livesLabel.setText("Lives: " + lives);
            JOptionPane.showMessageDialog(this, "Incorrect!", "Error", JOptionPane.WARNING_MESSAGE);
            if (lives == 0) {
                int option = JOptionPane.showOptionDialog(
                    this,
                    "Game Over! You lost all your lives.\nWould you like to start a new game or exit?",
                    "Game Over",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null,
                    new String[]{"New Game", "Exit"},
                    "New Game"
                );

                if (option == JOptionPane.YES_OPTION) {
                    this.dispose();
                    SwingUtilities.invokeLater(Gui::new); 
                } else {
                    System.exit(0);
                }
            }
        }
    }

    private void clearSelectedCell() {
        if (selectedCell != null && selectedCell.isEditable()) {
            selectedCell.setText("");
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

