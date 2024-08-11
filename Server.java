import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Server extends JFrame implements ActionListener, Runnable {
    JTextField tf1;
    JTextArea ta1;
    JScrollPane p1;
    JButton b1;
    ServerSocket serverSocket;
    Socket socket;
    PrintWriter pw;
    BufferedReader br;

    public Server() {
        // Set up GUI layout
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tf1 = new JTextField();
        ta1 = new JTextArea();
        p1 = new JScrollPane(ta1);
        b1 = new JButton("SEND");

        // Add components to the frame
        add(tf1, BorderLayout.SOUTH);
        add(p1, BorderLayout.CENTER);
        add(b1, BorderLayout.EAST);

        // Configure frame properties
        setResizable(false);
        ta1.setEditable(false);
        setTitle("Chat Server");
        setSize(400, 300);
        setVisible(true);

        // Set up server socket
        try {
            serverSocket = new ServerSocket(4000);
            socket = serverSocket.accept(); // Accept client connection

            pw = new PrintWriter(socket.getOutputStream(), true);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Add action listeners
        b1.addActionListener(this);
        tf1.addActionListener(this);

        // Start a thread to handle incoming messages
        new Thread(this).start();
    }

    public void run() {
        while (true) {
            try {
                // Read incoming message
                String text = br.readLine();
                LocalDateTime now = LocalDateTime.now();
                String time = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

                // Update the chat area on the Event Dispatch Thread
                SwingUtilities.invokeLater(() -> ta1.append(text + "\t-senders " + time + "\n"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void actionPerformed(ActionEvent ae) {
        // Send message
        String text = tf1.getText();
        LocalDateTime now = LocalDateTime.now();
        String time = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        ta1.append(text + "\t-me " + time + "\n");
        pw.println(text);
        tf1.setText("");
    }

    public static void main(String[] args) {
        new Server();
    }
}