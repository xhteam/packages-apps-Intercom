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
	
	public void setAsSpeaker(){
		audioManager.setMode(AudioManager.MODE_IN_CALL);
		audioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
		audioManager.setStreamMute(AudioManager.STREAM_VOICE_CALL,false);
		OpenSpeaker();
	}

	public void OpenSpeaker() {
		if (!audioManager.isSpeakerphoneOn()) {
			audioManager.setSpeakerphoneOn(true);
			audioManager
					.setStreamVolume(
							AudioManager.STREAM_VOICE_CALL,
							audioManager
									.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
							AudioManager.STREAM_VOICE_CALL);
			/*audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 100,
					AudioManager.STREAM_VOICE_CALL);*/
		}
	}

	public void CloseSpeaker() {
		if (audioManager != null) {
			if (audioManager.isSpeakerphoneOn()) {
				audioManager.setSpeakerphoneOn(false);
			}
		}
	}
}
