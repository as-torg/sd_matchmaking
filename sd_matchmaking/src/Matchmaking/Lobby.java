package Matchmaking;

import java.util.ArrayList;

public class Lobby {
    /*
    Lobby onde os clientes escolhem os heróis
    Guarda os 30 heróis, marcando se estão disponíveis ou já foram escolhidos

    O ArrayList <Conta> jogadores já vem ordenado de forma crescente segundo o rank e pontuação dos jogadores
    Assim, para ter equipas equilibradas basta tirar de forma ordenada um jogador para cada equipa.
    Isto vai dar o equilíbrio máximo, sendo que a equipa 2 vai ficar cigual ou mais forte (excesso) que a equipa 1
     */
    ArrayList <Conta> equipa1, equipa2;
    public Lobby(ArrayList<Conta>jogadores){
        this.equipa1 = new ArrayList<Conta>();
        this.equipa2 = new ArrayList<Conta>();
        for (int i = 0; i<10;i=i+2) {
            equipa1.add(jogadores.get(i));
            equipa2.add(jogadores.get(i+1));
        }
    }
    public Conta getJogador(int equipa, int indice){
        if (equipa == 1) return equipa1.get(indice);
        else return equipa2.get(indice);
    }
    public void championSelect(){

    }
}
