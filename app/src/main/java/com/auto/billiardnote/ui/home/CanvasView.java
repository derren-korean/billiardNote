package com.auto.billiardnote.ui.home;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Arrays;
import java.util.HashMap;

public class CanvasView extends View {

    Context context;
    private StraightLine line;
    private HashMap<DrawingTool, Ball> balls;
    private DrawingTool drawingTool;
    private ShapeClickInterface listener;

    public void setClickListener(ShapeClickInterface listener) {
        this.listener = listener;
    }

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        line = new StraightLine();
        balls = new HashMap<>();
        drawingTool = DrawingTool.CUE_BALL;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Arrays.stream(DrawingTool.values())
                .filter(tool -> tool.getColor() != Color.BLACK)
                .forEach(tool -> createCircle(tool));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        switch (this.drawingTool) {
            case LINE:
                line.draw(canvas);
                break;
            case CUE_BALL: case ORANGE_BALL: case RED_BALL:
                balls.values().forEach(ball -> ball.draw(canvas));
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (x < 0f) {
            x = 0f;
        } else if (x > getWidth()) {
            x = getWidth();
        }
        if (y < 0f) {
            y = 0f;
        } else if (y > getHeight()) {
            y = getHeight();
        }
        if (this.drawingTool == DrawingTool.LINE) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    line.touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    line.touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    line.touch_up(x, y);
                    invalidate();
                    break;
            }
        } else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    balls.get(this.drawingTool).touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_DOWN: case MotionEvent.ACTION_UP:
                    break;
            }
        }
        return true;
    }

    public void createCircle(DrawingTool tool) {
        this.drawingTool = tool;
        Ball ball = new Ball( getWidth() / 2f, getHeight() / 2f, 40f, tool);
        ball.setClickListener(listener);
        balls.put(tool, ball);
        this.invalidate();
    }

    public void unDo() {
        if (line.unDo()) {invalidate();}
    }

    //TODO: click ShapeClickListener의 범위를 지정하여, 해당 범위에 클릭이 되었을 때, DrawingTool을 지정한다.
    //TODO: 해당 범위는 마지막 도착지점을 기록하여 해당 위치의 Radius 만큼 있을 때 반응한다.
    //TODO: 선그리기 버튼을 이용하여, 선을 그릴때 무시할 수 있도록 한다.
}
