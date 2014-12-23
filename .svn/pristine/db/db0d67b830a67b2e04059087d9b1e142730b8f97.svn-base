package com.chc.dochoo.userlogin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.test.found.R;

import org.apache.commons.lang.StringUtils;

public class GeneralAgreementActivity extends ActionBarActivity {

    public static final String BUNDLE_KEY_AGREEMENT_CONTENT = "content";
    public static final String BUNDLE_KEY_AGREEMENT_ACKNOWLEADGE = "acknowledgement";

    public static Intent getStartingIntent(
            Activity a, String content, String acknowledgement) {

        Intent intent = new Intent(a, GeneralAgreementActivity.class);
        Bundle extras = new Bundle();
        extras.putString(BUNDLE_KEY_AGREEMENT_CONTENT, content);
        extras.putString(BUNDLE_KEY_AGREEMENT_ACKNOWLEADGE, acknowledgement);
        intent.putExtras(extras);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_agreement);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (savedInstanceState == null && extras != null) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            fragment.setArguments(extras);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private ScrollView scrollView;
        private TextView textViewContent;
        private CheckBox checkBoxAcknowledgement;
        private Button btnContinue;


        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_general_agreement, container, false);
            assert rootView != null;
            scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
            textViewContent = (TextView) rootView.findViewById(R.id.content);
            checkBoxAcknowledgement = (CheckBox) rootView.findViewById(R.id.checkBox);
            btnContinue = (Button) rootView.findViewById(R.id.buttonContinue);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            Bundle extras = getArguments();
            String content = extras.getString(BUNDLE_KEY_AGREEMENT_CONTENT);
            String acknowleadge = extras.getString(BUNDLE_KEY_AGREEMENT_ACKNOWLEADGE);
            boolean isValid = isValidAgreement(content, acknowleadge);
            if (isValid) {
                Spanned fromHtml = Html.fromHtml(content);
                textViewContent.setText(fromHtml);
                checkBoxAcknowledgement.setText(acknowleadge);
            }
            checkBoxAcknowledgement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    btnContinue.setEnabled(checked);
                }
            });
            btnContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((GeneralAgreementActivity) getActivity()).onContinue();
                }
            });
        }

        private boolean isValidAgreement(String content, String acknowleadge) {
            return StringUtils.isNotBlank(content) && StringUtils.isNotBlank(acknowleadge);
        }
    }

    private void onContinue() {
        setResult(RESULT_OK);
        finish();
        this.overridePendingTransition(0, 0);
    }

}
