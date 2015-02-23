import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;


public class WIBUP2Test {
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private Trie t;

    @Before
    public void arrange() {
        t = new Trie();
        System.setOut(new PrintStream(out));
    }

    @After
    public void cleanUpOutStream() {
        System.setOut(null);
    }

    @Test
    public void canCreateEmptyNode() {
        Node n = new Node();
        assertEquals(n.outDegree, 0);
        assertFalse(n.terminal);
        assertEquals(n.node.length, 26);
        assertNull(n.node[0]); // first element
        assertNull(n.node[25]); // last element
    }

    @Test
    public void canCreateChildNode() {
        int outDegree = 2;
        int alphabetSize = 26;
        Children c = new Children(outDegree, true);

        assertEquals(c.outDegree, outDegree);
        assertTrue(c.terminal);
        assertEquals(c.node.length, alphabetSize);
    }

    @Test
    public void canInsertSimpleLetter() {
        assertTrue(t.insert("a"));
        assertEquals(1, t.membership());
        assertTrue(t.insert("z"));
        assertEquals(2, t.membership());

        assertTrue(t.isPresent("a"));
        assertTrue(t.isPresent("z"));

        assertTrue(t.delete("a"));
        assertEquals(1, t.membership());
        assertTrue(t.delete("z"));
        assertEquals(0, t.membership());

        assertFalse(t.isPresent("a"));
        assertFalse(t.isPresent("z"));
    }

    @Test
    public void canInsertAndRemoveTwoLetterWord() {
        assertTrue(t.insert("as"));
        t.listAll();
        assertEquals("as\n", out.toString());
        assertEquals(1, t.membership());

        assertTrue(t.isPresent("as"));

        assertFalse(t.head.node[0].terminal);
        assertTrue(t.head.node[0].node[getIndex('s')].terminal);

        assertTrue(t.delete("as"));
        assertFalse(t.isPresent("as"));
        assertEquals(0, t.membership());
    }

    @Test
    public void canInsertTwoWords() {
        assertTrue(t.insert("tom"));
        assertTrue(t.head.node[getIndex('t')].node[getIndex('o')].node[getIndex('m')].terminal);
        assertTrue(t.insert("cat"));
        t.listAll(); // prints alphabetically
        assertEquals("cat\ntom\n", out.toString());
    }

    @Test
    public void canInsertThreeWords() {
        assertTrue(t.insert("alice"));
        assertTrue(t.insert("bob"));
        assertTrue(t.insert("joe"));
        t.listAll(); // prints alphabetically
        assertEquals("alice\nbob\njoe\n", out.toString());
    }

    @Test
    public void canInsertThreeSimilarWords() {
        assertTrue(t.insert("vidi"));
        assertTrue(t.insert("veni"));
        assertTrue(t.insert("vici"));

        // check if present
        assertTrue(t.isPresent("vidi"));
        assertTrue(t.isPresent("veni"));
        assertTrue(t.isPresent("vici"));
        assertEquals(t.head.node[getIndex('v')].outDegree, 3);
        t.listAll(); // prints alphabetically
        assertEquals("veni\nvici\nvidi", out.toString());
    }

    private int getIndex(char c) {
        return c - 97;
    }
}
