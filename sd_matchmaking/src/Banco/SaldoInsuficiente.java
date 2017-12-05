package Banco;

class SaldoInsuficiente extends Exception {
    // Parameterless Constructor
    public SaldoInsuficiente() {
    }

    // Constructor that accepts a message
    public SaldoInsuficiente(String message) {
        super(message);
    }
}
