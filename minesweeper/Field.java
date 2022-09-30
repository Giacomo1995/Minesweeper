package minesweeper;

// Imports
import java.util.Random;
import java.awt.Color;


public class Field {

    private static Level level;  // Level of difficulty
    private static int[][] matrix;  // Matrix of integers


    public static Level getLevel() {
        return level;
    }


    public static int[][] getMatrix() {
        return matrix;
    }


    // Static method to randomly create the matrix for the game
    public static int[][] create(Level l) {
        level = l;
        matrix = new int[level.getRows()][level.getColumns()];

        Random rand = new Random();
        // Setting of mines
        for(int i = 0; i < level.getMines(); i++) {
            int r = rand.nextInt(level.getRows());
            int c = rand.nextInt(level.getColumns());

            while(matrix[r][c] == -1) {
                r = rand.nextInt(level.getRows());
                c = rand.nextInt(level.getColumns());
            }

            matrix[r][c] = -1; // -1 is the mine value;
        }

        // Setting the numbers on the matrix (0 means there are no mines)
        for(int i = 0; i < level.getRows(); i++) {
            for(int j = 0; j < level.getColumns(); j++) {
                if(matrix[i][j] != -1)
                    matrix[i][j] = counter(i, j);
            }
        }

        //printMatrix();
        return matrix;
    }


    // This method counts and returns the number of the mines around a cell with coordinates (i, j)
    private static int counter(int i, int j) {
        int count = 0;

        for(int r = i-1; r <= i+1; r++) {
            for(int c = j-1; c <= j+1; c++)
                if(r >= 0 && r < level.getRows() && c >= 0 && c < level.getColumns() && matrix[r][c] == -1)
                    count++;
        }

        return count;
    }


    // Method to return the color relative to the number of mines around a specific cell
    protected static Color getColor(int n) {
        switch(n) {
            case 1:
                return Color.blue;
            case 2:
                return Color.green;
            case 3:
                return Color.red;
            case 4:
                return Color.black;
            case 5:
                return Color.orange;
            case 6:
                return Color.cyan;
            case 7:
                return Color.magenta;
            case 8:
                return Color.darkGray;
        }

        return Color.black;
    }

}
