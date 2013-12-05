package com.example.custom_camera;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * �Զ���SurfaceView����ƬӰ���������ͼ
 * ʵ�������ջص������ӿڣ���������ǰ��Ļص��¼�
 * */
public class Preview extends SurfaceView implements SurfaceHolder.Callback { 
	
	private SurfaceHolder holder; 
	private Camera camera; 
    private static Context mContext;
    private CameraCallback callback=null;
    private int picW=0,picH=0; //Ҫ����ͼƬ�Ĵ�С
    private int currW,currH; //��ǰ��Ļ�ķֱ��ʴ�С
 
	public Preview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext=context;
		//��ȡ��ǰ��Ļ�ֱ���
		getDisplayMetrix();
		// ���SurfaceHolder���� 
		holder = getHolder(); 
		//��������
		holder.setFixedSize(currW, currH);
		// ָ�����ڲ�׽�����¼���SurfaceHolder.Callback���� 
		holder.addCallback(this); 
		//������Ļ����
		holder.setKeepScreenOn(true);
		// ����surfaceView��ά���Լ��Ļ�����,���ǵȴ���Ļ����Ⱦ���潫�������͵��û���ǰ 
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
	}

	public Preview(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext=context;
		//��ȡ��ǰ��Ļ�ֱ���
		getDisplayMetrix();
		// ���SurfaceHolder���� 
		holder = getHolder(); 
		//��������
		holder.setFixedSize(currW, currH);
		// ָ�����ڲ�׽�����¼���SurfaceHolder.Callback���� 
		holder.addCallback(this); 
		//������Ļ����
		holder.setKeepScreenOn(true);
		// ����surfaceView��ά���Լ��Ļ�����,���ǵȴ���Ļ����Ⱦ���潫�������͵��û���ǰ 
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	/**
	 * Preview��Ĺ��췽�� 
	 * @param context
	 */
	@SuppressWarnings("deprecation")
	public Preview(Context context) { 
		super(context); 
		mContext=context;
		//��ȡ��ǰ��Ļ�ֱ���
		getDisplayMetrix();
		// ���SurfaceHolder���� 
		holder = getHolder(); 
		//��������  
		holder.setFixedSize(currW, currH);
		// ָ�����ڲ�׽�����¼���SurfaceHolder.Callback���� 
		holder.addCallback(this); 
		//������Ļ����
		holder.setKeepScreenOn(true);
		// ����surfaceView��ά���Լ��Ļ�����,���ǵȴ���Ļ����Ⱦ���潫�������͵��û���ǰ 
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 
	} 
    
	/**
	 * ����һ��PictureCallback���󣬲�ʵ�����е�onPictureTaken���� 
	 **/
	private PictureCallback pictureCallback = new PictureCallback() { 
	// �÷������ڴ�����������Ƭ���� 
		@Override 
		public void onPictureTaken(byte[] data, Camera camera) { 
			Bitmap bmp=BitmapFactory.decodeByteArray(data, 0, data.length);
			log("data[]="+data+" bmp="+bmp+" camera="+camera);
			if(null!=callback)  
				callback.onCameraEnd(bmp);
		  } 
	}; 
 
	/**
	 * ��ʼ����ʱ���ø÷���
	 * @param holder SurfaceHolder 
	 */
	public void surfaceCreated(SurfaceHolder holder) { 
		try { 
			// ���Camera���� 
			camera = Camera.open(); 
			//ͨ��SurfaceView��ʾȡ������
			camera.setPreviewDisplay(holder); 
			camera.setDisplayOrientation(getPreviewDegree());
			// ��ʼ���� 
			camera.startPreview();
			if(null!=callback) callback.onCameraStart();
		} catch (IOException exception) { 
			// �ͷ��ֻ�����ͷ 
			camera.release(); 
			camera = null; 
		} 
		} 
	
	/**
	 * ֹͣ����ʱ���ø÷��� 
	 * @param holder SurfaceHolder
	 */
	public void surfaceDestroyed(SurfaceHolder holder) { 
		// �ͷ��ֻ�����ͷ 
		if(camera!=null)
			camera.stopPreview();
			camera.release(); 
		} 
	
	/**
	 * ����״̬�仯ʱ���ø÷��� 
	 */
	public void surfaceChanged(final SurfaceHolder holder, int format, int w, int h) { 
		try { 
			// ����������ʾ����Ӱ���SurfaceHolder���� 
				Camera.Parameters parameters = camera.getParameters(); 
				// ������Ƭ��ʽ 
				parameters.setPictureFormat(PixelFormat.JPEG); 
				// ������Ļ��������Ԥ���ߴ��С 
				if (((Activity)mContext).getWindowManager().getDefaultDisplay().getOrientation() == 0) 
				   parameters.setPreviewSize(w, h);
				else parameters.setPreviewSize(h, w); 
				//����ÿ��4֡
				parameters.setPreviewFrameRate(4);
				//������Ƭ������
				parameters.setJpegQuality(85);
				// ����������Ƭ��ʵ�ʷֱ��ʣ������еķֱ�����1024��768 
				Log.d("tag", "w="+w+"h="+h);
				if(picH==0||picW==0){
					parameters.setPictureSize(currW, currH);
				}
				else{
				    parameters.setPictureSize(picW, picH);	
				}
				
				//��������лص�����
				if(null!=callback) callback.onCameraChanged();
				
				// �Զ��Խ� 
				camera.autoFocus(new AutoFocusCallback() { 
					@Override
					public void onAutoFocus(boolean success, Camera camera) {
						// TODO Auto-generated method stub
						if(null!=callback)  callback.onCameraFocus(success);
					} 
				}); 
		} catch (Exception e) { 
		} 
	} 
	
	/**
	 * �ṩһ����̬���������ڸ����ֻ����������Ԥ��������ת�ĽǶ�
	 * @return int
	 */
	public int getPreviewDegree() {
			// ����ֻ��ķ���
			int rotation = ((Activity)mContext).getWindowManager().getDefaultDisplay().getRotation();
//			Log.d("rotation", ""+rotation);
			int degree = 0;
			// �����ֻ��ķ���������Ԥ������Ӧ��ѡ��ĽǶ�
			switch (rotation) {
			case Surface.ROTATION_0:
				degree = 90;
				break;
			case Surface.ROTATION_90:
				degree = 0;
				break;
			case Surface.ROTATION_180:
				degree = 270;
				break;
			case Surface.ROTATION_270:
				degree = 180;
				break;
			}
			return degree;
	}
	
	/**
	 * ֹͣ���գ������������Ƭ����PictureCallback�ӿڵ�onPictureTaken���� 
	 */
	public void takePicture() { 
		if (camera != null) {
		  camera.takePicture(null, null, pictureCallback);
		}
		
	} 
	
	/**
	 * ֹͣʵʱӰ����ʾ��SurfaceView��
	 */
	public void pausePreview(){
		if(camera!=null){
			camera.stopPreview();
		}
	}
	
	/**
	 * ���¿�ʼ��ȡʵʱӰ��SurfaceView��
	 */
	public void resumePreview(){
		if(camera!=null) camera.startPreview();
	}
	
	/**
	 * ������Ƭ����ص��ӿڣ�������Ƭ�������
	 * @param callback ��Ƭ����ص��ӿ�
	 */
	public void setOnCameraCallback(CameraCallback callback){
		this.callback=callback;
	}
	
	/**
	 * ����Ҫ����ͼƬ�Ŀ�Ⱥ͸߶�
	 * @param width Ҫ����ͼƬ�Ŀ��
	 * @param height Ҫ����ͼƬ�ĸ߶�
	 */
	public void setPictureSize(int width,int height){
		this.picW=width;
		this.picH=height;
	}
	
	/**
	 * ��ȡ��ǰ�ֻ���Ļ�ķֱ���
	 */
	public void getDisplayMetrix(){
		DisplayMetrics dm=new DisplayMetrics();
		((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
		this.currH=dm.heightPixels;
		this.currW=dm.widthPixels;
	}
	
	/**
	 * @author Administrator
	 * �Զ�����Ƭ����ص��ӿڣ����Զ�������Activity���лص�ʵ��
	 */
	public interface CameraCallback{
		/**
		 * ���տ�ʼ��������ͷ֮ǰ
		 */
		void onCameraStart();
		
		/**
		 * ���������ڼ� 
		 */
		void onCameraChanged();
		
		/**
		 * �������
		 * @param data Ҫ���ص�����ͷ���ս������
		 */
		void onCameraEnd(Bitmap bitmap);
		
		/**
		 * ���պ󴥷��Խ��¼�
		 * @param bool �Խ��Ƿ�ɹ�
		 * */
		void onCameraFocus(boolean bool);
	}
	
	void log(String msg){
		Log.d(getClass().getSimpleName(), msg);
	}
} 