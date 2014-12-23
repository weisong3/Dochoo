package com.chc.dochoo.userlogin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.test.found.R;

import java.util.ArrayList;
import java.util.List;

public class UserRoleSelectActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_role_select);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_role_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private RoleSelectorView roleSelectorView;
        private Button submitBtn;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_user_role_select, container, false);
            List<View> wrappers = new ArrayList<View>();
            assert rootView != null;
            wrappers.add(rootView.findViewById(R.id.wrapper_role_1));
            wrappers.add(rootView.findViewById(R.id.wrapper_role_2));
            wrappers.add(rootView.findViewById(R.id.wrapper_role_3));
            List<View> checks = new ArrayList<View>();
            checks.add(rootView.findViewById(R.id.check1));
            checks.add(rootView.findViewById(R.id.check2));
            checks.add(rootView.findViewById(R.id.check3));

            roleSelectorView = new RoleSelectorView(wrappers, checks);

            for (int i = 0; i < wrappers.size(); i++) {
                final int index = i;
                wrappers.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        roleSelectorView.onCheck(index);
                    }
                });
            }

            submitBtn = (Button) rootView.findViewById(R.id.buttonSubmit);
            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    doSubmit();
                }
            });

            return rootView;
        }

        private void doSubmit() {
            int position = roleSelectorView.getChecked();
            if (position != -1) {
                Activity a = getActivity();
                if (a instanceof UserRoleSelectActivity) {
                    ((UserRoleSelectActivity) a).onRoleSelected(Role.getRole(position));
                }
            }
        }

        private class RoleSelectorView {
            private List<View> wrappers;
            private List<View> checks;
            private int lastChecked = -1;

            private RoleSelectorView(List<View> wrappers, List<View> checks) {
                this.wrappers = wrappers;
                this.checks = checks;
            }

            public void onCheck(int position) {
                submitBtn.setEnabled(true);
                doUnCheck(lastChecked);
                doCheck(position);
                lastChecked = position;
            }

            private void doCheck(int position) {
                View view = wrappers.get(position);
                if (view != null) {
                    view.setBackgroundColor(getResources().getColor(R.color.role_selected_bg));
                }
                view = checks.get(position);
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
            }

            private void doUnCheck(int position) {
                if (position == -1) return;
                View view = wrappers.get(position);
                if (view != null) {
                    view.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
                view = checks.get(position);
                if (view != null) {
                    view.setVisibility(View.INVISIBLE);
                }
            }

            public int getChecked() {
                return lastChecked;
            }
        }
    }

    private void onRoleSelected(Role role) {
        if (role == null) return;

        Intent intent = RegisterInfoActivity.getIntent(this, role);
        startActivity(intent);

    }

}
