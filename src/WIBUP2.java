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
        if (word.length() == 1) {
            int i = getFirstLetterIndex(word);
            x.node[i] = new Children(1, true);
            return true;
        }

        int i = getFirstLetterIndex(word);
        String suffix = word.substring(1);
        if (x.node[i] == null) {
            x.node[i] = new Children(1, false);
            return insert(suffix, x.node[i]);
        } else if (x.node[i] != null) {
            x.node[i].outDegree++;
            return insert(suffix, x.node[i]);
        } else
            return false;
    }

    public boolean isPresent(String word) {
        int i = getFirstLetterIndex(word);
        return head.node[i] != null;
    }

    public boolean delete(String word) {
        int i = getFirstLetterIndex(word);
        head.node[i] = null;
        return head.node[i] == null;
    }

    private int getFirstLetterIndex(String word) {
        return word.charAt(0) - 97;
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
        Node headCopy = head;
        listAll(headCopy);
    }

    private void listAll(Node x) {
        for (int i = 0; i < 26; i++) {
            if (isMultipleDegreeChar(x, i)) {
                System.out.print(getLetter(i));
                listAll(x.node[i]);
            } else if (isNonTerminalChar(x, i)) {
                System.out.print(getLetter(i));
                listAll(x.node[i]);
            } else if (isTerminalChar(x, i)) {
                System.out.println(getLetter(i));
                listAll(x.node[i]);
            }
        }
    }

    private boolean isMultipleDegreeChar(Node x, int i) {
        return isNonTerminalChar(x, i) && x.node[i].outDegree > 1;
    }

    private boolean isNonTerminalChar(Node x, int i) {
        return x != null && x.node[i] != null && !x.node[i].terminal;
    }

    private char getLetter(int i) {
        return (char) (97 + i);
    }

    private boolean isTerminalChar(Node x, int i) {
        return x != null && x.node[i] != null && x.node[i].terminal;
    }
}

public class WIBUP2 {
}
