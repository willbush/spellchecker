Spellchecker
======

This was my second solo project from intro to data structures and algorithms, which uses a trie data structure to implement a spell checker. Insertions, deletions, and checking the spelling (word lookup) is performed in O(L) where L is the length of the word. 

This project was made to a specification, but some changes to the program deviating from the spec have already been made since it was turned in. At the moment the spell checker only works with lower case and has no input command validation, so input must be "perfect" (see examples below).


```
Command | arguments | action
----------------------------
N       |           | Prints student name
A       | w         | Add a word w
        |           | and print "Word inserted" or "Word already exist".
D       | w         | Delete a word w
        |           | and print "Word deleted" or "Word not present".
S       | w         | Search for word w
        |           | and print "Word found" or "Word not found".
M       |           | Print the membership (number of words in the tree).
C       | w1 w2 w3..| Checks the space seperated list of words for presence.
        |           | if not present, prints "Spelling mistake".
L       |           | Prints all the words in the trie structure in alphabetical order.
E       |           | Ends the run loop.
```

Example input:
```
N
A cat
A bat
A catch
A batch
A zoo
A zoom
A alltheworldsastage
A azazazazazazazazazaz
A zoo
A rat
A cat
A mouse
C hat cat rat pat mat ant any zooo zoo batch catch attach zoom azazazazazazazazazaz cat bat mouses
C tat mouse zoo alltheworldsastage razor pet rat zo ca ba fat
M
E
```

Example output for given input:
```
William Bush
Word inserted
Word inserted
Word inserted
Word inserted
Word inserted
Word inserted
Word inserted
Word inserted
Word already exists
Word inserted
Word already exists
Word inserted
Spelling mistake hat
Spelling mistake pat
Spelling mistake mat
Spelling mistake ant
Spelling mistake any
Spelling mistake zooo
Spelling mistake attach
Spelling mistake mouses
Spelling mistake tat
Spelling mistake razor
Spelling mistake pet
Spelling mistake zo
Spelling mistake ca
Spelling mistake ba
Spelling mistake fat
Membership is   10
```
