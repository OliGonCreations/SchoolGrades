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

public class FragmentSubjectAllocationDialog extends SherlockDialogFragment implements SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {

    TextView tvAllocationS, tvAllocationM, mTitle;
    SeekBar sbAllocation;
    CheckBox cbCustom;
    String title;


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
        int allocation = FragmentSubjects.db.getAllocation(title)[1];
        if (allocation == 1) {
            sbAllocation.setProgress(10);
            tvAllocationS.setText("2");
            tvAllocationM.setText("1");
            sbAllocation.setEnabled(false);
        } else {
            cbCustom.setChecked(true);
            sbAllocation.setProgress(allocation / 5);
        }
        builder.setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
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
            FragmentSubjects.db.updateAllocation(title, 66, 33);
        }
    }

    private void setProgress(int progress) {
        tvAllocationS.setText((20 - progress) * 5 + "");
        tvAllocationM.setText(progress * 5 + "");
        FragmentSubjects.db.updateAllocation(title, (20 - progress) * 5, progress * 5);
    }
}
