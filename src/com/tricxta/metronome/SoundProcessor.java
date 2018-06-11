package com.tricxta.metronome;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.*;

public class SoundProcessor implements Runnable
{
	
	//-----------------------------------------------------------------------------------
	// CONSTANTS
	//-----------------------------------------------------------------------------------
	
	private static final int INACTIVE_THREAD_SLEEP_MS = 100;
	private static final int ACTIVE_THREAD_SLEEP_MS = 5;

	//-----------------------------------------------------------------------------------
	// PUBLIC FUNCTIONS
	//-----------------------------------------------------------------------------------
	
	//-----------------------------------------------------------------------------------
	public SoundProcessor()
	{
		beatTicks_ = new ArrayList<>();
		ticking_ = false;
	}
	
	//-----------------------------------------------------------------------------------
	@Override
	public void run() 
	{
		while (true)
		{
			try {
		
				if (ticking_) {
					doTickLogic();
					Thread.sleep(ACTIVE_THREAD_SLEEP_MS);
				}
				else {
					Thread.sleep(INACTIVE_THREAD_SLEEP_MS);
				}
			
			}
			catch ( Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	//-----------------------------------------------------------------------------------
	public void StartTicking(int primaryCount, int secondaryCount, int tempoBpm)
	{
		double tempoMod = secondaryCount / 4; 	
		
		tempoBpm_ = tempoBpm;
		lastTickTime_ = System.currentTimeMillis();
		
		//reset tick count
		tickCount_ = 0;
		
		tickFreqMs_ = (long)(1000d / ((double)tempoBpm_ * tempoMod / 60d));
		
		//clear old ticks
		cleanupClips();
		
		//load in sounds for each tick
		for ( int t = 0; t < primaryCount; t++) {
			
			if ( t == 0) {
				beatTicks_.add(loadSoundFromResource("tick.wav"));
			}
			else {
				beatTicks_.add(loadSoundFromResource("tick2.wav"));
			}
		}
		
		ticking_ = true;
	}
	
	//-----------------------------------------------------------------------------------
	public void StopTicking()
	{
		ticking_ = false;
	}
	
	//-----------------------------------------------------------------------------------
	public boolean IsTicking()
	{
		return ticking_;
	}

	//-----------------------------------------------------------------------------------
	// PRIVATE MEMBERS
	//-----------------------------------------------------------------------------------
	
	private int 		tempoBpm_;
	private int			tickCount_;
	private boolean 	ticking_;
	private long 		lastTickTime_;
	private long		tickFreqMs_;
	//in order to play clips immediately on demand, 
	//there is a clip for each beat. When a beat is invoked, 
	//the next beat is prepared so we don't waste what would've been otherwise, idle time.
	//The first beat is always accentuated.
	private List<Clip>  beatTicks_;
	
	//-----------------------------------------------------------------------------------
	// PRIVATE FUNCTIONS
	//-----------------------------------------------------------------------------------
	
	//-----------------------------------------------------------------------------------
	private Clip loadSoundFromResource(String resourceName)
	{
		InputStream				inStream = SoundProcessor.class.getClassLoader().getResourceAsStream(resourceName);
	    AudioInputStream 		stream;
	    AudioFormat 			format;
	    DataLine.Info 			info;
	    final Clip 				clip;
	    
	    try {
		    stream = AudioSystem.getAudioInputStream(new BufferedInputStream(inStream));
		    format = stream.getFormat();
		    info = new DataLine.Info(Clip.class, format);
		    clip = (Clip) AudioSystem.getLine(info);
		    clip.open(stream);
		    
		    return clip;
	    }
	    catch ( Exception ex) {
	    	ex.printStackTrace();
	    	return null;
	    }
	}
	
	//-----------------------------------------------------------------------------------
	private void doTickLogic() throws Exception
	{
		long 	currentTime = System.currentTimeMillis();
		long 	nextTickTime = lastTickTime_ + tickFreqMs_;
		
		
		//check if enough time has elapsed to do another tick
		if ( currentTime >= nextTickTime) {
			
			//do a tick
			beatTicks_.get(tickCount_).start();
			
			lastTickTime_ = nextTickTime;	
			tickCount_ = (tickCount_ + 1) % beatTicks_.size();
			
			//prepare the next tick for playing
			resetClip(beatTicks_.get(tickCount_));
			
			//System.out.println("tick freq:" + tickFreqMs_ + ",tick time:" + nextTickTime + ". Delta(ms) = " + (nextTickTime - currentTime));
		}
		
		
	}
	
	//-----------------------------------------------------------------------------------
	private void resetClip(Clip c)
	{
		if ( c.isRunning()) {
			c.stop();
		}
		
		c.setFramePosition(0);
	}
	
	//-----------------------------------------------------------------------------------
	private void cleanupClips()
	{
		for ( Clip c : beatTicks_) {
			c.close();
		}
		beatTicks_.clear();
	}
}
