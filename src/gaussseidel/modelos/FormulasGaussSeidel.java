package gaussseidel.modelos;

import java.util.ArrayList;

public class FormulasGaussSeidel {

    private double[][] matriz;
    private double[][] matrizOrdenada;

    /*Este metodo se encarga de ordenar la matriz para obtener la diagonal dominante*/
    public int compruebaDiagonalDominante(double[][] matriz) {

        int contadorDiagonalDominante = 0; //Si el valor es igual a numero de filas tiene diagonal dominante

        ArrayList<int[]> valoresMayores = new ArrayList();//me sirve para ordenar la matriz y obtener la diagonal
        for (int i = 0; i < matriz.length; i++) {
            valoresMayores.add(obtienePosValorMayor(matriz, i));
            if (i == valoresMayores.get(i)[1]) {
                contadorDiagonalDominante++;
            }
        }

        /*
            0.1 7.0 -0.3 = -19.3
            0.3 -0.2 -10.0 = 71.4
            3.0 -0.1 -0.2 = 7.85
            posiciones: fila, columna. Indican la posicion del valor Xi que es mayor a los demas Xi + 1
            0, 1
            1, 2
            2, 0

            7.0 0.1 -0.3 = -19.3
            0.3 -0.2 -10.0 = 71.4
            3.0 -0.1 -0.2 = 7.85

            0, 0
            1, 2
            2, 0
         */
 /*Como podran obervar en el patron anterior las posicion indica donde se encuentra el valor Xi
        que es mayor a todos los demas Xi+1, si la posicion de columna de un valor mayor coincide con otra,
        entonces no se podra obtener una diagonal dominante*/
        for (int i = 0; i < valoresMayores.size(); i++) {
            int posicion = valoresMayores.get(i)[1];

            for (int j = 0; j < valoresMayores.size(); j++) {
                if (i != j) {
                    if (posicion == valoresMayores.get(j)[1]) {
                        return -1;
                    }
                }
            }
        }

        if (contadorDiagonalDominante == matriz.length) {
            System.out.println("tiene diagonal dominante");
            this.matriz = matriz;
            return 0;
        } else {
            System.out.println("no tiene diagonal dominante");
            //acomodando matriz
            matrizOrdenada = new double[matriz.length][matriz[0].length];
            for (int i = 0; i < matriz.length; i++) {//filas
                for (int j = 0; j < matriz[0].length; j++) {//columnas
                    /*Obteniendo el numero de posicion donde se encuentra el valor que es mayor a 
                    los demas en la ecuacion, determino a que fila se debe cambiar la ecuacion*/
                    int numFilaMover = valoresMayores.get(i)[1];
                    matrizOrdenada[numFilaMover][j] = matriz[i][j];
                }
            }

            System.out.println("Ecuacion ordenada");

            for (int i = 0; i < getMatrizOrdenada().length; i++) {
                for (int j = 0; j < getMatrizOrdenada()[0].length; j++) {
                    if (j == getMatrizOrdenada()[0].length - 1) {
                        System.out.print("= " + getMatrizOrdenada()[i][j]);
                    } else {
                        System.out.print(getMatrizOrdenada()[i][j] + " ");
                    }
                }
                System.out.println();
            }

            return 1;

        }

    }

    /*Este metodo devuelve la posicion del valor mayor, devuelve la fila y columna.
    Con esto es posible ordenar la matriz para obtener la diagonal dominante*/
    public int[] obtienePosValorMayor(double[][] ecua, int fila) {
        /*Son los valores de acuerdo a la ecuacion (fila=1 es la ecuacion 1)*/
        int numColumnas = ecua[0].length - 1;
        double[] valFila = new double[numColumnas];
        int contadorTrue = 0;//este valor va incrementando conforme x es mayor a los demas valores.
        int numerosComparacion = ecua[0].length - 2;
        for (int j = 0; j < numColumnas; j++) {//recorriendo columnas
            valFila[j] = ecua[fila][j];//guardando ecuacion especificada de acuerdo al numero de fila
        }
        for (int i = 0; i < numColumnas; i++) {
            double x = Math.abs(valFila[i]);
            contadorTrue = 0;
            for (int j = 0; j < numColumnas; j++) {
                if (i != j) {
                    if (x >= Math.abs(valFila[j])) {
                        contadorTrue++;
                        if (contadorTrue == numerosComparacion) {
                            int[] pos = new int[2];
                            pos[0] = fila;
                            pos[1] = i;//columna
                            return pos;
                        }
                    }
                }
            }
        }
        return null;
    }

    //Vector actual es como el vector incial
    public ArrayList<double[]> metodoGaussSeidel(double[] vectorActual, double[][] matriz, double tolerancia) {
        int filas = matriz.length;
        double[] vectorAnterior = new double[filas];
        /*guardo los valores de Xn y sus Ea por este motivo el tamaño es el numero de filas * 2 
        las primeras 4 posiciones guardan valores de Xi las otras 4 guardan errores aproximados*/
        double[] vectorValores = new double[filas * 2];
        ArrayList<double[]> valores = new ArrayList<>();//almacena todos los valores de Xi (se pretenden agregar a una tabla estos valores)
        double bi = 0;
        double xi = 0;
        double ai = 0;
        double errorAproximado = 0;
        /*Si el contador es igual al numero de filas, quiere decir que los resultados de xi son menores a la tolerancia*/
        int contadorCumpleCondicion = 0;
        int k = 0;//numero de iteraciones
        while (contadorCumpleCondicion != matriz.length) {
            contadorCumpleCondicion = 0;
            for (int i = 0; i < matriz.length; i++) {
                bi = matriz[i][matriz[0].length - 1];
                for (int j = 0; j < matriz[0].length - 1; j++) {
                    if (matriz[i][i] != matriz[i][j]) {
                        ai += -matriz[i][j] * vectorActual[j];
                    }
                }
                vectorAnterior[i] = vectorActual[i];
                xi = (bi + ai) / matriz[i][i];
                ai = 0; //limpio la variable para calcular nuevos valores
                vectorActual[i] = xi;
                errorAproximado = Math.abs(vectorActual[i] - vectorAnterior[i]);
                if ((errorAproximado) <= tolerancia) {
                    contadorCumpleCondicion++;
                }
                //Guardando errores aproximados
                vectorValores[filas + i] = errorAproximado;
            }
            //Guardando los valores de Xi en las posiciones correspondientes
            System.arraycopy(vectorActual, 0, vectorValores, 0, vectorValores.length - filas);
            valores.add((double[]) vectorValores.clone());
            k++;
        }
        return valores;
    }

    public double[][] getMatriz() {
        return matriz;
    }

    public double[][] getMatrizOrdenada() {
        return matrizOrdenada;
    }

}
