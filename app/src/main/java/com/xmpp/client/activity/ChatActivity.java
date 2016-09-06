package com.xmpp.client.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.xmpp.client.R;
import com.xmpp.client.adapter.ChatAdapter;
import com.xmpp.client.bean.MsgBean;
import com.xmpp.client.util.PreferenceUtil;
import com.xmpp.client.util.TimeRenderUtil;
import com.xmpp.client.util.XmppUtil;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yxm on 2016/8/31.
 * 聊天界面
 */
public class ChatActivity extends Activity {

    @BindView(R.id.formclient_text)
    EditText mMsgText;
    @BindView(R.id.formclient_pb)
    ProgressBar mPtrogressBar;
    @BindView(R.id.formclient_listview)
    ListView mListView;
    @BindView(R.id.formclient_btsend)
    Button btsend;
    @BindView(R.id.formclient_btattach)
    Button btattach;
    private ChatAdapter adapter;
    private List<MsgBean> listMsg = new ArrayList<MsgBean>();

    private FileTransferRequest request;
    private File file;

    private String friendName;
    private ChatManagerListener chatManagerListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);
        ButterKnife.bind(this);

        mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        adapter = new ChatAdapter(listMsg, this);
        mListView.setAdapter(adapter);
        friendName = PreferenceUtil.getPrefString(this, "toName", null);
        ChatManager cm = XmppUtil.getConnection().getChatManager();

        chatManagerListener = new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean b) {
                chat.addMessageListener(new MessageListener() {
                    @Override
                    public void processMessage(Chat chat, Message message) {
                        if (message.getBody() != null) {
                            Log.v("--tags--", "--tags-form--" + message.getBody());
                            MsgBean msgBean = new MsgBean(friendName, message.getBody(), TimeRenderUtil.getDate(), MsgBean.Type.INCOMING);

                            android.os.Message msg = android.os.Message.obtain();
                            msg.what = 1;
                            msg.obj = msgBean;
                            handler.handleMessage(msg);
                        }
                    }
                });
            }
        };
        cm.addChatListener(chatManagerListener);


        //接受文件
        FileTransferManager fileTransferManager = new FileTransferManager(XmppUtil.getConnection());
        fileTransferManager.addFileTransferListener(new RecFileTransferListener());
        XmppUtil.acceptFile("");
    }

    @OnClick({R.id.formclient_btsend, R.id.formclient_btattach})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.formclient_btsend:
                sendMessage();
                break;
            case R.id.formclient_btattach:
                Intent intent = new Intent(ChatActivity.this, FormFilesActivity.class);
                startActivityForResult(intent, 2);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //发送附件
        if (requestCode == 2 && resultCode == 2 && data != null) {

            String filepath = data.getStringExtra("filepath");
            if (filepath.length() > 0) {
                sendFile(filepath);
            }
        }
    }

    /**
     * 发送消息
     */
    private void sendMessage() {
        Chat chat = XmppUtil.getFriendChat(friendName, null);
        String msg = mMsgText.getText().toString();
        if (msg.length() > 0) {
            try {
                listMsg.add(new MsgBean(PreferenceUtil.getPrefString(this, "name", null), msg, TimeRenderUtil.getDate(), MsgBean.Type.OUTCOMING));
                adapter.notifyDataSetChanged();
                chat.sendMessage(msg);
            } catch (XMPPException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(ChatActivity.this, "请输入信息", Toast.LENGTH_SHORT).show();
        }
        mMsgText.setText("");
    }

    //退出
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        XmppUtil.getConnection().getChatManager().removeChatListener(chatManagerListener);
        finish();
    }

    private void sendFile(String filepath) {
        // ServiceDiscoveryManager sdm = new ServiceDiscoveryManager(connection);
        Log.i("send", "send");
        final FileTransferManager fileTransferManager = new FileTransferManager(XmppUtil.getConnection());
        //发送给water-pc服务器，water（获取自己的服务器，和好友）
        final OutgoingFileTransfer fileTransfer = fileTransferManager.createOutgoingFileTransfer(PreferenceUtil.getPrefString(getApplication(), "toName", null) + "@desktop-4k0rph0");

        final File file = new File(filepath);

        try {
            Log.i("send", "send0");
            fileTransfer.sendFile(file, "Sending");
            Log.i("send", "send1");
            Thread.sleep(5000);
            Toast.makeText(ChatActivity.this, "发送成功!", Toast.LENGTH_SHORT).show();

            Log.i("send", fileTransfer.getStatus().toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("send", "send3");
            Log.i("send", e.toString());
            Toast.makeText(ChatActivity.this, "发送失败!", Toast.LENGTH_SHORT).show();
            Log.i("send", "send4");
        }

    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case 1:
                    //获取消息并显示
                    MsgBean msgBean = (MsgBean) msg.obj;
                    listMsg.add(new MsgBean(msgBean.getUserid(),msgBean.getMsg(),msgBean.getDate(),msgBean.getType()));
                    Log.v("--tags--", "--tags---" + "----userid---"+msgBean.getUserid()+"----form----"+msgBean.getMsg()+"---date---"+msgBean.getDate()+"---type---"+msgBean.getType());
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    class RecFileTransferListener implements FileTransferListener {
        @Override
        public void fileTransferRequest(FileTransferRequest prequest) {
            file = new File("mnt/sdcard/" + prequest.getFileName());
            request = prequest;
            XmppUtil.file(ChatActivity.this,request,file);
        }
    }
}