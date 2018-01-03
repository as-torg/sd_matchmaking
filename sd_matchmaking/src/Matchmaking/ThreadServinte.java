package Matchmaking;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
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
    private BufferedReader in;
    private BufferedWriter out;
    private Socket socket;
    private BancoContasJogadores banco;
    private Conta contaJogador;
    private String username;
    private ArrayBlockingQueue mensagens;
    private ReentrantLock lock;

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
        String linhainput = "false", arg1 = null, arg2 = null;
        int i = -1, j = -1;
        try {
            //receber o método
            linhainput = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        antes de fazer qualquer coisa tem que ser feito login numa conta
        o pedido de login chega com o formato: "login'\n'username'\n'password'\n'"
         */
        boolean login = false;
        while(!login){
            //o comando tem que ser "login"
            while(!login) {
                if(linhainput.equals("login")){
                    //tenta fazer login
                    try {
                        //receber username
                        arg1 = in.readLine();
                        //receber password
                        arg2 = in.readLine();
                        //faz login e anota a conta associada ao cloente desta thread
                        if((contaJogador = banco.login(arg1,arg2))!=null){
                            username=contaJogador.getUsername();
                            login = true;
                            try {
                                out.write("login\nsim\n");
                                out.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else{//se o login falha
                            try {
                                out.write("login\nfalhou\n");
                                out.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                else if (linhainput.equals("criarConta")){
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
                            login = true;
                            out.write(linhainput);
                            out.newLine();
                            out.write("sim");
                            out.newLine();
                            out.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //o username já existe
                    else{
                        try {
                            out.write(linhainput);
                            out.newLine();
                            out.write("nao\n");
                            out.write(arg1);
                            out.newLine();
                            out.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                try {
                    out.write("nosessao\n");
                    out.flush();
                    linhainput=in.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        /*
        ######################################################
        Login feito com sucesso
        ######################################################
         */

        //processar um comando
        while (linhainput != null) {
            if((arg1 = contaJogador.readToCliente())!=null) linhainput = arg1;
            switch (linhainput) {
                case "logout":
                    contaJogador.logoutConta();
                    try {
                        out.write(linhainput);
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
                        arg1 = in.readLine();//username
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

                        //se o jogador já estiver na queue
                        if(contaJogador.isInQueue()){
                            out.write("inqueue\n");out.flush();
                        }
                        //ou numa partida não pode procurar um jogo novo
                        if(contaJogador.getIdPartida()!=-1){
                            out.write("ingame\n");out.flush();
                        }
                        //envia pedido para jogar ao GestorQueues
                        mensagens.put(username);
                        out.write("jogar\n");out.flush();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                    break;
                case "escolher":
                    try {
                        //responde com um champion. tem que levar locks para que as várias mensagens cheguem juntas
                        arg1 = in.readLine();
                        i=Integer.parseInt(arg1);
                        //envia o pedido à partida e atualiza o cliente com as escolhas atuais
                        contaJogador.escolherChampion(i);
                        mensagens.put("updateEquipa"+(contaJogador.getNumeroEquipa()+1)); //get dá 0 ou 1
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    /*//implementação antiga, problemas por causa do sincronismo
                    try {
                        lock.lock();
                        contaJogador.writeFromCliente(username);//username do jogador
                        contaJogador.writeFromCliente(String.valueOf(contaJogador.getNumeroJogador())); //número do jogador
                        contaJogador.writeFromCliente(in.readLine());//escolha do jogador
                        lock.unlock();
                        //recebe uma resposta, se tiver que escolher outro avisa o jogador
                        String resposta = contaJogador.readToCliente();
                        /*
                        * Como em todos os casos seria para enviar a resposta ao cliente, nem passa pelo switch
                        *
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
                        *
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                    break;
                case "setRank": //isto só existe para abrir contas logo com ranks diferentes nos testes pontuais
                    //"setRank 4"
                    try {
                        contaJogador.setRank(Integer.valueOf(in.readLine()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
