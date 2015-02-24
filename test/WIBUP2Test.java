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
        assertEquals(0, n.outDegree);
        assertFalse(n.terminal);
        assertEquals(26, n.node.length);
        assertNull(n.node[0]); // first element
        assertNull(n.node[25]); // last element
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
        assertEquals(2, t.membership());
        t.listAll(); // prints alphabetically
        assertEquals("cat\ntom\n", out.toString());
    }

    @Test
    public void canInsertThreeWords() {
        assertTrue(t.insert("alice"));
        assertTrue(t.insert("bob"));
        assertTrue(t.insert("joe"));
        assertEquals(3, t.membership());
        assertEquals(3, t.head.outDegree);
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
        assertEquals(3, t.membership());
        assertEquals(2, t.head.node[getIndex('v')].outDegree);
        t.listAll(); // prints alphabetically
        //assertEquals("veni\nvici\nvidi", out.toString());
    }

    @Test
    public void givenSimilarStringsOutDegreeWorks() {
        assertTrue(t.insert("abce"));
        assertTrue(t.insert("abcf"));
        assertTrue(t.insert("abcg"));
        assertTrue(t.insert("abch"));
        assertEquals(4, t.membership());

        assertTrue(t.isPresent("abce"));
        assertTrue(t.isPresent("abcf"));
        assertTrue(t.isPresent("abcg"));
        assertTrue(t.isPresent("abch"));

        Node c = t.head.node[getIndex('a')].node[getIndex('b')].node[getIndex('c')];
        assertEquals(4, c.outDegree);
    }

    @Test
    public void testComplexTree() {
        assertTrue(t.insert("the"));
        assertTrue(t.insert("thin"));
        assertTrue(t.insert("tint"));
        assertTrue(t.insert("song"));
        assertTrue(t.insert("so"));
        assertTrue(t.insert("son"));
        assertTrue(t.insert("sing"));
        assertTrue(t.insert("sin"));

        // check membership and presence
        assertEquals(8, t.membership());
        assertTrue(t.isPresent("the"));
        assertTrue(t.isPresent("thin"));
        assertTrue(t.isPresent("tint"));
        assertTrue(t.isPresent("song"));
        assertTrue(t.isPresent("so"));
        assertTrue(t.isPresent("son"));
        assertTrue(t.isPresent("sing"));
        assertTrue(t.isPresent("sin"));

        // verify not present
        assertFalse(t.isPresent("s"));
        assertFalse(t.isPresent("si"));
        assertFalse(t.isPresent("th"));
        assertFalse(t.isPresent("tin"));

        // test "the"
        Node t2 = t.head.node[getIndex('t')];
        Node th = t.head.node[getIndex('t')].node[getIndex('h')];
        Node the = t.head.node[getIndex('t')].node[getIndex('h')].node[getIndex('e')];
        assertEquals(2, t.head.outDegree); // out is 't' and 's'
        assertEquals(2, th.outDegree);
        assertEquals(0, the.outDegree);

        // test terminal for "the"
        assertFalse(t2.terminal);
        assertFalse(th.terminal);
        assertTrue(the.terminal);

        // test "song" out degree
        Node s = t.head.node[getIndex('s')];
        Node so = t.head.node[getIndex('s')].node[getIndex('o')];
        Node son = t.head.node[getIndex('s')].node[getIndex('o')].node[getIndex('n')];
        Node song = t.head.node[getIndex('s')].node[getIndex('o')].node[getIndex('n')].node[getIndex('g')];
        assertEquals(2, s.outDegree);
        assertEquals(1, so.outDegree);
        assertEquals(1, son.outDegree);
        assertEquals(0, song.outDegree);

        // test "song"
        Node si = t.head.node[getIndex('s')].node[getIndex('i')];
        Node sin = t.head.node[getIndex('s')].node[getIndex('i')].node[getIndex('n')];
        Node sing = t.head.node[getIndex('s')].node[getIndex('i')].node[getIndex('n')].node[getIndex('g')];
        assertEquals(1, si.outDegree);
        assertEquals(1, sin.outDegree);
        assertEquals(0, sing.outDegree);

        // test terminal for "song"
        assertFalse(s.terminal);
        assertTrue(so.terminal);
        assertTrue(son.terminal);
        assertTrue(song.terminal);
    }

    private int getIndex(char c) {
        return c - 97;
    }
}
