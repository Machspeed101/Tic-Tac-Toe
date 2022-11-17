import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.Random;
import javax.swing.JTextField;

public class TicTacToe extends JFrame implements ActionListener{
    private JButton startButton, replayButton;
    private JLabel username;
    private JTextField t;
    private Board board;
    private int lineThickness=4;
    private Color oColor=Color.BLUE, xColor=Color.RED;
    static final char BLANK=' ', O='O', X='X';
    private char position[]={  // Board position (BLANK, O, or X)
            BLANK, BLANK, BLANK,
            BLANK, BLANK, BLANK,
            BLANK, BLANK, BLANK};
    private int wins=0, losses=0, draws=0;  // game count by user, to determine if ai starts next round and for fun :)
    public boolean play = false;
    public boolean name = true;

    // Start the game
    public static void main() {
        new TicTacToe();
    }

    // Initialize
    public TicTacToe() {
        JFrame frame = new JFrame();
        frame.setTitle("Tic Tac Toe");
        frame.setLayout(new BorderLayout());
        JPanel topPanel=new JPanel();
        JPanel rightPanel=new JPanel();
        JPanel bottomPanel=new JPanel();
        rightPanel.add(startButton=new JButton("Start"));
        rightPanel.add(replayButton=new JButton("Replay"));
        topPanel.add(username = new JLabel("Welcome please enter your name"));
        bottomPanel.add(t = new JTextField(16));
        startButton.addActionListener(this);
        replayButton.addActionListener(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(topPanel, BorderLayout.NORTH);
        frame.getContentPane().add(rightPanel, BorderLayout.EAST);
        frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        frame.getContentPane().add(board=new Board(), BorderLayout.CENTER);
        frame.pack();
        frame.setSize(500, 500);
        frame.setVisible(true);

        JMenuBar menubar = new JMenuBar();
        frame.setJMenuBar(menubar);
        JMenu menuMenu = new JMenu("Menu");
        menubar.add(menuMenu);
        JMenuItem resetItem = new JMenuItem("Reset");
        menuMenu.add(resetItem);
        resetItem.addActionListener(e -> reset());
        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.addActionListener(e -> quit());
        menuMenu.add(quitItem);

        t.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    if (name == true){ // can only add name when its true, so only 1 attempt
                        String input = t.getText();
                        username.setText("Hello " + input + " You are circle, and the computer is cross"); 
                        name = false;}
                }
            });

    }

    private void quit() //exits the game
    {
        System.exit(0);
    }

    private void reset()  //rests the whole board
    {
        for (int j=0; j<9; ++j)
            position[j]=BLANK;
        username.setText("Welcome please enter your name");
        name = true;
        board.repaint();
        play = false;
        wins = 0;
        losses = 0;
        draws = 0;
    }

    // buttons
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==startButton && name == false) { 
            play = true; //if this is true the game can start

        }
        else if (e.getSource()==startButton && name == true) {
            username.setText("Please enter username");
        }
        else if (e.getSource()==replayButton) {
            for (int j=0; j<9; ++j) // makes the grid blank
                position[j]=BLANK;

            if ((wins+losses+draws)%2 == 1) // checks the stats if the computer starts 
                board.nextMove();              // next round

            username.setText("Hello " + t.getText() + " You are circle, and the computer is cross");
        }
        board.repaint();
    }

    // Board is what actually plays and displays the game
    private class Board extends JPanel implements MouseListener{
        private Random random=new Random();
        private int rows[][]={{0,2},{3,5},{6,8},{0,6},{1,7},{2,8},{0,8},{2,6}};

        // Endpoints of the 8 rows in position[] (across, down, diagonally)

        public Board() {
            addMouseListener(this);
        }

        // Redraw the board
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            int w=getWidth();
            int h=getHeight();
            Graphics2D g2d = (Graphics2D) g;

            // Draw the grid
            g2d.setPaint(Color.WHITE);
            g2d.fill(new Rectangle2D.Double(0, 0, w, h));
            g2d.setPaint(Color.BLACK);
            g2d.setStroke(new BasicStroke(lineThickness));
            g2d.draw(new Line2D.Double(0, h/3, w, h/3));
            g2d.draw(new Line2D.Double(0, h*2/3, w, h*2/3));
            g2d.draw(new Line2D.Double(w/3, 0, w/3, h));
            g2d.draw(new Line2D.Double(w*2/3, 0, w*2/3, h));

            // Draw the Os and Xs
            for (int i=0; i<9; ++i) {
                double xpos=(i%3+0.5)*w/3.0;
                double ypos=(i/3+0.5)*h/3.0;
                double xr=w/8.0;
                double yr=h/8.0;
                if (position[i]==O) {
                    g2d.setPaint(oColor);
                    g2d.draw(new Ellipse2D.Double(xpos-xr, ypos-yr, xr*2, yr*2));
                }
                else if (position[i]==X) {
                    g2d.setPaint(xColor);
                    g2d.draw(new Line2D.Double(xpos-xr, ypos-yr, xpos+xr, ypos+yr));
                    g2d.draw(new Line2D.Double(xpos-xr, ypos+yr, xpos+xr, ypos-yr));
                }
            }
        }

        // Draw an O where the mouse is clicked
        public void mouseClicked(MouseEvent e) {

            if (play == true){

                int xpos=e.getX()*3/getWidth();
                int ypos=e.getY()*3/getHeight();
                int pos=xpos+3*ypos;

                if (pos>=0 && pos<9 && position[pos]==BLANK) {
                    position[pos]=O;
                    repaint();

                    putX();  // computer plays
                    repaint();

                }

            }
        }
        // Ignore other mouse events
        public void mousePressed(MouseEvent e) {}

        public void mouseReleased(MouseEvent e) {}

        public void mouseEntered(MouseEvent e) {}

        public void mouseExited(MouseEvent e) {}

        // Computer plays X
        void putX() {

            // Check if game is over
            if (won(O))
                newGame(O);
            else if (isDraw())
                newGame(BLANK);

            // Play X, could end the game
            else {
                nextMove();
                if (won(X))
                    newGame(X);
                else if (isDraw())
                    newGame(BLANK);
            }
        }

        // Return true if player has won
        boolean won(char player) {
            for (int i=0; i<8; ++i)
                if (testRow(player, rows[i][0], rows[i][1]))
                    return true;
            return false;
        }

        // checks if player has won in the row from position[a] to position[b]
        boolean testRow(char player, int a, int b) {
            return position[a]==player && position[b]==player 
            && position[(a+b)/2]==player;
        }

        // Play X in the best spot
        void nextMove() {
            int r=findRow(X);  // try to win by completing a row of x
            if (r<0)
                r=findRow(O);  // try to stop O from winning
            if (r<0) {  // no direct option random movement
                do
                r=random.nextInt(9);
                while (position[r]!=BLANK);
            }
            position[r]=X;
        }

        // Return 0-8 for the position of a blank spot in a row if the
        // other 2 spots are occupied by player, or -1 if no spot exists
        int findRow(char player) {
            for (int i=0; i<8; ++i) {
                int result=find1Row(player, rows[i][0], rows[i][1]);
                if (result>=0)
                    return result;
            }
            return -1;
        }

        // If 2 of 3 spots in the row from position[a] to position[b]
        // are occupied by player and the third is blank, then return the
        // index of the blank spot, else return -1.
        int find1Row(char player, int a, int b) {
            int c=(a+b)/2;  // middle spot
            if (position[a]==player && position[b]==player && position[c]==BLANK)
                return c;
            if (position[a]==player && position[c]==player && position[b]==BLANK)
                return b;
            if (position[b]==player && position[c]==player && position[a]==BLANK)
                return a;
            return -1;
        }

        // checks if all the 9 spots are filled
        boolean isDraw() {
            for (int i=0; i<9; ++i)
                if (position[i]==BLANK)
                    return false;
            return true;
        }

        // gives results and stores wins loss and draws
        void newGame(char winner) {
            repaint();

            String result;
            if (winner==O) {
                ++wins;
                result = "You Win!";
                username.setText ("You win, Congrats " + t.getText());
            }
            else if (winner==X) {
                ++losses;
                result = "I Win!";
                username.setText ("AI wins");

            }
            else {
                result = "Tie";
                ++draws;
                username.setText ("It's a tie");
            }

        }
    } 
} 