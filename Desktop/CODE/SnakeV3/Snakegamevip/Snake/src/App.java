import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

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
        frame.setSize(40*15+13,40*15+85);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        
        StartMenu startMenu = new StartMenu(this);
        frame.add(startMenu);
        frame.setVisible(true);
        

    }

    public void startGame(String difficulty, String snakeSkin) {
        frame.getContentPane().removeAll();
        SnakeGame snakeGame;
        try {
            snakeGame = new SnakeGame(40*15,40*15,difficulty, snakeSkin);
            System.out.println(snakeSkin);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        frame.add(snakeGame);
        frame.revalidate();
        snakeGame.requestFocus();
                // Load and play background music
                if(difficulty =="Easy"){
                    try {
                        URL resource = SnakeGame.class.getClassLoader().getResource("start.wav");
                        File musicFile = new File(resource.getFile());
                        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(musicFile);
                        backgroundMusic = AudioSystem.getClip();
                        backgroundMusic.open(audioInputStream);
                        backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
                    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                        e.printStackTrace();
                    }
                }
                else if(difficulty == "Medium"){
                    try {
                        URL resource = SnakeGame.class.getClassLoader().getResource("level2.wav");
                        File musicFile = new File(resource.getFile());
                        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(musicFile);
                        backgroundMusic = AudioSystem.getClip();
                        backgroundMusic.open(audioInputStream);
                        backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
                    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                        e.printStackTrace();
                }
            }
                else{
                    try {
                        URL resource = SnakeGame.class.getClassLoader().getResource("level3.wav");
                        File musicFile = new File(resource.getFile());
                        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(musicFile);
                        backgroundMusic = AudioSystem.getClip();
                        backgroundMusic.open(audioInputStream);
                        backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
                    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                        e.printStackTrace();
                }
                }
            }
}
