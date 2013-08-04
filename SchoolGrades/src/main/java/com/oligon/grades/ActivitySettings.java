package com.oligon.grades;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import au.com.bytecode.opencsv.CSVWriter;

public class ActivitySettings extends SherlockPreferenceActivity {

    private static final String PREF_KEY_EXPORT = "pref_export";
    private static Handler handler = new Handler();
    private static Context context;

    CheckBox mShare;
    EditText mFileName;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addPreferencesFromResource(R.xml.prefs_main);
        context = this;
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (PREF_KEY_EXPORT.equals(preference.getKey())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_export, null);
            ((TextView) view.findViewById(R.id.dialog_export_title)).setText(getString(R.string.dialog_export_title));
            mShare = (CheckBox) view.findViewById(R.id.dialog_export_share);
            mFileName = (EditText) view.findViewById(R.id.dialog_export_filename);
            builder.setView(view)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            final String fileName = mFileName.getText().toString();
                            final boolean share = mShare.isChecked();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    File exportDir = new File(Environment.getExternalStorageDirectory(), "");
                                    if (!exportDir.exists())
                                        exportDir.mkdirs();
                                    final File file = new File(exportDir, fileName + ".csv");
                                    try {
                                        file.createNewFile();
                                        CSVWriter csvWrite = new CSVWriter(new FileWriter(file), '\t');
                                        Database db = new Database(context);
                                        Cursor cursor = db.getEverything();
                                        ArrayList<String> row1 = new ArrayList<String>();
                                        ArrayList<String> row1S = new ArrayList<String>();
                                        ArrayList<String> row2S = new ArrayList<String>();
                                        ArrayList<String> row3S = new ArrayList<String>();
                                        ArrayList<String> row4S = new ArrayList<String>();
                                        ArrayList<String> rowA = new ArrayList<String>();
                                        row1.add("");
                                        row1S.add("KS1/1");
                                        row2S.add("KS1/2");
                                        row3S.add("KS2/1");
                                        row4S.add("KS2/1");
                                        rowA.add("Durchschnitt S/M");
                                        if (cursor.moveToFirst()) {
                                            int i;
                                            do {
                                                row1.add(cursor.getString(1));
                                                i = cursor.getInt(2);
                                                if (i != -1)
                                                    row1S.add(i + "");
                                                else row1S.add("-");
                                                i = cursor.getInt(3);
                                                if (i != -1)
                                                    row2S.add(i + "");
                                                else row2S.add("-");
                                                i = cursor.getInt(4);
                                                if (i != -1)
                                                    row3S.add(i + "");
                                                else row3S.add("-");
                                                i = cursor.getInt(5);
                                                if (i != -1)
                                                    row4S.add(i + "");
                                                else row4S.add("-");
                                                rowA.add(cursor.getString(6).replace(".", ","));
                                            } while (cursor.moveToNext());
                                        }
                                        String[] dummyArray = new String[0];
                                        csvWrite.writeNext(row1.toArray(dummyArray));
                                        csvWrite.writeNext(row1S.toArray(dummyArray));
                                        csvWrite.writeNext(row2S.toArray(dummyArray));
                                        csvWrite.writeNext(row3S.toArray(dummyArray));
                                        csvWrite.writeNext(row4S.toArray(dummyArray));
                                        csvWrite.writeNext(rowA.toArray(dummyArray));
                                        csvWrite.close();
                                        cursor.close();
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (share) {
                                                    Intent share = new Intent(Intent.ACTION_SEND);
                                                    MimeTypeMap mime = MimeTypeMap.getSingleton();
                                                    share.setType(mime.getMimeTypeFromExtension("csv"));
                                                    share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getPath()));
                                                    startActivity(Intent.createChooser(share, "Datei senden"));
                                                } else
                                                    Toast.makeText(context, "Erfolgreich exportiert", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } catch (Exception sqlEx) {
                                        Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(context, "Fehler beim exportieren", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                }
                            }).start();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

}
