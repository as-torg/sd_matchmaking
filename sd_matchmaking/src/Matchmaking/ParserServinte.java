package Matchmaking;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class ParserServinte implements Runnable {
    /*
    Tal como no parser do cliente, esta classe serve para fazer push de notificações de forma assíncrona.
    Esta thread vai estar permanentemente a interpretar mensagens vindas do sistema tais como "escolher", ou "timeout"
    Evita-se assim que a ThreadServinte fique impossibilitada de receber comandos por estar bloqueada a esperar notificações
    */

    private Conta conta;
    private BufferedWriter out;
    public ParserServinte(Conta conta, BufferedWriter toCliente){
        this.conta = conta;
        this.out = toCliente;
    }
    @Override
    public void run() {
        while(conta.isSessao()){
            String mensagem = conta.readToCliente();
            switch (mensagem){
                case "timeout":
                    try {
                        out.write("timeout\n");
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "escolherAgora":
                    try {
                        //avisa o jogador que o jogo começou e já pode escolher
                        out.write("escolher\n");
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "ganhou":
                    try {
                        out.write("ganhou\n");
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "perdeu":
                    try {
                        out.write("perdeu\n");
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "ingame":
                    try {
                        out.write("ingame\n");
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case"inqueue":
                    try {
                        out.write("inqueue\n");
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "updateEquipa":
                    ArrayList<Integer> escolhasAtuais = conta.getEscolhas();
                    try {
                        out.write("updateEquipa\n");
                        for (Integer a :escolhasAtuais) {//ocorre 5 vezes, o cliente lê 5 vezes e atualiza no lado dele
                            out.write(String.valueOf(a));//envia o número escolhido por cada jogador, sequencialmente
                            out.newLine();
                            /*
                            exemplo:
                            25\n
                            3\n
                            11\n
                            24\n
                            0\n

                             */
                        }
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                case"start":
                    ArrayList<String>usernames = conta.getUsernames();
                    ArrayList<Integer>escolhasGerais = conta.getEscolhasGerais();
                    try {
                        out.write("start\n");
                        //envia as picks finais para serem mostradas aos clientes
                        for(int i=0;i<10;i++){
                            //usernames
                            out.write(usernames.get(i));
                            out.newLine();
                            //escolhas
                            out.write(escolhasGerais.get(i));
                            out.newLine();
                        }
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }

    }
}
