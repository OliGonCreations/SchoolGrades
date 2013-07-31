package com.oligon.grades;

import android.os.Bundle;
import android.view.MotionEvent;

import com.actionbarsherlock.app.SherlockActivity;

public class ActivityOverlay extends SherlockActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overlay);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        finish();
        return super.dispatchTouchEvent(ev);
    }
}
