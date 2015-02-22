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
	public void canInsertTwoLetterWord() {
		assertTrue(t.insert("as"));
		t.listAll();
		assertEquals("as\n", out.toString());
		assertEquals(1, t.membership());
		assertTrue(t.isPresent("as"));
		assertTrue(t.head.node[0].terminal);
		assertTrue(t.delete("as"));
		assertFalse(t.isPresent("as"));
		assertEquals(0, t.membership());
		t.listAll();
	}
}
