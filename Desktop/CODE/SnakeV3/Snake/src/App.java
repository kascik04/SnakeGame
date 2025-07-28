import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class App {
    private JFrame frame;
    private Clip backgroundMusic;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            App app = new App();
            app.showStartMenu();
        });
    }

    public void showStartMenu() {
        frame = new JFrame("Snake");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600,600);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        
        StartMenu startMenu = new StartMenu(this);
        frame.add(startMenu);
        frame.setVisible(true);
        

    }

    public void startGame() {
        frame.getContentPane().removeAll();
        SnakeGame snakeGame;
        try {
            snakeGame = new SnakeGame(600, 600);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        frame.add(snakeGame);
        frame.revalidate();
        snakeGame.requestFocus();
                // Load and play background music
                try {
                    File musicFile = new File("D:\\SnakeV3\\Snake\\src\\start.wav"); // Update the path to your music file
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(musicFile);
                    backgroundMusic = AudioSystem.getClip();
                    backgroundMusic.open(audioInputStream);
                    backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
                } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                    e.printStackTrace();
                }
            }
}
