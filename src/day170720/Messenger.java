package day170720;


import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class Messenger {

    static Communicator chat;
    private static JTextArea textArea;
    private static JScrollPane sp;
    private static List userList;

    public static void main(String[] args) {

        JFrame frame = new JFrame("Чат");

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        LayoutManager manager = new BorderLayout();

        JPanel panel = new JPanel(manager);
        panel.setPreferredSize(new Dimension(400, 400));

        textArea = new JTextArea();
        textArea.setEditable(false);

        sp = new JScrollPane(textArea);

        panel.add(sp, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();

        JTextField textField = new JTextField(25);

        textField.addActionListener(e -> {
            sendText(textField);
        });

        inputPanel.add(textField);

        JButton sendButton = new JButton("Отправить");
        inputPanel.add(sendButton);

        sendButton.addActionListener(e -> {
            sendText(textField);
        });

        panel.add(inputPanel, BorderLayout.SOUTH);


        userList = new List(10, false);

        userList.addActionListener(e -> textField.setText(e.getActionCommand() + " "));

        panel.add(userList, BorderLayout.WEST);

        frame.add(panel);

        frame.pack();

        frame.setVisible(true);

        chat = new Communicator();
        chat.init(Messenger::processServerMessage);
    }

    private static void sendText(JTextField textField) {
        String text = textField.getText();
        textField.setText("");
        chat.sendTextToServer(text);
    }

    private static void processServerMessage(String text) {
        if (text.startsWith("/name")) {
            String[] words = text.split(" ");
            String userName = words[1];
            textArea.append("Welcome to chat, " + userName + "\n");
            return;
        }

        if (text.startsWith("/list")) {
            String[] names = text.split(" ");
            for (int i = 1; i < names.length; i++) {
                userList.add(names[i]);
            }
            return;
        }

        if (text.startsWith("/add")) {
            String[] words = text.split(" ");
            String userName = words[1];
            userList.add(userName);
            textArea.append(userName + " joined the chat\n");
            return;
        }

        if (text.startsWith("/remove")) {
            String[] words = text.split(" ");
            String userName = words[1];
            userList.remove(userName);
            textArea.append(userName + " left the chat\n");
            return;
        }

        if (text.startsWith("/nick")) {
            String[] words = text.split(" ");
            String oldName = words[1];
            String newName = words[2];

            String[] items = userList.getItems();
            int index = -1;
            for (int i = 0; i < items.length; i++) {
                if (items[i].equals(oldName)) {
                    index = i;
                    break;
                }
            }

            if (index != -1) {
                userList.replaceItem(newName, index);
            }

            return;
        }

        if (text.startsWith("/msg")) {
            String[] data = text.split(" ", 3);
            String sender = data[1];
            String message = data[2];
            textArea.append("[from: " + sender + "] > " + message + "\n");
            return;
        }

        if (text.startsWith("/mymsg")) {
            String[] data = text.split(" ", 3);
            String recipient = data[1];
            String message = data[2];
            textArea.append("[to: " + recipient + "] > " + message + "\n");
            return;
        }


        textArea.append(text + "\n");
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

}
