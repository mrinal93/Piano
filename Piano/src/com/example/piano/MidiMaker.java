package com.example.piano;
import java.io.*;
import java.util.ArrayList;

import android.os.Environment;

// This program generates a MIDI file with the music 
// Score that in the method is specified.

public class MidiMaker //extends Frame implements ActionListener, WindowListener
{
	// The method score, you specify the melody by 
	// Calling the method of play. 
	// This method takes two parameters: 
	// * An int that specifies the height of the tone (or 0 for silence) 
	// Int * a that indicates the length of the tone

   // private static final long serialVersionUID = 1;

    public void score()
    {
    	// This is an example melody: a scale
        //play2(c,2,e,2,g,2);
        
        //System.out.println("Score done");
    }

    // There are constants available for a few pitches frequently used

    final int c0=48, d0=50, e0=52, f0=53, g0=55, a0=57, b0=59,
              c =60, d =62, e =64, f =65, g =67, a =69, b =71,
              c1=72, d1=74, e1=76, f1=77, g1=79, a1=81, b1=83,
              c2=84, rest=0;


    // these methods is a note sharp (sharp) or flat (apartment) give 
    // Or raise an octave or lower

    static int sharp(int x)
    {
        return x+1;
    }
    static int flat(int x)
    {
        return x-1;
    }
    static int high(int x)
    {
        return x+12;
    }
    static int low(int x)
    {
        return x-12;
    }


    //======================================================================
    // the rest of the program should not be changed


  /*  TextField filenameText, tpqText;
    Button    genButton;
    Label     messageLabel;*/

    final byte NoteOn = (byte) 144;
    final byte NoteOff = (byte) 128;
    final byte defaultVolume = (byte) 100;
    final int  defaultTPQ = 480;

    int timeSinceLastNote;
    int tpq;

    ByteArrayOutputStream track;


  

    public MidiMaker()
    {
        

    }

    public void actionPerformed(String name)
    {
          //messageLabel.setText("generating track");
    	    
           // score();

            try
            {
            	//System.out.println("In try catch");
                DataOutputStream data;
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Piano/Temp";
                File dir = new File(path);
                if(!dir.exists())
                    dir.mkdirs();
                File file = new File(dir,name);
                    if(file.exists()) 
                      file.delete();
                data = new DataOutputStream(new FileOutputStream(file));

                data.writeBytes("MThd");
                data.writeInt(6);
                data.writeInt(1);
                data.writeShort((short)defaultTPQ);
                data.writeBytes("MTrk");
                data.writeInt( track.size() + 4);
                data.write( track.toByteArray() );
                data.writeInt(16723712);
                data.close();
                //System.out.println("action Performed done!");

            }
            catch (Exception e)
            {
                System.out.println("Exception! "+e.getMessage());
            }
        }
    

    void play(int pitch, String name)
    {
    	
    	//System.out.println("In action Performed");
		track = new ByteArrayOutputStream();
        track.reset();
        tpq = defaultTPQ;//Integer.parseInt(tpqText.getText());
        timeSinceLastNote = 0;
    	//System.out.println("Play");
    	double duration=2;
        int durat;
        durat = (int)(duration*tpq/4);

        if (pitch==0)
            timeSinceLastNote += durat;
        else
        {
            sendLength(timeSinceLastNote);
            sendByte( NoteOn );
            sendByte( (byte) pitch );
            sendByte( defaultVolume );

            sendLength(durat);
            sendByte( NoteOff );
            sendByte( (byte) pitch );
            sendByte( defaultVolume );
            timeSinceLastNote = 0;
        }
        actionPerformed(name);
    }
    void play2(ArrayList<Integer> pitch, String name)
    {
    	//System.out.println("In action Performed");
		track = new ByteArrayOutputStream();
        track.reset();
        tpq = defaultTPQ;//Integer.parseInt(tpqText.getText());
        timeSinceLastNote = 0;
    	double duration=2;
    	//System.out.println("Play2");
        int durat;
        durat = (int)(duration*tpq/4);
        
        for(int i=0;i<pitch.size();i++)
        { 	
          	if (pitch.get(i)==0)
        		timeSinceLastNote += durat;
	        else
	        {
	            sendLength(timeSinceLastNote);
	            sendByte( NoteOn );
	            int p = pitch.get(i);
	            sendByte( (byte) p);
	            sendByte( defaultVolume );
	        }
        }
        for(int i=0;i<pitch.size();i++)
        {
	        
	        sendLength(durat);
	        sendByte( NoteOff );
	        int p = pitch.get(i);
	        sendByte( (byte) p);
	        sendByte( defaultVolume );
	        timeSinceLastNote = 0;
        }     

        actionPerformed(name);
    }

    void sendLength(int x)
    {
        if (x>=2097152)
        {
            sendByte((byte)(128+x/2097152));
            x %= 2097152;
        }
        if (x>=16384)
        {
            sendByte((byte)(128+x/16384));
            x %= 16384;
        }
        if (x>=128)
        {
            sendByte((byte)(128+x/128));
            x %= 128;
        }
        sendByte( (byte)x );
    }

    void sendByte(byte b)
    {
        track.write(b);
    }

    
}
