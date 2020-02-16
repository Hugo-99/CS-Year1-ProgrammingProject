package Data;

/**
 * Object for storing words and the number of their appearances when needed.
 * implements comparable to allow sorting.
 * Author: Adam
 */
public class WordFrequency implements Comparable<WordFrequency> {
    String word;
    public int frequency;

    WordFrequency(String word, int numberOfOccurrences) {
        this.word = word;
        this.frequency = numberOfOccurrences;
    }

    public int compareTo(WordFrequency otherWord) {
        return otherWord.frequency - this.frequency;
    }

    public String toString() {
        return (word);
    }
}
