import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

public class FlappyBird extends JFrame implements ActionListener, KeyListener {

    private Timer timer;
    private int birdY, birdVelocity;
    private boolean isJumping;
    private List<Obstacle> obstacles;
    private int obstacleSpeed;
    private int obstacleGap;
    private int distanceBetweenObstacles;
    private int score;
    private Image birdImage;
    private Image obstacleImage;
    private Image obstacleImage2;
    private int obstacleWidth;
    private JLabel scoreLabel;

    public FlappyBird() {
        setTitle("Flappy Bird");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        birdY = 300;
        birdVelocity = 0;
        isJumping = false;
        obstacles = new ArrayList<>();
        obstacleSpeed = 10;
        obstacleGap = 200;
        obstacleWidth = 50;
        distanceBetweenObstacles = 300;
        score = 0;

        try {
            birdImage = ImageIO.read(new File("bird.png"));
            obstacleImage = ImageIO.read(new File("obstacle.png"));
            obstacleImage2 = ImageIO.read(new File("obstacle2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));

        timer = new Timer(20, this);
        timer.start();

        addKeyListener(this);
        setFocusable(true);
        generateObstacle(getWidth());
        generateObstacle(getWidth() + distanceBetweenObstacles);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(scoreLabel, BorderLayout.NORTH);
        panel.add(new GamePanel(), BorderLayout.CENTER);
        add(panel);
    }

    private void generateObstacle(int x) {
        int obstacleHeight = new Random().nextInt(getHeight() - obstacleGap - 50);
        boolean comingFromTop = new Random().nextBoolean();

        Image obstacleImageToUse = comingFromTop ? obstacleImage2 : obstacleImage;

        obstacles.add(new Obstacle(x, comingFromTop ? 0 : getHeight() - obstacleHeight, obstacleWidth, obstacleHeight, obstacleImageToUse));
        obstacles.add(new Obstacle(x, comingFromTop ? obstacleHeight + obstacleGap : 0, obstacleWidth, getHeight() - obstacleHeight - obstacleGap, obstacleImageToUse));
    }

    public void actionPerformed(ActionEvent e) {
        birdVelocity += 2;
        birdY += birdVelocity;

        // Verificar si el pájaro toca la parte superior o inferior de la pantalla
        if (birdY < 0 || birdY > getHeight() - 50) {
            gameOver();
        }

        if (isJumping) {
            birdVelocity = -15;
            isJumping = false;
        }

        Iterator<Obstacle> iterator = obstacles.iterator();
        while (iterator.hasNext()) {
            Obstacle obstacle = iterator.next();
            obstacle.x -= obstacleSpeed;

            if (obstacle.x + obstacle.width < 0) {
                iterator.remove();
                score += 10;  // Sumar puntos cuando se pasa un obstáculo
            }
        }

        if (obstacles.isEmpty() || obstacles.get(obstacles.size() - 1).x < getWidth() - distanceBetweenObstacles) {
            generateObstacle(getWidth());
        }

        checkCollision();

        scoreLabel.setText("Score: " + score);

        repaint();
    }

    private void checkCollision() {
        Rectangle birdRectangle = new Rectangle(150, birdY, 50, 50);
        for (Obstacle obstacle : obstacles) {
            if (birdRectangle.intersects(obstacle)) {
                gameOver();
            }
        }
    }

    private void gameOver() {
        timer.stop();
        int choice = JOptionPane.showConfirmDialog(this, "Game Over! Your score: " + score + "\nDo you want to play again?", "Game Over", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            System.exit(0);
        }
    }

    private void restartGame() {
        birdY = 300;
        birdVelocity = 0;
        obstacles.clear();
        score = 0;
        generateObstacle(getWidth());
        generateObstacle(getWidth() + distanceBetweenObstacles);
        timer.start();
    }

    private class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(Color.CYAN);
            g.fillRect(0, 0, getWidth(), getHeight());

            g.drawImage(birdImage, 150, birdY, this);

            for (Obstacle obstacle : obstacles) {
                Image scaledObstacleImage = obstacle.image.getScaledInstance(obstacle.width, obstacle.height, Image.SCALE_SMOOTH);
                g.drawImage(scaledObstacleImage, obstacle.x, obstacle.y, this);
            }
        }
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            isJumping = true;
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlappyBird game = new FlappyBird();
            game.setVisible(true);
        });
    }

    private class Obstacle extends Rectangle {
        private Image image;

        public Obstacle(int x, int y, int width, int height, Image image) {
            super(x, y, width, height);
            this.image = image;
        }
    }
}