package com.jacobstechnologies.wordle;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dictionary {
    private List<String> mDictionary;

    public List<String> getmDictionary() {
        return mDictionary;
    }

    public void setmDictionary(List<String> mDictionary) {
        this.mDictionary = mDictionary;
    }

    public List<String> getDict(Context c){
        List<String> dict = new ArrayList<>();
        try {
            InputStream inputStream = c.getResources().openRawResource(R.raw.dictionary);
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String str;
            while((str = in.readLine()) != null){
                List<String> s = Arrays.asList(str.split(" "));
                for (int i = 0; i < s.size(); i++){
                    boolean isLetter = true;
                    for (int j = 0; j < s.get(i).length(); j++){
                        if (!Character.isLetter(s.get(i).charAt(j))){
                            isLetter = false;
                        }
                    }
                    if (s.get(i).length() == 5 && isLetter){
                        dict.add(s.get(i).toLowerCase());
                    }
                }
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dict;
    }
}
