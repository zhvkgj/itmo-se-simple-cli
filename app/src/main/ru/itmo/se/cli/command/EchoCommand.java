package ru.itmo.se.cli.command;

import java.util.List;

/**
 * Класс представляющий команду shell-a echo.
 *
 * @author Sergey Sokolvyak
 */
public final class EchoCommand extends Command {
    private final List<String> arguments;

    /**
     * Конструктор команды.
     *
     * @param arguments аргументы команды
     */
    public EchoCommand(List<String> arguments) {
        this.arguments = arguments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int execute() {
        output.writeToDestination(String.join(" ", arguments));
        return 0;
    }
}
