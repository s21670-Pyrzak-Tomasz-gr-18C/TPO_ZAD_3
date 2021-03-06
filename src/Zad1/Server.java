package Zad1;

import com.sun.xml.internal.bind.v2.TODO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.*;


public class Server {

    public static void main(String[] args) throws IOException, InterruptedException {
        new Server();
    }

    Map<String, String> tags = new HashMap<>();

    Server() throws IOException {
        setExampleTagList();


        // Utworzenie kanału gniazda serwera
        // i związanie go z konkretnym adresem (host+port)
        String host = "localhost";
        int port = 12345;
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.socket().bind(new InetSocketAddress(host, port));

        // Ustalenie trybu nieblokującego
        // dla kanału serwera gniazda
        serverChannel.configureBlocking(false);

        // Utworzenie selektora
        Selector selector = Selector.open();

        // Rejestracja kanału gniazda serwera u selektora
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Serwer: czekam ... ");

        // Selekcja gotowych operacji do wykonania i ich obsługa
        // w pętli dzialania serwera
        while (true) {

            // Selekcja gotowej operacji
            // To wywolanie jest blokujące
            // Czeka aż selektor powiadomi o gotowości jakiejś operacji na jakimś kanale
            selector.select();

            // Teraz jakieś operacje są gotowe do wykonania
            // Zbiór kluczy opisuje te operacje (i kanały)
            Set<SelectionKey> keys = selector.selectedKeys();

            // Przeglądamy "gotowe" klucze
            Iterator<SelectionKey> iter = keys.iterator();

            while (iter.hasNext()) {

                // pobranie klucza
                SelectionKey key = iter.next();

                // musi być usunięty ze zbioru (nie ma autonatycznego usuwania)
                // w przeciwnym razie w kolejnym kroku pętli "obsłużony" klucz
                // dostalibyśmy do ponownej obsługi
                iter.remove();

                // Wykonanie operacji opisywanej przez klucz
                if (key.isAcceptable()) { // połaczenie klienta gotowe do akceptacji

                    System.out.println("Serwer: ktoś się połączył ..., akceptuję go ... ");
                    // Uzyskanie kanału do komunikacji z klientem
                    // accept jest nieblokujące, bo już klient czeka
                    SocketChannel cc = serverChannel.accept();

                    // Kanał nieblokujący, bo będzie rejestrowany u selektora
                    cc.configureBlocking(false);

                    // rejestrujemy kanał komunikacji z klientem
                    // do monitorowania przez ten sam selektor
                    cc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

                    continue;
                }

                if (key.isReadable()) {  // któryś z kanałów gotowy do czytania

                    // Uzyskanie kanału na którym czekają dane do odczytania
                    SocketChannel cc = (SocketChannel) key.channel();

                    serviceRequest(cc);

                    // obsługa zleceń klienta
                    // ...
                    continue;
                }
                if (key.isWritable()) {  // któryś z kanałów gotowy do pisania

                    // Uzyskanie kanału
                    //SocketChannel cc = (SocketChannel) key.channel();

                    // pisanie do kanału
                    // ...
                    continue;
                }

            }
        }

    }

    private void setExampleTagList() {
        tags.put("Moda", "wiadomość1");
        tags.put("Motoryzacja", "wiadomość1");
        tags.put("Polityka", "wiadomość1");
    }


    // Strona kodowa do kodowania/dekodowania buforów
    private static Charset charset = Charset.forName("ISO-8859-2");
    private static final int BSIZE = 1024;

    // Bufor bajtowy - do niego są wczytywane dane z kanału
    private ByteBuffer bbuf = ByteBuffer.allocate(BSIZE);

    // Tu będzie zlecenie do pezetworzenia
    private StringBuffer reqString = new StringBuffer();


    private void serviceRequest(SocketChannel sc) {
        if (!sc.isOpen()) return; // jeżeli kanał zamknięty

        System.out.print("Serwer: czytam komunikat od klienta ... ");
        // Odczytanie zlecenia
        reqString.setLength(0);
        bbuf.clear();

        try {
            readLoop:
            // Czytanie jest nieblokujące
            while (true) {               // kontynujemy je dopóki
                int n = sc.read(bbuf);   // nie natrafimy na koniec wiersza
                if (n > 0) {
                    bbuf.flip();
                    CharBuffer cbuf = charset.decode(bbuf);
                    while (cbuf.hasRemaining()) {
                        char c = cbuf.get();
                        //System.out.println(c);
                        if (c == '\r' || c == '\n') break readLoop;
                        else {
                            //System.out.println(c);
                            reqString.append(c);
                        }
                    }
                }
            }

            String[] request = reqString.toString().split(",");
            String cmd = request[0].toLowerCase();
            if (cmd.equals("hi")) {
                sc.write(charset.encode(CharBuffer.wrap("Hi," + createExampleTagList())));
                //TODO
                // jesli klient lub admin (get) lista dostepnych tematów - jesli tylko get, jesli get plus tematy - lista tematów plus informacje z mapy z tematami
                // jeśli nie klient (put, update) dzielimy zapytanie i wykonujemy odpowiednie metody addtemat update temat

            } else if (cmd.equals("add")) {
                String tagNameToAdd = request[1].substring(0,1).toUpperCase()+request[1].substring(1);
                if (!tags.containsKey(tagNameToAdd)) {
                    addNewTag(tagNameToAdd);
                    sc.write(charset.encode(CharBuffer.wrap("added," + createExampleTagList())));
                } else {
                    sc.write(charset.encode(CharBuffer.wrap("notadded," + createExampleTagList())));
                }
            }else if(cmd.equals("update")){
                String tagNameToUpdate = request[1].substring(0,1).toUpperCase()+request[1].substring(1);
                if(tags.containsKey(tagNameToUpdate)) {
                    updateTag(tagNameToUpdate, request[2]);
                    sc.write(charset.encode(CharBuffer.wrap("updated," + createExampleTagList())));
                }else{
                    sc.write(charset.encode(CharBuffer.wrap("notupdated," + createExampleTagList())));
                }
            } else if (cmd.equals("remove")) {
                String tagNameToRemove = request[1].substring(0,1).toUpperCase()+request[1].substring(1);
                    if(tags.containsKey(tagNameToRemove)) {
                        removeTag(tagNameToRemove);
                        sc.write(charset.encode(CharBuffer.wrap("removed," + createExampleTagList())));
                    }else{
                        sc.write(charset.encode(CharBuffer.wrap("notremoved," + createExampleTagList())));
                    }

            }else if(cmd.equals("get")){
                String tagNameToGet = request[1].substring(0,1).toUpperCase()+request[1].substring(1);
                if(tags.containsKey(tagNameToGet)){
                    System.out.println("wiadomość dla tematu:"+tagNameToGet+" - "+tags.get(tagNameToGet));
                    sc.write(charset.encode(CharBuffer.wrap("news,"+tags.get(tagNameToGet))+","+createExampleTagList()));
                }else{
                    sc.write(charset.encode(CharBuffer.wrap("bad request," + createExampleTagList())));
                }


            } else if (cmd.equals("Bye")) {           // koniec komunikacji

                sc.write(charset.encode(CharBuffer.wrap("Bye")));
                System.out.println("Serwer: mówię \"Bye\" do klienta ...\n\n");

                sc.close();                      // - zamknięcie kanału
                sc.socket().close();             // i gniazda
            }


                //TODO
                // stworzyć app dla admina i dla klienta (wykorzystac istniejącą - odpalamy kilku klientów tak jak w porzednim zadaniu)
                // server - należy stworzyć mapę temat:info (kilka przykładowych od razu na początku)
                // server -  metoda przetwarzająca reqest (na poczatku żądania potrzeba get, update lub put).
                // przekazujemy String z tematami przekazanymi przez button od admina-put, update lub klienta-get.
                // Administrator ma własną app z GUI - przyciski dodaj temat, aktualizuj temat, usun temat, zakończ połączenie (wysyłamy bye) .
                // okno gdzie wpisyje nazwę tematu do dodania, przyciski  addNew, update, delete. Przyciski dodają get add lub put
                // po połączeniu podobnie jak klient dostaje też listę istniejących tematów (checkbox(radiobuton) + update lub checkbox(radiobuton) + delete lub  okno z nazwą tematu + add)
                // server po put get update dzieli zapytania:
                // add - metoda dodaj temat
                // update - metoda aktualizuj wpis
                // get - metoda odczytaj wartość po kluczu
                // walidacja tematów (add- czy już istnieje, put-czy istnieje, get czy istnieje)
                // Klient ma własną app, okno do wyświetlania inf plus checkboxy z dostępnymi tematami
                //  po uruchomieniu aplikacji wyświetla mu się lista dostępnych tematów (przy odpaleniu pobieramy klucze z mapy wysyłamy tylko get),
                // jesli wybierze tematy plus przycisk wyświetl info, z checkboxów zczytujemy tematy i po naciśnieciu przycisku wysyłamy get do servera z tematami,
                // server odsyła wiadomość z inf przypisanymi do kluczy


        } catch (Exception exc) { // przerwane polączenie?
            exc.printStackTrace();
            try {
                sc.close();
                sc.socket().close();
            } catch (Exception e) {
            }
        }

    }

    private void removeTag(String tagName) {
       try {
               tags.remove(tagName);
       }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTag(String tagName, String tagNews) {
        try {
            if (tags.containsKey(tagName)) {
                tags.replace(tagName, tagNews);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void addNewTag(String tagName) {
        try {
            tags.put(tagName,"");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String createExampleTagList() {
        String lista = String.join(",", tags.keySet());
        return lista;
    }

}
