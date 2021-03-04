package ru.itmo.se.cli.command;

import ru.itmo.se.cli.environment.Context;

import java.util.AbstractMap;
import java.util.List;

/**
 * Класс, представляющий последовательность объявлений переменных или присваиваний переменным.
 *
 * @author Sergey Sokolvyak on 01.03.2021
 */
public class VariablesProcessingCommand extends Command {
    private final List<AbstractMap.SimpleImmutableEntry<String, String>> sequenceVarDecl;

    /**
     * Конструктор класса.
     *
     * @param varNamesAndValues список имен и значений переменных
     */
    public VariablesProcessingCommand(List<AbstractMap.SimpleImmutableEntry<String, String>> varNamesAndValues) {
        this.sequenceVarDecl = varNamesAndValues;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int execute() {
        var context = Context.getInstance();
        for (AbstractMap.SimpleImmutableEntry<String, String> varDecl : sequenceVarDecl) {
            context.setOrAddVariable(varDecl.getKey(), varDecl.getValue());
        }
        output.writeToDestination("");
        return 0;
    }
}
