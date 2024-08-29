 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import Codigo.Analizar;
import Codigo.Codigo;
import Codigo.LexicalAnalyzer;
import Codigo.Error;
import Codigo.Simbolo;

/**
 *
 * @author MaterChief117
 */
public class Control {
    int MAX = 15;
    ArrayList<Analizar> tokens;
    ArrayList<Error> list;
    int count;
    ArrayList<Simbolo> globalList; 
    ArrayList<Simbolo> localList;
    
    public void Control() {}
    
    // Do the analyse lexic
    public ArrayList<Analizar> analyseLexic(String textEdit)
    {
        int i = 1;
        boolean ignore = false;
        ArrayList<Analizar> list = new ArrayList<>();
        Scanner scanner = new Scanner(textEdit);
        Analizar token = new Analizar();
        
        while ( scanner.hasNextLine() )
        {
            String line = (scanner.nextLine()).toString();
            LexicalAnalyzer lexic = new LexicalAnalyzer(new StringReader(line));
            
            try {
                while ( (token = lexic.yylex()) != null )
                {
                    token.setLinea(String.valueOf(i));
                    
                    if ( token.getToken().equals("Identificador") || token.getToken().equals("Entero") || token.getToken().equals("Real") )
                    {
                        int length = Integer.parseInt(token.getColFinal()) - Integer.parseInt(token.getColIni());
                        
                        if ( length > MAX )
                            token.setError("Error ! Excede el tamaño maximo (15) !\n");
                    }
                    
                    if ( token.getLexema().equals("{") )
                        ignore = true;
                    else if ( token.getLexema().equals("}") )
                    {
                        ignore = false;
                        continue;
                    }
                    
                    if (!ignore)
                        list.add(token);
                }
                
            i++;
                    
            } catch (IOException ex) {
                Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
        
        if ( ignore )
            JOptionPane.showMessageDialog(null, "Error ! Te falta cerrar el comentario !");
       
        return list;
    }
    
    // Do the Analyse Sintatic
    public ArrayList<Error> analyseSintatic(String textEdit) {
        // Do the Lexic Analyse
        tokens = analyseLexic(textEdit);
        list = new ArrayList<>();
        count = 0;
        
        if ( tokens.isEmpty() ) {
            list.add(new Error( "0", "Error ! No hay tokens para el análisis sintáctico !") );
            return list;
        }
        
        // Control a sintatic analyse
        while( count < tokens.size() ) {
            if ( tokens.get(count).getLinea().equals("1") ) 
                if ( accept("Palabra_Reservada_Program") ) {
                    if ( expect("Identificador") ) {
                        if ( !expect("Punto_y_Coma") ) 
                            list.add(new Error( tokens.get(count).getLinea(), "Error ! El siguiente token debe ser ';'") );
                
                    } else list.add(new Error( tokens.get(count).getLinea(), "Error ! El siguiente token debe ser un Identificador (int, boolean)'") );
                
                } else list.add(new Error( tokens.get(count).getLinea(), "Error ! El programa debe inicializarse con la palabra reservada 'program' ") );
            
            if ( !accept("Palabra_Reservada_Begin") )
                nextToken();
            
            if ( accept("Palabra_Reservada_End") || count == tokens.size() - 1 ) {
                count++;
                continue;
            }
            
            block();
        }

        if ( list.size() == 0 )
            list.add(new Error( "-1", "Exito ! El análisis sintactico fue exitoso !" ) );
        
        return list;
    }
    
    // ######################### --- Help Methods to do sintatic analyse --- ###################################

    // Compare specific Token
    public boolean accept(String token) {
        if ( tokens.get(count).getToken().equalsIgnoreCase(token) )
            return true;   
        
        return false;
    }
    
    // Compare the next token
    public boolean expect(String token) {
        nextToken();
        
        if ( accept(token) )
            return true;
        
        return false;
    }
    
    // Compare if the previous token is accept
    public boolean acceptPreviousToken(String token) {
        --count;
        
        if ( accept(token) ) {
            ++count;
            return true;
        }
        
        ++count;
        return false;
    }
    
    // Increment to the next token
    public void nextToken() {
        int MAX = tokens.size() - 1;
        
        if ( count < MAX)
            ++count;
    }
    
    // Decrement previous token
    public int previousToken() {
        if ( count > 0 )
            --count;
        
        return count;
    }
    
    public void block() {
        if ( accept("Palabra_Reservada_Int") || accept("Palabra_Reservada_Boolean") )
            partVarDeclaration();
        
        if ( accept("Palabra_Reservada_Procedure") )
            procedurePart();
        
        if ( accept("Palabra_Reservada_Begin") ) {
            compCondition();
        }
            
        else {
            if ( !accept("Punto_y_Coma") ) {
                list.add(new Error( tokens.get(count).getLinea(), "Error ! Esperando la palabra reservada 'begin'/'procedure'/'int'/'boolean' !") );
                list.add(new Error( tokens.get(count).getLinea(), "Manejo de errores realizado ! ignorar tokens hasta encontrarlos ';' !") );

                while( !accept("PUNTO_Y_COMA") ) {
                    nextToken();
                    
                    if ( count == tokens.size() - 1 )
                        break;
                }
            }
        }
    }
    
    public void partVarDeclaration() {
        varDeclaration();
        
        while ( accept("Punto_y_Coma") ) {
            nextToken();
            varDeclaration();
        }
        
        previousToken();
        
        if ( !accept("Punto_y_Coma") ) {
            list.add(new Error( tokens.get(count).getLinea(), "Error ! Sómbolo esperado ';' !") ); 
            nextToken();
        }
        
        nextToken();
    }
    
    public void varDeclaration() {
        if ( accept("Palabra_Reservada_Int") || accept("Palabra_Reservada_Boolean") ) {
            nextToken();
            identList();
        }   
    }
    
    public void identList() {
        if ( accept("Identificador") ) {
            nextToken();
            
            while( accept("Punto") ) {
                nextToken();
                identList();
            }
        } else {
            list.add(new Error( tokens.get(count).getLinea(), "Error ! Se esperaba un 'Identificador' !") );
            list.add(new Error( tokens.get(count).getLinea(), "Manejo de errores realizado! ignorar tokens hasta encontrarlos ';' | ':' !") );
            
            while ( !accept("Punto_y_Coma") || !accept("DOS_PUNTOS") ) {
                nextToken();
             
                if ( count == tokens.size() - 1 )
                      break;
            }
        }   
    }
    
    public void procedurePart() {
        while( accept("Palabra_Reservada_Procedure") ) {
            procedureDeclaration();
            
            if ( !accept("Punto_y_Coma") && !accept("Palabra_Reservada_End") && !accept("Palabra_Reservada_Begin") ) {
                list.add(new Error( tokens.get(count).getLinea(), "Erro ! Símbolo esperado ';' !") );
                nextToken();
            }
        }
    }
    
    public void procedureDeclaration() {
        if ( accept("Palabra_Reservada_Procedure") ) {
            if ( expect("Identificador") ) {
                if ( expect("Parentesis_Apertura") ) {
                    formalParam();
                    
                    if ( expect("Punto_y_Coma") ) {
                        nextToken();
                        block();
                    } else {
                        list.add(new Error( tokens.get(count).getLinea(), "Error ! Símbolo esperado ';' !") );
                        nextToken();
                    } 
                }
                
                if ( expect("Punto_y_Coma") ) {
                    nextToken();
                    block();
                } else {
                    list.add(new Error( tokens.get(count).getLinea(), "Error ! Simbolo esperado ';' !") );
                    nextToken();
                } 
            }
        }
    }
    
    public void formalParam() {
        if ( accept("Parentesis_Apertura") ) {
            nextToken();
            formalSectionParam();
            
            while( expect("Punto_y_Coma") ) {
                nextToken();
                formalSectionParam();
            }
            
            if ( !accept("Parentesis_Cierre") ) {
                list.add(new Error( tokens.get(count).getLinea(), "Error ! Símbolo esperado ')' !") ); 
                list.add(new Error( tokens.get(count).getLinea(), "Manejo de errores realizado! ignorar tokens hasta encontrarlos ';'!") );
                
                while ( !accept("Punto_y_Coma") ) {
                    nextToken();
                    
                    if ( count == tokens.size() - 1 )
                        break;
                }
            } 
        }
    }
    
    public void formalSectionParam() {
        if ( accept("Palabra_Reservada_Var") ) {
            nextToken();
            identList();
            
            if ( accept("Operador_Dos_Puntos")) {
                nextToken();
                
                if ( !accept("Palabra_Reservada_Int") && !accept("Palabra_Reservada_Boolean")  ) {
                    list.add(new Error( tokens.get(count).getLinea(), "Error ! Se esperaba 'Identificador' !") );
                    list.add(new Error( tokens.get(count).getLinea(), "Manejo de errores realizado! ignorar tokens hasta encontrarlos ';'!") );
                
                    while ( !accept("Punto_y_coma") ) {
                        nextToken();
                        
                        if ( count == tokens.size() - 1 )
                            break;
                    }
                }
                
            } else {
                list.add(new Error( tokens.get(count).getLinea(), "Error ! Símbolo esperado ':' !") );
                list.add(new Error( tokens.get(count).getLinea(), "Manejo de errores realizado! ignorar tokens hasta encontrarlos ';' !") );
                
                while ( !accept("Punto_y_Coma") ) {
                    nextToken();
                    
                    if ( count == tokens.size() - 1 )
                        break;
                }
            }                    
            
        } else {
            identList();
            
            if ( accept("Operador_Dos_Puntos")) {
                nextToken();
                
                if ( !accept("Identificador") ) {
                    list.add(new Error( tokens.get(count).getLinea(), "Error ! Se esperaba 'Identificador' !") );
                    list.add(new Error( tokens.get(count).getLinea(), "Manejo de erroes realizado! ignorar tokens hasta encontrarlos ';'!") );
                
                    while ( !accept("Punto_y_Coma") ) {
                        nextToken();
                        
                        if ( count == tokens.size() - 1 )
                            break;
                    }
                }
            
            } else {
                list.add(new Error( tokens.get(count).getLinea(), "Error ! Esperado o símbolo ':' !") );
                list.add(new Error( tokens.get(count).getLinea(), "Manejo de erroes realizado! ignorar tokens hasta encontrarlos ';'!") );
                
                    while ( !accept("Punto_y_Coma") ) {
                        nextToken();
                        
                        if ( count == tokens.size() - 1 )
                            break;
                    }
            }
        }
    }
    
    public void compCondition() {
        if ( accept("Palabra_Reservada_Begin") ) {
            nextToken();
            condition();
            
            while ( accept("Punto_y_Coma") ) {
                nextToken();
                condition();
                
                if ( !accept("Palabra_Reservada_End") && !accept("Punto_y_Coma") && !accept("Palabra_Reservada_Begin")) {
                    list.add(new Error( tokens.get(count).getLinea(), "Error ! Palabra reservada esperada 'end' !") );
                    list.add(new Error( tokens.get(count).getLinea(), "Manejo de erroes realizado! ignorar tokens hasta encontrarlos ';'!") );
                
                    while ( !accept("Punto_y_Coma") ) {
                        nextToken();
                        
                        if ( count == tokens.size() - 1 )
                            break;
                    }
                }
            }
        } else {
            list.add(new Error( tokens.get(count).getLinea(), "Errorr ! Palabra reservada esperada 'begin' !") );
            list.add(new Error( tokens.get(count).getLinea(), "Manejo de erroes realizado! ignorar tokens hasta encontrarlos ';'!") );
                
            while ( !accept("Punto_y_Coma") ) {
                nextToken();

                if ( count == tokens.size() - 1 )
                    break;
            }
        }
    }
    
    // Missing  Chamada de Procedimento ???????
    public void condition() {
        if ( accept("Identificador") || accept("Palabra_Reservada_Write") || accept("Palabra_Reservada_Read") ) {
           attribution();
        
        } else if ( accept("Palabra_Reservada_Begin") ) {
           compCondition();
        
        } else if ( accept("Palabra_Reservada_If") ) {
            conditionIf();
        
        } else if ( accept("Palabra_Reservada_While") ) {
            conditionLoop();
        
        } else {
            list.add(new Error( tokens.get(count).getLinea(), "Error ! Se esperaba algun 'comando' !") );
            list.add(new Error( tokens.get(count).getLinea(), "Manejo de erroes realizado! ignorar tokens hasta encontrarlos ';' | 'end' | 'else'!") );
                
            while ( !accept("Punto_y_Coma") || !accept("Palabba_Reservada_End") || !accept("Palabra_Reservada_Else") ) {
                nextToken();
                
                if ( count == tokens.size() - 1 )
                    break;
            }
        }
    }
    
    public void attribution() {
        variable();
        
        if ( accept("Operador_Igual") ) {
            nextToken();
            expression();
        
        } else if ( accept("Parentesis_Apertura") && (acceptPreviousToken("Palabra_Reservada_Write") || acceptPreviousToken("Palabra_Reservada_Read")) ) {
            nextToken();
            expression();
        }
        
        else {
            list.add(new Error( tokens.get(count).getLinea(), "Error ! Operador esperado ':=' !") );
            list.add(new Error( tokens.get(count).getLinea(), "Manejo de erroes realizado! ignorar tokens hasta encontrarlos ';' | 'end' | 'else'!") );
                
            while ( !accept("Punto_y_Coma") || !accept("Palabra_Reservada_End") || !accept("Palabra_Reservada_Else") ) {
                nextToken();
                
                if ( count == tokens.size() - 1 )
                    break;
            }
        }
    }
    
    public void procedureCall() {
        if ( accept("Identificador") ) {
            if ( expect("Parentesis_Apertura") ) {
                nextToken();
                listExpression();
                
                if ( !expect("Parentesis_Cierre") ) {
                    list.add(new Error( tokens.get(count).getLinea(), "Error ! Se esperaba ')' !") );
                    list.add(new Error( tokens.get(count).getLinea(), "Manejo de erroes realizado! ignorar tokens hasta encontrarlos ';' | 'end' | 'else'!") );
                
                    while ( !accept("Punto_y_Coma") || !accept("Palabra_Reservada_End") || !accept("Palabra_Reservada_Else") ) {
                        nextToken();
                        
                        if ( count == tokens.size() - 1 )
                            break;
                    }
                }
                
            } else{
                list.add(new Error( tokens.get(count).getLinea(), "Error ! Se esperaba '(' !") );
                list.add(new Error( tokens.get(count).getLinea(), "Manejo de erroes realizado! ignorar tokens hasta encontrarlos ';' | 'end' | 'else'!") );
                
                while ( !accept("Punto_y_Coma") || !accept("Palabra_Reservada_End") || !accept("Palabra_Reservada_Else") ) {
                    nextToken();
                    
                    if ( count == tokens.size() - 1 )
                        break;
                }
            }
            
        } else {
            list.add(new Error( tokens.get(count).getLinea(), "Error ! Se esperaba un 'Identificador' !") );
            list.add(new Error( tokens.get(count).getLinea(), "Manejo de erroes realizado! ignorar tokens hasta encontrarlos ';' | 'end' | 'else'!") );
                
            while ( !accept("Punto_y_Coma") || !accept("Palabra_Reservada_End") || !accept("Palabra_Reservada_Else") ) {
                nextToken();
                
                if ( count == tokens.size() - 1 )
                    break;
            }
        }
    }
    
    // Parse the conditionIF
    public void conditionIf() {
        if ( accept("Palabra_Reservada_If") ) {
            nextToken();
            expression();
            
            if ( expect("Palabra_Reservada_Then") ) {
                nextToken();
                condition();
                
                if ( accept("Palabra_Reservada_Else") ) {
                    nextToken();
                    condition();
                } 
                
            } else if ( acceptPreviousToken("Palabra_Reservada_Then") ) {
                condition();
                
                if ( accept("Palabra_Reservada_Else") ) {
                    nextToken();
                    condition();
                } 
            } else {
                list.add(new Error( tokens.get(count).getLinea(), "Error ! Palabra reservada esperada 'then' !") );
                list.add(new Error( tokens.get(count).getLinea(), "Manejo de erroes realizado! ignorar tokens hasta encontrarlos ';' | 'end' | 'else'!") );
                
                while ( !accept("Punto_y_Coma") || !accept("Palabra_Reservada_End") || !accept("Palabra_Reservada_Else") ) {
                    nextToken();
                    
                    if ( count == tokens.size() - 1 )
                        break;
                }
            }
            
        } else {
            list.add(new Error( tokens.get(count).getLinea(), "Error ! Palabra reservada esperada 'if' !") );
            list.add(new Error( tokens.get(count).getLinea(), "Manejo de erroes realizado! ignorar tokens hasta encontrarlos ';' | 'end' | 'else'!") );
                
            while ( !accept("Punto_y_Coma") || !accept("Palabra_Reservada_End") || !accept("Palabra_Reservada_Else") ) {
                nextToken();
                
                if ( count == tokens.size() - 1 )
                    break;
            }
        }
    }
    
    // Parse the condition
    public void conditionLoop() {
        if ( accept("Palabra_Reservada_While") ) {
            nextToken();
            expression();
            
            if ( accept("Palabra_Reservada_Do") ) {
                nextToken();
                condition();
            } //else list.add( new SintaticError( tokens.get(count).getLine(), "Erro ! Esperado palavra reservada 'do' !") );
            
            if ( accept("Parentesis_Cierre") )
                nextToken();
            
        } else {
            list.add(new Error( tokens.get(count).getLinea(), "Error ! Palabra reservada esperada 'while' !") );
            list.add(new Error( tokens.get(count).getLinea(), "Manejo de erroes realizado! ignorar tokens hasta encontrarlos ';' | 'end' | 'else'!") );
                
            while ( !accept("Punto_y_Coma") || !accept("Palabra_Reservada_End") || !accept("Palabra_Reservada_Else") ) {
                nextToken();
                
                if ( count == tokens.size() - 1 )
                    break;
            }
        }
    }
    
    // Parse the expression
    public void expression() {
        simpleExpression();
        
        if ( accept("Operador_Menor") || accept("Operador_Igual") || accept("Operador_Mayor") || accept("Operador_Menor_Igual") ||
            accept("Operador_Mayor_Igual") || accept("Operador_Diferente") ) {
            nextToken();
            
            simpleExpression();
        }
    }
    
    // Parse the Simple Expression
    public void simpleExpression() {
        if ( accept("Operador_Suma") || accept("Operador_Resta") ) {
            nextToken();
        }
        
        term();
        
        while( accept("Operador_Suma") || accept("Operador_Resta") || accept("Palabra_Reservada_Or") || accept("Operador_Multiplicacion")) {
            nextToken();
            term();
        }
    }
    
    // Parse the term
    public void term() {
        factor();
        
        while ( accept("Operador_Multiplicacion") || accept("Palabra_Reservada_Div") || accept("Palabra_Reservada_And") || accept("Operador_Division") ) {
            nextToken();
            factor();
            
            if ( accept("Palabra_Reservada_End") )
                nextToken();
        }
    }
    
    // Parse the factor
    public void factor() {
        if ( accept("Identificador") || accept("Palabra_Reservada_True") || accept("Palabra_Reservada_False") || accept("Palabra_Reservada_Write") || accept("Palabra_Reservada_Read") ) {
            nextToken();
            variable();
        } else if ( accept("Entero") || accept("Real") ) {
            nextToken();
        } else if ( accept("Parentesis_Apertura") ) {
            nextToken();
            expression();
            
            if ( !accept("Parentesis_Cierre") && !accept("Palabra_Reservada_Begin") && !accept("Palabra_Reservada_Then") ) {
                list.add(new Error( tokens.get(count).getLinea(), "Error ! Se esperaba ')' !") );
                list.add(new Error( tokens.get(count).getLinea(), "Manejo de erroes realizado! ignorar tokens hasta encontrarlos ';'!") );
                
                while ( !accept("Punto_y_Coma") || !accept("Palabra_Reservada_End") || !accept("Palabra_Reservada_Else") ) {
                    nextToken();
                    
                    if ( count == tokens.size() - 1 )
                        break;
                }
            }
            
        } else if ( accept("Palabra_Reservada_Not") ) {
            nextToken();
            factor();
            
        } else {
            list.add(new Error( tokens.get(count).getLinea(), "Error ! Factor esperado !") );
            list.add(new Error( tokens.get(count).getLinea(), "Manejo de erroes realizado! ignorar tokens hasta encontrarlos ';'!") );
                
            while ( !accept("Punto_y_Coma") || !accept("Palabra_Reservada_End") || !accept("Palabra_Reservada_Else") ) {
                nextToken();
                
                if ( count == tokens.size() - 1 )
                    break;
            }
        } 
    }
    
    // Parse the variable
    public void variable() {
        if ( accept("Identificador") || accept("Palabra_Reservada_True") || accept("Palabra_Reservada_False") || accept("Palabra_Reservada_Write") || accept("Palabra_Reservada_Read") ) {
            
            nextToken();
            if ( accept("Operador_Suma") || accept("Operador_Resta") )
                expression();
            
           
        } else if ( accept("Parentesis_Cierre") )
            nextToken();
        
    }
    
    // Parse list of expression
    public void listExpression() {
        expression();
        
        while ( expect("Coma") ) {
            nextToken();
            expression();
        }
    }
    
    // ######################### --- Semantic Methods --- ###################################
    public ArrayList<Error> analyseSemantic(String textEdit) {
        ArrayList<Error> errorList = createSemanticTable(textEdit);
        ArrayList<Error> globalError;
        ArrayList<Error> localError;
        
        // Verify if a sintatic is ok, if not, return sintatic errors and stop semantic analyse
        if ( errorList.size() > 0 )
            if ( errorList.get( errorList.size()-1 ).getLinea().equals("999") )
                return errorList;
        
        globalError = findSemanticErrors(0);
        localError = findSemanticErrors(1);

        // Append the errorList in only one list
        if ( !globalError.isEmpty() )
            errorList.addAll(globalError);

        if ( !localError.isEmpty() )
            errorList.addAll(localError);

        if ( errorList.isEmpty() ) {
            errorList.add(new Error("-1", "Sin errores, análisis semántico correcto !"));
            return errorList;
        } else {
            errorList.sort(new Sortybyroll());
            return errorList;
        }
    }
    
    public ArrayList<Error> findSemanticErrors(int level) {
        ArrayList<String> procedimento = new ArrayList<>();
        ArrayList<String> procedimento2 = new ArrayList<>();    
        ArrayList<Error> errorList = new ArrayList<>();
        HashMap <Integer, String> type = new HashMap<>();
        int countRead = 0;
        int countWrite = 0;
        String lexeme, oldLine;
        Simbolo symbol;
        int position = 0;
        int begin, end;
        
        if ( level == 0 ) {
            begin = Integer.parseInt(globalList.get(0).getPosicion());
            end = Integer.parseInt(globalList.get( globalList.size() - 1).getPosicion());
        }
        else {
            begin = Integer.parseInt(localList.get(0).getPosicion());
            end = Integer.parseInt(localList.get( localList.size() - 1).getPosicion());
        }
        
        for( int i = begin; i < end; i++ )
        {
            lexeme = tokens.get(i).getLexema();
            symbol = searchSymbol(tokens.get(i).getLexema(), level);
            
            if ( tokens.get(i).getToken().equals("Identificador") ) { 
                if ( !isDeclared(symbol) && !tokens.get(i-1).getLexema().equals("program") && !tokens.get(i-1).getLexema().equals("procedure") && !tokens.get(i-1).getLexema().equals("var") ) 
                    errorList.add(new Error(tokens.get(i).getLinea(), "Error ! La variable nunca se declara: " + symbol.getLexema() + " ! ") );
                else if ( (level == 0) && searchSymbol(lexeme, 1) != null && !isDeclared(symbol) )
                    errorList.add(new Error(tokens.get(i).getLinea(), "Error ! Alcance inadecuado: " + symbol.getLexema() + " ! ") );
                
                if ( tokens.get(i+1).getToken().equals("Operador_Igual") ) {
                    if ( symbol.getTipo().equals("Entero") ) {
                        if ( tokens.get(i+2).getToken().equals("Palabra_Reservada_True") || tokens.get(i+2).getToken().equals("Palabra_Reservada_False") || tokens.get(i+2).getToken().equals("Real") )
                            errorList.add(new Error(tokens.get(i).getLinea(), "Error ! Asignar un valor de otro tipo a una variable !"));
                        
                        if ( tokens.get(i+3).getToken().equals("Operador_Division") ){
                            oldLine = tokens.get(i).getLinea(); 
                            position = i+2;

                            while( position < tokens.size() && tokens.get(position).getLinea().equals(oldLine) ) {
                                if ( tokens.get(position).getToken().equals("Real") )
                                   errorList.add(new Error(tokens.get(position).getLinea(), "Error ! Asignar un valor de otro tipo a una variable !"));

                                if ( (tokens.get(position).getToken().equals("Operador_Division") || tokens.get(position).getToken().equals("Palabra_Reservada_Div"))  && tokens.get(position+1).getLexema().equals("0") )
                                    errorList.add(new Error(tokens.get(position).getLinea(), "Error ! No es posible dividir por cero !"));

                                position++;
                            }
                        }
                    } else if ( symbol.getTipo().equals("Booleano") ) {
                        if ( tokens.get(i+2).getToken().equals("Entero") || tokens.get(i+2).getToken().equals("Real") )
                            errorList.add(new Error(tokens.get(i).getLinea(), "Error ! Asignar un valor de otro tipo a una variable !"));
                    }
                }
            } else if ( tokens.get(i).getToken().equals("Palabra_Reservada_Read") ) {
                if ( searchSymbol(tokens.get(i+2).getLexema(), level).getTipo().equals("Entero") )
                    type.put(countRead, "Entero");
                else
                    type.put(countRead,"Booleano");
                   
                countRead++;
                
            } else if ( tokens.get(i).getToken().equals("Palabra_Reservada_Write") ) {
                if ( searchSymbol(tokens.get(i+2).getLexema(), level).getTipo().equals("Entero") ) {
                    if (type.get(countWrite).equals("Booleano") )
                        errorList.add(new Error(tokens.get(i).getLinea(), "Error ! Lectura y escritura con diferentes tipos !"));
                }
                    
                else if ( type.get(countWrite).equals("Entero") )
                    errorList.add(new Error(tokens.get(i).getLinea(), "Error ! Lectura y escritura con diferentes tipos !"));
                
                countWrite++;
            
            } else if ( tokens.get(i).getToken().equals("Palabra_Reservada_Procedure") ) {
                oldLine = tokens.get(i).getLinea(); 
                position = i;
                position++;
                
                while( position < tokens.size() && tokens.get(position).getLinea().equals(oldLine) )
                {
                    if ( tokens.get(position).getToken().equals("Identificador") 
                            || tokens.get(position).getToken().equals("Palabra_Reservada_Int")
                            || tokens.get(position).getToken().equals("Palabra_Reservada_Boolean"))
                        procedimento.add( tokens.get(position).getLexema() );
                    
                    position++;
                }
                
            } else if ( procedimento.size() > 0 && tokens.get(i).getLexema().equals(procedimento.get(0)) && !tokens.get(i-1).getToken().equals("Palabra_Reservada_Procedure") ) {
                    oldLine = tokens.get(i).getLinea();
                    position = i;
                    position++;
                    
                    while( position < tokens.size() && tokens.get(position).getLinea().equals(oldLine) )
                    {
                        if ( tokens.get(position).getToken().equals("Identificador") 
                                || tokens.get(position).getToken().equals("Palabra_Reservada_Int")
                                || tokens.get(position).getToken().equals("Palabra_Reservada_Boolean"))
                            procedimento2.add( tokens.get(position).getLexema() );
                        
                        position++;
                    }

                    int j = 0;
                    
                    if ( procedimento.size() != procedimento2.size() )
                        errorList.add(new Error(tokens.get(i).getLinea(), "Error ! El número de elementos en los parámetros es incorrecto !"));
                    else
                        for ( String s: procedimento ) {
                            if ( !s.equals(procedimento2.get(j)) )
                                errorList.add(new Error(tokens.get(i).getLinea(), "Error ! Los elementos de los parámetros son diferentes !"));

                            j++;
                        }
                }
        }
        
        errorList.addAll( isNeverUsedError(level) );
        
        return errorList;
    }
    
    public ArrayList<Error> createSemanticTable(String textEdit) {
        int level;
        ArrayList<Error> errorList = new ArrayList<>();
        globalList = new ArrayList<>();
        localList = new ArrayList<>();
        
        String category, type, value, scope, isUsed, line;
        Simbolo symbol = null;
        
        // Verify if sintatic analyse is ok, if yes, clean the errorList and do the semantic analyse
        if ( (errorList = analyseSintatic(textEdit)).size() == 1 && errorList.get(0).getLinea().equals("-1") ) {
            errorList = new ArrayList<>();
            level = count = 0;
            
            while ( count < tokens.size() ) {
                category = type = value = scope = isUsed = "";
                
                if ( accept("Palabra_Reservada_Procedure") )
                    level++;
                
                if ( (symbol = searchSymbol(tokens.get(count).getLexema(), level)) == null ) {
                    category = setCategory(tokens.get(count).getToken());
                    scope = setScope( tokens.get(count).getLexema(), level);
                    line = tokens.get(count).getLinea();
                   
                    if ( category.equals("Variable") ) {
                        type = setType(count, tokens.get(count).getLinea());
                         isUsed = "N";
                    }
                  
                    if ( level == 0 ) 
                        globalList.add(new Simbolo(tokens.get(count).getLexema(), tokens.get(count).getToken(), category, type, value, scope, isUsed, line, String.valueOf(count)) );
                
                    else
                        localList.add(new Simbolo(tokens.get(count).getLexema(), tokens.get(count).getToken(), category, type, value, scope, isUsed, line, String.valueOf(count)) );
                }
                
                else {
                    if ( tokens.get(count).getToken().equals("Identificador") ) {
                        if ( tokens.get(count+1).getToken().equals("Operador_Igual") )
                            if ( tokens.get(count+2).getToken().equals("Entero") )
                                symbol.setValor(tokens.get(count+2).getLexema() + ":Entero" );
                            else if ( tokens.get(count+2).getToken().equals("Real") )
                                symbol.setValor(tokens.get(count+2).getLexema() + ":Real" );
                            else if ( tokens.get(count+2).getToken().equals("Palabra_Reservada_True") || tokens.get(count+2).getToken().equals("Palabra_Reservada_False") )
                                symbol.setValor(tokens.get(count+2).getLexema() + ":Booleano" );
                            else
                                symbol.setValor(tokens.get(count+2).getLexema() + ":Expresión" );
                        
                        symbol.setIsUsed("S");
  
                        if ( isDeclared(symbol) ) {
                           int position = count;
                           String oldLine = tokens.get(position).getLinea();

                           while ( position > 0 && tokens.get(position).getLinea().equals(oldLine) ) {
                               if ( tokens.get(position).getToken().equals("Palabra_Reservada_Int") || tokens.get(position).getToken().equals("Palabra_Reservada_Boolean") ) {
                                   errorList.add(new Error(symbol.getLinea(), "Error ! Variable ya declarada !"));
                                   break;
                               }

                               position--;
                           }
                        }
                    }
                }

                count++;
            }
        } 
        
        // Return the errorList with sintatic errors
        else {
            errorList.add(new Error("999", "Error ! Error sintáctico !!") );
            return errorList;
        }
        
        return errorList;
    }
    
    public ArrayList<Error> isNeverUsedError(int level) {
        ArrayList<Error> errorList = new ArrayList<>();
        ArrayList<Simbolo> temp;
        int i = 0;
        
        if ( level == 0 )
            temp = globalList;
        else
            temp = localList;
        
        for ( Simbolo s: temp ) {
            if ( (level == 0 && searchSymbol(s.getLexema(), 1) == null) && s.getSeUtiliza().equals("N") && !temp.get(i-1).getLexema().equals("program") && !temp.get(i-1).getLexema().equals("procedure") && !temp.get(i-1).getLexema().equals("var") )
                errorList.add(new Error(s.getLinea(), "Error ! Variable nunca utilizada: " + s.getLexema() + " ! ") );
        
            i++;
        }
        
        return errorList;
    }
    
    // Verify if the variable is declared   
    public boolean isDeclared(Simbolo symbol) {
        int position = Integer.parseInt(symbol.getPosicion());
        String oldLine = tokens.get(position).getLinea();
        
        while ( position > 0 && tokens.get(position).getLinea().equals(oldLine) ) {
            if ( tokens.get(position).getToken().equals("Palabra_Reservada_Int") || tokens.get(position).getToken().equals("Palabra_Reservada_Boolean") )
                return true;

            position--;
        }
        
        return false;
    }
    
    // Search for a existent symbol in the table
    public Simbolo searchSymbol(String lexeme, int level) {
        ArrayList<Simbolo> temp = new ArrayList<>();
        
        if ( level == 0 )
            temp = globalList;
        else
            temp = localList;
       
        if ( temp != null )
            for( Simbolo s: temp )
                if ( s.getLexema().equals(lexeme) )
                    return s;
        
        return null;
    }
    
    // Insert a Symbol in the table
    public boolean insertSymbol(Simbolo symbol, String line, int level) {
        ArrayList<Simbolo> temp = new ArrayList<>();
        
        if ( level == 0 )
            temp = globalList;
        else
            temp = localList;
        
        if ( searchSymbol(symbol.getLexema(),  level) == null ) {
            temp.add(symbol);
            return true;
        } 
        
        return false;
    }
    
    // Remove a Symbol in the table
    public boolean removeSymbol(String lexeme, int level) {
        ArrayList<Simbolo> temp = new ArrayList<>();
        
        if ( level == 0 )
           temp = globalList;
        else
           temp = localList;
        
        for( Simbolo s: temp )
            if ( s.getLexema().equals(lexeme) ) {
                temp.remove(s);
                
                return true;
            } 
        
        return false;
    }
    
    public String setCategory(String token) {
        switch(token) {
            case "Palabra_Reservada_Program":
                return "Palabra que inicia el programa";
               
            case "Identificador":
                return "Variable";
                
            case "Palabra_Reservada_Procedure":
                return "Funcion del programa";
                
            case "Palabra_Reservada_Begin":
                return "Inicio de un bloque";
                
            case "Palabra_Reservada_End":
                return "Final de un bloque";
                
            case "Palabra_Reservada_If":
                return "Condicional";
                
            case "Palabra_Reservada_Read":
                return "Lector";
                
            case "Palabra_Reservada_Write":
                return "Escribir";
                
            case "Palabra_Reservada_While":
                return "Blucle de repetición";
                
            case "Palabra_Reservada_Int":
            case "Palabra_Reservada_Boolean":
                return "Tipo de variable";
                
            default:
                return "--------------";
        }
    }
    
    public String setType(int count, String line) {  
        for ( int i = count; i > 0; i-- ) {
            if ( tokens.get(i).getLinea().equals(line) ) {
                if ( tokens.get(i).getToken().equals("Palabra_Reservada_Int") )
                    return "Entero";
                else if ( tokens.get(i).getToken().equals("Palabra_Reservada_Boolean") )
                    return "Booleano";
            }
            
            else
                break;
        }
        
        return "";
    }
    
    public String setScope(String lexeme, int level) {
        if ( lexeme.equals("procedure") )
            return "Procedimento";
        else if ( lexeme.equals("a1") )
            return "Parametro";
        else if ( level == 0 )
            return "Global";
        else
            return "Local";
    }
    
    // ######################### --- Generate and Execute Intermediate Code Methods --- ###################################
    public ArrayList<Codigo> generateIntermediateCode(String textEdit) {
        ArrayList<Analizar> tokens = analyseLexic(textEdit);
        ArrayList<Codigo> codeList = new ArrayList<>();
        int count = 0;
        int positionExpression = -1;
        String lineExpression = "";
        String actualLine;
        int i = 0;
        
        while ( true ) {  
            actualLine = tokens.get(i).getToken();
            
            switch(tokens.get(i).getToken())
            {
                case "Palabra_Reservada_Program":
                    codeList.add(new Codigo(-1, "program", "INPP", count) );
                    count++;
                    break;
                
                case "Identificador":
                    if ( !tokens.get(i-1).getToken().equals("Palabra_Reservada_Program") )
                        if ( isDeclaring(tokens, i) ) {
                            codeList.add(new Codigo(-1, tokens.get(i).getLexema(), "AMEN 1", count) );
                            count++;
                        }

                        else {
                            codeList.add(new Codigo(-1, tokens.get(i).getLexema(), 
                                    "CRVL " + getPosition(codeList, tokens.get(i).getLexema(), i), count) );

                            if ( tokens.get(i+1).getToken().equals("Operador_Igual") ) {
                                positionExpression = count;
                                lineExpression = tokens.get(i).getLinea();
                            }

                            count++;

                        }
                    break;
                    
                case "Palabra_Reservada_Read":
                    codeList.add(new Codigo(-1, "read", "LEIT", count) );
                    count++;
                    
                    codeList.add(new Codigo(-1, tokens.get(i).getLexema(), 
                            "ARMZ " + getPosition(codeList, tokens.get(i+2).getLexema(), count), count) );
                    count++;
                    
                    codeList.add(new Codigo(-1, "read", "LEIT", count) );
                    count++;
                    
                    codeList.add(new Codigo(-1, tokens.get(i).getLexema(), 
                            "ARMZ " + getPosition(codeList, tokens.get(i+4).getLexema(), count), count) );
                    count++;
                    i+=6;
                    break;
                
                case "ENTERO":
                    codeList.add(new Codigo(-1, tokens.get(i).getLexema(), 
                                "CRCT " + tokens.get(i).getLexema(), count) );
                    count++;
                    break;
                    
                case "Palabra_Reservada_Div":
                    codeList.add(new Codigo(-1, tokens.get(i+1).getLexema(), 
                                "CRVL " + getPosition(codeList, tokens.get(i+1).getLexema(), count), count) );
                    count++;
                    codeList.add(new Codigo(-1, tokens.get(i).getLexema(), 
                                "DIVI", count) );
                    count++;
                    i++;
                    break;
                
                case "Operador_Suma":
                    codeList.add(new Codigo(-1, tokens.get(i).getLexema(), 
                                "SUMA", count) );
                    count++;
                    break;
                    
                case "Operador_Multiplicacion":
                     codeList.add(new Codigo(-1, tokens.get(i).getLexema(), 
                                "MULT", count) );
                     count++;
                     break;
                     
                 case "Palabra_Reservada_Write":
                    codeList.add(new Codigo(-1, tokens.get(i+2).getLexema(), 
                            "CRVL 0", count));
                    count++;
                    
                    codeList.add(new Codigo(-1, "write", "IMPE", count) );
                    count++;
                    i += 3;
                    break;
                    
                 case "Palabra_Reservada_If":
                     codeList.add(new Codigo(-1, tokens.get(i+1).getLexema(), 
                            "CRVL " + getPosition(codeList, tokens.get(i+1).getLexema(), count), count) );
                     count++;
                     
                     codeList.add(new Codigo(-1, tokens.get(i+3).getLexema(), 
                            "CRVL " + getPosition(codeList, tokens.get(i+3).getLexema(), count), count) );
                     count++;
                     
                     if ( tokens.get(i+2).getLexema().equals(">") ) {
                         codeList.add(new Codigo(-1, tokens.get(i+2).getLexema(), 
                            "CMMA", count) );
                         count++;
                     }
                     else if ( tokens.get(i+2).getLexema().equals("<") ) {
                         codeList.add(new Codigo(-1, tokens.get(i+2).getLexema(), 
                            "CMME", count) );
                         count++;
                     }
                     else if ( tokens.get(i+2).getLexema().equals("=") ) {
                         codeList.add(new Codigo(-1, tokens.get(i+2).getLexema(), 
                            "CMIG", count) );
                         count++;
                     }
                     else if ( tokens.get(i+2).getLexema().equals(">=") ) {
                         codeList.add(new Codigo(-1, tokens.get(i+2).getLexema(), 
                            "CMAG", count) );
                         count++;
                     }
                     else if ( tokens.get(i+2).getLexema().equals("<=") ) {
                         codeList.add(new Codigo(-1, tokens.get(i+2).getLexema(), 
                            "CMEG", count) );
                         count++;
                     }
                     else {
                         codeList.add(new Codigo(-1, tokens.get(i+2).getLexema(), 
                            "CMDG", count) );
                         count++;
                     }
                     
                     codeList.add(new Codigo(-1, "DSVF", "DSVF 0", count) );
                     count++;
                     
                     i += 3;
                     break;
                     
                 case "Palabra_Reservada_End":
                    codeList.add(new Codigo(-1, tokens.get(i).getLexema(), "NADA", count) );
                    count++;
                    
                    codeList.add(new Codigo(-1, tokens.get(i).getLexema(), "PARA", count) );
                    count++;
            }
            
            if ( codeList.get(count-1).getCodigo().equals("PARA") ) {
                break;
            }
            
            if ( positionExpression != -1 && !actualLine.equals(lineExpression) ) {
                codeList.add(new Codigo(-1, "", 
                                "ARMZ ", positionExpression));
                positionExpression = -1;
                lineExpression = "";
                count++;
            }
            
            i++;
        }
        
        return codeList;
    }
    
    public boolean isDeclaring(ArrayList<Analizar> tokens, int position) {
        String oldLine = tokens.get(position).getLinea();
        
        if ( position > 0)
            while ( position > 0 && tokens.get(position).getLinea().equals(oldLine) ) {
                if ( tokens.get(position).getLexema().equals("var") )
                    return true;

                position--;
            }
        
        return false;
    }
    
    public int getPosition(ArrayList<Codigo> codeList, String lexeme, int position) {
        while ( --position > 0 && position < codeList.size()) {
            if ( codeList.get(position).getLexema().equals(lexeme) )
                return position-1;
        }
        
        return -1;
    }
}

class Sortybyroll implements Comparator<Error>
{
    public int compare(Error a, Error b)
    {
        int linea = Integer.parseInt(a.getLinea());
        int lineb = Integer.parseInt(b.getLinea());

        return linea - lineb;
    }
}
 