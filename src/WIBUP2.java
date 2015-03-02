import java.util.Scanner;

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
        boolean placed = word.length() > 0 && insert(word, head);
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
        if (word.length() == 1)
            return deleteLastLetter(x, word);
        return word.length() > 0 && deletePrefixLetters(x, word);
    }

    private boolean deletePrefixLetters(Node x, String word) {
        boolean lastLetterDeleted = false;
        int i = getFirstLetterIndex(word);
        if (letterIsPresent(x, i)) {
            lastLetterDeleted = delete(x.node[i], word.substring(1));

            if (!wordDeleted && lastLetterDeleted) {
                if (isTerminal(x, i) || x.node[i].outDegree > 1) {
                    x.node[i].outDegree--;
                    lastLetterDeleted = true;
                    wordDeleted = true;
                } else if (x == head) {
                    deleteHeadLetter(x, i);
                    lastLetterDeleted = true;
                } else {
                    x.node[i] = null;
                    lastLetterDeleted = true;
                }
            }
        }
        return lastLetterDeleted;
    }

    private boolean deleteLastLetter(Node x, String word) {
        int i = getFirstLetterIndex(word);
        if (isTerminal(x, i) && x.node[i].outDegree > 0) {
            x.node[i].terminal = false;
            wordDeleted = true;
            return true;
        } else if (x == head) {
            deleteHeadLetter(x, i);
            return true;
        } else if (isTerminal(x, i)) {
            x.node[i] = null;
            return true;
        }
        return false;
    }

    private void deleteHeadLetter(Node x, int i) {
        x.outDegree--;
        x.node[i] = null;
        wordDeleted = true;
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
        if (isTerminal(x, i) && x.node[i].outDegree > 0) {
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
