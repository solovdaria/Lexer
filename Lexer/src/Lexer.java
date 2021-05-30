import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class Lexer {
    BufferedReader reader;
    ArrayList<Token> tokens;

    String[] keywords = {"abstract", "arguments", "Array", "boolean", "break", "byte", "case",
            "catch", "char", "class", "const", "continue", "Date", "debugger", "decodeURI", "decodeURIComponent",
            "default", "delete", "do", "double", "else", "encodeURI", "encodeURIComponent", "enum", "Error",
            "eval", "EvalError", "export", "extends", "final", "finally", "float", "for", "Function",
            "goto", "if", "implements", "import", "in", "Infinity", "instanceof", "int", "interface", "isFinite",
            "isNaN", "let", "long", "Math", "NaN", "native", "new", "null", "Number", "Object", "package", "parseFloat",
            "parseInt", "private", "protected", "public", "RangeError", "ReferenceError", "RegExp", "return", "short",
            "static", "String", "super", "switch", "synchronized", "SyntaxError", "this", "throw", "transient",
            "try", "TypeError", "typeof", "undefined", "URIError", "var", "void", "volatile", "while", "with", "yield"};

    public Lexer(String filename) throws FileNotFoundException {
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename),
                Charset.forName("UTF-8")));
        tokens = new ArrayList<Token>();

    }

    public void tokenize() throws IOException {
        int r = reader.read();


        while (r != -1) {

            char ch = (char) r;
            if (!Character.isWhitespace(ch)) {
                if (Character.isLetter(ch) || ch == '$' || ch == '_') {
                    Token token = new Token(TokenType.IDENTIFIER, ch);
                    r = literalFA(ch, token);
                    tokens.add(token);
                } else if (isSeparator(ch)) {
                    Token token = new Token(TokenType.SEPARATOR, ch);
                    r = reader.read();
                    tokens.add(token);

                } else if (isOperator(ch)) {
                    Token token = new Token(TokenType.OPERATOR, ch);
                    r = operatorFA(ch, token);
                    tokens.add(token);
                } else if (ch == '\"' || ch == '\'') {
                    Token token = new Token(TokenType.STRING_LITERAL, ch);
                    r = stringLiteralFA(ch, token);
                    tokens.add(token);
                } else if (Character.isDigit(ch)) {
                    Token token = new Token(TokenType.INTEGER_LITERAL, ch);
                    r = numberFA(ch, token);
                    tokens.add(token);
                } else {
                    Token token = new Token(TokenType.INCORRECT_TOKEN, ch);
                    r = reader.read();
                    tokens.add(token);
                }
            } else r = reader.read();
        }
        for (Token t : tokens) System.out.println(t);

    }

    private boolean isKeyword(Token token) {
        String data = token.getName();
        for (String keyword : keywords) {
            if (data.equals(keyword)) return true;
        }
        return false;
    }

    private boolean isBoolean(Token token) {
        String data = token.getName();
        if (data.equals("true") || data.equals("false"))
            return true;
        return false;
    }

    private boolean isSeparator(int ch) {
        return ch == '(' || ch == ')' || ch == '{' || ch == '}' || ch == ',' || ch == '.' || ch == ';' || ch == '[' || ch == ']';
    }

    private int numberFA(char ch, Token token) throws IOException {
        int nextCh = reader.read();
        boolean isFloat = false;
        if (ch == '0') {
            if (nextCh == 'x' || nextCh == 'X') {
                token.addChar((char) nextCh);
                nextCh = reader.read();
                while (Character.isDigit(nextCh) || (nextCh >= 97 && nextCh <= 102) || (nextCh >= 65 && nextCh <= 72)) {
                    token.addChar((char) nextCh);
                    nextCh = reader.read();
                }
            } else if (nextCh == 'b' || nextCh == 'B') {
                token.addChar((char) nextCh);
                nextCh = reader.read();
                while (nextCh == '0' || nextCh == '1') {
                    token.addChar((char) nextCh);
                    nextCh = reader.read();
                }
            } else if (nextCh == 'o' || nextCh == 'O') {
                token.addChar((char) nextCh);
                nextCh = reader.read();
                while (nextCh >= 48 && nextCh <= 55) {
                    token.addChar((char) nextCh);
                    nextCh = reader.read();
                    nextCh = eProcessing(token, nextCh);
                }
            } else {
                int tempCh = dotProcessing(nextCh, token);
                if (tempCh != -2) {
                    isFloat = true;
                    nextCh = tempCh;
                } else {

                    while (nextCh >= 48 && nextCh <= 55) {
                        token.addChar((char) nextCh);
                        nextCh = reader.read();
                        nextCh = eProcessing(token, nextCh);
                    }
                }
            }

        } else {
            while (Character.isDigit(nextCh)) {
                token.addChar((char) nextCh);
                nextCh = reader.read();
            }
            int dotPrRes = dotProcessing(nextCh, token);
            if (dotPrRes != -2) {
                nextCh = dotPrRes;
                isFloat = true;
            } else {

                nextCh = eProcessing(token, nextCh);
            }


        }
        if (nextCh == 'f' || nextCh == 'F' || nextCh == 'd' || nextCh == 'D') {
            token.setType(TokenType.FLOATING_POINT_LITERAL);
            token.addChar((char) nextCh);
            nextCh = reader.read();
        } else if (!isFloat) {
            if (nextCh == 'L' || nextCh == 'l') {
                token.addChar((char) nextCh);
                nextCh = reader.read();

            }
        } else token.setType(TokenType.FLOATING_POINT_LITERAL);
        int dotProcRes = dotProcessing(nextCh, token);
        if (dotProcRes != -2) {
            token.setType(TokenType.INCORRECT_TOKEN);
            nextCh = dotProcRes;
        }
        if (Character.isWhitespace(nextCh) || isOperator(nextCh) || nextCh == -1 || (isSeparator(nextCh) && nextCh != '.'))
            return nextCh;

        //Error handling
        token.addChar((char) nextCh);
        nextCh = literalFA(nextCh, token);
        token.setType(TokenType.INCORRECT_TOKEN);
        return nextCh;
    }

    private int dotProcessing(int nextCh, Token token) throws IOException {
        if (nextCh == '.') {
            token.addChar((char) nextCh);
            nextCh = reader.read();
            while (Character.isDigit(nextCh)) {
                token.addChar((char) nextCh);
                nextCh = reader.read();
            }
            nextCh = eProcessing(token, nextCh);
            return nextCh;
        } else return -2;
    }

    private int eProcessing(Token token, int nextCh) throws IOException {
        if (nextCh == 'e' || nextCh == 'E') {
            token.addChar((char) nextCh);
            nextCh = reader.read();
            if (nextCh == '-' || nextCh == '+') {
                token.addChar((char) nextCh);
                nextCh = reader.read();
            }
            while (Character.isDigit(nextCh)) {
                token.addChar((char) nextCh);
                nextCh = reader.read();
            }
        }
        return nextCh;
    }

    private int stringLiteralFA(int ch, Token token) throws IOException {
        int r = reader.read();

        while (r != ch && r != -1) {
            if (r == -1 || r == '\n') {
                token.setType(TokenType.INCORRECT_TOKEN);
            }
            token.addChar((char) r);
            if (r == '\\') {
                int nextCh = reader.read();
                token.addChar((char) nextCh);
            }
            r = reader.read();
        }
        token.addChar((char) r);
        return reader.read();
    }

    private int operatorFA(int ch, Token token) throws IOException {
        int opType = operatorTypeCheck(ch);
        int ch2;
        switch (opType) {
            case 0:

                return reader.read();
            case 1:
                ch2 = reader.read();

                if (ch2 == '=') {
                    token.addChar((char) ch2);
                    ch2 = reader.read();
                    if(ch == '!' && ch2=='='){
                        token.addChar((char) ch2);
                        ch2 = reader.read();
                    }

                } else if (ch == '/') {
                    if (ch2 == '*' || ch2 == '/') {
                        token.addChar((char) ch2);
                        token.setType(TokenType.COMMENT);
                        return (commentFA(ch2, token));
                    }else{
                        token.addChar((char) ch2);
                        token.setType(TokenType.REGEXP);
                        return(regExFA(ch2, token));
                    }

                }
                return ch2;

            case 2:
                ch2 = eqDuplCheck(ch, token);
                if (ch2 == -2) return reader.read();
                else return ch2;

            default:
                ch2 = eqDuplCheck(ch, token);
                if (ch2 != -2) return ch2;
                if (ch == '>') {
                    ch2 = eqDuplCheck(ch, token);
                    if (ch2 != -2) return ch2;
                }
                ch2 = reader.read();
                if (ch2 == '=') {
                    token.addChar((char) ch2);
                    return reader.read();
                } else return ch2;
        }

    }

    private int commentFA(int ch2, Token token) throws IOException {
        int r = reader.read();
        if (ch2 == '*') {
            do {
                //r = reader.read();
                if (r == -1) {
                    token.setType(TokenType.INCORRECT_TOKEN);
                    return -1;
                }
                token.addChar((char) r);
                if (r == '*') {
                    int nextCh = reader.read();
                    if (nextCh == '/') {
                        token.addChar((char) nextCh);
                        break;
                    } else {
                        r = nextCh;

                    }


                } else {
                    r = reader.read();
                }
            } while (true);
            return reader.read();
        } else {
            do {
                r = reader.read();
                if (r == -1 || r == '\n' || r == 13) return r;
                token.addChar((char) r);


            } while (true);
        }
    }


    private int regExFA(int ch, Token token) throws IOException{
        if(ch == '\\'){
            int ch2 = reader.read();
            if (isaBackslashSeq(ch2)) {
                token.addChar((char)ch2);
            }else{
                token.setType(TokenType.INCORRECT_TOKEN);
                }
            }else{
            if(ch == '\n' || ch == 13 || ch==-1){
                token.setType(TokenType.INCORRECT_TOKEN);
            }
        }

        do{
            ch = reader.read();
            token.addChar((char)ch);
            if(ch == '\\'){
                int ch2 = reader.read();
                if (isaBackslashSeq(ch2)) {
                    token.addChar((char)ch2);
                }else{
                    token.setType(TokenType.INCORRECT_TOKEN);
                }
            }else{
                if(ch == '\n' || ch == 13 || ch == -1){
                    token.setType(TokenType.INCORRECT_TOKEN);
                }
            }
        }while(ch!='\n' && ch!= '/' && ch!=-1);
        return reader.read();
        }



    private boolean isaBackslashSeq(int nextCh) {
        if( nextCh != 'n' && nextCh != 'r' && nextCh != 't' && nextCh != 'f' &&
                nextCh != 'b' && nextCh != '\"' && nextCh != '\\' && nextCh != '\'' && nextCh != '/'){
            return false;
    }
        return true;
    }
    private int eqDuplCheck(int ch, Token token) throws IOException {
        int ch2;
        ch2 = reader.read();
        if (ch2 == ch) {
            token.addChar((char) ch2);
            return -2;
        }else if (ch2 == '=') {
            token.addChar((char) ch2);
            return reader.read();
        }  else return ch2;

    }

    private int literalFA(int ch, Token token) throws IOException {
        ch = reader.read();
        while ((Character.isLetter(ch) || Character.isDigit(ch) || ch == '$' || ch == '_') && ch != -1) {
            token.addChar((char) ch);
            ch = reader.read();
            ch = (char) ch;
        }
        if (isKeyword(token)) token.setType(TokenType.KEYWORD);
        else if (isBoolean(token)) token.setType(TokenType.BOOLEAN_LITERAL);
        else if (token.getName().equals("null")) token.setType(TokenType.NULL_LITERAL);
        else token.setType(TokenType.IDENTIFIER);
        return ch;
    }

    private boolean isOperator(int ch) {
        if (ch == '=' || ch == '>' || ch == '<' || ch == '!' || ch == '+' || ch == '-'
                || ch == '*' || ch == '/' || ch == '|' || ch == '%' || ch == '&' || ch == '^'
                || ch == '~' || ch == ':') {
            return true;
        } else
            return false;

    }


    private int operatorTypeCheck(int ch) {
        if (ch == '~' || ch == '?' || ch == ':')
            return 0;
        if (ch == '*' || ch == '/' || ch == '%' || ch == '^'  || ch == '!' )
            return 1;
        if (ch == '+' || ch == '-' || ch == '|' || ch == '&')
            return 2;
        else
            return 3;
    }
}
