package com.cuk.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MainPageListAdapter extends ArrayAdapter<RecipeEntry>{
    private List<RecipeEntry> entries;
    private int layout;

    public MainPageListAdapter(Context context, int resource, List<RecipeEntry> objects) {
        super(context, resource, objects);

        this.entries = objects;
        this.layout = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(
                    layout, parent, false
            );
        }

        ((TextView) convertView.findViewById(R.id.entry_title)).setText(entries.get(position).title);
        ((TextView) convertView.findViewById(R.id.entry_time)).setText(entries.get(position).cookTime + " minutes");
        ((TextView) convertView.findViewById(R.id.entry_type)).setText(entries.get(position).type);

        return convertView;
    }
}
