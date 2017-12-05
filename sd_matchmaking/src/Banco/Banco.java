package Banco;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

public class Banco implements InterfaceBanco {

    ReentrantLock lock;
    private ArrayList<Conta> contas;

    public Banco(int n){
        lock = new ReentrantLock();
        int i=0;
        contas = new ArrayList<Conta>();
        for(i=0;i<n;i++){
            this.contas.add(new Conta(0));
        }
    }

    public double consultar(int n)throws ContaInvalida {
        return this.contas.get(n).getSaldo();
    }

    public void depositar(int n, double d)throws ContaInvalida {
        this.contas.get(n).addSaldo(d);
    }

    public void levantar(int n, double d)throws ContaInvalida, SaldoInsuficiente {
        if(this.contas.get(n).getSaldo()>=d) this.contas.get(n).tiraSaldo(d);
    }

    public void transferir(int n1, int n2, double d) throws ContaInvalida, SaldoInsuficiente {
        if(this.contas.get(n1).getSaldo()>=d){
            this.contas.get(n1).tiraSaldo(d);
            this.contas.get(n2).addSaldo(d);
        }
    }

    public int criarConta(double saldo){
        Conta c = new Conta(saldo);
        this.contas.add(c);
        return contas.size()-1;
    }

    public double fecharConta(int id) throws ContaInvalida {//faltam muitas coisas
        double saldo = 0;
        lock.lock();
        saldo = this.contas.get(id).getSaldo();
        this.contas.get(id).tiraSaldo(saldo);
        this.contas.remove(id);
        lock.unlock();
        return saldo;
    }

    public double consultarTotal(int contas[]) throws ContaInvalida {//é preciso dar lock a todas as contas antes de consultar o total, para que a "imagem" não mude durante a execução
        lock.lock();
        double saldo=0;
        //é também usado um lockBanco para evitar que se mexa no arraylist
        //um ciclo for para dar lock nas contas do banco

        for( int i : contas){
            System.out.print("Conta " + i + ": size " + this.contas.size() + "\n");
            try{
                saldo += this.contas.get(i).getSaldo();
            }
            catch (Exception e){}


            try{Thread.currentThread().sleep(1000);}catch (Exception e){}
        }
        lock.unlock();
        return saldo;
    }
}