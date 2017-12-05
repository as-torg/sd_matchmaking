package Banco;

public interface InterfaceBanco {
    int criarConta(double saldo);
    double fecharConta(int id) throws ContaInvalida;
    double consultar(int id) throws ContaInvalida;
    double consultarTotal(int [] contas) throws ContaInvalida;
    void levantar(int id, double valor) throws ContaInvalida, SaldoInsuficiente;
    void depositar(int id, double valor) throws ContaInvalida;
    void transferir(int conta_origem, int conta_destino, double valor) throws ContaInvalida, SaldoInsuficiente;

}
