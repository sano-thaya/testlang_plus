package com.testlang.compiler.parser;

import java_cup.runtime.Symbol;

%%
%public
%class TestLangLexer
%unicode
%line
%column
%cup

%{
    private Symbol symbol(int type) {
        return new Symbol(type, yyline + 1, yycolumn + 1);
    }

    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline + 1, yycolumn + 1, value);
    }

    private String unescapeString(String raw) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if (c == '\\' && i + 1 < raw.length()) {
                char n = raw.charAt(++i);
                switch (n) {
                    case '"': sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    case '/': sb.append('/'); break;
                    case 'b': sb.append('\b'); break;
                    case 'f': sb.append('\f'); break;
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    default:
                        throw new RuntimeException("Line " + (yyline + 1) + ": Invalid escape sequence \\" + n + " in string literal");
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
%}

Whitespace = [ \t\r\n\f\uFEFF]+
Identifier = [A-Za-z_][A-Za-z0-9_]*
Number = [0-9]+
StringLiteral = \"([^\\\"\r\n]|\\[\"\\/bfnrt])*\"
InvalidIdentifier = [0-9]+[A-Za-z_][A-Za-z0-9_]*
Comment = "//"[^\r\n]*

%%

{Whitespace}                 { /* ignore */ }
{Comment}                    { /* ignore */ }

"config"                    { return symbol(sym.CONFIG); }
"base_url"                  { return symbol(sym.BASE_URL); }
"header"                    { return symbol(sym.HEADER); }
"let"                       { return symbol(sym.LET); }
"test"                      { return symbol(sym.TEST); }
"GET"                       { return symbol(sym.GET); }
"POST"                      { return symbol(sym.POST); }
"PUT"                       { return symbol(sym.PUT); }
"DELETE"                    { return symbol(sym.DELETE); }
"expect"                    { return symbol(sym.EXPECT); }
"status"                    { return symbol(sym.STATUS); }
"body"                      { return symbol(sym.BODY); }
"contains"                  { return symbol(sym.CONTAINS); }

"{"                         { return symbol(sym.LBRACE); }
"}"                         { return symbol(sym.RBRACE); }
";"                         { return symbol(sym.SEMICOLON); }
"="                         { return symbol(sym.EQUALS); }

{StringLiteral}              {
                                String content = yytext().substring(1, yytext().length() - 1);
                                return symbol(sym.STRING, unescapeString(content));
                             }

{InvalidIdentifier}          { throw new RuntimeException("Line " + (yyline + 1) + ": Invalid identifier '" + yytext() + "'"); }
{Number}                     { return symbol(sym.NUMBER, Integer.parseInt(yytext())); }
{Identifier}                 { return symbol(sym.IDENTIFIER, yytext()); }

\"([^\\\"\r\n]|\\.)*    { throw new RuntimeException("Line " + (yyline + 1) + ": Unterminated string literal"); }
.                            { throw new RuntimeException("Line " + (yyline + 1) + ": Unexpected character '" + yytext() + "'"); }
