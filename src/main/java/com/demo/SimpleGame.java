package com.demo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class SimpleGame extends JPanel implements ActionListener, KeyListener {

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

    public SimpleGame() {
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
        int numObstacles = level + 2;
        for (int i = 0; i < numObstacles; i++) {
            int x = (int) (Math.random() * (this.getWidth() - 50));
            int y = (int) (Math.random() * (this.getHeight() - 50));
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

    private void checkCollision() {
        if (playerX < targetX + targetSize && playerX + 20 > targetX &&
                playerY < targetY + targetSize && playerY + 20 > targetY) {
            score++;
            targetX = (int) (Math.random() * (this.getWidth() - targetSize));
            targetY = (int) (Math.random() * (this.getHeight() - targetSize));

            if (score % 5 == 0) {
                level++;
                playerSpeed++;
                generateObstacles();
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
        JFrame frame = new JFrame("Simple Game with Obstacles and Levels");
        SimpleGame game = new SimpleGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
