package com.android.intercom.manager;

import android.content.Context;
import android.media.AudioManager;

public class AudioControl {
	private final String TAG = "AudioControl";
	private static AudioManager audioManager;
	private static AudioControl audioControlInstance;
	
	
	
	public static AudioControl getAudioControlInstance(Context context) {
		if( null == audioControlInstance){
			audioControlInstance = new AudioControl();
			audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
			audioManager.setMode(AudioManager.STREAM_VOICE_CALL);
		}
		
		return audioControlInstance;
	}
	
	public void setMicrophoneMute(boolean mute){
		audioManager.setMicrophoneMute(mute);
	}
	
	public void setSpeakerphoneOn(boolean mute){
		audioManager.setSpeakerphoneOn(mute);
	}
	
	
	
	
	
}
