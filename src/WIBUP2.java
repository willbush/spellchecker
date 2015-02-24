class Node {
    protected static final int ALPHABET_SIZE = 26;
    int outDegree; // the number of children a node has
    boolean terminal; // true if node represents the last letter in a word
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
    private int membership = 0;

    Trie() {
        head = new Node();
    }

    public boolean insert(String word) {
        boolean placed = insert(word, head);
        if (placed)
            membership++;
        return placed;
    }

    private boolean insert(String word, Node x) {
        if (word.length() == 1)
            return insertLastLetter(word, x);

        int i = getFirstLetterIndex(word);
        String suffix = word.substring(1);
        if (x.node[i] == null) {
            x.node[i] = new Children(0, false);
            x.outDegree++;
        }
        return insert(suffix, x.node[i]);
    }

    private boolean insertLastLetter(String word, Node x) {
        int i = getFirstLetterIndex(word);
        if (isTerminal(x, i))
            return false; // word already inserted
        else if (letterIsPresent(x, i)) {
            x.node[i].terminal = true;
            return true;
        } else {
            x.node[i] = new Children(0, true);
            x.outDegree++;
            return true;
        }
    }

    public boolean isPresent(String word) {
        Node current = head;

        while (word.length() > 0) {
            int firstLetterIndex = getFirstLetterIndex(word);
            word = (word.length() > 0) ? word.substring(1) : "";

            if (word.length() == 0 && isTerminal(current, firstLetterIndex))
                return true;
            else if (letterIsPresent(current, firstLetterIndex))
                current = current.node[firstLetterIndex];
            else
                return false;
        }
        return false;
    }

    private boolean isTerminal(Node x, int i) {
        return x.node[i] != null && x.node[i].terminal;
    }

    public boolean delete(String word) {
        int i = getFirstLetterIndex(word);
        head.node[i] = null;
        boolean removed = head.node[i] == null;
        if (removed)
            membership--;
        return removed;
    }

    private int getFirstLetterIndex(String word) {
        return word.charAt(0) - 97;
    }

    public void listAll() {
        listAll(head);
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
        return x != null && letterIsPresent(x, i) && !x.node[i].terminal;
    }

    private char getLetter(int i) {
        return (char) (97 + i);
    }

    private boolean isTerminalChar(Node x, int i) {
        return x != null && letterIsPresent(x, i) && x.node[i].terminal;
    }

    private boolean letterIsPresent(Node x, int i) {
        return x.node[i] != null;
    }

    public int membership() {
        return membership;
    }
}

public class WIBUP2 {
    public static void main(String[] args) {

    }
}
