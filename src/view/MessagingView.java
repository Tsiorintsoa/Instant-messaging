package view;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

public final class MessagingView {
    private static JTextField textField;
    private static JButton button;
    private static JFrame frame;
    private static JPanel messagePanel;

    private static final ImageIcon logoIcon = new ImageIcon("src/ressource/icon.png");
    private static final ImageIcon sendIcon = new ImageIcon("src/ressource/sendIcon.png");

    //hashmap pour gerer les couleurs box message et les avatar en random
    private static final Map<String, Color> USER_COLORS = new HashMap<>();
    private static final Map<String, ImageIcon> USER_AVATARS = new HashMap<>();
    private static final ImageIcon[] AVATARS = {
            new ImageIcon(new ImageIcon("src/ressource/avatar/1.jpg").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)),
            new ImageIcon(new ImageIcon("src/ressource/avatar/2.jpg").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)),
            new ImageIcon(new ImageIcon("src/ressource/avatar/3.jpg").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)),
            new ImageIcon(new ImageIcon("src/ressource/avatar/4.jpg").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH))
    };
    private static final Map<String, Color> COLOR_PALETTE = Map.of(
            "PRIMARY_COLOR", new Color(187, 180, 174),
            "SECONDARY_COLOR", new Color(147, 192, 234),
            "TERTIARY_COLOR", new Color(184, 238, 184)
    );

    public static void setLookAndFeel(LookAndFeel lookAndFeel) {
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (UnsupportedLookAndFeelException e) {
            System.out.println("Look and feel not supported, using default");
        }
    }

    public static String askUserName(String message, String title) {
        // Invisible parent, always on top
        JFrame parentFrame = new JFrame();
        parentFrame.setAlwaysOnTop(true);
        parentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        parentFrame.setIconImage(logoIcon.getImage());
        parentFrame.setUndecorated(true); // do not show

        parentFrame.setVisible(true);
        centerWithRandomOffset(parentFrame);

        String input;
        while (true) {
            input = JOptionPane.showInputDialog(parentFrame, message, title, JOptionPane.PLAIN_MESSAGE);

            //affichage d'un jdialogue pour lesser un choix a l'user de mettre un pseudo ou pas s'il ne saisie que dalle
            if (input == null || input.trim().isEmpty()) {
                int choice = JOptionPane.showConfirmDialog(
                        parentFrame,
                        "No input was provided. Do you want to continue with 'UNKNOWN'?",
                        "Input Required",
                        JOptionPane.YES_NO_OPTION
                );
                if (choice == JOptionPane.YES_OPTION) {
                    input = "UNKNOWN";
                    break;
                }
            } else {
                break;
            }
        }

        parentFrame.dispose();
        return input;
    }

    public static void createMainFrame(String title) {
        // Create non-resizable JFrame
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());
        frame.setBackground(COLOR_PALETTE.get("PRIMARY_COLOR"));

        //ajout d'un icon
        frame.setIconImage(logoIcon.getImage());

        centerWithRandomOffset(frame);

        // Upper part: for the messages
        messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(messagePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(scrollPane, BorderLayout.CENTER);

        // Lower part: textField and button
        JPanel inputPanel = new JPanel(new BorderLayout());
        textField = new JTextField();

        //ajout pour icon send redimessionné
        Image resizedImage = sendIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(resizedImage);

        button = new JButton(resizedIcon);
        button.setBackground(COLOR_PALETTE.get("SECONDARY_COLOR"));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputPanel.add(textField, BorderLayout.CENTER);
        inputPanel.add(button, BorderLayout.EAST);
        frame.add(inputPanel, BorderLayout.SOUTH);
        //mettre le bouton send par defaut
        frame.getRootPane().setDefaultButton(button);
        frame.setVisible(true);
    }

    // "Consumer" to pass a lambda as parameter
    public static void addListenerToButton(JButton button, Consumer<String> onSend) {
        button.addActionListener(e -> {
            String inputText = textField.getText().trim();
            if (!inputText.isEmpty()) {
                onSend.accept(inputText);
                textField.setText("");
            } else {
                JOptionPane.showMessageDialog(frame, "Empty text field...", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    public static void addStyledMessage(String message, String sender) {
        //deffinition des couleur et avatar
        Color boxColor = getUserColor(sender);
        ImageIcon avatar = getUserAvatar(sender);
        JPanel messageBox = createMessageBox(boxColor);

        // ajout des avatars, heure et couleur au message box
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        JLabel senderLabel = new JLabel("<html><b>" + sender + "</b>  <i>  " + time + "</i></html>", avatar, SwingConstants.LEFT);
        senderLabel.setHorizontalAlignment(SwingConstants.LEFT);

        // Add message body to messageBox
        JLabel messageLabel = new JLabel(message);
        messageLabel.setHorizontalAlignment(SwingConstants.LEFT);

        messageBox.add(senderLabel, BorderLayout.NORTH);
        messageBox.add(messageLabel, BorderLayout.CENTER);

        messagePanel.add(messageBox);
        messagePanel.add(Box.createRigidArea(new Dimension(0, 5))); // vertical gap

        // Refresh messagePanel
        messagePanel.revalidate();
        messagePanel.repaint();
    }

    //verifie si la couleur est deja attribuer et attribue une couleur
    private static Color getUserColor(String user) {
        return USER_COLORS.computeIfAbsent(user, k -> generateRandomColor());
    }

    //verifie si l'icon est deja attribuer et l'attribue
    private static ImageIcon getUserAvatar(String user) {
        return USER_AVATARS.computeIfAbsent(user, k -> AVATARS[new Random().nextInt(AVATARS.length)]);
    }

    //definie une couleur aleatoire
    private static Color generateRandomColor() {
        Random rand = new Random();
        return new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
    }

    private static void centerWithRandomOffset(JFrame component) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = component.getSize();

        // centered position
        int x = (screenSize.width - frameSize.width) / 2;
        int y = (screenSize.height - frameSize.height) / 2;

        // some random offset to avoid overlay
        Random random = new Random();
        int offsetX = random.nextInt(51) - 25;
        int offsetY = random.nextInt(51) - 25;

        // centering frame
        component.setLocation(x + offsetX, y + offsetY);
    }

    private static JPanel createMessageBox(Color bgColor) {
        JPanel messageBox = new JPanel(new BorderLayout());

        Dimension fixedSize = new Dimension(350, 60);
        messageBox.setPreferredSize(fixedSize);
        messageBox.setMaximumSize(fixedSize);
        messageBox.setMinimumSize(fixedSize);

        messageBox.setBackground(bgColor);
        messageBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return messageBox;
    }

    public static JTextField getTextField() {
        return textField;
    }

    public static JButton getButton() {
        return button;
    }

    public static JFrame getFrame() {
        return frame;
    }

    public static JPanel getMessagePanel() {
        return messagePanel;
    }
}
