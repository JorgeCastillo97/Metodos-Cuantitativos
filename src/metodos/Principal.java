package metodos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;
/**
 *
 * @author Jorge Castillo
 */
public class Principal {
    
    //Coeficientes de la función objetivo
    private static HashMap<String, Double> coefFuncObjDec = new HashMap<>();
    
    private static HashMap<Integer,String> restricciones = new HashMap<>();
    
    public static void main(String[] args) {
        byte file, op;
        Scanner teclado = new Scanner(System.in);
        String linea=null;
        System.out.println("Archivo a leer:");
        System.out.println("1) Problema A");
        System.out.println("2) Problema B");
        System.out.println("3) Problema C");
        System.out.println("4) Problema D");
        System.out.println("5) Problema E");    //Problema con Resolución Simplex 2.0
        file = teclado.nextByte();
        System.out.println("1) Minimizar \n2) Maximizar");
        op = teclado.nextByte();
        try {
            switch(file) {
            case 1:
                leerArchivo("Problema_A.txt", op);
                break;
            case 2:
                leerArchivo("Problema_B.txt", op);
                break;
            case 3:
                leerArchivo("Problema_C.txt", op);
                break;
            case 4:
                leerArchivo("Problema_D.txt", op);
                break;
            case 5:
                leerArchivo("Problema_E.txt", op);
                break;
            default:
                System.err.println("Elija una opción válida.");
                break;
            }
        } catch (IOException ioex) {
            System.out.println(ioex.getMessage());
        }
    }
    
    /**
     * Lee el archivo a partir del cual se obtendrán los datos del problema.
     * @param nombre El nombre del archivo que va a ser leido.
     * @param op Minimizar / Maximizar función objetivo.
     * @throws IOException Si ocurre una excepción durante la lectura y/o escritura del archivo.
     */
    public static void leerArchivo(String nombre, int op) throws IOException {
        File f = null;
        FileReader r = null;
        BufferedReader br = null;
        String linea=null,funObjetivo = null;
        StringTokenizer st;
        String aux[];
        double val;
        int i,j;
        //Número de restricciones
        int fil=0;
        //Lectura del archivo
        try {
            f = new File(nombre);
            r= new FileReader(f);
            br = new BufferedReader(r);
            
            while ((linea= br.readLine())!=null) {
                //System.out.println(fil+ " " + linea);
                if(linea.startsWith("z")) {
                    System.out.println("Obtiendo función objetivo...");
                    st = new StringTokenizer(linea);
                    st.nextToken();
                    funObjetivo = st.nextToken();
                    //Obtenemos los coeficientes de la función objetivo
                    coefFuncObjDec = getCoefFuncObj(funObjetivo);
                }
                else {
                    System.out.println("Obteniendo restricción "+ fil +"...");
                    restricciones.put(fil-1, linea);
                }
                fil++;
            }
            double tabla[][]= new double[fil-1][4];
            br.close();
            r.close();
            //Arreglo de coeficientes de restriciones lado dereccho.
            double coefRest[] = new double[fil-1];
            
            System.out.println("Eliminando variables artificiales...");
            
            //Obtenemos nuevos coeficientes de las restricciones.
            for(i=0; i<restricciones.size(); i++) {
                aux = restricciones.get(i).split("[abcd+<>=]+");
                for(j=0; j<aux.length; j++) {
                    //Si hay restricción mayor / mayor igual, cambiamos el signo de la inecuación.
                    if(restricciones.get(i).contains(">")) {
                        //System.out.println("Aux["+ j +"] = "+ aux[j]);
                        val = Double.parseDouble(aux[j]);
                        //System.out.println("val= "+val);
                        if(j == 4) {
                            coefRest[i] = (-1.00)*(Double.parseDouble(aux[j]));
                        } else {
                            tabla[i][j] = (-1.00)*(Double.parseDouble(aux[j]));
                        }
                    } else { //Los coeficientes permanecen igual.
                        if(j == 4) {
                            coefRest[i] = Double.parseDouble(aux[j]);
                        } else {
                            tabla[i][j] = (Double.parseDouble(aux[j]));
                        }
                    }
                }
            }
            
            //Matriz que representa la adición de las variables de holgura.
            double matH[][];
            matH = crearMatrizHolgura(fil-1);
            
            Solve solver = new Solve(op, funObjetivo, coefFuncObjDec, matH.length, tabla, coefRest, matH);
            
        } catch (FileNotFoundException e) {
            System.err.println("Archivo no encontrado.");
        } catch (IOException ioex) {
            System.out.println(ioex.getMessage());
        }
        
    }
    
    /**
     * Llena el HashMap con los coeficientes de la función objetivo.
     * @param s cadena que contiene la funcion objetivo.
     * @return HashMap con los coeficientes de la función objetivo.
     */
    public static HashMap<String, Double> getCoefFuncObj(String s) {
        String aux[];
        aux = s.split("[abcd+]+");
        if(s.contains(".")) {
            System.out.println("Coeficientes decimales...");
            HashMap<String,Double> coeficientesDec = new HashMap<>();
            coeficientesDec.put("a", Double.parseDouble(aux[0]));
            coeficientesDec.put("b", Double.parseDouble(aux[1]));
            coeficientesDec.put("c", Double.parseDouble(aux[2]));
            coeficientesDec.put("d", Double.parseDouble(aux[3]));
            
            /*coeficientesDec.entrySet().forEach((entry) -> {
            System.out.println(entry.getKey() + "=" + entry.getValue());
            });*/
            return coeficientesDec;
        } else {
            HashMap<String,Double> coeficientesDec = new HashMap<>();
            coeficientesDec.put("a", Double.parseDouble(aux[0]));
            coeficientesDec.put("b", Double.parseDouble(aux[1]));
            coeficientesDec.put("c", Double.parseDouble(aux[2]));
            coeficientesDec.put("d", Double.parseDouble(aux[3]));
            
            /*coeficientesDec.entrySet().forEach((entry) -> {
            System.out.println(entry.getKey() + "=" + entry.getValue());
            });*/
            return coeficientesDec;
        }
    }
    
    /**
     * Crea la matriz identidad que representa la adición de los coeficientes de 
     * las variables de holgura para cada restricción menor ó menor-igual encontrada.
     * @param tam Número de restricciones.
     * @return Matriz identidad con coeficientes de las variables de holgura.
     */
    public static double[][] crearMatrizHolgura(int tam) {
        double H[][] = new double[tam][tam];
        for(int i =0; i<tam; i++) {
            for(int j=0; j<tam; j++ ){
                if(i == j) {
                    H[i][j] = (double)1;
                } else H[i][j] = (double)0;
            }
        }
        return H;
    }
    
}
