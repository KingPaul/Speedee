package com.kimpotato.speedee;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;


public class LogInActivity extends ActionBarActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private static final int MSG_USERID_FOUND = 1;
    private static final int MSG_LOGIN = 2;
    private static final int MSG_AUTH_CANCEL = 3;
    private static final int MSG_AUTH_ERROR = 4;
    private static final int MSG_AUTH_COMPLETE = 5;
    private boolean isValid = true;
    private int thirdPartLogType = 0;
    private String userId = "";
    private HashMap<String, Object> res = new HashMap<String, Object>();

    private Button btnLogin;
    private TextView tvQQ;
    private TextView tvWeixin;
    private TextView tvSina;

    private TextView tvRegister;
    //    private HttpClass httpClass;
    private MyAsyncTask mat;
    private EditText etTelNum;
    private EditText etPasswd;
    private TextWatcher mTextWatcher;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String userName;
    private String passwd;
    private boolean isRememer = false;
    private boolean isAutoLogin = false;
    private TextView actionbarVisitor;
    private TextView tvForgetPasswd;
    private Handler handler;
    //    private OnLoginListener signupListener;
    private CheckBox cbRemeberPasswd;
    private CheckBox cbAutoLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activtiy_login);
        etTelNum = (EditText) findViewById(R.id.etTelNum);
        etPasswd = (EditText) findViewById(R.id.etPasswd);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        tvQQ = (TextView) findViewById(R.id.tvQQ);
        tvQQ.setOnClickListener(this);
        tvSina = (TextView) findViewById(R.id.tvSina);
        tvSina.setOnClickListener(this);
        tvWeixin = (TextView) findViewById(R.id.tvWeixin);
        tvWeixin.setOnClickListener(this);
        tvForgetPasswd = (TextView) findViewById(R.id.tvForgetPasswd);
        tvForgetPasswd.setOnClickListener(this);

        tvRegister = (TextView) findViewById(R.id.tvRegtister);
        tvRegister.setOnClickListener(this);

        mTextWatcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 用户名修改后，密码置空
                System.out.println("username changed!");
                etPasswd.setText("");
                editor.putString("passwd", "");
                editor.commit();
            }
        };
        etTelNum.addTextChangedListener(mTextWatcher);


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        sp = getSharedPreferences("RememberUser", MODE_PRIVATE);
        editor = sp.edit();
        userName = sp.getString("userName", "");
        passwd = sp.getString("passwd", "");
        isRememer = sp.getBoolean("remember", false);
        isAutoLogin = sp.getBoolean("autologin", false);
        System.out.println(userName + ":" + passwd + ":" + isRememer + ":"
                + isAutoLogin);
        cbRemeberPasswd = (CheckBox) findViewById(R.id.cbRemeberPasswd);
        cbAutoLogin = (CheckBox) findViewById(R.id.cbAutoLogin);
        cbRemeberPasswd.setChecked(isRememer);
        cbAutoLogin.setChecked(isAutoLogin);
        cbRemeberPasswd
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        System.out.println("remember " + isChecked);
                        isRememer = isChecked;
                        if (!isChecked) {
                            // Toast.makeText(LoginActivity.this,
                            // "取消记住密码则无法自动登陆！", Toast.LENGTH_SHORT)
                            // .show();
                            cbAutoLogin.setChecked(false);
                        }

                    }
                });
        cbAutoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                System.out.println("autologin " + isChecked);
                isAutoLogin = isChecked;
                if (isChecked) {
                    // Toast.makeText(LoginActivity.this, "自动登陆需要记住密码！",
                    // Toast.LENGTH_SHORT).show();
                    cbRemeberPasswd.setChecked(true);
                }
            }
        });
        etTelNum.setText(userName);
        if (isRememer) {
            etPasswd.setText(passwd);
        }
        if (isAutoLogin && userName != "" && passwd != "") {
            mat = new MyAsyncTask();
            mat.execute();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                userName = etTelNum.getText().toString();
                passwd = etPasswd.getText().toString();
                if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(LogInActivity.this, "用户名不能为空", Toast.LENGTH_LONG)
                            .show();
                } else if (TextUtils.isEmpty(passwd)) {
                    Toast.makeText(LogInActivity.this, "密码不能为空", Toast.LENGTH_LONG)
                            .show();
                } else {
                    mat = new MyAsyncTask();
                    mat.execute();
                }
                break;
            case R.id.tvRegtister:
//                Intent intentRegister = new Intent(this, InputTelNumActivity.class);
//                startActivity(intentRegister);
                break;
            case R.id.tvForgetPasswd:
                // 忘记密码
//                Intent intentForgetPasswd = new Intent(this,
//                        ForgetPasswdActivity.class);
//                startActivity(intentForgetPasswd);
                break;
            case R.id.tvQQ:
                // 增加qq关联登录代码
                System.out.println("您选择以QQ方式登陆！");
//                thirdPartLogType = 2;
//                ShareSDK.initSDK(LoginActivity.this);
//                Platform qq = ShareSDK.getPlatform(QQ.NAME);
//                authorize(qq);
                break;
            case R.id.tvSina:
                // 增加新浪关联登录代码
                System.out.println("您选择以微博方式登陆！");
//                thirdPartLogType = 3;
//                ShareSDK.initSDK(LoginActivity.this);
//                Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
//                authorize(sina);
                break;
            case R.id.tvWeixin:
                // 增加微信关联登录代码
                System.out.println("您选择以微信方式登陆！");
//                thirdPartLogType = 1;
//                ShareSDK.initSDK(LoginActivity.this);
//                // Platform wechat = ShareSDK.getPlatform(LoginActivity.this,
//                // Wechat.NAME);
//                // wechat.setPlatformActionListener(this);
//                // wechat.authorize();
//                authorize(new Wechat(this));
                break;


        }
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, String> {

        private ProgressDialog dialog;
        private UserInfo userInfo;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btnLogin.setEnabled(false);
//            btnLogin.setBackgroundResource(R.drawable.login_disenable);
            dialog = new ProgressDialog(LogInActivity.this);
            dialog.setTitle("正在登录...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
//            httpClass = new HttpClass(LoginActivity.this);
//            Log.i(TAG, "onPreExecute() called");
        }

        @Override
        protected String doInBackground(String... arg0) {
            String strResult = "success";
            Log.i(TAG, "doInBackground(Params... params) called");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            if (TextUtils.isEmpty(passwd) || TextUtils.isEmpty(userName)) {
//                System.out.println("用户名或者密码不能为空");
//                strResult = null;
//            } else {
//                try {
//					/*
//					 * 判断是手机号还是邮箱登陆
//					 */
//                    if (isEmail(userName)) {
//                        System.out.println("Email方式登陆");
//                        if (httpClass.isEmailRegistered(userName)) {
//                            userInfo = httpClass.loginWithEmail(userName,
//                                    passwd);
//                            if (userInfo != null) {
//                                strResult = "success";
//                            } else {
//                                strResult = null;
//                            }
//                        } else {
//                            System.out.println("该邮箱地址尚未注册");
//                            strResult = "emailnoregister";
//                        }
//
//                    } else if (isTelNum(userName)) {
//                        System.out.println("Tel方式登陆");
//                        if (httpClass.isTelNumRegistered(userName)) {
//
//                            userInfo = httpClass.loginWithTelNum(userName,
//                                    passwd);
//                            if (userInfo != null) {
//                                strResult = "success";
//                            } else {
//                                strResult = null;
//                            }
//                        } else {
//                            System.out.println("该手机号尚未注册");
//                            strResult = "telnoregister";
//                        }
//                    }
//                } catch (ParseException e1) {
//                    // TODO Auto-generated catch block
//                    e1.printStackTrace();
//                } catch (IOException e1) {
//                    // TODO Auto-generated catch block
//                    e1.printStackTrace();
//                } catch (JSONException e1) {
//                    // TODO Auto-generated catch block
//                    e1.printStackTrace();
//                }
//            }
            return strResult;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dialog.dismiss();
            btnLogin.setEnabled(true);
            btnLogin.setBackgroundResource(R.drawable.login_bg);
            if (result == null) {
                Toast.makeText(LogInActivity.this, "登录失败", Toast.LENGTH_LONG)
                        .show();
            } else if (result.equals("telnoregister")) {
                Toast.makeText(LogInActivity.this, "该手机号尚未注册",
                        Toast.LENGTH_LONG).show();
            } else if (result.equals("emailnoregister")) {
                Toast.makeText(LogInActivity.this, "该邮箱地址尚未注册",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(LogInActivity.this, "登录成功", Toast.LENGTH_LONG)
                        .show();
                System.out.println("登录成功：" + result);

                /**
                 * 登陆之后，从主界面返回，不应返回到登陆界面
                 */
//                // 记住密码
                if (isRememer) {
                    System.out.println("本地记住密码：" + userName + ":" + passwd
                            + ":" + isRememer + ":" + isAutoLogin);
                    editor.putString("userName", userName);
                    editor.putString("passwd", passwd);
                    editor.putBoolean("autologin", isAutoLogin);
                    editor.putBoolean("remember", isRememer);
                } else {
                    editor.putString("userName", userName);
                    editor.putString("passwd", "");
                    editor.putBoolean("autologin", false);
                    editor.putBoolean("remember", false);
                }
                editor.commit();
                Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }

    }
}
