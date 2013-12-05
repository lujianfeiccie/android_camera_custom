package com.example.custom_camera;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

/*
版权所有：版权所有(C)2013，固派软件
文件名称：com.goopai.selfdrive.BaseActivity.java
系统编号：
系统名称：SelfDrive
模块编号：
模块名称：
设计文档：
创建日期：2013-11-14 上午2:06:02
作 者：陆键霏
内容摘要：最基础的Activity, 每个工程中的Activity都需要继承这个类来实现风格统一
类中的代码包括三个区段：类变量区、类属性区、类方法区。
文件调用:
 */
public abstract class BaseActivity extends FragmentActivity {
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	/**
	 * 启用初始化,用于规范大家的方法命名
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
		  * 设置为竖屏
		  */
		 if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
		  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		 }
	}

	public abstract void initView();
	public abstract void initData();
	public abstract void initEvent();
	
}


