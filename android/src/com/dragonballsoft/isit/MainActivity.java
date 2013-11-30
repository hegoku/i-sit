package com.dragonballsoft.isit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity{
	public Button loginb;
	public TextView aa;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		aa = (TextView) findViewById(R.id.copyright);
		loginb=(Button)findViewById(R.id.button1);
		loginb.setOnClickListener(new Button1Listener());

	}

	class Button1Listener implements OnClickListener{
		public void onClick(View v){
			Sqlite mydata=new Sqlite(MainActivity.this,"isit_account");
			SQLiteDatabase db=mydata.getWritableDatabase();
			Cursor cursor=db.rawQuery("select * from account", null);
			int count=cursor.getCount();
			int point=1;
			if(count>1){
				Random rand = new Random();
				point=rand.nextInt(count)+1;
			}
			int i=1;
			while(cursor.moveToNext()){
				if(i==point){
					String id=cursor.getString(cursor.getColumnIndex("teacherID"));
					String pwd=cursor.getString(cursor.getColumnIndex("pwd"));
					login(decode(id),decode(pwd));
				}
				i++;
			}
		}
	}

	public void login(String id,String pwd){
		HttpClient client = new DefaultHttpClient();
		String loginUrl = "https://securelogin.arubanetworks.com/auth/index.html/u";
		HttpPost httpPost = new HttpPost(loginUrl);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user", id));
		params.add(new BasicNameValuePair("password", pwd));
		try{
			httpPost.setEntity(new UrlEncodedFormEntity(params));
		}catch (UnsupportedEncodingException e){
			e.printStackTrace();
		}

		HttpResponse httpResponse = null ;

		try{
			httpResponse = client.execute(httpPost);
			
			if(httpResponse.getStatusLine().getStatusCode() == 200){

				Toast.makeText(MainActivity.this, "ok", Toast.LENGTH_SHORT).show();
				try{
					EntityUtils.toString(httpResponse.getEntity());
					Toast.makeText(MainActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
					
					EasyTracker easyTracker = EasyTracker.getInstance(this);

					  // MapBuilder.createEvent().build() returns a Map of event fields and values
					  // that are set and sent with the hit.
					  easyTracker.send(MapBuilder
					      .createEvent("isit",     // Event category (required)
					                   "button_press",  // Event action (required)
					                   "login",   // Event label
					                   null)            // Event value
					      .build()
					  );
	
					update();
				}catch (ParseException e){
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (IOException e){
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}else
				Toast.makeText(MainActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
			
		}catch (ClientProtocolException e){
			Toast.makeText(MainActivity.this, "请检查网络连接或已登陆", Toast.LENGTH_SHORT).show();
		}catch (IOException e){
			Toast.makeText(MainActivity.this, "请检查网络连接!", Toast.LENGTH_SHORT).show();
			EasyTracker easyTracker = EasyTracker.getInstance(this);

			  // MapBuilder.createEvent().build() returns a Map of event fields and values
			  // that are set and sent with the hit.
			  easyTracker.send(MapBuilder
			      .createEvent("isit",     // Event category (required)
			                   "button_press",  // Event action (required)
			                   "login",   // Event label
			                   null)            // Event value
			      .build()
			  );
		}
	}

	public String encode(String a){
		int i=0;
		String mid="";
		for(i=0;i<a.length();i++){
			String bi=Integer.toBinaryString(Integer.parseInt(String.valueOf(a.charAt(i))));
			int num=4-bi.length();
			for(int j = 0;j<num;j++){
				bi="0"+bi;
			}
			mid+=bi;
		}
		
		String res="";
		for(i=0;i<mid.length();i+=2){
			if(mid.charAt(i)=='0' && mid.charAt(i+1)=='0')res+="#";
			if(mid.charAt(i)=='1' && mid.charAt(i+1)=='1')res+="%";
			if(mid.charAt(i)=='0' && mid.charAt(i+1)=='1')res+="^";
			if(mid.charAt(i)=='1' && mid.charAt(i+1)=='0')res+="&";
		}
		return res;
	}
	
	public String decode(String a){
		String mid="";
		for(int i=0;i<a.length();i++){
			if(a.charAt(i)=='#')mid+="00";
			if(a.charAt(i)=='%')mid+="11";
			if(a.charAt(i)=='^')mid+="01";
			if(a.charAt(i)=='&')mid+="10";
		}
		String res="";
		for(int i=0;i<mid.length();i+=4){
			String aaa="";
			for(int j=i;j<(i+4);j++){
				aaa+=mid.charAt(j);
			}
			res+=Integer.valueOf(aaa,2).toString();
		}
		return res;
	}
	
	public void update(){
		Sqlite mydata=new Sqlite(MainActivity.this,"isit_account");
		SQLiteDatabase db=mydata.getWritableDatabase();
		
		try{
			byte[] data = readParse("http://www.dragonballsoft.cn/download/isit_account.json");
			JSONArray array;
			try{
				array = new JSONArray(new String(data));
				for (int i = 0; i < array.length(); i++) {
					JSONObject item;
					item = array.getJSONObject(i);
					String tid = item.getString("teacherID");
					String pwd = item.getString("pwd");
					Cursor cursor=db.rawQuery("select * from account where teacherID=?", new String[]{tid});
					if(cursor.getCount()==0){
						ContentValues value=new ContentValues();
						value.put("teacherID",tid);
						value.put("pwd", pwd);
						db.insert("account", null, value);
					}
				}
				//Toast.makeText(MainActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
			}catch (JSONException e){
				// TODO Auto-generated catch block
				
			}
		}catch (Exception e1){
			// TODO Auto-generated catch block
			Toast.makeText(MainActivity.this, "更新错误", Toast.LENGTH_SHORT).show();
		}
	}
	
	public static byte[] readParse(String urlPath) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        URL url = new URL(urlPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        InputStream inStream = conn.getInputStream();
        while ((len = inStream.read(data)) != -1) {
                outStream.write(data, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
	}
}
