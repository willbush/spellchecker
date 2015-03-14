import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class InputTest {
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private Main program;
    private Trie trie;

    @Before
    public void arrange() {
        trie = new Trie();
        System.setOut(new PrintStream(out));
    }

    @After
    public void cleanUpOutStream() {
        System.setOut(null);
    }

    @Test
    public void canProcessInput1() throws IOException {
        program = getProgram("test/testData/in1.txt");
        program.run();

        String expectedOut = getOutputString("test/testData/out1.txt");
        assertEquals(expectedOut, out.toString());
    }

    @Test
    public void canProcessInput2() throws IOException {
        program = getProgram("test/testData/in2.txt");
        program.run();

        String expectedOut = getOutputString("test/testData/out2.txt");
        assertEquals(expectedOut, out.toString());
    }

    @Test
    public void canProcessInput3() throws IOException {
        program = getProgram("test/testData/in3.txt");
        program.run();

        String expectedOut = getOutputString("test/testData/out3.txt");
        assertEquals(expectedOut, out.toString());
    }

    @Test
    public void canProcessInput4() throws IOException {
        program = getProgram("test/testData/in4.txt");
        program.run();

        String expectedOut = getOutputString("test/testData/out4.txt");
        assertEquals(expectedOut, out.toString());
    }

    private Main getProgram(String path) throws IOException {
        File input = new File(path);
        InputStream in = new FileInputStream(input);
        return new Main(in);
    }

    private String getOutputString(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, StandardCharsets.UTF_8);
    }

    @Test
    public void memoryStress() throws IOException {
        File wordList = new File("test/testData/wordList.txt");

        RandomAccessFile r = new RandomAccessFile(wordList, "r");
        // insert 349900 words
        String line;
        while ((line = r.readLine()) != null) {
            assertTrue(trie.insert(line));
            assertFalse(trie.insert(line));
            assertTrue(trie.isPresent(line));
        }
        assertEquals(349900, trie.getWordCount());

        // delete 349900 words
        r.seek(0);
        while ((line = r.readLine()) != null) {
            assertTrue(trie.delete(line));
            assertFalse(trie.delete(line));
            assertFalse(trie.isPresent(line));
        }
        assertEquals(0, trie.getWordCount());
        assertEquals(0, trie.head.outDegree);
        assertFalse(trie.head.isTerminal);

        for (Node n : trie.head.node)
            assertNull(n);
    }
}
