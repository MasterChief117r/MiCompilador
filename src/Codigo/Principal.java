/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Codigo;

import java.io.File;

/**
 *
 * @author MaterChief117
 */
public class Principal {
    public static void main(String[] args)
    {
        String ruta = "D:/OTROS/ETC/MIUMG/2024/SEMESTRE 7/COMPILADORES/UNIDAD 3/PROYECTOCOMPILADOR/MiCompilador/src/Codigo/Lexer.flex";
        generarLexer(ruta);
    }
    
    public static void generarLexer (String ruta){
        File archivo = new File (ruta);
        JFlex.Main.generate(archivo);
    }
}
