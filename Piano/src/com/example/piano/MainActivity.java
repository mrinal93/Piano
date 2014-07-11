package com.example.piano;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	String filepath;
	//public static final int NOTE_ON = 0x90;
    //public static final int NOTE_OFF = 0x80;
    public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
	ArrayList<Integer> notes = new ArrayList<Integer>();
	ArrayList<Integer> tune = new ArrayList<Integer>();
	ArrayList<String> fileNames = new ArrayList<String>();
	MediaPlayer mp;
	Button upload, get, playCustom, play;
	Logger logger;
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		notes.add(R.raw.c); notes.add(R.raw.ch);notes.add(R.raw.d);notes.add(R.raw.dh);
		notes.add(R.raw.e);notes.add(R.raw.f);notes.add(R.raw.fh);notes.add(R.raw.g);
		notes.add(R.raw.gh);notes.add(R.raw.a);notes.add(R.raw.ah);notes.add(R.raw.b);
		logger = Logger.getLogger("MyLog");  
		upload = (Button) findViewById(R.id.button1);
		get = (Button) findViewById(R.id.button2);
		playCustom = (Button) findViewById(R.id.button3);
		play = (Button) findViewById(R.id.button4);
		play.setVisibility(View.VISIBLE);
		upload.setVisibility(View.VISIBLE);
		playCustom.setVisibility(View.INVISIBLE);
		get.setVisibility(View.INVISIBLE);
	    FileHandler fh;  
	    

	    try {  

	        // This block configure the logger with handler and formatter  
	        fh = new FileHandler("mnt/sdcard/test/MyLogFile.log");  
	        logger.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);  

	        // the following statement is used to log any messages  
	        logger.info("My first log");  

	    } catch (SecurityException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }  

	    logger.info("Log begins");  
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	 // filepath contains path of the file
	public void click(View view)
	{
		getFile();
		play.setVisibility(View.INVISIBLE);
	    get.setVisibility(View.VISIBLE);
		System.out.println("Path: "+filepath);
	}
	public void playCustom(View view)
	{
		
		Intent intent = new Intent(this, PlayGame.class);
		intent.putStringArrayListExtra("fileNames", fileNames);
		intent.putExtra("game", 1);
		finish();
		startActivity(intent);
	}
	public void play(View view)
	{
		
		Intent intent = new Intent(this, PlayGame.class);
		intent.putExtra("game", 0);
		finish();
		startActivity(intent);
	}
	public void notes(View view)
	{
		play.setVisibility(View.INVISIBLE);
	    playCustom.setVisibility(View.VISIBLE);
		try{
		getNotes();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	public void  getFile()
	{
	    // To open up a gallery browser
	    Intent intent = new Intent();
	    intent.setType("file/*");
	    intent.setAction(Intent.ACTION_GET_CONTENT);
	    System.out.println("getFile complete1");
	    startActivityForResult(Intent.createChooser(intent, "Select File"),1);
	    System.out.println("getFile complete");
	    
	    // To handle when an image is selected from the browser, add the following to your Activity
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
	    if (resultCode == RESULT_OK) {
	        if (requestCode == 1) {
	            // currImageURI is the global variable I'm using to hold the content:// URI of the image
	            Uri currImageURI = data.getData();

	            File file = new File(getRealPathFromURI(currImageURI));

	            if (file.exists())
	            {
	                filepath=file.getAbsolutePath();
	                System.out.println("FP: "+filepath);
	            }
	            else
	            {
	                System.out.println("File Not Found");
	            }
	        }
	    }
	}

	public String getRealPathFromURI(Uri contentURI)
	{
		String result;
	    Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
	    if (cursor == null) { // Source is Dropbox or other similar local file path
	        result = contentURI.getPath();
	    } else { 
	        cursor.moveToFirst(); 
	        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
	        result = cursor.getString(idx);
	        cursor.close();
	    }
	    return result;
	}
	
	public void getNotes() throws Exception
	{
		MidiFile mf = null;
        File input = new File(filepath);
        int count=0;

        try
        {
            mf = new MidiFile(input);
        }
        catch(IOException e)
        {
            System.err.println("Error parsing MIDI file:");
            e.printStackTrace();
            return;
        }
        fileNames.clear();
        ArrayList<MidiTrack> tracks= new ArrayList<MidiTrack>();
        tracks = mf.getTracks();
        for (int i=0; i < tracks.size(); i++) { 
        	MidiTrack track = tracks.get(i);
        	Iterator<MidiEvent> it = track.getEvents().iterator();
        	ArrayList<Integer> chordOn = new ArrayList<Integer>();
        	ArrayList<Integer> chordOff = new ArrayList<Integer>();
        	ArrayList<Integer> note = new ArrayList<Integer>();
        	//int count=0;
        	int flag=0;
        	System.out.println("Notes");
        	//MidiEvent E = it.next();
        	MidiEvent F=null;
        	MidiEvent E;
        	while(it.hasNext())
            {
       		// System.out.println("Count enter");
        		if(flag==0){
        			E = it.next();
        			while(!(E.getClass().equals(NoteOn.class)) && !(E.getClass().equals(NoteOff.class)))
             		  {
             			  E=it.next();
             		  }
        			
        		}
        		else
        			E = F;
                if(E.getClass().equals(NoteOn.class))
                {
               	 if(((NoteOn)E).getChannel()==0)
               	 {
               		  F=it.next();   //check if it exists!  
               		  while(!(F.getClass().equals(NoteOn.class)) && !(F.getClass().equals(NoteOff.class)))
               		  {
               			  F=it.next();
               		  }
	               	 if(F.getClass().equals(NoteOff.class))
	               	 {
	               		 if(((NoteOff)F).getChannel()==0 && ((NoteOff)F).getNoteValue()==((NoteOn)E).getNoteValue())
	               		 {
	               			 note.clear();
	               			 note.add(((NoteOn)E).getNoteValue());                			
	               			 MidiMaker md=new MidiMaker();
	               			 //md.play(((NoteOff)F).getNoteValue(), "music"+count+"mid");
	               			 count++;
	               		 }
	               		 System.out.print("Note: ");
	               		 for(int j=0;j<note.size();j++){                			 
	               			 System.out.print(note.get(j));
	               		 }
	               		 System.out.println();
	               		
	               	 }
	               	 else if(F.getClass().equals(NoteOn.class))
	               	 {
	               		 chordOn.clear();
	               		 chordOn.add(((NoteOn)E).getNoteValue());
	               		 while(it.hasNext() && F.getClass().equals(NoteOn.class))
	               		 {
	               			 System.out.println("While - ChordOn");
	               			 chordOn.add(((NoteOn)F).getNoteValue());
	               			 F=it.next();
	               			while(!(F.getClass().equals(NoteOn.class)) && !(F.getClass().equals(NoteOff.class)))
	                 		  {
	                 			  F=it.next();
	                 		  }
	               			 
	               		 }
	               		 if(F.getClass().equals(NoteOff.class))
	               		 {
	               			 chordOff.clear();
	               			 //int key = ((NoteOff)F).getNoteValue();
	               			 //if(chordOn.contains(((Integer)key)))
	               			 //chordOff.add(((NoteOff)F).getNoteValue());
	               			 while(F.getClass().equals(NoteOff.class) && it.hasNext())
	               			 {
	               				chordOff.add(((NoteOff)F).getNoteValue());
	               				F=it.next();	               				
	               			 }
	               			 flag=1;
	               		 }
	               			System.out.print("ChordOn: ");
	                		 for(int j=0;j<note.size();j++){                			 
	                			 System.out.print(chordOn.get(j));
	                		 }
	                		 System.out.println();
	                		 System.out.print("ChordOff: ");
	                		 for(int j=0;j<note.size();j++){                			 
	                			 System.out.print(chordOff.get(j));
	                	
	                		 System.out.println();
	               		 }
	               	 }
               	 }
                }}
        	/*if(it.hasNext())
        	{
        		E = it.next();
        	}*/
        	/* while(it.hasNext())
             {
        		// System.out.println("Count enter");
                // MidiEvent E = it.next();
        		 System.out.println(E.getClass());
                 if(E.getClass().equals(NoteOn.class))
                 {
                	 if(((NoteOff)E).getChannel()==0){
                	 System.out.println("Here");
                	 F=it.next();   //check if it exists!
                	 System.out.println("here1");
                	 if(F.getClass().equals(NoteOff.class))
                	 {
                		 if(((NoteOff)F).getNoteValue()==((NoteOn)E).getNoteValue())
                		 {
                			 note.clear();
                			 note.add(((NoteOff)F).getNoteValue());                			
                			 MidiMaker md=new MidiMaker();
                			 //md.play(((NoteOff)F).getNoteValue(), "music"+count+"mid");
                			 count++;
                		 }
                		 E=it.next();
                		 System.out.print("Note: ");
                		 for(int j=0;j<note.size();j++){                			 
                			 System.out.print(note.get(j));
                		 }
                		 System.out.println();
                	 }
                	 else if(F.getClass().equals(NoteOn.class))
                	 {
                		 if(((NoteOn)F).getNoteValue()!=((NoteOn)E).getNoteValue())
                		 {
                			 chordOn.clear();
                			 chordOn.add(((NoteOn)E).getNoteValue());
                		 }
                		 while(it.hasNext() && (F=it.next()).getClass().equals(NoteOn.class))
                		 {
                			 chordOn.add(((NoteOn)F).getNoteValue());
                		 }
                		 if(F.getClass().equals(NoteOff.class))
                		 {
                			 chordOff.clear();
                			 chordOff.add(((NoteOff)F).getNoteValue());
                			 while(it.hasNext() && (F=it.next()).getClass().equals(NoteOn.class))
                			 {
                				 chordOff.add(((NoteOff)F).getNoteValue());
                			 }
                		 }
                		 System.out.print("ChordOn: ");
                		 for(int j=0;j<note.size();j++){                			 
                			 System.out.print(chordOn.get(j));
                		 }
                		 System.out.println();
                		 System.out.print("ChordOff: ");
                		 for(int j=0;j<note.size();j++){                			 
                			 System.out.print(chordOff.get(j));
                		 }
                		 System.out.println();
                		 
                	 }
                 }*//////
                	
                 /*if(E.getClass().equals(NoteOn.class))
                 {
                	 if(((NoteOn)E).getChannel()==0)
                	 {
                	 int k=((NoteOn)E).getNoteValue();
                	 System.out.println("NoteOn "+" Key: "+ ((NoteOn)E).getNoteValue()+
                			 " NoteName: "+NOTE_NAMES[k%12] );
                	 }
                 }
                 else if(E.getClass().equals(NoteOff.class))
                 {
                	 if(((NoteOff)E).getChannel()==0)
                	 {
                	 int k=((NoteOff)E).getNoteValue();
                	/* System.out.println("NoteOff "+" Key: "+((NoteOff)E).getNoteValue()
                			 +
                 			 " NoteName: "+NOTE_NAMES[k%12] );
                	 }
                 }
                 MidiEvent F;
                 if(it.hasNext())
                 {
                	 F = it.next();
                	 flag=1;
                 }
                 else
                 {
                	 flag=0;
                	 F = E;
                 }
                 if(flag==1 && E.getClass().equals(NoteOn.class) && F.getClass().equals(NoteOn.class))
                 {
                	 if(((NoteOn)E).getChannel()==0 && ((NoteOn)F).getChannel()==0)
                	 {
	                	 chord.clear();
		                 while(F.getClass().equals(NoteOn.class) && it.hasNext())
		                 {               	
		                		 int key =((NoteOn)E).getNoteValue();
		                		 chord.add(key);
		                		 E=F;
		                		 F=it.next();		                		 
		                 }
                	 }
                 int key =((NoteOn)E).getNoteValue();
        		 chord.add(key);
        		 MidiMaker md = new MidiMaker();
        		// String name = "music"+((Integer)counter).toString()+".midi";
        		 note.add(key);
        		 md.play2(chord, "music"+count+".mid");
        		 fileNames.add("music"+count+".mid");
        		 count++;
                 }
                 else if(E.getClass().equals(NoteOn.class))
                 {
                	 if(((NoteOn)E).getChannel()==0)
                	 {
	                	 note.clear();
	                	 int key =((NoteOn)E).getNoteValue();
	                	 note.add(key);
	                	 MidiMaker md = new MidiMaker();
	             		 md.play(key, "music"+count+".mid");
	             		 fileNames.add("music"+count+".mid");
	             		count++;
                	 }
                 }
                 System.out.print("Chord contains: ");
                 for(int j=0; j<chord.size();j++)
                 {
                	 System.out.print(chord.get(j)+",");
                 }
                 System.out.println();
                 System.out.print("Note contains: ");
                 for(int j=0; j<note.size();j++)
                 {
                	 System.out.print(note.get(j)+",");
                 }
                 System.out.println();
                	
              }*/   
             }
      //  }
       // }
	}
}
