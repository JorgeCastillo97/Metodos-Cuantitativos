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
        mostarDatos();
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
    }
}
