package Zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

public class Admin {
    static String input="";
    static AdminApp adminApp;
    static CharBuffer cbuf = null;
    static SocketChannel channel = null;
    static Charset charset  = Charset.forName("ISO-8859-2");

    public static void main(String[] args) throws IOException {

        adminApp = new AdminApp();


        String server = "localhost"; // adres hosta serwera
        int port = 12345; // numer portu

        try {
            // Utworzenie kanału
            channel = SocketChannel.open();

            // Ustalenie trybu nieblokującego
            channel.configureBlocking(false);

            // połączenie kanału
            channel.connect(new InetSocketAddress(server, port));

            System.out.print("Klient: łączę się z serwerem ...");

            while (!channel.finishConnect()) {
            }

        } catch(UnknownHostException exc) {
            System.err.println("Uknown host " + server);
            // ...
        } catch(Exception exc) {
            exc.printStackTrace();
            // ...
        }
        System.out.println("\nKlient: jestem połączony z serwerem ...");
        int rozmiar_bufora = 1024;
        ByteBuffer inBuf = ByteBuffer.allocateDirect(rozmiar_bufora);

        System.out.println("Admin: wysyłam - Hi");
        channel.write(charset.encode("Hi\n"));

        // pętla czytania
        while (true) {
            inBuf.clear();
            int readBytes = channel.read(inBuf);
            if (readBytes == 0) {
                continue;

            }
            else if (readBytes == -1) {
                break;
            }
            else {
                inBuf.flip();
                cbuf = charset.decode(inBuf);

                String odSerwera = cbuf.toString();

                System.out.println("Klient: serwer właśnie odpisał ... " + odSerwera);

                String[] response = odSerwera.split(",");
                String cmd = response[0].toLowerCase();
                if (cmd.equals("hi")) {
                    updateTaskListInfo(response);
                    updateInfoLabel("Wyświetlono wszystkie dostępne tematy wiadomości");
                } else if (cmd.equals("added")) {
                    updateTaskListInfo(response);
                    updateInfoLabel("Dodano nowy temat");
                } else if (cmd.equals("notadded")) {
                    updateTaskListInfo(response);
                    updateInfoLabel("Temat o podanej nazwie już istnieje");
                } else if (cmd.equals("removed")) {
                    updateInfoLabel("Usunięto wybrany temat oraz wiadomości");
                    updateTaskListInfo(response);
                } else if (cmd.equals("notremoved")) {
                    updateInfoLabel("Brak tematu o podanej nazwie");
                    updateTaskListInfo(response);
                } else if (cmd.equals("updated")){
                    updateInfoLabel("Zaktualizowano terść wiadomości dla wybranego tematu");
                    updateTaskListInfo(response);
                } else if (cmd.equals("notupdated")){
                    updateInfoLabel("Nie udało się zaktualizowano terśi wiadomości dla wybranego tematu");
                    updateTaskListInfo(response);
                }
                cbuf.clear();
                if (odSerwera.equals("Bye")) break;
            }
        }

    }
    private static void updateTaskListInfo(String[] response) {
        String tasksList ="";
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < response.length ; i++) {
            stringBuilder.append(response[i]);
            stringBuilder.append("\n");
        }
        tasksList = stringBuilder.toString();
       adminApp.informationArea.setText(tasksList);
    }
    private static void updateInfoLabel(String text) {
        adminApp.infoLabel.setText(text);
    }
}
