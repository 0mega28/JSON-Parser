package com.example.compiler;

import java.util.*;

/*
    Syntax
    VALUE = STRING-LIT | NUMBER | TRUE | FALSE | NULL | OBJECT | ARRAY
    OBJECT = "{" ( PAIR (, PAIR)* ) ? "}"
    PAIR = STRING-LIT ":" VALUE
    ARRAY = "[" ( VALUE (, VALUE)* )? "]"
*/
public class JSONParser {
    private int pos;
    private final String input;
    private final Stack<Object> stack = new Stack<>();

    private JSONParser(String input) {
        this.input = input;
        pos = 0;
    }

    public static Object parse(String input) {
        JSONParser recognizer = new JSONParser(input);
        if (recognizer.parseValue()) return recognizer.stack.pop();
        else return null;
    }

    private void skipWhitespace() {
        while (pos < input.length() &&
               Character.isWhitespace(input.charAt(pos))) {
            pos++;
        }
    }

    private boolean parseStringLit() {
        skipWhitespace();
        if (input.charAt(pos) != '"') return false;
        int endQuote = input.indexOf('"', pos + 1);
        if (endQuote == -1) return false;
        stack.push(input.substring(pos + 1, endQuote));
        pos = endQuote + 1;
        return true;
    }

    private boolean parseNumber() {
        skipWhitespace();
        int pos0 = pos;
        while (pos < input.length() &&
               Character.isDigit(input.charAt(pos))) {
            pos++;
        }
        if (pos0 == pos) {
            return false;
        }
        stack.push(Integer.parseInt(input.substring(pos0, pos)));
        return true;
    }

    private boolean parseNull() {
        skipWhitespace();
        String aNull = "null";
        if (input.startsWith(aNull, pos)) {
            pos += aNull.length();
            stack.push(null);
            return true;
        }
        return false;
    }

    private boolean parseTrue() {
        skipWhitespace();
        String aTrue = "true";
        if (input.startsWith(aTrue, pos)) {
            pos += aTrue.length();
            stack.push(true);
            return true;
        }
        return false;
    }

    private boolean parseFalse() {
        skipWhitespace();
        String aFalse = "false";
        if (input.startsWith(aFalse, pos)) {
            pos += aFalse.length();
            stack.push(false);
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
        skipWhitespace();
        if (input.charAt(pos) == ch) {
            pos++;
            return true;
        }
        return false;
    }

    private boolean parseObject() {
        int pos0 = pos;
        int stackSize0 = stack.size();
        boolean success = parseChar('{') &&
                          parsePairs() &&
                          parseChar('}');

        if (!success) {
            pos = pos0;
            return false;
        }

        HashMap<String, Object> object = new HashMap<>();
        while (stack.size() > stackSize0) {
            Object value = stack.pop();
            String key = (String) stack.pop();
            object.put(key, value);
        }
        stack.push(object);

        return true;
    }

    private boolean parsePair() {
        int pos0 = pos;
        boolean success = parseStringLit() &&
                          parseChar(':') &&
                          parseValue();
        if (!success) {
            pos = pos0;
            return false;
        }
        return true;
    }

    private void parsePairsTails() {
        while (true) {
            int pos0 = pos;
            boolean success = parseChar(',') &&
                              parsePair();
            if (!success) {
                pos = pos0;
                return;
            }
        }
    }

    private boolean parsePairs() {
        if (parsePair()) parsePairsTails();
        return true;
    }

    private boolean parseValues() {
        if (parseValue()) parseValueTails();
        return true;
    }

    private void parseValueTails() {
        while (true) {
            int pos0 = pos;
            boolean success = parseChar(',') &&
                              parseValue();
            if (!success) {
                pos = pos0;
                return;
            }
        }
    }

    private boolean parseArray() {
        int pos0 = pos;
        int stackSize0 = stack.size();

        boolean success = parseChar('[') &&
                          parseValues() &&
                          parseChar(']');
        if (!success) {
            pos = pos0;
            return false;
        }

        ArrayList<Object> array = new ArrayList<>();
        while (stack.size() > stackSize0) {
            array.add(stack.pop());
        }
        Collections.reverse(array);
        stack.push(array);

        return true;
    }

}
