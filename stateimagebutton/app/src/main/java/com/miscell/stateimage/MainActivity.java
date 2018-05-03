package com.miscell.stateimage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((StateImageButton) findViewById(R.id.btn_novel)).showIndicator(true);
        ((StateImageButton) findViewById(R.id.btn_settings_dot)).showIndicator(true);
    }

    @Override
    public void onClick(View view) {

    }

    public void onButtonSelected(View v) {
        View button = ((ViewGroup) v.getParent()).getChildAt(0);
        boolean selected = button.isSelected();
        button.setSelected(!selected);
    }

    public void onButtonDisabled(View v) {
        View button = ((ViewGroup) v.getParent()).getChildAt(0);
        boolean enabled = button.isEnabled();
        button.setEnabled(!enabled);
    }
}
