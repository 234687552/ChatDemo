package com.example.administrator.chatdemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/22 0022.
 */
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private TextView userName;
    private EditText friendName;
    private ListView friendList;
    private ArrayAdapter<String> friendAdapter;
    private List<String> friends = new ArrayList<String>();
    private MyMessageListener msgListener;
    private MyContactListener contactListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        userName = (TextView) findViewById(R.id.user_name);
        userName.setText(EMClient.getInstance().getCurrentUser());
        friendName = (EditText) findViewById(R.id.friend_name);
        friendList = (ListView) findViewById(R.id.friend_list);
        //设置信息监听器
        msgListener = new MyMessageListener();
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
        //设置好友监听器
        contactListener = new MyContactListener();
        EMClient.getInstance().contactManager().setContactListener(contactListener);
        //获取好友列表
        getFriends();
    }

    @Override
    protected void onDestroy() {
        EMClient.getInstance().contactManager().removeContactListener(contactListener);
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
        super.onDestroy();
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
        try {
            friends.clear();
            friends = EMClient.getInstance().contactManager().getAllContactsFromServer();
            friendAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, friends);
            friendList.setAdapter(friendAdapter);
            Log.w(TAG, "getFriends: " + friends.size());
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
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
                    Toast.makeText(MainActivity.this, ("收到消息" + messages), Toast.LENGTH_SHORT).show();
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
        public void onContactRefused(String username) {
            //好友请求被拒绝
        }

        @Override
        public void onContactInvited(final String username, final String reason) {
            //收到好友邀请
            try {
                EMClient.getInstance().contactManager().acceptInvitation(username);
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, ("收到好友邀请:" + username + "/原因:" + reason), Toast.LENGTH_SHORT).show();
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
}
