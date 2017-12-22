package Matchmaking;

import Banco.ThreadServinte;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Servidor {

    /*
    Processo central
    Mantém um BancoContas e lança threads quando recebe conexões
    Cada threads vai estar ligadas a um só cliente até ele fazer logout.
     */
    private ServerSocket serverSocket;
    private BancoContasJogadores banco;
    private ArrayList <Queue> queues;
    protected HashMap<String,Partida> partidas;

    public Servidor (){
        this.queues = new ArrayList<Queue>();
        for(int i = 0; i < 10; i++){
            queues.add(i, new Queue(i));
        }
        this.banco = new BancoContasJogadores(queues);
        this.partidas = new HashMap<String, Partida>();
    }
    public void servir() throws IOException {
        serverSocket = new ServerSocket(12345);
        System.out.println("A receber pedidos");
        while (true){
            Socket clientSocket;
            clientSocket = serverSocket.accept();
            System.out.println("Um pedido foi recebido");
            Matchmaking.ThreadServinte s = new Matchmaking.ThreadServinte(clientSocket, this.banco, partidas );
        }
    }
}
