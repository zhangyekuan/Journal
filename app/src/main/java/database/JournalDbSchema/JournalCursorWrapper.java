package database.JournalDbSchema;
import database.JournalDbSchema.JournalDbSchema.JournalTable;
import android.database.Cursor;
import android.database.CursorWrapper;

import com.kk.android.dayonejournal.Journal;

import java.util.UUID;

public class JournalCursorWrapper extends CursorWrapper {
    public JournalCursorWrapper(Cursor cursor){
        super(cursor);
    }
    public Journal getJournal(){
        String uuidString = getString(getColumnIndex(JournalTable.Cols.UUID));
        String title = getString(getColumnIndex(JournalTable.Cols.TITLE));
        String content = getString(getColumnIndex(JournalTable.Cols.CONTENT));
        int year = getInt(getColumnIndex(JournalTable.Cols.YEAR));
        int month = getInt(getColumnIndex(JournalTable.Cols.MONTH));
        int day = getInt(getColumnIndex(JournalTable.Cols.DAY));
        String path = getString(getColumnIndex(JournalTable.Cols.PATH));
        String location = getString(getColumnIndex(JournalTable.Cols.LOCATION));

        Journal journal = new Journal(UUID.fromString(uuidString));
        journal.setTitle(title);
        journal.setContent(content);
        journal.setYear(year);
        journal.setMonth(month);
        journal.setDay(day);
        journal.setPath(path);
        journal.setLocation(location);
        return journal;
    }
}
