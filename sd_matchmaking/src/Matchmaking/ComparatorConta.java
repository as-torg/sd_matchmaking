package Matchmaking;

import java.util.Comparator;

public class ComparatorConta implements Comparator<Conta> {

    public int compare(Conta c1, Conta c2){
        int ranks = c1.getRank() - c2.getRank();
        /*
        Se os ranks forem diferentes, retorna essa diferença (ordena segundo o rank)
        Se os ranks forem iguais, retorna a diferença pontual (ordena pelos pontos do jogador)
         */
        if(ranks == 0) {
            return c1.getPontos()-c2.getPontos();
        };
        return ranks;
    }

    @Override
    public Comparator<Conta> reversed() {
        return null;
    }
}
