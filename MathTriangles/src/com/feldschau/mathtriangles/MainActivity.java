package com.feldschau.mathtriangles;

import com.feldschau.mathtriangles.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener{

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu); 
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_settings:
            	startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void onClick(View view) {
		TriangleView tview = (TriangleView) view;
		
		tview.isShowingAnswer = !tview.isShowingAnswer;

		if (!tview.isShowingAnswer) {
			tview.setNewValues();			
		}
		tview.setBackgroundColor(0xFFFFFFFF);
		tview.invalidate();		
	}
}
