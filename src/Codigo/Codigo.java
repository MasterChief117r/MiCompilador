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
public class Codigo {
    public int Ifposicion;
    public String lexema;
    public String Codigo;
    public int Posicion;

    public Codigo() {}

    public Codigo(int Ifposicion, String lexema, String Codigo, int Posicion) {
        this.Ifposicion = Ifposicion;
        this.lexema = lexema;
        this.Codigo = Codigo;
        this.Posicion = Posicion;
    }

    public int getIfposicion() {
        return Ifposicion;
    }

    public void setIfposicion(int Ifposition) {
        this.Ifposicion = Ifposition;
    }

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public String getCodigo() {
        return Codigo;
    }

    public void setCodigo(String Codigo) {
        this.Codigo = Codigo;
    }

    public int getPosicion() {
        return Posicion;
    }

    public void setPosicion(int Posicion) {
        this.Posicion = Posicion;
    }
}
