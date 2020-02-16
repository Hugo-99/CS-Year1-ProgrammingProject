package Data;

/**
 * Superclass for all objects returned from Query
 * Author: Stefan
 */
public class DBElement {
    final int id;
    protected final String author;

    DBElement(int id, String author) {
        this.id = id;
        this.author = author;
    }
}