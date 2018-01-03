package Matchmaking;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public class Conta {
    /*
        Representa uma conta de um utilizador.
        Guarda os seus atributos como rank e password
        -rank atual
        -numero de partidas
        -username
        -password
        -chave do jogo atual (se estiver em matchmaking de um partida)
        O rank
    */
    private int rank; //valor de 0 a 9
    private int pontos; //começa em 0, ao chegar a 100 sobe de rank, -100 desce de rank; faz reset a 0
    private int numeroJogador;//indica o número de jogador na partida, ex: player1, player2 [0,4]
    private int numeroEquipa; //indica se ficou na equipa 1 ou 2 [0,1] para funcionar diretamente em if(numeroEquipa)
    private String username; //não pode ser alterado
    private String password; //não pode ser alterada
    private int idPartida; //indica em que partida está. null se não está a jogar
    private boolean sessao; //indica se o dono da conta tem login feito. Necessário ser true para poder interagir.
    //por defeito, a sessão é inciada durante a criação da conta.
    private boolean inQueue; //indica se está a procurar uma partida
    private ReentrantLock lock;
    private ArrayBlockingQueue toCliente;
    private ArrayBlockingQueue fromCliente;
    private ArrayList<Integer> escolhas, escolhasGerais; //escolhas das equipas
    private ArrayList<String> usernames;

    /*
    Para poder implementar a comunicação de forma mais fácil, os buffers da socket aberta para o cliente
    vão estar aqui guardados. Assim, quem estiver a lidar com informação do cliente poderá comunicar com ele.
    Isto evita a criação de mais threads orientadas à comunicação, e reduz a complexidade, mantendo as comunicações
    dentro do contexto.
     */

    /*
    ######################################################
    Construtor
     */
    public Conta(String username, String password) {
        this.username = username;
        this.password = password;
        this.rank = 0;
        this.pontos = 0;
        this.numeroJogador = -1;
        this.numeroEquipa = -1;
        this.idPartida = -1;
        this.lock = new ReentrantLock();
        this.sessao = true;
        this.inQueue = false;
        toCliente = new ArrayBlockingQueue(5); //uma devia chegar, o parser está sempre a fazer poll(),
        //só para o caso do parser estar atrasado e o sistema der gás
        fromCliente = null;
        this.escolhasGerais = null;
        this.escolhas = null;
        this.usernames = null;
    }

    /*
    ######################################################
    Métodos relevantes
    */
    public synchronized void registaResultados(int pontos) {
        this.pontos += pontos;

        if (this.pontos > 100) {
            this.rank++;
            this.pontos = this.pontos - 100;
        } else if (this.pontos < -100) {
            this.rank--;
            this.pontos = this.pontos + 100;
        }
        idPartida = -1;
        escolhas = null;
        escolhasGerais = null;
    }

    public boolean loginConta(String password) {
        lock.lock();
        if (this.password.equals(password) && !this.sessao) this.sessao = true;
        lock.unlock();
        return sessao;
    }

    public void logoutConta() {
        lock.lock();
        this.sessao = false;
        this.inQueue = false;
        this.idPartida = -1; //não deve de ser preciso, apenas por segurança
        lock.unlock();
    }

    /*
    ######################################################
    Escritores e leitores das mensagens
    */
    public String readFromCliente() {
        return (String) fromCliente.poll();
    }

    public String readToCliente() {
        return (String) toCliente.poll();
    }

    public void writeToCliente(String mensagem) {
        try {
            toCliente.put(mensagem);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void writeFromCliente(String mensagem) {
        try {
            fromCliente.put(mensagem);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
    ######################################################
    Gets e sets
     */

    public boolean isSessao() {
        return sessao;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
    public int getIdPartida() {
        return idPartida;
    }

    public int getRank() {
        int res = -1;
        lock.lock();
        res = this.rank;
        lock.unlock();
        return res;
    }

    public int getPontos() {
        int res = 0;
        lock.lock();
        res = this.pontos;
        lock.unlock();
        return res;
    }

    public int getNumeroJogador() {
        return numeroJogador;
    }

    public String getUsername() {
        return username;
    }

    public boolean isInQueue() {
        return inQueue;
    }

    public void setInQueue(boolean inQueue) {
        this.inQueue = inQueue;
    }

    public boolean escolherChampion(int numeroChampion){
        boolean flag = true;
        lock.lock();
        for(Integer reservado: escolhas) { //reservado é o número do champion escolhido pelo colega de equipa em equipa[i]
            flag = flag && reservado!=numeroChampion ;
        }
        if(flag) escolhas.set(numeroJogador,numeroChampion);
        lock.unlock();
        return flag;
    }

    public void prepararJogo(int numeroEquipa, int numeroJogador, int idPartida, ArrayBlockingQueue fromCliente, ArrayList<Integer>equipa){
        this.numeroJogador = numeroJogador;
        this.numeroEquipa =numeroEquipa;
        this.idPartida = idPartida;
        this.escolhas = equipa;
        /*
        Este arraylist tem, em cada índice i=[0,4] o champion escolhido pelo jogador i.
        As escolhas de champions serão feitas concorrentemente pelas threadsServintes de cada cliente
         */
        this.fromCliente = fromCliente;
        /*
        Este set serve para que todos os pedidos de escolha dos clientes sejam colocados por ordem numa só queue
        Assim o Partida vai ler o primeiro pedido, e não tem que andar a fazer scan nos diversos clientes individualmente
        Cliente --->>> (mensagem via socket) --->>> ThreadServinte --->>> (mensagem escrita em fromCliente) --->>> Partida
         */
    }

    public ArrayList<Integer> getEscolhas(){
        return escolhas;
    }

    public ArrayList<Integer> getEscolhasGerais(){
        return escolhasGerais; //se não tiver chegado ao fim do tempo a Partida ainda não colocou, e sai um null
    }

    public void setEscolhasGerais(ArrayList<Integer> escolhasGerais) {
        this.escolhasGerais = escolhasGerais;
    }

    public void setUsernames(ArrayList<String> usernames) {
        this.usernames = usernames;
    }

    public int getNumeroEquipa() {
        return numeroEquipa;
    }

    public ArrayList<String> getUsernames() {
        return usernames;
    }
}
    /*
    ######################################################
    Não usado

    public int compareTo(Conta c){
        return this.rank - c.getRank();
    }
    public int compare(Conta c1, Conta c2){
        return c1.getRank() - c2.getRank();
    }
    public boolean equals(Object o){//deriving equals do Haskell
        if (this==o){
            return true;
        } //se o apontador do objeto o for para este objeto (this)
        if (o==null || o.getClass()!=this.getClass()){
            return false;
        }
        //agora temos que fazer cast do objecto para um complexo. Não se pode fazer o.getA() porque ainda é da classe objeto
        else{
            Conta c = (Conta) o;
            return (this.username.equals(c.getUsername()));
        }
    }

    public String getPassword() {
        return password;
    }
    public void setIdPartida(String idPartida) {
        this.idPartida = idPartida;//já é feito no prepararJogo()
    }
    public String getIdPartida() {
        return idPartida;
    }

    */

