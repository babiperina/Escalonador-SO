package model.enums;

public enum Estado {
    EXECUTANDO(1), APTO(2), ESPERANDO(3), FINALIZADO(4), ABORTADO(5);

    private final int valor;

    Estado(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return this.valor;
    }
}

