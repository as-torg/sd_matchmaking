package Matchmaking;


import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;

public class GestorQueues implements Runnable {

    private ArrayList<Queue> queues;
    private BancoContasJogadores banco;
    private ArrayBlockingQueue mensagens;
    private int contadorPartidas;

    public GestorQueues(BancoContasJogadores b, ArrayBlockingQueue mensagens){
        this.mensagens = mensagens;
        this.banco = b;
        this.queues = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            queues.add(i, new Queue());
        }
        contadorPartidas = 0;
    }


    @Override
    public void run() {
        String username = null;
        while(true){
            /*
            Recebe um pedido da blocking queue (username de alguém que quer jogar)
            O processo fica bloqeado até que haja um pedido para jogar
            */
            try {
                username = (String)mensagens.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //if(username.contains("cancelar"))
            //carrega e prepara dados necessários
            Conta conta = banco.getConta(username);
            if(conta.getIdPartida()==0){
                conta.writeToCliente("ingame");
                break;
            }
            if(conta.isInQueue()){
                conta.writeToCliente("inqueue");
                break;
            }
            conta.setInQueue(true);
            int rankJogador = conta.getRank();
            Queue queueIndicada = queues.get(rankJogador);
            Queue queueAbaixo;
            //coloca nas queues. queue indicada ainda tem vagas, porque ao começar é esvaziada
            queueIndicada.jogar(conta);
            //se ficar cheia, prepara uma partida
            if (queueIndicada.isFull()) {
                //extrai os jogadores da queue
                ArrayList<Conta> jogadores = queueIndicada.getJogadores();
                //remove esses jogadores das outras queues (duplicados na queueAbaixo existem para ranks<0)
                queueAbaixo = queues.get(rankJogador-1);
                for (Conta c:jogadores){
                    queueAbaixo.removerJogador(c);
                }
                //remove todos da queue indicada, fazendo um reset a essa queue
                queueIndicada.reset();
                /*
                cria uma Partida para os jogadores
                Os jogadores são ordenados de forma crescente de habilidade, e colocados em equipas.
                A forma usada é pegar em dois maus, um para cada equipa; dois médios, um para cada equipa;...
                Em caso de desigualdade leva a uma tendência favorável à segunda equipa, porque fica sempre com o melhor.
                 */
                Collections.sort(jogadores, new ComparatorConta());
                ArrayList<Conta> equipa1, equipa2;
                equipa1 = new ArrayList<>();
                equipa2 = new ArrayList<>();
                for (int i = 0; i<10;i=i+2) {
                    equipa1.add(jogadores.get(i));
                    equipa2.add(jogadores.get(i+1));
                }
                //criar uma nova Partida
                contadorPartidas++;
                Partida partida = new Partida(equipa1,equipa2, contadorPartidas);
                //começar a Partida numa nova thread
                Thread t = new Thread(partida);
                t.start();
            }

            /*
            se o rank do jogador for maior que 0, pode também ser colocado na queue abaixo
            o processo vai ser análogo, mas as inserções vão ser feitas na queue abaixo, e a remoção na queue indicada
            os jogadores com rank 0 apenas serão registados na queue de nível 0, ou seja, na indicada (daí o if())
             */
            if (rankJogador > 0){
                queueAbaixo=queues.get(rankJogador-1);
                queueAbaixo.jogar(conta);
                if (queueAbaixo.isFull()) {
                    ArrayList<Conta> jogadores = queueIndicada.getJogadores();
                    for (Conta c:jogadores){
                        queueIndicada.removerJogador(c);
                    }
                    queueAbaixo.reset();
                    Collections.sort(jogadores, new ComparatorConta());
                    ArrayList<Conta> equipa1, equipa2;
                    equipa1 = new ArrayList<>();
                    equipa2 = new ArrayList<>();
                    for (int i = 0; i<10;i=i+2) {
                        equipa1.add(jogadores.get(i));
                        equipa2.add(jogadores.get(i+1));
                    }
                    contadorPartidas++;
                    Partida partida = new Partida(equipa1,equipa2,contadorPartidas);
                    Thread t = new Thread(partida);
                    t.start();
                }
            }
        }
    }
}
    /*
    public synchronized void jogar(Conta conta){
        String username = conta.getUsername();
        int rankJogador = conta.getRank();

        Queue queueIndicada = queues.get(rankJogador);
        Queue queueAbaixo = null;

        if(queueIndicada.size()<10){ //queue indicada ainda tem vagas
            queueIndicada.jogar(conta);
            //se ficar cheia, prepara uma partida
            if (queueIndicada.isFull()) {
                //guarda os jogadores
                ArrayList<Conta> jogadores = queueIndicada.getJogadores();
                //remove esses jogadores das outras queues
                queueAbaixo=queues.get(rankJogador-1);
                for (Conta c:jogadores){
                    queueAbaixo.removerJogador(c);
                    }
                //remove todos da queue indicada, fazendo um reset a essa queue
                queueIndicada.reset();
                //cria uma Partida para os jogadores
                Collections.sort(jogadores, new ComparatorConta());
                ArrayList<Conta> equipa1, equipa2;
                equipa1 = new ArrayList<Conta>();
                equipa2 = new ArrayList<Conta>();
                for (int i = 0; i<10;i=i+2) {
                    equipa1.add(jogadores.get(i));
                    equipa2.add(jogadores.get(i+1));
                }
                Partida partida = new Partida()
                //começa a Partida
                }
            }

        }
        else{//queue indicada está cheia, inicia
            queueIndicada.jogar(conta);
            ArrayList<Conta> jogadores = queueIndicada.getJogadores();
            for (Conta c:jogadores){
                for (Queue q:queues){
                    if (q.getRank()!=rankJogador) q.removerJogador(c.getUsername());
                }
            }
        }
        //para o caso em que o jogador tem rank 0 só pode estar na queue indicada. Todos os outros podem estar na abaixo
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
        if (numeroJogadoresIndicada==10 && rankJogador==0){

        }
        */
