package ru.itmo.se.cli.parser;

import ru.itmo.se.cli.environment.Context;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Класс, отвечающий за подстановку переменных.
 *
 * @author Sergey Sokolvyak on 26.02.2021
 */
public class ExpansionProvider {
    private static final String SUBSTITUTION_PATTERN = "(\\$[a-zA-Z_]\\w*)|(\\$\\{[a-zA-Z_]\\w*})";

    /**
     * Выполняет подстановку переменных в переданной строке.
     *
     * @param content строка, в которой необходимо осуществить подстановку переменной
     * @return строку с подставленными переменными
     */
    public String expandString(String content) {
        var pattern = Pattern.compile(SUBSTITUTION_PATTERN);
        var matcher = pattern.matcher(content);
        var sbWithExtendedContent = new StringBuilder();
        String currentSubstitution;
        while (matcher.find()) {
            currentSubstitution = Objects.nonNull(matcher.group(1))
                ? expandIdentifier(matcher.group(1), false)
                : expandIdentifier(matcher.group(2), true);
            matcher.appendReplacement(sbWithExtendedContent, currentSubstitution);
        }
        matcher.appendTail(sbWithExtendedContent);

        String extendedContent = sbWithExtendedContent.toString();
        if (extendedContent.contains("${"))
            throw new ParsingException("Syntax error: bad substitution");

        return extendedContent;
    }

    private String expandIdentifier(String substitution, boolean isBraceSubstitution) {
        var identifier = isBraceSubstitution
            ? substitution.substring(2, substitution.length() - 1)
            : substitution.substring(1);

        if (identifier.isBlank())
            throw new ParsingException("Syntax error: bad substitution");

        return Context.getInstance().getVariable(identifier);
    }
}
