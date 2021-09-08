package com.example.textreg2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FavoriteWordListAdapter extends ArrayAdapter {
    private Activity mContext;
    List<FavoriteWordHelperClass> favoriteWordHelperClassList;

    public FavoriteWordListAdapter(Activity mContext, List<FavoriteWordHelperClass> favoriteWordHelperClassList) {
        super(mContext, R.layout.favorite_word_item, favoriteWordHelperClassList);
        this.mContext = mContext;
        this.favoriteWordHelperClassList = favoriteWordHelperClassList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = mContext.getLayoutInflater();
        View listViewItem = layoutInflater.inflate(R.layout.favorite_word_item,null,true);
        TextView word = (TextView)listViewItem.findViewById(R.id.tv_word);
        TextView meaning = (TextView)listViewItem.findViewById(R.id.tv_meaning);
        FavoriteWordHelperClass favoriteWordHelperClass = favoriteWordHelperClassList.get(position);
        word.setText(favoriteWordHelperClass.getWord());
        meaning.setText(favoriteWordHelperClass.getMeaning());
        return listViewItem;
    }

}
