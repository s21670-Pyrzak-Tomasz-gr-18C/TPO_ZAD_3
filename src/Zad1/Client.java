package Zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

public class Client {
    static String input="";
    static ClientApp clientApp;
    static CharBuffer cbuf = null;
    static SocketChannel channel = null;
    static Charset charset  = Charset.forName("ISO-8859-2");

    public static void main(String[] args) throws IOException {

        clientApp = new ClientApp();


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
        Scanner scanner = new Scanner(System.in);
        int rozmiar_bufora = 1024;
        ByteBuffer inBuf = ByteBuffer.allocateDirect(rozmiar_bufora);



        System.out.println("Klient: wysyłam - Hi"); // TODO dla klienta i admina wysyłamy  tylko get przy połączeniu żeby otrzymać listę tematów, dla admina
        // "Powitanie" do serwera
        channel.write(charset.encode("Hi\n"));

        // pętla czytania
        while (true) {
            inBuf.clear();	// opróżnienie bufora wejściowego
            int readBytes = channel.read(inBuf); // czytanie nieblokujące

            if (readBytes == 0) {

                continue;

            }
            else if (readBytes == -1) { // kanał zamknięty po stronie serwera
                // dalsze czytanie niemożlwe
                // ...
                break;
            }
            else {		// dane dostępne w buforze
                //System.out.println("coś jest od serwera");

                inBuf.flip();

                cbuf = charset.decode(inBuf);

                String odSerwera = cbuf.toString();

                System.out.println("Klient: serwer właśnie odpisał ... " + odSerwera);
                //TODO tu przy połączeniu dostaniemy listę tematów, metoda wysyłająca do gui tematy check-boxy (radio-buttony)

                String[] response = odSerwera.split(",");
                String cmd = response[0].toLowerCase();
                if (cmd.equals("hi")) {
                 updateTaskListInfo(response,1);

                } else if (cmd.equals("news")) {
                  clientApp.newsArea.setText(response[1]);
                    updateTaskListInfo(response,2);

                } else if (cmd.equals("bad request")){
                    updateInfoLabel("Nie znaleziono wiadomości dla podanego tematu");
                    updateTaskListInfo(response,1);
                }

                cbuf.clear();
                if (odSerwera.equals("Bye")) break;
            }
        }

        scanner.close();

    }
    private static void updateTaskListInfo(String[] response, int index) {
        String tasksList ="";
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = index; i < response.length ; i++) {
            stringBuilder.append(response[i]);
            stringBuilder.append("\n");
        }
        tasksList = stringBuilder.toString();
        clientApp.informationArea.setText(tasksList);
    }

    private static void updateInfoLabel(String text) {
        ClientApp.newsArea.setText(text);
    }
}

