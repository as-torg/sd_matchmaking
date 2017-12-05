package Banco;

import Banco.Banco;

import java.util.concurrent.locks.ReentrantLock;

public class ClienteLevanta implements Runnable{
    private Banco b;
    private ReentrantLock lock;

    public ClienteLevanta (Banco b){
        lock = new ReentrantLock();
        this.b=b;
    }

    @Override
    public void run() {
        int i=0;
        try {
            b.transferir(0,1,10);
        } catch (Banco.ContaInvalida contaInvalida) {
            contaInvalida.printStackTrace();
        } catch (Banco.SaldoInsuficiente saldoInsuficiente) {
            saldoInsuficiente.printStackTrace();
        }
        lock.lock();
        try {
            b.fecharConta(1);
        } catch (Banco.ContaInvalida contaInvalida) {
            contaInvalida.printStackTrace();
        }
        lock.unlock();
        try {
            b.consultar(0);
        } catch (Banco.ContaInvalida contaInvalida) {
            contaInvalida.printStackTrace();
        }
        //while(i<1000){ b.levantar(0,5); i++; }
        //b.levantar(1,1000);

    }
}