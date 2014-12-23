package com.chc.dochoo.contacts;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chc.found.models.ColleagueCenterUser;
import com.chc.found.models.ColleagueDoctorUser;
import com.chc.found.models.DoctorUser;
import com.chc.found.models.EntityUser;
import com.chc.found.models.MedicalCenterUser;
import com.chcgp.hpad.util.download.ImageDownloader;
import com.test.found.R;

import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.List;

/**
 * Created by HenryW on 2/25/14.
 */
public class ColleagueExpandableListAdapter extends BaseExpandableListAdapter {

    private List<ColleagueCenterUser> mList;
    private OnContactOptionListener mCallback;

    public ColleagueExpandableListAdapter(List<ColleagueCenterUser> mList, OnContactOptionListener mCallback) {
        if (mList == null || mCallback == null) throw new IllegalArgumentException();
        this.mCallback = mCallback;
        this.mList = mList;
    }

    @Override
    public int getGroupCount() {
        return mList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return mList.get(i).getColleagueDoctors().size()+1;
    }

    @Override
    public ColleagueCenterUser getGroup(int groupPosition) {
        return mList.get(groupPosition);
    }

    /**
     * Returns the child with corresponding index, where in each group, the first
     * child is the group itself. So the index of the children starts at 1
     * @param groupPosition
     * @param childPosititon
     * @return
     */
    @Override
    public EntityUser getChild(int groupPosition, int childPosititon) {
        ColleagueCenterUser group = getGroup(groupPosition);
        if (childPosititon == 0) return group;
        if (group != null) {
            List<ColleagueDoctorUser> colleagueDoctors = group.getColleagueDoctors();
            if (colleagueDoctors != null) {
                return colleagueDoctors.get(childPosititon - 1);
            }
        }
        return null;
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i2) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        View root = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_colleague_group, viewGroup, false);
        TextView textView = (TextView) root.findViewById(R.id.groupNameTextView);
        String fullname = getGroup(i).getFullname();
        if (StringUtils.isNotBlank(fullname)) textView.setText(fullname);
        return root;
    }

    @Override
    public View getChildView(int i, int i2, boolean b, View view, ViewGroup viewGroup) {
        final EntityUser entityUser = getChild(i, i2);
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.doctor_collabrator_list_item, viewGroup, false);
        final ImageView imageView = (ImageView) view.findViewById(R.id.doctor_collabrator_listview_item_photo);
//            TextView newMessage = (TextView) view.findViewById(R.id.doctor_collabrator_listview_item_message_count);
        final ViewGroup wrapper = (ViewGroup) view.findViewById(R.id.doctor_collabrator_listview_item_wrapper);
        TextView name = (TextView) view
                .findViewById(R.id.doctor_collabrator_listview_item_name);
        TextView specialty = (TextView) view
                .findViewById(R.id.doctor_collabrator_listview_item_specialty);
        int defaultBitmap = R.drawable.doc_default;
        if (entityUser instanceof DoctorUser){
            String degree = ((DoctorUser) entityUser).getDegree();
            if(StringUtils.isNotBlank(degree)){
                degree = StringUtils.removeStart(degree,"Others^");
            }
            else    degree = "";
            specialty.setText(degree);
        }

        else if (entityUser instanceof MedicalCenterUser) {
            String address = ((MedicalCenterUser) entityUser).getAddress();
            if (isValidTextInput(address)) {
                specialty.setText(address);
            } else {
                specialty.setText("");
            }
            defaultBitmap = R.drawable.group;
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
            ImageDownloader.getInstance().download(url, imageView,
                    viewGroup.getContext().getResources(), defaultBitmap);
        }

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        return false;
    }

    private boolean isValidTextInput(String text) {
        return text != null && StringUtils.isNotBlank(text)
                && !text.equals("null") && !text.contains("null null") && !StringUtils.trim(text).equals(", ,");
    }

    public void updateList(List<ColleagueCenterUser> colleagueCenterUsers) {
        this.mList = colleagueCenterUsers;
        notifyDataSetChanged();
    }
}
