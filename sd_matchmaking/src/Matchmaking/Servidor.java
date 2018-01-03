package Matchmaking;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;


public class Servidor {

    /*
    Processo central
    Mantém um BancoContas e lança threads quando recebe conexões
    Cada threads vai estar ligadas a um só cliente até ele fazer logout.
     */
    public static void main(String[]args) throws IOException {
        BancoContasJogadores banco = new BancoContasJogadores();
        ArrayBlockingQueue mensagens = new ArrayBlockingQueue(20);//capacidade do buffer de pedidos
        GestorQueues queues = new GestorQueues(banco, mensagens);
        Thread gestor = new Thread(queues);
        gestor.run();
        System.out.println("Queues abertas\n");
        ServerSocket serverSocket = new ServerSocket(12345);
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
