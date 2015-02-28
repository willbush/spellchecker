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
        String[] words = {"tom", "cat"};
        insertAndAssert(words, true);
        Node m = getLastNodeOf("tom");
        assertTrue(m.terminal);
        assertEquals(2, t.membership());
        t.listAll(); // prints alphabetically
        assertEquals("cat\ntom\n", out.toString());
    }

    @Test
    public void canInsertThreeWords() {
        String[] words = {"alice", "bob", "joe"};

        insertAndAssert(words, true);
        insertAndAssert(words, false); // inserting the same words should fail
        assertIsPresent(words, true);
        assertEquals(3, t.membership());
        assertEquals(3, t.head.outDegree);

        t.listAll(); // prints alphabetically
        assertEquals("alice\nbob\njoe\n", out.toString());
    }

    @Test
    public void canInsertThreeSimilarWords() {
        String[] words = {"vidi", "veni", "vici"};

        insertAndAssert(words, true);
        assertIsPresent(words, true);

        int[] outDegree = {2, 2, 1, 0};
        boolean[] terminals = {false, false, false, true};
        testOutDegreeAndTerminal("vidi", outDegree, terminals);
        assertEquals(3, t.membership());

        t.listAll(); // prints alphabetically
        //assertEquals("veni\nvici\nvidi", out.toString()); // listAll not fully implemented yet
    }

    @Test
    public void givenSimilarStringsOutDegreeWorks() {
        String[] words = {"abce", "abcf", "abcg", "abch"};
        insertAndAssert(words, true);
        insertAndAssert(words, false);
        assertIsPresent(words, true);
        assertEquals(4, t.membership());


        Node c = getLastNodeOf("abc");
        assertEquals(4, c.outDegree);
    }

    @Test
    public void testComplexTree() {
        String[] words = {"the", "thin", "tint", "song", "so", "son", "sing", "sin"};
        insertAndAssert(words, true);
        insertAndAssert(words, false);
        assertIsPresent(words, true);

        String[] nonInsertedWords = {"th", "thi", "tin", "s"};
        assertIsPresent(nonInsertedWords, false);

        assertEquals(8, t.membership());

        int[] theOutDegrees = {2, 2, 0};
        boolean[] theTerminals = {false, false, true};
        testOutDegreeAndTerminal("the", theOutDegrees, theTerminals);

        // "sing" and "song" share the same out degrees
        int[] OutDegrees = {2, 1, 1, 0};
        boolean[] songTerminals = {false, true, true, true};
        boolean[] singTerminals = {false, false, true, true};
        testOutDegreeAndTerminal("song", OutDegrees, songTerminals);
        testOutDegreeAndTerminal("sing", OutDegrees, singTerminals);
    }

    private Node getLastNodeOf(String word) {
        Node current = t.head;

        while (word.length() > 0) {
            int firstLetterIndex = getFirstLetterIndex(word);
            word = word.substring(1);

            if (word.length() == 0)
                return current.node[firstLetterIndex];
            else
                current = current.node[firstLetterIndex];
        }
        return null;
    }

    private void insertAndAssert(String[] words, boolean b) {
        for (String word : words)
            assertEquals(b, t.insert(word));
    }

    private void assertIsPresent(String[] words, boolean b) {
        for (String word : words)
            assertEquals(b, t.isPresent(word));
    }

    private void testOutDegreeAndTerminal(String word, int[] outDegrees, boolean[] terminals) {
        int i = 0;
        Node current = t.head;

        while (word.length() > 0) {
            int firstLetterIndex = getFirstLetterIndex(word);
            current = current.node[firstLetterIndex];
            assertEquals(outDegrees[i], current.outDegree);
            assertEquals(terminals[i], current.terminal);
            word = word.substring(1);
            i++;
        }
    }

    private int getFirstLetterIndex(String word) {
        return word.charAt(0) - 'a';
    }

    private int getIndex(char c) {
        return c - 'a';
    }
}
