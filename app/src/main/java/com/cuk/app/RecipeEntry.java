package com.cuk.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeEntry implements Serializable{
    public int id;
    public String title;
    public String type;
    public int cookTime;  //minutes
    public List<String> ingredients;
    public List<String> steps;

    RecipeEntry(int id, String title, String type, int cookTime, String[] ingredients, String[] steps){
        this.id = id;
        this.title = title;
        this.type = type;
        this.cookTime = cookTime;
        this.ingredients = Arrays.asList(ingredients);
        this.steps = Arrays.asList(steps);

        /*
        try {
            JSONObject prop = new JSONObject(propJSON);

            this.type = prop.getString("type");
            this.cookTime = prop.getInt("cookTime");

            this.ingredients = new ArrayList<>();
            JSONArray ingredients = prop.getJSONArray("ingredients");
            for(int i = 0; i < ingredients.length(); i++){
                this.ingredients.add(ingredients.getString(i));
            }

            this.steps = new ArrayList<>();
            JSONArray steps = prop.getJSONArray("steps");
            for(int i = 0; i < steps.length(); i++){
                this.steps.add(steps.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        */
    }
}
