package ru.words.finder.app.domain;

import java.nio.file.Path;

public record FileChunkTask(Path file, long startPosition, long endPosition, boolean wholeFile) {
}
