package ru.words.finder.app;

import ru.words.finder.app.domain.TaskQueue;
import ru.words.finder.app.service.impl.ChunkTaskCollectorImpl;
import ru.words.finder.app.service.TaskCollector;
import ru.words.finder.app.service.impl.RegexpWordCounterImpl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static ru.words.finder.app.configuration.AppConfiguration.TOP_N;

/**
 * @author Ivan Kochkin
 * @since 26.08.2025
 */
public class WordFrequencyApp {

    public static void main(String[] args) throws Exception {
        TaskCollector taskCollector = new ChunkTaskCollectorImpl();
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите путь к папке: ");
        String directoryPath = scanner.nextLine();

        Path folder = Paths.get(directoryPath);
        if (!Files.isDirectory(folder)) {
            System.out.println("Указанный путь не является папкой.");
            return;
        }

        System.out.print("Введите минимальную длину слова: ");
        int minLength = scanner.nextInt();

        System.out.printf("Введите количество потоков(1 - %d): ", Runtime.getRuntime().availableProcessors());
        int numThreads = scanner.nextInt();
        if (numThreads < 1 || numThreads > Runtime.getRuntime().availableProcessors()) {
            System.out.printf("Недопустимое количество потоков. Будет использовано %d поток(-а/-ов).", Runtime.getRuntime().availableProcessors() / 2);
            numThreads = Runtime.getRuntime().availableProcessors() / 2;
        }
        System.out.println("Используется потоков: " + numThreads);
        TaskQueue taskQueue = new TaskQueue();
        RegexpWordCounterImpl wordCounter = new RegexpWordCounterImpl(minLength);
        long startTime = System.currentTimeMillis();

        List<WorkerThread> workers = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            WorkerThread worker = new WorkerThread(taskQueue, wordCounter);
            worker.start();
            workers.add(worker);
        }

        taskCollector.collectTasks(folder, taskQueue);

        for (WorkerThread worker : workers) {
            worker.join();
        }

        Map<String, Integer> globalResult = new HashMap<>();
        for (WorkerThread worker : workers) {
            for (var entry : worker.getLocalResult().entrySet()) {
                globalResult.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }

        globalResult.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(TOP_N)
                .forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));
        long endTime = System.currentTimeMillis();
        System.out.println("\nВремя выполнения: " + (endTime - startTime) + " мс");
    }
}
