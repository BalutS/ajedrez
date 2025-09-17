import java.util.ArrayList;
import java.util.List;

public class Caballo extends Pieza {
    public Caballo(ColorPieza color) {
        super(color);
    }

    @Override
    public String getUnicodeSymbol() {
        return (color == ColorPieza.BLANCO) ? "\u2658" : "\u265E";
    }

    @Override
    public List<int[]> getMovimientosValidos(int fila, int col, Tablero tablero) {
        List<int[]> movimientos = new ArrayList<>();

        // Todos los 8 posibles movimientos en "L"
        int[][] lMovimientos = {
            {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
            {1, -2}, {1, 2}, {2, -1}, {2, 1}
        };

        for (int[] m : lMovimientos) {
            int nextFila = fila + m[0];
            int nextCol = col + m[1];

            if (nextFila >= 0 && nextFila < 8 && nextCol >= 0 && nextCol < 8) {
                Pieza piezaEnCasilla = tablero.getPieza(nextFila, nextCol);
                // Puede moverse si la casilla está vacía o contiene una pieza enemiga
                if (piezaEnCasilla == null || piezaEnCasilla.getColor() != this.color) {
                    movimientos.add(new int[]{nextFila, nextCol});
                }
            }
        }
        return movimientos;
    }
}
