package com.example.textreg2.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {
    private String DATABASE_PATH = null;
    private static final String DATABASE_NAME = "sd.db";
    private SQLiteDatabase mDatabase;
    private final Context mContext;
    private boolean mNeedUpdate = false;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.mContext = context;
        this.DATABASE_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";
        Log.i("DATABASE_PATH", DATABASE_PATH);

        mNeedUpdate = false;
    }

    public void updateDataBase() throws IOException {
        if (mNeedUpdate) {
            File dbFile = new File(DATABASE_PATH + DATABASE_NAME);
            if (dbFile.exists())
                dbFile.delete();

            copyDatabase();

            mNeedUpdate = false;
        }
    }

    public boolean checkDataBase() {

        SQLiteDatabase checkDB = null;
        try {
            String myPath = DATABASE_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {

        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;

    }

    public void copyDatabase() {
        if (!checkDataBase()) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDBFile();
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    private void copyDBFile() throws IOException {

        InputStream mInput = mContext.getAssets().open(DATABASE_NAME);
        OutputStream mOutput = new FileOutputStream(DATABASE_PATH + DATABASE_NAME);
        byte[] mBuffer = new byte[128];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0)
            mOutput.write(mBuffer, 0, mLength);

        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    public boolean openDataBase() throws SQLException {
        mDatabase = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE);
        return mDatabase != null;
    }

    @Override
    public synchronized void close() {
        if (mDatabase != null)
            mDatabase.close();
        super.close();
    }

    public Cursor getMeaning(String text, int type)
    {
        Cursor cur=null;
        if (type==1) //en - vi and en-en
            cur = getEnVNMeaning(text);
        else if (type == 2)
            cur = getVnEnMeaning(text);
        else if (type == 3)
            cur = getEnEnMeaning(text);
        return cur;
    }

    public Cursor getSuggestions(String text, int type)
    {
        Cursor cur=null;
        if (type == 1||type==3) //en - vi and en-en
            cur = getEnSuggestions(text);
        else if (type == 2)
            cur = getVNSuggestions(text);
        return cur;
    }

    private Cursor getEnVNMeaning(String text)
    {
        Cursor c= mDatabase.rawQuery("SELECT _id, vn_description ,pronounce, example, synonym, antonym  FROM engword WHERE word==LOWER('"+text+"')",null);
        return c;
    }

    private Cursor getVnEnMeaning(String text)
    {
        Cursor c= mDatabase.rawQuery("SELECT _id, description FROM vietword WHERE word==LOWER('"+text+"')",null);
        return c;
    }

    private Cursor getEnEnMeaning(String text)
    {
        Cursor c= mDatabase.rawQuery("SELECT _id, en_description ,pronounce, example, synonym, antonym  FROM engword WHERE word==LOWER('"+text+"')",null);
        return c;
    }

    private Cursor getEnSuggestions(String text)
    {
        Cursor c= mDatabase.rawQuery("SELECT _id, word FROM engword WHERE word LIKE '"+text+"%' LIMIT 40",null);
        return c;
    }

    private Cursor getVNSuggestions(String text)
    {
        Cursor c= mDatabase.rawQuery("SELECT _id, word FROM vietword WHERE word LIKE '"+text+"%' LIMIT 40",null);
        return c;
    }

    public void editNote(String w, int type, String text)
    {
        Cursor c = getNote(w,type);

        if (c.moveToFirst()) {
            if (text==null || text.isEmpty())
                deleteNote(w,type);
            else
                updateNote(w,type,text);
        }
        else {
            insertNote(w,type,text);
        }
    }

    public Cursor getNote(String w, int wtype)
    {
        Cursor c = null;
        c = mDatabase.rawQuery("SELECT word, wnote FROM note WHERE word==LOWER('"+w+"') and type = '"+wtype+"'",null);
        return c;
    }

    private void insertNote(String w, int typ, String text) {
        mDatabase.execSQL("INSERT INTO note VALUES ('"+w+"','"+typ+"','"+text+"')");
    }

    private void deleteNote(String w, int typ)
    {
        mDatabase.execSQL("DELETE FROM note WHERE word = '"+w+"' and type = '"+typ+"'");
    }

    public void updateNote(String w, int typ, String text)
    {
        mDatabase.execSQL("UPDATE note SET wnote = '"+text+"' WHERE word = '"+w+"' and type = '"+typ+"'");
    }

    @Override
    public void onCreate(SQLiteDatabase db){ }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            mNeedUpdate = true;

        try {
            this.updateDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
