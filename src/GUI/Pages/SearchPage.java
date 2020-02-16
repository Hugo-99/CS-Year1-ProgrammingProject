package GUI.Pages;

import Data.Query;
import Data.WordFrequency;
import GUI.Animator;
import GUI.Widgets.*;
import Main.Event;
import Main.Project;

import java.util.ArrayList;
import java.util.Arrays;

import static GUI.Layout.*;

/**
 * Search Page
 * Authors: Adam, Koh, and Stefan
 */
public class SearchPage extends Page {
    public final CheckBoxWidget primaryCheckBox;
    public final CheckBoxWidget sortRadioButton;
    public Query query;
    public SearchBar searchBar;
    private String suggest;

    /**
     * Constructor for the Search Page
     * Authors: Adam, Koh
     * @param main Reference to the main class
     */
    public SearchPage(Project main) {
        super(main);

        // Initialize the search bar
        searchBar = new SearchBar(main, MARGIN_LEFT * 2, TOOLBAR_HEIGHT + PADDING, CONTENT_WIDTH - MARGIN_LEFT - MARGIN_RIGHT, BUTTON_HEIGHT);
        widgetList.add(searchBar);

        // New object to create check boxes
        ArrayList<String> primaryOptions = new ArrayList<>(Arrays.asList("Stories", "Comments", "Users", "Date"));
        primaryCheckBox = new CheckBoxWidget(main, (WINDOW_WIDTH / 2) - 140, TOOLBAR_HEIGHT + BUTTON_HEIGHT + (PADDING * 3), WINDOW_WIDTH / 4, WINDOW_HEIGHT / 2, "Include:", primaryOptions);
        primaryCheckBox.check(0);

        ArrayList<String> sortOptions = new ArrayList<>(Arrays.asList("Latest", "Oldest", "Highest Score"));
        sortRadioButton = new CheckBoxWidget(main, (WINDOW_WIDTH / 2), TOOLBAR_HEIGHT + BUTTON_HEIGHT + (PADDING * 3), WINDOW_WIDTH / 4, WINDOW_HEIGHT / 2, "Sort By:", sortOptions);
        sortRadioButton.radio = true;
        sortRadioButton.set(2);

        Widget filterCover = new Widget(main, 0, TOOLBAR_HEIGHT, WINDOW_WIDTH, SEARCH_INTERFACE_HEIGHT);
        DropdownIndicatorWidget ddi = new DropdownIndicatorWidget(main, WINDOW_WIDTH - DDI_WIDTH - (2 * MARGIN_RIGHT), TOOLBAR_HEIGHT + SEARCH_INTERFACE_HEIGHT - DDI_HEIGHT - PADDING);

        this.query = new Query("", 0, new boolean[]{true, false, false, false});

        widgetList.add(filterCover);
        widgetList.add(primaryCheckBox);
        widgetList.add(sortRadioButton);
        widgetList.add(searchBar);
        widgetList.add(ddi);
        widgetList.add(new NavBar(main, 2));
    }

    public void keyPressed() {
        if (main.key == main.ENTER || main.key == main.RETURN) {
            searchBar.enter();
        } else {
            Event.LAST = Event.SEARCH;
        }

        ArrayList<WordFrequency> autoComplete;
        if (main.key == main.BACKSPACE) {
            searchBar.backSpace();
            String textToComplete = (searchBar.getText()).trim();
            autoComplete = main.dictionary.autoComplete(textToComplete);
            String nextSuggestion = main.dictionary.suggest(textToComplete);
            if (nextSuggestion != null)
                suggest = nextSuggestion;
        } else {
            if (main.key == main.CODED) {
                searchBar.arrow(main.keyCode);
            } else {
                searchBar.add(main.key);
                String textToComplete = (searchBar.getText()).trim();
                autoComplete = main.dictionary.autoComplete(textToComplete);
                String nextSuggestion = main.dictionary.suggest(textToComplete);
                if (nextSuggestion != null)
                    suggest = nextSuggestion;
            }
        }
    }

    /**
     * Hides the filters and search bar
     * Author: Stefan
     */
    public void hideFilters() {
        Animator.hideFilters(main, this);
    }

    /**
     * Reveals the filters and search bar
     * Author: Stefan
     */
    public void showFilters() {
        Animator.showFilters(main, this);
    }
}
