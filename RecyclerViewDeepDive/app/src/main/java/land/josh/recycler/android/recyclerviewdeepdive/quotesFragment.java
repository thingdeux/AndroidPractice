package land.josh.recycler.android.recyclerviewdeepdive;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class quotesFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public quotesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_quotes, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.quote_recycler);
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        // A Recycler cannot function without a layout manager, the manager does the setting of
        // Items positions on creation of a new viewHolder.
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new quoteRecyclerAdapter(quote.quoteGen());
        mRecyclerView.setAdapter(mAdapter);

        return v;
    }


}
