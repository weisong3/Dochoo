package com.chcgp.hpad.util.download;

import org.lucasr.smoothie.HttpHelper;
import org.lucasr.smoothie.SimpleItemLoader;

import uk.co.senab.bitmapcache.BitmapLruCache;
import uk.co.senab.bitmapcache.CacheableBitmapDrawable;
import android.view.View;
import android.widget.Adapter;

public abstract class AbstractSmoothieImageLoader extends SimpleItemLoader<String, CacheableBitmapDrawable> {
    private final BitmapLruCache mCache;
    
    public AbstractSmoothieImageLoader(BitmapLruCache cache) {
        mCache = cache;
    }

    @Override
    public CacheableBitmapDrawable loadItemFromMemory(String url) {
        return mCache.getFromMemoryCache(url);
    }

    @Override
    public String getItemParams(Adapter adapter, int position) {
        return (String) adapter.getItem(position);
    }

    @Override
    public CacheableBitmapDrawable loadItem(String url) {
        CacheableBitmapDrawable wrapper = mCache.get(url);
        if (wrapper == null) {
            wrapper = mCache.put(url, HttpHelper.loadImage(url));
        }

        return wrapper;
    }

    @Override
    /**
     * 
     */
    public abstract void displayItem(View itemView, CacheableBitmapDrawable result, boolean fromMemory);
    // sample implementation
    /*
     *  
    {
        ViewHolder holder = (ViewHolder) itemView.getTag();

        if (result == null) {
            holder.title.setText("Failed");
            return;
        }

        result.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);

        if (fromMemory) {
            holder.image.setImageDrawable(result);
        } else {
            BitmapDrawable emptyDrawable = new BitmapDrawable(itemView.getResources());

            TransitionDrawable fadeInDrawable =
                    new TransitionDrawable(new Drawable[] { emptyDrawable, result });

            holder.image.setImageDrawable(fadeInDrawable);
            fadeInDrawable.startTransition(200);
        }

        holder.title.setText("Loaded");
    }
    *
    class ViewHolder {
	    public ImageView image;
	    public TextView title;
	}
     */
}
