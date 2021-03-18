package ru.itmo.se.cli.command.builder;

/**
 * Класс ошибки построения pipeline-а команд.
 *
 * @author Sergey Sokolvyak on 27.02.2021
 */
public class PipelineBuildingException extends RuntimeException {
    /**
     * Конструктор класса ошибки.
     *
     * @param message описание ошибки
     */
    public PipelineBuildingException(String message) {
        super(message);
    }

    /**
     * Конструктор класса ошибки.
     *
     * @param message описание ошибки
     * @param cause   причина возникновения
     */
    public PipelineBuildingException(String message, Throwable cause) {
        super(message, cause);
    }
}
