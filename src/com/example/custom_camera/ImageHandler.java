package com.example.custom_camera;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

/**
 * 图片处理类
 * */
public class ImageHandler {

	 /**
	  * 将一般继承自View类的视图转换为Bitmap
	  * @param v 要转换的源视图
	  * @return Bitmap
	  */
	public static Bitmap getViewBitmap(View v) {
	    v.clearFocus();
	    v.setPressed(false);
	    boolean willNotCache = v.willNotCacheDrawing();
	    v.setWillNotCacheDrawing(false);
	    int color  = v.getDrawingCacheBackgroundColor();
	    v.setDrawingCacheBackgroundColor(0);
	    if(color != 0){
	        v.destroyDrawingCache();
	    }
	    v.buildDrawingCache();
	    Bitmap cacheBitmap = v.getDrawingCache();
	    if(cacheBitmap == null){
	       return null;
	    }
	    Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
	    v.destroyDrawingCache();
	    v.setWillNotCacheDrawing(willNotCache);
	    v.setDrawingCacheBackgroundColor(color); 
        return bitmap;
	         
    }
	
   /**
     * 制作图片水印效果
	 * @param src 要生成水印的源图片
	 * @param mark  水印图片
	 * @param left 距离src 左边距离
	 * @param top 距离src 顶部距离
	 * @return Bitmap 返回生成后的水印图片
	 */
	public static Bitmap watermark(Bitmap src,Bitmap mark,float left,float top){
	   if(src==null||mark==null) return null;
       int w = src.getWidth();
       int h = src.getHeight();
       Bitmap bmpTemp = Bitmap.createBitmap(w, h, Config.ARGB_8888);
       Canvas canvas = new Canvas(bmpTemp);
       Paint p = new Paint();
       String familyName = "宋体";
       Typeface font = Typeface.create(familyName, Typeface.BOLD);
       Log.i("TAG", "字体");
       p.setColor(Color.YELLOW);
       p.setTypeface(font);
       p.setTextSize(22);
       canvas.drawBitmap(src, 0, 0, p);
       canvas.drawBitmap(mark, left, top, p);
       canvas.save(Canvas.ALL_SAVE_FLAG);
       canvas.restore();
       return bmpTemp;
   }
   

	/**
	 * 将Drawable转化为Bitmap
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable){ 
		int width = drawable.getIntrinsicWidth(); 
		int height = drawable.getIntrinsicHeight(); 
		Bitmap bitmap = Bitmap.createBitmap(width, height, 
		drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 
		    : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0,0,width,height); 
		drawable.draw(canvas); 
		return bitmap;

	}
	
	/**
	 * 放大缩小图片
	 * @param bitmap
	 * @param w
	 * @param h
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap,int w,int h){ 
		int width = bitmap.getWidth(); 
		int height = bitmap.getHeight(); 
		Matrix matrix = new Matrix(); 
		float scaleWidht = ((float)w / width); 
		float scaleHeight = ((float)h / height); 
		matrix.postScale(scaleWidht, scaleHeight); 
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true); 
		return newbmp; 
	} 
}
