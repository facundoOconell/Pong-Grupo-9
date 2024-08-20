import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Ellipse2D;

public class PongGame extends JPanel implements ActionListener, KeyListener {
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 600;
    private static final int PALETTE_WIDTH = 15;
    private static final int PALETTE_HEIGHT = 100;
    private static final int BALL_SIZE = 20;
    private static final int PALETTE_SPEED = 12;
    private static final int INITIAL_BALL_SPEED_X = 9;
    private static final int INITIAL_BALL_SPEED_Y = 9;
    private static final int WIN_SCORE = 7;
    private static final double SPEED_INCREASE_PERCENT = 0.05;
    private static final int BORDER_WIDTH = 10;
    private static final int FIRST_HALF_DURATION = 45;
    private static final int SECOND_HALF_DURATION = 45;
    private static final int BREAK_DURATION = 5;
    private static final int SAKE_COUNTDOWN_DURATION = 3; // 3 segundos para el saque
    private static final int POST_BREAK_DURATION = 5; // 5 segundos de espera después del medio tiempo

    private Timer gameTimer;
    private Timer countdownTimer;
    private Timer serveCountdownTimer;
    private Timer postBreakTimer; // Nuevo temporizador para la pausa después del medio tiempo
    private boolean pause = false; // Bandera para verificar si el juego está en pausa
    private int leftScore = 0;
    private int rightScore = 0;
    private int timeRemaining = FIRST_HALF_DURATION;
    private int serveCountdownRemaining = SAKE_COUNTDOWN_DURATION;
    private int postBreakCountdownRemaining = POST_BREAK_DURATION; // Contador para la pausa
    private boolean breakTime = false;
    private boolean firstHalfCompleted = false;
    private boolean showBall = true;
    private boolean isServing = false;
    private boolean lastGoalByLeftPlayer = true;
    private int random_saque = 1;
    private int last_saque_down=0;
    private int last_saque_up=0;

    private Rectangle leftPalette;
    private Rectangle rightPalette;
    private Ellipse2D ball;

    private double ballVelocityX = INITIAL_BALL_SPEED_X;
    private double ballVelocityY = INITIAL_BALL_SPEED_Y;

    private boolean moveLeftUp = false;
    private boolean moveLeftDown = false;
    private boolean moveRightUp = false;
    private boolean moveRightDown = false;

    private JLabel leftScoreLabel;
    private JLabel rightScoreLabel;
    private JLabel timerLabel;

    private GameState gameState = GameState.MAIN_MENU;

    public PongGame(JLabel leftScoreLabel, JLabel rightScoreLabel, JLabel timerLabel) {
        this.leftScoreLabel = leftScoreLabel;
        this.rightScoreLabel = rightScoreLabel;
        this.timerLabel = timerLabel;

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(20, 20, 20)); // Fondo oscuro
        setFocusable(true);
        addKeyListener(this);

        leftPalette = new Rectangle(30, HEIGHT / 2 - PALETTE_HEIGHT / 2, PALETTE_WIDTH, PALETTE_HEIGHT);
        rightPalette = new Rectangle(WIDTH - 30 - PALETTE_WIDTH, HEIGHT / 2 - PALETTE_HEIGHT / 2, PALETTE_WIDTH, PALETTE_HEIGHT);
        ball = new Ellipse2D.Double(WIDTH / 2 - BALL_SIZE / 2, HEIGHT / 2 - BALL_SIZE / 2, BALL_SIZE, BALL_SIZE);

        gameTimer = new Timer(1000 / 144, this);
        countdownTimer = new Timer(1000, e -> {
            if (timeRemaining > 0 && !isServing && !pause) {
                timeRemaining--;
                updateTimerLabel();
            } else if (breakTime) {
                startBreak();
            } else if (firstHalfCompleted) {
                endGame();
            } else {
                startBreak();
            }
        });

        serveCountdownTimer = new Timer(1000, e -> {
            if (serveCountdownRemaining > 0) {
                serveCountdownRemaining--;
                repaint();
            } else {
                serveCountdownTimer.stop();
                isServing = false;
                showBall = true;
                startBallDirection();
                countdownTimer.start();
            }
        });

        postBreakTimer = new Timer(1000, e -> {
            if (postBreakCountdownRemaining > 0) {
                postBreakCountdownRemaining--;
                repaint();
            } else {
                postBreakTimer.stop();
                postBreakCountdownRemaining = POST_BREAK_DURATION; // Reiniciar el contador
                startSecondHalf();
            }
        });

        countdownTimer.stop();
        gameTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        switch (gameState) {
            case MAIN_MENU:
                drawMainMenu(g2d);
                break;
            case PLAYING:
                drawPlaying(g2d);
                break;
            case GAME_OVER:
                drawGameOver(g2d);
                break;
        }

        if (isServing && gameState != GameState.GAME_OVER) {
            g.setColor(Color.WHITE);
            g.setFont(new Font(Font.DIALOG, Font.BOLD, 48));
            String countdownText = String.valueOf(serveCountdownRemaining);
            FontMetrics fm = g.getFontMetrics();
            int x = (WIDTH - fm.stringWidth(countdownText)) / 2;
            int y = (HEIGHT - fm.getHeight()) / 2;
            g.drawString(countdownText, x, y);
        }

        // Mostrar mensaje de pausa
        if (pause) {
            g.setColor(new Color(255, 255, 255, 128)); // Fondo semi-transparente
            g.fillRect(0, 0, WIDTH, HEIGHT);

            g.setColor(Color.WHITE);
            g.setFont(new Font(Font.DIALOG, Font.BOLD, 48));
            String pauseText = "¡PAUSADO!";
            FontMetrics fm = g.getFontMetrics();
            int x = (WIDTH - fm.stringWidth(pauseText)) / 2;
            int y = (HEIGHT - fm.getHeight()) / 2;
            g.drawString(pauseText, x, y);
        }
    }

    private void drawMainMenu(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.DIALOG, Font.BOLD, 36));
        String title = "¡Bienvenido a Pong!";
        FontMetrics fm = g.getFontMetrics();
        int x = (WIDTH - fm.stringWidth(title)) / 2;
        int y = (HEIGHT - fm.getHeight()) / 2;
        g.drawString(title, x, y);

        g.setFont(new Font(Font.DIALOG, Font.PLAIN, 24));
        String instruction = "Presiona 'Enter' para jugar";
        fm = g.getFontMetrics();
        x = (WIDTH - fm.stringWidth(instruction)) / 2;
        g.drawString(instruction, x, y + fm.getHeight() + 10);
    }

    private void drawPlaying(Graphics2D g) {
        g.setColor(new Color(100, 150, 200)); // Color para las paletas
        g.fill(leftPalette);
        g.setColor(new Color(225, 48, 42));
        g.fill(rightPalette);

        if (showBall) {
            g.setColor(new Color(200, 200, 200)); // Color para la pelota
            g.fill(ball);
        }

        // Línea central
        g.setColor(new Color(150, 150, 150)); // Línea central gris
        g.setStroke(new BasicStroke(2));
        g.drawLine(WIDTH / 2, 0, WIDTH / 2, HEIGHT);

        // Bordes superior e inferior
        g.setColor(new Color(80, 80, 80)); // Color de los bordes
        g.fillRect(0, 0, WIDTH, BORDER_WIDTH);
        g.fillRect(0, HEIGHT - BORDER_WIDTH, WIDTH, BORDER_WIDTH);
    }

    private void drawGameOver(Graphics2D g) {
        g.setColor(Color.RED);
        g.setFont(new Font(Font.DIALOG, Font.BOLD, 36));
        String winner = determineWinner();
        FontMetrics fm = g.getFontMetrics();
        int x = (WIDTH - fm.stringWidth(winner)) / 2;
        int y = (HEIGHT - fm.getHeight()) / 2;
        g.drawString(winner, x, y);

        g.setFont(new Font(Font.DIALOG, Font.PLAIN, 24));
        String instruction = "Presiona 'R' para reiniciar o 'Q' para salir";
        fm = g.getFontMetrics();
        x = (WIDTH - fm.stringWidth(instruction)) / 2;
        g.drawString(instruction, x, y + fm.getHeight() + 10);
    }

    private String determineWinner() {
        if (leftScore > rightScore) {
            return "¡Jugador 1 gana!";
        } else if (rightScore > leftScore) {
            return "¡Jugador 2 gana!";
        } else {
            return "¡Es un empate!";
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState == GameState.PLAYING && !pause && !isServing) {
            movePalettes();
            if (showBall) {
                moveBall();
                checkCollisions();
            }
            checkScores();
            repaint();
        }
    }

    private void moveBall() {
        ball.setFrame(ball.getX() + ballVelocityX, ball.getY() + ballVelocityY, BALL_SIZE, BALL_SIZE);

        // Rebote vertical en los bordes
        if (ball.getY() <= BORDER_WIDTH || ball.getY() >= HEIGHT - BALL_SIZE - BORDER_WIDTH) {
            ballVelocityY = -ballVelocityY;
        }

        // Rebote horizontal y anotación
        if (ball.getX() <= 0) {
            rightScore++;
            lastGoalByLeftPlayer = false;
            updateScoreLabels();
            // Solo iniciar el contador de saque si el juego no ha terminado
            if (gameState != GameState.GAME_OVER) {
                startServeCountdown();
            }
        } else if (ball.getX() >= WIDTH - BALL_SIZE) {
            leftScore++;
            lastGoalByLeftPlayer = true;
            updateScoreLabels();
            // Solo iniciar el contador de saque si el juego no ha terminado
            if (gameState != GameState.GAME_OVER) {
                startServeCountdown();
            }
        }
    }

    private void startServeCountdown() {
        if (gameState != GameState.GAME_OVER) {
            serveCountdownRemaining = SAKE_COUNTDOWN_DURATION;
            isServing = true;
            showBall = false;
            serveCountdownTimer.start();
            countdownTimer.stop();
        }
    }


    private static final double INITIAL_ANGLE = Math.toRadians(60); // Ángulo fijo en radianes

    private void startBallDirection() {
        // Determina la dirección horizontal de la pelota basada en el último gol
        random_saque=(int)Math.floor(Math.random()*1);
        if (lastGoalByLeftPlayer) {
            ballVelocityX = INITIAL_BALL_SPEED_X;
        } else {
            ballVelocityX = -INITIAL_BALL_SPEED_X;
        }
        
        if (random_saque==1||last_saque_up>=3) {
        	ballVelocityY=INITIAL_BALL_SPEED_Y;
        	last_saque_down+=1;
        	last_saque_up=0;
        }
        else if(random_saque==0||last_saque_down>=3) {
        	ballVelocityY=-INITIAL_BALL_SPEED_Y;
        	last_saque_up+=1;
        	last_saque_down=0;
        }

        resetBall(); // Coloca la pelota en el centro del campo
        repaint(); // Actualiza la vista
    }

    private void movePalettes() {
        if (moveLeftUp) {
            leftPalette.y -= PALETTE_SPEED;
        }
        if (moveLeftDown) {
            leftPalette.y += PALETTE_SPEED;
        }
        if (moveRightUp) {
            rightPalette.y -= PALETTE_SPEED;
        }
        if (moveRightDown) {
            rightPalette.y += PALETTE_SPEED;
        }

        // Asegurar que las paletas se mantengan dentro del área del juego
        if (leftPalette.y < BORDER_WIDTH) leftPalette.y = BORDER_WIDTH;
        if (leftPalette.y > HEIGHT - PALETTE_HEIGHT - BORDER_WIDTH) leftPalette.y = HEIGHT - PALETTE_HEIGHT - BORDER_WIDTH;
        if (rightPalette.y < BORDER_WIDTH) rightPalette.y = BORDER_WIDTH;
        if (rightPalette.y > HEIGHT - PALETTE_HEIGHT - BORDER_WIDTH) rightPalette.y = HEIGHT - PALETTE_HEIGHT - BORDER_WIDTH;
    }

    private void checkCollisions() {
        // Rebote en las paletas
        if (ball.getBounds2D().intersects(leftPalette) || ball.getBounds2D().intersects(rightPalette)) {
            ballVelocityX = -ballVelocityX;

            // Asegurar que la pelota no quede atascada en la paleta
            if (ball.getBounds2D().intersects(leftPalette)) {
                ball.setFrame(leftPalette.x + PALETTE_WIDTH, ball.getY(), BALL_SIZE, BALL_SIZE);
            } else if (ball.getBounds2D().intersects(rightPalette)) {
                ball.setFrame(rightPalette.x - BALL_SIZE, ball.getY(), BALL_SIZE, BALL_SIZE);
            }

            // Aumentar la velocidad en un 5% cada vez que la pelota rebote en una paleta
            ballVelocityX *= (1 + SPEED_INCREASE_PERCENT);
            ballVelocityY *= (1 + SPEED_INCREASE_PERCENT);
        }
    }

    private void checkScores() {
        // Verificar si algún jugador alcanzó el puntaje ganador con una ventaja de 2 goles
        if (leftScore >= WIN_SCORE || rightScore >= WIN_SCORE) {
            if (Math.abs(leftScore - rightScore) >= 2) {
                gameState = GameState.GAME_OVER;
                gameTimer.stop();
                countdownTimer.stop();
                repaint();
            }
        }
    }

    private void updateScoreLabels() {
        leftScoreLabel.setText("Jugador 1: " + leftScore);
        rightScoreLabel.setText("Jugador 2: " + rightScore);
    }

    private void updateTimerLabel() {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        timerLabel.setText(String.format("Tiempo Restante: %02d:%02d", minutes, seconds));
    }

    private void resetBall() {
        ball.setFrame(WIDTH / 2 - BALL_SIZE / 2, HEIGHT / 2 - BALL_SIZE / 2, BALL_SIZE, BALL_SIZE);
    }

    private void resetGame() {
        leftScore = 0;
        rightScore = 0;
        timeRemaining = FIRST_HALF_DURATION;
        breakTime = false;
        firstHalfCompleted = false;
        showBall = true;
        resetBall();
        startBallDirection();
        updateScoreLabels();
        updateTimerLabel();
        gameState = GameState.PLAYING;
        gameTimer.start();
        countdownTimer.start();
        serveCountdownTimer.stop(); // Detener el temporizador de saque en caso de reinicio
    }

    private void startBreak() {
        showBall = false;
        timeRemaining = BREAK_DURATION;
        updateTimerLabel();
        gameState = GameState.PLAYING;
        postBreakTimer.start(); // Inicia el temporizador de pausa después del medio tiempo
    }

    private void startSecondHalf() {
        timeRemaining = SECOND_HALF_DURATION;
        breakTime = false;
        firstHalfCompleted = true;
        updateTimerLabel();
        gameState = GameState.PLAYING;
        showBall = true; // Mostrar la pelota al comenzar la segunda mitad
        resetBall(); // Reiniciar la pelota al iniciar la segunda mitad
        startBallDirection(); // Asignar dirección inicial a la pelota
        countdownTimer.start(); // Reiniciar el temporizador del juego
    }

    private void endGame() {
        gameState = GameState.GAME_OVER;
        gameTimer.stop();
        countdownTimer.stop();
        serveCountdownTimer.stop(); // Asegúrate de detener el temporizador de saque
        repaint();
    }


    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameState == GameState.PLAYING) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    moveRightUp = true;
                    break;
                case KeyEvent.VK_DOWN:
                    moveRightDown = true;
                    break;
                case KeyEvent.VK_W:
                    moveLeftUp = true;
                    break;
                case KeyEvent.VK_S:
                    moveLeftDown = true;
                    break;
                case KeyEvent.VK_SPACE: // Pausar y reanudar el juego con la barra espaciadora
                    togglePause();
                    break;
            }
        } else if (gameState == GameState.MAIN_MENU) {
            if (e.getKeyCode() == KeyEvent.VK_P || e.getKeyCode() == KeyEvent.VK_ENTER) {
                resetGame();
                leftScoreLabel.setVisible(true);
                rightScoreLabel.setVisible(true);
                timerLabel.setVisible(true);
            }
        } else if (gameState == GameState.GAME_OVER) {
            if (e.getKeyCode() == KeyEvent.VK_R) {
                resetGame();
                leftScoreLabel.setVisible(true);
                rightScoreLabel.setVisible(true);
                timerLabel.setVisible(true);
            } else if (e.getKeyCode() == KeyEvent.VK_Q) {
                System.exit(0);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            moveRightUp = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            moveRightDown = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_W) {
            moveLeftUp = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            moveLeftDown = false;
        }
    }

    private void togglePause() {
        pause = !pause;
        if (pause) {
            gameTimer.stop();
            countdownTimer.stop();
        } else {
            gameTimer.start();
            countdownTimer.start();
        }
        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Juego de Pong");
        JLabel leftScoreLabel = new JLabel("Jugador 1: 0", SwingConstants.CENTER);
        JLabel rightScoreLabel = new JLabel("Jugador 2: 0", SwingConstants.CENTER);
        JLabel timerLabel = new JLabel("Tiempo Restante: 01:00", SwingConstants.CENTER);

        JPanel scorePanel = new JPanel();
        scorePanel.setPreferredSize(new Dimension(WIDTH, 50));
        scorePanel.setBackground(new Color(30, 30, 30)); // Fondo de panel de puntuación
        scorePanel.setLayout(new GridLayout(1, 3));

        leftScoreLabel.setForeground(Color.WHITE);
        rightScoreLabel.setForeground(Color.WHITE);
        timerLabel.setForeground(Color.WHITE);

        scorePanel.add(leftScoreLabel);
        scorePanel.add(timerLabel);
        scorePanel.add(rightScoreLabel);

        PongGame pongGame = new PongGame(leftScoreLabel, rightScoreLabel, timerLabel);

        frame.setLayout(new BorderLayout());
        frame.add(pongGame, BorderLayout.CENTER);
        frame.add(scorePanel, BorderLayout.NORTH);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        leftScoreLabel.setVisible(false);
        rightScoreLabel.setVisible(false);
        timerLabel.setVisible(false);
    }
}

enum GameState {
    MAIN_MENU,
    PLAYING,
    GAME_OVER
}
