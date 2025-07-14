public class SudokuSolver {

    private static int Grid_Size = 9;

    private static boolean isNumberInRow(int[][] board, int number, int row){
        for(int i =0; i < Grid_Size; i++){
            if(board[row][i] == number){
                return true;
            }
        }
        return false;
    }

    private static boolean isNumberInColumn(int[][] board, int number, int column){
        for(int i =0; i < Grid_Size; i++){
            if(board[i][column] == number){
                return true;
            }
        }
        return false;
    }

    private static boolean isNumberinBox(int[][] board, int number, int row, int column){
        int localBoxRow = row - row % 3;
        int localBoxColumn = column - column %3;

        for(int i = localBoxRow; i < localBoxRow + 3; i++){
            for(int j = localBoxColumn; j < localBoxColumn + 3; j++){
                if(board[i][j] == number){
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isValidPlacement(int[][] board , int number, int row, int column){
        return !isNumberInColumn(board, number, column) &&
            !isNumberInRow(board, number, row) &&
            !isNumberinBox(board, number, row, column);
    }

    public static int[][] getBoard(int [][] board){
        return board;
    }
}