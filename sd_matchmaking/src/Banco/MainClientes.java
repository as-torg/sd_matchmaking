package Banco;

import Banco.MainCliente1;
import Banco.MainCliente2;

import java.io.IOException;

public class MainClientes {
    public static void main(String[] s) throws IOException {
        MainCliente1 c1 = null;
        c1 = new MainCliente1();
        MainCliente2 c2 = null;
        c2 = new MainCliente2();
        Thread t1 = new Thread(c1);
        Thread t2 = new Thread(c2);
        t1.start();
        t2.start();
    }
}
