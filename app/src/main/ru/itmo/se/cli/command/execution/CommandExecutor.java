package ru.itmo.se.cli.command.execution;

import ru.itmo.se.cli.command.Command;

import java.util.List;

/**
 * Интерфейс сущности, ответственной за инициирование выполнения pipeline-а команд.
 *
 * @author Sergey Sokolvyak
 */
public interface CommandExecutor {
    /**
     * Иницирует выполнение pipeline-а.
     *
     * @param pipeline pipeline команд
     * @return код возврата
     */
    int execute(List<Command> pipeline);
}
