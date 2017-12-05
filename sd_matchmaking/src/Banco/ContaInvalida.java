package Banco;

class ContaInvalida extends Exception {
    // Parameterless Constructor
    public ContaInvalida() {
    }

    // Constructor that accepts a message
    public ContaInvalida(String message) {
        super(message);
    }
}
