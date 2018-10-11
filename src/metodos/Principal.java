package metodos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;
/**
 *
 * @author Jorge Castillo
 */
public class Principal {
    
    //Coeficientes enteros
    private static HashMap<String, Integer> coefFuncObjInt = new HashMap<>();
    //Coeficientes decimales
    private static HashMap<String, Double> coefFuncObjDec = new HashMap<>();
    
    private static HashMap<Byte,String> restricciones = new HashMap<>();
    
    public static void main(String[] args) {
        byte file, op;
        Scanner teclado = new Scanner(System.in);
        String linea=null;
        System.out.println("Archivo a leer:");
        System.out.println("1) Problema A");
        System.out.println("2) Problema B");
        System.out.println("3) Problema C");
        System.out.println("4) Problema D");
        file = teclado.nextByte();
        System.out.println("1) Minimizar \n2) Maximizar");
        op = teclado.nextByte();
        try {
            switch(file) {
            case 1:
                leerArchivo("Problema_A.txt");
                break;
            case 2:
                leerArchivo("Problema_B.txt");
                break;
            case 3:
                leerArchivo("Problema_C.txt");
                break;
            case 4:
                leerArchivo("Problema_D.txt");
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
     * @throws IOException Si ocurre una excepción durante la lectura y/o escritura del archivo.
     */
    public static void leerArchivo(String nombre) throws IOException {
        File f = null;
        FileReader r = null;
        BufferedReader br = null;
        String linea=null,funObjetivo = null;
        StringTokenizer st;
        //numero de restricciones
        byte fil=0;
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
                    if (funObjetivo.contains("\\.")) {
                        coefFuncObjDec = (HashMap<String, Double>)getCoefFuncObj(funObjetivo);
                    } else {
                        coefFuncObjInt = (HashMap<String, Integer>)getCoefFuncObj(funObjetivo);
                    }
                }
                else {
                    System.out.println("Obteniendo restricción "+ fil +"...");
                    restricciones.put(fil, linea);
                }
                fil++;
            }
            br.close();
            r.close();
            
            System.out.println("::::::::::::::::::::FUNCIÓN OBJETIVO::::::::::::::::::::");
            System.out.println("z = " + funObjetivo);
            if (coefFuncObjDec.isEmpty()) {
                coefFuncObjInt.entrySet().forEach((entry) -> {
                System.out.println(entry.getKey() + " = " + entry.getValue());
                });
            } else if(coefFuncObjInt.isEmpty()) {
                coefFuncObjDec.entrySet().forEach((entry) -> {
                System.out.println(entry.getKey() + " = " + entry.getValue());
                });
            }
            
            System.out.println("::::::::::::::::::::RESTRICCIONES::::::::::::::::::::");
            restricciones.entrySet().forEach((entry) -> {
                System.out.println(entry.getKey() + " ==> " + entry.getValue());
            });
            
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
    public static HashMap<String, ?> getCoefFuncObj(String s) {
        String aux[];
        aux = s.split("[abcd+]+");
        if(s.contains(".")) {
            System.out.println("DECIMALES!!!!");
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
            HashMap<String,Integer> coeficientesInt = new HashMap<>();
            coeficientesInt.put("a", Integer.parseInt(aux[0]));
            coeficientesInt.put("b", Integer.parseInt(aux[1]));
            coeficientesInt.put("c", Integer.parseInt(aux[2]));
            coeficientesInt.put("d", Integer.parseInt(aux[3]));
            
            /*coeficientesInt.entrySet().forEach((entry) -> {
            System.out.println(entry.getKey() + "=" + entry.getValue());
            });*/
            return coeficientesInt;
        }
    }
    
}
