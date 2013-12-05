package com.example.custom_camera;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

/*
��Ȩ���У���Ȩ����(C)2013���������
�ļ����ƣ�com.goopai.selfdrive.BaseActivity.java
ϵͳ��ţ�
ϵͳ���ƣ�SelfDrive
ģ���ţ�
ģ�����ƣ�
����ĵ���
�������ڣ�2013-11-14 ����2:06:02
�� �ߣ�½����
����ժҪ���������Activity, ÿ�������е�Activity����Ҫ�̳��������ʵ�ַ��ͳһ
���еĴ�������������Σ���������������������෽������
�ļ�����:
 */
public abstract class BaseActivity extends FragmentActivity {
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	/**
	 * ���ó�ʼ��,���ڹ淶��ҵķ�������
	 */
	protected void startInit(){
		initView();
		initData();
		initEvent();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		/**
		  * ����Ϊ����
		  */
		 if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
		  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		 }
	}

	public abstract void initView();
	public abstract void initData();
	public abstract void initEvent();
	
}


