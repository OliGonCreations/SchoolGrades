package com.oligon.grades;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class FragmentSubjects extends SherlockListFragment {

    public final static String KEY_DIALOG_TITLE = "KEY_DIALOG_TITLE";
    public static Database db;
    private static BaseAdapter myListAdapter;
    static String[] subjects;
    private static FragmentSubjects ctx;
    private static Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_subjects, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ctx = this;
        context = getActivity();
        db = new Database(getActivity());
        subjects = db.getAllSubjectTitles();
        myListAdapter = new CustomAdapter(getActivity(), subjects, db.getAllSubjectColors(), db.getAllCurrentAverages());
        setListAdapter(myListAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Bundle b = new Bundle();
        b.putString(KEY_DIALOG_TITLE, subjects[position]);
        SherlockDialogFragment dialog = new FragmentSubjectDialog();
        dialog.setArguments(b);
        dialog.show(getFragmentManager(), "SubjectDialog");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sort) {
            Toast.makeText(getActivity(), "Sort", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_add_exam).setVisible(false);
    }

    public static void updateContent() {
        myListAdapter = new CustomAdapter(context, subjects, db.getAllSubjectColors(), db.getAllCurrentAverages());
        ctx.setListAdapter(myListAdapter);
    }

    public static class CustomAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private String[] strings, currentAverages;
        private int[] colors;

        class ViewHolder {
            public TextView text, currentAverage;
            public ImageView color;
        }

        public CustomAdapter(Context context, String[] strings, int[] colors, String[] currentAverages) {
            this.strings = strings;
            this.colors = colors;
            this.currentAverages = currentAverages;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return strings.length;
        }

        @Override
        public Object getItem(int position) {
            return strings[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null) {
                rowView = inflater.inflate(R.layout.card_list_item, null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.text = (TextView) rowView.findViewById(R.id.text);
                viewHolder.currentAverage = (TextView) rowView.findViewById(R.id.subject_current_average);
                viewHolder.color = (ImageView) rowView.findViewById(R.id.color);
                rowView.setTag(viewHolder);
            }
            ViewHolder holder = (ViewHolder) rowView.getTag();
            holder.text.setText(strings[position]);
            holder.currentAverage.setText(currentAverages[position]);
            holder.color.setBackgroundColor(colors[position]);
            return rowView;
        }
    }
}
