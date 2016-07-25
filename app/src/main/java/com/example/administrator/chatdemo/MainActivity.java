package com.example.administrator.chatdemo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.NetUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/22 0022.
 */
public class MainActivity extends FragmentActivity {
    private static final String TAG = "MainActivity";
    private TextView myName;
    private EditText friendName;
    private Button logoutBtn;

    private ListView groupList;
    private GroupAdapter groupAdapter;
    private List<EMGroup> groups;
    boolean progressDiss=false;

    private ListView friendList;
    private ArrayAdapter<String> friendAdapter;
    private List<String> friends = new ArrayList<String>();


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
        friendList = (ListView) findViewById(R.id.friend_list);
        logoutBtn = (Button) findViewById(R.id.logout_btn);
        groupList = (ListView) findViewById(R.id.group_list);

        //对话列表
        friends = new ArrayList<String>();
        friendAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, friends);
        friendList.setAdapter(friendAdapter);
        friendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                beginTalk(EaseConstant.CHATTYPE_SINGLE, friends.get(position));
            }
        });

        //群聊列表
        groups = EMClient.getInstance().groupManager().getAllGroups();//本地获取
        groupAdapter = new GroupAdapter(this, 1, groups);
        groupList.setAdapter(groupAdapter);
        groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                beginTalk(EaseConstant.CHATTYPE_GROUP, groupAdapter.getItem(position).getGroupId());
            }
        });

        //聊天记录
//        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username);

        //退出监听
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        //设置信息监听器
        msgListener = new MyMessageListener();
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
        //设置好友监听器
        contactListener = new MyContactListener();
        EMClient.getInstance().contactManager().setContactListener(contactListener);
        //注册一个监听连接状态的listener
        emConnectionListener = new MyEMConnectionListener();
        EMClient.getInstance().addConnectionListener(emConnectionListener);


    }


    public void beginTalk(int chatType, String userName) {
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        intent.putExtra("userName", userName);
        intent.putExtra("chatType", chatType);
        startActivity(intent);
    }


    //创建一条发送TextMsg,属isProjectDetail为true的则是专门携带 project 内容携带者。
//    private void projectDetailMsg(int projectId) {
//        // toUserName为对方用户或者群聊的id，
//        EMMessage message = EMMessage.createTxtSendMessage("该条携带project内容", String.valueOf(projectId));
//        message.setChatType(EMMessage.ChatType.GroupChat);
//        //区别这条信息是信息携带者。
//        message.setAttribute("isProjectDetail", true);
//        message.getBooleanAttribute("isProjectDetail", false);
//
//        //携带project的基本信息：isfinish，starttime type
//        message.setAttribute("project_isFinish",true);
//        message.setAttribute("project_type","work");
//        //通过jsonObject来携带所有的ProjectLists；
//        JSONObject jsonObject=new JSONObject();
//        List<String > lists=new ArrayList<String >();
//        try {
//            jsonObject.putOpt("lists",lists);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        message.setAttribute("jsonLists",jsonObject);
//
//        //上传数据到DB方式
//        List<EMMessage> messages=new ArrayList<EMMessage>();
//        messages.add(message);
//        EMClient.getInstance().chatManager().importMessages(messages);
//        //发送message到db方式   send的方式 怎么更新？通过判断按照最新时间的一条？并不能获取到以前历史信息。
//        EMClient.getInstance().chatManager().sendMessage(message);
//    }


    /*
     退出
     */
    public void logout() {
        //我也不知道第一个参数设true为什么意义；参考demo设置为false
        EMClient.getInstance().logout(false, new EMCallBack() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(int i, String s) {
            }

            @Override
            public void onProgress(int i, String s) {
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

    //获取好友列表和群聊
    public void refreshFriendsGroups() {

        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        String stri = "加载朋友和群聊...";
        progressDialog.setMessage(stri);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        //异步获取朋友列表
        new Thread(new Runnable() {
            public void run() {
                try {
                    friends.clear();
                    friends.addAll(EMClient.getInstance().contactManager().getAllContactsFromServer());
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (progressDiss) {
                                progressDialog.dismiss();
                                progressDiss = false;
                            } else {
                                progressDiss = true;
                            }
                            friendAdapter.notifyDataSetChanged();
                        }
                    });

                } catch (final HyphenateException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            progressDiss=false;
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

        //异步获取群聊列表

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().groupManager().getJoinedGroupsFromServer();//从服务器下载到本地db。
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (progressDiss) {
                                progressDialog.dismiss();
                                progressDiss = false;
                            } else {
                                progressDiss = true;
                            }
                            groups = EMClient.getInstance().groupManager().getAllGroups();
                            groupAdapter = new GroupAdapter(MainActivity.this, 1, groups);
                            groupList.setAdapter(groupAdapter);
                            groupAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (final HyphenateException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            progressDiss=false;
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        }).start();

    }

    /*
    监听好友信息
     */
    private class MyMessageListener implements EMMessageListener {
        @Override
        public void onMessageReceived(final List<EMMessage> messages) {
            //收到消息
            for (EMMessage message : messages) {
                if (message.getChatType() == EMMessage.ChatType.GroupChat && message.getFrom() == "project_data") {
                    // TODO: 2016/7/25 0025
                }
            }
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
                    refreshFriendsGroups();
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
            refreshFriendsGroups();
        }
    }

    /*
    监听登录情况
     */
    private class MyEMConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //获取好友列表并更新群聊
                    refreshFriendsGroups();

                }
            });
        }

        @Override
        public void onDisconnected(final int error) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        // 显示帐号已经被移除
                        logout();
                        Toast.makeText(MainActivity.this, "帐号已经被移除", Toast.LENGTH_SHORT).show();
                    } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                        // 显示帐号在其他设备登录
                        logout();
                        Toast.makeText(MainActivity.this, "帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                    } else {
                        if (NetUtils.hasNetwork(MainActivity.this)) {
                            //连接不到聊天服务器
//                            Toast.makeText(MainActivity.this, "连接不到聊天服务器", Toast.LENGTH_SHORT).show();
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
                            refreshFriendsGroups();
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
