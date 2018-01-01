package Matchmaking;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class BancoContasJogadores {
    /*
    Guarda as contas de todos os jogadores num map,
    e disponibiliza métodos para operar sobre elas
    -criar
    -remover
    -registar resultados de partidas nos clientes respetivos
    -verificar login (Username existe, e a password é igual)
    ...
     */
    private HashMap<String,Conta> contas;
    private ReentrantLock lock;

    public BancoContasJogadores() {
        this.contas = new HashMap<String,Conta>();
    }
    public Conta getConta(String username){
        return contas.get(username);
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
}
