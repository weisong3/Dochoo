package com.chc.found;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.chc.dochoo.CHCApplication;
import com.chc.found.config.Apis;
import com.chc.found.models.AddEntityState;
import com.chc.found.models.EntityUser;
import com.chc.found.network.NetworkRequestsUtil;
import com.chc.found.presenters.EntityPresenter;
import com.chc.found.views.IEntityView;
import com.chcgp.hpad.util.download.ImageDownloader;
import com.chcgp.hpad.util.general.CHCGeneralUtil;
import com.devsmart.android.ui.HorizontalListView;
import com.test.found.R;

import java.io.File;
import java.util.List;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouch.OnImageViewTouchDoubleTapListener;
import it.sephiroth.android.library.imagezoom.ImageViewTouch.OnImageViewTouchSingleTapListener;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.DisplayType;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.OnDrawableChangeListener;

public class ImageViewerActivity extends ActionBarActivity implements
		IEntityView {

	private static final String LOG_TAG = "imageviewer";
	public static final String GALLARY_ARRAY = "id";
	public static final String ENTRANCE_ID = "entrance_id";
	private static final int MAX_TEXTURE_SIZE = 2048;

	GalleryAdapter adapter;
	EntityPresenter presenter;
	ImageViewTouch mImage;
	HorizontalListView galleryListView;

	static int displayTypeCount = 0;

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		super.onNewIntent(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_image_viewer);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (presenter == null) {
			presenter = new EntityPresenter(this);
		}
		galleryListView = (HorizontalListView) findViewById(R.id.imageviewer_gallery);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String[] galleryArray = extras.getStringArray(
                    GALLARY_ARRAY);
            String uri = extras.getString(ENTRANCE_ID);

            if (adapter == null) {
                adapter = new GalleryAdapter(galleryArray, this);
                galleryListView.setAdapter(adapter);
            } else {
                adapter.updateArray(galleryArray);
            }
            selectImage(uri);
        }
	}

    @Override
    public void onSupportContentChanged() {
        super.onSupportContentChanged();
        // set the default image display type
        mImage = (ImageViewTouch) findViewById(R.id.imageviewer_image);
        mImage.setDisplayType(DisplayType.FIT_TO_SCREEN);

        mImage.setSingleTapListener(new OnImageViewTouchSingleTapListener() {

            @Override
            public void onSingleTapConfirmed() {
                if (Apis.DEBUG) Log.d(LOG_TAG, "onSingleTapConfirmed");
            }
        });

        mImage.setDoubleTapListener(new OnImageViewTouchDoubleTapListener() {

            @Override
            public void onDoubleTap() {
                if (Apis.DEBUG) Log.d(LOG_TAG, "onDoubleTap");
            }
        });

        mImage.setOnDrawableChangedListener(new OnDrawableChangeListener() {

            @Override
            public void onDrawableChanged(Drawable drawable) {
                if (Apis.DEBUG) Log.i(LOG_TAG, "onBitmapChanged: " + drawable);
            }
        });
    }

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	Matrix imageMatrix;

	public void selectImage(String uri) {
		Bitmap bitmap = getBitmapFromURL(uri);
		if (null != bitmap) {
			// calling this will force the image to fit the ImageView container
			// width/height

			if (null == imageMatrix) {
				imageMatrix = new Matrix();
			} else {
				// get the current image matrix, if we want restore the
				// previous matrix once the bitmap is changed
				// imageMatrix = mImage.getDisplayMatrix();
			}
			mImage.setImageBitmap(bitmap, imageMatrix.isIdentity() ? null
					: imageMatrix, ImageViewTouchBase.ZOOM_INVALID,
					ImageViewTouchBase.ZOOM_INVALID);

		} else {
			Toast.makeText(this, "Failed to load the image", Toast.LENGTH_LONG)
					.show();
		}
	}

	private class GalleryAdapter extends BaseAdapter {
		private String[] galleryArray;
		Context context;

		public GalleryAdapter(String[] galleryArray, Context context) {
			super();
			this.galleryArray = galleryArray;
			this.context = context;
		}

		public void updateArray(String[] galleryArray) {
			this.galleryArray = galleryArray;
			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return galleryArray.length;
		}

		@Override
		public String getItem(int position) {
			return galleryArray[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = LayoutInflater.from(context).inflate(
					R.layout.center_gallery_item, parent, false);
			ImageView imageView = (ImageView) view
					.findViewById(R.id.center_gallery_item_imageview);
			final String uri = NetworkRequestsUtil
					.getGalleryImageUrlString(getItem(position));
			int dimen = getResources().getDimensionPixelSize(R.dimen.center_gallery_item);
			ImageDownloader.getInstance().download(uri, imageView, dimen, dimen, getResources(), R.drawable.doc_default);

			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					selectImage(uri);
				}
			});
			return view;
		}

	}

	private Bitmap getBitmapFromURL(String url) {
		String filename = String.valueOf(url.hashCode());

		File f = new File(getCacheDirectory(mImage.getContext()), filename);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int screenWidth = metrics.widthPixels;
		int screenHeight = metrics.heightPixels;
		
		if (f.exists() && f.isFile()) {
			return CHCGeneralUtil.decodeFileMaximumSize(f, MAX_TEXTURE_SIZE);
//			return Bitmap.createScaledBitmap(bitmap, screenWidth, (int) ((double) bitmap.getHeight() / bitmap.getWidth() * screenWidth), false);
		}

		return null;
	}

	private static File getCacheDirectory(Context context) {
		String sdState = android.os.Environment.getExternalStorageState();
		File cacheDir;

		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File sdDir = android.os.Environment.getExternalStorageDirectory();

			// TODO : Change your diretcory here
			cacheDir = new File(sdDir, "/data/chc/cache");
		} else
			cacheDir = context.getCacheDir();

		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
		return cacheDir;
	}

	@Override
	public CHCApplication getCHCApplication() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onEntityLoaded(EntityUser user) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEntityLoaded(List<EntityUser> user) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getEntityFailed(AddEntityState state) {
		// TODO Auto-generated method stub

	}
}
