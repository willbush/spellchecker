import java.util.Scanner;

class Node {
    protected static final int ALPHABET_SIZE = 26;
    int outDegree; // the number of children a node has
    boolean terminal; // true if node represents the end of a word
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
    Node head; // head's terminal should always be false
    private int membership = 0; // the number of words in the trie

    Trie() {
        head = new Node();
    }

    /**
     * @param word to insert
     * @return true if inserted successfully
     */
    public boolean insert(String word) {
        boolean placed = word.length() > 0 && insert(word, head);
        if (placed)
            membership++;
        return placed;
    }

    private boolean insert(String word, Node x) {
        int i = getFirstLetterIndex(word);

        if (word.length() == 1)
            return insertLastLetter(i, x);

        if (!letterIsPresent(x, i))
            insertLetter(x, i, false);

        String prefix = word.substring(1);
        return insert(prefix, x.node[i]);
    }

    private boolean insertLastLetter(int i, Node x) {
        if (isTerminal(x, i))
            return false; // word already inserted
        else if (letterIsPresent(x, i)) {
            x.node[i].terminal = true;
            return true;
        } else {
            insertLetter(x, i, true);
            return true;
        }
    }

    private void insertLetter(Node x, int i, boolean isTerminal) {
        x.node[i] = new Children(0, isTerminal);
        x.outDegree++;
    }

    /**
     * @param word to check
     * @return true if word is present
     */
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

    /**
     * @param word to delete
     * @return true if deleted
     */
    public boolean delete(String word) {
        int membershipBeforeDelete = membership;

        if (word.length() > 0)
            deleteMe(head, word);

        return membershipBeforeDelete - 1 == membership;
    }

    /**
     * @param x    current node
     * @param word word to delete
     * @return true if current letter can be deleted
     */
    private boolean deleteMe(Node x, String word) {
        int i = getFirstLetterIndex(word);
        int length = word.length();

        if (length == 1 && lastLetterIsValid(x, i))
            return canDelete(x, i, length);

        return letterCanBeDeleted(x, i, word) && canDelete(x, i, length);
    }

    private boolean lastLetterIsValid(Node x, int i) {
        return x == head || isTerminal(x, i);
    }

    /*
    If the letter exist then we recurse until the base case is reached.
    If canDelete successfully removes the word in the base case or while recursing out,
    then deleteMe will return false and previous prefix letters are not to be deleted
    (i.e. letterCanBeDeleted == false).
     */
    private boolean letterCanBeDeleted(Node x, int i, String word) {
        return word.length() > 1 && letterIsPresent(x, i) && deleteMe(x.node[i], word.substring(1));
    }

    /**
     * @return true if prefix letter can be deleted.
     * false if word successfully removed and other letter deletions are not needed
     */
    private boolean canDelete(Node x, int i, int length) {
        boolean canDelete = false;

        if (lastLetterIsShared(x, i, length)) {
            x.node[i].terminal = false;
            membership--;
        } else if (prefixLetterIsShared(x, i, length)) {
            x.node[i].outDegree--;
            membership--;
        } else if (x == head) {
            deleteHeadLetter(x, i);
        } else {
            x.node[i] = null;
            canDelete = true;
        }
        return canDelete;
    }

    private boolean lastLetterIsShared(Node x, int i, int length) {
        return length == 1 && isTerminal(x, i) && x.node[i].outDegree > 0;
    }

    private boolean prefixLetterIsShared(Node x, int i, int length) {
        return length > 1 && (isTerminal(x, i) || x.node[i].outDegree > 1);
    }

    private void deleteHeadLetter(Node x, int i) {
        x.outDegree--;
        membership--;
        x.node[i] = null;
    }

    private int getFirstLetterIndex(String word) {
        return word.charAt(0) - 'a';
    }

    /**
     * prints all words in the trie structure
     */
    public void listAll() {
        listAll(head, "");
    }

    private void listAll(Node x, String s) {
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

    private void processPossibleWord(Node x, String word, int i) {
        if (isTerminal(x, i) && x.node[i].outDegree > 0) {
            System.out.println(word);
            listAll(x.node[i], word);
        } else if (isTerminal(x, i))
            System.out.println(word);
        else
            listAll(x.node[i], word);
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

    /**
     * @return number of words in the trie structure
     */
    public int membership() {
        return membership;
    }
}

public class WIBUP2 {
    private Scanner input;
    private Trie trie;

    WIBUP2(java.io.InputStream in) {
        input = new Scanner(in);
        trie = new Trie();
    }

    protected void run() {
        boolean done = false;

        while (!done) {
            String line = input.nextLine();
            String[] tokens = line.split(" ");
            done = handleInput(tokens);
        }
    }

    private boolean handleInput(String[] tokens) {
        boolean done = false;

        switch (tokens[0]) {
            case "N": {
                System.out.println("William Bush");
                break;
            }
            case "A": {
                insert(tokens[1]);
                break;
            }
            case "D": {
                delete(tokens[1]);
                break;
            }
            case "S": {
                search(tokens[1]);
                break;
            }
            case "M": {
                System.out.printf("Membership is %4d\n", trie.membership());
                break;
            }
            case "C": {
                checkWords(tokens);
                break;
            }
            case "L": {
                trie.listAll();
                break;
            }
            case "E": {
                done = true;
                break;
            }
        }
        return done;
    }

    private void insert(String word) {
        if (trie.insert(word))
            System.out.println("Word inserted");
        else
            System.out.println("Word already exists");
    }

    private void delete(String word) {
        if (trie.delete(word))
            System.out.println("Word deleted");
        else
            System.out.println("Word not present");
    }

    private void checkWords(String[] tokens) {
        for (int i = 1; i < tokens.length; i++)
            if (!trie.isPresent(tokens[i]))
                System.out.println("Spelling mistake " + tokens[i]);
    }

    private void search(String word) {
        if (trie.isPresent(word))
            System.out.println("Word found");
        else
            System.out.println("Word not found");
    }

    public static void main(String[] args) {
        WIBUP2 program = new WIBUP2(System.in);
        program.run();
    }
}
