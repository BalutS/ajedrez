import java.util.ArrayList;
import java.util.List;

public class Reina extends Pieza {
    public Reina(ColorPieza color) {
        super(color);
    }

    @Override
    public String getUnicodeSymbol() {
        return (color == ColorPieza.BLANCO) ? "\u2655" : "\u265B";
    }

    @Override
    public List<int[]> getMovimientosValidos(int fila, int col, Tablero tablero) {
        List<int[]> movimientos = new ArrayList<>();

        // Direcciones: horizontales, verticales y diagonales
        int[][] direcciones = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };

        for (int[] d : direcciones) {
            int nextFila = fila + d[0];
            int nextCol = col + d[1];

            while (nextFila >= 0 && nextFila < 8 && nextCol >= 0 && nextCol < 8) {
                Pieza piezaEnCasilla = tablero.getPieza(nextFila, nextCol);
                if (piezaEnCasilla == null) {
                    movimientos.add(new int[]{nextFila, nextCol});
                } else {
                    if (piezaEnCasilla.getColor() != this.color) {
                        movimientos.add(new int[]{nextFila, nextCol});
                    }
                    break;
                }
                nextFila += d[0];
                nextCol += d[1];
            }
        }
        return movimientos;
    }
}
