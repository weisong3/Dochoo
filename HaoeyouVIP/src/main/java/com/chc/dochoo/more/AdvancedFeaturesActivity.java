package com.chc.dochoo.more;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.chcgp.hpad.util.general.ViewFinder;
import com.test.found.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AdvancedFeaturesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_features);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.advanced_features, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private ListView mListView;

        public PlaceholderFragment() {
            // empty constructor as per android guideline
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_advanced_features, container, false);
            assert rootView != null;

            mListView = (ListView) rootView.findViewById(R.id.feature_list_view);

            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            if (mListView != null) {
                mListView.setAdapter(new FeaturesAdapter(Arrays.asList(FeatureElement.values())));
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Object atPosition = adapterView.getItemAtPosition(i);
                        if (atPosition instanceof FeatureElement) {
                            FeatureElement element = (FeatureElement) atPosition;
                            startActivityFromClass(element.getActivityClass());
                        }
                    }
                });
            }
        }

        private void startActivityFromClass(Class<?> activityClass) {
            if (getActivity() == null) return;
            Intent intent = new Intent(getActivity(), activityClass);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.push_right_in_fast, R.anim.push_left_out_fast);
        }
    }

    private static enum FeatureElement {
        EXPORT(R.string.export_all_message, ExportAllMessagesActivity.class);

        FeatureElement(int stringId, Class<?> activityClass) {
            mStringId = stringId;
            mActivityClass = activityClass;
        }

        public int getStringId() {
            return mStringId;
        }

        public Class<?> getActivityClass() {
            return mActivityClass;
        }

        private final int mStringId;
        private final Class<?> mActivityClass;
    }

    private static class FeaturesAdapter extends BaseAdapter {

        private final List<FeatureElement> mElements;

        FeaturesAdapter(List<FeatureElement> elements) {
            if (elements == null) throw new IllegalArgumentException();
            mElements = elements;
        }

        @Override
        public int getCount() {
            return mElements.size();
        }

        @Override
        public FeatureElement getItem(int i) {
            return mElements.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                //noinspection ConstantConditions
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(android.R.layout.simple_list_item_1, viewGroup, false);
            }

            final FeatureElement item = getItem(i);

            TextView textView = ViewFinder.get(view, android.R.id.text1);
            textView.setText(item.getStringId());

            return view;
        }
    }

}
