package minesweeper;

// Imports
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;


public class GUI extends JFrame implements ActionListener, MouseListener {

    private static GUI instance = null;
    static JMenu menu;
    static Level level;  // Game's level
    static JButton restart;  // Button to restart the game
    static JButton[][] field;  // Matrix of buttons
    static JPanel matrixPanel;
    static JPanel menuPanel;
    static JButton resetButton;
    static JLabel markersCounterLabel;
    static JLabel timeLabel;
    static int matrix[][];
    static int counter = 0;
    static int markersCounter = 0;
    static int t = 0;
    static Timer timer;
    static boolean firstClick = false;


    // Pattern singleton
    public static GUI getInstance(Level l) {
        if(instance == null)
           instance = new GUI(l);

        return instance;
    }


    private GUI(Level l) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        super.setIconImage(new ImageIcon(getClass().getResource("/Icons/Icon.png")).getImage());

        level = l;
        super.setTitle("Minesweeper");
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        resetButton = new JButton();
        ImageIcon smileIcon = new ImageIcon(getClass().getResource("/Icons/SmilingFace.png"));
        resetButton.setIcon(smileIcon);
        markersCounterLabel = new JLabel(level.getMines() + "");
        markersCounter = level.getMines();
        timeLabel = new JLabel();
        timeLabel.setOpaque(true);
        timeLabel.setBackground(Color.black);
        timeLabel.setForeground(Color.red);
        timeLabel.setText("0");
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        timer = new Timer(1000, e -> {
            GUI.timeLabel.setText(++GUI.t + "");
        });

        JMenuBar menuBar = new JMenuBar();
        super.setJMenuBar(menuBar);
        menu = new JMenu("Difficulty");
        menuBar.add(menu);

        JMenuItem e = new JMenuItem("Easy");
        JMenuItem m = new JMenuItem("Medium");
        JMenuItem h = new JMenuItem("Hard");

        e.setActionCommand("Easy");
        m.setActionCommand("Medium");
        h.setActionCommand("Hard");
        e.addActionListener(this);
        m.addActionListener(this);
        h.addActionListener(this);
        menu.add(e);
        menu.add(m);
        menu.add(h);

        switch(level.name()) {
            case "EASY":
                super.setSize(400, 450);  // width and height
                super.setMinimumSize(new Dimension(400, 450));
                break;

            case "MEDIUM":
                super.setSize(520, 520);  // width and height
                super.setMinimumSize(new Dimension(500, 500));
                break;

            case "HARD":
                super.setSize(700, 600);  // width and height
                super.setMinimumSize(new Dimension(700, 700));
                break;
        }

        matrixPanel = new JPanel();  // This panel will contain the matrix of buttons
        menuPanel = new JPanel();
        super.setLayout(new BorderLayout());
        super.add(menuPanel, BorderLayout.NORTH);
        super.add(matrixPanel, BorderLayout.CENTER);
        GridLayout gl = new GridLayout(level.getRows(), level.getColumns(), 0, 0);
        matrixPanel.setLayout(gl);
        menuPanel.setLayout(new GridLayout(1, 3, 150, 0));
        menuPanel.add(timeLabel, BorderLayout.CENTER);
        menuPanel.add(resetButton, BorderLayout.CENTER);
        menuPanel.add(markersCounterLabel, BorderLayout.CENTER);
        markersCounterLabel.setOpaque(true);
        markersCounterLabel.setHorizontalAlignment(SwingConstants.CENTER);
        markersCounterLabel.setBackground(Color.black);
        markersCounterLabel.setForeground(Color.red);

        field = new JButton[level.getRows()][level.getColumns()];
        matrix = Field.create(level);

        // Panel of buttons
        for(int i = 0; i < level.getRows(); i++)
            for(int j = 0; j < level.getColumns(); j++) {
                field[i][j] = new JButton();
                field[i][j].setActionCommand(i + "," + j);  // Buttons' Id
                field[i][j].setOpaque(true);
                field[i][j].addActionListener(this);
                field[i][j].addMouseListener(this);
                matrixPanel.add(field[i][j]);
            }

        resetButton.setActionCommand("-1,-1");
        resetButton.addActionListener(this);
        super.setVisible(true);  // Visibility true

    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if(!timer.isRunning())
            timer.start();

        switch(e.getActionCommand()) {
            case "Easy":
                reset(Level.EASY);
                SwingUtilities.updateComponentTreeUI(this);  // Reload the JFrame
                return;

            case "Medium":
                reset(Level.MEDIUM);
                SwingUtilities.updateComponentTreeUI(this);  // Reload the JFrame
                return;

            case "Hard":
                reset(Level.HARD);
                SwingUtilities.updateComponentTreeUI(this); // Reload the JFrame
                return;

            case "Exit":
                System.exit(0);
                return;
        }

        int i = Integer.parseInt(e.getActionCommand().split(",")[0]);
        int j = Integer.parseInt(e.getActionCommand().split(",")[1]);

        if(i == -1 && j == -1) {
            reset(level);  // Reset for starting a new game
            SwingUtilities.updateComponentTreeUI(this);  // Reload the JFrame
            return;
        }

        if(field[i][j].getIcon() != null)
            return;

        if(firstClick == false) {
            if(matrix[i][j] != 0)
                while(matrix[i][j] != 0)
                    matrix = Field.create(level);

            firstClick = true;
        }

        printCell(i, j);
        field[i][j].setEnabled(false);
        field[i][j].setBackground(Color.GRAY);
        counter++;

        switch(matrix[i][j]) {
            case -1:
                for(int k = 0; k < level.getRows(); k++)
                    for(int z = 0; z < level.getColumns(); z++) {
                        printCell(k, z);
                        field[k][z].setEnabled(false);
                        field[k][z].setBackground(Color.DARK_GRAY);

                        if(matrix[k][z] == -1)
                            field[k][z].setBackground(Color.RED);
                    }

                resetButton.setIcon(new ImageIcon(getClass().getResource("/Icons/CrossedOutEyesFace.png")));
                timer.stop();

                break;

            case 0:
                for(int k = 0; k < level.getRows(); k++)
                    for(int z = 0; z < level.getColumns(); z++)
                        field[i][j].setEnabled(true);

                showNeighbours(i, j);
                break;
        }

        // WIN
        if( ((level.getRows() * level.getColumns()) - level.getMines()) == counter && matrix[i][j] != -1 ) {
            for(int k = 0; k < level.getRows(); k++)
                for(int z = 0; z < level.getColumns(); z++) {
                    field[k][z].setBackground(Color.GREEN);
                    field[k][z].setEnabled(false);
                }

            resetButton.setIcon(new ImageIcon(getClass().getResource("/Icons/SmilingFace.png")));
            timer.stop();
        }

        SwingUtilities.updateComponentTreeUI(this);  // Reload the JFrame
    }


    // Method for printing the cell with position (i, j)
    private void printCell(int i, int j) {
        switch (matrix[i][j]) {
            case -1:
                ImageIcon img = new ImageIcon(getClass().getResource("/Icons/Bomb.png"));
                field[i][j].setIcon(img);
                break;
            case 0:
                field[i][j].setText("");
                break;
            default:
                field[i][j].setText(matrix[i][j] + "");
                break;
        }
    }


    // Method to show the neighbour cells of a cell with value 0
    private void showNeighbours(int i, int j) {

        if(field[i][j].isEnabled() && matrix[i][j] == 0) {
            field[i][j].setText("");

            if(field[i][j].getIcon() != null)
                markersCounterLabel.setText(++markersCounter + "");
        } else return;

        field[i][j].setIcon(null);
        field[i][j].setBackground(Color.GRAY);
        field[i][j].setEnabled(false);

        for(int r = i-1; r <= i+1; r++) {
            for(int c = j-1; c <= j+1; c++) {
                if(r >= 0 && r < level.getRows() && c >= 0 && c < level.getColumns() && field[r][c].isEnabled()) {
                    if(matrix[r][c] == 0) {
                        counter++;
                        showNeighbours(r, c);
                    }
                    else {
                        if(field[r][c].getIcon() != null)
                            markersCounterLabel.setText(++markersCounter + "");

                        field[r][c].setIcon(null);
                        field[r][c].setBackground(Color.GRAY);
                        field[r][c].setText(matrix[r][c] + "");
                        field[r][c].setEnabled(false);
                        counter++;
                    }
                }
            }
        }
    }


    private void reset(Level l) {
        if(!l.name().equals(level.name())) {
            timer.stop();
            this.setVisible(false);
            new GUI(l);
        }

        for(int n = 0; n < level.getRows(); n++) {
            for(int m = 0; m < level.getColumns(); m++) {
                field[n][m].setEnabled(true);
                field[n][m].setIcon(null);
                field[n][m].setText("");
                field[n][m].setBackground(null);
            }
        }

        firstClick = false;
        matrix = Field.create(level);
        resetButton.setOpaque(true);
        resetButton.setIcon(new ImageIcon(getClass().getResource("/Icons/SmilingFace.png")));
        resetButton.setEnabled(true);
        counter = 0;
        markersCounter = level.getMines();
        markersCounterLabel.setText(markersCounter +"");
        t = 0;
        timer.stop();
        timeLabel.setText("0");
    }


    @Override
    public void mouseClicked(MouseEvent e) {

        ImageIcon img = new ImageIcon(getClass().getResource("/Icons/Flag.png"));
        JButton button = (JButton)e.getComponent();

        if(e.getButton() == 3 && button.isEnabled()) {
            if(button.getIcon() == null) {
                button.setIcon(img);
                markersCounterLabel.setText(--markersCounter +"");
            }
            else {
                button.setIcon(null);
                markersCounterLabel.setText(++markersCounter +"");
            }
        }
    }


    @Override
    public void mousePressed(MouseEvent e) {
        JButton button = (JButton)e.getComponent();

        if(button.isEnabled())
            resetButton.setIcon(new ImageIcon(getClass().getResource("/Icons/OpenMouthFace.png")));
    }


    @Override
    public void mouseReleased(MouseEvent e) {
        if(resetButton.getIcon() != null && !resetButton.getIcon().toString().contains("Icons/CrossedOutEyesFace.png"))
            resetButton.setIcon(new ImageIcon(getClass().getResource("/Icons/SmilingFace.png")));
    }


    @Override
    public void mouseEntered(MouseEvent e) {
    }


    @Override
    public void mouseExited(MouseEvent e) {
    }

}
