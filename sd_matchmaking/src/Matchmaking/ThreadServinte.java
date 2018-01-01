package Matchmaking;

import java.io.*;
import java.net.Socket;
import java.nio.channels.AsynchronousFileChannel;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadServinte implements Runnable {
    /*
    Lida com o LinkCliente via socket. Corresponde a um LinkServidor.
    Recebe pedidos, valida credenciais, invoca métodos e comunica com o LinkCliente.
    Quando recebe um comando do LinkCliente, vai identificar e tomar as ações certas (switch).
    Os métodos operam sobre:
    -o BancoContas (para coisas sobre as contas dos jogadores)
    -um GestorQueues (para colocar o jogador com outros)
    -uma Partida (indiretamente por mensagens: escolha dos heróis, avisar dos disponíveis e receber a escolha)

     */
    /*
    BufferedReader fromR; //lê do reader
    BufferedWriter toR, toW; //escreve para o reader ou writer
    */
    BufferedReader in;
    BufferedWriter out;
    Socket socket;
    BancoContasJogadores banco;
    Conta contaJogador;
    String username;
    ArrayBlockingQueue mensagens;
    ReentrantLock lock;

    /*
    O Writer não vai ter lógica implementada, apenas envia mensagens ao cliente. Estas podem ser provenientes
    de dois objetos
     */

    public ThreadServinte(Socket s, BancoContasJogadores b, GestorQueues queues, ArrayBlockingQueue mensagens) {
        this.socket = s;
        this.banco = b;
        this.contaJogador = null;
        this.username = null;
        this.mensagens = mensagens; //mensagens a serem lidas pelo GestorQueues (pedidos para jogar)
        this.lock = new ReentrantLock();
        /*
        this.w = new ThreadServinteWriter();
        this.fromR = w.getBuffer();
        this.r = new ThreadServinteReader(toW);
        this.toR = r.getBuffer();
        */
    }

    @Override
    public void run(){
        /*
        Um comando na linha inteira: (método agrumento1 argumento2 ...)
                                        (linhainput arg1 arg2 ...)
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
                    //também anota a conta associada ao cliente desta thread
                    if ((contaJogador = banco.criarConta(arg1, arg2)) != null){
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
                case "login":
                    try {
                        //receber username
                        arg1 = in.readLine();
                        //receber password
                        arg2 = in.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //faz login e anota a conta associada ao cloente desta thread
                    if((contaJogador = banco.login(arg1,arg2))!=null){
                        username=contaJogador.getUsername();
                        try {
                            out.write(linhainput);
                            out.newLine();
                            out.write("sim");
                            out.newLine();
                            out.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    };
                    break;
                case "logout":
                    contaJogador.logoutConta();


                    try {
                        out.write(linhainput);
                        out.newLine();
                        out.write("sim");
                        out.newLine();
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "consultarRank":
                    //Receber username. Pode consultar rank de outrs jogadores
                    try {
                        arg1 = in.readLine();
                        i =  banco.consultarRank(arg1);
                        out.write(linhainput);
                        out.newLine();
                        //responde com o rank do jogador procurado
                        out.write(String.valueOf(i));
                        out.newLine();
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "consultarPontos":
                    //Receber username. Pode consultar os pontos atuais de outrs jogadores
                    try {
                        arg1 = in.readLine();
                        i =  banco.consultarPontos(arg1);
                        out.write(linhainput);
                        out.newLine();
                        //responde com os pontos do jogador procurado
                        out.write(String.valueOf(i));
                        out.newLine();
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "jogar":
                    try {
                        mensagens.put(username);//envia pedido para jogar ao GestorQueues
                        /*
                        Tentativa de colocar um cancelamento à procura de um jogo, possível melhoria
                        while(!(arg2 = contaJogador.readToCliente()).equals("escolher")){

                            if (arg1.equals("cancelar")){
                                ReentrantLock lock = new ReentrantLock();
                                lock.lock();
                                mensagens.put(arg1);
                                mensagens.put(username);
                            }
                        }
                        */
                        //recebe um alerta de quando é para escolher, partida está pronta
                        contaJogador.readToCliente();
                        //avisa o jogador
                        out.write("começar");out.newLine();out.write("escolher");out.newLine();out.flush();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //Talvez não possa esperar uma resposta porque o 10º jogador vai assumir controlo da partida.
                    try {
                        out.write(linhainput);
                        out.newLine();
                        //responde com uma confirmação
                        out.write("");
                        out.newLine();
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "escolher":
                    try {
                        //responde com um champion. tem que levar locks para que as várias mensagens cheguem juntas
                        arg1 = in.readLine();
                        i=Integer.parseInt(arg1);
                        //envia o pedido à partida
                        lock.lock();
                        contaJogador.writeFromCliente(username);//username do jogador
                        contaJogador.writeFromCliente(String.valueOf(contaJogador.getNumeroJogador())); //número do jogador
                        contaJogador.writeFromCliente(in.readLine());//escolha do jogador
                        lock.unlock();
                        //recebe uma resposta, se tiver que escolher outro avisa o jogador
                        String resposta = contaJogador.readToCliente();
                        /*
                        * Como em todos os casos seria para enviar a resposta ao cliente, nem passa pelo switch
                        */
                        out.write("resposta");
                            out.newLine();
                            out.flush();
                        /*
                        switch (resposta){
                            case"escolhido":
                                out.write("escolhido");
                                out.newLine();
                                out.flush();
                                break;
                            case "outro":
                                out.write("outro");
                                out.newLine();
                                out.flush();
                                break;
                            case"ganhou":
                                out.write("ganhou");
                                out.newLine();
                                out.flush();
                                break;
                            case"perdeu":
                                out.write("perdeu");
                                out.newLine();
                                out.flush();
                                break;
                        }
                        */
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
            try {
                linhainput = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            /*
            Fim do switch, deve ser feito aqui primeiro uma nova leitura de in ou verificar comandos superiores?
             */
        }
        //Fim do while()
    }
}
