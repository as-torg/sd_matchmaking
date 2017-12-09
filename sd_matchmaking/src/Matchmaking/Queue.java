package Matchmaking;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class Queue {

    /*
    Fila de espra para uma partida.
    Acumula jogadores de ranks compatíveis até ter 10.
    Nessa altura procedem para matchmaking (são colocados num lobby de escolha dos heróis).

    As queues vão procurar jogadores com o rank indicado ou com o rank acima. Ao sucederem em preencher 10 jogadores,
    o servidor é avisado, e os usernames são removidos de todas as outras queues em que possam estar.

    Na prática é apenas uma classe auxiliar para abstraír o código.
     */

    int queueRank; //rank mínimo da queue
    private ArrayList<String> listaJogadores; //usernames
    ReentrantLock lock;
    /*
    ######################################################
    Construtores
     */
    public Queue(int queueRank){
        this.queueRank = queueRank;
        this.lock = new ReentrantLock();
    }
    /*
    ######################################################
    Métodos relevantes
    */
    public int adicionarJogador(String username, int rankJogador){
        /*
        O teste ao rank deve ser feito pelo método que invoca este, ao determinar em que queues vai inserir o jogador.
        O teste serve apenas para confirmação, mas é redundante.
         */
        int tamanhoQueue = -1;
        lock.lock();
        if(listaJogadores.contains(username)){
            //jogador já está na queue
            lock.unlock();
            return -1;
        }
        if (rankJogador == queueRank || rankJogador == queueRank+1) {
            //LinkCliente não está na queue, esta é uma queue certa para o seu rank.
            listaJogadores.add(username);
            tamanhoQueue = listaJogadores.size();
            lock.unlock();
            return tamanhoQueue;
        }
        else {
            //jogador não está na queue certa
            lock.unlock();
            return -1;
        }
    }
    public int removerJogador(String username){
        int tamanhoQueue = -1;
        lock.lock();
        listaJogadores.remove(username);
        tamanhoQueue = listaJogadores.size();
        lock.unlock();
        return tamanhoQueue;
    }




    //########################################
    //temporário
    public ArrayList<String> getEquipas(c){
        /*
        A equipa 1 está nos 5 primeiros, a equipa 2 nos restantes.
        O size do arraylist será 10.
         */
        for(int i=0; i<10; i++){
            if(listaJogadores.get(i)

        }

        return listaJogadores;
    }


}
