import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;


public class TrieTest {
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
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
    public void canCreateEmptyNode() {
        Node n = new Node();
        assertEquals(0, n.outDegree);
        assertFalse(n.terminal);
        assertEquals(26, n.node.length);
        assertNull(n.node[0]); // first element
        assertNull(n.node[25]); // last element
    }

    @Test
    public void canListEmptyTrie() {
        trie.listAll();
        assertEquals("", out.toString());
    }

    @Test
    public void insertNothingDoesNothing() {
        assertFalse(trie.insert(""));
    }

    @Test
    public void canInsertSimpleLetter() {
        assertTrue(trie.insert("a"));
        assertEquals(1, trie.membership());
        assertTrue(trie.insert("z"));
        assertEquals(2, trie.membership());

        assertTrue(trie.isPresent("a"));
        assertTrue(trie.isPresent("z"));

        assertTrue(trie.delete("a"));
        assertEquals(1, trie.membership());
        assertTrue(trie.delete("z"));
        assertEquals(0, trie.membership());

        assertFalse(trie.isPresent("a"));
        assertFalse(trie.isPresent("z"));
    }

    @Test
    public void canInsertTwoWords() {
        String[] words = {"tom", "cat"};
        insertAndAssert(words, true);
        Node m = getLastNodeOf("tom");
        assertTrue(m.terminal);
        assertEquals(2, trie.membership());
        trie.listAll(); // prints alphabetically
        assertEquals("cat\ntom\n", out.toString());
    }

    @Test
    public void canInsertThreeWords() {
        String[] words = {"alice", "bob", "joe"};

        insertAndAssert(words, true);
        insertAndAssert(words, false); // inserting the same words should fail
        assertIsPresent(words, true);
        assertEquals(3, trie.membership());
        assertEquals(3, trie.head.outDegree);

        trie.listAll(); // prints alphabetically
        assertEquals("alice\nbob\njoe\n", out.toString());
    }

    @Test
    public void canInsertThreeSimilarWords() {
        String[] words = {"vidi", "veni", "vici"};

        insertAndAssert(words, true);
        assertIsPresent(words, true);

        int[] outDegree = {1, 2, 2, 1, 0};
        boolean[] terminals = {false, false, false, false, true};
        testOutDegreeAndTerminal("vidi", outDegree, terminals);
        assertEquals(3, trie.membership());

        trie.listAll(); // prints alphabetically
        assertEquals("veni\nvici\nvidi\n", out.toString());
    }

    @Test
    public void givenSimilarStringsOutDegreeWorks() {
        String[] words = {"abce", "abcf", "abcg", "abch"};
        insertAndAssert(words, true);
        insertAndAssert(words, false);
        assertIsPresent(words, true);
        assertEquals(4, trie.membership());


        Node c = getLastNodeOf("abc");
        assertEquals(4, c.outDegree);
    }

    @Test
    public void deleteNothingDoesNothing() {
        assertFalse(trie.delete(""));
    }

    @Test
    public void canDeleteALetter() {
        assertTrue(trie.insert("a"));
        assertTrue(trie.delete("a"));
        assertEquals(0, trie.membership());
        assertEquals(0, trie.head.outDegree);
        assertFalse(trie.head.terminal);
    }

    @Test
    public void canDeleteTwoBranchingWords() {
        assertTrue(trie.insert("as"));
        assertTrue(trie.insert("at"));
        assertEquals(2, trie.membership());

        assertTrue(trie.delete("as"));
        assertFalse(trie.isPresent("as"));
        assertTrue(trie.isPresent("at"));

        assertTrue(trie.delete("at"));
        assertFalse(trie.isPresent("at"));
        assertFalse(trie.isPresent("as"));
        assertEquals(0, trie.membership());
    }

    @Test
    public void testComplexTreeInsertions() {
        String[] words = {"the", "tin", "thin", "tint", "song", "so", "son", "sing", "sin"};
        insertAndAssert(words, true);
        insertAndAssert(words, false);
        assertIsPresent(words, true);

        String[] nonInsertedWords = {"th", "thi", "ti", "s"};
        assertIsPresent(nonInsertedWords, false);

        assertEquals(9, trie.membership());

        int[] theOutDegrees = {2, 2, 2, 0};
        boolean[] theTerminals = {false, false, false, true};
        testOutDegreeAndTerminal("the", theOutDegrees, theTerminals);

        // "sing" and "song" share the same out degrees
        int[] OutDegrees = {2, 2, 1, 1, 0};
        boolean[] songTerminals = {false, false, true, true, true};
        boolean[] singTerminals = {false, false, false, true, true};
        testOutDegreeAndTerminal("song", OutDegrees, songTerminals);
        testOutDegreeAndTerminal("sing", OutDegrees, singTerminals);

        // test if can list alphabetically
        String expectedList = "sin\nsing\nso\nson\nsong\nthe\nthin\ntin\ntint\n";
        trie.listAll();
        assertEquals(expectedList, out.toString());
    }

    @Test
    public void testComplexTreeDeletions() {
        String[] words = {"the", "tin", "thin", "tint", "song", "so", "son", "sing", "sin"};
        insertAndAssert(words, true);

        // delete son
        String expectedList = "sin\nsing\nso\nsong\nthe\nthin\ntin\ntint\n";
        deleteWord("son", expectedList);
        assertTrue(trie.isPresent("song"));

        // delete so
        expectedList = "sin\nsing\nsong\nthe\nthin\ntin\ntint\n";
        deleteWord("so", expectedList);
        assertTrue(trie.isPresent("song"));

        // delete song
        expectedList = "sin\nsing\nthe\nthin\ntin\ntint\n";
        deleteWord("song", expectedList);
        int[] OutDegrees = {2, 1, 1, 1, 0};
        boolean[] singTerminals = {false, false, false, true, true};
        testOutDegreeAndTerminal("sing", OutDegrees, singTerminals);

        // delete the
        expectedList = "sin\nsing\nthin\ntin\ntint\n";
        deleteWord("the", expectedList);
        int[] thinOutDegrees = {2, 2, 1, 1, 0};
        boolean[] thinTerminals = {false, false, false, false, true};
        testOutDegreeAndTerminal("thin", thinOutDegrees, thinTerminals);

        // check membership
        assertEquals(5, trie.membership());

        // delete thin
        expectedList = "sin\nsing\ntin\ntint\n";
        deleteWord("thin", expectedList);
        int[] tintOutDegrees = {2, 1, 1, 1, 0};
        boolean[] tintTerminals = {false, false, false, true, true};
        testOutDegreeAndTerminal("tint", tintOutDegrees, tintTerminals);

        // delete sin
        expectedList = "sing\ntin\ntint\n";
        deleteWord("sin", expectedList);
        int[] singOutDegrees = {2, 1, 1, 1, 0};
        boolean[] singTerminals2 = {false, false, false, false, true};
        testOutDegreeAndTerminal("sing", singOutDegrees, singTerminals2);

        // delete sing
        expectedList = "tin\ntint\n";
        deleteWord("sing", expectedList);
        int[] tinOutDegrees = {1, 1, 1, 1, 0};
        boolean[] tinTerminals = {false, false, false, false, true};
        testOutDegreeAndTerminal("tin", tinOutDegrees, tinTerminals);
    }

    private void deleteWord(String word, String expectedList) {
        assertTrue(trie.delete(word));
        assertFalse(trie.delete(word));
        assertFalse(trie.isPresent(word));
        out.reset();
        trie.listAll();
        assertEquals(expectedList, out.toString());
    }

    private Node getLastNodeOf(String word) {
        Node current = trie.head;

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
            assertEquals(b, trie.insert(word));
    }

    private void assertIsPresent(String[] words, boolean b) {
        for (String word : words)
            assertEquals(b, trie.isPresent(word));
    }

    private void testOutDegreeAndTerminal(String word, int[] outDegrees, boolean[] terminals) {
        int i = 0;
        Node current = trie.head;

        while (word.length() > 0) {
            int firstLetterIndex = getFirstLetterIndex(word);
            assertEquals(outDegrees[i], current.outDegree);
            assertEquals(terminals[i], current.terminal);
            word = word.substring(1);
            current = current.node[firstLetterIndex];
            i++;
        }
    }

    private int getFirstLetterIndex(String word) {
        return word.charAt(0) - 'a';
    }
}
