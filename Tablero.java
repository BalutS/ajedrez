public class Tablero {
    private Pieza[][] tablero = new Pieza[8][8];
    private ColorPieza turnoActual;
    private int[] ultimoMovimientoDoblePeon = null;

    public Tablero() {
        setupInicial();
        this.turnoActual = ColorPieza.BLANCO;
    }

    public ColorPieza getTurnoActual() {
        return turnoActual;
    }

    public void cambiarTurno() {
        turnoActual = (turnoActual == ColorPieza.BLANCO) ? ColorPieza.NEGRO : ColorPieza.BLANCO;
    }

    public Pieza getPieza(int fila, int col) {
        return tablero[fila][col];
    }

    public int[] getUltimoMovimientoDoblePeon() {
        return ultimoMovimientoDoblePeon;
    }

    public void moverPieza(int fromFila, int fromCol, int toFila, int toCol) {
        Pieza pieza = getPieza(fromFila, fromCol);

        // Resetear en passant
        ultimoMovimientoDoblePeon = null;

        // Lógica de Enroque
        if (pieza instanceof Rey && Math.abs(fromCol - toCol) == 2) {
            tablero[toFila][toCol] = pieza;
            tablero[fromFila][fromCol] = null;
            pieza.setHaMovido(true);
            if (toCol == 6) {
                Pieza torre = getPieza(fromFila, 7);
                tablero[fromFila][5] = torre;
                tablero[fromFila][7] = null;
                torre.setHaMovido(true);
            } else {
                Pieza torre = getPieza(fromFila, 0);
                tablero[fromFila][3] = torre;
                tablero[fromFila][0] = null;
                torre.setHaMovido(true);
            }
        } else if (pieza instanceof Peon && fromCol != toCol && getPieza(toFila, toCol) == null) {
            // Lógica de Captura al Paso
            tablero[toFila][toCol] = pieza;
            tablero[fromFila][fromCol] = null;
            tablero[fromFila][toCol] = null; // Capturar el peón
            pieza.setHaMovido(true);
        } else {
            // Movimiento normal
            if (pieza instanceof Peon && Math.abs(fromFila - toFila) == 2) {
                ultimoMovimientoDoblePeon = new int[]{toFila, toCol};
            }
            tablero[toFila][toCol] = pieza;
            tablero[fromFila][fromCol] = null;
            if (pieza != null) {
                pieza.setHaMovido(true);
            }
        }
    }

    private int[] encontrarRey(ColorPieza color) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Pieza p = getPieza(i, j);
                if (p instanceof Rey && p.getColor() == color) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    public boolean estaEnJaque(ColorPieza colorRey) {
        int[] posRey = encontrarRey(colorRey);
        if (posRey == null) return false;
        ColorPieza colorAtacante = (colorRey == ColorPieza.BLANCO) ? ColorPieza.NEGRO : ColorPieza.BLANCO;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Pieza p = getPieza(i, j);
                if (p != null && p.getColor() == colorAtacante) {
                    java.util.List<int[]> movimientos = p.getMovimientosValidos(i, j, this);
                    for (int[] mov : movimientos) {
                        if (mov[0] == posRey[0] && mov[1] == posRey[1]) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean simularMovimientoYVerificarJaque(int fromFila, int fromCol, int toFila, int toCol, ColorPieza colorRey) {
        Pieza piezaMovida = getPieza(fromFila, fromCol);
        Pieza piezaCapturada = getPieza(toFila, toCol);
        tablero[toFila][toCol] = piezaMovida;
        tablero[fromFila][fromCol] = null;
        boolean reyEnJaque = estaEnJaque(colorRey);
        tablero[fromFila][fromCol] = piezaMovida;
        tablero[toFila][toCol] = piezaCapturada;
        return reyEnJaque;
    }

    public boolean tieneMovimientosLegales(ColorPieza color) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Pieza p = getPieza(i, j);
                if (p != null && p.getColor() == color) {
                    java.util.List<int[]> movimientos = p.getMovimientosValidos(i, j, this);
                    for (int[] mov : movimientos) {
                        if (!simularMovimientoYVerificarJaque(i, j, mov[0], mov[1], color)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void promocionarPeon(int fila, int col, String tipoPieza) {
        Pieza peon = getPieza(fila, col);
        if (peon == null || !(peon instanceof Peon)) return;
        ColorPieza color = peon.getColor();
        Pieza nuevaPieza;
        switch (tipoPieza.toLowerCase()) {
            case "reina": nuevaPieza = new Reina(color); break;
            case "torre": nuevaPieza = new Torre(color); break;
            case "alfil": nuevaPieza = new Alfil(color); break;
            case "caballo": nuevaPieza = new Caballo(color); break;
            default: nuevaPieza = new Reina(color); break;
        }
        tablero[fila][col] = nuevaPieza;
    }

    private void setupInicial() {
        // Piezas Negras
        tablero[0][0] = new Torre(ColorPieza.NEGRO);
        tablero[0][1] = new Caballo(ColorPieza.NEGRO);
        tablero[0][2] = new Alfil(ColorPieza.NEGRO);
        tablero[0][3] = new Reina(ColorPieza.NEGRO);
        tablero[0][4] = new Rey(ColorPieza.NEGRO);
        tablero[0][5] = new Alfil(ColorPieza.NEGRO);
        tablero[0][6] = new Caballo(ColorPieza.NEGRO);
        tablero[0][7] = new Torre(ColorPieza.NEGRO);
        for (int i = 0; i < 8; i++) {
            tablero[1][i] = new Peon(ColorPieza.NEGRO);
        }

        // Piezas Blancas
        tablero[7][0] = new Torre(ColorPieza.BLANCO);
        tablero[7][1] = new Caballo(ColorPieza.BLANCO);
        tablero[7][2] = new Alfil(ColorPieza.BLANCO);
        tablero[7][3] = new Reina(ColorPieza.BLANCO);
        tablero[7][4] = new Rey(ColorPieza.BLANCO);
        tablero[7][5] = new Alfil(ColorPieza.BLANCO);
        tablero[7][6] = new Caballo(ColorPieza.BLANCO);
        tablero[7][7] = new Torre(ColorPieza.BLANCO);
        for (int i = 0; i < 8; i++) {
            tablero[6][i] = new Peon(ColorPieza.BLANCO);
        }
    }
}
