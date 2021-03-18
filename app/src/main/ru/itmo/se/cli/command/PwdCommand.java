package ru.itmo.se.cli.command;

/**
 * Класс, представляющий команду shell-a pwd.
 *
 * @author Sergey Sokolvyak
 */
public class PwdCommand extends Command {
    /**
     * {@inheritDoc}
     */
    @Override
    public int execute() {
        String currentDirectory = System.getProperty("user.dir");
        output.writeToDestination(currentDirectory);
        return 0;
    }
}
