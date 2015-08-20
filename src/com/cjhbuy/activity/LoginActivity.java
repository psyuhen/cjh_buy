package com.cjhbuy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cjhbuy.auth.SessionManager;
import com.cjhbuy.bean.User;
import com.cjhbuy.utils.CommonsUtil;
import com.cjhbuy.utils.HttpUtil;
import com.cjhbuy.utils.JsonUtil;
import com.cjhbuy.utils.SecureUtil;
import com.cjhbuy.utils.Validator;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;

public class LoginActivity extends BaseActivity implements OnClickListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginActivity.class);
	private TextView login_register;
	private TextView login_retrieve;
	private Button login_btn;
	
	//手机号和密码
	private EditText mMobileView;
	private EditText mPasswordView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initView();
		initData();
	}

	private void initData() {
		right_imgbtn.setVisibility(View.GONE);
		title.setText("登录");
	}

	@Override
	public void initView() {
		
		super.initView();
		login_register = (TextView) findViewById(R.id.login_registe);
		login_register.setOnClickListener(this);
		login_retrieve = (TextView) findViewById(R.id.login_retrieve);
		login_retrieve.setOnClickListener(this);
		login_btn = (Button) findViewById(R.id.login_btn);
		login_btn.setOnClickListener(this);
		
		// 初始化登录页面
		mMobileView = (EditText) findViewById(R.id.login_username_edit);
		mPasswordView = (EditText) findViewById(R.id.login_password_edit); 
	}

	@Override
	public void onClick(View v) {
		
		super.onClick(v);
		switch (v.getId()) {
		case R.id.login_registe:
			startActivity(new Intent(LoginActivity.this, RegisteActivity.class));
			break;
		case R.id.login_btn:
			attemptLogin();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 登录事件
	 */
	private void attemptLogin() {
		// 清空错误
		mMobileView.setError(null);
		mPasswordView.setError(null);
		
		// 保存输入的手机号码和密码
		String mobile = mMobileView.getText().toString();
		String password = mPasswordView.getText().toString();
		
		boolean cancel = false;
		View focusView = null;
		
		// 手机号码为必输入的以及正确输入.
		if (TextUtils.isEmpty(mobile)) {
			mMobileView.setError(getString(R.string.error_field_required));
			focusView = mMobileView;
			cancel = true;
		} else if (!Validator.isMoblie(mobile)) {
			mMobileView.setError(getString(R.string.error_invalid_mobile));
			focusView = mMobileView;
			cancel = true;
		}

		// 检验密码.
		if (TextUtils.isEmpty(password)){
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}else if(!Validator.isPwdLength(password)){
			mPasswordView.setError(getString(R.string.error_length_password));
			focusView = mPasswordView;
			cancel = true;
		}else if(!Validator.isPassword(password)){
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}
		

		if (cancel) {
			// 有错误，不登录，焦点在错误的输入框中，并显示错误
			focusView.requestFocus();
		}else{
			//显示进度条，并发送登录请求，准备登录
			password = SecureUtil.shaEncode(password);

			User user = new User();
			user.setMobile(mobile);
			user.setPassword(password);
			user.setUser_type("1");//买家
			// 发送登录请求
			try{
				//先检查手机号是否存在
				String url = HttpUtil.BASE_URL + "/user/isregister.do?mobile="+mobile;
				String json = HttpUtil.getRequest(url);
				if(json == null){
					CommonsUtil.showLongToast(getApplicationContext(), "手机号码不存在");
					sessionManager.putBoolean(SessionManager.IS_LOGIN, false);
					return ;
				}
				
				url = HttpUtil.BASE_URL + "/user/login.do";
				json = HttpUtil.postRequest(url, user);
				if(json == null){
					CommonsUtil.showLongToast(getApplicationContext(), "登录失败,用户密码错误或者网络异常");
					sessionManager.putBoolean(SessionManager.IS_LOGIN, false);
					return ;
				}
				
				user = JsonUtil.parse2Object(json, User.class);
				sessionManager.createLoginSession(user);
				
				startActivity(new Intent(LoginActivity.this, MeActivity.class));
				finish();
			}catch (Exception e) {
				LOGGER.error(">>> 用户登录失败", e);
				CommonsUtil.showLongToast(getApplicationContext(), "用户登录失败");
			}
		}
	}
}