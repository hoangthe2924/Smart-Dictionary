package com.example.textreg2.database;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.example.textreg2.R;
import com.example.textreg2.searchword.DictionaryActivity;

public class DBAsynctask extends AsyncTask<Void,Void,Boolean> {

    private Context mContext;
    private AlertDialog mAlertDialog;
    private DatabaseHelper mDBHelper;

    public DBAsynctask(Context context){
        mContext=context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        AlertDialog.Builder d = new AlertDialog.Builder(mContext, R.style.MyDialogTheme);
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View dialogView = layoutInflater.inflate(R.layout.alert_dialog_database_loading, null);
        d.setTitle("Loading Database...");
        d.setView(dialogView);
        mAlertDialog = d.create();
        mAlertDialog.setCancelable(false);
        mAlertDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        mDBHelper = new DatabaseHelper(mContext);
        mDBHelper.copyDatabase();
        mDBHelper.close();
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        mAlertDialog.dismiss();
        DictionaryActivity.openDatabase();
    }
}
