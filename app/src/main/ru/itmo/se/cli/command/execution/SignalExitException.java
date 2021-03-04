package ru.itmo.se.cli.command.execution;

/**
 * Сигнал к завершению работы интерпретатора.
 *
 * @author Sergey Sokolvyak on 03.03.2021
 */
public class SignalExitException extends RuntimeException {
    /**
     * Конструктор класса ошибки.
     *
     * @param message описание ошибки
     */
    public SignalExitException(String message) {
        super(message);
    }
}
