package ru.itmo.se.cli.environment;

/**
 * Интерфейс дескриптора ввода-вывода.
 *
 * @author Sergey Sokolvyak
 */
public interface Descriptor {
    /**
     * Перечисление, содержащее допустимые типы дескриптора.
     */
    enum DescriptorType {
        File, Console
    }

    /**
     * Читает данные из источника.
     *
     * @return строковое представление данных из источника
     */
    String readFromSource();

    /**
     * Записывает данные в источник, стирая прошлое содержимое.
     *
     * @param input строковое представление данных, которые будут записаны
     */
    void writeToDestination(String input);

    /**
     * Возвращает тип дескриптора.
     *
     * @return тип дескриптора
     * @see DescriptorType
     */
    DescriptorType getType();
}
