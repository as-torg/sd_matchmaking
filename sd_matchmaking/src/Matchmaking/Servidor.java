package Matchmaking;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class Servidor {

    /*
    Processo central
    Mantém um BancoContas e lança threads quando recebe conexões
    Cada threads vai estar ligadas a um só cliente até ele fazer logout.
     */
    private ServerSocket serverSocket;
    private BancoContasJogadores banco;
    private GestorQueues queues;
    private ArrayBlockingQueue mensagens;

    public Servidor (){
        this.banco = new BancoContasJogadores();
        this.mensagens = new ArrayBlockingQueue(20);//capacidade do buffer de pedidos
        this.queues = new GestorQueues(banco, mensagens);
    }
    public void servir() throws IOException {
        Thread gestor = new Thread(queues);
        gestor.run();
        System.out.println("Queues abertas\n");
        serverSocket = new ServerSocket(12345);
        System.out.println("A receber pedidos\n");
        while (true){
            Socket clientSocket;
            clientSocket = serverSocket.accept();
            System.out.println("Um pedido foi recebido\n");
            Matchmaking.ThreadServinte s = new Matchmaking.ThreadServinte(clientSocket, banco, queues, mensagens );
            Thread t = new Thread(s);
            t.start();
        }
    }
}
