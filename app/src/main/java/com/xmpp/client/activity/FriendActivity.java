package com.xmpp.client.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xmpp.client.adapter.FriendAdapter;
import com.xmpp.client.R;
import com.xmpp.client.util.PreferenceUtil;
import com.xmpp.client.util.XmppUtil;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by yxm on 2016/8/31.
 * 好友列表
 */
public class FriendActivity extends Activity {
    @BindView(R.id.friend_listview)
    ListView mListFriend;
    FriendAdapter adapter;
    List<Map<String, String>> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_layout);

        ButterKnife.bind(this);
        list = new ArrayList<Map<String, String>>();

        Observable.create(new Observable.OnSubscribe<List<Map<String, String>>>() {
            @Override
            public void call(Subscriber<? super List<Map<String, String>>> subscriber) {
                Roster roster = XmppUtil.getRoster();
                Collection<RosterGroup> entriesGroup = roster.getGroups();
                for (RosterGroup group : entriesGroup) {
                    Collection<RosterEntry> entries = group.getEntries();
                    for (RosterEntry entry : entries) {
                        Map<String, String> map = new HashMap<>();
                        map.put("name", entry.getName());
                        list.add(map);
                    }
                }
                subscriber.onNext(list);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<Map<String, String>>>() {
                    @Override
                    public void call(List<Map<String, String>> maps) {
                        adapter = new FriendAdapter(maps, FriendActivity.this);
                        mListFriend.setAdapter(adapter);
                    }
                });

        mListFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PreferenceUtil.setPrefString(FriendActivity.this, "toName", list.get(position).get("name"));
                startActivity(new Intent(FriendActivity.this, ChatActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
