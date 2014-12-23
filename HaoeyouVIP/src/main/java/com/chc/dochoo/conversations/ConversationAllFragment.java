package com.chc.dochoo.conversations;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chc.dochoo.CHCApplication;
import com.chc.found.models.DoctorUser;
import com.chc.found.models.EntityUser;
import com.chc.found.models.MedicalCenterUser;
import com.chc.found.models.PatientUser;
import com.chcgp.hpad.util.download.ImageDownloader;
import com.test.found.R;

import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Displays all conversations
 * Created by HenryW on 1/13/14.
 */
public class ConversationAllFragment extends AbstractConversationListFragment {

    private ConversationPresenter conversationPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        conversationPresenter = new ConversationPresenter(getActivity(), CHCApplication.getInstance(getActivity()).getHelper());
    }

    private void showActions(Context context, final Conversation conversation) {
        new AlertDialog.Builder(context)
                .setMessage(R.string.delete_conversation_confirm)
                .setPositiveButton(R.string.delete_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        doDelete(conversation);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();
    }

    private void doDelete(Conversation conversation) {
        if(conversation instanceof PrivateConversation){
            conversationPresenter.deletePrivateConversation((PrivateConversation)conversation);
        }
        else if(conversation instanceof GroupConversation){
            conversationPresenter.deleteGroupConversation((GroupConversation)conversation);
        }
        reloadMsgList();
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadMsgList();
    }

    /**
     * Loads conversation list from database and refresh it on screen
     */
    private void reloadMsgList() {
        List<Conversation> all = conversationPresenter.getAllConversations();
        if (all == null || all.isEmpty()) {
            onEmptyConversationList(true);
            if (all == null) return;
        } else {
            onEmptyConversationList(false);
            sortList(all);
        }
        if (getListAdapter() == null) {
            setListAdapter(new AllConversationListAdapter(all));
        } else {
            ((AllConversationListAdapter) getListAdapter()).updateList(all);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView listView = getListView();
        if (listView != null) {
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Object item = adapterView.getItemAtPosition(position);
                    if (item instanceof PrivateConversation) {
                        showActions(view.getContext(), (PrivateConversation) item);
                        return true;
                    }
                    else if(item instanceof GroupConversation){
                        showActions(view.getContext(), (GroupConversation) item);
                        return true;
                    }
                    return false;
                }
            });
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Object item = adapterView.getItemAtPosition(position);
                    if (item instanceof PrivateConversation) {
                        onContactClicked(
                                ((PrivateConversation) item).getTargetId(),
                                ((PrivateConversation) item).getId(),
                                false
                        );
                    }
                    if (item instanceof GroupConversation){
                        onContactClicked(
                                ((GroupConversation) item).getTargetGroupchatId(),
                                ((GroupConversation) item).getId(),
                                true
                        );
                    }
                }
            });
        }
    }

    private void sortList(List<Conversation> all) {
        Collections.sort(all, new Comparator<Conversation>() {
            @Override
            public int compare(Conversation conversation, Conversation conversation2) {
                if (conversation == null || conversation2 == null) return 0;
                // sort by descending last message time
                InstantMessage lastMsgLeft = conversation.getLastMsg();
                InstantMessage lastMsgRight = conversation2.getLastMsg();
                if (lastMsgLeft == null && lastMsgRight == null) return 0;
                else if (lastMsgLeft == null) return 1;
                else if (lastMsgRight == null) return -1;

                return ((Long) lastMsgRight.getTime()).compareTo(lastMsgLeft.getTime());
            }
        });
    }

    @Override
    protected void messageReceived() {
        reloadMsgList();
    }

    private class AllConversationListAdapter extends BaseAdapter {

        private List<Conversation> conversations;

        AllConversationListAdapter(List<Conversation> conversations) {
            if (conversations == null) throw new IllegalArgumentException("Null list");
            this.conversations = conversations;
        }

        void updateList(List<Conversation> list) {
            this.conversations = list;
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return this.conversations.size();
        }

        @Override
        public Conversation getItem(int i) {
            return this.conversations.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            final Conversation conversation = getItem(i);
            if (conversation instanceof PrivateConversation) {
                return getPrivateConversationView(i, convertView, viewGroup);
            } else {
                return getGroupConversationView(i, convertView, viewGroup);
            }
        }

        private View getGroupConversationView(int i, View convertView, ViewGroup viewGroup) {
            final Conversation conversation = getItem(i);
            View view = convertView;
            final InstantMessage lastMsg = conversation.getLastMsg();
            final GroupConversation group = (GroupConversation)conversation;
            if (view == null) {
                view = LayoutInflater.from(getActivity()).inflate(R.layout.doctor_collabrator_list_item, viewGroup, false);
            }
            return getConversationView(view, group, null, lastMsg);
        }

        private View getConversationView(View view, Conversation conversation, EntityUser entityUser, final InstantMessage lastMsg){
            final ImageView imageView = (ImageView) view.findViewById(R.id.doctor_collabrator_listview_item_photo);
            TextView numOfNewMsgs = (TextView) view.findViewById(R.id.conversation_message_count);
            TextView name = (TextView) view
                    .findViewById(R.id.doctor_collabrator_listview_item_name);
            TextView specialty = (TextView) view
                    .findViewById(R.id.doctor_collabrator_listview_item_specialty);


            if (conversation != null && conversation.getNumOfUnread() > 0) {
                numOfNewMsgs.setText(String.valueOf(conversation.getNumOfUnread()));
                numOfNewMsgs.setVisibility(View.VISIBLE);
            } else {
                numOfNewMsgs.setVisibility(View.INVISIBLE);
            }

            if(conversation instanceof GroupConversation){
                name.setText(conversation.getTopic());
                imageView.setImageResource(R.drawable.group_conversation);
            }
            if (lastMsg != null) {
                specialty.setText(getMessageContentOrDescription(lastMsg));
            } else {
                specialty.setText("");
            }

            if (entityUser != null) {
                if (entityUser.getFullname() != null && !entityUser.getFullname().contains("null"))
                    name.setText(entityUser.getFullname());
                else
                    name.setVisibility(View.GONE);
                String url = entityUser.getProfileIconUrl();
                int defaultBitmap = R.drawable.doc_default;
                if(entityUser instanceof PatientUser)    defaultBitmap = R.drawable.patient;
                else if (entityUser instanceof MedicalCenterUser) defaultBitmap = R.drawable.group;
                if (url != null && StringUtils.isNotBlank(url)) {
                    ImageDownloader.getInstance().download(url, imageView,
                            getResources(), defaultBitmap);
                }
            }

            return view;
        }

        private View getPrivateConversationView(int i, View convertView, ViewGroup viewGroup) {
            final Conversation conversation = getItem(i);
            View view = convertView;
            final InstantMessage lastMsg = conversation.getLastMsg();
            final EntityUser entityUser = conversationPresenter.getEntityUserFromPrivateConversation((PrivateConversation) conversation);
            if (view == null) {
                view = LayoutInflater.from(getActivity()).inflate(R.layout.doctor_collabrator_list_item, viewGroup, false);
            }
            //final ImageView imageView = (ImageView) view.findViewById(R.id.doctor_collabrator_listview_item_photo);
            getConversationView(view, conversation, entityUser, lastMsg);
            /*TextView numOfNewMsgs = (TextView) view.findViewById(R.id.conversation_message_count);
//            final ViewGroup wrapper = (ViewGroup) view.findViewById(R.id.doctor_collabrator_listview_item_wrapper);
            TextView name = (TextView) view
                    .findViewById(R.id.doctor_collabrator_listview_item_name);
            TextView specialty = (TextView) view
                    .findViewById(R.id.doctor_collabrator_listview_item_specialty);

            if (conversation != null && conversation.getNumOfUnread() > 0) {
                numOfNewMsgs.setText(String.valueOf(conversation.getNumOfUnread()));
                numOfNewMsgs.setVisibility(View.VISIBLE);
            } else {
                numOfNewMsgs.setVisibility(View.INVISIBLE);
            }
            if (entityUser != null && entityUser.getFullname() != null && !entityUser.getFullname().contains("null"))
                name.setText(entityUser.getFullname());
            else
                name.setVisibility(View.GONE);

            if (lastMsg != null) {
                specialty.setText(getMessageContentOrDescription(lastMsg));
            } else {
                specialty.setText("");
            }

            if (entityUser != null) {
                String url = entityUser.getProfileIconUrl();
                if (url != null && StringUtils.isNotBlank(url)) {
                    ImageDownloader.getInstance().download(url, imageView,
                            getResources(), R.drawable.doc_default);
                }
            }
*/
            return view;
        }
    }

    private void onContactClicked(String targetId, String conversationId, Boolean isGroupchat) {
        if(isGroupchat)
            GroupConversationActivity.startActivity(getActivity(),targetId,conversationId, null);
        else
            PrivateConversationActivity.startActivity(getActivity(), targetId, conversationId);
    }

    private void onContactImageClicked(String targetId, String conversationId) {
        //onContactClicked(targetId, conversationId);
    }
}
