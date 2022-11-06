import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Gui {
    private JLabel serverStateLable;
    private JButton serverRunButton;
    private JTextField portTextField;
    private JPanel formPanel;
    private JTextPane serverMassageTextPane;
    private JTextField serverSendTextField;
    private JButton serverSendButton;
    private JTextField clientTargetServerIpTextField;
    private JButton clientConnectButton;
    private JTextField clientSendTextField;
    private JButton sendButton;
    private JTextPane clientMassagesTextPane;
    private JLabel clientStateLable;
    private JCheckBox HEXDisplayCheckBox;
    private OutputStream serverOutputStream;
    private OutputStream clientOutputStream;


    public Gui() {

        serverRunButton.addActionListener(e -> new Thread(server).start());
        serverSendButton.addActionListener(e -> {
            try {
                serverOutputStream.write(serverSendTextField.getText().getBytes());
                serverOutputStream.write(System.lineSeparator().getBytes());
                serverOutputStream.flush();
                Style outStyle = serverMassageTextPane.getStyle("outStyle");
                StyledDocument doc = serverMassageTextPane.getStyledDocument();
                doc.insertString(doc.getLength(), "out : " + serverSendTextField.getText() + System.lineSeparator(), outStyle);
            } catch (IOException | BadLocationException ex) {
                throw new RuntimeException(ex);
            }
        });
        clientConnectButton.addActionListener(e -> new Thread(client).start());
        sendButton.addActionListener(e -> {
            try {
                clientOutputStream.write(clientSendTextField.getText().getBytes());
                clientOutputStream.write(System.lineSeparator().getBytes());
                clientOutputStream.flush();
                Style outStyle = clientMassagesTextPane.getStyle("outStyle");
                StyledDocument doc = clientMassagesTextPane.getStyledDocument();
                doc.insertString(doc.getLength(), "out : " + clientSendTextField.getText() + System.lineSeparator(), outStyle);
            } catch (IOException | BadLocationException ex) {
                throw new RuntimeException(ex);
            }


        });

    }


    Runnable server = new Runnable() {
        @Override
        public void run() {
            int port = Integer.parseInt(portTextField.getText());
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                serverRunButton.setEnabled(false);
                portTextField.setEnabled(false);
                Style massagesStyle = serverMassageTextPane.addStyle("massagesStyle", null);
                StyleConstants.setForeground(massagesStyle, new Color(0, 100, 0));
                StyleConstants.setFontSize(massagesStyle, 20);
                Style inStyle = serverMassageTextPane.addStyle("inStyle", null);
                StyleConstants.setForeground(inStyle, new Color(0, 0, 0));
                StyleConstants.setFontSize(inStyle, 20);
                Style outStyle = serverMassageTextPane.addStyle("outStyle", null);
                StyleConstants.setForeground(outStyle, new Color(0, 0, 150));
                StyleConstants.setFontSize(outStyle, 20);
                serverStateLable.setText("waiting for client");
                StyledDocument doc = serverMassageTextPane.getStyledDocument();
                doc.insertString(doc.getLength(), "waiting for client on address " + serverSocket.getInetAddress() + System.lineSeparator(), massagesStyle);
                Socket socket = serverSocket.accept(); // blocks
                serverSendButton.setEnabled(true);
                doc.insertString(doc.getLength(), "new client connected : " +
                        socket.getInetAddress() + System.lineSeparator(), massagesStyle);
                serverStateLable.setText("connected");
                InputStream inputStream = socket.getInputStream();
                serverOutputStream = socket.getOutputStream();
                StyleConstants.setForeground(massagesStyle, Color.black);

                while (true) {
//                    String s = Character.toString((char) inputStream.read());
                    String s;
                    int input = inputStream.read();
                    if (HEXDisplayCheckBox.isSelected()) {
                        s="<"+input+">";
                    } else {
                        s = Character.toString((char) input);
                    }
                    doc.insertString(doc.getLength(), s, inStyle);
                }
            } catch (IOException | BadLocationException e) {
                throw new RuntimeException(e);
            }
        }
    };

    Runnable client = new Runnable() {

        @Override
        public void run() {
            try (Socket socket = new Socket(clientTargetServerIpTextField.getText(), Integer.parseInt(portTextField.getText()))) {
                Style massagesStyle = clientMassagesTextPane.addStyle("massagesStyle", null);
                StyleConstants.setForeground(massagesStyle, new Color(0, 100, 0));
                StyleConstants.setFontSize(massagesStyle, 20);
                Style inStyle = clientMassagesTextPane.addStyle("inStyle", null);
                StyleConstants.setForeground(inStyle, new Color(0, 0, 0));
                StyleConstants.setFontSize(inStyle, 20);
                Style outStyle = clientMassagesTextPane.addStyle("outStyle", null);
                StyleConstants.setForeground(outStyle, new Color(0, 0, 150));
                StyleConstants.setFontSize(outStyle, 20);
                StyledDocument doc = clientMassagesTextPane.getStyledDocument();
                doc.insertString(doc.getLength(), "connected to Server at " + socket.getInetAddress() + System.lineSeparator(), massagesStyle);
                clientStateLable.setText("connected");
                InputStream inputStream = socket.getInputStream();
                clientOutputStream = socket.getOutputStream();
                StyleConstants.setForeground(massagesStyle, Color.black);
                clientConnectButton.setEnabled(false);

                while (true) {
                    String s;
                    int input = inputStream.read();
                    if (HEXDisplayCheckBox.isSelected()) {
                        s="<"+input+">";
                    } else {
                        s = Character.toString((char) input);
                    }
                    doc.insertString(doc.getLength(), s, inStyle);
                }
            } catch (IOException | BadLocationException e) {
                throw new RuntimeException(e);
            }
        }
    };

    public static void main(String[] args) {
        JFrame frame = new JFrame("Gui");
        frame.setContentPane(new Gui().formPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(1200, 700);
        frame.setVisible(true);
    }

}
