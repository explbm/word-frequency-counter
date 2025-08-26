package ru.words.finder.app.service.impl;

import ru.words.finder.app.domain.FileChunkTask;
import ru.words.finder.app.domain.TaskQueue;
import ru.words.finder.app.service.TaskCollector;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static ru.words.finder.app.configuration.AppConfiguration.BIG_FILE_THRESHOLD;
import static ru.words.finder.app.configuration.AppConfiguration.CHUNK_SIZE;

/**
 * Реализация TaskCollector, которая собирает задачи для обработки файлов в очередь, разбивая большие файлы на куски
 * @author Ivan Kochkin
 * @since 26.08.2025
 */
public class ChunkTaskCollectorImpl implements TaskCollector {

    @Override
    public void collectTasks(Path folder, TaskQueue taskQueue) {
        try (var files = Files.list(folder)) {
            files.filter(path -> path.toString().endsWith(".txt"))
                    .forEach(path -> {
                        try {
                            long size = Files.size(path);
                            if (size < BIG_FILE_THRESHOLD) {
                                taskQueue.addTask(new FileChunkTask(path, 0, size, true));
                            } else {
                                try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
                                    long chunkStartIndex = 0;
                                    while (chunkStartIndex < size) {
                                        long chunkEndIndex = Math.min(chunkStartIndex + CHUNK_SIZE, size);

                                        if (chunkEndIndex < size) {
                                            ByteBuffer buffer = ByteBuffer.allocate(128);
                                            channel.position(chunkEndIndex);
                                            int bytesRead = channel.read(buffer);

                                            if (bytesRead > 0) {
                                                buffer.flip();
                                                String tail = new String(buffer.array(), 0, bytesRead);
                                                int wordBoundary = findWordBoundary(tail);

                                                if (wordBoundary > 0) {
                                                    chunkEndIndex += wordBoundary;
                                                }
                                            }
                                        }

                                        taskQueue.addTask(new FileChunkTask(path, chunkStartIndex, chunkEndIndex, false));
                                        chunkStartIndex = chunkEndIndex;
                                    }
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        taskQueue.close();
    }

    private int findWordBoundary(String text) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isWhitespace(c) || i == text.length() - 1) {
                return i + 1;
            }
        }
        return 0;
    }
}
