package com.semanticweb.receipe.receipeapp;

import android.app.Fragment;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Each fragment shows a information of this ingredient in different language.
 * How many language depends on the data in DBpedia.
 * @author Yi-Ru
 *
 */
public class IngredientFragment extends Fragment {
	
	private TextView ingredientName;
	private TextView ingredientAbstract;
	private String name;
	private String info;
	private String language; 
    
	public static final IngredientFragment newInstance(String ingredient, String info, String language){
		IngredientFragment f = new IngredientFragment();
//		use Bundle to send variables to fragment
        Bundle bdl = new Bundle(1);
        bdl.putString("ingredient", ingredient);
        bdl.putString("info", info);
        bdl.putString("language", language);
        f.setArguments(bdl);
        return f;
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        use getArguments() to read variables
        name = getArguments().getString("ingredient");
        info = getArguments().getString("info");
        language = getArguments().getString("language");
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	return inflater.inflate(R.layout.fragment_pager, container, false);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ingredientName = (TextView) view.findViewById(R.id.pager_ingre_name);
        ingredientAbstract = (TextView) view.findViewById(R.id.pager_ingre_abstract);
        
        ingredientName.setText(name+" in "+language+" : ");
        ingredientAbstract.setText(info);
        ingredientAbstract.setMovementMethod(new ScrollingMovementMethod());
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
