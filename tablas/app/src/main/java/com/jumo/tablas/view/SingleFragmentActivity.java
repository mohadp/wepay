package com.jumo.tablas.view;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.jumo.tablas.R;


/**
 * Created by Moha on 2/4/15.
 */
public abstract class SingleFragmentActivity extends ActionBarActivity {
    protected abstract Fragment createFragment();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
		setContentView(getActivityLayoutResId());
		
		FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(getFragmentLayoutResId());

        if(fragment == null){
            fragment = createFragment();
            fm.beginTransaction().add(getFragmentLayoutResId(), fragment).commit();
        }
    }

    protected int getActivityLayoutResId(){
        return R.layout.activity_single_fragment;

    }

    protected int getFragmentLayoutResId(){
        return R.id.fragmentContainer;
    }
}
