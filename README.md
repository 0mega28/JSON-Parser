# JSONParser

## Overview
`JSONParser` is a lightweight Java-based library designed to parse JSON strings into their corresponding Java objects. It supports parsing JSON objects, arrays, strings, numbers, boolean values, and null values, adhering to a custom-built parsing mechanism.

## Features
- Parse JSON strings into Java objects like `HashMap` and `ArrayList`.
- Handles nested objects and arrays.
- Supports standard JSON values: strings, numbers, `true`, `false`, `null`.
- Provides robust error handling for invalid JSON strings.
- Includes JUnit tests to verify functionality.

## Syntax Specification
The JSON syntax supported by `JSONParser` is defined as follows:

```
VALUE  = STRING-LIT | NUMBER | TRUE | FALSE | NULL | OBJECT | ARRAY
OBJECT = "{" ( PAIR (, PAIR)* ) ? "}"
PAIR   = STRING-LIT ":" VALUE
ARRAY  = "[" ( VALUE (, VALUE)* ) ? "]"
```

## Installation
1. Clone the repository:
   ```bash
   git clone <repository-url>
   ```

2. Import the project into your favorite IDE (e.g., IntelliJ IDEA, Eclipse).

3. Build the project using Maven.

## Usage
To parse a JSON string:

```java
import com.example.compiler.JSONParser;

public class Main {
    public static void main(String[] args) {
        String json = """
        {
            "key": "value",
            "number": 123,
            "boolean": true,
            "array": [1, 2, 3],
            "object": {"nestedKey": "nestedValue"}
        }
        """;

        Object result = JSONParser.parse(json);
        System.out.println(result);
    }
}
```


## Limitations
- Does not handle floating-point numbers.
- Limited support for malformed JSON strings.
- No advanced error reporting for debugging invalid JSON.

## License
This project is licensed under the MIT License. See the LICENSE file for details.

