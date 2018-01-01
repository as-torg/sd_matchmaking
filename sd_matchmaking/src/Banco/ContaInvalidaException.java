package Banco;

class ContaInvalidaException extends Exception {
    // Parameterless Constructor
    public ContaInvalidaException() {
    }

    // Constructor that accepts a message
    public ContaInvalidaException(String message) {
        super(message);
    }
}
