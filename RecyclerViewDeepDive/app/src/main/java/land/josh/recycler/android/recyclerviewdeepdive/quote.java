package land.josh.recycler.android.recyclerviewdeepdive;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by josh on 8/31/15.
 */
public class quote {
    public String mQuoteText;
    public String mQuoteAuthor;

    public quote(String text, String author) {
        mQuoteText = text;
        mQuoteAuthor = author;
    }

    public String getQuoteAuthor() {
        return mQuoteAuthor;
    }

    public String getQuoteText() {
        return mQuoteText;
    }

    public static ArrayList<quote> quoteGen() {
        ArrayList<quote> quotes = new ArrayList<quote>();
        for (int i = 0; i < 200; i++) {
            quotes.add(new quote("Hope is the thing with feathers that perches in the soul - and sings the tunes without the words - and never stops at all.", "Emily Dickinson"));
        }
        return quotes;
    }
}
