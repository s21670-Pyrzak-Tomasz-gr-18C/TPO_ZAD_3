package Zad1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

public class ClientApp {

    int width = Toolkit.getDefaultToolkit().getScreenSize().width;
    int height = Toolkit.getDefaultToolkit().getScreenSize().height;

    JButton showNews = new JButton();

      JButton refreschButton = new JButton();
      static JTextArea informationArea = new JTextArea();
      JTextField newTagName = new JTextField();
      static JTextArea newsArea = new JTextArea();

      JLabel tag = new JLabel();


    public ClientApp() {
        JFrame frame = new JFrame();
        frame.setLayout(null);
        frame.setTitle("TPO3 Zad 1 Client");
        frame.setLocation(width / 4, height / 4);
        frame.setSize(1070, 430);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);

        frame.add(showNews);
        frame.add(informationArea);
        frame.add(refreschButton);
        frame.add(newTagName);
        frame.add(newsArea);

        showNews.setBounds(570, 300, 200, 50);
        showNews.setText("Najnowsze wiadomości");

        showNews.addActionListener((ActionEvent e)->{
            if (!newTagName.getText().equals("") && newTagName.getText() != null) {
                Client.cbuf = CharBuffer.wrap("get," + newTagName.getText() + "\n");
                ByteBuffer outBuf = Client.charset.encode(Client.cbuf);
                try {
                    Client.channel.write(outBuf);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }else{
                newsArea.setText("Podano niewłaściwą nazwę tematu");
            }

        });

        informationArea.setBounds(50, 50, 200, 200);

        refreschButton.setBounds(820, 300, 200, 50);
        refreschButton.setText("Odświerz liste tematów");

        refreschButton.addActionListener((ActionEvent e)->{
            Client.cbuf = CharBuffer.wrap("hi,\n");
            ByteBuffer outBuf = Client.charset.encode(Client.cbuf);
            try {
                Client.channel.write(outBuf);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        newTagName.setBounds(50,300,200,50);
        tag.setBounds(50,360,200,15);
        tag.setText("Podaj nazwę tematu");
        tag.setFont(new Font("Calibri", Font.ITALIC, 12));

        newsArea.setBounds(300,50,720,200);

    }


}
