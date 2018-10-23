/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metodos;

import java.util.ArrayList;
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
    //HashMap para identificar la posicion de la fila y la variable que entra en cada iteración.
    private static HashMap<Integer,String> varCoefMult;
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
        this.varCoefMult = new HashMap<>(restricciones);
        System.out.println("Recibiendo datos...");
        Cj = getCj(coefFuncObj,numR);
        Solve.tabla = getTablaToSolve(Solve.coefRest, matHolgura,numR );
        //mostarDatos();
        
        //Se crea el arreglo Zj.
        double Zj[] = new double[numR+4];
        //El valor final de la funcion objetivo es calculado independientemente.
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
        for(double v : coefRestDer) {
            if(v < (double)0){ metodo=true; break;}
            else metodo=false;
        }
        if(metodo == true){
            System.out.println("Existen coeficientes negativos en el vector solución!");
            simplex3(Solve.tabla, Cj, coefRestDer, Zj, CjZj, coefMult, numR, Solve.op, (double)0, 0, true, true);
        } else {
            System.out.println("NO existen coeficientes negativos en el vector solución!");
            simplex2(Solve.tabla, Cj, coefRestDer,Zj, CjZj, coefMult, numR, Solve.op, (double)0, 0, false,true);
        }
    }
    
    /**
     * Muestra los datos obtenidos del archivo leido.
     */
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
        /*System.out.println(":::::::::::::::MATRIZ CON COEFICIENTES DE VARIABLES DE HOLGURA:::::::::::::::");
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
        }*/
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
    public static double[] getCj(HashMap<String, Double> FO, int r) {
        double cj[] = new double[FO.size() + r];
        cj[0] = FO.get("a");
        cj[1] = FO.get("b");
        cj[2] = FO.get("c");
        cj[3] = FO.get("d");
        for(int i=4; i<cj.length; i++) {
            cj[i] = (double)0;
        }
        
        return cj;
    }
    
    /**
     * Método de solución Simplex 3.0
     * @param T tabla del problema planteado.
     * @param Cj Arreglo con los cieficientes de la función objetivo.
     * @param CoefRestDer Coeficientes de las restricciones del lado derecho.
     * @param Zj Arreglo con los coeficientes de Zj.
     * @param CjZj Arreglo con los coeficientes de Cj-Zj.
     * @param coefMult Arreglo con los coeficientes de la función objetivo que van a estar intercambiandose.
     * @param r Número de restricciones.
     * @param op Minimizar/Maximixar función.
     * @param coefEntra Valor del coeficiente de Cj que se intercambia en una determinada fila.
     * @param filaCambio Fila donde se sustituirá el coeficiente de Cj en coefMult.
     * @param negativos Si existen valores negativos en el vector solución, se llama recursivamente a Simplex 3.0,
     * de lo contrario se llama a Simplex 2.0
     * @param iterInicial Valor booleano que indica si es la primera iteración del problema (primer tabla).
     */
    public static void simplex3(double T[][], double Cj[],double CoefRestDer[], double Zj[], double CjZj[], double coefMult[], int r, int op, double coefEntra, int filaCambio, boolean negativos, boolean iterInicial) {
        System.out.println("SIMPLEX 3.0");
        if (iterInicial) {
            for(double c: coefMult) {
                c = (double)0;
            }
        }
        if(!negativos) {
            /*System.out.println("Termina metodo Simplex 3.0");
                simplex2.0();
            */
        } else {
            //Inicia calculo de zj y cj-zj
            //Si es la primera iteración, coeficientes de coefMult valen 0, no se realiza cambio
            if(iterInicial) {
                //System.out.println("No hay intercambio de filas, primera iteración.");
            } else {
                //System.out.println("Hay intercambio de filas.");
            }
            // Cálculo del vector Zj.
            double sumaZjCol = 0.0;
            for(int j =0; j<Zj.length; j++) {
                for(int i=0; i<r ; i++) {
                    sumaZjCol += (coefMult[i])*(T[i][j]);
                }
                Zj[j] = sumaZjCol;
            }
            /*for(int i =0; i< Zj.length; i++) {
                System.out.println("Zj[" + i + "] = " + Zj[i]);
            }*/
            // Cálculo del valor de FO.
            double zFinal= 0.0;
            for(int i =0; i<r; i++) {
                zFinal += (coefMult[i])*(CoefRestDer[i]);
            }
            //System.out.println("Valor de zFinal = " + Zfinal);
            
            //Calculo de Cj-Zj
            for(int i=0; i<CjZj.length; i++) {
                CjZj[i] = Cj[i] - Zj[i];
            }
            
            //Una vez calculado Zj, Cj-Zj y zFinal, se muestran los datos de la iteración.
            mostrarIteracion(Cj, T, Zj, zFinal, CjZj, CoefRestDer);
            
            //::::::::::::::    MODIFICACIÓN TABLA ORIGINAL           ::::::::::::::
            
            /*Se obtiene la fila con el coeficiente mas (-) en CoefRestDer
              Siempre se encontrará un valor negativo, ya que la bandera negativos = true
            */
            int f=0;
            double neg=CoefRestDer[0];
            for(int i =1; i<CoefRestDer.length; i++) {
                if(CoefRestDer[i] < 0.0) {
                    if(CoefRestDer[i] < neg) {
                        neg = CoefRestDer[i];
                    }
                }
            }
            System.out.println("Coeficiente mas negativo:" + neg);
            //Se encuentra la posición de la fila
            for(int i=0; i<CoefRestDer.length; i++) {
                if(CoefRestDer[i] == neg) {
                    f = i;
                }
            }
            System.out.println("Encontrado en fila: " + f);
            
            /*Se guardan los coeficientes de la fila elegida y el valor del coef. 
              de la restricción del lado derecho.
            */
            double filEleg[] = T[f];
            double coRD = CoefRestDer[f];
            
            // Se divide la fila de Cj-Zj/fila elegida.
            double cociente[] = new double[Cj.length];
            for(int i =0; i<Cj.length; i++) {
                //Si existe división 0/0
                if(Math.abs(filEleg[i]) == 0.0 && Math.abs(CjZj[i]) == 0.0) {
                    filEleg[i] = CjZj[i]= cociente[i] = 0;
                } else if(Math.abs(filEleg[i]) == 0.0 && Math.abs(CjZj[i]) != 0.0) {
                    //Divison c/0.
                    /*filEleg[i] = 0;
                    CjZj[i] = 0;*/
                    cociente[i] = M;
                } else {
                    cociente[i] = CjZj[i] / filEleg[i];  
                }
            }
            /*for(double v : cociente) {
                System.out.println(v);
            }*/
            
            //Se filtran los coeficientes distintos de 0 en el vector cociente.
            ArrayList<Double> filtrados = new ArrayList<Double>();
            for(int i =0; i<cociente.length; i++) {
                if(cociente[i] != 0.0 && cociente[i] != M) {
                    filtrados.add(cociente[i]);
                }
            }
            
            //Coeficientes filtrados
            System.out.println("Cocientes filtrados distintos de cero e infinito:");
            filtrados.forEach((fil) -> {
                System.out.println(fil); 
            });
            
            //Se elige la columna
            int c=0;
            double min = Math.abs(filtrados.get(0));
            for(int i = 1; i<filtrados.size(); i++) {
                if(Math.abs(filtrados.get(i)) < min) {
                    min = filtrados.get(i);
                }
            }
            System.out.println("Menor coeficiente (+)(-): " + min);
            //Se encuentra la posición de la columna.
            for(int i=0; i<cociente.length; i++) {
                if(cociente[i] == min) {
                    c = i;
                    break;
                }
            }
            System.out.println("Encontrado en columna: " + c);
            
            double pivote = T[f][c];
            double reciproco = 1/pivote;
            System.out.println("Valor pivote = " + pivote);
            
            //Obtenemos filaUnitaria
            double filU[] = new double[4+r];
            for(int i=0; i<filU.length; i++) {
                filU[i] = T[f][i]*reciproco;
            }
            
            System.out.println("Fila Unitaria");
            for(int i=0; i<filU.length; i++) {
                System.out.println("filU[" + i + "] = " + filU[i]);
            }
            
            
            
            //Obtenemos los coeficientes de arriba y abajo del valor pivote (columna)
            double coefsColPiv[] = new double[r];
            for(int i=0; i<r; i++) {
                coefsColPiv[i] = T[i][c];
            }
            System.out.println("Coeficientes columna pivote");
            for(double val: coefsColPiv){
                System.out.println(val);
            }
            //Obtenemos los conjugados de los coeficientes de la columna pivote.
            double coefsColConj [] = new double[coefsColPiv.length];
            //Seteamos la unidad en la fila del pivote
            coefsColConj[f] = pivote*reciproco;
            for(int i=0; i<r; i++) {
                if(i != f){
                    coefsColConj[i] = (-1.0)*(coefsColPiv[i]);
                }
            }
            
            System.out.println("Coeficientes columna pivote conjugados");
            for(int i=0; i<coefsColConj.length; i++) {
                System.out.println("coefsColConj[" + i + "] = " + coefsColConj[i]);
            }
            
            //Reemplazamos el valor de la fila unitaria en la tabla original
            T[f] = filU;
            /*System.out.println("Nueva tabla con fila unitaria");
            for(double [] fi: T){
                for(double v : fi){
                    System.out.print("\t" + v);
                }
                System.out.println("");
            }*/
            
            /*
            Para saber que coeficiente reemplazar en coefMult, preguntar por col
            Si col =0 ->Entra a
            Si col =1 ->Entra b
            Si col =2 ->Entra c
            Si col =3 ->Entra d
            Si col <0 ->Entra holgura
            Para saber en que fila (en que posición de coefMult, asignar valor de filas
            varCoefMult<fila, "a/b/c/d/h dependiendo valor de  col">
            varCoefMult<1,b> para prob 3 en 1ra iteración.
            */
            
            
            //Coeficientes conjugados para hacer 0 todos los valores de la columna, menos pivote
            /*Para no cambiar el valor unitario del pivote se preguntara en la iteración de la tabla
            por el valor de i, si el valor de i es igual al de la fila encontrada se ignora la fila, 
            de lo contrario, se suma cada valor de filaUnitaria[i]*CoefConjugado[i] + T[i][j];
            y se guada en la misma posicion de la tabla.
            */
            
            
            //simplex3.0();
        }
    }
    
    /**
     * Método de solución Simplex 2.0
     * @param T tabla del problema planteado.
     * @param Cj Arreglo con los cieficientes de la función objetivo.
     * @param CoefRestDer Coeficientes de las restricciones del lado derecho.
     * @param Zj Arreglo con los coeficientes de Zj.
     * @param CjZj Arreglo con los coeficientes de Cj-Zj.
     * @param coefMult Arreglo con los coeficientes de la función objetivo que van a estar intercambiandose.
     * @param r Número de restricciones.
     * @param op Minimizar/Maximixar función.
     * @param coefEntra Valor del coeficiente de Cj que se intercambia en una determinada fila.
     * @param filaCambio Fila donde se sustituirá el coeficiente de Cj en coefMult.
     * @param negativos Si existen valores negativos en el vector solución, se llama recursivamente a Simplex 3.0
     * @param iterInicial Valor booleano que indica si es la primera iteración del problema (primer tabla).
     */
    public static void simplex2(double T[][], double Cj[],double CoefRestDer[], double Zj[], double CjZj[], double coefMult[], int r, int op, double coefEntra, int filaCambio, boolean negativos, boolean iterInicial) {
        System.out.println("SIMPLEX 2.0");
        if(negativos) {
            //simplex3();
        } else {
            //Inicia calculo de zj y cj-zj
            //Si es la primera iteración, coeficientes de coefMult valen 0, no se realiza cambio
            if(iterInicial) {
                //System.out.println("No hay intercambio de filas, primera iteración.");
            } else {
                //System.out.println("Hay intercambio de filas.");
            }
            // Cálculo del vector Zj.
            double sumaZjCol = 0.0;
            for(int j =0; j<Zj.length; j++) {
                for(int i=0; i<r ; i++) {
                    sumaZjCol += (coefMult[i])*(T[i][j]);
                }
                Zj[j] = sumaZjCol;
            }
            /*for(int i =0; i< Zj.length; i++) {
                System.out.println("Zj[" + i + "] = " + Zj[i]);
            }*/
            // Cálculo del valor de FO.
            double zFinal= 0.0;
            for(int i =0; i<r; i++) {
                zFinal += (coefMult[i])*(CoefRestDer[i]);
            }
            //System.out.println("Valor de zFinal = " + Zfinal);
            
            //Calculo de Cj-Zj
            for(int i=0; i<CjZj.length; i++) {
                CjZj[i] = Cj[i] - Zj[i];
            }
            
            //Una vez calculado Zj, Cj-Zj y zFinal, se muestran los datos de la iteración.
            mostrarIteracion(Cj, T, Zj, zFinal, CjZj, CoefRestDer);
            
            
            
            
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
    
    /**
     * Muestra una iteración obtenida.
     * @param Cj Arreglo de coeficientes de función objetivo.
     * @param T Tabla principal del problema.
     * @param Zj Arreglo con los valores del vector Zj.
     * @param valFO Valor obtenido de la evaluación de la función objetivo.
     * @param CjZj Arreglo con los valores del vector Cj-Zj.
     * @param coefRestDer Arreglo con los coeficientes de las restricciones del lado derecho.
     */
    public static void mostrarIteracion(double Cj[], double T[][], double Zj[], double valFO, double CjZj[], double coefRestDer[]) {
        System.out.println(":::::::::::::::::       ITERACION       :::::::::::::::::");
        System.out.println("\ta\tb\tc\td");
        System.out.print("Cj");
        for(int i =0; i<Cj.length; i++) {
            System.out.print("\t" + Cj[i]);
        }
        System.out.println("");
        for(int i =0; i<T.length; i++) {
            for(int j=0; j<T[i].length; j++) {
                System.out.print("\t" + T[i][j]);
            }
            System.out.println("\t" + coefRestDer[i]);
        }
        System.out.print("Zj");
        for(int i=0; i<Zj.length; i++) {
            System.out.print("\t" + Zj[i]);
        }
        System.out.println("\t" + valFO);
        
        System.out.print("Cj-Zj");
        for(int i =0; i<CjZj.length; i++) {
            System.out.print("\t" + CjZj[i]);
        }
        System.out.println("");
    }
}
