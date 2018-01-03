package Matchmaking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.locks.ReentrantLock;

public class Queue {

    /*
    Fila de espra para uma partida.
    Acumula jogadores de ranks compatíveis até ter 10.
    Nessa altura procedem para matchmaking (são colocados num lobby de escolha dos heróis).

    As queues vão procurar jogadores com o rank indicado ou com o rank acima. Ao sucederem em preencher 10 jogadores,
    o servidor é avisado, e os usernames são removidos de todas as outras queues em que possam estar.
     */

    private ArrayList<Conta> listaJogadores; //usernames
    private ReentrantLock lock;
    /*
    ######################################################
    Construtores
     */
    public Queue() {
        this.lock = new ReentrantLock();
        this.listaJogadores = new ArrayList<Conta>();
    }

    public int removerJogador(Conta c) {
        int tamanhoQueue;
        lock.lock();
        listaJogadores.remove(c);
        tamanhoQueue = listaJogadores.size();
        lock.unlock();
        return tamanhoQueue;
    }

    public synchronized void jogar(Conta conta){
        String username = conta.getUsername();
        if((listaJogadores.size())<10 && !listaJogadores.contains(username)) {
            listaJogadores.add(conta);
        }
    }

    //########################################
    //temporário
    public ArrayList<Conta> getJogadores(){
        /*
        Os jogadores estão ainda desordenados. Será tratado por quem invocar
        O size do arraylist será 10.
         */
        return listaJogadores;
    }
    public synchronized int size(){
        return listaJogadores.size();
    }
    public boolean isFull(){
        return this.size()==10;
    }
    public synchronized void reset(){
        listaJogadores.clear();
    }

}

    /*
    ######################################################
    Métodos relevantes
    */
    /*
    public int adicionarJogador(Conta conta) {
        /*
        O teste ao rank deve ser feito pelo método que invoca este, ao determinar em que queues vai inserir o jogador.
        O teste serve apenas para confirmação, mas é redundante.
         *
        int tamanhoQueue = -1;
        String username = conta.getUsername();
        int rankJogador = conta.getRank();
        lock.lock();
        if (listaJogadores.contains(username)) {
            //jogador já está na queue
            lock.unlock();
            return -1;
        }
        else {
            if (rankJogador == queueRank || rankJogador == queueRank + 1) {
                //LinkCliente não está na queue, esta é uma queue certa para o seu rank.
                listaJogadores.add(conta);
                tamanhoQueue = listaJogadores.size();
                lock.unlock();
                return tamanhoQueue;
            } else {
                //jogador não está na queue certa
                lock.unlock();
                return -1;
            }
        }
    }
    */