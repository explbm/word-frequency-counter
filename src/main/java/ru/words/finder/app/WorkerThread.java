package ru.words.finder.app;

import ru.words.finder.app.domain.FileChunkTask;
import ru.words.finder.app.domain.TaskQueue;
import ru.words.finder.app.service.impl.RegexpWordCounterImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ivan Kochkin
 * @since 26.08.2025
 */
public class WorkerThread extends Thread {
    private final TaskQueue taskQueue;
    private final RegexpWordCounterImpl wordCounter;
    private final Map<String, Integer> localCount = new HashMap<>();

    public WorkerThread(TaskQueue taskQueue, RegexpWordCounterImpl wordCounter) {
        this.taskQueue = taskQueue;
        this.wordCounter = wordCounter;
    }

    @Override
    public void run() {
        try {
            FileChunkTask task;
            while ((task = taskQueue.getTask()) != null) {
                Map<String, Integer> partial;
                if (task.wholeFile()) {
                    partial = wordCounter.countInFile(task.file());
                } else {
                    partial = wordCounter.countInFileChunk(
                        task.file(),
                        task.startPosition(),
                        task.endPosition()
                    );
                }
                mergeLocal(partial);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("Ошибка в потоке: " + e.getMessage());
        }
    }

    private void mergeLocal(Map<String, Integer> partial) {
        for (var entry : partial.entrySet()) {
            localCount.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
    }

    public Map<String, Integer> getLocalResult() {
        return localCount;
    }
}
