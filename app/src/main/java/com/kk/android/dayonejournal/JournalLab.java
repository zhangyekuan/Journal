package com.kk.android.dayonejournal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import database.JournalDbSchema.JournalBaseHelper;
import database.JournalDbSchema.JournalCursorWrapper;
import database.JournalDbSchema.JournalDbSchema.JournalTable;

public class JournalLab {
    private static JournalLab sJournalLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;
    public static JournalLab get(Context context){
        if(sJournalLab == null){
            sJournalLab = new JournalLab(context);
        }
        return sJournalLab;
    }
    private JournalLab(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new JournalBaseHelper(mContext)
                .getWritableDatabase();
    }
    public void addJournal(Journal j){
        ContentValues values = getContentValues(j);
        mDatabase.insert(JournalTable.NAME,null,values);
    }
    public void deleteJournal(Journal j){
        mDatabase.delete(JournalTable.NAME,
                "uuid == ?",new String[]{j.getId().toString()});
    }
    public List<Journal> getJournals(){
       List<Journal> journals = new ArrayList<>();
       JournalCursorWrapper cursor = queryJournals(null,null);
       try{cursor.moveToFirst();
       while(!cursor.isAfterLast()){
           journals.add(cursor.getJournal());
           cursor.moveToNext();
            }
       }finally {
           cursor.close();
       }
       return journals;
    }
    public Journal getJournal(UUID id){
        JournalCursorWrapper cursor = queryJournals(
                JournalTable.Cols.UUID + " =?",
                new String[] {id.toString()}
        );
        try{
            if(cursor.getCount() == 0){
                return null;
            }
            cursor.moveToFirst();
            return cursor.getJournal();
        }finally {
            cursor.close();
        }
    }
    private static ContentValues getContentValues(Journal journal){
        ContentValues values = new ContentValues();
        values.put(JournalTable.Cols.UUID,journal.getId().toString());
        values.put(JournalTable.Cols.TITLE,journal.getTitle());
        values.put(JournalTable.Cols.CONTENT,journal.getContent());
        values.put(JournalTable.Cols.YEAR,journal.getYear());
        values.put(JournalTable.Cols.MONTH,journal.getMonth());
        values.put(JournalTable.Cols.DAY,journal.getDay());
        values.put(JournalTable.Cols.PATH,journal.getPath());
        values.put(JournalTable.Cols.LOCATION,journal.getLocation());
        return values;
    }
    public void updateJournal(Journal journal){
        String uuidString = journal.getId().toString();
        ContentValues values = getContentValues(journal);
        mDatabase.update(JournalTable.NAME,values,
                JournalTable.Cols.UUID + " = ?",
                new String[] {uuidString});
    }
    private JournalCursorWrapper queryJournals(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                JournalTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new JournalCursorWrapper(cursor);
    }
    public File getPhotoFile(Journal journal){
        File filesDir = mContext.getFilesDir();
        return new File(filesDir,journal.getPhotoFilename());
    }
}
