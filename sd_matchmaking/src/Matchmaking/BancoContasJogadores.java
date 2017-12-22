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

    public BancoContasJogadores(ArrayList<Queue>queues) {
        this.contas = new HashMap<String, Conta>();
        this.queues = queues;
        /*
        Abre 10 queues, cada uma com um rank entre 0 e 10.
        Em queues[i] fica a queue de rank i (supota jogadores i e i+1)
         */

    }
    public Conta criarConta(String username, String password){
        Conta c = new Conta (username, password);
        //testa o equals porque podem haver dois usernames diferentes com o mesmo hash (mesmo que improvável)
        if(!contas.get(username).equals(c)){
            return contas.put(username,c);
        }
        return null;
    }
    public Conta login(String username, String password){
        if(contas.get(username).loginConta(password)) return contas.get(username);
        return null;
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
        //inserir o jogador nas queues
        Queue queueIndicada = queues.get(rankJogador);
        numeroJogadoresIndicada = queueIndicada.adicionarJogador(conta);
        Queue queueAbaixo = null;
        if(rankJogador>0){
            queueAbaixo = queues.get(rankJogador-1);
            numeroJogadoresAbaixo = queueAbaixo.adicionarJogador(conta);
        }
        //Se estiver uma queue completa, remove repetidos nas outras e começa o jogo
        if(numeroJogadoresIndicada==10 && rankJogador>0){
            ArrayList<Conta> jogadores = queueIndicada.getJogadores();
            for (Conta c:jogadores){
                for (Queue q:queues){
                    if (q.getRank()!=rankJogador) q.removerJogador(c.getUsername());
                }
            }
        }


        //Se tiver enchido uma queue, remover as inscrições repetidas e começar o jogo

    }
    public void sairQueue(String username){
        /*
        Um jogador de rank x pode estar nas filas x e x-1.
        Exceção para rank 0, que só podem estar na fila 0.
         */
        int rankJogador = contas.get(username).getRank();
        queues.get(rankJogador).removerJogador(username);
        if(rankJogador>0) queues.get(rankJogador-1).removerJogador(username);
    }
}
