package com.oligon.grades;

import android.content.Context;

import co.juliansuarez.libwizardpager.wizard.model.AbstractWizardModel;
import co.juliansuarez.libwizardpager.wizard.model.MultipleFixedChoicePage;
import co.juliansuarez.libwizardpager.wizard.model.PageList;

public class WizardModel extends AbstractWizardModel {


    public WizardModel(Context context) {
        super(context);
    }

    @Override
    protected PageList onNewRootPageList() {
        return new PageList(new MultipleFixedChoicePage(this, "Kernfächer")
                .setChoices(ActivityInit.choicesPrim)
                , new MultipleFixedChoicePage(this, "Nebenfächer")
                .setChoices(ActivityInit.choicesSec)
        );
    }
}
