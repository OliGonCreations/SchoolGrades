package com.oligon.grades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.juliansuarez.libwizardpager.wizard.model.AbstractWizardModel;
import co.juliansuarez.libwizardpager.wizard.model.ModelCallbacks;
import co.juliansuarez.libwizardpager.wizard.model.Page;
import co.juliansuarez.libwizardpager.wizard.ui.PageFragmentCallbacks;
import co.juliansuarez.libwizardpager.wizard.ui.ReviewFragment;
import co.juliansuarez.libwizardpager.wizard.ui.StepPagerStrip;

public class ActivityInit extends SherlockFragmentActivity implements PageFragmentCallbacks,
        ReviewFragment.Callbacks, ModelCallbacks {

    public static ArrayList<String> choicesPrim = new ArrayList<String>();
    public static ArrayList<String> choicesSec = new ArrayList<String>();
    public String primSubject = "";
    public String secSubject = "";
    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;
    private boolean mEditingAfterReview;
    private AbstractWizardModel mWizardModel = new WizardModel(this);
    private boolean mConsumePageSelectedEvent;
    private Button mNextButton;
    private Button mPrevButton;
    private List<Page> mCurrentPageSequence;
    private StepPagerStrip mStepPagerStrip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        choicesPrim.clear();
        choicesSec.clear();
        choicesPrim.addAll(Arrays.asList(getResources().getStringArray(R.array.prim_subjects)));
        choicesSec.addAll(Arrays.asList(getResources().getStringArray(R.array.sec_subjects)));
        primSubject = getString(R.string.prim_subject_pl);
        secSubject = getString(R.string.sec_subject_pl);

        setContentView(R.layout.activity_init);

        if (savedInstanceState != null) {
            mWizardModel.load(savedInstanceState.getBundle("model"));
        }

        mWizardModel.registerListener(this);

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.view_pager);
        mPager.setAdapter(mPagerAdapter);
        mStepPagerStrip = (StepPagerStrip) findViewById(R.id.pager_strip);
        mStepPagerStrip
                .setOnPageSelectedListener(new StepPagerStrip.OnPageSelectedListener() {
                    @Override
                    public void onPageStripSelected(int position) {
                        position = Math.min(mPagerAdapter.getCount() - 1,
                                position);
                        if (mPager.getCurrentItem() != position) {
                            mPager.setCurrentItem(position);
                        }
                    }
                });

        mNextButton = (Button) findViewById(R.id.next_button);
        mPrevButton = (Button) findViewById(R.id.prev_button);

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mStepPagerStrip.setCurrentPage(position);

                if (mConsumePageSelectedEvent) {
                    mConsumePageSelectedEvent = false;
                    return;
                }

                mEditingAfterReview = false;
                updateBottomBar();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPager.getCurrentItem() == mCurrentPageSequence.size()) {
                    ActivityMain.sp.edit().putBoolean(ActivityMain.KEY_SETUP, true).commit();
                    startActivity(new Intent(ActivityInit.this, ActivityMain.class));
                    new Thread(new Runnable() {
                        public void run() {
                            ArrayList<String> primSubjects = mWizardModel.findByKey("Kernfächer").getData().getStringArrayList(Page.SIMPLE_DATA_KEY);
                            ArrayList<String> secSubjects = mWizardModel.findByKey("Nebenfächer").getData().getStringArrayList(Page.SIMPLE_DATA_KEY);
                            Database db = new Database(getApplicationContext());
                            db.addSubjects(primSubjects, secSubjects);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    FragmentSubjects.updateContent();
                                }
                            });
                            SharedPreferences.Editor editor = ActivityMain.sp.edit();
                            editor.putInt(ActivityMain.KEY_NUM_PRIM, primSubjects.size());
                            for (int i = 0; i < primSubjects.size(); i++)
                                editor.putString(ActivityMain.KEY_SUBJECT_PRIM + i, primSubjects.get(i));
                            editor.putInt(ActivityMain.KEY_NUM_SEC, secSubjects.size());
                            for (int i = 0; i < secSubjects.size(); i++)
                                editor.putString(ActivityMain.KEY_SUBJECT_SEC + i, secSubjects.get(i));
                            editor.commit();
                        }
                    }).start();
                } else {
                    if (mEditingAfterReview) {
                        mPager.setCurrentItem(mPagerAdapter.getCount() - 1);
                    } else {
                        mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                    }
                }
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            }
        });

        onPageTreeChanged();
        updateBottomBar();
        Bundle b = new Bundle();
        b.putStringArrayList(Page.SIMPLE_DATA_KEY, getSubjects(ActivityMain.KEY_NUM_PRIM, ActivityMain.KEY_SUBJECT_PRIM));
        mWizardModel.findByKey("Kernfächer").resetData(b);
        b = new Bundle();
        b.putStringArrayList(Page.SIMPLE_DATA_KEY, getSubjects(ActivityMain.KEY_NUM_SEC, ActivityMain.KEY_SUBJECT_SEC));
        mWizardModel.findByKey("Nebenfächer").resetData(b);
    }

    @Override
    public void onPageTreeChanged() {
        mCurrentPageSequence = mWizardModel.getCurrentPageSequence();
        recalculateCutOffPage();
        mStepPagerStrip.setPageCount(mCurrentPageSequence.size() + 1);
        mPagerAdapter.notifyDataSetChanged();
        updateBottomBar();
    }

    private void updateBottomBar() {
        int position = mPager.getCurrentItem();
        if (position == mCurrentPageSequence.size()) {
            mNextButton.setText(R.string.finish);
            mNextButton.setBackgroundResource(R.drawable.finish_background);
            mNextButton.setTextAppearance(this, R.style.TextAppearanceFinish);
        } else {
            mNextButton.setText(mEditingAfterReview ? R.string.review : R.string.next);
            mNextButton.setBackgroundResource(R.drawable.item_background_holo_light);
            TypedValue v = new TypedValue();
            getTheme().resolveAttribute(android.R.attr.textAppearanceMedium, v, true);
            mNextButton.setTextAppearance(this, v.resourceId);
            mPrevButton.setTextAppearance(this, v.resourceId);
            mNextButton.setEnabled(position != mPagerAdapter.getCutOffPage());
        }
        mPrevButton.setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
    }

    private ArrayList<String> getSubjects(final String keyNum, final String keySub) {
        final ArrayList<String> bundle = new ArrayList<String>();
        new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < ActivityMain.sp.getInt(keyNum, 0); i++)
                    bundle.add(ActivityMain.sp.getString(keySub + i, ""));
            }
        }).start();
        return bundle;
    }

    @Override
    public void onStop() {
        super.onStop();
        mWizardModel.unregisterListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("model", mWizardModel.save());
    }

    @Override
    public AbstractWizardModel onGetModel() {
        return mWizardModel;
    }

    @Override
    public void onEditScreenAfterReview(String key) {
        for (int i = mCurrentPageSequence.size() - 1; i >= 0; i--) {
            if (mCurrentPageSequence.get(i).getKey().equals(key)) {
                mConsumePageSelectedEvent = true;
                mEditingAfterReview = true;
                mPager.setCurrentItem(i);
                updateBottomBar();
                break;
            }
        }
    }

    @Override
    public void onPageDataChanged(Page page) {
        if (page.isRequired()) {
            if (recalculateCutOffPage()) {
                mPagerAdapter.notifyDataSetChanged();
                updateBottomBar();
            }
        }
    }

    @Override
    public Page onGetPage(String key) {
        return mWizardModel.findByKey(key);
    }

    private boolean recalculateCutOffPage() {
        int cutOffPage = mCurrentPageSequence.size() + 1;
        for (int i = 0; i < mCurrentPageSequence.size(); i++) {
            Page page = mCurrentPageSequence.get(i);
            if (page.isRequired() && !page.isCompleted()) {
                cutOffPage = i;
                break;
            }
        }

        if (mPagerAdapter.getCutOffPage() != cutOffPage) {
            mPagerAdapter.setCutOffPage(cutOffPage);
            return true;
        }

        return false;
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {
        private int mCutOffPage;
        private Fragment mPrimaryItem;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if (i == 1) {
                return mCurrentPageSequence.get(i).createFragment();
            }
            if (i >= mCurrentPageSequence.size()) {
                return new ReviewFragment();
            }
            return mCurrentPageSequence.get(i).createFragment();

        }

        @Override
        public int getItemPosition(Object object) {
            if (object == mPrimaryItem) {
                return POSITION_UNCHANGED;
            }

            return POSITION_NONE;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position,
                                   Object object) {
            super.setPrimaryItem(container, position, object);
            mPrimaryItem = (Fragment) object;
        }

        @Override
        public int getCount() {
            return Math.min(mCutOffPage + 1, mCurrentPageSequence == null ? 1
                    : mCurrentPageSequence.size() + 1);
        }

        public int getCutOffPage() {
            return mCutOffPage;
        }

        public void setCutOffPage(int cutOffPage) {
            if (cutOffPage < 0) {
                cutOffPage = Integer.MAX_VALUE;
            }
            mCutOffPage = cutOffPage;
        }
    }


}
