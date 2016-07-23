package com.example.administrator.chatdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.exceptions.HyphenateException;

public class LoginActivity extends AppCompatActivity {
    private EditText createAccount;
    private EditText createPassword;
    private EditText inputAcount;
    private EditText inputPassword;
    private static final String TAG = "LoginActivity";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

    }
    private void init(){
        createAccount = (EditText) findViewById(R.id.create_account);
        createPassword = (EditText) findViewById(R.id.create_password);
        inputAcount = (EditText) findViewById(R.id.input_account);
        inputPassword = (EditText) findViewById(R.id.input_passwrod);

        EMOptions options = new EMOptions();
        // 默认添加好友时，true是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        //取消自动登录
        options.setAutoLogin(false);
        //初始化
        EMClient.getInstance().init(LoginActivity.this, options);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(false);

        if (EMClient.getInstance().isLoggedInBefore()){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            return;
        }
    }
    /*
    新建账户
     */
    public void createNewAccount(View view){
        final String username= String.valueOf(createAccount.getText());
        final String pwd= String.valueOf(createPassword.getText());
        if (!TextUtils.isEmpty(username)&&!TextUtils.isEmpty(pwd)){
            //注册失败会抛出HyphenateException
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().createAccount(username, pwd);//同步方法
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, ("注册成功"), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (HyphenateException e) {
                        final int errorCode=e.getErrorCode();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, ("" + errorCode), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).start();

        }else {
            Toast.makeText(LoginActivity.this, "输入值不可为空", Toast.LENGTH_SHORT).show();
        }
    }
    /*
    登录
     */
    public void login(View view)
    {

        final String username= String.valueOf(inputAcount.getText());
        String pwd= String.valueOf(inputPassword.getText());
        if (!TextUtils.isEmpty(username)&&!TextUtils.isEmpty(pwd)){
            EMClient.getInstance().login(username, pwd,new EMCallBack() {//回调
                @Override
                public void onSuccess() {
                    EMClient.getInstance().groupManager().loadAllGroups();
                    EMClient.getInstance().chatManager().loadAllConversations();
                    Intent intent =new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, ("登录成功"), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                @Override
                public void onProgress(int progress, String status) {
                }

                @Override
                public void onError(int code, final String message) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }else {
            Toast.makeText(LoginActivity.this, "输入值不可为空", Toast.LENGTH_SHORT).show();
        }
    }


}
