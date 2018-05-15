package com.kk.android.dayonejournal;

import java.util.Calendar;

public class CalendarDate {
    private Calendar mCalendar;
    public void setDate(Journal journal){
        mCalendar = Calendar.getInstance();
        int year = mCalendar.get(mCalendar.YEAR);
        int month = mCalendar.get(mCalendar.MONTH)+1;
        int day = mCalendar.get(mCalendar.DAY_OF_MONTH);
        journal.setYear(year);
        journal.setMonth(month);
        journal.setDay(day);
    }
}
