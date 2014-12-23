package com.chcgp.hpad.util.fragment.v4;

import android.support.v4.app.Fragment;

import java.lang.reflect.Field;

public abstract class ChildFragmentManagerFixFragment extends Fragment {
	
	@Override
	public void onDetach() {
	    super.onDetach();

	    try {
	    	
	        Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
	        childFragmentManager.setAccessible(true);
	        childFragmentManager.set(this, null);

	    } catch (NoSuchFieldException e) {
	        throw new RuntimeException(e);
	    } catch (IllegalAccessException e) {
	        throw new RuntimeException(e);
	    }
	}
}
