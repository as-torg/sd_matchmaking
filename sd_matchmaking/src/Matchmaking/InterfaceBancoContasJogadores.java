package Matchmaking;

public interface InterfaceBancoContasJogadores {
    Conta criarConta(String username, String password);
    Conta login(String username, String password);
    int consultarRank(String username);
    int consultarPontos(String username);
}
