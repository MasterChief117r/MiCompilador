/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Codigo;

/**
 *
 * @author MaterChief117
 */
public class Pila {
    public Object[] stack;
    public int posicion;
    
    public Pila() {
        this.posicion = -1;
        this.stack = new Object[1000];
    }
    
    public boolean isEmpty() {
        if ( this.posicion == - 1)
            return true;
        
        return false;
    }
    
    public int size() {
        if ( this.isEmpty() )
            return 0;
        
        return this.posicion + 1;
    }
    
    public Object getTop() {
        if ( this.isEmpty() )
            return null;
        
        return this.stack[this.posicion];
    }
    
    public Object pop() {
        if ( isEmpty() )
            return null;
        
        return this.stack[this.posicion--];
    }
    
    public void push(Object value) {
        if ( this.posicion < this.stack.length - 1)
            this.stack[++posicion] = value; 
    }
}
