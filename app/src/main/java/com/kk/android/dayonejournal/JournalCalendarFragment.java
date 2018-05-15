package com.kk.android.dayonejournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;


public class JournalCalendarFragment extends Fragment {
    private CalendarView mCalendarView;
    private FloatingActionButton mFloatingActionButton;
    private static int sYear;
    private static int sMonth;
    private static int sDay;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_date,null);
        mCalendarView = (CalendarView)view
                .findViewById(R.id.calender_date);
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView,
                                            int year, int month, int day) {
                Toast.makeText(getActivity(),
                        "你选择了"+year+"年"+(month+1)+"月"+day+"日"
                        , Toast.LENGTH_SHORT).show();
                 sYear = year;
                 sMonth = month;
                 sDay = day;
            }
        });
        mFloatingActionButton = (FloatingActionButton)view.findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Journal journal = new Journal();
                journal.setYear(sYear);
                journal.setMonth(sMonth+1);
                journal.setDay(sDay);
                JournalLab.get(getActivity()).addJournal(journal);
                EventBus.getDefault().post(new MessageEvent(journal.getId()));
                Intent intent = JournalPagerActivity.newIntent(getActivity(),journal.getId());
                startActivity(intent);
            }
        });
        return view;
    }
}
