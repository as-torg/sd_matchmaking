package Banco;

import Banco.Banco;

public class ClienteDeposita implements Runnable{
    private Banco b;

    public ClienteDeposita(Banco b){
        this.b=b;
    }

    @Override
    public void run() {
        int i=0; int[] a = {0, 1, 2};
        b.criarConta(0);
        try {
            b.consultarTotal(a);
        } catch (Banco.ContaInvalida contaInvalida) {
            contaInvalida.printStackTrace();
        }
        ///while(i<1000){ b.depositar(0,5); i++;}
        //b.transferir(0,2,5);
    }
}
