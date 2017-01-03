package com.manbas.downmusic.bean;

import java.io.Serializable;

public class LyricContent implements Serializable{
  
    private String Lyric;
    
    private int LyricTime;

	public String getLyric() {
		return Lyric;
	}

	public void setLyric(String lyric) {
		Lyric = lyric;
	}

	public int getLyricTime() {
		return LyricTime;
	}

	public void setLyricTime(int lyricTime) {
		LyricTime = lyricTime;
	}
	
}
