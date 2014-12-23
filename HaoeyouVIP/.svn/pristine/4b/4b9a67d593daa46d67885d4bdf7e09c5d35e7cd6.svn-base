package com.chc.dochoo.contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.chc.found.models.DoctorUser;
import com.chc.found.models.EntityUser;
import com.chc.found.models.MedicalCenterUser;
import com.chc.found.utils.PinyinUtils;
import com.chcgp.hpad.util.download.ImageDownloader;
import com.test.found.R;

import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Created by Lance Ji on 4/20/14.
 */
public class AlphaDoctorContactListAdapter extends BaseAdapter implements SectionIndexer{
    private Context context;
    private List<EntityUser> userList;
    private OnContactOptionListener mCallback;


    public AlphaDoctorContactListAdapter(Context context, List<EntityUser> objects,OnContactOptionListener mCallback){
        super();
        this.userList = objects;
        this.context = context;
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
        view = LayoutInflater.from(context).inflate(R.layout.alphabet_doctor_collabrator_list_item, parent, false);
        final ImageView imageView = (ImageView) view.findViewById(R.id.alphabet_doctor_collabrator_listview_item_photo);
        final ViewGroup wrapper = (ViewGroup) view.findViewById(R.id.alphabet_doctor_collabrator_listview_item_wrapper);
        TextView name = (TextView) view
                .findViewById(R.id.alphabet_doctor_collabrator_listview_item_name);
        TextView specialty = (TextView) view
                .findViewById(R.id.alphabet_doctor_collabrator_listview_item_specialty);

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
        if (entityUser.getFullname() != null && !entityUser.getFullname().contains("null"))
            name.setText(entityUser.getFullname());
        else
            name.setVisibility(View.GONE);

        String url = entityUser.getProfileIconUrl();
        if (url != null && StringUtils.isNotBlank(url)) {
            /*ImageDownloader.getInstance().download(url, imageView,
                    context.getResources(), R.drawable.doc_default);*/
            int size = context.getResources().getDimensionPixelSize(R.dimen.doctor_collabrator_listview_item_height);
            int defaultBitmap = R.drawable.doc_default;
            if (entityUser instanceof MedicalCenterUser) defaultBitmap = R.drawable.group;
            ImageDownloader.getInstance().download(url, imageView,size,size,
                    context.getResources(), defaultBitmap);
        }

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

    public void updateList(List<EntityUser> userList) {
        MyCursor myCursor = new MyCursor(userList);
        this.userList = userList;
        notifyDataSetChanged();
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
