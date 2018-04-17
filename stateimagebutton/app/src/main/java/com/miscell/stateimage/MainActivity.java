package com.miscell.stateimage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StateImageButton button = (StateImageButton) findViewById(R.id.btn_novel);
        button.showIndicator(true);
    }

    public void onButtonClicked(View v) {
    }
}
