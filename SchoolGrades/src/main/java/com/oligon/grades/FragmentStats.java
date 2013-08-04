package com.oligon.grades;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;

public class FragmentStats extends SherlockFragment {

    ProgressBar mProgress;
    TextView mProgressText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgress = (ProgressBar) view.findViewById(R.id.stats_progress_bar);
        mProgressText = (TextView) view.findViewById(R.id.stats_progress_text);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume(){
        super.onResume();
        new ProgressTask().execute();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_add_exam).setVisible(false);
        menu.findItem(R.id.action_sort).setVisible(false);
    }

    private class ProgressTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            int progress = 0;
            int[] grades;
            for (String subject : ActivityMain.db.getAllSubjectTitles()) {
                grades = ActivityMain.db.getGradesS(subject);
                for (int i = 0; i < grades.length; i++) {
                    if (grades[i] != -1)
                        progress++;
                    publishProgress(progress);
                    try {
                        Thread.sleep(80);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mProgress.setProgress(values[0]);
            mProgressText.setText(values[0] + " / 40");
        }
    }

}
