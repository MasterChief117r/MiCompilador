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
public class Analizar {
    private String lexema, token, linea, ColIni, ColFinal, error;

    public Analizar(String lexema, String token, String linea, String ColIni, String ColFinal, String error) {
        this.lexema = lexema;
        this.token = token;
        this.linea = linea;
        this.ColIni = ColIni;
        this.ColFinal = ColFinal;
        this.error = error;
    }
    
    public Analizar() {}

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

    public String getLinea() {
        return linea;
    }

    public void setLinea(String linea) {
        this.linea = linea;
    }

    public String getColIni() {
        return ColIni;
    }

    public void setColIni(String ColIni) {
        this.ColIni = ColIni;
    }

    public String getColFinal() {
        return ColFinal;
    }

    public void setColFinal(String ColFinal) {
        this.ColFinal = ColFinal;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
