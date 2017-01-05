package com.manbas.downmusic.utlis;

import android.os.Environment;
import android.util.Log;

import com.manbas.downmusic.base.LogUtis;
import com.manbas.downmusic.bean.LyricContent;
import com.manbas.downmusic.config.Config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


public class LrcRead {


	private List<LyricContent> LyricList;

	private LyricContent mLyricContent;


	public LrcRead(){

		mLyricContent=new LyricContent();

		LyricList=new ArrayList<LyricContent>();

	}

	/*
	* 缓存在线歌曲文件*
	*/
	public void ReadOnline(String urlPath,String songId){

		String lrcPath=getLrcPath(songId);
		LogUtis.Log("lrcpath："+lrcPath);

		try {
			URL url=new URL(urlPath);
			URLConnection urlConnection=url.openConnection();
			urlConnection.connect();
			HttpURLConnection httpConn= (HttpURLConnection) urlConnection;
			if(httpConn.getResponseCode()==HttpURLConnection.HTTP_OK){
				String sdcardpath="";
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					 sdcardpath= Environment.getExternalStorageDirectory().getAbsolutePath();
					File file=new File(sdcardpath+"/manbasjiMusic/");
					if(!file.exists()){
						file.mkdirs();
					}
				}else{
					Log.i("sdcard","sdcard不存在");
				}
				Log.i("sdcard","sdcard===="+sdcardpath);
			}

			BufferedReader bf=new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"utf-8"));
			PrintWriter out=new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(lrcPath),"utf-8")));
			char c[]=new char[256];
			int temp = -1;
			while ((temp=bf.read())!=-1){
				bf.read(c);
				out.write(c);
			}

			bf.close();
			out.close();

		} catch (MalformedURLException e) {
			e.printStackTrace();
			LogUtis.Log(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			LogUtis.Log(e.getMessage());
		}

	}

	public List<LyricContent> Read(String file) throws FileNotFoundException,IOException{

		String Lrc_data="";
		File mFile =new File(file);
		FileInputStream mFileInputStream = new FileInputStream(mFile);
		InputStreamReader mInputStreamReader= new InputStreamReader(mFileInputStream,"utf-8");

		BufferedReader mBufferedReader=new BufferedReader(mInputStreamReader);

		while((Lrc_data=mBufferedReader.readLine())!=null){

			Lrc_data=Lrc_data.replace("[", "");

			Lrc_data=Lrc_data.replace("]", "@");

			String splitLrc_data[]=Lrc_data.split("@");

			if(splitLrc_data.length>1){
				
				mLyricContent.setLyric(splitLrc_data[1]);
				
				int LyricTime= TimeStr(splitLrc_data[0]);
				
				mLyricContent.setLyricTime(LyricTime);
				
				LyricList.add(mLyricContent);
				
				mLyricContent=new LyricContent();
			}

		}

		mBufferedReader.close();

		mInputStreamReader.close();

		return LyricList;
	}
        
	    public int TimeStr(String timeStr){
			
			timeStr=timeStr.replace(":", ".");
			timeStr=timeStr.replace(".", "@");
			
			String timeData[]=timeStr.split("@");
			LogUtis.Log("timeData:"+timeData);
			if(timeData.length>2){
				try {
					int minute=Integer.parseInt(timeData[0]);
					int second=Integer.parseInt(timeData[1]);
					LogUtis.Log("timeData[1]:"+timeData[1]);
					LogUtis.Log("timeData[0]:"+timeData[0]);
					int millisecond=Integer.parseInt(timeData[2]);
					LogUtis.Log("timeData[2]:"+timeData[2]);
					int currentTime=(minute*60+second)*1000+millisecond*10;
					return currentTime;
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}

	    	return 0;

	    }
      
	    public List<LyricContent> GetLyricContent(){
	    	
			return LyricList;
	    }

		public String getLrcPath(String songId){
			return Config.LRCPath+songId+".lrc";
		}
}

