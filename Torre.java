import java.util.ArrayList;
import java.util.List;

public class Torre extends Pieza {
    public Torre(ColorPieza color) {
        super(color);
    }

    @Override
    public String getUnicodeSymbol() {
        return (color == ColorPieza.BLANCO) ? "\u2656" : "\u265C";
    }

    @Override
    public List<int[]> getMovimientosValidos(int fila, int col, Tablero tablero) {
        List<int[]> movimientos = new ArrayList<>();

        // Direcciones: arriba, abajo, izquierda, derecha
        int[][] direcciones = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] d : direcciones) {
            int nextFila = fila + d[0];
            int nextCol = col + d[1];

            while (nextFila >= 0 && nextFila < 8 && nextCol >= 0 && nextCol < 8) {
                Pieza piezaEnCasilla = tablero.getPieza(nextFila, nextCol);
                if (piezaEnCasilla == null) {
                    // La casilla está vacía, es un movimiento válido
                    movimientos.add(new int[]{nextFila, nextCol});
                } else {
                    // Hay una pieza en la casilla
                    if (piezaEnCasilla.getColor() != this.color) {
                        // Es una pieza enemiga, se puede capturar
                        movimientos.add(new int[]{nextFila, nextCol});
                    }
                    // No se puede seguir moviendo en esta dirección
                    break;
                }
                nextFila += d[0];
                nextCol += d[1];
            }
        }
        return movimientos;
    }
}
