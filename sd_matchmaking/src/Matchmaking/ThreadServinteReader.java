package Matchmaking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class ThreadServinteReader implements Runnable {
    private BufferedReader fromClienteGestor;//lê de um canal escrito pelo cliente ou servidor
    private BufferedWriter toWriter;//envia ao escritor
    private BufferedWriter toGestor;//envia à thread do gestor

    BancoContasJogadores banco; //variável partilhada, recebida via argumento
    Conta contaJogador; //variável partilhada, recebida via argumento
    public ThreadServinteReader(BufferedReader fromClienteGestor, BufferedWriter toWriter, BufferedWriter toGestor){
        this.fromClienteGestor = fromClienteGestor;
        this.toWriter = toWriter;
        this.toGestor = toGestor;
    }

    @Override
    public void run() {


    }
}
