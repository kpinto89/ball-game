package com.demo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class BallGame extends JPanel implements ActionListener, KeyListener {

    private Timer timer;
    private int playerX = 50;
    private int playerY = 50;
    private int playerSpeed = 10;
    private int targetX = 300;
    private int targetY = 200;
    private int targetSize = 30;
    private int score = 0;
    private int level = 1;
    private int timeLeft = 30; // 30 seconds timer
    private List<Rectangle> obstacles;

    public BallGame() {
        this.setFocusable(true);
        this.setPreferredSize(new Dimension(600, 400));
        this.setBackground(Color.BLACK);
        this.addKeyListener(this);
        timer = new Timer(30, this);
        timer.start();

        obstacles = new ArrayList<>();
        generateObstacles();

        // Timer for countdown
        new Timer(1000, e -> {
            if (timeLeft > 0) {
                timeLeft--;
            } else {
                timer.stop();
                JOptionPane.showMessageDialog(this, "Time's up! Final score: " + score, "Game Over", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }
        }).start();
    }

    private void generateObstacles() {
        obstacles.clear();
        int numObstacles = level + 2;  // Number of obstacles based on level
        System.out.println("Generating " + numObstacles + " obstacles...");

        for (int i = 0; i < numObstacles; i++) {
            int x = 0, y = 0;
            boolean overlap;
            int retryCount = 0;
            int maxRetries = 100;  // Max attempts to find a non-overlapping position

            // Try generating a new obstacle that doesn't overlap with existing ones
            do {
                overlap = false;
                x = (int) (Math.random() * (this.getWidth() - 50));  // Corrected obstacle width is 50
                y = (int) (Math.random() * (this.getHeight() - 50));  // Corrected obstacle height is 50

                // Ensure that x and y are within bounds
                if (x < 0) x = 0;
                if (y < 0) y = 0;

                // Check if the new obstacle intersects with any existing obstacles
                for (Rectangle existingObstacle : obstacles) {
                    if (new Rectangle(x, y, 50, 50).intersects(existingObstacle)) {
                        overlap = true;
                        break;  // Stop checking if an overlap is found
                    }
                }

                // Log the position attempt and overlap status
                System.out.println("Attempting position: (" + x + ", " + y + "), Overlap: " + overlap);

                retryCount++;

                if (retryCount > maxRetries) {
                    System.out.println("Max retries reached, placing obstacle anyway at: (" + x + ", " + y + ")");
                    overlap = false;  // Break the loop if we reach max retries
                }

            } while (overlap);  // Retry if there is an overlap

            // Log success
            System.out.println("Placed obstacle at: (" + x + ", " + y + ")");

            // Add the non-overlapping obstacle to the list
            obstacles.add(new Rectangle(x, y, 50, 50));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw player
        g.setColor(Color.GREEN);
        g.fillOval(playerX, playerY, 20, 20);

        // Draw target
        g.setColor(Color.RED);
        g.fillRect(targetX, targetY, targetSize, targetSize);

        // Draw obstacles
        g.setColor(Color.BLUE);
        for (Rectangle obstacle : obstacles) {
            g.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
        }

        // Draw score and level
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Level: " + level, 10, 40);
        g.drawString("Time Left: " + timeLeft + "s", 10, 60);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        checkCollision();
        repaint();
    }

    private void generateTarget() {
        boolean overlap;
        do {
            // Generate a random target position
            targetX = (int) (Math.random() * (this.getWidth() - targetSize));
            targetY = (int) (Math.random() * (this.getHeight() - targetSize));

            overlap = false;

            // Check if the target overlaps with any obstacle
            for (Rectangle obstacle : obstacles) {
                if (new Rectangle(targetX, targetY, targetSize, targetSize).intersects(obstacle)) {
                    overlap = true;
                    break;  // Stop checking if an overlap is found
                }
            }
        } while (overlap);  // Retry if there is an overlap
    }

    private void checkCollision() {
        if (playerX < targetX + targetSize && playerX + 20 > targetX &&
                playerY < targetY + targetSize && playerY + 20 > targetY) {
            score++;
            generateTarget();  // Ensure target doesn't overlap with obstacles

            if (score % 5 == 0) {
                level++;
                playerSpeed++;
                generateObstacles();  // Generate new obstacles at a higher level
            }
        }

        // Check collision with obstacles
        for (Rectangle obstacle : obstacles) {
            if (playerX < obstacle.x + obstacle.width && playerX + 20 > obstacle.x &&
                    playerY < obstacle.y + obstacle.height && playerY + 20 > obstacle.y) {
                JOptionPane.showMessageDialog(this, "You hit an obstacle! Game Over.", "Game Over", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                playerY = Math.max(playerY - playerSpeed, 0);
                break;
            case KeyEvent.VK_DOWN:
                playerY = Math.min(playerY + playerSpeed, this.getHeight() - 20);
                break;
            case KeyEvent.VK_LEFT:
                playerX = Math.max(playerX - playerSpeed, 0);
                break;
            case KeyEvent.VK_RIGHT:
                playerX = Math.min(playerX + playerSpeed, this.getWidth() - 20);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Ball Game with Obstacles and Levels");
        BallGame game = new BallGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
