import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class InputTest {
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private WIBUP2 program;

    @Before
    public void arrange() {
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

    private WIBUP2 getProgram(String path) throws IOException {
        File input = new File(path);
        InputStream in = new FileInputStream(input);
        return new WIBUP2(in);
    }

    private String getOutputString(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, StandardCharsets.UTF_8);
    }
}
