package ru.itmo.se.cli.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Класс парсера входной строки.
 *
 * @author Sergey Sokolvyak
 */
public class SimpleCommandLineParser implements CommandLineParser {
    private static final String VAR_DECL_PATTERN = "^[a-zA-Z_]\\w*=.*$";

    private String input;
    private int beginIndex;
    private int endIndex;
    private Token.Type previousType;
    private final ExpansionProvider expansionProvider;

    /**
     * Конструктор парсера.
     *
     * @param expansionProvider выполняет подстановку переменных
     */
    public SimpleCommandLineParser(ExpansionProvider expansionProvider) {
        this.expansionProvider = expansionProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Token> parse(String input) {
        this.input = input.trim();
        List<Token> tokens = new ArrayList<>();
        previousType = Token.Type.Pipe;
        Token currentToken;
        while (hasNextToken()) {
            currentToken = getToken();
            if (previousType == Token.Type.VarDecl && currentToken.getType() == Token.Type.Command)
                tokens.add(new Token("|", Token.Type.Pipe));
            tokens.add(currentToken);
            previousType = currentToken.getType();
        }
        reset();
        return tokens;
    }

    private Token getToken() throws ParsingException {
        skipSpaces();

        if (isPipeChar()) {
            return new Token("|", Token.Type.Pipe);
        }

        var sbWithTokenContent = new StringBuilder();
        Optional<String> currentParsed;
        while (beginIndex < input.length() && input.charAt(beginIndex) != ' ' && input.charAt(beginIndex) != '|') {
            currentParsed = tryTokenizeNotQuoted();
            if (currentParsed.isPresent()) {
                sbWithTokenContent.append(expansionProvider.expandString(currentParsed.get()));
                continue;
            }

            currentParsed = tryTokenizeFullQuoted();
            if (currentParsed.isPresent()) {
                sbWithTokenContent.append(expansionProvider.expandString(currentParsed.get()));
                continue;
            }

            currentParsed = tryTokenizeWeakQuoted();
            currentParsed.ifPresent(sbWithTokenContent::append);
        }

        String tokenContent = sbWithTokenContent.toString();
        Token.Type type = Token.Type.Arg;
        if (previousType == Token.Type.Pipe || previousType == Token.Type.VarDecl) {
            if (tokenContent.matches(VAR_DECL_PATTERN)) {
                type = Token.Type.VarDecl;
            } else {
                type = Token.Type.Command;
            }
        }

        return new Token(tokenContent, type);
    }

    private void skipSpaces() {
        while (input.charAt(beginIndex) == ' ') {
            beginIndex++;
            endIndex++;
        }
    }

    private boolean isPipeChar() {
        if (input.charAt(beginIndex) == '|') {
            beginIndex++;
            endIndex++;
            return true;
        }
        return false;
    }

    private Optional<String> tryTokenizeFullQuoted() {
        if (input.charAt(beginIndex) != '\"') {
            return Optional.empty();
        }

        boolean foundClosingQuote = false;
        while (++endIndex < input.length()) {
            if (input.charAt(endIndex) == '\"') {
                foundClosingQuote = true;
                endIndex++;
                break;
            }
        }

        if (!foundClosingQuote) {
            reset();
            throw new ParsingException("Syntax error: unexpected token \"");
        }

        Optional<String> result = Optional.of(input.substring(beginIndex + 1, endIndex - 1));
        beginIndex = endIndex;
        return result;
    }

    private Optional<String> tryTokenizeWeakQuoted() {
        if (input.charAt(beginIndex) != '\'') {
            return Optional.empty();
        }

        boolean foundClosingQuote = false;
        while (++endIndex < input.length()) {
            if (input.charAt(endIndex) == '\'') {
                foundClosingQuote = true;
                endIndex++;
                break;
            }
        }

        if (!foundClosingQuote) {
            reset();
            throw new ParsingException("Syntax error: unexpected token '");
        }

        Optional<String> result = Optional.of(input.substring(beginIndex + 1, endIndex - 1));
        beginIndex = endIndex;
        return result;
    }

    private Optional<String> tryTokenizeNotQuoted() {
        if (input.charAt(beginIndex) == '\'' || input.charAt(beginIndex) == '\"') {
            return Optional.empty();
        }

        char current;
        while (endIndex < input.length()) {
            current = input.charAt(endIndex);
            if (current == ' ' || current == '\"' || current == '\'' || current == '|')
                break;
            if (isForbiddenSymbol(current)) {
                reset();
                throw new ParsingException(String.format("Syntax error: unexpected token %c", current));
            }
            endIndex++;
        }

        Optional<String> result = Optional.of(input.substring(beginIndex, endIndex));
        beginIndex = endIndex;
        return result;
    }

    private boolean isForbiddenSymbol(char c) {
        return c == '(' || c == ')' || c == '<' || c == '>' || c == '\\';
    }

    private boolean hasNextToken() {
        return beginIndex < input.length();
    }

    private void reset() {
        input = null;
        beginIndex = 0;
        endIndex = 0;
        previousType = Token.Type.Pipe;
    }
}
