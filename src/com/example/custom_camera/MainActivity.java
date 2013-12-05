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
 * �Զ����������ƬActivity��������������Ƭ��������Activity 
 * ʵ��Preview.CameraCallback�ӿڣ��������չ���
 * ����Action: 
 * */
public class MainActivity extends BaseActivity implements Preview.CameraCallback,OnClickListener{

	LinearLayout bottom;
	View markView=null;   //ˮӡ��ͼ
	Button takepicture=null; //���հ�ť
	Button cancel=null;   //ȡ������
	Button save =null;   //����ͼƬ
	Preview preview; //���ս���
	Bitmap result=null;  //���պ�õ���ͼƬ
	Bitmap mark=null;  //������Ҫ�����ˮӡ��Ƭ
	
	PictureClickListener listener; //�Զ��嵥��������
	
	private String lock="lock";
	
	private int picWidth,picHeight; //Ҫ���ɵ�Ŀ��ͼƬ�Ŀ��
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		super.startInit();
	}

	@Override
	public void onCameraStart() {
		log("onCameraStart");
		//����ͷ��ǰ��׼������
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
		//������Ϻ�õ����ؽ��
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
		//�Խ��¼��ص�����,������ʾ�Խ�ͼƬ����ʾ�Ƿ����ճɹ�
		if(bool){
		    //��ʾ�Խ�ͼƬ
		}
		else{
			Toast.makeText(getApplicationContext(), "����Խ�ʧ���ˣ�", 500).show();
		}
	}
	
	/**
	 * ����Ҫ���ɵ�Ŀ��ͼƬ�Ŀ�Ⱥ͸߶�
	 */
	private void computeImageWH(){
		//��ȡ״̬���߶�
		int statusHeight = 0;
        Rect localRect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        Log.d("tag", "statusHeight:"+statusHeight);
        //��ȡ��Ļ�ֱ���
        DisplayMetrics dm=new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		picWidth=dm.widthPixels;
		picHeight=dm.heightPixels-statusHeight-bottom.getHeight(); //���Եײ���ť�ĸ߶�
	}
	/**
	 * @author Administrator
	 * ���հ�ť�ͷ��ذ�ť�������ӿ� 
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
	 * ����ͼƬ
	 * @param name
	 * @param source
	 * @param mark
	 * @return String ���ر����ļ���·��
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
	 * ����ˮӡͼƬ
	 * @param name
	 * @param source
	 * @param mark
	 */
	public void saveMarkPicture(String name,Bitmap source,Bitmap mark){
		Bitmap bitmap=ImageHandler.watermark(source, mark, 5, 5);
		saveFile(name, bitmap);
	}
	
   /**
    * ��תͼƬ 
    * @param angle 
    * @param bitmap 
    * @return Bitmap 
    */  
   public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {  
       //��תͼƬ ����   
       Matrix matrix = new Matrix();
       matrix.postRotate(angle);
       // �����µ�ͼƬ   
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
