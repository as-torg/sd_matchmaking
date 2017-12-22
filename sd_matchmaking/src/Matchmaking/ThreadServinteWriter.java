package Matchmaking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;

public class ThreadServinteWriter implements Runnable {
    private BufferedReader fromReaderGestor;
    private BufferedWriter toCliente;
    public ThreadServinteWriter(BufferedWriter toCliente, BufferedReader fromReaderGestor){
        this.fromReaderGestor = fromReaderGestor;
        this.toCliente = toCliente;

    }

    @Override
    public void run() {

    }
}
