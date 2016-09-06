package com.xmpp.client.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xmpp.client.R;
import com.xmpp.client.util.PreferenceUtil;
import com.xmpp.client.util.XmppUtil;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

/**
 * Created by yxm on 2016/8/31.
 * 登陆界面
 */
public class LoginActivity extends Activity implements OnClickListener {

    private EditText useridText, pwdText;
    private LinearLayout layout1, layout2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.formlogin);

        //获取用户和密码
        this.useridText = (EditText) findViewById(R.id.formlogin_userid);
        this.pwdText = (EditText) findViewById(R.id.formlogin_pwd);

        //正在登录
        this.layout1 = (LinearLayout) findViewById(R.id.formlogin_layout1);
        //登录界面
        this.layout2 = (LinearLayout) findViewById(R.id.formlogin_layout2);

        Button btsave = (Button) findViewById(R.id.formlogin_btsubmit);
        btsave.setOnClickListener(this);
        Button btcancel = (Button) findViewById(R.id.formlogin_btcancel);
        btcancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //根据ID来进行提交或者取消
        switch (v.getId()) {
            case R.id.formlogin_btsubmit:
                //取得填入的用户和密码
                final String user = this.useridText.getText().toString();
                final String pwd = this.pwdText.getText().toString();
                Thread t = new Thread(new Runnable() {
                    public void run() {
                        //sendEmptyMessage:发送一条消息
                        handler.sendEmptyMessage(1);
                        try {
                            //连接
                            XmppUtil.getConnection().login(user, pwd);
                            Presence presence = new Presence(Presence.Type.available);
                            XmppUtil.getConnection().sendPacket(presence);
                            PreferenceUtil.setPrefString(LoginActivity.this, "name", user);
                            startActivity(new Intent(LoginActivity.this, FriendActivity.class));
                            LoginActivity.this.finish();
                        } catch (XMPPException e) {
                            XmppUtil.closeConnection();

                            handler.sendEmptyMessage(2);
                        }
                    }
                });
                t.start();
                break;
            case R.id.formlogin_btcancel:
                finish();
                break;
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            if (msg.what == 1) {
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.GONE);
            } else if (msg.what == 2) {
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
                Toast.makeText(LoginActivity.this, "登录失败！", Toast.LENGTH_SHORT).show();
            }
        }

        ;
    };
}