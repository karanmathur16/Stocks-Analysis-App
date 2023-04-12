package com.example.stocksmaster;

import android.content.Context;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Auto suggest adapter.
 */
public class autoSuggestAdapter extends ArrayAdapter<String>{
    private List<String> listData;

    /**
     * Instantiates a new Auto suggest adapter.
     *
     * @param context  the context
     * @param resource the resource
     */
    public autoSuggestAdapter(Context context, int resource) {
        super(context, resource);
        listData = new ArrayList<>();
    }

    /**
     * Sets data.
     *
     * @param list the list
     */
    public void setData(List<String> list) {
        listData.clear();
        listData.addAll(list);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public String getItem(int position) {
        return listData.get(position);
    }

    /**
     * Used to Return the full object directly from adapter.
     *
     * @param position the position
     * @return object
     */
    public String getObject(int position) {
        return listData.get(position);
    }

    /**
     * Publish results.
     *
     * @param constraint the constraint
     * @param results    the results
     */
    protected void publishResults(CharSequence constraint, String[] results) {
        if (results != null && (results.length > 0)) {
            notifyDataSetChanged();
        } else {
            notifyDataSetInvalidated();
        }
    }
}