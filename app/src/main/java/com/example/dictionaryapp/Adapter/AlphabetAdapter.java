package com.example.dictionaryapp.Adapter;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

// AlphabetAdapter.java
public class AlphabetAdapter extends BaseAdapter {
    private final String[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");

    @Override
    public int getCount() {
        return alphabet.length;
    }

    @Override
    public Object getItem(int position) {
        return alphabet[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv = new TextView(parent.getContext());
        tv.setText(alphabet[position]);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(12);
        return tv;
    }
}
