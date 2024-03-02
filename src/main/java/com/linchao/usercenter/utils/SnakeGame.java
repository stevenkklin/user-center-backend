package com.linchao.usercenter.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

public class SnakeGame extends JFrame {
    private final int GRID_SIZE = 20;
    private final int CELL_SIZE = 25;
    private Point food = new Point(-1, -1); // 食物位置
    private LinkedList<Point> snake = new LinkedList<>(); // 蛇身体，包含点的列表
    private Point direction = new Point(0, 0); // 蛇移动的方向

    public SnakeGame() {
        setTitle("Snake Game");
        setSize(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 初始化蛇的位置
        snake.add(new Point(GRID_SIZE / 2, GRID_SIZE / 2));

        // 添加键盘监听器来控制蛇的方向
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        direction = new Point(0, -1);
                        break;
                    case KeyEvent.VK_DOWN:
                        direction = new Point(0, 1);
                        break;
                    case KeyEvent.VK_LEFT:
                        direction = new Point(-1, 0);
                        break;
                    case KeyEvent.VK_RIGHT:
                        direction = new Point(1, 0);
                        break;
                }
            }
        });

        // 设置定时器来更新游戏状态和重绘界面
        new Timer(100, e -> {
            updateGame();
            repaint();
        }).start();

        // 添加鼠标监听器来放置食物
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX() / CELL_SIZE;
                int y = e.getY() / CELL_SIZE;
                if (x >= 0 && x < GRID_SIZE && y >= 0 && y < GRID_SIZE) {
                    food.setLocation(x, y);
                }
            }
        });

        setFocusable(true);
    }

    private void updateGame() {
        if (direction.x == 0 && direction.y == 0) {
            return; // 如果方向未改变，不更新游戏状态
        }

        // 计算新头部位置
        Point head = snake.getFirst();
        Point newHead = new Point(head.x + direction.x, head.y + direction.y);

        // 检查新头部位置是否有效（不撞墙，不撞自己）
        if (newHead.x < 0 || newHead.y < 0 || newHead.x >= GRID_SIZE || newHead.y >= GRID_SIZE || snake.contains(newHead)) {
            JOptionPane.showMessageDialog(this, "Game Over");
            System.exit(0);
        }

        snake.addFirst(newHead);
        if (newHead.equals(food)) {
            food.setLocation(-1, -1); // 吃到食物后移除食物
        } else {
            snake.removeLast(); // 没有吃到食物，移动蛇身
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // 绘制蛇
        for (Point point : snake) {
            g.fillRect(point.x * CELL_SIZE, point.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }

        // 绘制食物
        if (food.x != -1 && food.y != -1) {
            g.fillOval(food.x * CELL_SIZE, food.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame frame = new SnakeGame();
            frame.setVisible(true);
        });
    }
}

