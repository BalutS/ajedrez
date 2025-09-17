import java.util.ArrayList;
import java.util.List;

public class Rey extends Pieza {
    public Rey(ColorPieza color) {
        super(color);
    }

    @Override
    public String getUnicodeSymbol() {
        return (color == ColorPieza.BLANCO) ? "\u2654" : "\u265A";
    }

    @Override
    public List<int[]> getMovimientosValidos(int fila, int col, Tablero tablero) {
        List<int[]> movimientos = new ArrayList<>();

        // Movimientos normales de un paso
        int[][] reyMovimientos = {
            {-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}
        };
        for (int[] m : reyMovimientos) {
            int nextFila = fila + m[0];
            int nextCol = col + m[1];
            if (nextFila >= 0 && nextFila < 8 && nextCol >= 0 && nextCol < 8) {
                Pieza piezaEnCasilla = tablero.getPieza(nextFila, nextCol);
                if (piezaEnCasilla == null || piezaEnCasilla.getColor() != this.color) {
                    movimientos.add(new int[]{nextFila, nextCol});
                }
            }
        }

        // Lógica de Enroque
        if (!haMovido && !tablero.estaEnJaque(this.color)) {
            // Enroque corto (lado del rey)
            Pieza torreDerecha = tablero.getPieza(fila, 7);
            if (torreDerecha instanceof Torre && !torreDerecha.haMovido()) {
                if (tablero.getPieza(fila, 5) == null && tablero.getPieza(fila, 6) == null) {
                    if (!tablero.simularMovimientoYVerificarJaque(fila, col, fila, 5, this.color) &&
                        !tablero.simularMovimientoYVerificarJaque(fila, col, fila, 6, this.color)) {
                        movimientos.add(new int[]{fila, 6});
                    }
                }
            }
            // Enroque largo (lado de la reina)
            Pieza torreIzquierda = tablero.getPieza(fila, 0);
            if (torreIzquierda instanceof Torre && !torreIzquierda.haMovido()) {
                if (tablero.getPieza(fila, 1) == null && tablero.getPieza(fila, 2) == null && tablero.getPieza(fila, 3) == null) {
                    if (!tablero.simularMovimientoYVerificarJaque(fila, col, fila, 2, this.color) &&
                        !tablero.simularMovimientoYVerificarJaque(fila, col, fila, 3, this.color)) {
                        movimientos.add(new int[]{fila, 2});
                    }
                }
            }
        }
        return movimientos;
    }
}
