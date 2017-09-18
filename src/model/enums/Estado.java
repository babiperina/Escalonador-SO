package model.enums;

public enum Estado {
    EXECUTANDO(1), APTO(2), FINALIZADO(3), ABORTADO(4);

    private final int valor;

    Estado(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return this.valor;
    }
}