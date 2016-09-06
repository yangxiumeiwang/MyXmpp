package com.xmpp.client.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yxm on 2016/8/31.
 * 链接服务器
 */
public class XmppUtil {

    private static XMPPConnection con = null;

    /**
     * 打开链接
     */
    private static void openConnection() {
        try {
            //url、端口，也可以设置连接的服务器名字，地址，端口，用户。
            ConnectionConfiguration connConfig = new ConnectionConfiguration("192.168.0.116", 5222);
            con = new XMPPConnection(connConfig);
            con.connect();
        } catch (XMPPException xe) {
            xe.printStackTrace();
        }
    }

    /**
     * 获取链接
     *
     * @return
     */
    public static XMPPConnection getConnection() {
        if (con == null) {
            openConnection();
        }
        return con;
    }


    /**
     * 关闭连接
     */
    public static void closeConnection() {
        con.disconnect();
        con = null;
    }

    private static Map<String, Chat> chatManage = new HashMap<String, Chat>();// 聊天窗口管理map集合

    /**
     * 发送消息
     *
     * @param friend    好友名
     * @param listenter 聊天监听器
     * @return
     */
    public static Chat getFriendChat(String friend, MessageListener listenter) {
        if (getConnection() == null)
            return null;
        /** 判断是否创建聊天窗口 */
        for (String fristr : chatManage.keySet()) {
            if (fristr.equals(friend)) {
                // 存在聊天窗口，则返回对应聊天窗口
                return chatManage.get(fristr);
            }
        }
        /** 创建聊天窗口 */
        Chat chat = getConnection().getChatManager().createChat(friend + "@" +
                getConnection().getServiceName(), listenter);
        /** 添加聊天窗口到chatManage */
        chatManage.put(friend, chat);
        return chat;
    }

    /**
     * 获取联系人
     *
     * @return
     */
    public static Roster getRoster() {
        Roster roster = null;
        if (con != null) {
            roster = con.getRoster();
        } else {
            getConnection();
        }
        return roster;
    }

    /**
     * 发送文件
     */
    public static void sendFile(String toUser, String filePath) throws XMPPException {
        if (con != null) {
            FileTransferManager fm = new FileTransferManager(con);
            Presence presence = con.getRoster().getPresence(toUser);//检查传递的用户是否正确
            if (presence == null) {
                System.out.print("用户不存在");
                return;
            } else {
                toUser = presence.getFrom();
                System.out.println("toname" + toUser);
                OutgoingFileTransfer oft = fm.createOutgoingFileTransfer(toUser + "@desktop-4k0rph0");
                oft.sendFile(new File(filePath), "my file");
                System.out.println("sending file status=" + oft.getStatus());
                long startTime = -1;
                while (!oft.isDone()) {
                    if (oft.getStatus().equals(FileTransfer.Status.error)) {
                        System.out.println("error!!!" + oft.getError());
                    } else {
                        double progress = oft.getProgress();
                        if (progress > 0.0 && startTime == -1) {
                            startTime = System.currentTimeMillis();
                        }
                        progress *= 100;
                        System.out.println("status=" + oft.getStatus());
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("used " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds  ");
            }
        } else {
            con = getConnection();
        }
    }

    public static void acceptFile(final String filePath) {

        if (con == null) {
            FileTransferManager fm = new FileTransferManager(con);
            fm.addFileTransferListener(new FileTransferListener() {
                @Override
                public void fileTransferRequest(FileTransferRequest fileTransferRequest) {
                    final IncomingFileTransfer inTransfer = fileTransferRequest.accept();
                    System.out.println("接收到文件发送请求，文件名称：" + fileTransferRequest.getFileName());
                    File file = new File(filePath + "\\" + fileTransferRequest.getFileName());
                    IncomingFileTransfer infiletransfer = fileTransferRequest.accept();
                    try {
                        infiletransfer.recieveFile(file);
                        System.out.println("接收成功！");
                    } catch (XMPPException e) {
                        System.out.println("文件传输失败");
                        e.printStackTrace();
                    }
                }
            });
        } else {
            con = getConnection();
        }
    }

    public static void file(final Context context,final FileTransferRequest request,final File file) {
        final IncomingFileTransfer infiletransfer = request.accept();
        //提示框
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("附件：")
                .setCancelable(false)
                .setMessage("是否接收文件：" + file.getName() + "?")
                .setPositiveButton("接受",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    infiletransfer.recieveFile(file);
                                } catch (XMPPException e) {
                                    Toast.makeText(context, "接收失败!", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                                try {
                                    Thread.sleep(5000);
                                    Toast.makeText(context, "接收完成!", Toast.LENGTH_SHORT).show();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                request.reject();
                                dialog.cancel();
                            }
                        }).show();
    }
}
