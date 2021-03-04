package ru.itmo.se.cli.command.execution;

import ru.itmo.se.cli.command.Command;
import ru.itmo.se.cli.environment.Console;

import java.util.List;

/**
 * Класс, ответственной за инициирование выполнения pipeline-а команд.
 *
 * @author Sergey Sokolvyak on 28.02.2021
 */
public class SimpleCommandExecutor implements CommandExecutor {

    /**
     * {@inheritDoc}
     */
    @Override
    public int execute(List<Command> pipeline) {
        for (Command command : pipeline) {
            command.execute();
        }
        Console.getInstance().flush();
        return 0;
    }
}
