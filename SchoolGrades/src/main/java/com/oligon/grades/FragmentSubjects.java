package com.oligon.grades;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class FragmentSubjects extends SherlockListFragment {

    public final static String KEY_DIALOG_TITLE = "KEY_DIALOG_TITLE";
    private static SimpleCursorAdapter mAdapter;
    private static Cursor cursor;
    private static String mOrder;

    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mAdapter.changeCursor(cursor);
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_subjects, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mOrder = Database.KEY_COUNT + " DESC";
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.card_list_item, cursor, new String[]{Database.KEY_SUBJECT, Database.KEY_COLOR, Database.KEY_CURRENT_AVERAGE}, new int[]{R.id.text, R.id.color, R.id.subject_current_average}, 0);
        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (columnIndex == 2) {
                    view.findViewById(R.id.color).setBackgroundColor(cursor.getInt(2));
                    return true;
                }
                return false;
            }
        });
        setListAdapter(mAdapter);
        updateContent();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Bundle b = new Bundle();
        b.putString(KEY_DIALOG_TITLE, ((Cursor) mAdapter.getItem(position)).getString(1));
        SherlockDialogFragment dialog = new FragmentSubjectDialog();
        dialog.setArguments(b);
        dialog.show(getFragmentManager(), "SubjectDialog");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sort) {
            new AlertDialog.Builder(getActivity()).setTitle(R.string.dialog_sort_title)
                    .setItems(R.array.sort_types, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    mOrder = Database.KEY_COUNT + " DESC";
                                    break;
                                case 1:
                                    mOrder = Database.KEY_SUBJECT;
                                    break;
                                case 2:
                                    mOrder = Database.KEY_CURRENT_AVERAGE + " DESC";
                                    break;
                            }
                            updateContent();
                            dialog.dismiss();
                        }

                    }).create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_add_exam).setVisible(false);
    }

    public static void updateContent() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                cursor = ActivityMain.db.getSubjectCursor(mOrder);
                handler.sendEmptyMessage(0);
            }
        }).start();
    }
}
