package ru.words.finder.app.service.impl;

import ru.words.finder.app.service.WordCounter;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ivan Kochkin
 * @since 26.08.2025
 */
public class RegexpWordCounterImpl implements WordCounter {
    private final Pattern pattern;

    public RegexpWordCounterImpl(int minLength) {
        this.pattern = Pattern.compile("\\b[\\p{L}\\p{N}]{" + minLength + ",}\\b", Pattern.UNICODE_CHARACTER_CLASS);
    }

    @Override
    public Map<String, Integer> countInFile(Path file) throws IOException {
        Map<String, Integer> localCount = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line.toLowerCase());
                while (matcher.find()) {
                    localCount.merge(matcher.group().toLowerCase(), 1, Integer::sum);
                }
            }
        }
        return localCount;
    }

    @Override
    public Map<String, Integer> countInFileChunk(Path file,
                                                 long start,
                                                 long end) throws IOException {
        Map<String, Integer> result = new HashMap<>();

        try (FileChannel channel = FileChannel.open(file, StandardOpenOption.READ)) {
            long size = end - start;
            ByteBuffer buffer = ByteBuffer.allocate((int) size);
            channel.position(start);
            channel.read(buffer);
            buffer.flip();

            String chunkContent = new String(buffer.array(), 0, (int) size);

            Matcher matcher = pattern.matcher(chunkContent);
            while (matcher.find()) {
                result.merge(matcher.group().toLowerCase(), 1, Integer::sum);
            }
        }

        return result;
    }
}
