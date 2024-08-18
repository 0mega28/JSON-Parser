package com.example.compiler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JSONRecognizerTest {
    @Test
    void recognizeCorrectWithoutSpace() {
        final String input = """
                {"metadata":{"created":"1600"},"data":[{"key":"value","true":true,"false":false,"number":2019,"null":null,"array":[],"object":{}},{}]}""";

        assertTrue(JSONRecognizer.recognize(input));
    }

    @Test
    void recognizeCorrectWithSpace() {
        final String input = """
                {
                  "metadata": {
                    "created": "1600"
                  },
                  "data": [
                    {
                      "key": "value",
                      "true": true,
                      "false": false,
                      "number": 2019,
                      "null": null,
                      "array": [],
                      "object": {}
                    },
                    {}
                  ]
                }""";

        assertTrue(JSONRecognizer.recognize(input));
    }

    @Test
    void recognizeInCorrect() {
        final String input = """
                {"metadata""created":"1600"},"data":[{"key":"value","true":true,"false":false,"number":2019,"null":null,"array":[],"object":{}},{}]}""";

        assertFalse(JSONRecognizer.recognize(input));
    }
}