import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Ajedrez extends JFrame {
    private JButton[][] squares = new JButton[8][8];

    public Ajedrez () {
        setTitle("Ajedrez Básico");
        setSize(600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(8, 8));

        initializeBoard();

        setVisible(true);
    }

    private void initializeBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton button = new JButton();
                button.setOpaque(true);
                button.setBorderPainted(false);

                // Color casilla: blanco o negro
                if ((row + col) % 2 == 0) {
                    button.setBackground(Color.WHITE);
                } else {
                    button.setBackground(Color.DARK_GRAY);
                }

                // Opcional: poner nombre para identificar cada botón
                button.setActionCommand(row + "," + col);

                // Aquí podrías añadir un ActionListener para manejar clicks
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Casilla clickeada: " + e.getActionCommand());
                        // Aquí va la lógica para seleccionar o mover piezas
                    }
                });

                squares[row][col] = button;
                add(button);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Ajedrez());
    }
}
