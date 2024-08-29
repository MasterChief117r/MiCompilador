import java_cup.runtime.*;
import model.Analzar;

%%

%{

private Analyse createAnalyse(String lexeme, String token, int line, int iniCol, int endCol) {
    return (new Analyse(lexeme, token, String.valueOf(line), String.valueOf(iniCol), String.valueOf(yylength() + endCol), ""));
}

%}

%public
%class LexicalAnalyzer
%type Analizar
%line
%column

BLANCO = [\n| |\t|\r]
IDENTIFICADOR = [_|a-z|A-Z][a-z|A-Z|0-9|_]*
ENTERO = 0|[1-9][0-9]*
REAL = [0-9][0-9]*","[0-9]+

LineaTerminador = \r|\n|\r\n
CA = \{
FC = \}
LlaveAperturaNegado = [^}]
ComentarioCorpo = {LlaveAperturaNegado}*
Comment = {AC}{ComentarioCorpo}{FC}
CommentLine = {OP_DIV}{OP_DIV}{ComentarioCorpo}

PA = "("
PC = ")"
COA = "["
COC = "]"
COMA = ","
PONTO_Y_COMA = ";"
DOS_PUNTOS = ":"

OP_SUMA = "+"
OP_RESTA = "-"
OP_DIV = "/"
OP_MULT = "*"

OP_MENOR = "<"
OP_IGUAL = ":="
OP_MAYOR = ">"
OP_MENORIGUAL = "<="
OP_MAYORIGUAL = ">="
OP_DIFERENTE = "<>"

PROGRAM = "program"
PROCEDURE = "procedure"
VAR = "var"
INT = "int"
BOOLEAN = "boolean"
READ = "read"
WRITE = "write"
TRUE = "true"
FALSE = "false"
BEGIN = "begin"
END = "end"
IF = "if"
WHILE = "while"
DO = "do"
ELSE = "else"
THEN = "then"
DIV = "div"
AND = "and"
OR = "or"
NOT = "not"

%%

{BLANCO} { /* */ }

{ENTERO} { return createAnalyse(yytext(), "Entero", yyline, yycolumn, yycolumn); }
{REAL} { return createAnalyse(yytext(), "Real", yyline, yycolumn, yycolumn); }

{PA} { return createAnalyse(yytext(), "Parentesis_Apertura", yyline, yycolumn, yycolumn); }
{PC} { return createAnalyse(yytext(), "Parentesis_Cierre", yyline, yycolumn, yycolumn); }
{COA} { return createAnalyse(yytext(), "Corchetes_Apertura", yyline, yycolumn, yycolumn); }
{COA} { return createAnalyse(yytext(), "Corchetes-Cierre", yyline, yycolumn, yycolumn); }
{COMA} { return createAnalyse(yytext(), "Coma", yyline, yycolumn, yycolumn); }
{PONTO_Y_COMA} { return createAnalyse(yytext(), "Ponto_Virgula", yyline, yycolumn, yycolumn); }

{OP_SUMA} { return createAnalyse(yytext(), "Operador_Suma", yyline, yycolumn, yycolumn); }
{OP_RES} { return createAnalyse(yytext(), "Operador_Resta", yyline, yycolumn, yycolumn); }
{OP_MULT} { return createAnalyse(yytext(), "Operador_Multiplicacion", yyline, yycolumn, yycolumn); }

{OP_MENOR} { return createAnalyse(yytext(), "Operador_Menor", yyline, yycolumn, yycolumn); }
{OP_IGUAL} { return createAnalyse(yytext(), "Operador_Igual", yyline, yycolumn, yycolumn); }
{OP_MAYOR} { return createAnalyse(yytext(), "Operador_Mayor", yyline, yycolumn, yycolumn); }
{OP_MENORIGUAL} { return createAnalyse(yytext(), "Operador_Menor_Igual", yyline, yycolumn, yycolumn); }
{OP_MAIORIGUAL} { return createAnalyse(yytext(), "Operador_Mayor_Igual", yyline, yycolumn, yycolumn); }
{OP_DIFERENTE} { return createAnalyse(yytext(), "Operador_Diferente", yyline, yycolumn, yycolumn); }

{PROGRAM} { return createAnalyse(yytext(), "Palabra_Reservada_Program", yyline, yycolumn, yycolumn); }
{PROCEDURE} { return createAnalyse(yytext(), "Palabra_Reservada_Procedure", yyline, yycolumn, yycolumn); }
{VAR} { return createAnalyse(yytext(), "Palabra_Reservada_Var", yyline, yycolumn, yycolumn); }
{INT} { return createAnalyse(yytext(), "Palabra_Reservada_Int", yyline, yycolumn, yycolumn); }
{BOOLEAN} { return createAnalyse(yytext(), "Palabra_Reservada_Boolean", yyline, yycolumn, yycolumn); }
{READ} { return createAnalyse(yytext(), "Palabra_Reservada_Read", yyline, yycolumn, yycolumn); }
{WRITE} { return createAnalyse(yytext(), "Palabra_Reservada_Write", yyline, yycolumn, yycolumn); }
{TRUE} { return createAnalyse(yytext(), "Palabra_Reservada_True", yyline, yycolumn, yycolumn); }
{FALSE} { return createAnalyse(yytext(), "Palabra_Reservada_False", yyline, yycolumn, yycolumn); }
{BEGIN} { return createAnalyse(yytext(), "Palabra_Reservada_Begin", yyline, yycolumn, yycolumn); }
{END} { return createAnalyse(yytext(), "Palabra_Reservada_End", yyline, yycolumn, yycolumn); }
{IF} { return createAnalyse(yytext(), "Palabra_Reservada_If", yyline, yycolumn, yycolumn); }
{WHILE} { return createAnalyse(yytext(), "Palabra_Reservada_While", yyline, yycolumn, yycolumn); }
{DO} { return createAnalyse(yytext(), "Palabra_Reservada_Do", yyline, yycolumn, yycolumn); }
{ELSE} { return createAnalyse(yytext(), "Palabra_Reservada_Else", yyline, yycolumn, yycolumn); }
{THEN} { return createAnalyse(yytext(), "Palabra_Reservada_Then", yyline, yycolumn, yycolumn); }
{DIV} { return createAnalyse(yytext(), "Palabra_Reservada_Div", yyline, yycolumn, yycolumn); }
{AND} { return createAnalyse(yytext(), "Palabra_Reservada_And", yyline, yycolumn, yycolumn); }
{OR} { return createAnalyse(yytext(), "Palabra_Reservada_Or", yyline, yycolumn, yycolumn); }
{NOT} { return createAnalyse(yytext(), "Palabra_Reservada_Not", yyline, yycolumn, yycolumn); }

{IDENTIFICADOR} { return createAnalyse(yytext(), "Identificador", yyline, yycolumn, yycolumn); }

{Comment} { /* Ignore Comments */ }
{CommentLine} { /* Ignore Comments */ }

{LLA} { return createAnalyse(yytext(), "Llaves_Apertura", yyline, yycolumn, yycolumn); }
{LLC} { return createAnalyse(yytext(), "Llaves_Cierre", yyline, yycolumn, yycolumn); }
{OP_DIV} { return createAnalyse(yytext(), "Operador_Division", yyline, yycolumn, yycolumn); }
{DOS_PUNTOS} { return createAnalyse(yytext(), "Operador_Dos_Puntos", yyline, yycolumn, yycolumn); }

. { return createAnalyse(yytext(), "Caracter_Invalido", yyline, yycolumn, yycolumn); }


