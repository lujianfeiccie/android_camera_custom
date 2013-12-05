package com.example.custom_camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 自定义的拍摄照片Activity，返回拍摄后的照片给调用者Activity 
 * 实现Preview.CameraCallback接口，监听拍照过程
 * 调用Action: 
 * */
public class MainActivity extends BaseActivity implements Preview.CameraCallback,OnClickListener{

	LinearLayout bottom;
	View markView=null;   //水印视图
	Button takepicture=null; //拍照按钮
	Button cancel=null;   //取消保存
	Button save =null;   //保存图片
	Preview preview; //拍照界面
	Bitmap result=null;  //拍照后得到的图片
	Bitmap mark=null;  //拍照中要加入的水印相片
	
	PictureClickListener listener; //自定义单击监听器
	
	private String lock="lock";
	
	private int picWidth,picHeight; //要生成的目标图片的宽高
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		super.startInit();
	}

	@Override
	public void onCameraStart() {
		log("onCameraStart");
		//摄像头打开前的准备工作
		mark=ImageHandler.getViewBitmap(markView);
		computeImageWH();
	}

	@Override
	public void onCameraChanged() {
		log("onCameraChanged");
		synchronized (lock) {
		   result=null;	
		}
	}

	@Override
	public void onCameraEnd(final Bitmap bitmap) {
		log("onCameraEnd");
		//拍照完毕后得到返回结果
		preview.pausePreview();
//		focus.setVisibility(View.GONE);
		synchronized (lock) {
			Bitmap zoomBitmap=ImageHandler.zoomBitmap(bitmap, picHeight, picWidth);
			result=rotaingImageView(preview.getPreviewDegree(), zoomBitmap);
			zoomBitmap=null;
		}
	}
	

	@Override
	public void onCameraFocus(boolean bool) {
		log("onCameraFocus");
		//对焦事件回调处理,例如显示对焦图片，提示是否拍照成功
		if(bool){
		    //显示对焦图片
		}
		else{
			Toast.makeText(getApplicationContext(), "拍摄对焦失败了！", 500).show();
		}
	}
	
	/**
	 * 计算要生成的目标图片的宽度和高度
	 */
	private void computeImageWH(){
		//获取状态栏高度
		int statusHeight = 0;
        Rect localRect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        Log.d("tag", "statusHeight:"+statusHeight);
        //获取屏幕分辨率
        DisplayMetrics dm=new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		picWidth=dm.widthPixels;
		picHeight=dm.heightPixels-statusHeight-bottom.getHeight(); //忽略底部按钮的高度
	}
	/**
	 * @author Administrator
	 * 拍照按钮和返回按钮监听器接口 
	 */
	class PictureClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.bt_camera_takepicture:
				preview.takePicture();
				Toast.makeText(getApplicationContext(), "take picture is done!", 500).show();
				break;
			case R.id.bt_camera_cancel:
				preview.resumePreview();
				break;
			case R.id.bt_camera_save:
				Toast.makeText(getApplicationContext(), "picture is save!", 500).show();
				new Thread(){
					public void run(){
				        synchronized (lock) {
				            	saveMarkPicture("pic.jpg", result, mark);
							}
					}
				}.start();
				preview.resumePreview();
				break;
			}
		}
		
	}
	
	/**
	 * 保存图片
	 * @param name
	 * @param source
	 * @param mark
	 * @return String 返回保存文件的路径
	 */
	public String saveFile(String name,Bitmap bm){
		File folder=new File("/mnt/sdcard/mark/");
		if(!folder.exists())
			folder.mkdir();
		File file=new File(folder,name);
		if(file.exists()){
			file.delete();
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		try {
			FileOutputStream fos=new FileOutputStream(file);
			bm.compress(CompressFormat.JPEG, 100, fos);
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return file.getAbsolutePath();
	}
	
	/**
	 * 保存水印图片
	 * @param name
	 * @param source
	 * @param mark
	 */
	public void saveMarkPicture(String name,Bitmap source,Bitmap mark){
		Bitmap bitmap=ImageHandler.watermark(source, mark, 5, 5);
		saveFile(name, bitmap);
	}
	
   /**
    * 旋转图片 
    * @param angle 
    * @param bitmap 
    * @return Bitmap 
    */  
   public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {  
       //旋转图片 动作   
       Matrix matrix = new Matrix();
       matrix.postRotate(angle);
       // 创建新的图片   
       Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,  
               bitmap.getWidth(), bitmap.getHeight(), matrix, true);
       return resizedBitmap;  
   }

	@Override
	public void initView() {
		log("initView");
		takepicture=(Button) findViewById(R.id.bt_camera_takepicture);
		cancel=(Button) findViewById(R.id.bt_camera_cancel);
		save=(Button) findViewById(R.id.bt_camera_save);
		preview=(Preview) findViewById(R.id.preview);
		bottom = (LinearLayout)findViewById(R.id.bottom);
		listener=new PictureClickListener();
		takepicture.setOnClickListener(listener);
		cancel.setOnClickListener(listener);
		save.setOnClickListener(listener);
		markView=findViewById(R.id.txt_mark);
	}

	@Override
	public void initData() {
		log("initData");
	}

	@Override
	public void initEvent() {
		log("initEvent");
		preview.setOnCameraCallback(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	void log(String msg){
		Log.d(getClass().getSimpleName(), msg);
	}
}
