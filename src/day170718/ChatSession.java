package day170718;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.function.Consumer;

public class ChatSession {
    private final String name;
    private final long delay;
    private PrintWriter writer;

    public ChatSession(String name, long delay) {
        this.name = name;
        this.delay = delay;
    }

    void processConnection(Socket socket, Consumer<String> broadcaster,
                           Consumer<ChatSession> sessionRemover) {

        try {
            Scanner scanner = new Scanner(socket.getInputStream());

            writer = new PrintWriter(socket.getOutputStream());

            send2Client("/name " + name);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                System.out.println(line);
                broadcaster.accept(name + " > " + line);
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
}