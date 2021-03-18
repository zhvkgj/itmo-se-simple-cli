package ru.itmo.se.cli.environment;

/**
 * Класс, представляющий консоль.
 *
 * @author Sergey Sokolvyak
 */
public final class Console {
    private final StringBuilder buffer = new StringBuilder();

    private static class ConsoleHolder {
        public static final Console HOLDER_INSTANCE = new Console();
    }

    private Console() {
    }

    /**
     * Возвращает единственный экземляр консоли.
     *
     * @return единственный экземляр консоли
     */
    public static Console getInstance() {
        return Console.ConsoleHolder.HOLDER_INSTANCE;
    }

    /**
     * Записывает данные в буфер консоли, стирая прошлое содержимое.
     *
     * @param input данные для записи
     */
    public void writeToBuffer(String input) {
        buffer.setLength(0);
        buffer.append(input);
    }

    /**
     * Возвращает буфер консоли.
     *
     * @return буфер консоли
     */
    public String readFromBuffer() {
        return buffer.toString();
    }

    /**
     * Печатает содержимое буфера консоли в консоли ввода-вывода.
     */
    public void flush() {
        String content = buffer.toString();
        if (!content.contentEquals(""))
            System.out.println(content);
        buffer.setLength(0);
    }
}
