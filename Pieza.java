package ajedrez;

import java.util.List;

public abstract class Pieza {
    protected ColorPieza color;
    protected boolean haMovido;

    public Pieza(ColorPieza color) {
        this.color = color;
        this.haMovido = false;
    }

    public boolean haMovido() {
        return haMovido;
    }

    public void setHaMovido(boolean haMovido) {
        this.haMovido = haMovido;
    }

    public ColorPieza getColor() {
        return color;
    }

    public abstract String getUnicodeSymbol();

    public abstract List<int[]> getMovimientosValidos(int fila, int col, Tablero tablero);

    @Override
    public String toString() {
        return getUnicodeSymbol();
    }
}
