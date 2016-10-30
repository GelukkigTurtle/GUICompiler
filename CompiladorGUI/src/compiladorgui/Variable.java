/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compiladorgui;

/**
 *
 * @author Freddy
 */
public class Variable {
    
    String nombre;
    String tipo;
    String valor;

    public Variable() {
    }

    public Variable(String nombre, String tipo, String valor) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.valor = valor;
    }
   

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
    
    
    
}
