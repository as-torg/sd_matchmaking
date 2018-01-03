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
    /*
    estão dois locks, sendo um pra cada equipa. No entanto tal só seria usado caso houvessem duas threads aqui a mexer
    mas atualmente só temos uma.
    */
    private ArrayList<Conta> equipa1, equipa2;
    private ArrayList<Integer> championsEquipa1, championsEquipa2;
    private int [] escolheu;
    /*
    o "championsEquipa" é útil enquanto estão a ser escolhidos porque não precisa de travessias,
    é onde as threads dos clientes fazem as picks de champions
    o "escolheu" é para poder identificar facilmente quem é que não escolheu, e retirar-lhes pontos
     */
    private Timer timer;
    private TimerTask ação;
    private ArrayBlockingQueue mensagens;
    private int idpartida;

    public Partida(ArrayList<Conta>e1, ArrayList<Conta>e2, int id){
        this.championsEquipa1 = new ArrayList<>(5);
        this.championsEquipa2 = new ArrayList<>(5);
        this.equipa1 = e1;
        this.equipa2 = e2;
        this.idpartida = id;
        this.escolheu = new int [10];
        for (int i=0; i<5; i++) {
            championsEquipa1.set(i,-1);
            championsEquipa2.set(i,-1);
            escolheu[i]=-1;
            escolheu[i+5]=-1;
        }
        /*
        redireciona as mensagens dos clientes para a Partida (escolhas de champions)
        os envios para os clientes são individuais porque é preciso distinguir o cliente, por exemplo para
        pedir que escolha outro caso já alguém tenha pedido o que quer
         */
        this.mensagens = new ArrayBlockingQueue(30);
        this.timer = new Timer();
        timer.cancel();
        //define o que fazer quando tocar o timer
        ação = new TimerTask() {
            @Override
            public void run() {
                /*
                verifica se todos escolheram com sucesso.
                seria possível ver quem é que não escolheu e dar uma penalização a esse jogador
                são processadas as duas equipas em paralelo (estilo loop unrolling)
                 */
                boolean todosEscolheram = true;
                //fecha as escolhas com uma cópia do que foi escolhido. Qualquer escolha após este momento será perdia
                ArrayList<Integer>escolhasGerais = new ArrayList<>(10);
                for (int i=0;i<5;i++){
                    escolhasGerais.set(i,championsEquipa1.get(i));
                    escolhasGerais.set(i+5,championsEquipa2.get(i));
                }
                /*
                qualquer valor -1 indica que o jogador com esse índice não escolheu
                se alguém não escolher, é penalizado e não é simulado o jogo
                 */
                for(int i=0; i<5;i++){
                    if(!(escolhasGerais.get(i)>=0)) {
                        equipa1.get(i).registaResultados(-20);
                        equipa1.get(i).writeToCliente("timeout");
                        todosEscolheram = false;
                    }
                    if(!(escolhasGerais.get(i)>=0)) {
                        equipa2.get(i).registaResultados(-20);
                        equipa2.get(i).writeToCliente("timeout");
                        todosEscolheram = false;
                    }
                }
                //Se todos tiverem escolhido normalmente, confirma a partida, informa os jogadores com os nomes e picks
                if (todosEscolheram){
                    ArrayList<String>usernames = new ArrayList<>(10);
                    for(int i=0;i<5;i++) {
                        usernames.set(i,equipa1.get(i).getUsername());
                        usernames.set(i+5,equipa2.get(i).getUsername());
                    }
                    for(int i=0;i<5;i++) {
                        //10 escolhas e 10 usernames são colocados para leitura nas contas.
                        //as threads são avisadas para atualizar porque o jogo vai começar
                        equipa1.get(i).setEscolhasGerais(escolhasGerais);
                        equipa1.get(i).setUsernames(usernames);
                        equipa1.get(i).writeToCliente("start");
                        equipa2.get(i).setEscolhasGerais(escolhasGerais);
                        equipa2.get(i).setUsernames(usernames);
                        equipa2.get(i).writeToCliente("start");
                    }

                    try {
                        wait(2000);//apenas para que o cliente receba e veja as equipas antes de "começar" o jogo
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //é gerado um resultado
                    int resultado = 0;
                    while(resultado == 0) resultado = ThreadLocalRandom.current().nextInt(-20, 20 + 1); //até 5 não inclusive
                    /*
                    negativo- Equipa 1 ganha
                    positivo- Equipa 2 ganha
                    O resultado nulo representaria um empate que não é permitido nas regras
                    */
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
            }
        };
        //coloca o timer a contar, 30 000 milisegundos, 30 segundos
    }

    @Override
    public void run() {

        //preparar todos os jogadores e suas contas
        for (int i=0; i<5;i++) {
            Conta c1 = equipa1.get(i);
            Conta c2 = equipa2.get(i);
            c1.prepararJogo(0,i,idpartida, mensagens,championsEquipa1);
            c2.prepararJogo(1,i,idpartida, mensagens,championsEquipa2);
            c1.writeToCliente("escolherAgora");
            c2.writeToCliente("escolherAgora");

        }
        //começar o relógio
        this.timer.schedule(ação, 30000);
        String mensagem;
        while(true){
            mensagem = (String) mensagens.poll();
            //trata dos broadcasts dos updates, atualiza a equipa que teve alterações
            if(mensagem.equals("updateEquipa1")){
                for (Conta c:equipa1) {
                    c.writeToCliente("updateEquipa");
                }
            }
            if(mensagem.equals("updateEquipa2")){
                for (Conta c:equipa2) {
                    c.writeToCliente("updateEquipa");
                }
            }


        }
        //ESTAVA AQUI V
    }
/*
* while (true){
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
                    //###############################
                    /*
                    atualizar os clientes com nensagens nas queues
                    Provavelmente terão que ser threads dedicadas a ler de buffers em clientes e interpretar
                    em switch, uma de cada lado
                    *
    //###############################


}
//caso já tenha sido escolhido pede para escolher outro
                else {
                        equipa1.get(numerojogador).writeToCliente("outro");
                        lock1.unlock();
                        }
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
                        else {
                        equipa2.get(numerojogador).writeToCliente("outro");
                        lock2.unlock();
                        }

                        }
                        //volta a atender mais um pedido enquanto não tocar o alarme
                        }*/

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


