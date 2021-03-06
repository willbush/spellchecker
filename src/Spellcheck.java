import java.util.Scanner;

class Node {
    protected static final int ALPHABET_SIZE = 26;
    int outDegree; // the number of direct children a node has
    boolean isTerminal; // true if node represents the end of a word
    Children[] node;

    Node() {
        outDegree = 0;
        isTerminal = false;
        node = new Children[ALPHABET_SIZE];
    }
}

class Children extends Node {
    Children(int outDegree, boolean terminal) {
        this.outDegree = outDegree;
        this.isTerminal = terminal;
        node = new Children[ALPHABET_SIZE];
    }
}

class Trie {
    private int wordCount = 0;
    Node head;

    Trie() {
        head = new Node();
    }

    /**
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
            x.node[i].isTerminal = true;
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
     * deleting letters in the base case and while recursing out. If canDeleteNext
     * successfully removes the word, it will return false, and all other
     * canDelete out recursion will also be false because the prefix
     * letters are shared with another word in the trie structure.
     *
     * @return true if current letter can be deleted
     */
    private boolean canDelete(Node x, String word) {
        boolean canDelete = false;
        int i = getFirstLetterIndex(word);
        int length = word.length();

        if (length == 1 && lastLetterIsValid(x, i))
            canDelete = canDeleteNext(x, i, length);

        else if (length > 1 && letterIsPresent(x, i))
            canDelete = canDelete(x.node[i], word.substring(1)) && canDeleteNext(x, i, length);

        return canDelete;
    }

    private boolean lastLetterIsValid(Node x, int i) {
        return x == head || isTerminal(x, i);
    }

    /**
     * Deletes letter if it is not shared. If letter is shared it makes appropriate
     * modifications to the trie structure by changing the outDegree or terminal of a node.
     *
     * @return true if next prefix letter can be deleted.
     * false if word successfully removed and other letter deletions are not needed
     */
    private boolean canDeleteNext(Node x, int i, int length) {
        boolean canDeleteNext = false;

        if (lastLetterIsShared(x, i, length))
            removeWordByTurningTerminalOff(x, i);
        else if (prefixLetterIsShared(x, i, length))
            removeWordByDecrementingOutDegree(x, i);
        else if (x == head)
            removeWordByDeletingHeadLetter(x, i);
        else {
            deleteLetter(x, i);
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

    /*
    Example: If removing "so" when "song" exist in the trie, then only the
    terminal for "o" needs to be set to false.
     */
    private void removeWordByTurningTerminalOff(Node x, int i) {
        x.node[i].isTerminal = false;
        wordCount--;
    }

    /**
     * @return true if prefix letter is shared with another word in the trie.
     */
    private boolean prefixLetterIsShared(Node x, int i, int length) {
        return length > 1 && (isTerminal(x, i) || x.node[i].outDegree > 1);
    }

    /*
    Example: If deleting "the" when "that" exist in the trie, previous letter
    "e" was already deleted and now we are at "h" which is a prefix letter and
    it is shared with "that". In this situation only the outDegree of "h" needs
    to be decremented to successfully remove the word "the". Also works for
    situations like deleting "song" when "so" already exist in the trie.
     */
    private void removeWordByDecrementingOutDegree(Node x, int i) {
        x.node[i].outDegree--;
        wordCount--;
    }

    /*
    Occurs when none of the letters from the word to be deleted are shared.
     */
    private void removeWordByDeletingHeadLetter(Node x, int i) {
        x.outDegree--;
        wordCount--;
        deleteLetter(x, i);
    }

    private void deleteLetter(Node x, int i) {
        x.node[i] = null;
    }

    /**
     * @return index of the first letter in a word (assuming word is lowercase)
     * such that a = 0, b = 1, ..., z = 25
     */
    private int getFirstLetterIndex(String word) {
        return word.charAt(0) - 'a';
    }

    public void printAllWords() {
        printAllWords(head, "");
    }

    private void printAllWords(Node x, String s) {
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
            printAllWords(x.node[i], word);
        } else if (isTerminal(x, i))
            System.out.println(word);
        else
            printAllWords(x.node[i], word);
    }

    private boolean isTerminal(Node x, int i) {
        return x.node[i] != null && x.node[i].isTerminal;
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

public class Spellcheck {
    private Scanner input;
    private Trie trie;

    Spellcheck(java.io.InputStream in) {
        input = new Scanner(in);
        trie = new Trie();
    }

    protected void run() {
        String[] tokens;

        do {
            String line = input.nextLine();
            tokens = line.split(" ");
            performCommands(tokens);
        } while (inputHasNext(tokens[0]));
    }

    private void performCommands(String[] tokens) {
        switch (tokens[0]) {
            case "N":
                System.out.println("William Bush");
                break;

            case "A":
                insert(tokens[1]);
                break;

            case "D":
                delete(tokens[1]);
                break;

            case "S":
                search(tokens[1]);
                break;

            case "M":
                System.out.printf("Membership is %4d\n", trie.getWordCount());
                break;

            case "C":
                checkWords(tokens);
                break;

            case "L":
                trie.printAllWords();
                break;
        }
    }

    private boolean inputHasNext(String command) {
        return !command.equals("E");
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
        Spellcheck program = new Spellcheck(System.in);
        program.run();
    }
}
