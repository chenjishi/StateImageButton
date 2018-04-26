package com.miscell.stateimage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StateImageButton button = (StateImageButton) findViewById(R.id.btn_novel);
        button.setOnClickListener(this);
        button.showIndicator(true);

        StateImageButton button1 = (StateImageButton) findViewById(R.id.btn_settings);
        button1.setOnClickListener(this);
        button1.showIndicator(true);
    }

    @Override
    public void onClick(View view) {

    }
}
