package Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Trie data structure for storing words that appear in titles of stories
 * Not currently used as it takes up too much RAM
 * Author: Adam
 */
public class Dictionary {
    private  DictionaryNode root;

    public Dictionary() {
        root = new DictionaryNode(10);
    }

    Dictionary(ArrayList<String> keysToInsert) {
        root = new DictionaryNode(10);
        for(String keyToInsert : keysToInsert) {
            this.insert(keyToInsert);
        }
    }

    public DictionaryNode getRoot() { return root;}
    void formTrie(ArrayList<String> keys) {
        for(String keyToInsert : keys) {
            this.insert(keyToInsert);
        }
    }

    /**
     * Inserts a single word into the dictionary.
     * @param keyToInsert
     */

    public void insert(String keyToInsert) {
        DictionaryNode node = root;
        keyToInsert = keyToInsert.toLowerCase();
        ArrayList<DictionaryNode> visitedNodes = new ArrayList<DictionaryNode>();
        visitedNodes.add(root);
        for(int i = 0; i < keyToInsert.length(); i++) {
            char c = keyToInsert.charAt(i);
            if(node != null && node.children.get(c) == null)
                node.children.put(c, new DictionaryNode(5));

            node = ((DictionaryNode) node.children.get(c));
            visitedNodes.add(node);
        }
        node.wordCount++;
        WordFrequency insertedWordFrequency = new WordFrequency(keyToInsert, node.wordCount);
        for(DictionaryNode nodeToUpdate : visitedNodes)
            nodeToUpdate.updateWords(insertedWordFrequency);
    }

    /**
     * Inserts multiple words, passed as an ArrayList of strings, into the dictionary.
     * @param keysToInsert
     */
    void insertAll(ArrayList<String> keysToInsert) {
        for(String keyToInsert : keysToInsert) {
            this.insert(keyToInsert);
        }
    }

    /**
     * Searches for the given word in the dictionary.
     * @param keyToSearch
     * @return Node corresponding to this word, or null if the word isn't found.
     */
    private DictionaryNode search(String keyToSearch) {
        DictionaryNode currentNode = root;
        for(int i = 0; i < keyToSearch.length(); i++) {
            char c = keyToSearch.charAt(i);
            if(currentNode.children.get(c) == null) {
                return null;
            }
            currentNode = ((DictionaryNode) currentNode.children.get(c));
        }
        return currentNode;
    }

    int wordCount(String word) {
        int count = 0;
        DictionaryNode node = this.search(word);
        if(node != null) {
            count = node.wordCount;
        }
        return count;
    }

    /**
     * Autocompletes the text passed in. Returns 5 most popular word with that prefix and all words up to 4 chars longer with that prefix.
     * @param textToComplete
     * @return ArrayList of WordFrequency, sorted, representing the words with the amount of uses.
     */
    public ArrayList<WordFrequency> autoComplete(String textToComplete) {
        ArrayList<WordFrequency> wordList = new ArrayList<>();
        DictionaryNode startingNode = search(textToComplete);
        if(startingNode != null) {
            wordList = recAutoComplete(textToComplete, 4, startingNode);
            for(WordFrequency word : startingNode.mostFrequentWords) {
                boolean contains = false;
                for(int i = 0; i < wordList.size(); i++)
                    if(word.word.equals(wordList.get(i).word))
                        contains = true;

                if(!contains)
                    wordList.add(word);
            }
        }
        Collections.sort(wordList);
        return wordList;
    }

    /**
     * a Function to implement autoComplete. Tries to recursively find up to n characters to make the word passed in longer.
     * Takes in the word to complete, how many characters more we want and the node we are starting in.
     * @param textToComplete
     * @param desiredLengthIncrease
     * @param node
     * @return ArrayList of all found words with their amounts of uses, stored as WordFrequency.
     */
    private static ArrayList<WordFrequency> recAutoComplete(String textToComplete, int desiredLengthIncrease, DictionaryNode node) {
        ArrayList<WordFrequency> wordList = new ArrayList<>();
        if(desiredLengthIncrease > 0) {
            for(Map.Entry<Character, DictionaryNode> entry : ((HashMap<Character, DictionaryNode>) (node.children)).entrySet()) {
                DictionaryNode nextNode = (DictionaryNode) (entry.getValue());
                //println((char)i);
                ArrayList<WordFrequency> newList = recAutoComplete(textToComplete + (char) (entry.getKey()), desiredLengthIncrease - 1, nextNode);
                if(newList != null)
                    wordList.addAll(newList);
            }
        }

        if(node.wordCount > 0)
            wordList.add(new WordFrequency(textToComplete, node.wordCount));

        return wordList;
    }

    public String suggest(String wordToSearch) {
        DictionaryNode node = search(wordToSearch);
        if(node != null)
            return node.mostFrequentWords[0].toString();
        else
            return null;
    }
    public int getDifferentCount() { return root.getDifferentCount();}
    public int getCountAllWords() { return root.getCountAllWords();}

}
