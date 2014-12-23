package com.chc.found.fragments;
//package com.chc.found;
//
//import android.os.Bundle;
//import android.support.v4.app.ActionBarActivity;
//import android.support.v4.app.ListFragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ListView;
//
//import com.test.found.R;
//
//public class MenuFragment extends ListFragment implements IMenuView {
//	
//	private MenuPresenter presenter;
//	
//	public MenuFragment() {
//		super();
//		this.presenter = new MenuPresenter(this);
//	}
//
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		return inflater.inflate(R.layout.menu_list, container, false);
//	}
//
//	public void onActivityCreated(Bundle savedInstanceState) {
//		super.onActivityCreated(savedInstanceState);
//		MenuAdapter adapter = new MenuAdapter(getActivity());
//		presenter.addItems(adapter);
//	}
//
//	@Override
//	public void onListItemClick(ListView l, View v, int position, long id) {
//		presenter.onItemClick(position);
//	}
//
//	@Override
//	public MenuAdapter getAdapter() {
//		return (MenuAdapter) this.getListAdapter();
//	}
//
//	@Override
//	public void failToReadProperties() {
//		// TODO to inform user of failure NEEDED?
//		
//	}
//	
//	@Override
//	public ActionBarActivity getParentActivity() {
//		return getActivity();
//	}
//
//	@Override
//	public CHCApplication getCHCApplication() {
//		return getParentActivity() == null
//				? null
//				: (CHCApplication) getParentActivity().getApplication();
//	}
//
//	@Override
//	public void setAdapter(MenuAdapter adapter) {
//		setListAdapter(adapter);
//	}
//	
//}
