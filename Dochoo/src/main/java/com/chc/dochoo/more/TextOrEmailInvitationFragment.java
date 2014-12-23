package com.chc.dochoo.more;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.chc.dochoo.CHCApplication;
import com.chc.found.presenters.ExternalIntentPresenter;
import com.test.found.R;

import org.apache.commons.lang.StringUtils;

/**
 * Created by HenryW on 2/25/14.
 */
public class TextOrEmailInvitationFragment extends Fragment {

    private EditText mEditText;
    private Button mSmsButton;
    private Button mEmailButton;
    private Button mShareButton;
    private ExternalIntentPresenter mPresenter;

    public static TextOrEmailInvitationFragment newInstance() {
        return new TextOrEmailInvitationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ExternalIntentPresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text_email_invitation, container, false);

        mEditText = (EditText) view.findViewById(R.id.editTextInvitation);
        mSmsButton = (Button) view.findViewById(R.id.buttonSms);
        mEmailButton = (Button) view.findViewById(R.id.buttonEmail);
        mShareButton = (Button) view.findViewById(R.id.buttonOther);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String pin = CHCApplication.getInstance(getActivity()).getUser().getPin();
        if (StringUtils.isNotBlank(pin)) {
            mEditText.setText(formatInvitationText(getActivity(), pin));
        }


        mSmsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.startSmsIntent(getActivity(), mEditText.getText().toString());
            }
        });
        mEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.startEmailIntent(getActivity(), getString(R.string.dochoo_invitation_title), mEditText.getText().toString());
            }
        });
        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.startSendTextIntent(getActivity(), mEditText.getText().toString());
            }
        });
    }

    private String formatInvitationText(Context context, String pin) {
        return String.format(context.getString(R.string.dochoo_invitation_sms), pin);
    }
}
