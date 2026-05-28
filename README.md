# Testlang++ Compiler

A small DSL compiler that turns Testlang++ specifications into JUnit tests. The build uses JFlex and CUP to generate the lexer and parser, then runs a code generator that produces `GeneratedTests.java` from a `.test` file.

## Features

- JFlex lexer + CUP parser generation
- Testlang++ DSL for HTTP test specs
- Java code generator that emits JUnit tests
- Maven build with generated sources and tests wired in

## Requirements

- Java 11+
- Maven 3.8+

## Project layout

- `src/main/jflex/TestLangLexer.flex` - Lexer definition
- `src/main/cup/TestLangParser.cup` - Grammar definition
- `src/main/java/com/testlang/compiler/TestLangCompiler.java` - Compiler entry point
- `src/main/resources/examples/sample.test` - Example DSL input
- `target/generated-sources/*` - Generated lexer/parser (build output)
- `target/generated-test-sources/testlang/GeneratedTests.java` - Generated tests (build output)

## Quick start

Generate sources, compile, and run tests:

```bash
mvn clean test
```

This will:

1. Generate the lexer and parser.
2. Run the compiler on the sample DSL.
3. Compile the generated test source.
4. Execute JUnit tests.

## Generate tests from a DSL file

You can run the compiler directly with Maven:

```bash
mvn -q exec:java \
  -Dexec.mainClass=com.testlang.compiler.TestLangCompiler \
  -Dexec.args="src/main/resources/examples/sample.test target/generated-test-sources/testlang"
```

The generated file will be:

```
target/generated-test-sources/testlang/GeneratedTests.java
```

## DSL example

```testlang
config {
  base_url = "https://httpbin.org";
  header "Content-Type" = "application/json";
}

let id = 42;

test login {
  POST "/anything/api/login" {
    body = "{\"username\":\"san\",\"password\":\"1234\"}";
  };
  expect status = 404;
  expect body contains "/anything/api/login";
}

test get_user {
  GET "/anything/api/users/$id";
  expect status = 200;
  expect header "Content-Type" contains "application/json";
}

test update_user {
  PUT "/anything/api/users/$id" {
    body = "{\"name\":\"Updated User\"}";
  };
  expect status = 200;
  expect body contains "Updated";
}
```

## Notes

- Generated sources are placed under `target/` and should not be committed.
- If you change the lexer or grammar, re-run `mvn clean test` to regenerate and compile.
