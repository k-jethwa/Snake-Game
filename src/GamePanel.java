import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    static final int screen_width = 600;
    static final int screen_height = 700;
    static final int unit_size = 30;
    static final int game_units = (screen_width * screen_height) / unit_size;
    static final int delay = 80;

    final int x[] = new int[game_units];
    final int y[] = new int[game_units];
    int bodyparts = 6;
    int applesEaten;
    int appleY;
    int appleX;
    char direction = 'R';
    boolean slithering = false;
    Timer timer;
    Random random;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(screen_width, screen_height));
        this.setBackground(Color.decode("#6B5B95"));
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        newApple();
        slithering = true;
        timer = new Timer(delay, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (slithering) {
            // Draw the grid
            for (int i = 0; i < (screen_height / unit_size); i++) {
                g.drawLine(i * unit_size, 0, i * unit_size, screen_height);
                g.drawLine(0, i * unit_size, screen_width, i * unit_size);
            }
            g.setColor(Color.RED);
            g.fillOval(appleX, appleY, unit_size, unit_size);

            // Draw the snake
            for (int i = 0; i < bodyparts; i++) {
                g.setColor(Color.decode("#FF6F61"));
                g.fillRect(x[i], y[i], unit_size, unit_size);
            }
            // Draw the score
            g.setColor(Color.decode("#88B04B"));
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (screen_width - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }

    public void newApple() {
        appleX = random.nextInt(screen_width / unit_size) * unit_size;
        appleY = random.nextInt(screen_height / unit_size) * unit_size;
    }


    public void move() {
        for (int i = bodyparts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U':
                y[0] = y[0] - unit_size;
                break;
            case 'D':
                y[0] = y[0] + unit_size;
                break;
            case 'L':
                x[0] = x[0] - unit_size;
                break;
            case 'R':
                x[0] = x[0] + unit_size;
                break;
        }
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) { 
            bodyparts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        for (int i = bodyparts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                slithering = false;
            }
        }

        // Debugging: print snake's head position
        System.out.println("Snake head position: (" + x[0] + ", " + y[0] + ")");

        // Check if head touches frame boundaries
        if (x[0] < 0 || x[0] >= screen_width || y[0] < 0 || y[0] >= screen_height) {
            slithering = false;
        }

        if (!slithering) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {

        g.setColor(Color.RED);
        g.setFont(new Font("Dialog", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (screen_width - metrics.stringWidth("Game Over")) / 2, screen_height / 2);

        if (this.getComponentCount() == 0) {
            JButton button = new JButton("Play Again?");



            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    resetGame();
                    GamePanel.this.remove(button);
                }
            });

            this.add(button);
            this.revalidate();
        }

    }

    public void resetGame() {
        bodyparts = 6;
        applesEaten = 0;
        direction = 'R';
        slithering = true;

        for (int i = 0; i < bodyparts; i++) {
            x[i] = 0;
            y[i] = 0;
        }

        newApple();
        timer.restart();

        this.setFocusable(true);
        this.requestFocusInWindow();

        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (slithering) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }
}
