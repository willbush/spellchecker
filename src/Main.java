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
    Node head;
    private int wordCount = 0; // the number of words in the trie

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
            wordCount++;
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
     * @param word to check if present
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
     * @return true if word deleted
     */
    public boolean delete(String word) {
        int wordCountBeforeDelete = wordCount;

        if (word.length() > 0)
            canDelete(head, word);

        return wordCountBeforeDelete - 1 == wordCount;
    }

    /**
     * This method recursively verifies the words existence, then starts
     * deleting letters while recursing out. If delete successfully removes the
     * word, it will return false. Once delete returns false once all the
     * canDelete out recursion will also be false because the prefix letters are
     * shared with another word in the trie structure.
     *
     * @param x    current node
     * @param word word to delete
     * @return true if current letter can be deleted
     */
    private boolean canDelete(Node x, String word) {
        boolean canDelete = false;
        int i = getFirstLetterIndex(word);
        int length = word.length();

        if (length == 1 && lastLetterIsValid(x, i))
            canDelete = delete(x, i, length);

        else if (length > 1 && letterIsPresent(x, i))
            canDelete = canDelete(x.node[i], word.substring(1)) && delete(x, i, length);

        return canDelete;
    }

    private boolean lastLetterIsValid(Node x, int i) {
        return x == head || isTerminal(x, i);
    }

    /**
     * Deletes letter at x i.
     *
     * @return true if prefix letter can be deleted.
     * false if word successfully removed and other letter deletions are not needed
     */
    private boolean delete(Node x, int i, int length) {
        boolean canDeleteNext = false;

        if (lastLetterIsShared(x, i, length)) {
            x.node[i].terminal = false;
            wordCount--;
        } else if (prefixLetterIsShared(x, i, length)) {
            x.node[i].outDegree--;
            wordCount--;
        } else if (x == head) {
            deleteHeadLetter(x, i);
        } else {
            x.node[i] = null;
            canDeleteNext = true;
        }
        return canDeleteNext;
    }

    /**
     * @return true if last letter is shared with another word in the trie.
     */
    private boolean lastLetterIsShared(Node x, int i, int length) {
        return length == 1 && isTerminal(x, i) && x.node[i].outDegree > 0;
    }

    /**
     * @return true if letter is shared with another word in the trie.
     */
    private boolean prefixLetterIsShared(Node x, int i, int length) {
        return length > 1 && (isTerminal(x, i) || x.node[i].outDegree > 1);
    }

    private void deleteHeadLetter(Node x, int i) {
        x.outDegree--;
        wordCount--;
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

    public int getWordCount() {
        return wordCount;
    }
}

public class Main {
    private Scanner input;
    private Trie trie;

    Main(java.io.InputStream in) {
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
                System.out.printf("Membership is %4d\n", trie.getWordCount());
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
        Main program = new Main(System.in);
        program.run();
    }
}
