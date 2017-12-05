package Banco;

import java.util.concurrent.locks.ReentrantLock;

public class Conta {
    private double saldo;
    private ReentrantLock lock;

    public Conta(double saldo){
        this.saldo=saldo;
        lock = new ReentrantLock();

    }
    public void addSaldo(double saldo){
        lock.lock();
        this.saldo += saldo;
        lock.unlock();
    }

    public double getSaldo(){
        double saldo = 0;
        lock.lock();
        saldo = this.saldo;
        lock.unlock();
        return saldo;
    }

    public void tiraSaldo(double saldo){
        lock.lock();
        this.saldo -= saldo;
        lock.unlock();
    }
}
