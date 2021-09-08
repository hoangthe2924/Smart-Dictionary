package com.example.textreg2.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.textreg2.R;
import com.example.textreg2.searchword.WordMeaningActivity;

public class FragmentSynonyms extends Fragment {
    public FragmentSynonyms() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_synonyms,container, false);//Inflate Layout

        Context context=getActivity();
        TextView text = (TextView)view.findViewById(R.id.tv_synonyms);

        String synonym= ((WordMeaningActivity)context).synonym;

        text.setText(synonym);
        if(synonym==null)
        {
            text.setText("Synonym not found");
        }
        return view;
    }
}
