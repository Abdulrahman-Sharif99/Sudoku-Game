import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Gui extends JFrame {
    private static final int GRID_SIZE = 9;
    private static final int SUBGRID_SIZE = 3;

    private int lives = 3;
    private int[][] puzzle;
    private int[][] solution;
    private JTextField[][] cells = new JTextField[GRID_SIZE][GRID_SIZE];
    private JLabel livesLabel;

    private JTextField selectedCell = null;

    private JPanel difficultyPanel;

    public Gui() {
        setTitle("Sudoku Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        difficultyPanel = new JPanel();
        difficultyPanel.setLayout(new GridBagLayout()); 

        JPanel buttonInnerPanel = new JPanel(new GridLayout(4, 1, 10, 10));

        JLabel label = new JLabel("Select Difficulty:", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 14)); 
        label.setPreferredSize(new Dimension(120, 40));

        JButton easyBtn = new JButton("Easy");
        JButton mediumBtn = new JButton("Medium");
        JButton hardBtn = new JButton("Hard");

        Font btnFont = new Font("Arial", Font.BOLD, 20);
        easyBtn.setFont(btnFont);
        mediumBtn.setFont(btnFont);
        hardBtn.setFont(btnFont);
        easyBtn.setPreferredSize(new Dimension(120, 40));
        mediumBtn.setPreferredSize(new Dimension(120, 40));
        hardBtn.setPreferredSize(new Dimension(120, 40));

        easyBtn.addActionListener(e -> startGame(SudokuGenerator.Difficulty.EASY));
        mediumBtn.addActionListener(e -> startGame(SudokuGenerator.Difficulty.MEDIUM));
        hardBtn.addActionListener(e -> startGame(SudokuGenerator.Difficulty.HARD));

        buttonInnerPanel.add(label);
        buttonInnerPanel.add(easyBtn);
        buttonInnerPanel.add(mediumBtn);
        buttonInnerPanel.add(hardBtn);

        // Add to center
        difficultyPanel.add(buttonInnerPanel);

        add(difficultyPanel, BorderLayout.CENTER);


        setSize(700, 550);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void startGame(SudokuGenerator.Difficulty difficulty) {
        remove(difficultyPanel);

        lives = 3;
        livesLabel = new JLabel("Lives: " + lives);
        livesLabel.setFont(new Font("Arial", Font.BOLD, 16));
        livesLabel.setHorizontalAlignment(SwingConstants.CENTER);

        puzzle = SudokuGenerator.generatePuzzleBoard(difficulty);
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
                } else {
                    cell.setEditable(true);
                    final int r = row, c = col;

                    cell.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if (selectedCell != null) {
                                selectedCell.setBackground(Color.WHITE);
                            }
                            selectedCell = cell;
                            highlightArea(r, c);
                            cell.setBackground(Color.LIGHT_GRAY);
                            cell.requestFocus();
                        }
                    });

                    cell.addKeyListener(new KeyAdapter() {
                        @Override
                        public void keyTyped(KeyEvent e) {
                            char ch = e.getKeyChar();
                            if (ch < '1' || ch > '9') {
                                e.consume();
                                return;
                            }

                            int inputNum = Character.getNumericValue(ch);

                            if (inputNum == solution[r][c]) {
                                cell.setText(String.valueOf(inputNum));
                                cell.setForeground(Color.BLUE);
                                cell.setEditable(false);
                                cell.setBackground(Color.WHITE);
                                selectedCell = null;
                                checkCompletion();
                            } else {
                                lives--;
                                livesLabel.setText("Lives: " + lives);
                                JOptionPane.showMessageDialog(Gui.this, "Incorrect!", "Error", JOptionPane.WARNING_MESSAGE);
                                cell.setText("");

                                if (lives == 0) {
                                    int option = JOptionPane.showOptionDialog(
                                        Gui.this,
                                        "Game Over! You lost all your lives.\nWould you like to start a new game or exit?",
                                        "Game Over",
                                        JOptionPane.YES_NO_OPTION,
                                        JOptionPane.ERROR_MESSAGE,
                                        null,
                                        new String[]{"New Game", "Exit"},
                                        "New Game"
                                    );

                                    if (option == JOptionPane.YES_OPTION) {
                                        dispose();
                                        SwingUtilities.invokeLater(Gui::new);
                                    } else {
                                        System.exit(0);
                                    }
                                }
                            }
                            e.consume();
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

        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.add(buttonPanel, BorderLayout.CENTER);
        rightPanel.add(livesLabel, BorderLayout.SOUTH);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(boardPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        revalidate();
        repaint();
    }

    private void handleNumberButton(int number) {
        if (selectedCell == null || !selectedCell.isEditable()) return;

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

        if (number == solution[row][col]) {
            selectedCell.setText(String.valueOf(number));
            selectedCell.setForeground(Color.BLUE);
            selectedCell.setEditable(false);
            selectedCell.setBackground(Color.WHITE);
            selectedCell = null;
            checkCompletion();
        } else {
            lives--;
            livesLabel.setText("Lives: " + lives);
            JOptionPane.showMessageDialog(this, "Incorrect!", "Error", JOptionPane.WARNING_MESSAGE);
            selectedCell.setText("");

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
                    dispose();
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

    private void resetHighlights() {
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                cells[r][c].setBackground(Color.WHITE);
            }
        }
    }

    private void highlightArea(int row, int col) {
        resetHighlights();
        for (int i = 0; i < GRID_SIZE; i++) {
            cells[row][i].setBackground(Color.LIGHT_GRAY);
            cells[i][col].setBackground(Color.LIGHT_GRAY);
        }
        int startRow = (row / SUBGRID_SIZE) * SUBGRID_SIZE;
        int startCol = (col / SUBGRID_SIZE) * SUBGRID_SIZE;
        for (int r = startRow; r < startRow + SUBGRID_SIZE; r++) {
            for (int c = startCol; c < startCol + SUBGRID_SIZE; c++) {
                cells[r][c].setBackground(Color.LIGHT_GRAY);
            }
        }
        cells[row][col].setBackground(Color.LIGHT_GRAY);
    }

    private void checkCompletion() {
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                String text = cells[r][c].getText();
                if (text.isEmpty() || Integer.parseInt(text) != solution[r][c]) {
                    return;
                }
            }
        }

        int option = JOptionPane.showOptionDialog(
            this,
            "Congratulations! You solved the Sudoku!\nDo you want to play again?",
            "Victory",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            new String[]{"Play Again", "Exit"},
            "Play Again"
        );

        if (option == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(Gui::new);
        } else {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Gui::new);
    }
}