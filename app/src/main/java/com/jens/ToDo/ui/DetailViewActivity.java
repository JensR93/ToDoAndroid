package com.jens.ToDo.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.jens.ToDo.R;

public class DetailViewActivity extends AppCompatActivity {

    public static final String ARG_ITEM_ID = "itemID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);
    }
}
