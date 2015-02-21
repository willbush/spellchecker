class Node {
	int outDegree;
	boolean terminal;
	Children[] node;

	Node() {
		outDegree = 0;
		terminal = false;
		final int ALPHABET_SIZE = 26;
		node = new Children[ALPHABET_SIZE];
	}
}

class Children extends Node {
	Children(int outDegree, boolean terminal) {
		this.outDegree = outDegree;
		this.terminal = terminal;
		node = new Children[outDegree];
	}
}

class Trie {
	Node head;

	Trie() {
		head = new Node();
	}

	public boolean insert(String word) {
		int firstLetterIndex = word.charAt(0) - 97; // a~z == 0~25
		int suffixLen = word.length() - 1;

		if (head.node[firstLetterIndex] == null) {
			head.node[firstLetterIndex] = new Children(suffixLen, true);
			return true;
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
		listAll(head, 0);
	}

	private void listAll(Node x, int i) {
		if (isNonTerminalChar(x, i)) {
			char letter = (char)(97 + i);
			System.out.print(letter);
			listAll(x.node[i], i);
			i++;
			listAll(x.node[i], i);
		} else if (isTerminalChar(x, i)) {
			System.out.println((char) 97 + i);
			listAll(x.node[i], i);
			i++;
			listAll(x.node[i], i);
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
