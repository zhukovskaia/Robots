package utils.i18n;

import java.text.MessageFormat;

public class FormattingBenchmark {
    private static final String PATTERN_MF = "Позиция: ({0,number,#.##}, {1,number,#.##}), Направление: {2,number,#.##}°";
    private static final String PATTERN_FMT = "Позиция: (%.2f, %.2f), Направление: %.2f°";
    private static final Object[] ARGS = {123.456, 789.012, 45.67};
    private static final int ITERATIONS = 100_000;

    public static void main(String[] args) {
        long t1 = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            String.format(PATTERN_FMT, ARGS);
        }
        long formatterTime = System.nanoTime() - t1;

        long t2 = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            MessageFormat.format(PATTERN_MF, ARGS);
        }
        long mfUncachedTime = System.nanoTime() - t2;

        long t3 = System.nanoTime();
        MessageFormat cachedMf = new MessageFormat(PATTERN_MF);
        for (int i = 0; i < ITERATIONS; i++) {
            cachedMf.format(ARGS);
        }
        long mfCachedTime = System.nanoTime() - t3;

        System.out.println("=== Сравнительный анализ форматирования (" + ITERATIONS + " итераций) ===");
        System.out.printf("1. Formatter (String.format)       : %d ms\n", formatterTime / 1_000_000);
        System.out.printf("2. MessageFormat (без кэша)        : %d ms\n", mfUncachedTime / 1_000_000);
        System.out.printf("3. MessageFormat (с кэшем парсера) : %d ms\n", mfCachedTime / 1_000_000);
        System.out.println("\nВывод: Кэширование объекта MessageFormat ускоряет форматирование в ~" +
                (mfUncachedTime / Math.max(mfCachedTime, 1)) + " раз, исключая повторный парсинг шаблона.");
    }
}