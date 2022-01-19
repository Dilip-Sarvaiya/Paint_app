package com.dilip_sarvaiya_700.imagecompressor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class PaintView extends View {

    private Bitmap btmBackground,btmView;
    private Paint mpaint= new Paint();

    private Path mPath = new Path();

    private  int colorBackground,sizeBrush,sizeEraser;
    private float mX,mY;
    private Canvas mCanvas;
    private final int DEFFERENCE_SPACE  =4;
    private ArrayList<Bitmap> listAction = new ArrayList<>();

    public PaintView(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);
//        this.btmBackground = btmBackground;
        init();

    }

    private void init() {

        sizeEraser = sizeBrush - 12;
        colorBackground = Color.WHITE;
        mpaint.setColor(Color.BLACK);
        mpaint.setAntiAlias(true);
        mpaint.setDither(true);
        mpaint.setStyle(Paint.Style.STROKE);
        mpaint.setStrokeCap(Paint.Cap.ROUND);
        mpaint.setStrokeJoin(Paint.Join.ROUND);
        mpaint.setStrokeWidth(toPx(sizeBrush));

    }

    private float toPx(int sizeBrush) {
        return sizeBrush * (getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        btmBackground = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        btmView =Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(btmView);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(colorBackground);
        canvas.drawBitmap(btmBackground,0,0,null);
        canvas.drawBitmap(btmView,0,0,null);

    }

    public void setSizeEraser(int s)
    {
        sizeEraser = s;
        mpaint.setStrokeWidth(toPx(sizeEraser));
    }

    public void enableEraser()
    {
        mpaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void disableEraser()
    {
        mpaint.setXfermode(null);
        mpaint.setShader(null);
        mpaint.setMaskFilter(null);
    }

    public void LastAction(Bitmap bitmap)
    {
        listAction.add(bitmap);

    }

    public  void returnLastAction()
    {
        if(listAction.size()>0)
        {
            listAction.remove(listAction.size()-1);

            if(listAction.size()>0)
            {
                btmView = listAction.get(listAction.size()-1);
            }
            else
            {
                btmView = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
            }

            mCanvas = new Canvas(btmView);
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x  = event.getX();
        float y = event.getY();

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                touchStart(x,y);
                break;

            case MotionEvent.ACTION_MOVE:
                touchMove(x,y);
                break;

            case MotionEvent.ACTION_UP:
                touchUp(x,y);
                break;

        }

        return  true;
    }

    private void touchUp(float x, float y) {
        mPath.reset();
    }

    public  Bitmap getBitmap()
    {
        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);
        return bitmap;
    }

    private void touchMove(float x, float y) {

        float dx = Math.abs(x-mX);
        float dy = Math.abs(x-mY);

        if(dx>=DEFFERENCE_SPACE || dy>=DEFFERENCE_SPACE)
        {
            mPath.quadTo(x,y,(x+mX)/2,(y+mY)/2);

            mY =y;
            mX = x;

            mCanvas.drawPath(mPath,mpaint);
        }

    }

    private void touchStart(float x, float y) {
         mPath.moveTo(x,y);
         mX = x;
         mY = y;
    }
}
