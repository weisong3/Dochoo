package com.chc.found;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.chc.found.fragments.NewsDetailFragment;
import com.test.found.R;

public class NewsActivity extends ActionBarActivity {
	private static final String KEY_ID = "id";

	public static void startActivity(Context context, String id) {
		Intent intent = new Intent(context, NewsActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(KEY_ID, id);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Bundle extras = getIntent().getExtras();
        if (extras == null) return;
        Fragment fragment = NewsDetailFragment.newInstance(extras.getString(KEY_ID));
		transaction.add(R.id.fragment_container, fragment);
		transaction.commit();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
