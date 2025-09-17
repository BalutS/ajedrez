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

    /**
     * Devuelve una lista de movimientos válidos para esta pieza desde la posición dada.
     * La validación no incluye verificar si el propio rey queda en jaque.
     * @param fila Fila actual de la pieza.
     * @param col Columna actual de la pieza.
     * @param tablero El estado actual del tablero.
     * @return Una lista de arrays de enteros, donde cada array es una coordenada [fila, col].
     */
    public abstract List<int[]> getMovimientosValidos(int fila, int col, Tablero tablero);


    @Override
    public String toString() {
        return getUnicodeSymbol();
    }
}
