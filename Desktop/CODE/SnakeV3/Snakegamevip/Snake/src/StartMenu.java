import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class StartMenu extends JPanel {
    private JButton startButton;
    private JButton instructionsButton;
    private JButton exitButton;
    private JComboBox<String> difficultyComboBox;
    private JComboBox<String> snakeSkinComboBox;
    private App app;
    private BufferedImage backgroundImage;

    public StartMenu(App ap) {
        this.app = ap;
        setLayout(new GridBagLayout());

        // Load the background image
        try {
            backgroundImage = ImageIO.read(getClass().getResource(("/startgame.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load a custom font
        Font customFont = new Font("Arial", Font.BOLD, 24);

        // Common button styling
        Dimension buttonSize = new Dimension(250, 60);
        Color buttonBackgroundColor = new Color(51, 153, 255);  // Blue buttons
        Color buttonTextColor = Color.WHITE;
        Color buttonBorderColor = new Color(0, 102, 204);  // Darker blue border

        // Start Game button
        startButton = createButton("Start Game", customFont, buttonSize, buttonBackgroundColor, buttonTextColor, buttonBorderColor);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedDifficulty = (String) difficultyComboBox.getSelectedItem();
                String selectedSkin = (String) snakeSkinComboBox.getSelectedItem();
                app.startGame(selectedDifficulty, selectedSkin);
            }
        });

        // Instructions button
        instructionsButton = createButton("Instructions", customFont, buttonSize, buttonBackgroundColor, buttonTextColor, buttonBorderColor);
        instructionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showInstructions();
            }
        });

        // Exit button
        exitButton = createButton("Exit", customFont, buttonSize, buttonBackgroundColor, buttonTextColor, buttonBorderColor);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Difficulty ComboBox
        String[] difficulties = {"Easy", "Medium", "Hard"};
        difficultyComboBox = new JComboBox<>(difficulties);
        difficultyComboBox.setFont(customFont);
        difficultyComboBox.setPreferredSize(buttonSize);

        // Snake Skin ComboBox
        String[] skins = {"Classic", "Grey", "Orange"};
        snakeSkinComboBox = new JComboBox<>(skins);
        snakeSkinComboBox.setFont(customFont);
        snakeSkinComboBox.setPreferredSize(buttonSize);

        // Panel to hold choice boxes
        JPanel choicePanel = new JPanel(new GridLayout(2, 2, 10, 10)); // GridLayout for arrangement
        choicePanel.setOpaque(false); // Make it transparent
        choicePanel.add(new JLabel("Select Difficulty:"));
        choicePanel.add(new JLabel("Select Snake Skin:"));
        choicePanel.add(difficultyComboBox);
        choicePanel.add(snakeSkinComboBox);

        // Layout setup
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 0, 20, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(choicePanel, gbc); // Add choice panel to the main panel
        gbc.gridy++;
        add(startButton, gbc);
        gbc.gridy++;
        add(instructionsButton, gbc);
        gbc.gridy++;
        add(exitButton, gbc);
    }

    private JButton createButton(String text, Font font, Dimension size, Color bgColor, Color textColor, Color borderColor) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setPreferredSize(size);
        button.setFocusPainted(false);
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setBorder(BorderFactory.createLineBorder(borderColor, 3));
        return button;
    }

    private void showInstructions() {
        String instructions = "Use arrow keys to move the snake.\nPress Space to pause/resume.\nPress ESC to exit.\nEat potion to break a stone. \nEat poisoned apple make the snake shorter.";
        JOptionPane.showMessageDialog(this, instructions, "Instructions", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the background image
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        // Draw a gradient overlay
        Graphics2D g2d = (Graphics2D) g;
        Color color1 = new Color(34, 34, 34, 150);  // Semi-transparent overlay
        Color color2 = new Color(17, 17, 17, 150);  // Semi-transparent overlay
        int w = getWidth();
        int h = getHeight();
        GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);

        // Draw a title
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.setColor(Color.WHITE);
        String title = "SnakeGame";
        FontMetrics fm = g.getFontMetrics();
        int x = (w - fm.stringWidth(title)) / 2;
        int y = 100;
        g.drawString(title, x, y);

        // Draw text in the bottom-right corner
        String bottomRightText = "Created by NONAME";
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        FontMetrics fmBottomRight = g.getFontMetrics();
        int textWidth = fmBottomRight.stringWidth(bottomRightText);
        int textHeight = fmBottomRight.getHeight();
        int xBottomRight = w - textWidth - 10; // 10 pixels from the right edge
        int yBottomRight = h - textHeight + fmBottomRight.getAscent(); // Bottom edge alignment
        g.drawString(bottomRightText, xBottomRight, yBottomRight);
    }
}
