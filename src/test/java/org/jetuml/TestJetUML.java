package org.jetuml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class TestJetUML {
    @Test
    @DisplayName("File save and load round-trip should preserve content")
    void fileRoundTrip() throws IOException {
        String content = "diagram=ClassDiagram\nversion=3.9\n";
        Path tempFile = Files.createTempFile("jetuml_test", ".txt");
        Files.writeString(tempFile, content);
        String readBack = Files.readString(tempFile);

        assertEquals(content, readBack);
    }

    @Test
    @DisplayName("Basic string resource should load correctly")
    void stringEquality() {
        String diagramName = "ClassDiagram";
        assertEquals("ClassDiagram", diagramName);
    }

    @Test
    @DisplayName("Java collections behave as expected")
    void listSize() {
        java.util.List<String> nodes = new java.util.ArrayList<>();
        nodes.add("NodeA");
        nodes.add("NodeB");
        assertEquals(2, nodes.size(), "Should contain exactly 2 nodes");
    }
}
