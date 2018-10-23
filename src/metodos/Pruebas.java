/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metodos;

/**
 *
 * @author Jorge Castillo
 */
public class Pruebas {
    public static void main(String args[]) {
        double m[][] = {{1,2,3},{4,5,6},{7,8,9}};
        double r[] = new double[3]; 
        
        for(int j=0; j<m.length; j++) {
            for(int i=0; i<m[j].length; i++) {
                r[j] += m[i][j];
            }
            System.out.println("");
        }
        System.out.println(r[0]);
        System.out.println(r[1]);
        System.out.println(r[2]);
        
        double val, inv;
        val = 0.55546;
        inv = 1.00/val;
        System.out.println((double)(val*inv));
        
        /*
        
        1 2 3   10
        4 5 6   20
        7 8 9   30
        
        */
        /*
        pivote = 5
        double filaUnitaria[] = new double[4 + restric];
        for(i=0; i<filaUnitaria.length; i++) {
            filaUnitaria[i] = 
        }
        */
        
    }
    
}
