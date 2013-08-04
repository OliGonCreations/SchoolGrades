package com.oligon.grades;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.oligon.grades.ui.ColorPickerPalette;
import com.oligon.grades.ui.ColorPickerSwatch;

public class FragmentSubjectAllocationDialog extends SherlockDialogFragment implements SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener, ColorPickerSwatch.OnColorSelectedListener {

    TextView tvAllocationS, tvAllocationM, mTitle;
    SeekBar sbAllocation;
    CheckBox cbCustom;
    String title;
    ColorPickerPalette mPalette;
    int[] mColors;
    int mSelectedColor;


    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null)
            return;
        getDialog().getWindow().setWindowAnimations(R.style.DialogFragmentAnimated_Window);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        title = getArguments().getString(FragmentSubjects.KEY_DIALOG_TITLE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_subject_allocation, null);
        mTitle = (TextView) view.findViewById(R.id.dialog_subject_title);
        mTitle.setText(title);
        tvAllocationS = (TextView) view.findViewById(R.id.tvAllocationS);
        tvAllocationM = (TextView) view.findViewById(R.id.tvAllocationM);
        cbCustom = (CheckBox) view.findViewById(R.id.cbCustom);
        sbAllocation = (SeekBar) view.findViewById(R.id.sbAllocation);
        cbCustom.setOnCheckedChangeListener(this);
        sbAllocation.setOnSeekBarChangeListener(this);
        int[] allocation = ActivityMain.db.getAllocation(title);
        if (allocation[1] == 1) {
            sbAllocation.setProgress(10);
            tvAllocationS.setText("2");
            tvAllocationM.setText("1");
            sbAllocation.setEnabled(false);
        } else {
            cbCustom.setChecked(true);
            sbAllocation.setProgress(allocation[1] / 5);
        }
        mPalette = (ColorPickerPalette) view.findViewById(R.id.color_picker);
        mPalette.init(2, 4, this);
        mColors = new int[]{ getResources().getColor(R.color.main_orange), getResources().getColor(R.color.main_blue),
                getResources().getColor(R.color.main_green), getResources().getColor(R.color.main_purple),
                getResources().getColor(R.color.main_red), getResources().getColor(R.color.main_yellow),
                getResources().getColor(R.color.main_brown), getResources().getColor(R.color.main_grey) };
        mSelectedColor = allocation[2];
        mPalette.drawPalette(mColors, mSelectedColor);
        builder.setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ActivityMain.db.updateAllocation(title, Integer.parseInt(tvAllocationS.getText().toString()), Integer.parseInt(tvAllocationM.getText().toString()), mSelectedColor);
                        FragmentSubjectDialog.updateValues();
                        getDialog().dismiss();
                    }
                });
        return builder.create();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        setProgress(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            sbAllocation.setEnabled(true);
            setProgress(sbAllocation.getProgress());
        } else {
            sbAllocation.setEnabled(false);
            tvAllocationS.setText("2");
            tvAllocationM.setText("1");
        }
    }

    private void setProgress(int progress) {
        tvAllocationS.setText((20 - progress) * 5 + "");
        tvAllocationM.setText(progress * 5 + "");
    }

    @Override
    public void onColorSelected(int color) {
        this.mSelectedColor = color;
        this.mPalette.drawPalette(this.mColors, this.mSelectedColor);
    }
}
