package com.chc.dochoo.contacts;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chc.found.models.DoctorUser;
import com.chc.found.models.EntityUser;
import com.chc.found.models.MedicalCenterUser;
import com.chcgp.hpad.util.download.ImageDownloader;
import com.test.found.R;

import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Created by HenryW on 1/13/14.
 */
@Deprecated
public class DoctorContactListAdapter extends BaseAdapter {

    private Context context;
    private List<EntityUser> userList;
    private OnContactOptionListener mCallback;

    public DoctorContactListAdapter(Context context,
                                    List<EntityUser> userList, OnContactOptionListener mCallback) {
        super();
        this.context = context;
        this.userList = userList;
        this.mCallback = mCallback;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public EntityUser getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final EntityUser entityUser = getItem(position);
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.doctor_collabrator_list_item, parent, false);
        final ImageView imageView = (ImageView) view.findViewById(R.id.doctor_collabrator_listview_item_photo);
//            TextView newMessage = (TextView) view.findViewById(R.id.doctor_collabrator_listview_item_message_count);
        final ViewGroup wrapper = (ViewGroup) view.findViewById(R.id.doctor_collabrator_listview_item_wrapper);
        TextView name = (TextView) view
                .findViewById(R.id.doctor_collabrator_listview_item_name);
        TextView specialty = (TextView) view
                .findViewById(R.id.doctor_collabrator_listview_item_specialty);
        if (entityUser instanceof DoctorUser){
            String s = ((DoctorUser) entityUser).getDegree();
            s = StringUtils.removeStart(s,"Others^");
            specialty.setText(s);
        }
        else if (entityUser instanceof MedicalCenterUser) {
            String address = ((MedicalCenterUser) entityUser).getAddress();
            if (isValidTextInput(address)) {
                specialty.setText(address);
            } else {
                specialty.setText("");
            }
        }
        imageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    imageView.setAlpha(0.7f);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    imageView.setAlpha(1f);
                    mCallback.onContactImageClicked(entityUser);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    imageView.setAlpha(1f);
                    return true;
                }
                return false;
            }
        });

        wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onContactClicked(entityUser.getId());
            }
        });

        wrapper.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view) {
               mCallback.onContactLongClicked(entityUser.getId());
               return true;
            }
        });
//            if (entityUser.getNumUnread() == 0) {
//                newMessage.setVisibility(View.INVISIBLE);
//            } else {
//                newMessage.setVisibility(View.VISIBLE);
//            }
        if (entityUser.getFullname() != null && !entityUser.getFullname().contains("null"))
            name.setText(entityUser.getFullname());
        else
            name.setVisibility(View.GONE);

        String url = entityUser.getProfileIconUrl();
        if (url != null && StringUtils.isNotBlank(url)) {
            Log.i("DoctorProfileDownload","url: " + url);
            ImageDownloader.getInstance().download(url, imageView,
                    context.getResources(), R.drawable.doc_default);
        }

        return view;
    }

    private boolean isValidTextInput(String text) {
        return text != null && StringUtils.isNotBlank(text)
                && !text.equals("null") && !text.contains("null null");
    }

    public void updateList(List<EntityUser> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }
}
