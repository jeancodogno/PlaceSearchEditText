package io.github.jeancodogno.placesearchedittext;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PlaceAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final int textViewResourceId;
    private final int resource;
    private ArrayList<String> items;

    public PlaceAdapter(Context context, int resource,  int textViewResourceId, ArrayList<String> items) {
        super(context, resource, textViewResourceId, items);
        this.textViewResourceId = textViewResourceId;
        this.context = context;
        this.resource = resource;
        this.items = new ArrayList<String>(items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) this.context).getLayoutInflater();
            view = inflater.inflate(this.resource, parent, false);
        }
        String location = this.items.get(position);
        if (location != null) {
            TextView lblName = (TextView) view.findViewById(this.textViewResourceId);
            if (lblName != null)
                lblName.setText(location, TextView.BufferType.EDITABLE);
        }

        return view;
    }
    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = (String) resultValue;
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {

                FilterResults filterResults = new FilterResults();
                filterResults.values = PlaceAdapter.this.items;
                filterResults.count = PlaceAdapter.this.items.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<String> filterList = (ArrayList<String>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (String location : filterList) {
                    add(location);
                    notifyDataSetChanged();
                }
            }
        }
    };
}