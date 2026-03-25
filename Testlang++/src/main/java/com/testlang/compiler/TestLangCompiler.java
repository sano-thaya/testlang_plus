package com.testlang.compiler;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.testlang.compiler.generator.CodeGenerator;
import com.testlang.compiler.model.Program;
import com.testlang.compiler.parser.TestLangLexer;
import com.testlang.compiler.parser.parser;

public final class TestLangCompiler {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: TestLangCompiler <input.test> <outputDir>");
            System.exit(1);
        }

        Path input = Path.of(args[0]);
        Path outputDir = Path.of(args[1]);
        Path outputFile = outputDir.resolve("GeneratedTests.java");

        try (Reader reader = Files.newBufferedReader(input, StandardCharsets.UTF_8)) {
            TestLangLexer lexer = new TestLangLexer(reader);
            parser syntaxParser = new parser(lexer);
            Program program = (Program) syntaxParser.parse().value;

            CodeGenerator generator = new CodeGenerator();
            generator.generate(program, outputFile);
        } catch (RuntimeException ex) {
            System.err.println(ex.getMessage());
            System.exit(2);
        }
    }
}
