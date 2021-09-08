package com.example.textreg2;

public class FavoriteWordHelperClass {
    String word,meaning;

    public FavoriteWordHelperClass(String word, String meaning) {
        this.word = word;
        this.meaning = meaning;
    }
    public FavoriteWordHelperClass(){};

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

}
