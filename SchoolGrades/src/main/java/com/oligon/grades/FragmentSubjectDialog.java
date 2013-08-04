package com.oligon.grades;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;
import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LinePoint;

import java.math.BigDecimal;

public class FragmentSubjectDialog extends SherlockDialogFragment implements NumberPickerDialogFragment.NumberPickerDialogHandler, View.OnClickListener, LineGraph.OnPointClickedListener, View.OnLongClickListener, PopupMenu.OnMenuItemClickListener {

    private static LineGraph graph;
    private static EditText grade1s, grade2s, grade3s, grade4s, grade1m, grade2m, grade3m, grade4m;
    private static TextView gradeAverage, gradeSAverage, gradeMAverage;
    private static String title;
    private static Resources res;
    private int mTerm = 0;
    private String mAttr;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        title = getArguments().getString(FragmentSubjects.KEY_DIALOG_TITLE);
        res = getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_subject, null);
        initialize(view);
        builder.setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        FragmentSubjects.updateContent();
                        getDialog().dismiss();
                    }
                });
        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateValues();
    }

    private void initialize(View view) {
        ((TextView) view.findViewById(R.id.dialog_subject_title)).setText(title);
        view.findViewById(R.id.dialog_subject_settings).setOnClickListener(this);
        graph = (LineGraph) view.findViewById(R.id.graph);
        graph.setOnPointClickedListener(this);
        gradeAverage = (TextView) view.findViewById(R.id.dialog_subject_current_average);
        gradeSAverage = (TextView) view.findViewById(R.id.dialog_subject_current_average_S);
        gradeMAverage = (TextView) view.findViewById(R.id.dialog_subject_current_average_M);
        gradeAverage.setText("-");
        grade1s = (EditText) view.findViewById(R.id.dialog_subject_gradeS_1);
        grade2s = (EditText) view.findViewById(R.id.dialog_subject_gradeS_2);
        grade3s = (EditText) view.findViewById(R.id.dialog_subject_gradeS_3);
        grade4s = (EditText) view.findViewById(R.id.dialog_subject_gradeS_4);
        grade1m = (EditText) view.findViewById(R.id.dialog_subject_gradeM_1);
        grade2m = (EditText) view.findViewById(R.id.dialog_subject_gradeM_2);
        grade3m = (EditText) view.findViewById(R.id.dialog_subject_gradeM_3);
        grade4m = (EditText) view.findViewById(R.id.dialog_subject_gradeM_4);
        grade1s.setBackground(res.getDrawable(R.drawable.edit_text_holo_orange));
        grade2s.setBackground(res.getDrawable(R.drawable.edit_text_holo_orange));
        grade3s.setBackground(res.getDrawable(R.drawable.edit_text_holo_orange));
        grade4s.setBackground(res.getDrawable(R.drawable.edit_text_holo_orange));
        grade1m.setBackground(res.getDrawable(R.drawable.edit_text_holo_light_blue));
        grade2m.setBackground(res.getDrawable(R.drawable.edit_text_holo_light_blue));
        grade3m.setBackground(res.getDrawable(R.drawable.edit_text_holo_light_blue));
        grade4m.setBackground(res.getDrawable(R.drawable.edit_text_holo_light_blue));
        grade1s.setClickable(true);
        grade2s.setClickable(true);
        grade3s.setClickable(true);
        grade4s.setClickable(true);
        grade1m.setClickable(true);
        grade2m.setClickable(true);
        grade3m.setClickable(true);
        grade4m.setClickable(true);
        grade1s.setOnClickListener(this);
        grade2s.setOnClickListener(this);
        grade3s.setOnClickListener(this);
        grade4s.setOnClickListener(this);
        grade1m.setOnClickListener(this);
        grade2m.setOnClickListener(this);
        grade3m.setOnClickListener(this);
        grade4m.setOnClickListener(this);
        grade1s.setOnLongClickListener(this);
        grade2s.setOnLongClickListener(this);
        grade3s.setOnLongClickListener(this);
        grade4s.setOnLongClickListener(this);
        grade1m.setOnLongClickListener(this);
        grade2m.setOnLongClickListener(this);
        grade3m.setOnLongClickListener(this);
        grade4m.setOnLongClickListener(this);
    }

    @Override
    public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber) {
        switch (reference) {
            case R.id.dialog_subject_gradeS_1:
                ActivityMain.db.updateGrade(title, 0, number, "s");
                break;
            case R.id.dialog_subject_gradeS_2:
                ActivityMain.db.updateGrade(title, 1, number, "s");
                break;
            case R.id.dialog_subject_gradeS_3:
                ActivityMain.db.updateGrade(title, 2, number, "s");
                break;
            case R.id.dialog_subject_gradeS_4:
                ActivityMain.db.updateGrade(title, 3, number, "s");
                break;
            case R.id.dialog_subject_gradeM_1:
                ActivityMain.db.updateGrade(title, 0, number, "m");
                break;
            case R.id.dialog_subject_gradeM_2:
                ActivityMain.db.updateGrade(title, 1, number, "m");
                break;
            case R.id.dialog_subject_gradeM_3:
                ActivityMain.db.updateGrade(title, 2, number, "m");
                break;
            case R.id.dialog_subject_gradeM_4:
                ActivityMain.db.updateGrade(title, 3, number, "m");
                break;
        }
        updateValues();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dialog_subject_settings) {
            SherlockDialogFragment dialog = new FragmentSubjectAllocationDialog();
            dialog.setArguments(getArguments());
            dialog.show(getFragmentManager(), "Allocation");
        } else {
            NumberPickerBuilder npb = new NumberPickerBuilder()
                    .setFragmentManager(getFragmentManager())
                    .setTargetFragment(FragmentSubjectDialog.this)
                    .setStyleResId(R.style.CustomBetterPickerTheme)
                    .setMaxNumber(15)
                    .setMinNumber(0)
                    .setPlusMinusVisibility(View.GONE)
                    .setDecimalVisibility(View.GONE)
                    .setReference(v.getId());
            npb.show();
        }
    }

    public static void updateValues() {
        new AsyncTask<Void, Integer, Void>() {

            int[] gradesS
                    ,
                    gradesM;
            BigDecimal average
                    ,
                    averageS
                    ,
                    averageM;

            @Override
            protected Void doInBackground(Void... params) {
                average = ActivityMain.db.getGradeAverage(title);
                publishProgress(0);
                averageS = ActivityMain.db.getGradeSAverage(title);
                publishProgress(1);
                averageM = ActivityMain.db.getGradeMAverage(title);
                publishProgress(2);
                gradesS = ActivityMain.db.getGradesS(title);
                publishProgress(3);
                gradesM = ActivityMain.db.getGradesM(title);
                publishProgress(4);
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                if (values[0] == 0 && average.intValue() >= 0)
                    gradeAverage.setText(average + "");
                else if (values[0] == 0) gradeAverage.setText("-");
                else if (values[0] == 1 && averageS.intValue() >= 0)
                    gradeSAverage.setText(averageS + "");
                else if (values[0] == 1) gradeSAverage.setText("-");
                else if (values[0] == 2 && averageM.intValue() >= 0)
                    gradeMAverage.setText(averageM + "");
                else if (values[0] == 2) gradeMAverage.setText("-");
                else if (values[0] == 3) {
                    if (gradesS[0] >= 0)
                        grade1s.setText(gradesS[0] + "");
                    else grade1s.setText("");
                    if (gradesS[1] >= 0)
                        grade2s.setText(gradesS[1] + "");
                    else grade2s.setText("");
                    if (gradesS[2] >= 0)
                        grade3s.setText(gradesS[2] + "");
                    else grade3s.setText("");
                    if (gradesS[3] >= 0)
                        grade4s.setText(gradesS[3] + "");
                    else grade4s.setText("");
                    graph.removeAllLines();
                    Line l = new Line();
                    LinePoint p;
                    for (int i = 0; i < gradesS.length; i++) {
                        p = new LinePoint();
                        p.setX(i);
                        p.setY(gradesS[i]);
                        if (gradesS[i] >= 0)
                            l.addPoint(p);
                    }
                    l.setColor(res.getColor(R.color.main_orange));
                    if (l.getPoints().size() > 1)
                        graph.addLine(l);
                    graph.setRangeY(0, 15);
                    graph.setLineToFill(0);
                } else if (values[0] == 4) {
                    if (gradesM[0] >= 0)
                        grade1m.setText(gradesM[0] + "");
                    else grade1m.setText("");
                    if (gradesM[1] >= 0)
                        grade2m.setText(gradesM[1] + "");
                    else grade2m.setText("");
                    if (gradesM[2] >= 0)
                        grade3m.setText(gradesM[2] + "");
                    else grade3m.setText("");
                    if (gradesM[3] >= 0)
                        grade4m.setText(gradesM[3] + "");
                    else grade4m.setText("");
                    Line l = new Line();
                    LinePoint p;
                    for (int i = 0; i < gradesM.length; i++) {
                        p = new LinePoint();
                        p.setX(i);
                        p.setY(gradesM[i]);
                        if (gradesM[i] >= 0)
                            l.addPoint(p);
                    }
                    l.setColor(res.getColor(R.color.main_blue));
                    if (l.getPoints().size() > 1)
                        graph.addLine(l);
                    graph.setRangeY(0, 15);
                }
                if (graph.getLines().size() > 0)
                    graph.setVisibility(View.VISIBLE);
                else graph.setVisibility(View.GONE);
            }
        }.execute();
    }

    @Override
    public void onClick(int lineIndex, int pointIndex) {
        String text = " Punkte";
        if (lineIndex == 1 || graph.getLines().size() < 2) text += " mündlich";
        else text += " schriftlich";
        CheatSheet.showCheatSheet(graph, graph.getLine(lineIndex).getPoint(pointIndex).getY() + text);
    }

    @Override
    public boolean onLongClick(View v) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.action_exam);
        popup.show();
        switch (v.getId()) {
            case R.id.dialog_subject_gradeS_1:
                mTerm = 0;
                mAttr = "s";
                break;
            case R.id.dialog_subject_gradeS_2:
                mTerm = 1;
                mAttr = "s";
                break;
            case R.id.dialog_subject_gradeS_3:
                mTerm = 2;
                mAttr = "s";
                break;
            case R.id.dialog_subject_gradeS_4:
                mTerm = 3;
                mAttr = "s";
                break;
            case R.id.dialog_subject_gradeM_1:
                mTerm = 0;
                mAttr = "m";
                break;
            case R.id.dialog_subject_gradeM_2:
                mTerm = 1;
                mAttr = "m";
                break;
            case R.id.dialog_subject_gradeM_3:
                mTerm = 2;
                mAttr = "m";
                break;
            case R.id.dialog_subject_gradeM_4:
                mTerm = 3;
                mAttr = "m";
                break;
        }
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            ActivityMain.db.updateGrade(title, mTerm, -1, mAttr);
            updateValues();
            return true;
        }
        return false;
    }
}
