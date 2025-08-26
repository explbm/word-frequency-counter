package ru.words.finder.app.domain;

import java.util.LinkedList;
import java.util.Queue;

public class TaskQueue {
    private final Queue<FileChunkTask> tasks = new LinkedList<>();
    private boolean closed = false;

    public synchronized void addTask(FileChunkTask task) {
        tasks.add(task);
        notifyAll();
    }

    public synchronized FileChunkTask getTask() throws InterruptedException {
        while (tasks.isEmpty() && !closed) {
            wait();
        }
        if (tasks.isEmpty()) return null;
        return tasks.poll();
    }

    public synchronized void close() {
        closed = true;
        notifyAll();
    }
}
