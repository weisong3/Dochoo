package com.chc.dochoo.profiles;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.chc.dochoo.CHCApplication;
import com.chc.found.models.EntityUser;
import com.chc.found.views.IProfileView;
import com.test.found.R;

public abstract class ProfileFragment extends Fragment implements IProfileView {
	
	private IProfilePresenter presenter;
	private ProfileCallback mCallback;
    private ProgressDialog mProgress;
	
	public interface ProfileCallback {
		void onTakePicture();
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (ProfileCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ProfileCallback");
        }
    }
	
	@Override
	public void onResume() {
		super.onResume();
        EntityUser user = CHCApplication.getInstance(getActivity()).getUser();
        if (user != null)
            setContent(user);
	}
	
	public IProfilePresenter getPresenter() {
		if (presenter == null) {
			IProfilePresenterFactory factory = AbstractProfilePresenterFactory.getFactory(
                    getProfileTypeClass());
            assert factory != null;
			presenter = factory.getPresenter(this);
		}
		return presenter;
	}

    protected abstract Class<? extends EntityUser> getProfileTypeClass();

    public ProfileCallback getProfileCallback() {
		return mCallback;
	}
	
	@Override
	public void onUpdateProfileFinished(EntityUser profile) {
        dismissProgress();
		Toast.makeText(getActivity(), getString(R.string.toast_success_update_profile), Toast.LENGTH_LONG).show();
        CHCApplication.getInstance(getActivity()).setUser(profile);
		getActivity().finish();
	}

	@Override
	public void onUpdateProfileFailed() {
        dismissProgress();
		Toast.makeText(getActivity(), getString(R.string.toast_failure_update_profile), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onUploadProfileIconFinished() {
        dismissProgress();
		Toast.makeText(getActivity(), getString(R.string.toast_success_upload_icon), Toast.LENGTH_LONG).show();
	}

	@Override
	public void onUploadProfileIconFailed() {
        dismissProgress();
		Toast.makeText(getActivity(), getString(R.string.toast_failure_upload_icon), Toast.LENGTH_SHORT).show();
	}

    protected void displayProgress() {
        if (mProgress == null) mProgress = ProgressDialog.show(
                getActivity(),
                null,
                getString(R.string.network_wait),
                true,
                false);
        else mProgress.show();
    }

    protected void dismissProgress() {
        if (mProgress != null && mProgress.isShowing()) mProgress.dismiss();
    }

	public abstract void setContent(EntityUser profile);
	public abstract void onPictureTaken(Bitmap pic);
	public abstract void updateContent();
}
