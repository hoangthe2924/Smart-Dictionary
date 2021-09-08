package com.example.textreg2.searchword;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.textreg2.FavoriteWordActivity;
import com.example.textreg2.FavoriteWordHelperClass;
import com.example.textreg2.R;
import com.example.textreg2.database.DatabaseHelper;
import com.example.textreg2.fragment.FragmentAntonyms;
import com.example.textreg2.fragment.FragmentDescription;
import com.example.textreg2.fragment.FragmentExample;
import com.example.textreg2.fragment.FragmentSynonyms;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class WordMeaningActivity extends AppCompatActivity {
    String mWord;
    DatabaseHelper myDbHelper;
    ViewPager viewPager;
    EditText ed_note;
    LinearLayout ln_note_box;
    Cursor c = null;
    int mType;

    public String pronunciation;
    public String description;
    public String antonym;
    public String synonym;
    public String example;
    public String note;
    public String userEmail;
    public String key="";


    FirebaseUser user;
    FirebaseDatabase database;
    TextToSpeech tts;
    ToggleButton favoriteWord;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_meaning);

        //received values
        Bundle bundle = getIntent().getExtras();
        mWord= bundle.getString("word");
        mType = bundle.getInt("type");


        getSupportActionBar().setTitle(mWord);

        favoriteWord=findViewById(R.id.btn_favorite);
        ln_note_box = findViewById(R.id.editnotebox);
        ed_note = findViewById(R.id.edt_note);

        myDbHelper = new DatabaseHelper(this);

        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }

        c=myDbHelper.getNote(mWord,mType);
        if (c.getCount()!=0) {
            c.moveToFirst();
            note = c.getString(c.getColumnIndex("wnote"));
        }
        else note = "";




        c = myDbHelper.getMeaning(mWord,mType);

        //set result on screen
        TextView wordTextView = findViewById(R.id.editWord);
        TextView pronTextView = findViewById(R.id.editPronunciation);

        wordTextView.setText(mWord);

        if (c.moveToFirst()) {
            if (mType == 1) {
                pronunciation = c.getString(c.getColumnIndex("pronounce"));
                description = c.getString(c.getColumnIndex("vn_description"));
                antonym = c.getString(c.getColumnIndex("antonym"));
                synonym = c.getString(c.getColumnIndex("synonym"));
                example = c.getString(c.getColumnIndex("example"));
                pronTextView.setText("[\t"+ pronunciation+"\t]");
            }
            else if (mType == 3) {
                pronunciation = c.getString(c.getColumnIndex("pronounce"));
                description = c.getString(c.getColumnIndex("en_description"));
                antonym = c.getString(c.getColumnIndex("antonym"));
                synonym = c.getString(c.getColumnIndex("synonym"));
                example = c.getString(c.getColumnIndex("example"));
                pronTextView.setText("[\t"+ pronunciation+"\t]");
            }
            else if (mType == 2) {
                description = c.getString(c.getColumnIndex("description"));
            }
        }

        viewPager = findViewById(R.id.tab_viewpager);

        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        favoriteWord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    addWordToDB();
                }
                else deleteWordFromDB();
            }
        });
    }

    private void deleteWordFromDB() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userEmail = user.getEmail();
        }
        final String emailID=userEmail.replaceAll("[-+.^:,@_]","");
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference(emailID);
        reference.child(mWord).removeValue();
    }

    private void addWordToDB() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userEmail = user.getEmail();
        }
        String emailID=userEmail.replaceAll("[-+.^:,@_]","");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(emailID);
        FavoriteWordHelperClass word = new FavoriteWordHelperClass(mWord,description);
        reference.child(mWord).setValue(word);
    }

    public void btn_add_note_onclick(View view) {
        if(ln_note_box.isShown()){
            ln_note_box.setVisibility(View.GONE);
            ed_note.clearFocus();
        }
        else
        {
            ln_note_box.setVisibility(View.VISIBLE);
            ed_note.setFocusable(true);
            ed_note.setText(note);
        }
    }

    public void btn_ok_note_onclick(View view) {
        myDbHelper.openDataBase();
        String text_note = ed_note.getText().toString();
        note = text_note;
        myDbHelper.editNote(mWord,mType,text_note);
        ln_note_box.setVisibility(View.GONE);
        Toast.makeText(this, "Add note successfully", Toast.LENGTH_SHORT).show();
    }

    public void btn_speak_word(View view) {
        if(Build.VERSION.SDK_INT < 23)
        {
            Toast.makeText(this, "This feature is not available on this device API version.\nPlease update to API 23 or later.",Toast.LENGTH_LONG).show();
        }
        else
        {
            tts = new TextToSpeech(WordMeaningActivity.this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status == TextToSpeech.SUCCESS){
                        int result;
                        if(mType==2){
                            result = tts.setLanguage(new Locale("vi"));
                        }
                        else
                            result = tts.setLanguage(Locale.getDefault());
                        if(result==TextToSpeech.LANG_MISSING_DATA || result==TextToSpeech.LANG_NOT_SUPPORTED){
                            Log.e("error", "This Language is not supported");
                            Toast.makeText(WordMeaningActivity.this, "This Language is not supported.",Toast.LENGTH_SHORT).show();

                        }
                        else{
                            tts.speak(mWord, TextToSpeech.QUEUE_FLUSH,null, null);
                        }
                    }
                    else
                        Log.e("error", "Initialization Failed!");
                }
            });
        }
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();


        ViewPagerAdapter(FragmentManager fragmentManager) {super(fragmentManager);
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }


        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new FragmentDescription(), "Description");
        if(mType!=2) {
            viewPagerAdapter.addFragment(new FragmentSynonyms(), "Synonyms");
            viewPagerAdapter.addFragment(new FragmentAntonyms(), "Antonyms");
            viewPagerAdapter.addFragment(new FragmentExample(), "Example");
        }
        viewPager.setAdapter(viewPagerAdapter);
    }

}