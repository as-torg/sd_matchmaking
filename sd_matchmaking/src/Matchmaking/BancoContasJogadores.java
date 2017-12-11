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
        int numeroJogadoresIndicada =-1;
        int numeroJogadoresAbaixo = -1;
        int rankJogador = -1;
        Conta conta = contas.get(username);
        rankJogador = conta.getRank();
        //Pegar nas queues permitidas
        Queue queueIndicada = queues.get(rankJogador);
        Queue queueAbaixo = null;
        if(rankJogador>0){
            queueAbaixo = queues.get(rankJogador-1);
        }
        //inserir o jogador nas queues
        numeroJogadoresIndicada = queueIndicada.adicionarJogador(username,rankJogador);
        numeroJogadoresAbaixo = queueAbaixo.adicionarJogador(username, rankJogador);
        //Se tiver enchido uma queue, remover as inscrições repetidas e começar o jogo
        if (numeroJogadoresIndicada == 10){
            ArrayList<Conta> jogadores = new ArrayList<Conta>();
            /*
            adicionar as contas dos jogadores numa fila pronta a jogar, ordenando por rank crescente
            Mais tarde serão separados em equipas o mais equilibradas possível
             */
            for (int i = 0; i < 10; i++){
                jogadores.add(contas.get(queueIndicada.getEquipas().get(i)));
            }
            Collections.sort(jogadores, new ComparatorConta());
            //remover todos  os casos repetidos noutras queues, evita que joguem 2 partidas simultâneas
            for (Conta c:jogadores){
                for (Queue q:queues){
                    q.removerJogador(c.getUsername());
                }
            }
            //lançar uma partida abrindo o lobby para escolher champions
            Lobby lobby = new Lobby(jogadores);
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
