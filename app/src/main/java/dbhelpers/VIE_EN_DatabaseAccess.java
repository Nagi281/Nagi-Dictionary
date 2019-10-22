package dbhelpers;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import model.Word;

public class VIE_EN_DatabaseAccess {
    public final static String DB_NAME_VA  = "viet_anh.db";
    private final static String DB_NAME = "viet_anh";
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static VIE_EN_DatabaseAccess instance;
    private VIE_EN_DatabaseAccess(Context context){
        this.openHelper = new DatabaseOpenHelper(context,DB_NAME_VA );
    }
    public static VIE_EN_DatabaseAccess getInstance(Context context){
        if(instance == null){
            instance = new VIE_EN_DatabaseAccess(context);
        }
        return instance;
    }

    public ArrayList<Word> getWordsOffset(int id, int offset){
        openDB();
        ArrayList<Word> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("select * from " + DB_NAME  +
                " WHERE id >= "+ id + " limit " + offset,null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            list.add(new Word(cursor.getInt(0),
                    cursor.getString(1),cursor.getString(2)));
            cursor.moveToNext();
        }
        cursor.close();
        closeDB();
        return list;
    }

    public void openDB(){
        this.database = openHelper.getWritableDatabase();
    }

    public void closeDB(){
        if(database!=null){
            this.database.close();
        }
    }

    public ArrayList<String> getWordsStartWith(String str) {
        openDB();
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("select * from " + DB_NAME  +
                " WHERE word like "+"'" + str + "%'" + " limit 10",null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            list.add(cursor.getString(1));
            cursor.moveToNext();
        }
        cursor.close();
        closeDB();
        return list;
    }
    public Word getWordByName(String word) {
        try {
            openDB();
            Cursor cursor = database.rawQuery("select * from " + DB_NAME 
                    + " where word = " +  "'" +word + "'",null);
            cursor.moveToFirst();
            Word word1 = new Word(cursor.getInt(0),
                    cursor.getString(1),cursor.getString(2));
            cursor.close();
            closeDB();
            return  word1;
        } catch (Exception err) {
            return null;
        }
    }

    public Word getWordsById(int wordId) {
        openDB();
        Word word = new Word();
        Cursor cursor = database.rawQuery("Select * from " + DB_NAME   +
                " where id = " + wordId,null);
        cursor.moveToFirst();
        word.setId(cursor.getInt(0));
        word.setName(cursor.getString(1));
        word.setContent(cursor.getString(2));
        word.insertScriptForHref();
        cursor.close();
        closeDB();
        return word;
    }
}
