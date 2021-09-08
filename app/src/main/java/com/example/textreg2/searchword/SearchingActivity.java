package com.example.textreg2.searchword;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import com.example.textreg2.R;
import com.example.textreg2.database.DatabaseHelper;

public class SearchingActivity extends AppCompatActivity {
    int type;
    SearchView mSearch;
    static DatabaseHelper mDatabaseHelper;
    SimpleCursorAdapter simpleCursorAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searching_activity);

        type = getIntent().getIntExtra("type",1);

        setTitleActionBar(type);

        mSearch = findViewById(R.id.search_view);
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearch.setIconified(false);
            }
        });

        mDatabaseHelper = new DatabaseHelper(this);

        try {
            mDatabaseHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }

        final String[] from = new String[]{"word"};
        final int[] to = new int[]{R.id.suggestion_text};

        simpleCursorAdapter = new SimpleCursorAdapter(SearchingActivity.this,
                R.layout.suggestion_row, null, from, to, 0
        ){
            @Override
            public void changeCursor(Cursor cursor) {
                super.changeCursor(cursor);
            }
        };

        mSearch.setSuggestionsAdapter(simpleCursorAdapter);

        mSearch.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionClick(int position) {

                // Add clicked text to search box
                CursorAdapter ca = mSearch.getSuggestionsAdapter();
                Cursor cursor = ca.getCursor();
                cursor.moveToPosition(position);
                String clicked_word =  cursor.getString(cursor.getColumnIndex("word"));
                mSearch.setQuery(clicked_word,false);

                //search.setQuery("",false);

                mSearch.clearFocus();
                mSearch.setFocusable(false);

                Intent intent = new Intent(SearchingActivity.this, WordMeaningActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("word",clicked_word);
                bundle.putInt("type", type);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);

                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                // Your code here
                return true;
            }
        });

        mSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                String text =  mSearch.getQuery().toString();

                Cursor c = mDatabaseHelper.getMeaning(text, type);

                if(c.getCount()==0)
                {
                    mSearch.setQuery("",false);

                    AlertDialog.Builder builder = new AlertDialog.Builder(SearchingActivity.this, R.style.MyDialogTheme);
                    builder.setTitle("Word Not Found");
                    builder.setMessage("Please search again");

                    String positiveText = getString(android.R.string.ok);
                    builder.setPositiveButton(positiveText,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // positive button logic
                                }
                            });

                    String negativeText = getString(android.R.string.cancel);
                    builder.setNegativeButton(negativeText,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mSearch.clearFocus();
                                }
                            });

                    AlertDialog dialog = builder.create();
                    // display dialog
                    dialog.show();
                }

                else
                {
                    //search.setQuery("",false);
                    mSearch.clearFocus();
                    mSearch.setFocusable(false);

                    Intent intent = new Intent(SearchingActivity.this, WordMeaningActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("word",text);
                    bundle.putInt("type", type);
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
                return false;
            }


            @Override
            public boolean onQueryTextChange(final String s) {

                mSearch.setIconifiedByDefault(false); //Give Suggestion list margins
                Cursor cursorSuggestion=mDatabaseHelper.getSuggestions(s,type);
                simpleCursorAdapter.changeCursor(cursorSuggestion);

                return false;
            }

        });


    }

    private void setTitleActionBar(int type) {
        if(type==1)
            getSupportActionBar().setTitle("Translate Eng-Vie");
        if(type==2)
            getSupportActionBar().setTitle("Translate Vie-Eng");
        if(type==3)
            getSupportActionBar().setTitle("Translate Eng-Eng");
    }

    @Override
    protected void onRestart() {

        super.onRestart();
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

}
