package Matchmaking;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

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
    /*
    BufferedReader fromR; //lê do reader
    BufferedWriter toR, toW; //escreve para o reader ou writer
    */
    Socket socket;
    BancoContasJogadores banco;
    Conta contaJogador;
    String username;
    HashMap<String,Partida> partidas;

    Thread r, w;
    /*
    O Writer não vai ter lógica implementada, apenas envia mensagens ao cliente. Estas podem ser provenientes
    de dois objetos
     */

    public ThreadServinte(Socket s, BancoContasJogadores b, HashMap <String, Partida> partidas) {
        this.socket = s;
        this.banco = b;
        this.contaJogador = null;
        this.partidas = partidas;
        this.username = null;
        /*
        this.w = new ThreadServinteWriter();
        this.fromR = w.getBuffer();
        this.r = new ThreadServinteReader(toW);
        this.toR = r.getBuffer();
        */
    }

    public BufferedReader getIn() {
        /*
        Serve para colocar processos do lado do servidor a ativar procedimentos (cases no switch)
        enquanto a ThreadServinte corre. O buffer vai ter mensagens provenientes da socket (cliente)
        e do próprio servidor.
         */
        return in;
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
            /*
            fazer uma espécie de pipe vindo da thread superior Partida, que vai sendo testada
            para verificar se tem que fazer alguma coisa extra
             */
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
                    banco.jogar(username);
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
                        out.write(linhainput);
                        out.newLine();
                        out.flush();
                        //responde com um champion
                        arg1 = in.readLine();
                        i=Integer.parseInt(arg1);
                        partidas.get(contaJogador.getIdPartida()).escolher(username,i);

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
