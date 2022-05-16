package Zad1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

public class AdminApp {

    int width = Toolkit.getDefaultToolkit().getScreenSize().width;
    int height = Toolkit.getDefaultToolkit().getScreenSize().height;


    JButton removeButton = new JButton();
    JButton addButton = new JButton();
    JButton updateButton = new JButton();
    JTextArea informationArea = new JTextArea();
    JTextField newTagName = new JTextField();
    JTextField newNews = new JTextField();
    JLabel addUpdate = new JLabel();
    JLabel addUpdate2 = new JLabel();
    JLabel tag = new JLabel();
    JLabel news = new JLabel();
    JLabel removeLabel = new JLabel();
    JLabel infoLabel = new JLabel();


    public AdminApp() {
        JFrame frame = new JFrame();
        frame.setLayout(null);
        frame.setTitle("TPO3 Zad 1");
        frame.setLocation(width / 4, height / 4);
        frame.setSize(1070, 430);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);


        frame.add(removeButton);
        frame.add(addButton);
        frame.add(updateButton);
        frame.add(informationArea);
        frame.add(newTagName);
        frame.add(newNews);
        frame.add(addUpdate);
        frame.add(addUpdate2);
        frame.add(tag);
        frame.add(news);
        frame.add(removeLabel);
        frame.add(infoLabel);

        updateButton.setBounds(650, 300, 180, 50);
        updateButton.setText("Edytuj wiadomość");

        removeButton.setBounds(850, 300, 180, 50);
        removeButton.setText("Usuń");

        removeLabel.setBounds(860, 360, 180, 15);
        removeLabel.setText("Usuń temat i wiadomość ");
        removeLabel.setFont(new Font("Calibri", Font.ITALIC, 12));

        newTagName.setBounds(50, 300, 180, 50);
        tag.setBounds(50, 360, 200, 15);
        tag.setText("Podaj nazwę tematu");
        tag.setFont(new Font("Calibri", Font.ITALIC, 12));

        newNews.setBounds(250, 300, 180, 50);
        news.setBounds(250, 360, 180, 15);
        news.setText("Wpisz treść wiadomości");
        news.setFont(new Font("Calibri", Font.ITALIC, 12));

        addUpdate.setBounds(450, 360, 180, 15);
        addUpdate2.setBounds(650, 360, 180, 15);
        addUpdate.setText("Dodaj nowy temat");
        addUpdate2.setText("Aktualizuj wiadomość");
        addUpdate.setFont(new Font("Calibri", Font.ITALIC, 12));
        addUpdate2.setFont(new Font("Calibri", Font.ITALIC, 12));

        addButton.setBounds(450, 300, 180, 50);
        addButton.setText("Nowy temat");

        infoLabel.setBounds(450, 200, 600, 50);
        infoLabel.setFont(new Font("Calibri", Font.BOLD, 25));


        addButton.addActionListener((ActionEvent e) -> {
            if (!newTagName.getText().equals("") && newTagName.getText() != null) {
                Admin.cbuf = CharBuffer.wrap("add," + newTagName.getText() + "\n");
                ByteBuffer outBuf = Admin.charset.encode(Admin.cbuf);
                try {
                    Admin.channel.write(outBuf);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                infoLabel.setText("Podano błędną nazwę tematu");
            }
        });

        updateButton.addActionListener((ActionEvent e) -> {
            if (!newNews.getText().equals("") && newNews.getText() != null
                    && !newTagName.getText().equals("") && newTagName.getText() != null) {
                Admin.cbuf = CharBuffer.wrap("update," + newTagName.getText() + "," + newNews.getText() + "\n");
                ByteBuffer outBuf = Admin.charset.encode(Admin.cbuf);
                try {
                    Admin.channel.write(outBuf);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                infoLabel.setText("Podano błędną nazwę tematu lub treść wiadomości");
            }

        });

        removeButton.addActionListener((ActionEvent e) -> {
            if (!newTagName.getText().equals("") && newTagName.getText() != null) {

                Admin.cbuf = CharBuffer.wrap("remove," + newTagName.getText() + "\n");
                ByteBuffer outBuf = Admin.charset.encode(Admin.cbuf);
                try {
                    Admin.channel.write(outBuf);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                infoLabel.setText("Podano błędną nazwę tematu");
            }

        });


        informationArea.setBounds(50, 50, 200, 200);
        // informationArea.setText("Dostępne Tematy "+tagsList);


    }


}