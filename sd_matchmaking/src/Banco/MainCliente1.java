package Banco;

import Banco.Cliente;

import java.io.IOException;

public class MainCliente1 implements Runnable {
    Cliente c;
    public MainCliente1() throws IOException {
        this.c = new Cliente();
    }
    @Override
    public void run() {
        try {
            c.escreverComando("criarConta 1");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            c.escreverComando("consultar 1");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
