package dbhelpers;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.nagidictionary.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.Word;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;
    private static String dataTable;
    private static int databaseSize = 350000;

    private DatabaseAccess(Context context, String dataTable) {
        this.openHelper = new DatabaseOpenHelper(context, dataTable + ".db");
        DatabaseAccess.dataTable = dataTable;
    }

    public static DatabaseAccess getInstance(Context context, String dataTable) {
        if (instance == null) {
            instance = new DatabaseAccess(context, dataTable);
        } else {
            if (!DatabaseAccess.dataTable.equals(dataTable)) {
                instance = new DatabaseAccess(context, dataTable);
            }
        }
        return instance;
    }

    public ArrayList<String> getWRds() {
        openDB();
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("Select * from " + dataTable, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(cursor.getString(1));
            cursor.moveToNext();
        }
        cursor.close();
        closeDB();
        return list;
    }

    public ArrayList<Word> getWordsOffset(int id, int offset) {
        openDB();
        ArrayList<Word> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("select * from " + dataTable +
                " WHERE id >= " + id + " limit " + offset, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(new Word(cursor.getInt(0),
                    cursor.getString(1), cursor.getString(2)));
            cursor.moveToNext();
        }
        cursor.close();
        closeDB();
        return list;
    }

    public ArrayList<Word> getListRandomForFlashCard(int offset) {
        openDB();
        int id;
        Random rand = new Random();
        id = rand.nextInt(databaseSize);
        ArrayList<Word> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("select * from " + dataTable +
                " WHERE id >= " + id + " limit " + offset, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(new Word(cursor.getInt(0),
                    cursor.getString(1), cursor.getString(2)));
            cursor.moveToNext();
        }
        cursor.close();
        closeDB();
        return list;
    }

    public Word getWordsById(int id) {
        openDB();
        Word word = new Word();
        Cursor cursor = database.rawQuery("Select * from " + dataTable +
                " where id = " + id, null);
        cursor.moveToFirst();
        word.setId(cursor.getInt(0));
        word.setName(cursor.getString(1));
        word.setContent(cursor.getString(2));
        word.insertScriptForHref();
        cursor.close();
        closeDB();
        return word;
    }

    public String getDefinition(String word) {
        String definition = " ";
        Cursor cursor = database.rawQuery("Select * from anh_viet " +
                "where word = '" + word + "'", null);
        cursor.moveToFirst();
        definition = cursor.getString(2);
        cursor.close();
        return definition;
    }

    public void openDB() {
        this.database = openHelper.getWritableDatabase();
    }

    public void closeDB() {
        if (database != null) {
            this.database.close();
        }
    }

    public ArrayList<String> getWordsStartWith(String str) {
        ArrayList<String> list = new ArrayList<>();
        try {
            openDB();
            Cursor cursor = database.rawQuery("select * from " + dataTable +
                    " WHERE word like " + "'" + str + "%'" + " limit 30", null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(cursor.getString(1));
                cursor.moveToNext();
            }
            cursor.close();
            closeDB();
        } catch (IllegalStateException err) {
            err.printStackTrace();
        }
        return list;
    }

    public Word getWordByName(String word) {
        try {
            openDB();
            Cursor cursor = database.rawQuery("select * from " + dataTable
                    + " where word = " + "\"" + word.trim() + "\"", null);
            cursor.moveToFirst();
            Word word1 = new Word(cursor.getInt(0),
                    cursor.getString(1), cursor.getString(2));
            cursor.close();
            closeDB();
            return word1;
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }

    public ArrayList<Word> getAllWords() {
        openDB();
        ArrayList<Word> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("select * from " + dataTable, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(new Word(cursor.getInt(0),
                    cursor.getString(1), cursor.getString(2)));
            cursor.moveToNext();
        }
        cursor.close();
        closeDB();
        return list;
    }

    public List<Word> getAllFavoritesWord() {
        openDB();
        ArrayList<Word> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("select * from " + dataTable + " a inner join "
                + MainActivity.FAVORITE + " b on a.id=b.id", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(new Word(cursor.getInt(0),
                    cursor.getString(1), cursor.getString(2)));
            cursor.moveToNext();
        }
        cursor.close();
        closeDB();
        return list;
    }

    public boolean isLiked(int wordId) {
        openDB();
        boolean result = false;
        Cursor cursor = database.rawQuery("select * from " + MainActivity.FAVORITE
                + " where id = " + wordId, null);
        if (cursor.getCount() == 1) {
            result = true;
        }
        cursor.close();
        closeDB();
        return result;
    }

    public boolean addIntoFavorite(int wordId) {
        boolean result = false;
        try {
            openDB();
            ContentValues value = new ContentValues();
            value.put("id", wordId);
            database.insert(MainActivity.FAVORITE, null, value);
            closeDB();
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return result;
        }
    }

    public List<Word> getAllOfYourWords() {
        openDB();
        ArrayList<Word> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("select * from " + MainActivity.YOUR_WORD, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(new Word(cursor.getInt(0),
                    cursor.getString(1), cursor.getString(2)));
            cursor.moveToNext();
        }
        cursor.close();
        closeDB();
        return list;
    }

    public boolean addNewWordIntoYours(Word word) {
        boolean result = false;
        try {
            openDB();
            ContentValues value = new ContentValues();
            value.put("word", word.getName());
            value.put("content", word.getContent());
            database.insert(MainActivity.YOUR_WORD, null, value);
            closeDB();
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return result;
        }
    }

    public boolean editAWord(Word word) {
        boolean result = false;
        try {
            openDB();
            ContentValues value = new ContentValues();
            value.put("word", word.getName());
            value.put("content", word.getContent());
            database.update(MainActivity.YOUR_WORD, value, " id = " + word.getId(), null);
            closeDB();
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return result;
        }
    }

    public boolean deleteWord(int id) {
        boolean result = false;
        try {
            openDB();
            database.delete(MainActivity.YOUR_WORD, " id = " + id, null);
            closeDB();
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return result;
        }
    }

    public boolean cancelLikeAWord(int id) {
        boolean result = false;
        try {
            openDB();
            database.delete(MainActivity.FAVORITE, " id = " + id, null);
            closeDB();
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return result;
        }
    }
}
