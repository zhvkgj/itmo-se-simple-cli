package ru.itmo.se.cli.parser;

/**
 * Класс токена синтаксического анализа.
 *
 * @author Sergey Sokolvyak on 25.02.2021
 */
public class Token {
    /**
     * Перечисление, содержащее допустимые типы токенов.
     */
    public enum Type {
        Command("command"),
        Arg("argument"),
        VarDecl("variable declaration"),
        Pipe("pipeline");

        private final String description;

        Type(String description) {
            this.description = description;
        }

        /**
         * Возвращает строковое описание типа токена.
         *
         * @return строковое описание типа токена
         */
        public String getDescription() {
            return description;
        }
    }

    private final String content;
    private final Type type;

    /**
     * Конструктор токена.
     *
     * @param content строковое представление токена
     * @param type    тип токена
     */
    public Token(String content, Type type) {
        this.content = content;
        this.type = type;
    }

    /**
     * Возвращает строковое представление токена.
     *
     * @return строковое представление токена
     */
    public String getContent() {
        return content;
    }

    /**
     * Возвращает тип токена.
     *
     * @return тип токена
     * @see Type
     */
    public Type getType() {
        return type;
    }
}
