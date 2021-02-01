/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package automatSimulator;


/**
 *
 * @author Peter Mikušík
 */
public class AutomatMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        AutomatVlastnosti vlastnosti = new AutomatVlastnosti();       
        Automat automat = new Automat(vlastnosti);
        automat.setLocationRelativeTo(null);
        automat.setVisible(true);
    }
    
}
