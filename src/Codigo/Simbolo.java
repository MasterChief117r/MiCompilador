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
public class Simbolo {
    private String lexema, token, categoria, tipo, valor, alcance, seUtiliza, linea, posicion;

    public Simbolo(String lexema, String token, String categoria, String tipo, String valor, String alcance, String seUtiliza, String linea, String posicion) {
        this.lexema = lexema;
        this.token = token;
        this.categoria = categoria;
        this.tipo = tipo;
        this.valor = valor;
        this.alcance = alcance;
        this.seUtiliza = seUtiliza;
        this.linea = linea;
        this.posicion = posicion;
    }

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
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

    public String getAlcance() {
        return alcance;
    }

    public void setAlcance(String alcance) {
        this.alcance = alcance;
    }

    public String getSeUtiliza() {
        return seUtiliza;
    }

    public void setIsUsed(String seUtiliza) {
        this.seUtiliza = seUtiliza;
    }

    public String getLinea() {
        return linea;
    }

    public void setLine(String linea) {
        this.linea = linea;
    }

    public String getPosicion() {
        return posicion;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }
}
