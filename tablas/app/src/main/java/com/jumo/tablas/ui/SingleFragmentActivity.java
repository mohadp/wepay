package com.jumo.tablas.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;

import com.jumo.tablas.R;
import com.jumo.tablas.ui.util.OnKeyEventListener;


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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        Fragment fragment = getFragmentManager().findFragmentById(getFragmentLayoutResId());

        if(fragment != null && fragment instanceof OnKeyEventListener){
            OnKeyEventListener listener = (OnKeyEventListener) fragment;
            return (listener.onKeyPress(keyCode, event))? true : super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }
}
