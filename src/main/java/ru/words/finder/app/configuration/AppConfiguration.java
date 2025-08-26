package ru.words.finder.app.configuration;

/**
 * @author Ivan Kochkin
 * @since 26.08.2025
 */
public class AppConfiguration {
    // Топ слов
    public static final int TOP_N = 10;
    // Размер после которого файл считается большим
    public static final long BIG_FILE_THRESHOLD = 50 * 1024 * 1024; // 50 MB
    // Размер куска файла
    public static final long CHUNK_SIZE = 16 * 1024 * 1024; // 16 MB

}
