package Matchmaking;

public interface InterfaceBancoContasJogadores {
    boolean criarConta(String username, String password);
    boolean login(String username, String password);
    int consultarRank(String username);
    int consultarPontos(String username);
    void logout(String username);
    void jogar(String username);
    void sairQueue(String username);




}
