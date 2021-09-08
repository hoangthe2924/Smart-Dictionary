package com.example.textreg2.searchword;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.textreg2.R;
import com.example.textreg2.database.DBAsynctask;
import com.example.textreg2.database.DatabaseHelper;

public class DictionaryActivity extends AppCompatActivity implements View.OnClickListener {
    CardView engviet;
    CardView vieteng;
    CardView engeng;

    static DatabaseHelper mDatabaseHelper;
    static boolean mDatabaseOpenned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dictionary_activity);
        engviet=findViewById(R.id.engviet);
        engviet.setOnClickListener(this);
        vieteng=findViewById(R.id.vieteng);
        vieteng.setOnClickListener(this);
        engeng=findViewById(R.id.engeng);
        engeng.setOnClickListener(this);

        mDatabaseHelper = new DatabaseHelper(this);

        if (mDatabaseHelper.checkDataBase()) {
            openDatabase();
        } else {
            DBAsynctask DBAsync = new DBAsynctask(DictionaryActivity.this);
            DBAsync.execute();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.engviet)
        {
            Intent intent = new Intent(this, SearchingActivity.class);

            intent.putExtra("type", 1);
            startActivity(intent);
        }
        if (v.getId()==R.id.vieteng)
        {
            Intent intent = new Intent(this, SearchingActivity.class);

            intent.putExtra("type", 2);
            startActivity(intent);
        }
        if (v.getId()==R.id.engeng)
        {
            Intent intent = new Intent(this, SearchingActivity.class);

            intent.putExtra("type", 3);
            startActivity(intent);
        }
    }

    public static void openDatabase() {
        try {
            mDatabaseHelper.openDataBase();
            mDatabaseOpenned = true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}