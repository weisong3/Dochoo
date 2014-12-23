package com.chc.dochoo.contacts;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.chc.found.config.Apis;
import com.chc.found.models.EntityUser;
import com.chc.found.models.PatientUser;
import com.chc.found.utils.PinyinUtils;
import com.chcgp.hpad.util.download.ImageDownloader;
import com.heartcenters.photoupload.dao.Patient;
import com.test.found.R;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lance on 4/22/14.
 */
public class AlphaDoctorPatientListAdapter extends ArrayAdapter<PatientUser> implements SectionIndexer{
    private Context context;
    private List<PatientUser> userList;
    private PatientListFragment.OnPatientOptionListener mCallback;

    public AlphaDoctorPatientListAdapter(Context context, int textViewResourceId, List<PatientUser> objects,PatientListFragment.OnPatientOptionListener mCallback){
        super(context, textViewResourceId, objects);
        this.userList = objects;
        this.context = context;
        this.mCallback = mCallback;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public PatientUser getItem(int position) {
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

        specialty.setVisibility(View.GONE); // patient has no specialty nor degree

        //alphabetize
        LinearLayout sortKeyLayout = (LinearLayout) view.findViewById(R.id.sort_key_layout);
        TextView sortKey = (TextView) view.findViewById(R.id.sort_key);

        if (position == 0) {
            sortKeyLayout.setVisibility(View.VISIBLE);
            sortKey.setText(getSortKey(entityUser.getFullname()));
        } else {
            String lastCatalog = getSortKey(userList.get(position - 1).getPinyinName());
            String catalog = getSortKey(userList.get(position).getPinyinName());
            if(StringUtils.equals(lastCatalog, catalog))
                sortKeyLayout.setVisibility(View.GONE);
            else
                sortKeyLayout.setVisibility(View.VISIBLE);
            sortKey.setText(catalog);
        }

        wrapper.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mCallback.onPatientClicked(entityUser.getId());
            }
        });
        wrapper.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view) {
                mCallback.onPatientLongClicked(entityUser.getId());
                return true;
            }
        });
        if (entityUser.getFullname() != null && !entityUser.getFullname().equals("null"))
            name.setText(entityUser.getFullname());
        else
            name.setText("");
        String url = entityUser.getProfileIconUrl();
        if (url != null && StringUtils.isNotBlank(url)) {
            int size = context.getResources().getDimensionPixelSize(R.dimen.doctor_collabrator_listview_item_height);
            ImageDownloader.getInstance().download(url, imageView,size,size,
                    context.getResources(), R.drawable.patient);

            imageView.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        imageView.setAlpha(0.8f);
                        return true;
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        imageView.setAlpha(1f);
                        mCallback.onPatientImageClicked((PatientUser) entityUser);
                        return true;
                    } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                        imageView.setAlpha(1f);
                        return true;
                    }
                    return false;
                }
            });
        }
        return view;
    }
    public void updateList(List<PatientUser> userList) {
        PatientListFragment.sortPatient(userList);
        this.userList = userList;
        List<EntityUser> entityList = new ArrayList<>();
        for(PatientUser pu:userList)    entityList.add(pu);
        MyCursor myCursor = new MyCursor(entityList);
        notifyDataSetChanged();
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
