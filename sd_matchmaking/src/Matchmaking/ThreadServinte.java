package Matchmaking;

import java.io.*;
import java.net.Socket;

public class ThreadServinte implements Runnable {
    /*
    Lida com o LinkCliente via socket. Corresponde a um LinkServidor.
    Recebe pedidos, valida credenciais, invoca métodos e comunica com o LinkCliente.
    Quando recebe um comando do LinkCliente, vai identificar e tomar as ações certas (switch).
    Os métodos operam sobre:
    -o BancoContas (para coisas sobre as contas dos jogadores)
    -uma Queue (para colocar o jogador com outros)
    -um Lobby (escolha dos heróis, avisar dos disponíveis e receber a escolha)

     */
    BufferedReader in;
    BufferedWriter out;
    Socket socket;
    BancoContasJogadores banco;
    /*
    deve precisar também das queues e dos lobbies (subir do @BancoContasJogadores para @Servidor).
    O servidor deve lançar uma thread quando o jogo estiver confirmado para correr,
    que vai montar o lobby e gerir as invocações de threads (cada uma comunica com o seu cliente para escolher o herói)
    Finalmente invoca o simulador
     */

    public ThreadServinte(Socket s, BancoContasJogadores b) {
        this.socket = s;
        this.banco = b;
    }

    @Override
    public void run(){

        /*
        Um comando a linha inteira (método agrumento1 argumento2 ...)
         */

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String linhainput = null, arg1 = null, arg2 = null, arg3 = null;
        int i = -1, j = -1;
        double d = -1;
        try {
            //receber o método
            linhainput = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //processar um comando
        while (linhainput != null) {
            switch (linhainput) {
                case "criarConta":
                    try {
                        //receber username
                        arg1 = in.readLine();
                        //receber password
                        arg2 = in.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //a conta foi criada e inserida com sucesso
                    if (banco.criarConta(arg1, arg2)){
                        try {
                            out.write(linhainput);
                            out.newLine();
                            out.write("sim");
                            out.newLine();
                            out.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
