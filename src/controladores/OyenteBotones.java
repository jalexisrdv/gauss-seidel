package controladores;

import gaussseidel.modelos.FormulasGaussSeidel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import vistas.PanelGaussSeidel;

public class OyenteBotones implements ActionListener {

    private final FormulasGaussSeidel formulas;
    private final PanelGaussSeidel panel;

    public OyenteBotones(FormulasGaussSeidel formulas, PanelGaussSeidel panel) {
        this.formulas = formulas;
        this.panel = panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JComponent componente = (JComponent) e.getSource();
        switch (componente.getName()) {
            case "botonEstablecerTamano":
                establecerFilasColumnasMatriz();
                establecerFilaColumnasVectorInicial();
                break;
            case "botonCalcularValores":
                JTable tablaMatriz = panel.getTablaMatriz();
                JTable tablaVectorInicial = panel.getVectorInicial();
                if(tablaVectorInicial.isEditing()) {
                    tablaVectorInicial.requestFocus();
                    tablaVectorInicial.getCellEditor().stopCellEditing();
                }
                if(tablaMatriz.isEditing()) {   
                    tablaMatriz.requestFocus();
                    tablaMatriz.getCellEditor().stopCellEditing();
                }
                if (!tablaMatriz.isEditing() && !tablaVectorInicial.isEditing()) {
                    double[][] matriz = obtenerValoresMatriz();
                    double[] vectorInicial = obtenerValoresVectorInicial();
                    double tolerancia = Double.parseDouble(panel.getCampoToleancia().getText());
                    int seOrdenoMatriz = formulas.compruebaDiagonalDominante(matriz);
                    switch(seOrdenoMatriz) {
                        case 1://INDICA QUE LA MATRIZ FUE ORDENADA
                            matriz = formulas.getMatrizOrdenada();
                            JOptionPane.showMessageDialog(panel, "La matriz sera ordenada \n para obtener la diagonal dominante", 
                                    "Matriz Ordenada", JOptionPane.INFORMATION_MESSAGE);
                            agregarValoresTablaMatriz(matriz);
                            break;
                        case 0://NO FUE ORDENADA, YA CUENTA CON DIAGONAL DOMINANTE
                            matriz = formulas.getMatriz();
                        break;
                        default://NO SE PUEDE OBTENER DIAGONAL DOMINANTE
                            JOptionPane.showMessageDialog(panel, "No se puede obtener \n la diagonal dominante", 
                                    "No se puede ordenar", JOptionPane.ERROR_MESSAGE);
                            return ;//salir del metodo
                    }
                    ArrayList<double[]> valores = formulas.metodoGaussSeidel(vectorInicial, matriz, tolerancia);
                    agregarResultados(matriz, valores);
                    panel.getPanelesTablas().setSelectedIndex(1);
                }
                break;
        }
    }
    
    private final void establecerFilaColumnasVectorInicial() {
        int columnas = Integer.parseInt(panel.getCampoColumnas().getText());
        JTable tablaMatriz = panel.getVectorInicial();
        DefaultTableModel modeloTablaMatriz = modeloTablaMatriz = (DefaultTableModel) tablaMatriz.getModel();
        Object[] objetos = new Object[columnas];
        modeloTablaMatriz.setColumnCount(0);
        modeloTablaMatriz.setRowCount(0);
        for (int i = 0; i < columnas; i++) {
            modeloTablaMatriz.addColumn("X" + i);
        }
        modeloTablaMatriz.addRow(objetos);
    }

    private final void establecerFilasColumnasMatriz() {
        int filas = Integer.parseInt(panel.getCampoFilas().getText());
        int columnas = Integer.parseInt(panel.getCampoColumnas().getText());
        JTable tablaMatriz = panel.getTablaMatriz();
        DefaultTableModel modeloTablaMatriz = modeloTablaMatriz = (DefaultTableModel) tablaMatriz.getModel();
        Object[] objetos = new Object[filas];
        modeloTablaMatriz.setColumnCount(0);
        modeloTablaMatriz.setRowCount(0);
        for (int i = 0; i < filas + 2; i++) {
            if (i < filas) {
                modeloTablaMatriz.addColumn("X" + i);
                modeloTablaMatriz.addRow(objetos);
            } else {
                if (i == filas + 1) {
                    modeloTablaMatriz.addColumn("Bi");
                }
            }
        }
    }
    
    private final double[] obtenerValoresVectorInicial() {
        JTable tablaVectorInicial = panel.getVectorInicial();
        DefaultTableModel modeloTablaVectorInicial = (DefaultTableModel) tablaVectorInicial.getModel();
        int columnas = modeloTablaVectorInicial.getColumnCount();
        double[] vector = new double[columnas];
        for (int i = 0; i < columnas; i++) {
            vector[i] = Double.parseDouble(modeloTablaVectorInicial.getValueAt(0, i).toString());
        }
        return vector;
    }

    private final double[][] obtenerValoresMatriz() {
        JTable tablaMatriz = panel.getTablaMatriz();
        DefaultTableModel modeloTablaMatriz = (DefaultTableModel) tablaMatriz.getModel();
        int filas = modeloTablaMatriz.getRowCount();
        int columnas = modeloTablaMatriz.getColumnCount();
        double[][] matriz = new double[filas][columnas];
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                matriz[i][j] = Double.parseDouble(modeloTablaMatriz.getValueAt(i, j).toString());
            }
        }
        return matriz;
    }
    
    private final void agregarValoresTablaMatriz(double[][] matriz) {
        JTable tablaMatriz = panel.getTablaMatriz();
        DefaultTableModel modeloTablaMatriz = (DefaultTableModel) tablaMatriz.getModel();
        int filas = modeloTablaMatriz.getRowCount();
        int columnas = modeloTablaMatriz.getColumnCount();
        modeloTablaMatriz.setRowCount(0);
        Object[] valores = new Object[columnas];
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                valores[j] = matriz[i][j];
            }
            modeloTablaMatriz.addRow(valores);
        }
    }

    private final void agregarResultados(double[][] matriz, ArrayList<double[]> valores) {
        JTable tablaValores = panel.getTablaResultados();
        DefaultTableModel modeloTabla = (DefaultTableModel) tablaValores.getModel();
        modeloTabla.setRowCount(0);
        int filas = matriz.length;
        Object[] valoresFila = new Object[3];
        for (int i = 0; i < valores.size(); i++) {
            valoresFila[0] = "Iteración: " + i;
            valoresFila[1] = "";
            valoresFila[2] = "";
            modeloTabla.addRow(valoresFila);//valor de k
            for (int j = 0; j < filas; j++) {
                valoresFila[0] = "X" + j;//valor de k (iteracion) no se debe ingresar
                valoresFila[1] = valores.get(i)[j];//valor de xi
                valoresFila[2] = valores.get(i)[filas + j];//valor de error aproximado
                modeloTabla.addRow(valoresFila);
            }
        }
    }

}
