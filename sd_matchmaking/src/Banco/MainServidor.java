package Banco;

import java.io.IOException;

public class MainServidor {
    public static void main(String[] s) throws Banco.ContaInvalidaException, IOException {
        Banco b = new Banco(0);
        Servidor sv = new Servidor( b);
        sv.servir();

        //às vezes o print vem antes da transferência, outas vezes vem depois. O programa está correto. Para sincronizar o programa temos que fazer um join() (tipo um wait de C)
    }

}
