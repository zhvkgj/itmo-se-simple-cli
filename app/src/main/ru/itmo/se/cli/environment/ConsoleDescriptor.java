package ru.itmo.se.cli.environment;

/**
 * Класс дескриптора для взаимодействия с консолью ввода-вывода.
 *
 * @author Sergey Sokolvyak on 27.02.2021
 */
public class ConsoleDescriptor implements Descriptor {
    /**
     * {@inheritDoc}
     */
    @Override
    public String readFromSource() {
        return Console.getInstance().readFromBuffer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToDestination(String input) {
        Console.getInstance().writeToBuffer(input);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DescriptorType getType() {
        return DescriptorType.Console;
    }
}
