package edu.stanford.cs108.mobiledraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.app.Activity;
import android.widget.RadioButton;
import java.util.*;

public class CustomView extends View{

    private GObject selectedObject;
    private EditText input_x;
    private EditText input_y;
    private EditText input_width;
    private EditText input_height;
    Canvas canvas;
    Paint blueOutline;
    Paint redOutline;
    Paint whiteFill;
    private ArrayList<GObject> gObjectList;

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);

        redOutline = new Paint();
        redOutline.setColor(Color.RED);
        redOutline.setStyle(Paint.Style.STROKE);
        redOutline.setStrokeWidth(5.0f);

        blueOutline = new Paint();
        blueOutline.setColor(Color.BLUE);
        blueOutline.setStyle(Paint.Style.STROKE);
        blueOutline.setStrokeWidth(15.0f);

        whiteFill = new Paint();
        whiteFill.setColor(Color.WHITE);
        whiteFill.setStyle(Paint.Style.FILL);

        canvas = new Canvas();
        gObjectList = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for(GObject obj: gObjectList){
            float left = obj.left;
            float right = obj.right;
            float bottom = obj.bottom;
            float up = obj.top;
            String type = obj.shape;
            Paint objectPaint = obj == selectedObject ? blueOutline : redOutline ;
            if(type.equals("rect")){
                canvas.drawRect(left, up, right, bottom, objectPaint);
                canvas.drawRect(left, up, right, bottom, whiteFill);
            } else if(type.equals("oval")){
                canvas.drawOval(left, up, right, bottom, objectPaint);
                canvas.drawOval(left, up, right, bottom, whiteFill);
            }
        }
    }


    public void UpdateGraph(){

        input_x = ((Activity) getContext()).findViewById(R.id.x);
        input_y = ((Activity) getContext()).findViewById(R.id.y);
        input_width = ((Activity) getContext()).findViewById(R.id.width);
        input_height = ((Activity) getContext()).findViewById(R.id.height);
        String x = input_x.getText().toString();
        String y = input_y.getText().toString();
        String width = input_width.getText().toString();
        String height = input_height.getText().toString();
        if (!x.equals("")&&!y.equals("")&&!width.equals("")&&!height.equals("")){
            if (selectedObject!=null) {
                float numX = Float.parseFloat(x);
                float numY = Float.parseFloat(y);
                float numWidth = Float.parseFloat(width);
                float numHeight = Float.parseFloat(height);
                GObject copyObject = new GObject(numX, numX + numWidth, numY, numY + numHeight, selectedObject.shape);
                gObjectList.remove(selectedObject);
                selectedObject = copyObject;
                gObjectList.add(copyObject);
                invalidate();
            }
        }
    }

    float x1;
    float x2;
    float y1;
    float y2;
    GObject gobj;

    @Override
    public boolean onTouchEvent(MotionEvent e){

        RadioButton erase;
        RadioButton select;
        RadioButton oval;
        RadioButton rect;

        float left;
        float right;
        float top;
        float bottom;

        erase = ((Activity) getContext()).findViewById(R.id.erase);
        oval = ((Activity) getContext()).findViewById(R.id.oval);
        rect = ((Activity) getContext()).findViewById(R.id.rect);
        select = ((Activity) getContext()).findViewById(R.id.select);
        input_x = ((Activity) getContext()).findViewById(R.id.x);
        input_y = ((Activity) getContext()).findViewById(R.id.y);
        input_width = ((Activity) getContext()).findViewById(R.id.width);
        input_height = ((Activity) getContext()).findViewById(R.id.height);

        if(oval.isChecked() || rect.isChecked()){
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x1 = e.getX();
                    y1 = e.getY();
                    break;

                case MotionEvent.ACTION_UP:
                    x2 = e.getX();
                    y2 = e.getY();
                    left = x1>x2 ? x2:x1;
                    right = x1>x2 ? x1:x2;

                    top = y1>y2 ? y2:y1;
                    bottom = y1>y2 ? y1:y2;
                    if(rect.isChecked()) {
                        gobj = new GObject(left, right, top, bottom, "rect");
                    } else {
                        gobj = new GObject(left, right, top, bottom, "oval");
                    }
                    selectedObject = gobj;
                    gObjectList.add(gobj);
                    break;
                default: break;
            }
            invalidate();
        }else{
            float checkedX = -1;
            float checkedY = -1;
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    checkedX = e.getX();
                    checkedY = e.getY();
                    break;
                default: break;
            }
            int objId=-1;
            for(int i = gObjectList.size()-1; i>=0; i--){
                GObject obj = gObjectList.get(i);
                if(checkedX > obj.left && checkedX < obj.right && checkedY < obj.bottom && checkedY > obj.top) {
                    objId = i;
                    break;
                }
            }
            if(erase.isChecked()){
                if (objId!=-1) {
                    GObject obj = gObjectList.get(objId);
                    gObjectList.remove(obj);
                    selectedObject = null;
                }

            }else if(select.isChecked()){
                if (objId!=-1) {
                    GObject obj = gObjectList.get(objId);
                    gObjectList.remove(obj);
                    gObjectList.add(obj);
                    selectedObject = obj;
                    String xCoordinates = String.format("%.5f", obj.left);
                    String yCoordinates = String.format("%.5f", obj.top);
                    String widthStr = String.format("%.5f", obj.width);
                    String heightStr = String.format("%.5f", obj.height);
                    input_x.setText(xCoordinates);
                    input_y.setText(yCoordinates);
                    input_width.setText(widthStr);
                    input_height.setText(heightStr);
                }else if (checkedX != -1&& checkedY != -1){
                    selectedObject = null;
                    input_x.setText("");
                    input_y.setText("");
                    input_width.setText("");
                    input_height.setText("");
                }
            }
            invalidate();
        }
        return true;
    }

    private class GObject{
        float left;
        float right;
        float top;
        float bottom;
        float width;
        float height;
        String shape;

        private GObject(float leftPoint, float rightPoint, float topPoint, float bottomPoint, String type){
            left = leftPoint;
            right = rightPoint;
            top = topPoint;
            bottom = bottomPoint;
            width = Math.abs(leftPoint-rightPoint);
            height = Math.abs(topPoint-bottomPoint);
            shape=type;
        }
        private void draw(Paint objectPaint){
            if(shape.equals("rect")){
                canvas.drawRect(left, top, right, bottom, objectPaint);
                canvas.drawRect(left, top, right, bottom, whiteFill);
            } else if(shape.equals("oval")){
                canvas.drawOval(left, top, right, bottom, objectPaint);
                canvas.drawOval(left, top, right, bottom, whiteFill);
            }
        }
    }
}