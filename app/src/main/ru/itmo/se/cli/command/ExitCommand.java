package ru.itmo.se.cli.command;

import ru.itmo.se.cli.command.execution.SignalExitException;
import ru.itmo.se.cli.environment.Context;

/**
 * Класс, представляющий команду shell-а exit.
 *
 * @author Sergey Sokolvyak on 27.02.2021
 */
public class ExitCommand extends Command {
    private final boolean shouldInterrupt;

    /**
     * Конструктор команды.
     *
     * @param shouldInterrupt флаг запуска
     */
    public ExitCommand(boolean shouldInterrupt) {
        this.shouldInterrupt = shouldInterrupt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int execute() {
        output.writeToDestination("");
        if (shouldInterrupt) {
            Context.getInstance().reset();
            throw new SignalExitException("Signal to exit the interpreter");
        }
        return 0;
    }
}
