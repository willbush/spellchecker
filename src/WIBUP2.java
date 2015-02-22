class Node {
	protected static final int ALPHABET_SIZE = 26;
	int outDegree;
	boolean terminal;
	Children[] node;

	Node() {
		outDegree = 0;
		terminal = false;
		node = new Children[ALPHABET_SIZE];
	}
}

class Children extends Node {
	Children(int outDegree, boolean terminal) {
		this.outDegree = outDegree;
		this.terminal = terminal;
		node = new Children[ALPHABET_SIZE];
	}
}

class Trie {
	Node head;

	Trie() {
		head = new Node();
	}

	public boolean insert(String word) {
		return insert(word, head);
	}

	private boolean insert(String word, Node x) {
		if (word.length() == 0) {
			return true;
		}

		int firstLetterIndex = word.charAt(0) - 97; // a~z == 0~25
		String suffix = word.substring(1);
		if (x.node[firstLetterIndex] == null) {
			x.node[firstLetterIndex] = new Children(1, true);
			return insert(suffix, x.node[firstLetterIndex]);
		} else if (x.node[firstLetterIndex] != null) {
			x.node[firstLetterIndex].outDegree++;
			return insert(suffix, x.node[firstLetterIndex]);
		} else
			return false;
	}

	public boolean isPresent(String word) {
		int firstLetterIndex = word.charAt(0) - 97; // a~z == 0~25
		return head.node[firstLetterIndex] != null;
	}

	public boolean delete(String word) {
		int firstLetterIndex = word.charAt(0) - 97; // a~z == 0~25
		head.node[firstLetterIndex] = null;
		return head.node[firstLetterIndex] == null;
	}

	public int membership() {
		int count = 0;
		for (Children c : head.node) {
			if (c != null)
				count++;
		}

		return count;
	}

	public void listAll() {
		listAll(head);
	}

	private void listAll(Node x) {
		for (int i = 0; i < 26; i++) {
			if (isNonTerminalChar(x, i)) {
				char letter = (char) (97 + i);
				System.out.print(letter);
				listAll(x.node[i]);
			} else if (isTerminalChar(x, i)) {
				char letter = (char) (97 + i);
				System.out.println(letter);
				listAll(x.node[i]);
			}
		}
	}

	private boolean isNonTerminalChar(Node x, int i) {
		return x != null && x.node[i] != null && !x.terminal && i < 26;
	}

	private boolean isTerminalChar(Node x, int i) {
		return x != null && x.node[i] != null && x.terminal && i < 26;
	}
}

public class WIBUP2 {
}
