package Matchmaking;

import java.util.Comparator;
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
    private String username; //não pode ser alterado
    private String password; //não pode ser alterada
    private String idPartida; //indica em que partida está. null se não está a jogar
    private boolean sessão; //indica se o dono da conta tem login feito. Necessário ser true para poder interagir.
    private ReentrantLock lock;

    /*
    ######################################################
    Construtores
     */
    public Conta(String username, String password) {
        this.username = username;
        this.password = password;
        this.rank = 0;
        this.pontos = 0;
        this.idPartida = null;
        this.lock = new ReentrantLock();
        this.sessão = false;
    }
    /*
    ######################################################
    Métodos relevantes
    */
    public void registaResultados(int pontos){
        lock.lock();
        this.pontos+=pontos;
        lock.unlock();
        if(this.pontos > 100) {
            this.rank++;
            this.pontos=this.pontos-100;
        }
        else if(this.pontos < -100){
            this.rank--;
            this.pontos = this.pontos+100;
        }
    }
    public boolean loginConta(String password){
        lock.lock();
        if (this.password.equals(password) && !this.sessão) this.sessão = true;
        lock.unlock();
        return true;
    }
    public boolean logoutConta(){
        lock.lock();
        this.sessão = false;
        lock.unlock();
        return false;
    }
    /*
    ######################################################
    Gets e sets
     */
    public int getRank() {
        int res = -1;
        lock.lock();
        if(sessão) res = this.rank;
        lock.unlock();
        return res;
    }
    public int getPontos() {
        int res = 0;
        lock.lock();
        if(sessão) res = this.pontos;
        lock.unlock();
        return res;
    }
    public String getIdPartida() {
        return idPartida;
    }
    public void setIdPartida(String idPartida) {
        this.idPartida = idPartida;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    /*
    ######################################################
    Comparators e equals
     */
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
}
