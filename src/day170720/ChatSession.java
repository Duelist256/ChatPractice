package day170720;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.function.Consumer;

public class ChatSession {
    private Socket socket;
    private String name;
    private final long delay;
    private PrintWriter writer;
    private Scanner scanner;

    public ChatSession(Socket socket, String name, long delay) {
        this.socket = socket;
        this.name = name;
        this.delay = delay;

        try {
            scanner = new Scanner(socket.getInputStream());
            writer = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void processConnection(Consumer<String> broadcaster,
                           Consumer<ChatSession> sessionRemover,
                           Consumer<String> messageGetter) {

        try {

            send2Client("/name " + name);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                System.out.println(line);

                if (line.startsWith("/nick ")) {
                    String oldName = name;
                    name = line.split(" ")[1];
                    broadcaster.accept("/nick " + oldName + " " + name);
                } else if (line.startsWith("/msg ")) {
                    String[] data = line.split(" ", 3);
                    String recipient = data[1];
                    String message = data[2];
                    messageGetter.accept("/msg " + name + " " + recipient + " " + message);

                    // fixme
                    send2Client("/mymsg " + " " + recipient + " " + message);
                } else {
                    broadcaster.accept(name + " > " + line);
                }

            }
            sessionRemover.accept(this);
            socket.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void send2Client(String line) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        writer.println(line);
        writer.flush();
    }

    public String getName() {
        return name;
    }
}