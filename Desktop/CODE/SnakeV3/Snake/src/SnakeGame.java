import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.io.File;
// import java.util.Calendar;
public class SnakeGame extends JPanel implements ActionListener, KeyListener{
    
    private class Tile{
        int x;
        int y;
        Tile(int x, int y){
            this.x = x;
            this.y = y;
        }
    }
    int boardWidth;
    int boardHeight;
    int tileSize = 40;
    // Calendar calendar;
    Tile snakeHead;
    ArrayList<Tile> snakeBody;
    ArrayList<Tile> wall_list;

    BufferedImage buffered;
    BufferedImage apple;
    BufferedImage power_up_img;
    BufferedImage stone_img;
    BufferedImage background_img;
    ArrayList<BufferedImage> dest = new ArrayList<BufferedImage>();
    ArrayList<BufferedImage> imgPartSnake = new ArrayList<BufferedImage>();
    Tile food;
    Tile powerUp;
    Random random;
    BufferedImage tamp;
    Timer gameloop;
    Timer wall_cooldown;
    int velocityX;
    int velocityY;
    int idxHeadImg;
    int angle = 0;
    int angleBody = 0;
    boolean gameOver = false;
    boolean isPaused = false;
    boolean[][] wall = new boolean[15][15]; 
    int score = 0;
    int count_wall = 0;
    int initialDelay = 200;
    int delayDecrease = 5;
    int powerUpDuration = 10000; // Power-up duration in milliseconds
    boolean hasPowerUp = false;
    Timer powerUpTimer;
    GameThread gameThread;
	TimerThread timerThread;
	int timer = 0;
    private Clip eatSoundClip;
    SnakeGame(int boardWidth, int boardHeight) throws IOException{
        
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);
        // calendar = Calendar.getInstance();
        timerThread = new TimerThread(); // Initialize TimerThread
        timerThread.start(); // Start the timer thread
        snakeHead = new Tile(5,5);
        snakeBody = new ArrayList<>();
        snakeBody.add(new Tile(5,4));
        idxHeadImg = 0;
        food = new Tile(10, 10);
        random = new Random();
        powerUp = new Tile(random.nextInt(boardWidth / tileSize), random.nextInt(boardHeight / tileSize));

        placeFood();
        velocityX = 0;
        velocityY = 1;
        // wall = new boolean[boardWidth/tileSize+1][boardHeight/tileSize+1];
        wall_list = new ArrayList<>();
        buffered = ImageIO.read(getClass().getResource("/snake.png"));
        apple = ImageIO.read(getClass().getResource("/apple.png"));
        power_up_img = ImageIO.read(getClass().getResource("/power_up.png"));
        stone_img = ImageIO.read(getClass().getResource("/stone.png"));
        background_img = ImageIO.read(getClass().getResource("/background.png"));

        for(int i = 0; i < 3; i++){
            for(int j = 0; j <3; j++){
                dest.add(buffered.getSubimage(i*42, j*42, 42, 42));
            }
        }

        gameloop = new Timer(200, this);
        gameloop.start();
        gameThread = new GameThread(100);
		timerThread = new TimerThread();
        timerThread.start();
        // gameThread.start();

    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public int part(Tile snake){
        int i = 0;
        if(snake.x==0 && snake.y == 1)
        {
            i = 1;
        }
        else if (snake.x==-1 && snake.y==0)
        {
            i = 2;
        }
        else if (snake.x==1 && snake.y==0)
        {
            i = 3;
        }


        return i; // 0 sang trai 1 sang phai 2 di len 3 di xuong
    }
    public BufferedImage rotate(BufferedImage img, double angle){
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage rotated = new BufferedImage(w, h, img.getType());
        Graphics2D g = rotated.createGraphics();
        g.rotate(Math.toRadians(angle),w/2,h/2);
        g.drawImage(img, null, 0,0);
        g.dispose();
        return rotated;

    }

    // public static BufferedImage imgRotated(){
    //     if()
    // }
    public BufferedImage getSnakeImage(int i, int angle) {
        return rotate(dest.get(i), angle);
    }
    public BufferedImage getSnakeBodyImage2(Tile tile1, Tile tile2, int i){
                int x = tile1.x - tile2.x;
                int y = tile1.y - tile2.y;
                BufferedImage tail = null;
                if (x ==0 && y == -1)
                    {
                        tail = rotate(dest.get(i), 180);
                    }
                    else if(x ==0 && y == 1)
                    {
                        tail = dest.get(i);
                    }
                    else if (x==-1 && y==0)
                    {
                        tail = rotate(dest.get(i), 90);
                    }
                    else
                    {
                        tail = rotate(dest.get(i), -90);
                    }
                    return tail;

    }
    public BufferedImage getSnakeBodyImage(Tile tile1, Tile tile2, Tile tile3){
        int i = tile2.x - tile1.x;
        int j = tile2.y - tile1.y;

        int x = tile2.x - tile3.x;
        int y = tile2.y - tile3.y;

        if ((i==x && x==0) || (j==y && y==0))
        {
            return getSnakeBodyImage2(tile2,tile1,8);
        }
        else if(i == 0 && j == 1)
        {
            if(x==1)
            {
            return getSnakeImage(7,0);
            }
            else
            {
            return getSnakeImage(4,0);
            }
        }
        else if(i == 0 && j == -1)
        {
            if(x==1)
            {
            return getSnakeImage(6,0);
            }
            else
            {
            return getSnakeImage(3,0);
            }
        }
        else if(i == 1 && j == 0)
        {
            if(y==1)
            {
            return getSnakeImage(7,0);
            }
            else
            {
            return getSnakeImage(6,0);
            }
        }
        else
        {
            if(y==1)
            {
            return getSnakeImage(4,0);
            }
            else
            {
            return getSnakeImage(3,0);
            }
        }
    }

    public void draw(Graphics g) {

        g.drawImage(background_img, 0, 0, boardWidth, boardHeight, null);
        
        // for(int i = 0; i<boardWidth/tileSize; i++){
        //     g.drawLine(i*tileSize, 0, i*tileSize, boardHeight);
        //     g.drawLine(0, i*tileSize, boardWidth, i*tileSize);
        // }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, boardWidth - 120, 30);
        g.drawString("Time: " + timer, 20, 30); // Display timer at top left

        g.drawImage(power_up_img, powerUp.x * tileSize+1, powerUp.y * tileSize+1, tileSize-2, tileSize-2, null);

        g.setColor(Color.red);
        for(int i = 0 ; i< wall_list.size(); i++){
            g.drawImage(stone_img, wall_list.get(i).x*tileSize, wall_list.get(i).y*tileSize,tileSize, tileSize, null);

        }
        // g.fillRect(food.x*tileSize, food.y*tileSize, tileSize, tileSize);
        g.drawImage(apple, food.x*tileSize, food.y*tileSize, tileSize, tileSize, null);
        
        // g.setColor(Color.green);
        // g.fillRect(snakeHead.x*tileSize, snakeHead.y*tileSize, tileSize, tileSize);
        // g.drawImage(buffered,snakeHead.x*tileSize,snakeHead.y*tileSize,tileSize,tileSize,null);
        // for(int i = 0; i <snakeBody.size(); i++){
        //     Tile snakePart = snakeBody.get(i);
        //     g.fillRect(snakePart.x*tileSize, snakePart.y*tileSize, tileSize, tileSize);
        // }
        for(int i = snakeBody.size()-1; i>=0; i--){
            Tile snakePart = snakeBody.get(i);
            if(i==0) {
                if (snakeBody.size()==1)
                g.drawImage(getSnakeBodyImage2(snakePart,snakeHead,5), snakePart.x*tileSize, snakePart.y*tileSize, tileSize+2, tileSize+2, null);
                else
                {
                g.drawImage(getSnakeBodyImage(snakeHead,snakePart,snakeBody.get(i+1)), snakePart.x*tileSize, snakePart.y*tileSize, tileSize+2, tileSize+2, null);

                }
                

            } else if (i!= snakeBody.size()-1){
                g.drawImage(getSnakeBodyImage(snakeBody.get(i-1),snakePart,snakeBody.get(i+1)), snakePart.x*tileSize, snakePart.y*tileSize, tileSize+2, tileSize+2, null);

            }   else {
                g.drawImage(getSnakeBodyImage2(snakePart,snakeBody.get(i-1),5), snakePart.x*tileSize, snakePart.y*tileSize, tileSize+2, tileSize+2, null);


            }
        }
        // g.drawImage(dest.get(1), snakeHead.x*tileSize, snakeHead.y*tileSize, tileSize, tileSize, null);
        g.drawImage(getSnakeImage(idxHeadImg++,angle), snakeHead.x*tileSize, snakeHead.y*tileSize, tileSize+2, tileSize+2, null);
        // g.dispose();
        if(idxHeadImg > 2)
        idxHeadImg = 0;
        // System.out.println(calendar.getTime().getSeconds());
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, boardWidth - 120, 30);
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Time: " + timer, boardWidth /2, boardHeight - 20);

        if (isPaused) {
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            FontMetrics metrics = g.getFontMetrics(g.getFont());
            String message = "Press Space to resume";
            int x = (boardWidth - metrics.stringWidth(message)) / 2;
            int y = (boardHeight - metrics.getHeight()) / 2;
            g.drawString(message, x, y);
        }

        // Vẽ thông báo kết thúc trò chơi
        if (gameOver) {
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            FontMetrics metrics = g.getFontMetrics(g.getFont());

            String gameOverMessage = "Game Over!";
            String scoreMessage = "Your Score is: " + score + " in: " + timer + " seconds!";
            String restartMessage = "Press Space to restart";

            int gameOverX = (boardWidth - metrics.stringWidth(gameOverMessage)) / 2;
            int scoreX = (boardWidth - metrics.stringWidth(scoreMessage)) / 2;
            int restartX = (boardWidth - metrics.stringWidth(restartMessage)) / 2;

            int centerY = boardHeight / 2;
            int lineHeight = metrics.getHeight();

            g.drawString(gameOverMessage, gameOverX, centerY - lineHeight);
            g.drawString(scoreMessage, scoreX, centerY);
            g.drawString(restartMessage, restartX, centerY + lineHeight);
        }

    }


    

    public boolean collision(int x, int y, Tile tile2){
        return x == tile2.x && y == tile2.y;
    }

    public void placeFood() {
        boolean validPosition = false;
        while (!validPosition) {
            int x = random.nextInt(boardWidth / tileSize);
            int y = random.nextInt(boardHeight / tileSize);
            validPosition = true;

            // Check for collision with snake body, snake head, power-up, and walls
            for (Tile part : snakeBody) {
                if (part.x == x && part.y == y) {
                    validPosition = false;
                    break;
                }
            }
            if (collision(x, y, snakeHead) || collision(x, y, powerUp) || wall[x][y]) {
                validPosition = false;
                continue; // Try again if collision detected
            }

            if (validPosition) {
                food.x = x;
                food.y = y;
            }
        }
    }


    public boolean check_snake(int x, int y)
    {
        for(int i = 0; i <snakeBody.size(); i++)
            {
                if(collision(x, y, snakeBody.get(i)))
                {
                    return true;
                }
            }

        if(collision(x, y, snakeHead))
        {
            return true;
        }
        return false;
    }
    public boolean check_road(int x, int y){
        if (x < 0 || x >= boardWidth/tileSize || y < 0 || y >= boardHeight/tileSize)
            return true;
        int count = 0;
        if (x > 0 && x < boardWidth/tileSize && y >= 0 && y < boardHeight/tileSize)
            if (!wall[x-1][y] && !check_snake(x-1, y))
                count += 1;
        if (x >= 0 && x < boardWidth/tileSize-1 && y >= 0 && y < boardHeight/tileSize)
            if (!wall[x+1][y] && !check_snake(x+1, y))
                count += 1;
        if (x >= 0 && x < boardWidth/tileSize && y > 0 && y < boardHeight/tileSize)
            if (!wall[x][y-1] && !check_snake(x, y-1))
                count += 1;
        if (x >= 0 && x < boardWidth/tileSize && y >= 0 && y < boardHeight/tileSize-1)
            if (!wall[x][y+1] && !check_snake(x, y+1))
                count += 1;
        if (count < 2)
            return false;
    
        return true;
    }

    public boolean check_wall(int x, int y){
    
        wall[x][y] = true;
        if (check_road(x-1, y) && check_road(x+1, y) && check_road(x, y-1) && check_road(x, y+1)) {
            return true;
        } else {
            wall[x][y] = false;
            return false;
        }
    }


    public void placeWall(){
        if(count_wall > 70)
        return;
        int x = random.nextInt(boardHeight/tileSize);
        int y = random.nextInt(boardHeight/tileSize);
        if (wall[x][y] == true)
        placeWall();
        else if(check_wall(x,y) && !check_snake(x, y))
        {
            count_wall++;
            wall_list.add(new Tile(x,y));
        }
        else placeWall();
    }

    public void checkPowerUp() {
        if (snakeHead.x == powerUp.x && snakeHead.y == powerUp.y) {
            score += 2; // Update score for eating power-up
            hasPowerUp = true;
            placePowerUp();

            // Slow down the game speed temporarily
            gameloop.setDelay(gameloop.getDelay() + 100);

            // Schedule power-up timer to revert the speed back after duration
            if (powerUpTimer != null) {
                powerUpTimer.stop();
            }
            powerUpTimer = new Timer(powerUpDuration, e -> {
                hasPowerUp = false;
                gameloop.setDelay(Math.max(gameloop.getDelay() - 100, 50)); // Ensure minimum speed
            });
            powerUpTimer.setRepeats(false);
            powerUpTimer.start();
        }
    }

    public void placePowerUp() {
        boolean validPosition = false;
        while (!validPosition) {
            int x = random.nextInt(boardWidth / tileSize);
            int y = random.nextInt(boardHeight / tileSize);
            validPosition = true;
            for (Tile part : snakeBody) {
                if (part.x == x && part.y == y) {
                    validPosition = false;
                    break;
                }
            }
            if(collision(x, y, snakeHead) || collision(x, y, food)|| wall[x][y])
            {
                validPosition = false;
                break;
            }
            if (validPosition) {
                powerUp.x = x;
                powerUp.y = y;
            }
        }
    }
    public void prepareSounds() {
        try {
            // Tải âm thanh từ tệp
            File soundFile = new File("D:\\SnakeV3\\Snake\\src\\eatsound.mp3");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);

            // Tạo Clip từ âm thanh đã tải
            eatSoundClip = AudioSystem.getClip();
            eatSoundClip.open(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void move(){
        if (collision(snakeHead.x, snakeHead.y, food))
        {
            snakeBody.add(new Tile(food.x, food.y));
            score += 5;
            System.out.print(score);
            if(score % 5 == 0)
            {
                placeWall();
            }
            placeFood();
            playEatSound();
        }

        for(int i = snakeBody.size()-1; i>=0; i--){
            Tile snakePart = snakeBody.get(i);
            if(i==0) {
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            } else {
                Tile prevSnakePart = snakeBody.get(i-1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        for(int i = 0 ; i < snakeBody.size(); i++){
            Tile snakePart = snakeBody.get(i);
            if(collision(snakePart.x, snakePart.y, snakeHead)){
                gameOver = true;
            }
        }
        if(snakeHead.x*tileSize < 0 || snakeHead.x*tileSize > boardWidth ||
        snakeHead.y*tileSize < 0 || snakeHead.y*tileSize > boardHeight ){
            gameOver = true;
        }
        if (snakeHead.x >= 0 && snakeHead.x < boardWidth/tileSize && snakeHead.y >= 0 && snakeHead.y < boardHeight/tileSize)
        if(wall[snakeHead.x][snakeHead.y])
        gameOver = true;
        // repaint();
    }
    
    public void playEatSound() {
        if (eatSoundClip != null) {
            eatSoundClip.setFramePosition(0); // Đặt lại vị trí của Clip về đầu
            eatSoundClip.start(); // Bắt đầu phát âm thanh
        }
    }

    

    public void pauseGame() {
        gameloop.stop();
        // gameThread.suspend();
		// timerThread.suspend();
		// repaint();
    }
    
    public void resumeGame() {
        if (!gameOver) {
            isPaused = false;
            gameloop.start();
			// timerThread.resume();
        }
    }

    public void restart() {
        snakeHead = new Tile(5, 5);
        snakeBody.clear();
        snakeBody.add(new Tile(5, 4));
        idxHeadImg = 0;
        score = 0;
        velocityX = 0;
        velocityY = 1;
        angle = 0; // Reset angle to default
        placeFood();
        placePowerUp();
        gameOver = false;
        isPaused = false;
        wall = new boolean[15][15];
        wall_list.clear();
        gameloop.setDelay(initialDelay); // Reset game speed
        gameloop.start();
        timer = 0;
        // timerThread.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        checkPowerUp();
        repaint();
        if(gameOver || isPaused){
            gameloop.stop();
            // timerThread.stop();

        }
        else
        {
            gameloop.start();
        }
    }


    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_UP && velocityY!=1){
            velocityX = 0;
            velocityY = -1;
            angle = 180;
        }
        else if(e.getKeyCode() == KeyEvent.VK_DOWN && velocityY!=-1){
            velocityX = 0;
            velocityY = 1;
            angle = 0;
        }
        else if(e.getKeyCode() == KeyEvent.VK_LEFT && velocityX!=1){
            velocityX = -1;
            velocityY = 0;
            angle = 90;
        }
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX!=-1){
            velocityX = 1;
            velocityY = 0;
            angle = -90;
        }
        else if (e.getKeyCode() == KeyEvent.VK_P) { // Phím P để tạm dừng
            pauseGame();
        } else if (e.getKeyCode() == KeyEvent.VK_R) { // Phím R để tiếp tục
            resumeGame();
        }  else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (gameOver) {
                restart();
            } else {
                isPaused = !isPaused;
            }
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) System.exit(0);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
    @Override
    public void keyReleased(KeyEvent e) {
    }
    class GameThread extends Thread {
        private int sleepTime;
        private volatile boolean running = true;

        GameThread(int level) {
            if (level == 1) {
                this.sleepTime = 140;
            } else if (level == 2) {
                this.sleepTime = 70;
            } else if (level == 3) {
                this.sleepTime = 40;
            }
        }

        public void stopThread() {
            running = false;
        }

        public void run() {
            while (running) {
                try {
                    Thread.sleep(sleepTime);
                    repaint();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    class TimerThread extends Thread {
        private volatile boolean running = true;

        TimerThread() {
        }

        public void stopThread() {
            running = false;
        }

        public void run() {
            while (running) {
                try {
                    if (gameOver) {
                        stopThread();
                    }
                    Thread.sleep(1000); // Update timer every second
                    timer++; // Increment timer value
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}