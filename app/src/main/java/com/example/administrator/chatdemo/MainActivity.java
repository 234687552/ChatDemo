package com.example.administrator.chatdemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.NetUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/22 0022.
 */
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private TextView myName;
    private EditText friendName;
    private ListView contentList;
    private ArrayAdapter<String> contentAdapter;
    private List<String> contents = new ArrayList<String>();

    private EditText toUserName;
    private EditText content;

    private MyMessageListener msgListener;
    private MyContactListener contactListener;
    private MyEMConnectionListener emConnectionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        myName = (TextView) findViewById(R.id.my_name);
        myName.setText(EMClient.getInstance().getCurrentUser());
        friendName = (EditText) findViewById(R.id.friend_name);
        contentList = (ListView) findViewById(R.id.content_list);
        toUserName = (EditText) findViewById(R.id.toUserName);
        content = (EditText) findViewById(R.id.content);
        //设置信息监听器
        msgListener = new MyMessageListener();
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
        //设置好友监听器
        contactListener = new MyContactListener();
        EMClient.getInstance().contactManager().setContactListener(contactListener);
        //注册一个监听连接状态的listener
        emConnectionListener=new MyEMConnectionListener();
        EMClient.getInstance().addConnectionListener(emConnectionListener);
        //获取好友列表
        getFriends();
        //对话列表
        contentAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, contents);
        contentList.setAdapter(contentAdapter);
    }

    private void refreshContent() {
        contentAdapter.notifyDataSetChanged();
    }

    /*
    发送按钮
     */
    public void send(View v) {
        String userName = toUserName.getText().toString().trim();
        String contentText = content.getText().toString().trim();
        if (!userName.equals(myName.getText())&&!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(contentText)) {
            //创建一条文本消息，content为消息文字内容，userName为对方用户或者群聊的id，后文皆是如此
            EMMessage message = EMMessage.createTxtSendMessage(contentText, userName);
            //发送消息
            EMClient.getInstance().chatManager().sendMessage(message);
            contents.add(myName.getText() + ":" + contentText);
            refreshContent();
        }
    }

    /*
     退出
     */
    public void logout(View view) {
        //此方法为异步方法
        EMClient.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub
            }
        });
    }

    /*
    添加好友
     */
    public void addFriend(View view) {
        final String toAddUsername = String.valueOf(friendName.getText());
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        String stri = "Is_sending_a_request";
        progressDialog.setMessage(stri);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        new Thread(new Runnable() {
            public void run() {

                try {
                    //demo use a hardcode reason here, you need let user to input if you like
                    String s = "Add_a_friend";
                    EMClient.getInstance().contactManager().addContact(toAddUsername, s);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s1 = "send_successful";
                            Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s2 = "Request_add_buddy_failure";
                            Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    //获取好友列表
    public void getFriends() {
    }

    /*
    监听好友信息
     */
    private class MyMessageListener implements EMMessageListener {

        @Override
        public void onMessageReceived(final List<EMMessage> messages) {
            //收到消息
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    contents.add(messages.get(0).getUserName() + ":" + ((EMTextMessageBody)messages.get(0).getBody()).getMessage());
                    refreshContent();
                }
            });
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> messages) {
            //收到已读回执
        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> message) {
            //收到已送达回执
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
        }
    }

    /*
    监听好友申请状态
     */
    private class MyContactListener implements EMContactListener {
        @Override
        public void onContactAgreed(final String username) {
            //好友请求被同意

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, ("好友请求被同意:" + username), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onContactRefused(final String username) {
            //好友请求被拒绝
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, ("好友请求被拒绝:" + username), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onContactInvited(final String username, final String reason) {
            //收到好友邀请
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showAgreedDialog(username, reason);
                }
            });
        }

        @Override
        public void onContactDeleted(String username) {
            //被删除时回调此方法
        }


        @Override
        public void onContactAdded(String username) {
            //增加了联系人时回调此方法
        }
    }

    /*
    监听登录情况
     */
    private class MyEMConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
        }

        @Override
        public void onDisconnected(final int error) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logout(getCurrentFocus());
                    if (error == EMError.USER_REMOVED) {
                        Toast.makeText(MainActivity.this, "显示帐号已经被移除", Toast.LENGTH_SHORT).show();
                        // 显示帐号已经被移除
                    } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                        // 显示帐号在其他设备登录
                        Toast.makeText(MainActivity.this, "显示帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                    } else {
                        if (NetUtils.hasNetwork(MainActivity.this)) {
                            //连接不到聊天服务器
                            Toast.makeText(MainActivity.this, "连接不到聊天服务器", Toast.LENGTH_SHORT).show();
                        } else {
                            //当前网络不可用，请检查网络设置
                            Toast.makeText(MainActivity.this, "当前网络不可用，请检查网络设置", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    //好友申请提示dialog
    private void showAgreedDialog(final String username, String reason) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("应用提示")
                .setMessage(
                        "用户 " + username + " 想要添加您为好友，是否同意？\n" + "验证信息：" + reason)
                .setPositiveButton("同意", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            EMClient.getInstance().contactManager().acceptInvitation(username);
                            dialog.dismiss();
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try {
                            EMClient.getInstance().contactManager().declineInvitation(username);
                            dialog.dismiss();
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                    }
                }).show();
    }

    @Override
    protected void onDestroy() {
        EMClient.getInstance().contactManager().removeContactListener(contactListener);
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
        EMClient.getInstance().removeConnectionListener(emConnectionListener);
        super.onDestroy();
    }
}
