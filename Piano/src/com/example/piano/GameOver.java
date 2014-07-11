package com.example.piano;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class GameOver extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		int sc = bundle.getInt("score");
		String s = "Score: "+ sc;
		
		setContentView(R.layout.activity_game_over);
		TextView over = (TextView) findViewById(R.id.gameOver);
		TextView score = (TextView) findViewById(R.id.score);
		Typeface papyrus = Typeface.createFromAsset(getAssets(),
				"fonts/Papyrus.TTF");
		over.setTypeface(papyrus);
		score.setTypeface(papyrus);
		score.setText(s);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game_over, menu);
		return true;
	}
	public void restart(View view)
	{
		Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
	}

}
