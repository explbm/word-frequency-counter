package ru.words.finder.app.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/**
 * @author Ivan Kochkin
 * @since 26.08.2025
 */
public interface WordCounter {

    /**
     * Подсчет слов в целом файле
     * @param file путь до файла
     * @return результат по файлу
     */
    Map<String, Integer> countInFile(Path file) throws IOException;

    /**
     * Подсчет слов в куске файла
     * @param file путь до файла
     * @param start начальная позиция куска
     * @param end конечная позиция куска
     * @return результат по куску файла
     */
    Map<String, Integer> countInFileChunk(Path file,
                                             long start,
                                             long end) throws IOException;
}
