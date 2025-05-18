import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBirdGame extends JPanel implements ActionListener, KeyListener {

    private javax.swing.Timer timer;
    private int birdY = 250;
    private int velocity = 0;
    private final int GRAVITY = 1;
    private final int JUMP_STRENGTH = -12;
    private final int BIRD_X = 100;
    private ArrayList<Rectangle> pipes;
    private Random rand;
    private int score = 0;
    private boolean gameOver = false;
    private int pipeSpeed = 5;
    private int gapMin = 120, gapMax = 180;
    private int pipeWidth = 60;

    private boolean birdFlapToggle = false; // simple flap animation toggle

    public FlappyBirdGame() {
        setPreferredSize(new Dimension(400, 600));
        setBackground(new Color(135, 206, 235));  // sky blue
        setFocusable(true);
        addKeyListener(this);

        rand = new Random();
        pipes = new ArrayList<>();
        addInitialPipes();

        timer = new javax.swing.Timer(20, this);  // 50 FPS approx
        timer.start();
    }

    private void addInitialPipes() {
        pipes.clear();
        score = 0;
        birdY = 250;
        velocity = 0;
        gameOver = false;

        // Add 3 sets of pipes spaced 200 pixels apart
        int x = 400;
        for (int i = 0; i < 3; i++) {
            addPipeAt(x);
            x += 200;
        }
    }

    private void addPipeAt(int x) {
        int gap = gapMin + rand.nextInt(gapMax - gapMin + 1);
        int height = 50 + rand.nextInt(250);
        pipes.add(new Rectangle(x, 0, pipeWidth, height));
        pipes.add(new Rectangle(x, height + gap, pipeWidth, 600 - (height + gap) - 100)); // leave 100px ground
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw sky (background)
        g.setColor(new Color(135, 206, 235));
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw ground
        g.setColor(new Color(85, 107, 47)); // dark green
        g.fillRect(0, getHeight() - 100, getWidth(), 100);

        // Draw bird with simple flap animation (yellow/orange toggle)
        g.setColor(birdFlapToggle ? Color.orange : Color.yellow);
        g.fillOval(BIRD_X, birdY, 30, 30);

        // Draw pipes
        g.setColor(new Color(34, 139, 34)); // forest green
        for (Rectangle pipe : pipes) {
            g.fillRect(pipe.x, pipe.y, pipe.width, pipe.height);
        }

        // Draw score
        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + score, 10, 30);

        if (gameOver) {
            g.setColor(new Color(255, 0, 0, 180));
            g.setFont(new Font("Arial", Font.BOLD, 48));
            String msg = "Game Over!";
            int msgWidth = g.getFontMetrics().stringWidth(msg);
            g.drawString(msg, (getWidth() - msgWidth) / 2, getHeight() / 2 - 20);

            g.setFont(new Font("Arial", Font.BOLD, 20));
            String restartMsg = "Press ENTER to Restart";
            int restartWidth = g.getFontMetrics().stringWidth(restartMsg);
            g.drawString(restartMsg, (getWidth() - restartWidth) / 2, getHeight() / 2 + 20);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) {
            repaint();
            return;
        }

        velocity += GRAVITY;
        birdY += velocity;

        // Prevent bird from going out of screen
        if (birdY < 0) birdY = 0;
        if (birdY > getHeight() - 100 - 30) birdY = getHeight() - 100 - 30;

        Rectangle bird = new Rectangle(BIRD_X, birdY, 30, 30);

        ArrayList<Rectangle> toRemove = new ArrayList<>();
        boolean passedPipe = false;

        for (Rectangle pipe : pipes) {
            pipe.x -= pipeSpeed;

            if (pipe.intersects(bird)) {
                gameOver = true;
            }

            // Count score when bird passes pipe only once (top pipes only)
            if (!pipe.isEmpty() && pipe.y == 0 && pipe.x + pipe.width == BIRD_X) {
                score++;
                passedPipe = true;
            }

            if (pipe.x + pipe.width < 0) {
                toRemove.add(pipe);
            }
        }

        pipes.removeAll(toRemove);

        // Add new pipes when needed to keep 6 pipes (3 pairs)
        if (pipes.size() < 6) {
            int lastX = pipes.get(pipes.size() - 1).x;
            addPipeAt(lastX + 200);
        }

        // Toggle bird flap every frame for simple animation
        birdFlapToggle = !birdFlapToggle;

        repaint();
    }

    private void restartGame() {
        addInitialPipes();
        score = 0;
        birdY = 250;
        velocity = 0;
        gameOver = false;
        timer.start();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameOver) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                velocity = JUMP_STRENGTH;
            }
        } else {
            // Restart on Enter key after game over
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                restartGame();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) { /* Not used */ }

    @Override
    public void keyTyped(KeyEvent e) { /* Not used */ }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Bird Clone");
        FlappyBirdGame game = new FlappyBirdGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Center the window
        frame.setVisible(true);
    }
}
