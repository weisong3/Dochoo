package com.chc.dochoo.profiles;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.chc.dochoo.CHCApplication;
import com.chc.dochoo.profiles.ProfileFragment.ProfileCallback;
import com.chc.found.models.DoctorUser;
import com.chc.found.models.EntityUser;
import com.chc.found.models.MedicalCenterUser;
import com.chc.found.models.PatientUser;
import com.chc.found.utils.SharedPreferenceUtil;
import com.chcgp.hpad.util.general.CHCGeneralUtil;
import com.test.found.R;

public class ProfileActivity extends ActionBarActivity implements ProfileCallback {
    private static final int PICTURE_RESULT = 0;
    private static final String TAG = ProfileActivity.class.getSimpleName();

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        doCreate();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        doCreate();
    }

    private void doCreate() {
        EntityUser user = CHCApplication.getInstance(this).getUser();
        if (user == null) {
            finish();
            return;
        }
        Class<?> cls = user.getClass();
        Fragment fragment;
        if (cls == PatientUser.class) {
            fragment = new PatientProfileFragment();
        } else if (cls == DoctorUser.class) {
            fragment = new DoctorProfileFragment();
        } else if (cls == MedicalCenterUser.class) {
            fragment = new CenterProfileFragment();
        } else {
            finish();
            return;
        }

        loadFragment(fragment);
    }

    public void takePicture() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, PICTURE_RESULT);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (getSupportFragmentManager().findFragmentById(R.id.profile_fragment_container) == null) {
            transaction.add(R.id.profile_fragment_container, fragment);
        } else if (getSupportFragmentManager().findFragmentById(R.id.profile_fragment_container) != fragment) {
            transaction.replace(R.id.profile_fragment_container, fragment);
            transaction.addToBackStack(fragment.getClass().getName());
        }
        transaction.commit();
    }

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, ProfileActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICTURE_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                // Display image received on the view
                Bundle b = data.getExtras(); // Kept as a Bundle to check for other things in my actual code
                if (b == null) return; // ignore
                Bitmap pic = (Bitmap) b.get("data");

                if (pic != null) { // Display your image in an ImageView in your layout (if you want to test it)
                    // crops the image thumbnail to square
                    pic = CHCGeneralUtil.cropSquareBitmap(pic);

                    SharedPreferenceUtil.writeUserImage(pic, this);
                    Fragment f = getSupportFragmentManager().findFragmentById(R.id.profile_fragment_container);
                    if (f instanceof ProfileFragment)
                        ((ProfileFragment) f).onPictureTaken(pic);
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // ignore
            }
        }

    }

    @Override
    public void onTakePicture() {
        takePicture();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_save) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.profile_fragment_container);
            if (fragment instanceof ProfileFragment) {
                ((ProfileFragment) fragment).updateContent();
            } else {
                // error
                finish();
            }
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.profile_cancel_message)
                .setNegativeButton(R.string.profile_cancel_negative_btn_text, null)
                .setPositiveButton(R.string.profile_cancel_positive_btn_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                    }
                })
                .create().show();
    }
}
