package com.example.henry.picyourbrain;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;
import android.util.AttributeSet;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.TypedValue;


public class DrawingView extends View{
    private Path mPath;
    private Paint mPaint, mCanvasPaint;
    private int mPaintColor = 0xFF660000;
    private Canvas mCanvas;
    private Bitmap mCanvasBitmap;
    private float mBrushSize, mLastBrushSize;
    private boolean mEraseFlag = false;

    public DrawingView(Context context, AttributeSet attrs){
        super(context,attrs);
        setupDrawing();
    }

    public void setupDrawing(){
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setColor(mPaintColor);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mBrushSize);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mCanvasPaint = new Paint(Paint.DITHER_FLAG);
        mBrushSize = getResources().getInteger(R.integer.medium_size);
        mLastBrushSize = mBrushSize;

    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w,h,oldw,oldh);
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mCanvasBitmap);
    }
    @Override
    protected void onDraw(Canvas canvas){
        canvas.drawBitmap(mCanvasBitmap, 0, 0, mCanvasPaint);
           canvas.drawPath(mPath,mPaint);

    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(touchX, touchY);
                mCanvas.drawPath(mPath,mPaint);
                mPath.reset();
                mPath.moveTo(touchX,touchY);
                break;
            case MotionEvent.ACTION_UP:
                mCanvas.drawPath(mPath, mPaint);
                mPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void setColor(String newColor){
        mPaintColor = Color.parseColor(newColor);
        mPaint.setColor(mPaintColor);
        invalidate();
    }

    public void setBrushSize(float newSize){
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                              newSize,getResources().getDisplayMetrics());
        mPaint.setStrokeWidth(mBrushSize);
    }

    public void setLastBrushSize(float lastSize){
        mLastBrushSize =lastSize;
    }
    public float getLastBrushSize(){
        return mLastBrushSize;
    }

    public void setEraseFlag(boolean isErase){
        mEraseFlag = isErase;
        if(mEraseFlag){
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        else{
            mPaint.setXfermode(null);
        }
    }

    public void startNew(){
        mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }
}
