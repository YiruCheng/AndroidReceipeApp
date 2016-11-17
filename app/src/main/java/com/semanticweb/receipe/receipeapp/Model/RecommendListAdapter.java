package com.semanticweb.receipe.receipeapp.Model;

import java.util.List;
import java.util.Map;

import com.semanticweb.receipe.receipeapp.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("ViewHolder")
public class RecommendListAdapter extends BaseAdapter {

	private List<Map<String, String>> result;
	private Context context;
    private String[] from;
    private List<Bitmap> imgList;
    private static LayoutInflater inflater = null;
    
    public RecommendListAdapter(Activity activity, List<Map<String, String>> dataList, List<Bitmap> imgList, String[] from) {
    	result = dataList;
        context = activity;
        this.from = from;
        this.imgList = imgList;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return result.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.listitem_recommend, null);
        holder.recipeName = (TextView) rowView.findViewById(R.id.recipeName);
        holder.recipeDescription = (TextView) rowView.findViewById(R.id.recipeDescription);
        holder.recipeImg = (ImageView) rowView.findViewById(R.id.recipeImg);
        
        holder.recipeName.setText(result.get(position).get(from[0]));
        String descript = result.get(position).get(from[1]);
        holder.recipeDescription.setText(descript.substring(0, (descript.length()/3)));
        holder.recipeImg.setImageBitmap(imgList.get(position));
		return rowView;
	}
	
	public class Holder{
        TextView recipeName;
        TextView recipeDescription;
        ImageView recipeImg;
    }

}
