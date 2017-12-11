package Matchmaking;

public class Servidor {
    /*
    Processo central
    Mantém um BancoContas e lança threads quando recebe conexões
    Cada threads vai estar ligadas a um só cliente até ele fazer logout.
     */
    BancoContasJogadores banco;
    public Servidor (){
        this.banco = new BancoContasJogadores();
    }
}
