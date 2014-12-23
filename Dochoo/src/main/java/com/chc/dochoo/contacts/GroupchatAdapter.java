package com.chc.dochoo.contacts;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.chc.found.models.DoctorUser;
import com.chc.found.models.EntityUser;
import com.chc.found.models.GroupMember;
import com.chc.found.models.MedicalCenterUser;
import com.chc.found.models.PatientUser;
import com.chcgp.hpad.util.download.ImageDownloader;
import com.test.found.R;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lance on 5/23/14.
 */
public class GroupchatAdapter extends BaseAdapter implements SectionIndexer{
    private Context context;
    private List<EntityUser> userList;
    private List<String> memberList;
    private List<String> newMemberList;
    private OnContactOptionListener mCallback;
    private OnContactGroupOptionListener groupCallback;

    public GroupchatAdapter(OnContactOptionListener mCallback, OnContactGroupOptionListener groupCallback, Context context, List<EntityUser> userList, List<GroupMember> currentList) {
        this.mCallback = mCallback;
        this.groupCallback = groupCallback;
        this.context = context;
        this.userList = userList;
        memberList = new ArrayList<>();
        if(currentList != null){
            for(GroupMember member: currentList){
                memberList.add(member.getUserId());
            }
        }
        newMemberList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public EntityUser getItem(int i) {
        return userList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final EntityUser entityUser = getItem(position);
        final View view;
        view = LayoutInflater.from(context).inflate(R.layout.alphabet_doctor_collabrator_list_item, parent, false);
        final ImageView imageView = (ImageView) view.findViewById(R.id.alphabet_doctor_collabrator_listview_item_photo);
        final ViewGroup wrapper = (ViewGroup) view.findViewById(R.id.alphabet_doctor_collabrator_listview_item_wrapper);
        TextView name = (TextView) view
                .findViewById(R.id.alphabet_doctor_collabrator_listview_item_name);
        TextView specialty = (TextView) view
                .findViewById(R.id.alphabet_doctor_collabrator_listview_item_specialty);
        int defaultBitmap = R.drawable.doc_default;
        if (entityUser instanceof DoctorUser){
            String s = ((DoctorUser) entityUser).getDegree();
            s = StringUtils.removeStart(s, "Others^");
            specialty.setText(s);
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
        else if(entityUser instanceof PatientUser){
            specialty.setVisibility(View.GONE); // patient has no specialty nor degree
            defaultBitmap = R.drawable.patient;
        }
        if (entityUser.getFullname() != null && !entityUser.getFullname().contains("null"))
            name.setText(entityUser.getFullname());
        else
            name.setVisibility(View.GONE);

        String url = entityUser.getProfileIconUrl();
        if (url != null && StringUtils.isNotBlank(url)) {
            /*ImageDownloader.getInstance().download(url, imageView,
                    context.getResources(), R.drawable.doc_default);*/
            int size = context.getResources().getDimensionPixelSize(R.dimen.doctor_collabrator_listview_item_height);
            ImageDownloader.getInstance().download(url, imageView,size,size,
                    context.getResources(), defaultBitmap);
        }

        //alphabetize
        LinearLayout sortKeyLayout = (LinearLayout) view.findViewById(R.id.sort_key_layout);
        TextView sortKey = (TextView) view.findViewById(R.id.sort_key);
        if (position == 0) {
            sortKey.setText(getSortKey(entityUser.getPinyinName()));
            sortKeyLayout.setVisibility(View.VISIBLE);
        } else {
            String lastCatalog = getSortKey(userList.get(position - 1).getPinyinName());
            String catalog = getSortKey(userList.get(position).getPinyinName());
            if(StringUtils.equals(lastCatalog, catalog))
                sortKeyLayout.setVisibility(View.GONE);
            else
                sortKeyLayout.setVisibility(View.VISIBLE);
            sortKey.setText(catalog);
        }

        //for group chat
        final CheckBox checkBox = (CheckBox)view.findViewById(R.id.contact_multiple_checkbox);
        checkBox.setVisibility(View.VISIBLE);
        if(memberList.contains(entityUser.getId())){
            checkBox.setChecked(true);
            wrapper.setClickable(false);
            wrapper.setEnabled(false);
            wrapper.setAlpha(0.5f);
            for (int i = 0; i < wrapper.getChildCount(); i++) {
                View child = wrapper.getChildAt(i);
                child.setEnabled(false);
            }
        }

        if(newMemberList.contains(entityUser.getId())){
            checkBox.setChecked(true);
        }
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked){
                    newMemberList.add(entityUser.getId());
                    groupCallback.onContactAddToGroup(entityUser, newMemberList.size());
                }
                else{
                    newMemberList.remove(entityUser.getId());
                    groupCallback.onContactRemoveFromGroup(entityUser, newMemberList.size());
                }
            }
        });

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
                if(checkBox.isChecked()) checkBox.setChecked(false);
                else    checkBox.setChecked(true);
                //mCallback.onContactClicked(entityUser.getId());
            }
        });

        wrapper.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view) {
                mCallback.onContactLongClicked(entityUser.getId());
                return true;
            }
        });
        return view;
    }

    private boolean isValidTextInput(String text) {
        return text != null && StringUtils.isNotBlank(text)
                && !text.equals("null") && !text.contains("null null") && !StringUtils.trim(text).equals(", ,");
    }

    private String getSortKey(String pinyinName) {
        String key = "#";
        if(StringUtils.isNotBlank(pinyinName)){
            key = pinyinName.substring(0, 1).toUpperCase();
        }
        if (key.matches("[A-Z]")) {
            key = key.toUpperCase();
        }
        else key = "#";
        return key;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public int getPositionForSection(int section) {
        for(int i = 0;i<userList.size(); i++){
            String l = StringUtils.isBlank(userList.get(i).getPinyinName())?"":userList.get(i).getPinyinName().substring(0, 1);
            if(!l.matches("[A-Z]") && !l.matches("[a-z]"))  l = "#";
            char firstChar = l.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int i) {
        return 0;
    }
}
