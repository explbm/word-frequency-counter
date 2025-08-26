package ru.words.finder.app.service;

import ru.words.finder.app.domain.TaskQueue;

import java.nio.file.Path;

/**
 * @author Ivan Kochkin
 * @since 26.08.2025
 */
public interface TaskCollector {

    /**
     * Метод собирает задачи для обработки файлов в очередь
     * @param folder директория с текстовыми файлами
     * @param taskQueue очередь задач
     */
    void collectTasks(Path folder, TaskQueue taskQueue);
}
