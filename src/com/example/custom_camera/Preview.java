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
 * 自定义SurfaceView，照片影像的容器视图
 * 实现了拍照回调监听接口，处理拍照前后的回调事件
 * */
public class Preview extends SurfaceView implements SurfaceHolder.Callback { 
	
	private SurfaceHolder holder; 
	private Camera camera; 
    private static Context mContext;
    private CameraCallback callback=null;
    private int picW=0,picH=0; //要拍照图片的大小
    private int currW,currH; //当前屏幕的分辨率大小
 
	public Preview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext=context;
		//获取当前屏幕分辨率
		getDisplayMetrix();
		// 获得SurfaceHolder对象 
		holder = getHolder(); 
		//设置像素
		holder.setFixedSize(currW, currH);
		// 指定用于捕捉拍照事件的SurfaceHolder.Callback对象 
		holder.addCallback(this); 
		//保持屏幕常亮
		holder.setKeepScreenOn(true);
		// 设置surfaceView不维护自己的缓冲区,而是等待屏幕的渲染引擎将内容推送到用户面前 
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
	}

	public Preview(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext=context;
		//获取当前屏幕分辨率
		getDisplayMetrix();
		// 获得SurfaceHolder对象 
		holder = getHolder(); 
		//设置像素
		holder.setFixedSize(currW, currH);
		// 指定用于捕捉拍照事件的SurfaceHolder.Callback对象 
		holder.addCallback(this); 
		//保持屏幕常亮
		holder.setKeepScreenOn(true);
		// 设置surfaceView不维护自己的缓冲区,而是等待屏幕的渲染引擎将内容推送到用户面前 
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	/**
	 * Preview类的构造方法 
	 * @param context
	 */
	@SuppressWarnings("deprecation")
	public Preview(Context context) { 
		super(context); 
		mContext=context;
		//获取当前屏幕分辨率
		getDisplayMetrix();
		// 获得SurfaceHolder对象 
		holder = getHolder(); 
		//设置像素  
		holder.setFixedSize(currW, currH);
		// 指定用于捕捉拍照事件的SurfaceHolder.Callback对象 
		holder.addCallback(this); 
		//保持屏幕常亮
		holder.setKeepScreenOn(true);
		// 设置surfaceView不维护自己的缓冲区,而是等待屏幕的渲染引擎将内容推送到用户面前 
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 
	} 
    
	/**
	 * 创建一个PictureCallback对象，并实现其中的onPictureTaken方法 
	 **/
	private PictureCallback pictureCallback = new PictureCallback() { 
	// 该方法用于处理拍摄后的照片数据 
		@Override 
		public void onPictureTaken(byte[] data, Camera camera) { 
			Bitmap bmp=BitmapFactory.decodeByteArray(data, 0, data.length);
			log("data[]="+data+" bmp="+bmp+" camera="+camera);
			if(null!=callback)  
				callback.onCameraEnd(bmp);
		  } 
	}; 
 
	/**
	 * 开始拍照时调用该方法
	 * @param holder SurfaceHolder 
	 */
	public void surfaceCreated(SurfaceHolder holder) { 
		try { 
			// 获得Camera对象 
			camera = Camera.open(); 
			//通过SurfaceView显示取景画面
			camera.setPreviewDisplay(holder); 
			camera.setDisplayOrientation(getPreviewDegree());
			// 开始拍照 
			camera.startPreview();
			if(null!=callback) callback.onCameraStart();
		} catch (IOException exception) { 
			// 释放手机摄像头 
			camera.release(); 
			camera = null; 
		} 
		} 
	
	/**
	 * 停止拍照时调用该方法 
	 * @param holder SurfaceHolder
	 */
	public void surfaceDestroyed(SurfaceHolder holder) { 
		// 释放手机摄像头 
		if(camera!=null)
			camera.stopPreview();
			camera.release(); 
		} 
	
	/**
	 * 拍照状态变化时调用该方法 
	 */
	public void surfaceChanged(final SurfaceHolder holder, int format, int w, int h) { 
		try { 
			// 设置用于显示拍照影像的SurfaceHolder对象 
				Camera.Parameters parameters = camera.getParameters(); 
				// 设置照片格式 
				parameters.setPictureFormat(PixelFormat.JPEG); 
				// 根据屏幕方向设置预览尺寸大小 
				if (((Activity)mContext).getWindowManager().getDefaultDisplay().getOrientation() == 0) 
				   parameters.setPreviewSize(w, h);
				else parameters.setPreviewSize(h, w); 
				//设置每秒4帧
				parameters.setPreviewFrameRate(4);
				//设置照片的质量
				parameters.setJpegQuality(85);
				// 设置拍摄照片的实际分辨率，本例中的分辨率是1024×768 
				Log.d("tag", "w="+w+"h="+h);
				if(picH==0||picW==0){
					parameters.setPictureSize(currW, currH);
				}
				else{
				    parameters.setPictureSize(picW, picH);	
				}
				
				//拍摄过程中回调监听
				if(null!=callback) callback.onCameraChanged();
				
				// 自动对焦 
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
	 * 提供一个静态方法，用于根据手机方向获得相机预览画面旋转的角度
	 * @return int
	 */
	public int getPreviewDegree() {
			// 获得手机的方向
			int rotation = ((Activity)mContext).getWindowManager().getDefaultDisplay().getRotation();
//			Log.d("rotation", ""+rotation);
			int degree = 0;
			// 根据手机的方向计算相机预览画面应该选择的角度
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
	 * 停止拍照，并将拍摄的照片传入PictureCallback接口的onPictureTaken方法 
	 */
	public void takePicture() { 
		if (camera != null) {
		  camera.takePicture(null, null, pictureCallback);
		}
		
	} 
	
	/**
	 * 停止实时影像显示到SurfaceView中
	 */
	public void pausePreview(){
		if(camera!=null){
			camera.stopPreview();
		}
	}
	
	/**
	 * 重新开始获取实时影像到SurfaceView中
	 */
	public void resumePreview(){
		if(camera!=null) camera.startPreview();
	}
	
	/**
	 * 设置照片拍摄回调接口，监听照片拍摄过程
	 * @param callback 照片拍摄回调接口
	 */
	public void setOnCameraCallback(CameraCallback callback){
		this.callback=callback;
	}
	
	/**
	 * 设置要拍照图片的宽度和高度
	 * @param width 要拍照图片的宽度
	 * @param height 要拍照图片的高度
	 */
	public void setPictureSize(int width,int height){
		this.picW=width;
		this.picH=height;
	}
	
	/**
	 * 获取当前手机屏幕的分辨率
	 */
	public void getDisplayMetrix(){
		DisplayMetrics dm=new DisplayMetrics();
		((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
		this.currH=dm.heightPixels;
		this.currW=dm.widthPixels;
	}
	
	/**
	 * @author Administrator
	 * 自定义照片拍摄回调接口，供自定义拍照Activity进行回调实现
	 */
	public interface CameraCallback{
		/**
		 * 拍照开始，打开摄像头之前
		 */
		void onCameraStart();
		
		/**
		 * 正在拍照期间 
		 */
		void onCameraChanged();
		
		/**
		 * 拍照完毕
		 * @param data 要返回的摄像头拍照结果数据
		 */
		void onCameraEnd(Bitmap bitmap);
		
		/**
		 * 拍照后触发对焦事件
		 * @param bool 对焦是否成功
		 * */
		void onCameraFocus(boolean bool);
	}
	
	void log(String msg){
		Log.d(getClass().getSimpleName(), msg);
	}
} 