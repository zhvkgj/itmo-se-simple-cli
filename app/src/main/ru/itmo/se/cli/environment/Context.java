package ru.itmo.se.cli.environment;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс контекста для хранения переменных, созданных пользователем.
 * После окончания текущей сессии контекст очищается.
 *
 * @author Sergey Sokolvyak
 */
public final class Context {
    private final Map<String, String> context;

    private static class ContextHolder {
        public static final Context HOLDER_INSTANCE = new Context();
    }

    private Context() {
        context = new HashMap<>();
    }

    /**
     * Возвращает единственный экземляр контекста.
     *
     * @return единственный экземпляр контекста
     * @see Context
     */
    public static Context getInstance() {
        return ContextHolder.HOLDER_INSTANCE;
    }

    /**
     * Возвращает значение переменной по имени. Если переменная не была найдена,
     * то метод возвращает пустую строку в качестве значения.
     *
     * @param name имя переменной
     * @return значение переменной
     */
    public String getVariable(String name) {
        return context.getOrDefault(name, "");
    }

    /**
     * Добавляет переменную с именем и значением в контекст.
     *
     * @param name  имя переменной
     * @param value значения переменной
     */
    public void setOrAddVariable(String name, String value) {
        context.put(name, value);
    }

    /**
     * Очищает контекст.
     */
    public void reset() {
        context.clear();
    }
}
