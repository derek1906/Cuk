package com.cuk.app;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;

public class DatabaseManager{
    private Context ctx;
    public SQLiteDatabase db;

    public DatabaseManager(Context ctx, onCreateDatabase... callback) {
        this.ctx = ctx;

        if (!ctx.getDatabasePath("cukApp").exists()) {
            //alert
            if(callback.length == 1){
                callback[0].exec();
            }else if(callback.length > 1){
                throw new IllegalArgumentException("Too many callbacks");
            }
            //create
            db = ctx.openOrCreateDatabase("cukApp", Context.MODE_PRIVATE, null);
            //db.execSQL("CREATE TABLE IF NOT EXISTS recipes(_id INTEGER PRIMARY KEY AUTOINCREMENT, name NVARCHAR, prop NVARCHAR);");
            TableManager.createTable(db, "recipes",
                    new String[]{"name", "NVARCHAR"},
                    new String[]{"type", "NVARCHAR"},
                    // TODO: add style
                    new String[]{"cookTime", "int"},
                    new String[]{"ingredients", "NVARCHAR"},
                    new String[]{"steps", "NVARCHAR"}
            );
            TableManager.createTable(db, "stored_entries",
                    new String[]{"entry_id", "int"}
            );
            new TableManager(db, "recipes").createSampleData();

        } else {
            db = ctx.openOrCreateDatabase("cukApp", Context.MODE_PRIVATE, null);
        }

    }

    public TableManager table(String name) {
        return new TableManager(db, name);
    }

    public void close() {
        db.close();
    }

    interface onCreateDatabase{
        void exec();
    }
}

class TableManager {
    SQLiteDatabase db;
    String table;

    public TableManager(SQLiteDatabase db, String table) {
        this.db = db;
        this.table = table;
    }

    public static void createTable(SQLiteDatabase db, String name, boolean createID, String[]... pairs) {
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(name).append("(");

        if(createID){
            sql.append("_id INTEGER PRIMARY KEY AUTOINCREMENT,");
        }

        String comma = "";
        for (String[] pair : pairs) {
            sql.append(comma);
            comma = ",";
            sql.append(pair[0]).append(" ").append(pair[1]);
        }
        sql.append(");");

        db.execSQL(sql.toString());
    }
    //Default to create _id
    static void createTable(SQLiteDatabase db, String name, String[]... pairs){
        TableManager.createTable(db, name, true, pairs);
    }

    public boolean checkIfValueExists(String col, String val) {
        return db.query(table, new String[]{col}, col + "=" + val, null, null, null, null).getCount() > 0;
    }

    public void query(String[] columns, String selection, String[] selectionArgs, DataLooper looper){
        Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
        cursor.moveToPosition(-1);
        while(cursor.moveToNext()){
            looper.exec(cursor, new Get(cursor));
        }
        cursor.close();

    }
    public void query(String[] columns, DataLooper looper){
        this.query(columns, null, null, looper);
    }

    public void insert(ContentValues contents) {
        db.insert(table, null, contents);
    }

    public void delete(String selection, String[] selectionArgs) {
        db.delete(table, selection, selectionArgs);
    }

    public void delete(String selection, int selectionArg) {
        delete(selection + "=?", new String[]{"" + selectionArg});
    }

    public void createSampleData() {
        insert(new SampleRecipe(
                "Corn and Cheese Chowder",
                "Dinner",
                20,
                new String[]{"Flour", "Egg"},
                new String[]{"One", "Two", "Three"}
        ).getContent());

        insert(new SampleRecipe(
                "Another Dish",
                "Lunch",
                10,
                new String[]{"Banana", "Apple"},
                new String[]{"One", "Two", "Three"}
        ).getContent());

        insert(new SampleRecipe(
                "Foo Bar",
                "Breakfast",
                30,
                new String[]{"Expensive Stuff", "Hundred Dollar Bills"},
                new String[]{"One", "Two", "Three"}
        ).getContent());
        /*
        String[] names = {"Corn and Cheese Chowder", "Another Dish", "Foo bar"},
                props = {
                        "{" +
                                "'type':'Lunch'," +
                                "'ingredients':['Flour','Egg']," +
                                "'cookTime': 20," +
                                "'steps':[" +
                                "'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla eleifend condimentum ligula quis ultrices.'," +
                                "'Maecenas elit ipsum, vehicula et blandit a, pretium commodo nisl. Vivamus laoreet porta accumsan.'," +
                                "'Pellentesque et metus id justo tristique euismod nec eget eros. Proin at quam eget nunc laoreet auctor.'," +
                                "'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla eleifend condimentum ligula quis ultrices.'," +
                                "'Maecenas elit ipsum, vehicula et blandit a, pretium commodo nisl. Vivamus laoreet porta accumsan.'," +
                                "'Pellentesque et metus id justo tristique euismod nec eget eros. Proin at quam eget nunc laoreet auctor.'" +
                                "]}",
                        "{" +
                                "'type':'Lunch'," +
                                "'ingredients':['Flour','Egg']," +
                                "'cookTime': 20," +
                                "'steps':[" +
                                "'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla eleifend condimentum ligula quis ultrices.'," +
                                "'Maecenas elit ipsum, vehicula et blandit a, pretium commodo nisl. Vivamus laoreet porta accumsan.'," +
                                "'Pellentesque et metus id justo tristique euismod nec eget eros. Proin at quam eget nunc laoreet auctor.'," +
                                "'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla eleifend condimentum ligula quis ultrices.'," +
                                "'Maecenas elit ipsum, vehicula et blandit a, pretium commodo nisl. Vivamus laoreet porta accumsan.'," +
                                "'Pellentesque et metus id justo tristique euismod nec eget eros. Proin at quam eget nunc laoreet auctor.'" +
                                "]}",
                        "{" +
                                "'type':'Breakfast'," +
                                "'ingredients':['Lorem','Ipsum']," +
                                "'cookTime': 30," +
                                "'steps':[" +
                                "'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla eleifend condimentum ligula quis ultrices.'," +
                                "'Maecenas elit ipsum, vehicula et blandit a, pretium commodo nisl. Vivamus laoreet porta accumsan.'," +
                                "'Pellentesque et metus id justo tristique euismod nec eget eros. Proin at quam eget nunc laoreet auctor.'," +
                                "'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla eleifend condimentum ligula quis ultrices.'," +
                                "'Maecenas elit ipsum, vehicula et blandit a, pretium commodo nisl. Vivamus laoreet porta accumsan.'," +
                                "'Pellentesque et metus id justo tristique euismod nec eget eros. Proin at quam eget nunc laoreet auctor.'" +
                                "]}"
                };

        for (int i = 0; i < names.length; i++) {
            ContentValues contents = new ContentValues();
            contents.put("name", names[i]);
            contents.put("prop", props[i]);
            insert(contents);
        }
        */
    }

    interface DataLooper{
        void exec(Cursor cursor, Get get);
    }

    //get column from cursor
    class Get{
        Cursor cursor;
        Get(Cursor cur){ this.cursor = cur;}
        int Int(String col){
            return cursor.getInt(cursor.getColumnIndex(col));
        }
        String String(String col){
            return cursor.getString(cursor.getColumnIndex(col));
        }
        String[] StringArray(String col){
            String[] stringArray = {""};
            try {
                JSONArray array = new JSONArray(cursor.getString(cursor.getColumnIndex(col)));
                stringArray = new String[array.length()];
                for(int i = 0; i < stringArray.length; i++){
                    stringArray[i] = array.getString(i);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return stringArray;
        }
    }
}

class SampleRecipe{
    String name, type;
    int cookTime;
    String[] ingredients, steps;

    public SampleRecipe(String name, String type, int cookTime, String[] ingredients, String[] steps){
        this.name = name;
        this.type = type;
        this.cookTime = cookTime;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public ContentValues getContent(){
        ContentValues contents = new ContentValues();
        contents.put("name", name);
        contents.put("type", type);
        contents.put("cookTime", cookTime);
        contents.put("ingredients", new JSONArray(Arrays.asList(ingredients)).toString());
        contents.put("steps", new JSONArray(Arrays.asList(steps)).toString());

        return contents;
    }
}