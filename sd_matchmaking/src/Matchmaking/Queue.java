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

    int queueRank; //rank mínimo da queue
    private ArrayList<Conta> listaJogadores; //usernames
    ReentrantLock lock;

    /*
    ######################################################
    Construtores
     */
    public Queue(int queueRank) {
        this.queueRank = queueRank;
        this.lock = new ReentrantLock();
        this.listaJogadores = new ArrayList<Conta>();
    }

    /*
    ######################################################
    Métodos relevantes
    */
    public int adicionarJogador(Conta conta) {
        /*
        O teste ao rank deve ser feito pelo método que invoca este, ao determinar em que queues vai inserir o jogador.
        O teste serve apenas para confirmação, mas é redundante.
         */
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

    public int removerJogador(String username) {
        int tamanhoQueue = -1;
        lock.lock();
        listaJogadores.remove(username);
        tamanhoQueue = listaJogadores.size();
        lock.unlock();
        return tamanhoQueue;
    }

    public synchronized void jogar(Conta conta){
        int tamanhoQueue = -1;
        String username = conta.getUsername();
        int rankJogador = conta.getRank();
        if((tamanhoQueue = listaJogadores.size())<10 && !listaJogadores.contains(username) && (rankJogador == queueRank || rankJogador == queueRank + 1)) {
            listaJogadores.add(conta);
            tamanhoQueue++;
            if ( tamanhoQueue == 10){
                /*
                Formar as duas equipas de forma +- equilibrada
                A estratégia é ordenar os jogadores segundo os ranks/pontos,
                e colocar os consecutivos em equipas defirentes.
                Isto define um bias favorável à segunda equipa em caso de desajuste
                 */
                Collections.sort(listaJogadores, new ComparatorConta());
                ArrayList<Conta> equipa1, equipa2;
                equipa1 = new ArrayList<Conta>();
                equipa2 = new ArrayList<Conta>();
                for (int i = 0; i<10;i=i+2) {
                    equipa1.add(listaJogadores.get(i));
                    equipa2.add(listaJogadores.get(i+1));
                }
                listaJogadores.clear();
                /*
                Acordar todos os jogadores desta partida e chamar o construtor
                O tempo começa logo a contar no momento em que o objeto é criado, mas está
                na última instrução do construtor para dar o mais próximo possível de 30 segundos aos jogadores
                Com o objeto, a partida fica pronta para receber as escolhas dos jogadores de cada equipa
                 */
                this.notifyAll();
                Partida partida = new Partida(equipa1, equipa2);
            }
            else try {
                //esperar por mais jogadores
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int getRank(){
        int res =-1;
        lock.lock();
        res = queueRank;
        lock.unlock();
        return res;
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


}
/*
*/