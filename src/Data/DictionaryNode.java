package Data;

import java.sql.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;


/**
 * Node of a Dictionary Object
 * Author: Adam
 */
class DictionaryNode {
    HashMap children;
    int wordCount;
    private int amountToStore;
    WordFrequency[] mostFrequentWords;
    private int differentWords;
    private int countAllWords;
    DictionaryNode(int amountToStore) {
        children = new HashMap<Character, DictionaryNode>();
        wordCount = 0;
        this.amountToStore = amountToStore;
        mostFrequentWords = new WordFrequency[amountToStore];
        for(int i = 0; i < amountToStore; i++)
            mostFrequentWords[i] = new WordFrequency("", 0);
        differentWords=0;
        countAllWords++;
    }

    /**
     * Updates nodes that are its prefixes, so they can change their mostPopularWords arrays.
     * @param word
     */
    void updateWords(WordFrequency word) {
        boolean alreadyInserted=false;
        for(WordFrequency wordInArray : mostFrequentWords)
        {
            if(wordInArray.word.equals(word.word)) {
                alreadyInserted = true;
                wordInArray.frequency++;
            }
        }
        countAllWords++;
        if(word.frequency==1)
            differentWords++;
        if(!alreadyInserted) {
            int index = amountToStore - 1;
            while (index >= 0 && word.frequency > mostFrequentWords[index].frequency)
                index--;

            index++;
            for (int i = amountToStore - 1; i > index; i--)
                mostFrequentWords[i] = mostFrequentWords[i - 1];

            if (index < amountToStore)
                mostFrequentWords[index] = word;
        }
        Arrays.sort(mostFrequentWords);
    }
    public int getDifferentCount()
    {
        return differentWords;
    }
    public int getCountAllWords() { return countAllWords;}
    public WordFrequency[] getMostPopularWords() { return mostFrequentWords;}
}