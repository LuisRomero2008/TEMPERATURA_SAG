package com.example.temperatura_sag.Common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MyFirm extends View {

    /*public int color,strokeWidth;
    public boolean emboss,blur;
    public Path path;

    public MyFirm(int color, int strokeWidth, boolean emboss, boolean blur, Path path) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.emboss = emboss;
        this.blur = blur;
        this.path = path;
    }*/
    Paint paint;
    Path path;
    public MyFirm(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        path=new Path();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
    }
    public void clear(){
        path.reset();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path,paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float xPos=event.getX();
        float yPos=event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
            path.moveTo(xPos,yPos);
            return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(xPos,yPos);
                break;

            case MotionEvent.ACTION_UP:
                break;

             default:
                 return false;
        }
        invalidate();
        return true;
    }
}

