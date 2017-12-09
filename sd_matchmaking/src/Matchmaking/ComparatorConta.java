package Matchmaking;

import java.util.Comparator;

public class ComparatorConta implements Comparator<Conta> {

    public int compare(Conta c1, Conta c2){
        return c1.getRank() - c2.getRank();
    }

    @Override
    public Comparator<Conta> reversed() {
        return null;
    }
}
