package ru.itmo.se.cli.command;

import ru.itmo.se.cli.environment.ConsoleDescriptor;
import ru.itmo.se.cli.environment.Descriptor;


/**
 * Абстрактный класс, представляющий команду shell-a.
 *
 * @author Sergey Sokolvyak
 */
public abstract class Command {
    protected Descriptor input;
    protected Descriptor output;

    protected Command() {
        this.input = new ConsoleDescriptor();
        this.output = new ConsoleDescriptor();
    }

    /**
     * Иницирует выполнение команды.
     *
     * @return код возрата
     */
    public abstract int execute();
}
