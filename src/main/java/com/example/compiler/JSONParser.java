package com.example.compiler;

import java.util.*;
import java.util.function.Supplier;

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
        JSONParser parser = new JSONParser(input);
        if (parser.valueParser.parse()) return parser.stack.pop();
        else return null;
    }

    private void skipWhitespace() {
        while (pos < input.length() &&
               Character.isWhitespace(input.charAt(pos))) {
            pos++;
        }
    }

    private boolean parseChar(char ch) {
        skipWhitespace();
        if (input.charAt(pos) == ch) {
            pos++;
            return true;
        }
        return false;
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

    interface Parser {
        boolean parse();
    }

    private class CharParser implements Parser {
        private final char c;

        CharParser(char c) {
            this.c = c;
        }

        @Override
        public boolean parse() {
            return parseChar(c);
        }
    }

    private class NumberParser implements Parser {

        @Override
        public boolean parse() {
            return parseNumber();
        }
    }

    private class StringLitParser implements Parser {

        @Override
        public boolean parse() {
            return parseStringLit();
        }
    }

    private class StringParser implements Parser {
        private final String toParse;
        private final Object value;

        StringParser(String toParse, Object value) {
            this.toParse = toParse;
            this.value = value;
        }

        @Override
        public boolean parse() {
            skipWhitespace();
            if (input.startsWith(toParse, pos)) {
                pos += toParse.length();
                stack.push(value);
                return true;
            }

            return false;
        }
    }

    private class Sequence implements Parser {
        private final Parser[] parsers;

        Sequence(Parser... parsers) {
            this.parsers = parsers;
        }

        @Override
        public boolean parse() {
            int pos0 = pos;
            for (Parser parser : parsers) {
                boolean success = parser.parse();
                if (!success) {
                    pos = pos0;
                    return false;
                }
            }

            return true;
        }
    }

    private class Separated implements Parser {
        private final Parser parser;
        private final CharParser separatorParser;

        Separated(char separator, Parser parser) {
            this.separatorParser = new CharParser(separator);
            this.parser = parser;
        }

        @Override
        public boolean parse() {
            if (parser.parse()) {
                while (true) {
                    int pos0 = pos;
                    boolean success = separatorParser.parse() && parser.parse();
                    if (!success) {
                        pos = pos0;
                        return false;
                    }
                }
            }
            return true;
        }
    }

    private class Optional implements Parser {
        private final Parser parser;

        Optional(Parser parser) {
            this.parser = parser;
        }

        @Override
        public boolean parse() {
            parser.parse();
            return true;
        }
    }

    private class Either implements Parser {
        private final Parser[] parsers;

        Either(Parser... parsers) {
            this.parsers = parsers;
        }


        @Override
        public boolean parse() {
            for (Parser parser : parsers) {
                if (parser.parse()) {
                    return true;
                }
            }
            return false;
        }
    }

    private class ForwardRef implements Parser {
        private final Supplier<Parser> parserSupplier;

        ForwardRef(Supplier<Parser> parserSupplier) {
            this.parserSupplier = parserSupplier;
        }

        public boolean parse() {
            return parserSupplier.get().parse();
        }
    }

    private class ComposeObject implements Parser {
        private final Parser parser;

        public ComposeObject(Parser parser) {
            this.parser = parser;
        }

        @Override
        public boolean parse() {
            int stackSize0 = stack.size();
            if (!parser.parse()) return false;

            HashMap<String, Object> object = new HashMap<>();
            while (stack.size() > stackSize0) {
                Object value = stack.pop();
                String key = (String) stack.pop();
                object.put(key, value);
            }
            stack.push(object);

            return true;
        }
    }

    private class ComposeArray implements Parser {
        private final Parser parser;

        public ComposeArray(Parser parser) {
            this.parser = parser;
        }

        @Override
        public boolean parse() {
            int stackSize0 = stack.size();
            if (!parser.parse()) return false;

            ArrayList<Object> array = new ArrayList<>();
            while (stack.size() > stackSize0) {
                array.add(stack.pop());
            }
            Collections.reverse(array);
            stack.push(array);

            return true;
        }
    }


    private final Parser nullParser = new StringParser("null", null);
    private final Parser trueParser = new StringParser("true", true);
    private final Parser falseParser = new StringParser("false", false);
    private final Parser stringLitParser = new StringLitParser();
    private final Parser numberParser = new NumberParser();
    private final Parser valueParser = new Either(
            stringLitParser, numberParser,
            nullParser, trueParser, falseParser,
            new ForwardRef(() -> this.objectParser),
            new ForwardRef(() -> this.arrayParser)
    );
    private final Parser pairParser = new Sequence(
            stringLitParser,
            new CharParser(':'),
            valueParser
    );
    private final Parser objectParser = new ComposeObject(
            new Sequence(
                    new CharParser('{'),
                    new Optional(
                            new Separated(',', pairParser)
                    ),
                    new CharParser('}')
            ));
    private final Parser arrayParser = new ComposeArray(
            new Sequence(
                    new CharParser('['),
                    new Optional(
                            new Separated(',', valueParser)
                    ),
                    new CharParser(']')
            ));
}
