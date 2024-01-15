import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FlappyBirdGame extends JFrame implements ActionListener, KeyListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int birdY = 250;
    private int birdVelocity = 0;
    private List<Rectangle> obstacles;
    private Timer timer;
    private Random random;
    private final int obstacleGap = 150; // Espacio mínimo entre obstáculos

    public FlappyBirdGame() {
        setTitle("Flappy Bird");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        obstacles = new ArrayList<>();
        random = new Random();

        timer = new Timer(20, this);
        timer.start();

        addKeyListener(this);
        setFocusable(true);
        generateObstacleDown();
        generateObstacleUp();
    }

    private void generateObstacleDown() {
        int obstacleWidth = 50;
        int minHeight = 50;
        int maxHeight = 400 - obstacleGap; // Ajuste de la altura máxima
        int obstacleHeight = random.nextInt(maxHeight - minHeight + 1) + minHeight;

        // Asegura que los obstáculos se intercalen alternativamente
        int xPosition = obstacles.isEmpty() ? 800 : obstacles.get(obstacles.size() - 1).x + 200;

        obstacles.add(new Rectangle(xPosition, 500 - obstacleHeight, obstacleWidth, obstacleHeight));

        // Genera obstáculos adicionales antes de que alcancen el borde izquierdo
        for (int i = 1; i <= 2; i++) {
            obstacles.add(new Rectangle(xPosition + i * 200, 500 - random.nextInt(maxHeight - minHeight + 1) + minHeight,
                    obstacleWidth, random.nextInt(maxHeight - minHeight + 1) + minHeight));
        }
    }

    private void generateObstacleUp() {
        int obstacleWidth = 50;
        int minHeight = 50;
        int maxHeight = 400 - obstacleGap; // Ajuste de la altura máxima
        int obstacleHeight = random.nextInt(maxHeight - minHeight + 1) + minHeight;

        // Asegura que los obstáculos se intercalen alternativamente
        int xPosition = obstacles.isEmpty() ? 800 : obstacles.get(obstacles.size() - 1).x;

        obstacles.add(new Rectangle(xPosition, 0, obstacleWidth, obstacleHeight));

        // Genera obstáculos adicionales antes de que alcancen el borde izquierdo
        for (int i = 1; i <= 2; i++) {
            int newY = obstacles.get(obstacles.size() - 1).y + obstacleHeight + obstacleGap;
            obstacles.add(new Rectangle(xPosition + i * 200, newY,
                    obstacleWidth, random.nextInt(maxHeight - minHeight + 1) + minHeight));
        }
    }


    public void actionPerformed(ActionEvent e) {
        birdVelocity += 1; // Gravedad
        birdY += birdVelocity;

        for (Rectangle obstacle : obstacles) {
            obstacle.x -= 5; // Velocidad del obstáculo

            if (obstacle.x + obstacle.width < 0) {
                obstacles.remove(obstacle);
                generateObstacleDown();
                generateObstacleUp();
                break;
            }
        }

        checkCollision();

        repaint();
    }

    private void checkCollision() {
        Rectangle birdRect = new Rectangle(100, birdY, 50, 30); // Ajuste de las dimensiones del pájaro

        // Verificar colisión con la zona verde
        Rectangle greenZone = new Rectangle(0, 500, 800, 100);
        if (birdRect.intersects(greenZone)) {
            resetGame();
            return;
        }

        // Verificar colisión con los obstáculos
        for (Rectangle obstacle : obstacles) {
            if (birdRect.intersects(obstacle)) {
                // Colisión con el obstáculo, aquí puedes manejar la lógica de juego (p. ej., reiniciar juego)
                resetGame();
            }
        }
    }

    private void resetGame() {
        birdY = 250;
        birdVelocity = 0;
        obstacles.clear();
        generateObstacleDown();
        generateObstacleUp();
    }

    public void paint(Graphics g) {
        super.paint(g);

        g.setColor(new Color(135, 206, 250)); // Ajuste del color del fondo
        g.fillRect(0, 0, 800, 600);

        g.setColor(new Color(34, 139, 34)); // Ajuste del color de la zona verde
        g.fillRect(0, 500, 800, 100);

        g.setColor(Color.red);
        g.fillRect(100, birdY, 50, 30); // Ajuste de las dimensiones del pájaro

        for (Rectangle obstacle : obstacles) {
            g.setColor(new Color(139, 69, 19)); // Ajuste del color de los obstáculos
            if (obstacle.y == 0) {
                // Obstáculo arriba
                g.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
            } else {
                // Obstáculo abajo
                g.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
            }
        }

        Toolkit.getDefaultToolkit().sync();
    }

    public static void main(String[] args) {
        FlappyBirdGame game = new FlappyBirdGame();
        game.setVisible(true);
    }

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            birdVelocity = -10;
        }
    }

    public void keyReleased(KeyEvent e) {}
}
