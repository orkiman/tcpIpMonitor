import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Gui {
    private JPanel serverPanel;
    private JPanel clientPanel;
    private JLabel stateLable;
    private JButton serverRunButton;
    private JButton serverStopButton;
    private JTextField portTextField;
    private JPanel formPanel;
    private JScrollPane serverScrollPane;
    private JTextPane massageTextPane;
    ServerSocket serverSocket;

    public Gui() {
        serverRunButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (serverSocket == null){
                    int port = Integer.parseInt(portTextField.getText());
                    try (ServerSocket serverSocket = new ServerSocket(port)) {

                        System.out.println("Server is listening on port " + port);
                        StyledDocument doc = massageTextPane.getStyledDocument();
                        massageTextPane.getStyledDocument().insertString(doc.getLength(),"waiting for client",new SimpleAttributeSet());
                        new Thread(()->{
                            try {
                                Socket socket = serverSocket.accept();
                                massageTextPane.getStyledDocument().insertString(doc.getLength(),"new client connected",new SimpleAttributeSet());
                                InputStream inputStream = socket.getInputStream();
                                OutputStream outputStream = socket.getOutputStream();

                            } catch (IOException | BadLocationException ex) {
                                throw new RuntimeException(ex);
                            }
                        }).start();



                            Socket socket = serverSocket.accept();

                            System.out.println("New client connected");

                            OutputStream output = socket.getOutputStream();
                            PrintWriter writer = new PrintWriter(output, true);

                            writer.println(new Date().toString());
                        } catch (IOException | BadLocationException ex) {
                        throw new RuntimeException(ex);
                    }

                } catch (IOException ex) {
                        System.out.println("Server exception: " + ex.getMessage());
                        ex.printStackTrace();
                    }
//                    try(serverSocket = new ServerSocket(port)){
//                        serverSocket.accept();
//                    }catch (IOException ex) {
//                        System.out.println("Server exception: " + ex.getMessage());
//                        ex.printStackTrace();
//                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Gui");
        frame.setContentPane(new Gui().formPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
