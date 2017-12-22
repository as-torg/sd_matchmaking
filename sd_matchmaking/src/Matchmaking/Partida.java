package Matchmaking;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

public class Partida {
    /*
    Pega em duas equipas e simula uma partida.

    Controla as threads servintes de cada cliente, servindo como um gestor das mesmas.
    Invoca cada thread sob seu controlo para comunicar com o cliente na seleção de champions
    Invoca geradores aleatórios para determinar que equipa ganha e quão grande foi a diferença de performance
    (maior diferença significa que os vencedores sobem mais, e os edrrotados descem mais)
     */
    private ReentrantLock lock1, lock2;
    private ArrayList<Conta> equipa1, equipa2;
    private String[] championsEquipa1, championsEquipa2;
    private Timer timer;
    public Partida(ArrayList<Conta>equipa1, ArrayList<Conta>equipa2){
        this.championsEquipa1 = new String[30];
        this.championsEquipa2 = new String[30];
        this.equipa1 = equipa1;
        this.equipa2 = equipa2;
        this.lock1 = new ReentrantLock();
        this.lock2 = new ReentrantLock();
        this.timer = new Timer();
        timer.cancel();
        //define o que fazer quando tocar o timer
        TimerTask ação = new TimerTask() {
            @Override
            public void run() {
                int resultado = Partida.simula();
                if(resultado<0) {
                    //resultado negativo, equipa 1 ganha,
                    for (Conta c : equipa1) {
                        //adiciona pontos aos vencedores
                        c.registaResultados(-resultado);
                    }
                    for (Conta c : equipa2) {
                        //remove pontos aos derrotados
                        c.registaResultados(resultado);
                    }
                }
                else {
                    //se o resultado for ppositivo, equipa 2 ganha,
                    for (Conta c : equipa1) {
                        //remove pontos aos derrotados
                        c.registaResultados(-resultado);
                    }
                    for (Conta c : equipa2) {
                        //adiciona pontos aos vencedores
                        c.registaResultados(resultado);
                    }
                }
            }
        };
        //coloca o timer a contar, 30 000 milisegundos, 30 segundos
        this.timer.schedule(ação, 30000);

    }


    public void escolher(String username, int i){
        //dois locks para permitir escritas paralelas entre equipas (deve fazer pouca diferença)
        if (equipa1.contains(username)){
            lock1.lock();
            if (this.championsEquipa1[i].equals(null)){
                championsEquipa1[i]=username;
            }
            lock1.unlock();
        }
        else if (equipa2.contains(username)){
            lock2.lock();
            if (this.championsEquipa2[i].equals(null)){
                championsEquipa2[i]=username;
            }
            lock2.unlock();
        }
    }
    public static int simula(){
        int resultado=0;
        while(resultado==0) resultado = ThreadLocalRandom.current().nextInt(-20, 20 + 1); //até 5 não inclusive
        /*
        negativo- Equipa 1 ganha
        positivo- Equipa 2 ganha por pouco
        O resultado nulo representaria um empate que não é permitido nas regras
        */
        return resultado;
        }

}


