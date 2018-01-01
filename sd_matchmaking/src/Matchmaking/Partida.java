package Matchmaking;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

public class Partida implements Runnable{
    /*
    Pega em duas equipas e simula uma partida.

    Controla as threads servintes de cada cliente, servindo como um gestor das mesmas.
    Invoca cada thread sob seu controlo para comunicar com o cliente na seleção de champions
    Invoca geradores aleatórios para determinar que equipa ganha e quão grande foi a diferença de performance
    (maior diferença significa que os vencedores sobem mais, e os edrrotados descem mais)
     */
    private ReentrantLock lock1, lock2;
    /*
    estão dois locks, sendo um pra cada equipa. No entanto tal só seria usado caso houvessem duas threads aqui a mexer
    mas atualmente só temos uma.
    */
    private ArrayList<Conta> equipa1, equipa2;
    private ArrayList<String> usernames1, usernames2;
    private String[] championsEquipa1, championsEquipa2;
    private Timer timer;
    private TimerTask ação;
    private ArrayBlockingQueue mensagens;
    private int idpartida;

    public Partida(ArrayList<Conta>e1, ArrayList<Conta>e2, int id){
        this.championsEquipa1 = new String[30];
        this.championsEquipa2 = new String[30];
        this.equipa1 = e1;
        this.equipa2 = e2;
        this.idpartida = id;
        this.lock1 = new ReentrantLock();
        this.lock2 = new ReentrantLock();
        //estes usernames são para facilitar a procura quando for para registar escolhas de champions
        for (int i=0; i<5; i++) {
            usernames1.add(i,equipa1.get(i).getUsername());
            equipa1.get(i).setNumeroJogador(i);
        }
        for (int i=0; i<5; i++) {
            usernames2.add(i,equipa2.get(i).getUsername());
            equipa2.get(i).setNumeroJogador(5+i);
        }
        /*
        redireciona as mensagens dos clientes para a Partida (escolhas de champions)
        os envios para os clientes são individuais porque é preciso distinguir o cliente, por exemplo para
        pedir que escolha outro caso já alguém tenha pedido o que quer
         */
        this.mensagens = new ArrayBlockingQueue(30);
        for (Conta c: equipa1) {
            c.setFromCliente(mensagens);
        }
        for (Conta c: equipa2) {
            c.setFromCliente(mensagens);
        }
        this.timer = new Timer();
        timer.cancel();
        //define o que fazer quando tocar o timer
        ação = new TimerTask() {
            @Override
            public void run() {
                /*
                verifica se todos escolheram com sucesso.
                seria possível ver quem é que não escolheu e dar uma penalização a esse jogador
                 */
                int champions=0;
                for(int i=0;i<5;i++){
                    if(!championsEquipa1[i].equals(null))champions++;
                    if(!championsEquipa2[i].equals(null))champions++;
                }

                //Se todos tiverem escolhido normalmente, é gerado um resultado
                if (champions == 10){
                    for (int i=0;i<5;i++) {
                        equipa1.get(i).setIdPartida(idpartida);
                        equipa2.get(i).setIdPartida(idpartida);
                    }
                    int resultado = 0;
                    while(resultado == 0) resultado = ThreadLocalRandom.current().nextInt(-20, 20 + 1); //até 5 não inclusive
                    /*
                    negativo- Equipa 1 ganha
                    positivo- Equipa 2 ganha por pouco
                    O resultado nulo representaria um empate que não é permitido nas regras
                    */
                    if(resultado<0) {
                        //resultado negativo, equipa 1 ganha,
                        for (Conta c : equipa1) {
                            //adiciona pontos aos vencedores e retira da partida, já pode entrar na queue novamente
                            c.registaResultados(-resultado);
                            c.writeToCliente("ganhou");
                        }
                        for (Conta c : equipa2) {
                            //remove pontos aos derrotados e retira da partida, já pode entrar na queue novamente
                            c.registaResultados(resultado);
                            c.writeToCliente("perdeu");
                        }
                    }
                    else {
                        //se o resultado for ppositivo, equipa 2 ganha,
                        for (Conta c : equipa1) {
                            //remove pontos aos derrotados e retira da partida, já pode entrar na queue novamente
                            c.registaResultados(-resultado);
                            c.writeToCliente("perdeu");
                        }
                        for (Conta c : equipa2) {
                            //adiciona pontos aos vencedores e retira da partida, já pode entrar na queue novamente
                            c.registaResultados(resultado);
                            c.writeToCliente("ganhou");
                        }
                    }
                }
            }
        };
        //coloca o timer a contar, 30 000 milisegundos, 30 segundos
    }

    @Override
    public void run() {
        //começar o relógio
        this.timer.schedule(ação, 30000);
        //alertar todos os jogadores
        for (int i=0; i<5;i++) {
            equipa1.get(i).writeToCliente("escolher");
            equipa2.get(i).writeFromCliente("escolher");
        }
        while (true){
            String username = (String)mensagens.poll();
            int numerojogador = Integer.parseInt((String)mensagens.poll());
            int champion = Integer.parseInt((String)mensagens.poll());

            //se estiver na primeira equipa
            if(numerojogador<5){
                lock1.lock();
                //tenta regista o champion
                if(championsEquipa1[champion].equals(null)) {
                    championsEquipa1[champion]=username;
                    equipa1.get(numerojogador).writeToCliente("escolhido");
                }
                //caso já tenha sido escolhido pede para escolher outro
                else {equipa1.get(numerojogador).writeToCliente("outro");}
                lock1.unlock();
            }
            //se estiver na segunda equipa
            else {
                lock2.lock();
                //tenta regista o champion
                if(championsEquipa2[champion].equals(null)) {
                    championsEquipa2[champion] = username;
                    equipa1.get(numerojogador).writeToCliente("escolhido");
                }
                //caso já tenha sido escolhido pede para escolher outro
                else equipa2.get(numerojogador).writeToCliente("outro");
                lock2.unlock();
            }
        //volta a atender mais um pedido enquanto não tocar o alarme
        }

    }


    /*
    public static int simula(){
        int resultado=0;
        while(resultado==0) resultado = ThreadLocalRandom.current().nextInt(-20, 20 + 1); //até 5 não inclusive
        /*
        negativo- Equipa 1 ganha
        positivo- Equipa 2 ganha por pouco
        O resultado nulo representaria um empate que não é permitido nas regras
        *
        return resultado;
        }
    */
}


