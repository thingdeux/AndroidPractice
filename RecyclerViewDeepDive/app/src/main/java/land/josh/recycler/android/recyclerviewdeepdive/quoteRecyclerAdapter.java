package land.josh.recycler.android.recyclerviewdeepdive;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by josh on 8/30/15.
 */
public class quoteRecyclerAdapter extends RecyclerView.Adapter<quoteRecyclerAdapter.ViewHolder> {
    private ArrayList<quote> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string
        public TextView mQuoteAuthorTextView;
        public TextView mQuoteTextView;

        public ViewHolder(View v) {
            super(v);
            mQuoteTextView = (TextView) v.findViewById(R.id.quote_text);
            mQuoteAuthorTextView = (TextView) v.findViewById(R.id.quote_author);
        }
    }

    // Provide a suitable constructor (for the dataset)
    public quoteRecyclerAdapter(ArrayList<quote> myDataset) {
        mDataset = myDataset;
    }

    // Create new viewholders (invoked by the layout manager)
    // Called when it's time to "spin up" a new viewholder
    @Override
    public quoteRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.quote_view, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Binds the objects from the dataset to the view.
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get element from the dataset at this position - replace the contents of the view with it
        holder.mQuoteTextView.setText(mDataset.get(position).getQuoteText());
        holder.mQuoteAuthorTextView.setText(mDataset.get(position).getQuoteAuthor());
    }

    // Return the size of the dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
