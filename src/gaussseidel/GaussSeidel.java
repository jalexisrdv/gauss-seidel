package gaussseidel;

import javax.swing.JFrame;
import vistas.PanelGaussSeidel;

/**
 *
 * @author jarv
 */
public class GaussSeidel {

    public static void main(String[] args) {
        PanelGaussSeidel panel = new PanelGaussSeidel();
        JFrame f = new JFrame("Gauss Seidel");
        f.add(panel);
        f.setBounds(300, 100, 600, 500);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }

}
