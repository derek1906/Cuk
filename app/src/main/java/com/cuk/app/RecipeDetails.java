package com.cuk.app;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class RecipeDetails extends AppCompatActivity {

    DatabaseManager dbManager;
    RecipeEntry entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Floating Action Button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TableManager table = dbManager.table("stored_entries");

                //insert if not exist

                if(!table.checkIfValueExists("entry_id", ""+entry.id)){
                    ContentValues contents = new ContentValues();
                    contents.put("entry_id", entry.id);
                    table.insert(contents);
                    Snackbar.make(view, getString(R.string.added_to_cookbook), Snackbar.LENGTH_LONG).show();
                }else{
                    table.delete("entry_id", entry.id);
                    Snackbar.make(view, getString(R.string.removed_from_cookbook), Snackbar.LENGTH_LONG).show();
                }

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        entry = (RecipeEntry) extras.getSerializable("recipe");
        populateFields(entry);

        dbManager = new DatabaseManager(this);

        //Action bar title opacity
        final ScrollView scrollArea = (ScrollView) findViewById(R.id.scrollArea);
        final View actionBarTitle = toolbar.findViewById(R.id.actionBarTitle);
        scrollArea.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            boolean isShown = false;
            int threshold = (int) getResources().getDimension(R.dimen.recipe_details_image_height);

            @Override
            public void onScrollChanged() {
                int scrollY = scrollArea.getScrollY();
                Log.d("cukApp", String.valueOf(scrollY));

                if (!isShown && scrollY > threshold) {
                    actionBarTitle.animate().setDuration(200).alpha(1f);
                    isShown = true;
                } else if (isShown && scrollY < threshold) {
                    actionBarTitle.animate().setDuration(200).alpha(0f);
                    isShown = false;
                }
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getEnterTransition().addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {

                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    findViewById(R.id.fab).animate().alpha(1f);
                }

                @Override
                public void onTransitionCancel(Transition transition) {

                }

                @Override
                public void onTransitionPause(Transition transition) {

                }

                @Override
                public void onTransitionResume(Transition transition) {

                }
            });
        }
    }

    private String populateFields(RecipeEntry recipe){
        //populate fields

        ((TextView) findViewById(R.id.title)).setText(recipe.title);
        ((TextView) findViewById(R.id.actionBarTitle)).setText(recipe.title);
        ((TextView) findViewById(R.id.type)).setText(recipe.type);
        ((TextView) findViewById(R.id.cookTime)).setText(getResources().getString(R.string.main_page_cookTime, recipe.cookTime));

        LinearLayout ingredients_list = (LinearLayout) findViewById(R.id.ingredients_list);
        for(int i = 0; i < recipe.ingredients.size(); i++){
            TextView entry = new TextView(this);
            entry.setText(" · " + recipe.ingredients.get(i));
                    entry.setLayoutParams(new AbsListView.LayoutParams(
                            AbsListView.LayoutParams.WRAP_CONTENT,
                            AbsListView.LayoutParams.WRAP_CONTENT));
            entry.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
            entry.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.abc_text_size_medium_material));
            ingredients_list.addView(entry);
        }

        LinearLayout steps = (LinearLayout) findViewById(R.id.steps);
        for(int i = 0; i < recipe.steps.size(); i++){
            View step = LayoutInflater.from(this).inflate(R.layout.recipe_details_step, steps, false);
            ((TextView) step.findViewById(R.id.step_number)).setText(getResources().getString(R.string.step_number, i+1));
            ((TextView) step.findViewById(R.id.instruction)).setText(recipe.steps.get(i));

            steps.addView(step);
        }

        return recipe.title;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
