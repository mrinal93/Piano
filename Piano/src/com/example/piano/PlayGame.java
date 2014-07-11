package com.example.piano;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class PlayGame extends Activity {
	MediaPlayer mp = new MediaPlayer();
	Random generator = new Random(System.currentTimeMillis());
	
	ArrayList<Integer> sound = new ArrayList<Integer>(); //sounds for a song
	ArrayList<Integer> sound1 = new ArrayList<Integer>();
	ArrayList<Integer> sound2 = new ArrayList<Integer>();
	/*Stores the position of black tile in each row. Used in transition*/
	ArrayList<Integer> blackTile = new ArrayList<Integer>();	
	
	/* Row i stores the IDs of buttons in ith row.*/
	ArrayList<ImageView> row1 = new ArrayList<ImageView>();
	ArrayList<ImageView> row2 = new ArrayList<ImageView>();
	ArrayList<ImageView> row3 = new ArrayList<ImageView>();
	ArrayList<ImageView> row4 = new ArrayList<ImageView>();
	ArrayList<ArrayList<ImageView>> rows = new ArrayList<ArrayList<ImageView>>();
	int counter=0;
	int score=0;
	//MyCount counter2;
	private static CountDownTimer counter2;
	private int height;
	private int width;
	Animation animation;
	private TextView tv ;
	/*Game denotes whether it is 
	 * Custom mode(game=1) or default mode(game=0)*/
	private int game; 
	/*Contains the list of mp3 files generated from the MIDI file*/
	private ArrayList<String> fileNames=new ArrayList<String>();
	
	/**
	 * Executes when a black tile is touched.
	 */
	OnClickListener touchBlack =new OnClickListener() 
    {
	    public void onClick(View v)
	    {
	    	for(int i=0;i<4;i++)
	    	if(row2.get(i).equals(((ImageView)v)))
	    	{
		    	black();
		    	transition();
		    	score++;
		    	break;
	    	}
	    } 
    };
    /**
     * Executes when a white tile is touched.
     */
    OnClickListener touchWhite =new OnClickListener() 
    {
    	public void onClick(View v)
        {	
    		counter2.cancel();
    		((ImageView)v).setImageResource(R.drawable.red_black);
    		System.out.println("Cancel called in touchWhite");
    		Intent intent = new Intent(v.getContext(), GameOver.class);
        	intent.putExtra("score",score);
            finish();
            startActivity(intent);	   					        
        }
    };
    /**
     * Functionality of the count down timer.
     */
    OnClickListener startTimer =new OnClickListener() 
    {
	    public void onClick(View v)
	    {
	    	counter2 = new CountDownTimer(60000, 1000) {
				
				@Override
				public void onTick(long millisUntilFinished) {
					tv.setText("Time: "  + millisUntilFinished/1000);
					
				}
				
				@Override
				public void onFinish() {
					 tv.setText("Time Up!");            
			         Over();
					
				}
			};
	    	 counter2.start();
	    	 black();
		     transition();
		     score++;
	    } 
    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		game = bundle.getInt("game");
		if(game==1)
			fileNames = bundle.getStringArrayList("fileNames");
		
		
		setContentView(R.layout.activity_play_game);
		
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		height = displaymetrics.heightPixels; //432
		width = displaymetrics.widthPixels;	  //800
		System.out.println(height+","+width);
		screenSet();
		rowInitialise();
		getSound();
		randomColour();
		tv = (TextView)findViewById(R.id.textView1);
	}

	/*
	 * Initialising the arrays containing the IDs of each button/tile.
	 */
	private void rowInitialise() 
	{
		ImageView tile1 = (ImageView)findViewById(R.id.imageButton4);
		ImageView tile2 = (ImageView)findViewById(R.id.imageButton4b);
		ImageView tile3 = (ImageView)findViewById(R.id.imageButton4c);
		ImageView tile4 = (ImageView)findViewById(R.id.imageButton4d);
		
		row1.add(tile1);row1.add(tile2);row1.add(tile3);row1.add(tile4);
		
		ImageView tile5 = (ImageView)findViewById(R.id.imageButton3);
		ImageView tile6 = (ImageView)findViewById(R.id.imageButton3b);
		ImageView tile7 = (ImageView)findViewById(R.id.imageButton3c);
		ImageView tile8 = (ImageView)findViewById(R.id.imageButton3d);
		
		row2.add(tile5);row2.add(tile6);row2.add(tile7);row2.add(tile8);
		
		ImageView tile9 = (ImageView)findViewById(R.id.imageButton2);
		ImageView tile10 = (ImageView)findViewById(R.id.imageButton2b);
		ImageView tile11 = (ImageView)findViewById(R.id.imageButton2c);
		ImageView tile12 = (ImageView)findViewById(R.id.imageButton2d);
		
		row3.add(tile9);row3.add(tile10);row3.add(tile11);row3.add(tile12);
		
		ImageView tile13 = (ImageView)findViewById(R.id.imageButton1);
		ImageView tile14 = (ImageView)findViewById(R.id.imageButton1b);
		ImageView tile15 = (ImageView)findViewById(R.id.imageButton1c);
		ImageView tile16 = (ImageView)findViewById(R.id.imageButton1d);
		
		row4.add(tile13);row4.add(tile14);row4.add(tile15);row4.add(tile16);
		
		rows.add(row1); rows.add(row2); rows.add(row3); rows.add(row4);
		System.out.println("Size of all rows: "+row1.size()+","+row2.size()
				+","+row3.size()+","+row4.size()+"--"+rows.size());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.play_game, menu);
		return true;
	}
	/**
	 * Generates a random number between [0,3]
	 * @return the random number
	 */
	int randomGenerator() {
		 return (generator.nextInt(31)%4);
	}
	/**
	 * Generates a random black tile in the upper row.
	 */
	public void randomColour()
	{
		ArrayList<ImageView> row1 =new ArrayList<ImageView>(rows.get(0));
		for(int i=0; i<row1.size();i++)
		{
			
			//System.out.println("Grey set in RandomColour");
			row1.get(i).setImageResource(R.drawable.grey);
		}
		blackTile.clear();		
		for(int i=1;i<4;i++)
		{
			final ArrayList<ImageView> row ;//= new ArrayList<ImageView>();
			row = new ArrayList<ImageView>(rows.get(i));
			int black = randomGenerator();			
			black=black+1;
			blackTile.add(black);
			Log.e("Random", "Value: "+black);
			
			for(int j=0; j<4; j++)
			{
				if(j+1==black)
				{
					if(i==1)
					{
						row.get(j).setImageResource(R.drawable.yellow);
						row.get(j).setOnClickListener(startTimer);
					}
					else{
					row.get(j).setImageResource(R.drawable.black);
					row.get(j).setOnClickListener(touchBlack);
					}
				}
				else
				{
					row.get(j).setImageResource(R.drawable.white);
					row.get(j).setOnClickListener(touchWhite);
				}
			}		
		}
		
	}
	/**
	 * Function responsible for playing the sound when the correct
	 *  black tile is touched.
	 */
	public void black()
	{
		//mp.stop();
		if(game==1)
		{
			String SD_CARD_PATH = Environment.getExternalStorageDirectory().toString();
			String path = SD_CARD_PATH + "/Piano/Temp/" + fileNames.get(counter);
			System.out.println("File Path: "+path);
			
			mp = new MediaPlayer();
			try {
				mp.setDataSource(path);
			} catch (IllegalArgumentException e) {
				e.getMessage();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				mp.prepare();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mp.start();
			
			counter++;
			if(counter>=fileNames.size())
				counter=0;
		}
		else
		{
			mp = MediaPlayer.create(this, sound1.get(counter));
			mp.start();
			counter++;
			if(counter>=sound1.size())
				counter=0;
		}
		mp.setOnCompletionListener(new OnCompletionListener() 
		{
		    public void onCompletion(MediaPlayer mp)
		    {
		        mp.release();

		    };
		});
		
	}
	/**
	 * When a tile is touched, it moves the tiles accordingly.
	 * BlackTile stores the position of black tile in the previous row
	 */
	public void transition()
	{
		for(int i=0;i<blackTile.size();i++)
		{
			System.out.print("="+blackTile.get(i));
		}
		for(int i=0;i<4;i++) //i=0,1,2 -- 3
		{
			ArrayList<ImageView> row = new ArrayList<ImageView>();
			row = rows.get(i);
			if(i==0)
			{
				//row.get(blackTile.get(0)).setImageResource(R.drawable.grey);
				for(int j=0; j<4; j++)
				{
					if(blackTile.get(i)-1==j)
					{
						row.get(j).setImageResource(R.drawable.grey);
						//row.get(j).setFillAfter
						System.out.println("Grey set in Transition");
						animation = AnimationUtils.loadAnimation(getApplicationContext(),
					                R.anim.slide_down);
						row.get(j).startAnimation(animation);
						
					}
					else
					{
						row.get(j).setImageResource(R.drawable.white);
						
					}
				 }
			}
			else
			{
				int tile;
				if(i!=3)
					tile = blackTile.get(i);
				else
				{
					tile = randomGenerator();
					tile=tile+1;
					blackTile.remove(0);
					blackTile.add(tile);
					System.out.println("New Black Tile"+tile);
				}
				for(int j=0; j<4; j++)
				{
					if(tile-1==j)
					{
						row.get(j).setImageResource(R.drawable.black);
						//row.get(j).setVisibility(View.INVISIBLE);
						//if(i==3){
							animation = AnimationUtils.loadAnimation(getApplicationContext(),
					                R.anim.slide_down);
							row.get(j).startAnimation(animation);
						//}
						row.get(j).setOnClickListener(touchBlack);
					}
					else
					{
						row.get(j).setImageResource(R.drawable.white);
						row.get(j).setOnClickListener(touchWhite);
					}
				}
			}
		}
		for(int i=0;i<blackTile.size();i++)
		{
			System.out.print("="+blackTile.get(i));
		}
		
	}
	/**
	 * Adjusts the size of the tiles according to the screen size.
	 */
	private void screenSet() 
	{
		ImageView b1a = (ImageView)findViewById(R.id.imageButton1);
		ImageView b2a = (ImageView)findViewById(R.id.imageButton2);
		ImageView b3a = (ImageView)findViewById(R.id.imageButton3);
		ImageView b4a = (ImageView)findViewById(R.id.imageButton4);
		
		ImageView b1b = (ImageView)findViewById(R.id.imageButton1b);
		ImageView b2b = (ImageView)findViewById(R.id.imageButton2b);
		ImageView b3b = (ImageView)findViewById(R.id.imageButton3b);
		ImageView b4b = (ImageView)findViewById(R.id.imageButton4b);
		
		ImageView b1c = (ImageView)findViewById(R.id.imageButton1c);
		ImageView b2c = (ImageView)findViewById(R.id.imageButton2c);
		ImageView b3c = (ImageView)findViewById(R.id.imageButton3c);
		ImageView b4c = (ImageView)findViewById(R.id.imageButton4c);
		
		ImageView b1d = (ImageView)findViewById(R.id.imageButton1d);
		ImageView b2d = (ImageView)findViewById(R.id.imageButton2d);
		ImageView b3d = (ImageView)findViewById(R.id.imageButton3d);
		ImageView b4d = (ImageView)findViewById(R.id.imageButton4d);
		
		ArrayList<ImageView> views = new ArrayList<ImageView>();
		views.add(b1a);views.add(b2a);views.add(b3a);views.add(b4a);
		views.add(b1b);views.add(b2b);views.add(b3b);views.add(b4b);
		views.add(b1c);views.add(b2c);views.add(b3c);views.add(b4c);
		views.add(b1d);views.add(b2d);views.add(b3d);views.add(b4d);
		
		for(int i=0; i<views.size();i++)
		{
			ImageView v = new ImageView(this);
			v = views.get(i);
			v.requestLayout();
			v.getLayoutParams().height = height/5+height/40;
			v.getLayoutParams().width = width/5 + width/40+width/80;
		}
	}
	/**
	 * Add sounds to the array.
	 */
	public void getSound()
	{
		sound.add(R.raw.one); sound.add(R.raw.two); sound.add(R.raw.three);
		sound.add(R.raw.four); sound.add(R.raw.five); sound.add(R.raw.six);
		sound.add(R.raw.seven); sound.add(R.raw.eight); sound.add(R.raw.nine);
		sound.add(R.raw.ten); sound.add(R.raw.eleven); sound.add(R.raw.twelve);
		sound.add(R.raw.thirteen); sound.add(R.raw.fourteen); sound.add(R.raw.fifteen);
		sound.add(R.raw.sixteen); sound.add(R.raw.seventeen); sound.add(R.raw.eighteen);
		sound.add(R.raw.nineteen); sound.add(R.raw.twenty);
		sound.add(R.raw.s21); sound.add(R.raw.s22); sound.add(R.raw.s23); sound.add(R.raw.s24);
		sound.add(R.raw.s25); sound.add(R.raw.s26); sound.add(R.raw.s27); sound.add(R.raw.s28);
		sound.add(R.raw.s29); sound.add(R.raw.s30); sound.add(R.raw.s31); sound.add(R.raw.s32);
		sound.add(R.raw.s33); sound.add(R.raw.s34); sound.add(R.raw.s35); sound.add(R.raw.s36);
		sound.add(R.raw.s37); sound.add(R.raw.s38); sound.add(R.raw.s39); sound.add(R.raw.s40);
		sound.add(R.raw.s41); sound.add(R.raw.s42); sound.add(R.raw.s43); sound.add(R.raw.s44);
		sound.add(R.raw.s45); sound.add(R.raw.s46); sound.add(R.raw.s47); sound.add(R.raw.s48);
		sound.add(R.raw.s49); sound.add(R.raw.s50); sound.add(R.raw.s51); sound.add(R.raw.s52);
		sound.add(R.raw.s53); sound.add(R.raw.s54); sound.add(R.raw.s55); sound.add(R.raw.s56);
		sound.add(R.raw.s57); sound.add(R.raw.s58); sound.add(R.raw.s59); sound.add(R.raw.s60);
		sound.add(R.raw.s61);
		
		sound1.add(R.raw.b1); sound1.add(R.raw.b2); sound1.add(R.raw.b3); sound1.add(R.raw.b4);
		sound1.add(R.raw.b5); sound1.add(R.raw.b6); sound1.add(R.raw.b7); sound1.add(R.raw.b8);
		sound1.add(R.raw.b9); sound1.add(R.raw.b10); sound1.add(R.raw.b11); sound1.add(R.raw.b12);
		sound1.add(R.raw.b13); sound1.add(R.raw.b14); sound1.add(R.raw.b15); sound1.add(R.raw.b16);
		sound1.add(R.raw.b17); sound1.add(R.raw.b18); sound1.add(R.raw.b19); sound1.add(R.raw.b20);
		sound1.add(R.raw.b21); sound1.add(R.raw.b22); sound1.add(R.raw.b23); sound1.add(R.raw.b24);
		sound1.add(R.raw.b1); sound1.add(R.raw.b2); sound1.add(R.raw.b3); sound1.add(R.raw.b4);
		sound1.add(R.raw.b5); sound1.add(R.raw.b6); sound1.add(R.raw.b7); sound1.add(R.raw.b8);
		sound1.add(R.raw.b9); sound1.add(R.raw.b10); sound1.add(R.raw.b11); sound1.add(R.raw.b12);
		sound1.add(R.raw.b13); sound1.add(R.raw.b14); sound1.add(R.raw.b15); sound1.add(R.raw.b16);
		sound1.add(R.raw.b17); sound1.add(R.raw.b18); sound1.add(R.raw.b19); sound1.add(R.raw.b20);
		sound1.add(R.raw.b21); sound1.add(R.raw.b22); sound1.add(R.raw.b23); sound1.add(R.raw.b24);
		sound1.add(R.raw.b25); sound1.add(R.raw.b26); sound1.add(R.raw.b27); sound1.add(R.raw.b28);
		sound1.add(R.raw.b29); sound1.add(R.raw.b30); sound1.add(R.raw.b31); sound1.add(R.raw.b32);
		sound1.add(R.raw.b33); sound1.add(R.raw.b34); sound1.add(R.raw.b35); sound1.add(R.raw.b36);
		sound1.add(R.raw.b37); sound1.add(R.raw.b38); sound1.add(R.raw.b39); sound1.add(R.raw.b40);
		sound1.add(R.raw.b41); sound1.add(R.raw.b42); sound1.add(R.raw.b43); sound1.add(R.raw.b44);
		sound1.add(R.raw.b45); sound1.add(R.raw.b46); sound1.add(R.raw.b47); sound1.add(R.raw.b48);
	} 
	public void onBackPressed()
	{
	   //logic here, for example an intent
	   Intent intent = new Intent(this, MainActivity.class);    
	   finish();
	   counter2.cancel();
	   startActivity(intent);   
	}
	public void Over()
	{
		counter2.cancel();
		System.out.println("Cancel called, score is "+score);
		Intent intent = new Intent(this, GameOver.class);
    	intent.putExtra("score",score);
        finish();
        startActivity(intent);
	}
	
}
