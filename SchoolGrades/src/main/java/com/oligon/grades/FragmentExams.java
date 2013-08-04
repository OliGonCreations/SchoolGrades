package com.oligon.grades;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.cocosw.undobar.UndoBarController;
import com.doomonafireball.betterpickers.datepicker.DatePickerBuilder;
import com.doomonafireball.betterpickers.datepicker.DatePickerDialogFragment;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class FragmentExams extends SherlockListFragment implements PopupMenu.OnMenuItemClickListener, AdapterView.OnItemLongClickListener, UndoBarController.UndoListener {

    private static SimpleCursorAdapter mAdapter;
    private static Cursor cursor;
    private static String mDeleteSubject, mDeleteDate;
    private static Calendar calendar = Calendar.getInstance();

    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mAdapter.changeCursor(cursor);
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.card_list_item_exam, cursor, new String[]{Database.KEY_SUBJECT, Database.KEY_DATE, Database.KEY_COLOR}, new int[]{R.id.title, R.id.exam_date, R.id.exam_color}, 0);
        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (columnIndex == 2) {
                    Calendar c = Calendar.getInstance();
                    try {
                        c.setTime(Database.sdf.parse(cursor.getString(2)));
                    } catch (ParseException e) {
                        return false;
                    }
                    ((TextView) view.findViewById(R.id.exam_date)).setText(DateFormat.getDateFormat(getActivity()).format(c.getTime()));
                    return true;
                }
                if (columnIndex == 4) {
                    view.findViewById(R.id.exam_color).setBackgroundColor(cursor.getInt(4));
                    return true;
                }
                return false;
            }
        });
        setListAdapter(mAdapter);
        updateList();
    }

    @Override
    public void onResume() {
        super.onResume();
        getListView().setOnItemLongClickListener(this);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_sort).setVisible(false);
    }

    public static void updateList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                cursor = ActivityMain.db.getExamCursor();
                handler.sendEmptyMessage(0);
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_exam) {
            SherlockDialogFragment dialog = new FragmentExamsAddDialog();
            dialog.show(getFragmentManager(), "AddExamDialog");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuItemClick(android.view.MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            Bundle b = new Bundle();
            b.putString(Database.KEY_SUBJECT, mDeleteSubject);
            b.putString(Database.KEY_DATE, mDeleteDate);
            UndoBarController.show(getActivity(), "Klausur entfernt", this, b);
            try {
                calendar.setTime(Database.sdf.parse(mDeleteDate));
            } catch (ParseException e) {
                return false;
            }
            ActivityMain.db.deleteExam(mDeleteSubject, calendar);
            updateList();
        }
        return false;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        PopupMenu popup = new PopupMenu(getActivity(), view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.action_exam);
        popup.show();
        mDeleteSubject = ((Cursor) mAdapter.getItem(position)).getString(1);
        mDeleteDate = ((Cursor) mAdapter.getItem(position)).getString(2);
        return false;
    }

    @Override
    public void onUndo(Parcelable token) {
        try {
            calendar.setTime(Database.sdf.parse(((Bundle) token).getString(Database.KEY_DATE)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ActivityMain.db.addExam(((Bundle) token).getString(Database.KEY_SUBJECT), calendar, "");
        updateList();
    }

    private static class FragmentExamsAddDialog extends SherlockDialogFragment implements DatePickerDialogFragment.DatePickerDialogHandler {

        Spinner spSubject, spDate;
        EditText etExtra;
        private int year, month, day;
        private boolean dateSet = false;
        Calendar c = Calendar.getInstance();

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_exam_add, null);
            ((TextView) view.findViewById(R.id.dialog_exam_title)).setText(getResources().getString(R.string.dialog_exam_add_title));
            spSubject = (Spinner) view.findViewById(R.id.dialog_exam_add_subject);
            spDate = (Spinner) view.findViewById(R.id.dialog_exam_add_date);
            spDate.setClickable(false);
            spDate.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    DatePickerBuilder npb = new DatePickerBuilder()
                            .setFragmentManager(getFragmentManager())
                            .setTargetFragment(FragmentExamsAddDialog.this)
                            .setStyleResId(R.style.CustomBetterPickerTheme).setYear(year);
                    if (dateSet)
                        npb.setMonthOfYear(month).setDayOfMonth(day);
                    npb.show();
                    return false;
                }
            });
            updateDate();
            etExtra = (EditText) view.findViewById(R.id.dialog_exam_add_extra);
            ArrayAdapter<String> subjects = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, Arrays.asList(ActivityMain.db.getAllSubjectTitles()));
            subjects.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spSubject.setAdapter(subjects);
            builder.setView(view)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            ActivityMain.db.addExam(spSubject.getSelectedItem().toString(), c, etExtra.getText().toString());
                            getDialog().dismiss();
                            updateList();
                        }
                    });
            return builder.create();
        }

        @Override
        public void onDialogDateSet(int reference, int year, int monthOfYear, int dayOfMonth) {
            this.year = year;
            this.month = monthOfYear;
            this.day = dayOfMonth;
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, day);
            updateDate();
            dateSet = true;
        }

        private void updateDate() {
            ArrayList<String> list = new ArrayList<String>();
            String m = new DateFormatSymbols().getMonths()[month];
            list.add(day + ". " + m + " " + year);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
            spDate.setAdapter(adapter);
        }
    }
}
