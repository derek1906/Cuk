package com.cuk.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MainPageListNewAdapter extends RecyclerView.Adapter<MainPageListNewAdapter.ViewHolder> {
    private List<RecipeEntry> entries;
    private Context ctx;

    MainPageListNewAdapter(Context context, List<RecipeEntry> list){
        entries = list;
        ctx = context;
    }

    @Override
    public MainPageListNewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.main_page_new_entry, parent, false
        );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainPageListNewAdapter.ViewHolder holder, final int position) {
        RecipeEntry entry = entries.get(position);
        holder.title.setText(entry.title);
        holder.type.setText(entry.type);
        holder.cookTime.setText(ctx.getResources().getString(R.string.main_page_cookTime, entry.cookTime));

        holder.position = position;
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView cookTime;
        public TextView type;

        public CardView card;
        int position;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.entry_title);
            cookTime = (TextView) itemView.findViewById(R.id.entry_time);
            type = (TextView) itemView.findViewById(R.id.entry_type);

            card = (CardView) itemView.findViewById(R.id.card);

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ctx, RecipeDetails.class);
                    intent.putExtra("recipe", entries.get(position));
                    Pair<View, String> p1 = Pair.create((View) v.findViewById(R.id.entry_image), "recipeImage");
                    ActivityOptionsCompat options = ActivityOptionsCompat
                            .makeSceneTransitionAnimation((Activity) ctx, p1);
                    ctx.startActivity(intent, options.toBundle());
                }
            });
        }
    }
}
