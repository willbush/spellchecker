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
    // initialized and used by listAll to build the current dictionary of words in the Trie.
    private String[] dictionary;
    private int dictIndex;
    private boolean wordDeleted;

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
        String prefix = word.substring(1);
        if (x.node[i] == null) {
            x.node[i] = new Children(0, false);
            x.outDegree++;
        }
        return insert(prefix, x.node[i]);
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
            word = word.substring(1);

            if (word.length() == 0 && isTerminal(current, firstLetterIndex))
                return true;
            else if (letterIsPresent(current, firstLetterIndex))
                current = current.node[firstLetterIndex];
            else
                return false;
        }
        return false;
    }

    public boolean delete(String word) {
        wordDeleted = false;
        boolean removed = delete(head, word);
        if (removed)
            membership--;
        return removed;
    }

    private boolean delete(Node x, String word) {
        boolean delete = false;

        if (word.length() == 1) {
            int i = getFirstLetterIndex(word);
            if (letterIsPresent(x, i)) {
                if (isTerminal(x, i) && x.node[i].outDegree > 0) {
                    x.node[i].terminal = false;
                    wordDeleted = true;
                    delete = true;
                } else if (isTerminal(x, i)) {
                    x.node[i] = null;
                    delete = true;
                }
            }
            return delete;
        }

        if (word.length() > 0) {
            int i = getFirstLetterIndex(word);
            if (letterIsPresent(x, i)) {
                delete = delete(x.node[i], word.substring(1));

                if (!wordDeleted && delete) {
                    if (isTerminal(x, i) || x.node[i].outDegree > 1) {
                        x.node[i].outDegree--;
                        delete = true;
                        wordDeleted = true;
                    } else if (x == head) {
                        x.outDegree--;
                        x.node[i] = null;
                        delete = true;
                        wordDeleted = true;
                    } else {
                        x.node[i] = null;
                        delete = true;
                    }
                }
            }
        }
        return delete;
    }

    private int getFirstLetterIndex(String word) {
        return word.charAt(0) - 'a';
    }

    public void listAll() {
        dictIndex = 0;
        dictionary = new String[membership];
        buildDictionary(head, "");

        for (String word : dictionary)
            System.out.println(word);
    }

    private void buildDictionary(Node x, String s) {
        int i = 0;
        int outFound = 0;

        while (outFound < x.outDegree) {
            if (letterIsPresent(x, i)) {
                String possibleWord = s + getLetter(i);
                processPossibleWord(x, possibleWord, i);
                outFound++;
            }
            i++;
        }
    }

    private void processPossibleWord(Node x, String s, int i) {
        if (isTerminalWithOutDegree(x, i)) {
            addToDictionary(s);
            buildDictionary(x.node[i], s);
        } else if (isTerminal(x, i))
            addToDictionary(s);
        else
            buildDictionary(x.node[i], s);
    }

    private void addToDictionary(String word) {
        dictionary[dictIndex] = word;
        dictIndex++;
    }

    private boolean isTerminalWithOutDegree(Node x, int i) {
        return isTerminal(x, i) && x.node[i].outDegree > 0;
    }

    private boolean isTerminal(Node x, int i) {
        return x.node[i] != null && x.node[i].terminal;
    }

    private char getLetter(int i) {
        return (char) ('a' + i);
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
