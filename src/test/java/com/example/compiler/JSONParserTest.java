package com.example.compiler;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JSONParserTest {
    @Test
    void parseCorrectWithoutSpace() {
        final String input = """
                {"metadata":{"created":"1600"},"data":[{"key":"value","true":true,"false":false,"number":2019,"null":null,"array":[],"object":{}},{}]}""";

        HashMap<String, Object> inner = new HashMap<>();
        inner.put("key", "value");
        inner.put("true", true);
        inner.put("false", false);
        inner.put("number", 2019);
        inner.put("null", null);
        inner.put("array", List.of());
        inner.put("object", Map.of());
        Object expected = Map.of(
                "metadata", Map.of("created", "1600"),
                "data", List.of(
                        inner,
                        Map.of()
                )
        );
        assertEquals(expected, JSONParser.parse(input));
    }

    @Test
    void parseCorrectWithSpace() {
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

        HashMap<String, Object> inner = new HashMap<>();
        inner.put("key", "value");
        inner.put("true", true);
        inner.put("false", false);
        inner.put("number", 2019);
        inner.put("null", null);
        inner.put("array", List.of());
        inner.put("object", Map.of());
        Object expected = Map.of(
                "metadata", Map.of("created", "1600"),
                "data", List.of(
                        inner,
                        Map.of()
                )
        );
        assertEquals(expected, JSONParser.parse(input));
    }

    @Test
    void parseInCorrect() {
        final String input = """
                {"metadata""created":"1600"},"data":[{"key":"value","true":true,"false":false,"number":2019,"null":null,"array":[],"object":{}},{}]}""";

        assertNull(JSONParser.parse(input));
    }
}