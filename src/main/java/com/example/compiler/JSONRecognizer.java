package com.example.compiler;

/*
    Syntax
    VALUE = STRING-LIT | NUMBER | TRUE | FALSE | NULL | OBJECT | ARRAY
    OBJECT = "{" ( PAIR (, PAIR)* ) ? "}"
    PAIR = STRING-LIT ":" VALUE
    ARRAY = "[" ( VALUE (, VALUE)* )? "]"
*/
public class JSONRecognizer {
    private int pos;
    private final String input;

    private JSONRecognizer(String input) {
        this.input = input;
        pos = 0;
    }

    public static boolean recognize(String input) {
        JSONRecognizer recognizer = new JSONRecognizer(input);
        return recognizer.parseValue();
    }

    private boolean skipWhitespace() {
        while (pos < input.length() &&
               Character.isWhitespace(input.charAt(pos))) {
            pos++;
        }
        return true;
    }

    private boolean parseStringLit() {
        if (input.charAt(pos) != '"') return false;
        int endQuote = input.indexOf('"', pos + 1);
        if (endQuote == -1) return false;
        pos = endQuote + 1;
        return true;
    }

    private boolean parseNumber() {
        int currPos = pos;
        while (pos < input.length() &&
               Character.isDigit(input.charAt(pos))) {
            pos++;
        }
        return currPos != pos;
    }

    private boolean parseNull() {
        String aNull = "null";
        if (input.startsWith(aNull, pos)) {
            pos += aNull.length();
            return true;
        }
        return false;
    }

    private boolean parseTrue() {
        String aTrue = "true";
        if (input.startsWith(aTrue, pos)) {
            pos += aTrue.length();
            return true;
        }
        return false;
    }

    private boolean parseFalse() {
        String aFalse = "false";
        if (input.startsWith(aFalse, pos)) {
            pos += aFalse.length();
            return true;
        }
        return false;
    }

    private boolean parseValue() {
        return parseStringLit() || parseNumber() || parseNull() ||
               parseTrue() || parseFalse() ||
               parseObject() || parseArray();
    }

    private boolean parseChar(char ch) {
        if (input.charAt(pos) == ch) {
            pos++;
            return true;
        }
        return false;
    }

    private boolean parseObject() {
        int currPos = pos;
        boolean success = skipWhitespace() &&
                          parseChar('{') &&
                          skipWhitespace() &&
                          parsePairs() &&
                          skipWhitespace() &&
                          parseChar('}') &&
                          skipWhitespace();
        if (!success) {
            pos = currPos;
            return false;
        }
        return true;
    }

    private boolean parsePair() {
        int currPos = pos;
        boolean success = parseStringLit() &&
                          skipWhitespace() &&
                          parseChar(':') &&
                          skipWhitespace() &&
                          parseValue();
        if (!success) {
            pos = currPos;
            return false;
        }
        return true;
    }

    private boolean parsePairs() {
        if (!parsePair()) {
            return true;
        }

        while (true) {
            int currPos = pos;
            boolean success = skipWhitespace() &&
                              parseChar(',') &&
                              skipWhitespace() &&
                              parsePair() &&
                              skipWhitespace();
            if (!success) {
                pos = currPos;
                return true;
            }
        }
    }

    private boolean parseValues() {
        if (!parseValue()) {
            return true;
        }

        while (true) {
            int currPos = pos;
            boolean success = skipWhitespace() &&
                              parseChar(',') &&
                              skipWhitespace() &&
                              parseValue() &&
                              skipWhitespace();
            if (!success) {
                pos = currPos;
                return true;
            }
        }
    }


    private boolean parseArray() {
        int currPos = pos;

        boolean success = skipWhitespace() &&
                          parseChar('[') &&
                          skipWhitespace() &&
                          parseValues() &&
                          skipWhitespace() &&
                          parseChar(']') &&
                          skipWhitespace();
        if (!success) {
            pos = currPos;
            return false;
        }

        return true;
    }

}
