package com.oligon.grades;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;

import java.util.Calendar;

public class FragmentAbitur extends SherlockFragment {

    CalendarView calendarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        calendarView = new CalendarView(getActivity());
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);
        calendarView.setSelectedDateVerticalBar(R.drawable.background_card);
        calendarView.setSelectedWeekBackgroundColor(Color.parseColor("#22000000"));
        return calendarView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_add_exam).setVisible(false);
        menu.findItem(R.id.action_sort).setVisible(false);
    }

}
