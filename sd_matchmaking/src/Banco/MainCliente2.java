package Banco;

import Banco.Cliente;

import java.io.IOException;

public class MainCliente2 implements Runnable {
    Cliente c;
    public MainCliente2() throws IOException {
        this.c = new Cliente();
    }
    @Override
    public void run() {
        try {
            c.escreverComando("criarConta 2");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            c.escreverComando("consultar 2");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            c.escreverComando("depositar 2 10");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
