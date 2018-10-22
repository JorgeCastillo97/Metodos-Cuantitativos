/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metodos;

import java.util.HashMap;

/**
 *
 * @author Jorge Castillo
 */
public class Solve {
    
    private static int op;
    private static String funcObj;
    private static HashMap<String,Double> coefFuncObj;
    private static int numR;
    private static double coefRest[][];
    private static double coefRestDer[];
    private static double matHolgura[][];
    private static double Cj[];
    private static final double M = 10000.0;
    //TABLA FINAL QUE USARÁ EL METODO SIMPLEX.
    private static double tabla[][];

    /**
     * Constructor de la clase donde se emplea Simplex 2.0 y 3.0
     * @param op Minimizar / Maximizar función objetivo.
     * @param funObj Función objetivo.
     * @param FO HashMap con los coeficientes de la función objetivo.
     * @param restricciones Número de restricciones.
     * @param tabla Matríz con los nuevos coeficientes de cada restricción.
     * @param coefRest Arreglo con los coeficientes de cada restricción, lado derecho.
     * @param matH Matríz con los coeficientes de las variables de holgura.
     */
    public Solve(int op, String funObj,HashMap<String, Double> FO, int restricciones, double [][] tabla, double[] coefRest, double[][] matH) {
        this.op = op;
        this.funcObj = funObj;
        this.coefFuncObj = FO;
        this.numR = restricciones;
        this.coefRest = tabla;
        this.coefRestDer = coefRest;
        this.matHolgura = matH;
        System.out.println("Recibiendo datos...");
        Cj = getCj(coefFuncObj);
        Solve.tabla = getTablaToSolve(Solve.coefRest, matHolgura,numR );
        mostarDatos();
        
        //Se crea el arreglo Zj.
        double Zj[] = new double[numR+5];
        //Se crea arreglo Cj-Zj
        double CjZj[] = new double[numR + 4];

        //Se crea el arreglo coefMult que contiene los coeficientes de la fila de intercambio para obtener Zj.
        double coefMult[] = new double[numR];
        for(int i =0; i<coefMult.length; i++) {
            coefMult[i]= (double)0;
        }
        
        /*  Comienza solución.
            Se elije método de solución a utilizar
            metodo=True --> Simplex 3.0 / metodo=False --> Simplex 2.0
        */
        boolean metodo=false;
        for(double v : Cj) {
            if(v < (double)0){ metodo=true; break;}
            else metodo=false;
        }
        if(metodo == true){
            //simplex3(Solve.tabla, Cj, Zj,CjZj, coefMult ,numR, Solve.op, (double)0, 0, true);
            simplex3(Solve.tabla, Cj, Zj, CjZj, coefMult, numR, Solve.op, (double)0, 0, true);
        } else {
            simplex2(Solve.tabla, Cj, Zj, CjZj, coefMult, numR, Solve.op, (double)0, 0, false);
        }
    }
    
    public static void mostarDatos() {
        System.out.println(":::::::::::::::FUNCIÓN OBJETIVO:::::::::::::::");
        if (op == 1) {
            System.out.print("Max z=");
        } else {
            System.out.print("Min z=");
        }
        System.out.println(funcObj);
        coefFuncObj.entrySet().forEach((e) -> {
            System.out.println(e.getKey() + " ==> " + e.getValue());
        });
        System.out.println("Número de restricciones: " + numR);
        System.out.println(":::::::::::::::COEFICIENTES DE RESTRICCIONES:::::::::::::::");
        for(double fila[]: coefRest) {
            for(double val: fila) {
                System.out.print(val + "\t");
            }
            System.out.println("");
        }
        System.out.println(":::::::::::::::COEFICIENTES DE RESTRICCIONES LADO DERECHO:::::::::::::::");
        for(double r: coefRestDer) {
            System.out.print(r + "\t");
        }
        System.out.println("");
        System.out.println(":::::::::::::::MATRIZ CON COEFICIENTES DE VARIABLES DE HOLGURA:::::::::::::::");
        for(double fil[]: matHolgura) {
            for(double h: fil) {
                System.out.print(h + "\t");
            }
            System.out.println("");
        }
        System.out.println("");
        System.out.println(":::::::::::::::TABLA INICIAL:::::::::::::::");
        for(double fil[]: tabla) {
            for(double c : fil) {
                System.out.print(c + "\t");
            }
            System.out.println("");
        }
    }
    
    /**
     * Obtiene la matriz resultante para resolver el P.P.L., añadiendo los coeficientes 
     * de las variables de holgura con los coeficientes de las restricciones.
     * @param coefRest Coeficientes de restricciones.
     * @param matH Coeficientes variables de holgura.
     * @param t Número de restricciones.
     * @return Matriz final para la solución del problema.
     */
    public static double[][] getTablaToSolve(double [][]coefRest, double[][]matH, int t) {
        double [][]tablaSolve = new double[t][4+t];
        System.out.println("CoefRest.length =" + coefRest.length);
        for(int i=0; i<coefRest.length; i++) {
            for(int j=0; j<4; j++) {
                tablaSolve[i][j] = coefRest[i][j];
            }
        }
        for(int i=0; i< coefRest.length; i++ ) {
            for(int j=4; j<(4+t); j++) {
                tablaSolve[i][j] = matH[i][j-4];
            }
        }
        return tablaSolve;
    }
    
    /**
     * Se llena el arreglo Cj
     * @param FO Coeficientes de la función objetivo.
     * @return Arreglo con coeficientes de la función objetivo.
     */
    public static double[] getCj(HashMap<String, Double> FO) {
        double cj[] = new double[FO.size()];
        cj[0] = FO.get("a");
        cj[1] = FO.get("b");
        cj[2] = FO.get("c");
        cj[3] = FO.get("d");
        
        return cj;
    }
    
    /**
     * Método de solución Simplex 3.0
     * @param T tabla del problema planteado.
     * @param Cj Arreglo con los cieficientes de la función objetivo.
     * @param Zj Arreglo con los coeficientes de Zj.
     * @param CjZj Arreglo con los coeficientes de Cj-Zj.
     * @param coefMult Arreglo con los coeficientes de la función objetivo que van a estar intercambiandose.
     * @param r Número de restricciones.
     * @param op Minimizar/Maximixar función.
     * @param coefEntra Valor del coeficiente de Cj que se intercambia en una determinada fila.
     * @param filaCambio Fila donde se sustituirá el coeficiente de Cj en coefMult.
     * @param negativos Si existen valores negativos en el vector solución, se llama recursivamente a Simplex 3.0
     */
    public static void simplex3(double T[][], double Cj[], double Zj[], double CjZj[], double coefMult[], int r, int op, double coefEntra, int filaCambio, boolean negativos) {
        System.out.println("SIMPLEX 3.0");
        if(!negativos) {
            //System.out.println("Termina metodo Simplex 3.0");
        } else {
            //simplex3.0();
        }
    }
    
    /**
     * Método de solución Simplex 2.0
     * @param T tabla del problema planteado.
     * @param Cj Arreglo con los cieficientes de la función objetivo.
     * @param Zj Arreglo con los coeficientes de Zj.
     * @param CjZj Arreglo con los coeficientes de Cj-Zj.
     * @param coefMult Arreglo con los coeficientes de la función objetivo que van a estar intercambiandose.
     * @param r Número de restricciones.
     * @param op Minimizar/Maximixar función.
     * @param coefEntra Valor del coeficiente de Cj que se intercambia en una determinada fila.
     * @param filaCambio Fila donde se sustituirá el coeficiente de Cj en coefMult.
     * @param negativos Si existen valores negativos en el vector solución, se llama recursivamente a Simplex 3.0
     */
    public static void simplex2(double T[][], double Cj[], double Zj[], double CjZj[], double coefMult[], int r, int op, double coefEntra, int filaCambio, boolean negativos) {
        System.out.println("SIMPLEX 2.0");
        if(negativos) {
            //simplex3();
        } else {
            //Si se desea maximizar
            if(op == 1) {
                /*Se verifica que no existan (+) en Zj
                    if se encuentran (+) en Zj
                        simplex2();
                    else
                        Termina metodo!
                */
            } else {
            //Si se desea minimizar
                /*Se verifica que no existan (-) en Zj
                    if se encuentran (-) en Zj
                        simplex2();
                    else
                        Termina metodo!
                */
            }
        }
    }
}
