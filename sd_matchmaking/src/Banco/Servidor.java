package Banco;

import Banco.Banco;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    ServerSocket serverSocket;
    Banco b;
    public Servidor(Banco b){
        this.b = b;
    }

    //O servidor cria uma nova thread para responder a cada cliente.

    public void servir() throws IOException {
        serverSocket = new ServerSocket(12345);
        while (true){
            System.out.println("A receber novos pedidos");
            Socket clientSocket;
            clientSocket = serverSocket.accept();
            System.out.println("Um pedido foi recebido");
            ThreadServinte s = new ThreadServinte(clientSocket,b);
        }
    }
}
