package Banco;

public interface InterfaceBanco {
    int criarConta(double saldo);
    double fecharConta(int id) throws Banco.ContaInvalidaException;
    double consultar(int id) throws Banco.ContaInvalidaException;
    double consultarTotal(int [] contas) throws Banco.ContaInvalidaException;
    void levantar(int id, double valor) throws Banco.ContaInvalidaException, SaldoInsuficiente;
    void depositar(int id, double valor) throws Banco.ContaInvalidaException;
    void transferir(int conta_origem, int conta_destino, double valor) throws Banco.ContaInvalidaException, SaldoInsuficiente;

}
