package com.semanticweb.receipe.receipeapp.Utilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ParseImg {
	
	private BitmapFactory.Options options;
	
	public ParseImg() {
		this.options = new BitmapFactory.Options();
		this.options.inSampleSize = 10;
	}

	public Bitmap parseFromURL(Context context, String realURL){
		Bitmap bm = null;
		try {
			URL url = new URL(realURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			bm = BitmapFactory.decodeStream(conn.getInputStream(), null, options);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			bm = parseFromAssets(context, "noimage.gif");
		} catch (IOException e) {
			e.printStackTrace();
			bm = parseFromAssets(context, "noimage.gif");
		}
		return bm;
	}
	
	public Bitmap parseFromAssets(Context context, String fileName){
		Bitmap bm = null;
		AssetManager assetManager = context.getAssets();
		try {
			InputStream is = assetManager.open(fileName);
			bm = BitmapFactory.decodeStream(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bm;
	}
}
