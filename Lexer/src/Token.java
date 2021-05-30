public class Token {
    private TokenType type;
    private String name;

    public Token(TokenType type, char a) {
        name = new String();
        name += a;
        this.type = type;
    }


    public String getName() {
        return name;
    }


    public void addChar(char ch) {
        name += ch;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "{" + type + ", " + name + "}";
    }
}
