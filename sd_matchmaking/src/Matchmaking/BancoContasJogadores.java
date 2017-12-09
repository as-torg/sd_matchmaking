package Matchmaking;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.CompareGenerator;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class BancoContasJogadores implements InterfaceBancoContasJogadores{
    /*
    Guarda as contas de todos os jogadores num map,
    e disponibiliza métodos para operar sobre elas
    -criar
    -remover
    -registar resultados de partidas nos clientes respetivos
    -verificar login (Username existe, e a password é igual)
    ...
     */
    private HashMap<String, Conta> contas;
    private ArrayList<Queue> queues;
    private ReentrantLock lock;

    public BancoContasJogadores() {
        this.contas = new HashMap<String, Conta>();
        this.queues = new ArrayList<Queue>();
        /*
        Abre 10 queues, cada uma com um rank entre 0 e 10.
        Em queues[i] fica a queue de rank i (supota jogadores i e i+1)
         */
        for(int i = 0; i < 10; i++){
            queues.add(i, new Queue(i));
        }
    }
    public boolean criarConta(String username, String password){
        Conta c = new Conta (username, password);
        //testa o equals porque podem haver dois usernames diferentes com o mesmo hash (mesmo que improvável)
        if(!contas.get(username).equals(c)){
            contas.put(username,c);
            return true;
        }
        return false;
    }
    public boolean login(String username, String password){
        return contas.get(username).loginConta(password);
    }
    public int consultarRank(String username){
        return contas.get(username).getRank();
    }
    public int consultarPontos(String username){
        return contas.get(username).getPontos();
    }
    public void logout(String username){
        contas.get(username).logoutConta();
    }
    public void jogar(String username){
        int numeroJogadores =-1;
        int rankJogador = -1;
        Conta conta = contas.get(username);
        rankJogador = conta.getRank();
        Queue queueIndicada = queues.get(rankJogador);
        numeroJogadores = queueIndicada.adicionarJogador(username,rankJogador);
        //Se tiver enchido a queue
        if (numeroJogadores == 10){
            ArrayList<Conta> jogadores = new ArrayList<Conta>();
            for (int i = 0; i < 10; i++){
                //adicionar as contas dos jogadores na queue
                jogadores.add(contas.get(queueIndicada.getEquipas().get(i)));
            }
            Collections.sort(jogadores, new ComparatorConta());



        }
    }
    public void sairQueue(String username){
        int rankJogador = contas.get(username).getRank();
        /*
        Um jogador de rank x pode estar nas filas x e x-1.
        Exceção para rank 0, que só podem estar na fila 0.
         */

        queues.get(rankJogador).removerJogador(username);
        if(rankJogador>0) queues.get(rankJogador-1).removerJogador(username);
    }
}
