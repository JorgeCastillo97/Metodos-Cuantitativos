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
        mostrarDatos();
        
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
            simplex3(Solve.tabla, Cj, coefRestDer, Zj, CjZj, coefMult, numR, Solve.op, true, true);
        } else {
            System.out.println("NO existen coeficientes negativos en el vector solución!");
            //llamar a actualizaZj() y actualizaCj-Zj(); 
            double zFinal = getzFinalInicio(numR,coefMult,coefRestDer);
            Zj = getZjInicio(Zj.length, numR, coefMult, Solve.tabla);
            CjZj = getCjZjInicio(Cj, Zj);
                    
            System.out.println("cF3 = False, se llama a simplex 2.0 desde constructor.");
            simplex2(Solve.tabla, Cj, coefRestDer,Zj, CjZj, coefMult, zFinal ,numR, Solve.op, false, true, false);
        }
    }
    
    /**
     * Calcula el valor inicial de la función objetivo cuando es llamado simplex 2 desde el constructor
     * @param r Número de restricciones.
     * @param coefMult Coeficientes intercambiados por coeficientes de Cj.
     * @param coefRestDer Coeficientes de lado derecho de las restricciones.
     * @return Valor inicial de la FO cuando simplex 2 es llamado desde el constructor.
     */
    public static double getzFinalInicio(int r, double[] coefMult, double [] coefRestDer) {
        double zFinal= 0.0;
            for(int i =0; i<r; i++) {
                zFinal += (coefMult[i])*(coefRestDer[i]);
            }
            return zFinal;
    }
    
    public static double[] getZjInicio(int ZjL, int r, double [] coefMult, double [][] T) {
        double sumaZjCol = 0.0;
        double Zj2[] = new double[ZjL];
        for(int j =0; j<ZjL; j++) {
            for(int i=0; i<r ; i++) {
                sumaZjCol += (coefMult[i])*(T[i][j]);
            }
            Zj2[j] = sumaZjCol;
            sumaZjCol = 0.0;
        }
        return Zj2;
    }
    
    public static double[] getCjZjInicio(double[] Cj, double [] Zj) {
        double CjZj[] = new double[Cj.length];
        for(int i=0; i<CjZj.length; i++) {
            CjZj[i] = Cj[i] - Zj[i];
        }
        return CjZj;
    }
    
    /**
     * Muestra los datos obtenidos del archivo leido.
     */
    public static void mostrarDatos() {
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
     * @param negativosVS Si existen valores negativos en el vector solución, se llama recursivamente a Simplex 3.0,
     * de lo contrario se llama a Simplex 2.0
     * @param iterInicial Valor booleano que indica si es la primera iteración del problema (primer tabla).
     */
    public static void simplex3(double T[][], double Cj[],double CoefRestDer[], double Zj[], double CjZj[], double coefMult[], int r, int op, boolean negativosVS, boolean iterInicial) {
        System.out.println("SIMPLEX 3.0");
        if (iterInicial) {
            for(double c: coefMult) {
                c = (double)0;
            }
        }
        if(!negativosVS) {
            /*System.out.println("Termina metodo Simplex 3.0");
                simplex2.0();
            */
            System.out.println("No hay negativos en vector solución!");
            System.out.println("CAMBIANDO A SIMPLEX 2.0...");
            
            //::::::::::: CÁLCULO DEL VALOR DE Z (FO) :::::::::::
            double zFinal= 0.0;
            for(int i =0; i<r; i++) {
                zFinal += (coefMult[i])*(CoefRestDer[i]);
            }
            
            simplex2(T, Cj, CoefRestDer, Zj, CjZj, coefMult, zFinal ,r, op, negativosVS, iterInicial, true);
        } else {
            //::::::::::: INICIA CÁLCULO DE Zj y Cj-Zj :::::::::::
            
            //::::::::::: CÁLCULO DE Zj :::::::::::
            double sumaZjCol = 0.0;
            for(int j =0; j<Zj.length; j++) {
                for(int i=0; i<r ; i++) {
                    sumaZjCol += (coefMult[i])*(T[i][j]);
                }
                Zj[j] = sumaZjCol;
                sumaZjCol = 0.0;
            }
            /*for(int i =0; i< Zj.length; i++) {
                System.out.println("Zj[" + i + "] = " + Zj[i]);
            }*/
            //::::::::::: CÁLCULO DEL VALOR DE Z (FO) :::::::::::
            double zFinal= 0.0;
            for(int i =0; i<r; i++) {
                zFinal += (coefMult[i])*(CoefRestDer[i]);
            }
            //System.out.println("Valor de zFinal = " + Zfinal);
            
            //::::::::::: CÁLCULO DEL VALOR DE Cj-Zj :::::::::::
            for(int i=0; i<CjZj.length; i++) {
                CjZj[i] = Cj[i] - Zj[i];
            }
            
            //::::::::::: SE MUESTRA ITERACIÓN :::::::::::
            mostrarIteracion(Cj, T, Zj, zFinal, CjZj, CoefRestDer);
            
            //::::::::::::::    MODIFICACIÓN TABLA ORIGINAL           ::::::::::::::
            
            /*::::::::::::::    SE ELIGE LA FILA           ::::::::::::::
              Siempre se encontrará un valor negativo, ya que bandera negativos = true
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
            System.out.println("Coeficiente mas negativo: " + neg);
            
            //::::::::::: SE ENCUENTRA LA POSICIÓN DE LA FILA :::::::::::
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
            
            //::::::::::: SE DIVIDE LA FILA DE Cj-Zj/FILA ELEGIDA. :::::::::::
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
            
            //::::::::::: FILTRACIÓN DE COCIENTES DISTINTOS DE 0 E INFINITO (M) :::::::::::
            ArrayList<Double> filtrados = new ArrayList<>();
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
            
            //::::::::::: SE ELIGE LA COLUMNA :::::::::::
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
            
            //::::::::::: OBTENEMOS FILA UNITARIA :::::::::::
            double filU[] = new double[4+r];
            for(int i=0; i<filU.length; i++) {
                filU[i] = T[f][i]*reciproco;
            }
            
            /*System.out.println("Fila Unitaria");
            for(int i=0; i<filU.length; i++) {
                System.out.println("filU[" + i + "] = " + filU[i]);
            }*/
            
            //::::::::::: OBTENEMOS COEFICIENTES DE COLUMNA PIVOTE :::::::::::
            double coefsColPiv[] = new double[r];
            for(int i=0; i<r; i++) {
                coefsColPiv[i] = T[i][c];
            }
            /*System.out.println("Coeficientes columna pivote");
            for(double val: coefsColPiv){
                System.out.println(val);
            }*/
            
            //::::::::::: OBTENEMOS COEFICIENTES CONJUGADOS DE COLUMNA PIVOTE :::::::::::
            double coefsColConj [] = new double[coefsColPiv.length];
            
            //::::::::::: SETEAMOS LA UNIDAD EN FILA PIVOTE :::::::::::
            coefsColConj[f] = pivote*reciproco;
            for(int i=0; i<r; i++) {
                if(i != f){
                    coefsColConj[i] = (-1.0)*(coefsColPiv[i]);
                }
            }
            
            /*System.out.println("Coeficientes columna pivote conjugados");
            for(int i=0; i<coefsColConj.length; i++) {
                System.out.println("coefsColConj[" + i + "] = " + coefsColConj[i]);
            }*/
            
            //::::::::::: REEMPLAZO DE LA FILA UNITARIA EN TABLA ORIGINAL :::::::::::
            T[f] = filU;
            /*System.out.println("Nueva tabla con fila unitaria");
            for(double [] fi: T){
                for(double v : fi){
                    System.out.print("\t" + v);
                }
                System.out.println("");
            }*/
            
            //::::::::::: HACEMOS 0 LOS COEFICIENTES DE LA COLUMNA PIVOTE :::::::::::
            for(int i=0; i<r; i++) {
                for(int j =0; j<Cj.length; j++){
                    if(i != f){ //Mientras no sea la fila pivote
                        T[i][j] = ((coefsColConj[i])*(filU[j])) + T[i][j];
                    }
                }
            }
            
            /*System.out.println("Nueva tabla obtenida con columna pivote en 0");
            for(double[] fil: T){
                for(double val : fil){
                    System.out.print("\t" + val);
                }
                System.out.println("");
            }*/
            
            //::::::::::: CALCULO DE LOS NUEVOS VALORES DEL VECTOR SOLUCIÓN :::::::::::
            CoefRestDer[f] = CoefRestDer[f]*reciproco;
            System.out.println("NUEVO CoefRestDer: " + CoefRestDer[f] );
            
            for(int i =0; i<CoefRestDer.length; i++) {
                if( i != f) { //Mientras no sea la fila pivote
                    CoefRestDer[i] = (coefsColConj[i])*(CoefRestDer[f]) + CoefRestDer[i];
                }
            }
            System.out.println("Nuevos coeficientes vector solución");
            for(int i =0; i<CoefRestDer.length; i++) {
                System.out.println("CoefRestDer[" + i + "] = " + CoefRestDer[i]);
            }
            
            //::::::::::: SE INTERCAMBIA COEFICIENTE DE Cj (COLUMNA) EN FILA PIVOTE :::::::::::
            if(c == 0) {        //Se intercambia coef de a
                System.out.println("ENTRA a EN FILA " + f);
                varCoefMult.put(f, "a");
                coefMult[f] = Cj[c];
            } else if(c == 1){  //Se intercambia coef de b
                System.out.println("ENTRA b EN FILA " + f);
                varCoefMult.put(f, "b");
                coefMult[f] = Cj[c];
            } else if(c == 2){  //Se intercambia coef de c
                System.out.println("ENTRA c EN FILA " + f);
                varCoefMult.put(f, "c");
                coefMult[f] = Cj[c];
            } else if(c == 3){  //Se intercambia coef de d
                System.out.println("ENTRA d EN FILA " + f);
                varCoefMult.put(f, "d");
                coefMult[f] = Cj[c];
            } else {            //Se intercambia coef de variable de holgura (0)
                System.out.println("ENTRA var de holgura EN FILA " + f);
                varCoefMult.put(f, "h");
                coefMult[f] = 0;
            }
            /*System.out.println("Nuevos coeficientes de coefMult:");
            for(int i=0; i<coefMult.length; i++){
                System.out.println("coefMult[" + i + "] = " + coefMult[i]);
            }*/
            
            varCoefMult.entrySet().forEach((e) -> {
                System.out.println("Fila " + e.getKey() + " --> " + e.getValue());
            });
            
            //::::::::::: SE VERIFICA QUE NO EXISTEN NEGATIVOS EN VECTOR SOLUCIÓN :::::::::::
            boolean banderaNeg = false;
            System.out.println("Verificando si existen valores negativos en vector solución.");
            for (int i = 0; i < CoefRestDer.length; i++) {
                if( CoefRestDer[i] < 0.0) {
                    System.out.println("Se encontraron valores negativos en el vector solución!");
                    banderaNeg = true;
                    break;
                }
            }
            if(banderaNeg) { //Si se encontraron valores negativos en vectSol
                simplex3(T, Cj, CoefRestDer, Zj, CjZj, coefMult, r, op, true, false);
            } else {
                simplex3(T, Cj, CoefRestDer, Zj, CjZj, coefMult, r, op, false, false);
            }
            
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
     * @param zFinal último valor de FO calculado
     * @param r Número de restricciones.
     * @param op Minimizar/Maximixar función.
     * @param negativosVS Si existen valores negativos en el vector solución, se llama recursivamente a Simplex 3.0
     * @param iterInicial Valor booleano que indica si es la primera iteración del problema (primer tabla).
     * @param cF3 Valor booleano que indica is se deberá actualizar Zj y Cj-Zj
     */
    public static void simplex2(double T[][], double Cj[],double CoefRestDer[], double Zj[], double CjZj[], double coefMult[], double zFinal, int r, int op, boolean negativosVS, boolean iterInicial, boolean cF3) {
        System.out.println("SIMPLEX 2.0");
        if (iterInicial) {
            for(double c: coefMult) {
                c = (double)0;
            }
        }
        if(negativosVS) {
            //simplex3();
        } else {
            //Si la llamada se hizo desde S 3.0
            if(cF3) {
                System.out.println("Se actualizará Zj y Cj-Zj, la llamada se hizo desde S 3.0");
                
                //::::::::::: INICIA CÁLCULO DE Zj y Cj-Zj :::::::::::
                //System.out.println("Se actualiza Zj y Cj-Zj");
                //::::::::::: CÁLCULO DE Zj :::::::::::
                double sumaZjCol = 0.0;
                double Zj2[] = new double[Zj.length];
                for(int j =0; j<Zj.length; j++) {
                    for(int i=0; i<r ; i++) {
                        sumaZjCol += (coefMult[i])*(T[i][j]);
                    }
                    Zj2[j] = sumaZjCol;
                    sumaZjCol = 0.0;
                }
                /*for(int i =0; i< Zj.length; i++) {
                    System.out.println("Zj2[" + i + "] = " + Zj2[i]);
                }*/

                //Asignamos los valores del arreglo Zj2 al arreglo Zj original, ya que se mostrará Zj
                Zj = Zj2;

                //::::::::::: CÁLCULO DEL VALOR DE Cj-Zj :::::::::::
                for(int i=0; i<CjZj.length; i++) {
                    CjZj[i] = Cj[i] - Zj[i];
                }
                //::::::::::: CÁLCULO DEL VALOR DE Z (FO) :::::::::::
                double zFinalcF3=0;
                for(int i =0; i<r; i++) {
                    zFinalcF3 += (coefMult[i])*(CoefRestDer[i]);
                }
                System.out.println("Valor de zFinal3 = " + zFinalcF3);
                
                zFinal = zFinalcF3;
                System.out.println("Zj y Cj-Zj actualizados");
                System.out.println("------------");
            }
            else {
                System.out.println("No se actualizan Zj y Cj-Zj, la llamada se hizo desde S 2.0");
            }
            
            //::::::::::: SE MUESTRA ITERACIÓN :::::::::::
            System.out.println("Iteración antes de modificación de tabla");
            mostrarIteracion(Cj, T, Zj, zFinal, CjZj, CoefRestDer);
            
            
            //::::::::::::::    MODIFICACIÓN TABLA ORIGINAL           ::::::::::::::
            
            if(op == 1) {   //Si se desea maximizar
                
                //::::::::::: SE ELIGE LA COLUMNA :::::::::::
                
                //Columna mas (+) en Cj-Zj
                double positivo=0;
                int c=0, f=0;
                for(int i=0; i<CjZj.length; i++) {
                    if(CjZj[i] > 0) {
                        if (CjZj[i] > positivo) {
                            positivo = CjZj[i];
                        }
                    }
                }
                
                //::::::::::: SE VERIFICA QUE EXISTA COEFICIENTE (+) EN Cj-Zj, DE LO CONTRARIO NO HAY CRITERIO DE ELECCIÓN DE COLUMNA :::::::::::
                if(positivo == 0) { //Si es 0, no se hayaron (-) en Cj-Zj
                    System.out.println("No hay criterio de elección de columna, no hay coeficientes (+) en Cj-Zj");
                    System.out.println("Solución interrumpida!");
                    System.exit(0);
                }
                System.out.println("Coeficiente más (+) en Cj-Zj: " + positivo);
                
                //::::::::::: SE ENCUENTRA LA POSICIÓN DE LA COLUMNA :::::::::::
                for(int j=0; j<CjZj.length; j++) {
                    if(CjZj[j] == positivo) {
                        c = j;
                         break;
                    }
                }
                System.out.println("Encontrado en columna: " + c);
                
                /*Se guardan los coeficientes de la columna elegida*/
                double colEleg[] = new double[r];
                for(int i =0; i<r; i++) {
                    colEleg[i] = T[i][c];
                }
                
                /*System.out.println("COEFICIENTES COLUMNA ELEGIDA");
                for(double v : colEleg) {
                    System.out.println(v);
                }*/
                
                //::::::::::: SE ENCUENTRA EL MENOR COEFICIENTE (+) VecSol / colElegida :::::::::::
                //::::::::::: SE DIVIDE VEC-SOL/ COLUMNA ELEGIDA. :::::::::::
                double cociente[] = new double[r];
                for(int i =0; i<r; i++) {
                    //Si existe división 0/0
                    if(Math.abs(colEleg[i]) == 0.0 && Math.abs(CoefRestDer[i]) == 0.0) {
                        colEleg[i] = CoefRestDer[i]= cociente[i] = 0;
                    } else if(Math.abs(colEleg[i]) == 0.0 && Math.abs(CoefRestDer[i]) != 0.0) {
                        //Divison c/0.
                        /*filEleg[i] = 0;
                        CjZj[i] = 0;*/
                        cociente[i] = M;
                    } else {
                        cociente[i] = CoefRestDer[i] / colEleg[i];  
                    }
                }
                for(double v : cociente) {
                    System.out.println(v);
                }

                //::::::::::: FILTRACIÓN DE COCIENTES DISTINTOS DE 0 E INFINITO (M) :::::::::::
                ArrayList<Double> filtrados = new ArrayList<>();
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
                
                //::::::::::: SE FILTRAN COCIENTES POSITIVOS :::::::::::
                ArrayList<Double> filtradosPos = new ArrayList<>();
                for(int i =0; i<filtrados.size(); i++) {
                    if(filtrados.get(i) > 0) {
                        filtradosPos.add(filtrados.get(i));
                    }
                }
                
                //::::::::::: SE VERIFICA SI SE AGREGARON COEF [+) :::::::::::
                if(filtradosPos.isEmpty()) {
                    System.out.println("No existen coeficientes positivos, no hay criterio para seleccionar fila!");
                    System.exit(0);
                }
                
                System.out.println("Cocientes positivos filtrados");
                for(double v : filtradosPos) {
                    System.out.println(v);
                }
            
                //::::::::::: SE ELIGE LA FILA :::::::::::
                
                //Menor cociente (+)
                double min = filtradosPos.get(0);
                for(int i = 1; i<filtradosPos.size(); i++) {
                    if(filtradosPos.get(i) < min) {
                        min = filtradosPos.get(i);
                    }
                }
                System.out.println("Menor coeficiente (+): " + min);
                //Se encuentra la posición de la fila.
                for(int i=0; i<cociente.length; i++) {
                    if(cociente[i] == min) {
                        f = i;
                        break;
                    }
                }
                System.out.println("Encontrado en fila: " + f);
                
                double pivote = T[f][c];
                double reciproco = 1/pivote;
                System.out.println("Valor pivote = " + pivote);

                //::::::::::: OBTENEMOS FILA UNITARIA :::::::::::
                double filU[] = new double[4+r];
                for(int i=0; i<filU.length; i++) {
                    filU[i] = T[f][i]*reciproco;
                }

                /*System.out.println("Fila Unitaria");
                for(int i=0; i<filU.length; i++) {
                    System.out.println("filU[" + i + "] = " + filU[i]);
                }*/

                //::::::::::: OBTENEMOS COEFICIENTES DE COLUMNA PIVOTE :::::::::::
                double coefsColPiv[] = new double[r];
                for(int i=0; i<r; i++) {
                    coefsColPiv[i] = T[i][c];
                }
                /*System.out.println("Coeficientes columna pivote");
                for(double val: coefsColPiv){
                    System.out.println(val);
                }*/

                //::::::::::: OBTENEMOS COEFICIENTES CONJUGADOS DE COLUMNA PIVOTE :::::::::::
                double coefsColConj [] = new double[coefsColPiv.length];

                //::::::::::: SETEAMOS LA UNIDAD EN FILA PIVOTE :::::::::::
                coefsColConj[f] = pivote*reciproco;
                for(int i=0; i<r; i++) {
                    if(i != f){
                        coefsColConj[i] = (-1.0)*(coefsColPiv[i]);
                    }
                }

                /*System.out.println("Coeficientes columna pivote conjugados");
                for(int i=0; i<coefsColConj.length; i++) {
                    System.out.println("coefsColConj[" + i + "] = " + coefsColConj[i]);
                }*/

                //::::::::::: REEMPLAZO DE LA FILA UNITARIA EN TABLA ORIGINAL :::::::::::
                T[f] = filU;
                /*System.out.println("Nueva tabla con fila unitaria");
                for(double [] fi: T){
                    for(double v : fi){
                        System.out.print("\t" + v);
                    }
                    System.out.println("");
                }*/

                //::::::::::: HACEMOS 0 LOS COEFICIENTES DE LA COLUMNA PIVOTE :::::::::::
                for(int i=0; i<r; i++) {
                    for(int j =0; j<Cj.length; j++){
                        if(i != f){ //Mientras no sea la fila pivote
                            T[i][j] = ((coefsColConj[i])*(filU[j])) + T[i][j];
                        }
                    }
                }

                /*System.out.println("Nueva tabla obtenida con columna pivote en 0");
                for(double[] fil: T){
                    for(double val : fil){
                        System.out.print("\t" + val);
                    }
                    System.out.println("");
                }*/

                //::::::::::: CALCULO DE LOS NUEVOS VALORES DEL VECTOR SOLUCIÓN :::::::::::
                CoefRestDer[f] = CoefRestDer[f]*reciproco;
                System.out.println("NUEVO CoefRestDer: " + CoefRestDer[f] );

                for(int i =0; i<CoefRestDer.length; i++) {
                    if( i != f) { //Mientras no sea la fila pivote
                        CoefRestDer[i] = (coefsColConj[i])*(CoefRestDer[f]) + CoefRestDer[i];
                    }
                }
                System.out.println("Nuevos coeficientes vector solución");
                for(int i =0; i<CoefRestDer.length; i++) {
                    System.out.println("CoefRestDer[" + i + "] = " + CoefRestDer[i]);
                }
                
                //::::::::::: SE INTERCAMBIA COEFICIENTE DE Cj (COLUMNA) EN FILA PIVOTE :::::::::::
                if(c == 0) {        //Se intercambia coef de a
                    System.out.println("ENTRA a EN FILA " + f);
                    varCoefMult.put(f, "a");
                    coefMult[f] = Cj[c];
                } else if(c == 1){  //Se intercambia coef de b
                    System.out.println("ENTRA b EN FILA " + f);
                    varCoefMult.put(f, "b");
                    coefMult[f] = Cj[c];
                } else if(c == 2){  //Se intercambia coef de c
                    System.out.println("ENTRA c EN FILA " + f);
                    varCoefMult.put(f, "c");
                    coefMult[f] = Cj[c];
                } else if(c == 3){  //Se intercambia coef de d
                    System.out.println("ENTRA d EN FILA " + f);
                    varCoefMult.put(f, "d");
                    coefMult[f] = Cj[c];
                } else {            //Se intercambia coef de variable de holgura (0)
                    System.out.println("ENTRA var de holgura EN FILA " + f);
                    varCoefMult.put(f, "h");
                    coefMult[f] = 0;
                }
                /*System.out.println("Nuevos coeficientes de coefMult:");
                for(int i=0; i<coefMult.length; i++){
                    System.out.println("coefMult[" + i + "] = " + coefMult[i]);
                }*/

                varCoefMult.entrySet().forEach((e) -> {
                    System.out.println("Fila " + e.getKey() + " --> " + e.getValue());
                });
                
                
                
                //::::::::::: CALCULAMOS Zj y Cj-Zj con los nuevos coeficientes de intercambio :::::::::::
                System.out.println("llamar actualizaZj() y actualizaCj-Zj()");
                
                //::::::::::: INICIA CÁLCULO DE Zj y Cj-Zj :::::::::::
                //System.out.println("Se actualiza Zj y Cj-Zj");
                //::::::::::: CÁLCULO DE Zj :::::::::::
                double sumaZjCol = 0.0;
                double Zj2[] = new double[Zj.length];
                for(int j =0; j<Zj.length; j++) {
                    for(int i=0; i<r ; i++) {
                        sumaZjCol += (coefMult[i])*(T[i][j]);
                    }
                    Zj2[j] = sumaZjCol;
                    sumaZjCol = 0.0;
                }
                /*for(int i =0; i< Zj.length; i++) {
                    System.out.println("Zj2[" + i + "] = " + Zj2[i]);
                }*/

                //Asignamos los valores del arreglo Zj2 al arreglo Zj original, ya que se mostrará Zj
                Zj = Zj2;

                //::::::::::: CÁLCULO DEL VALOR DE Z (FO) :::::::::::
                double nvozFinal= 0.0;
                for(int i =0; i<r; i++) {
                    nvozFinal += (coefMult[i])*(CoefRestDer[i]);
                }
                //System.out.println("Valor de zFinal = " + Zfinal);
                
                zFinal = nvozFinal;
                
                //::::::::::: CÁLCULO DEL VALOR DE Cj-Zj :::::::::::
                for(int i=0; i<CjZj.length; i++) {
                    CjZj[i] = Cj[i] - Zj[i];
                }
                
                
                //::::::::::: VERIFICACIÓN DE CONDICIONES PARA LLAMADA RECURSIVA S 2.0 :::::::::::
                //:::::::::::                       MAX                                :::::::::::
                
                //::::::::::: SE VERIFICA QUE NO EXISTEN NEGATIVOS EN VECTOR SOLUCIÓN :::::::::::
                /*System.out.println("Iteración antes de verificación en S 2.0");
                mostrarIteracion(Cj, T, Zj, zFinal, CjZj, CoefRestDer);*/
                boolean banderaNeg = false;
                System.out.println("Verificando si existen valores negativos en vector solución.");
                for (int i = 0; i < CoefRestDer.length; i++) {
                    if( CoefRestDer[i] < 0.0) {
                        System.out.println("Se encontraron valores negativos en el vector solución!");
                        banderaNeg = true;
                        break;
                    }
                }
                
                if(banderaNeg == false) {
                    //No hay coeficientes negativos en vector Solución
                    System.out.println("No se encontraron valores negativos en el vector solución");
                    
                    System.out.println("Verificando si existen (+) en Cj-Zj...");
                    boolean p = false;
                    for(int i =0; i<CjZj.length; i++){
                        if(CjZj[i] > 0.0){ p=true; break;}
                    }
                    if(p){
                        System.out.println("Existen (+) en Cj-Zj");
                        simplex2(T, Cj, CoefRestDer, Zj, CjZj, coefMult, zFinal ,r, op, false, false,false);
                    } else {
                        System.out.println("\nNO existen (+) en Cj-Zj");
                        System.out.println("\n############# Termina solución de Problema #############");
                        mostrarIteracion(Cj, T, Zj, min, CjZj, CoefRestDer);
                        varCoefMult.entrySet().forEach((e) -> {
                            System.out.println("Fila " + e.getKey() + " variable " + e.getValue());
                        });
                        System.out.printf("Z = %.2f\n", zFinal);
                        System.exit(0);
                    }
                    
                } else {
                    //Hay coeficientes negativos en vector Solución
                    //simplex 3.0(); con bandera negativosVS = True;
                    System.out.println("Se encontraron coeficientes en Vector Solución negativos!");
                    System.out.println("CAMBIANDO A SIMPLEX 3.0...");
                    simplex3(T, Cj, CoefRestDer, Zj, CjZj, coefMult, r, op, true, false);
                }
                
                
                
                /*Se verifica que no existan (+) en Zj
                    if se encuentran (+) en Zj
                        simplex2();
                    else
                        Termina metodo!
                */
            } else {        //Si se desea minimizar
                
                //::::::::::: SE ELIGE LA COLUMNA :::::::::::
                
                //Columna mas (-) en Cj-Zj
                double negativo=0;
                int c=0, f=0;
                for(int i=0; i<CjZj.length; i++) {
                    if(CjZj[i] < 0) {
                        if (CjZj[i] < negativo) {
                            negativo = CjZj[i];
                        }
                    }
                }
                
                //::::::::::: SE VERIFICA QUE EXISTA COEFICIENTE (-) EN Cj-Zj, DE LO CONTRARIO NO HAY CRITERIO DE ELECCIÓN DE COLUMNA :::::::::::
                if(negativo == 0) { //Si es 0, no se hayaron (-) en Cj-Zj
                    System.out.println("No hay criterio de elección de columna, no hay coeficientes (-) en Cj-Zj");
                    System.out.println("Solución interrumpida!");
                    System.exit(0);
                }
                System.out.println("Coeficiente más (-) en Cj-Zj: " + negativo);
                
                //::::::::::: SE ENCUENTRA LA POSICIÓN DE LA FILA :::::::::::
                for(int i=0; i<CoefRestDer.length; i++) {
                    if(CoefRestDer[i] == negativo) {
                        c = i;
                    }
                }
                System.out.println("Encontrado en columna: " + c);
                
                /*Se guardan los coeficientes de la columna elegida*/
                double colEleg[] = new double[r];
                for(int i =0; i<r; i++) {
                    colEleg[i] = T[i][c];
                }
                
                /*System.out.println("COEFICIENTES COLUMNA ELEGIDA");
                for(double v : colEleg) {
                    System.out.println(v);
                }*/
                
                //::::::::::: SE ENCUENTRA EL MENOR COEFICIENTE (+) VecSol / colElegida :::::::::::
                //::::::::::: SE DIVIDE VEC-SOL/ COLUMNA ELEGIDA. :::::::::::
                double cociente[] = new double[r];
                for(int i =0; i<r; i++) {
                    //Si existe división 0/0
                    if(Math.abs(colEleg[i]) == 0.0 && Math.abs(CoefRestDer[i]) == 0.0) {
                        colEleg[i] = CoefRestDer[i]= cociente[i] = 0;
                    } else if(Math.abs(colEleg[i]) == 0.0 && Math.abs(CoefRestDer[i]) != 0.0) {
                        //Divison c/0.
                        /*filEleg[i] = 0;
                        CjZj[i] = 0;*/
                        cociente[i] = M;
                    } else {
                        cociente[i] = CoefRestDer[i] / colEleg[i];  
                    }
                }
                for(double v : cociente) {
                    System.out.println(v);
                }

                //::::::::::: FILTRACIÓN DE COCIENTES DISTINTOS DE 0 E INFINITO (M) :::::::::::
                ArrayList<Double> filtrados = new ArrayList<>();
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
                
                //::::::::::: SE FILTRAN COCIENTES POSITIVOS :::::::::::
                ArrayList<Double> filtradosPos = new ArrayList<>();
                for(int i =0; i<filtrados.size(); i++) {
                    if(filtrados.get(i) > 0) {
                        filtradosPos.add(filtrados.get(i));
                    }
                }
                
                //::::::::::: SE VERIFICA SI SE AGREGARON COEF [+) :::::::::::
                if(filtradosPos.isEmpty()) {
                    System.out.println("No existen coeficientes positivos, no hay criterio para seleccionar fila!");
                    System.exit(0);
                }
                
                System.out.println("Cocientes positivos filtrados");
                for(double v : filtradosPos) {
                    System.out.println(v);
                }
            
                //::::::::::: SE ELIGE LA FILA :::::::::::
                
                //Menor cociente (+)
                double min = filtradosPos.get(0);
                for(int i = 1; i<filtradosPos.size(); i++) {
                    if(filtradosPos.get(i) < min) {
                        min = filtradosPos.get(i);
                    }
                }
                System.out.println("Menor coeficiente (+): " + min);
                //Se encuentra la posición de la fila.
                for(int i=0; i<cociente.length; i++) {
                    if(cociente[i] == min) {
                        f = i;
                        break;
                    }
                }
                System.out.println("Encontrado en fila: " + f);
                
                double pivote = T[f][c];
                double reciproco = 1/pivote;
                System.out.println("Valor pivote = " + pivote);

                //::::::::::: OBTENEMOS FILA UNITARIA :::::::::::
                double filU[] = new double[4+r];
                for(int i=0; i<filU.length; i++) {
                    filU[i] = T[f][i]*reciproco;
                }

                /*System.out.println("Fila Unitaria");
                for(int i=0; i<filU.length; i++) {
                    System.out.println("filU[" + i + "] = " + filU[i]);
                }*/

                //::::::::::: OBTENEMOS COEFICIENTES DE COLUMNA PIVOTE :::::::::::
                double coefsColPiv[] = new double[r];
                for(int i=0; i<r; i++) {
                    coefsColPiv[i] = T[i][c];
                }
                /*System.out.println("Coeficientes columna pivote");
                for(double val: coefsColPiv){
                    System.out.println(val);
                }*/

                //::::::::::: OBTENEMOS COEFICIENTES CONJUGADOS DE COLUMNA PIVOTE :::::::::::
                double coefsColConj [] = new double[coefsColPiv.length];

                //::::::::::: SETEAMOS LA UNIDAD EN FILA PIVOTE :::::::::::
                coefsColConj[f] = pivote*reciproco;
                for(int i=0; i<r; i++) {
                    if(i != f){
                        coefsColConj[i] = (-1.0)*(coefsColPiv[i]);
                    }
                }

                /*System.out.println("Coeficientes columna pivote conjugados");
                for(int i=0; i<coefsColConj.length; i++) {
                    System.out.println("coefsColConj[" + i + "] = " + coefsColConj[i]);
                }*/

                //::::::::::: REEMPLAZO DE LA FILA UNITARIA EN TABLA ORIGINAL :::::::::::
                T[f] = filU;
                /*System.out.println("Nueva tabla con fila unitaria");
                for(double [] fi: T){
                    for(double v : fi){
                        System.out.print("\t" + v);
                    }
                    System.out.println("");
                }*/

                //::::::::::: HACEMOS 0 LOS COEFICIENTES DE LA COLUMNA PIVOTE :::::::::::
                for(int i=0; i<r; i++) {
                    for(int j =0; j<Cj.length; j++){
                        if(i != f){ //Mientras no sea la fila pivote
                            T[i][j] = ((coefsColConj[i])*(filU[j])) + T[i][j];
                        }
                    }
                }

                /*System.out.println("Nueva tabla obtenida con columna pivote en 0");
                for(double[] fil: T){
                    for(double val : fil){
                        System.out.print("\t" + val);
                    }
                    System.out.println("");
                }*/

                //::::::::::: CALCULO DE LOS NUEVOS VALORES DEL VECTOR SOLUCIÓN :::::::::::
                CoefRestDer[f] = CoefRestDer[f]*reciproco;
                System.out.println("NUEVO CoefRestDer: " + CoefRestDer[f] );

                for(int i =0; i<CoefRestDer.length; i++) {
                    if( i != f) { //Mientras no sea la fila pivote
                        CoefRestDer[i] = (coefsColConj[i])*(CoefRestDer[f]) + CoefRestDer[i];
                    }
                }
                System.out.println("Nuevos coeficientes vector solución");
                for(int i =0; i<CoefRestDer.length; i++) {
                    System.out.println("CoefRestDer[" + i + "] = " + CoefRestDer[i]);
                }
                
                //::::::::::: SE INTERCAMBIA COEFICIENTE DE Cj (COLUMNA) EN FILA PIVOTE :::::::::::
                if(c == 0) {        //Se intercambia coef de a
                    System.out.println("ENTRA a EN FILA " + f);
                    varCoefMult.put(f, "a");
                    coefMult[f] = Cj[c];
                } else if(c == 1){  //Se intercambia coef de b
                    System.out.println("ENTRA b EN FILA " + f);
                    varCoefMult.put(f, "b");
                    coefMult[f] = Cj[c];
                } else if(c == 2){  //Se intercambia coef de c
                    System.out.println("ENTRA c EN FILA " + f);
                    varCoefMult.put(f, "c");
                    coefMult[f] = Cj[c];
                } else if(c == 3){  //Se intercambia coef de d
                    System.out.println("ENTRA d EN FILA " + f);
                    varCoefMult.put(f, "d");
                    coefMult[f] = Cj[c];
                } else {            //Se intercambia coef de variable de holgura (0)
                    System.out.println("ENTRA var de holgura EN FILA " + f);
                    varCoefMult.put(f, "h");
                    coefMult[f] = 0;
                }
                /*System.out.println("Nuevos coeficientes de coefMult:");
                for(int i=0; i<coefMult.length; i++){
                    System.out.println("coefMult[" + i + "] = " + coefMult[i]);
                }*/

                varCoefMult.entrySet().forEach((e) -> {
                    System.out.println("Fila " + e.getKey() + " --> " + e.getValue());
                });
                
                //::::::::::: CALCULAMOS Zj y Cj-Zj con los nuevos coeficientes de intercambio :::::::::::
                System.out.println("llamar actualizaZj() y actualizaCj-Zj()");
                
                //::::::::::: INICIA CÁLCULO DE Zj y Cj-Zj :::::::::::
                //System.out.println("Se actualiza Zj y Cj-Zj");
                //::::::::::: CÁLCULO DE Zj :::::::::::
                double sumaZjCol = 0.0;
                double Zj2[] = new double[Zj.length];
                for(int j =0; j<Zj.length; j++) {
                    for(int i=0; i<r ; i++) {
                        sumaZjCol += (coefMult[i])*(T[i][j]);
                    }
                    Zj2[j] = sumaZjCol;
                    sumaZjCol = 0.0;
                }
                /*for(int i =0; i< Zj.length; i++) {
                    System.out.println("Zj2[" + i + "] = " + Zj2[i]);
                }*/

                //Asignamos los valores del arreglo Zj2 al arreglo Zj original, ya que se mostrará Zj
                Zj = Zj2;

                //::::::::::: CÁLCULO DEL VALOR DE Z (FO) :::::::::::
                double nvozFinal= 0.0;
                for(int i =0; i<r; i++) {
                    nvozFinal += (coefMult[i])*(CoefRestDer[i]);
                }
                //System.out.println("Valor de zFinal = " + Zfinal);
                
                zFinal = nvozFinal;
                
                //::::::::::: CÁLCULO DEL VALOR DE Cj-Zj :::::::::::
                for(int i=0; i<CjZj.length; i++) {
                    CjZj[i] = Cj[i] - Zj[i];
                }
                
                //::::::::::: VERIFICACIÓN DE CONDICIONES PARA LLAMADA RECURSIVA S 2.0 :::::::::::
                //:::::::::::                       MIN                                :::::::::::
                
                //::::::::::: SE VERIFICA QUE NO EXISTEN NEGATIVOS EN VECTOR SOLUCIÓN :::::::::::
                boolean banderaNeg = false;
                System.out.println("Verificando si existen valores negativos en vector solución.");
                for (int i = 0; i < CoefRestDer.length; i++) {
                    if( CoefRestDer[i] < 0.0) {
                        System.out.println("Se encontraron valores negativos en el vector solución!");
                        banderaNeg = true;
                        break;
                    }
                }
                if(banderaNeg == false) {
                    System.out.println("No se encontraron valores negativos en el vector solución");
                    
                    System.out.println("Verificando si existen (-) en Cj-Zj...");
                    boolean n = false;
                    for(int i =0; i<CjZj.length; i++){
                        if(CjZj[i] < 0.0){ n=true; break;}
                    }
                    if(n){
                        System.out.println("Existen (-) en Cj-Zj");
                        simplex2(T, Cj, CoefRestDer, Zj, CjZj, coefMult, zFinal ,r, op, false, false, false);
                    } else {
                        System.out.println("\nNO existen (-) en Cj-Zj");
                        System.out.println("\n############# Termina solución de Problema #############");
                        mostrarIteracion(Cj, T, Zj, min, CjZj, CoefRestDer);
                        varCoefMult.entrySet().forEach((e) -> {
                            System.out.println("Fila " + e.getKey() + " variable " + e.getValue());
                        });
                        System.out.printf("Z = %.2f\n", zFinal);
                        System.exit(0);
                    }
                    
                } else {
                    //Hay coeficientes negativos en vector Solución
                    //simplex 3.0(); con bandera negativosVS = True;
                    System.out.println("Se encontraron coeficientes en Vector Solución negativos!");
                    System.out.println("CAMBIANDO A SIMPLEX 3.0...");
                    simplex3(T, Cj, CoefRestDer, Zj, CjZj, coefMult, r, op, true, false);
                }
                
                
                
                
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
            System.out.printf("\t%.2f", Cj[i]);
        }
        System.out.println("");
        for(int i =0; i<T.length; i++) {
            for(int j=0; j<T[i].length; j++) {
                System.out.printf("\t%.2f", T[i][j]);
            }
            System.out.printf("\t%.2f\n", coefRestDer[i]);
        }
        System.out.print("Zj");
        for(int i=0; i<Zj.length; i++) {
            System.out.printf("\t%.2f", Zj[i]);
        }
        System.out.printf("\t%.2f\n", valFO);
        
        System.out.print("Cj-Zj");
        for(int i =0; i<CjZj.length; i++) {
            System.out.printf("\t%.2f", CjZj[i]);
        }
        System.out.println("");
    }
}
