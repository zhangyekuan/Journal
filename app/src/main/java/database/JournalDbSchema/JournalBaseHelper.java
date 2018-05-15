package database.JournalDbSchema;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import database.JournalDbSchema.JournalDbSchema.JournalTable;

public class JournalBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "journalBase.db";
    public JournalBaseHelper(Context context){
        super(context,DATABASE_NAME,null,VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + JournalTable.NAME + "(" +
        " _id integer primary key autoincrement," +
        JournalTable.Cols.UUID + ", " +
        JournalTable.Cols.TITLE + ", "+
        JournalTable.Cols.YEAR + ", " +
        JournalTable.Cols.MONTH + ", " +
        JournalTable.Cols.DAY +", " +
        JournalTable.Cols.CONTENT +"," +
        JournalTable.Cols.PATH + "," +
        JournalTable.Cols.LOCATION +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
