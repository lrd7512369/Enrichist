package com.lrd.enrichist;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


/**
 * 英知
 */
public class ConstellationView extends View implements View.OnClickListener {

    private static final long DELAY = 100;

    private UIHandler handler;
    private Paint paint;
    private int width;
    private int height;
    private List<Star> stars;
    private int starCount;
    private int limitDis;

    private Point touchPoint;
    private boolean isTouch;
    private boolean move;

    private int downX;
    private int downY;
    private int moveX;
    private int moveY;

    public ConstellationView(Context context) {
        super(context);
        init();
    }

    public ConstellationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ConstellationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {

        touchPoint = new Point();
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
//        setOnClickListener(this);
    }

    private void initStars() {
        starCount = 20;
        limitDis = 150;
        stars = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < starCount; i++) {
            int startX = random.nextInt(width);
            int startY = random.nextInt(height);
            Point startPos = new Point(startX, startY);
            Point endPos = getBorderPos(random.nextInt(4));
            Point currentPos = new Point(startX, startY);
            int radius = random.nextInt(3) + 3;
            int speed = random.nextInt(1) + 2;
            Star star = new Star(startPos, currentPos, endPos, radius, speed, Star.TYPE_NORMAL);
            stars.add(star);
        }
    }

    public void move() {
        WeakReference<ConstellationView> reference = new WeakReference<ConstellationView>(this);
        handler = new UIHandler(reference);
        handler.sendEmptyMessage(0);
        setAlpha(0);
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "alpha", 0, 1.0f);
        animator.setDuration(3000);
        animator.start();
    }

    private void updateStar(Star old) {
        Random random = new Random();
        int first = random.nextInt(4);
        Point startPos = getBorderPos(first);
        Point currentPos = new Point(startPos);
        Point endPos = getBorderPos(which(first, random));
        int radius = random.nextInt(3) + 3;
        int speed = random.nextInt(1) + 2;
        old.setStartPos(startPos);
        old.setCurrentPos(currentPos);
        old.setEndPos(endPos);
        old.setRadius(radius);
        old.setSpeed(speed);
    }

    private int which(int first, Random random) {
        int second = random.nextInt(4);
        if (second == first) {
            return which(first, random);
        }
        return second;
    }

    private Point getBorderPos(int which) {
        int endX = 0;
        int endY = 0;
        Random random = new Random();
        switch (which) {
            case 0:
                endX = random.nextInt(width);
                endY = 0;
                break;
            case 1:
                endX = 0;
                endY = random.nextInt(height);
                break;
            case 2:
                endX = random.nextInt(width);
                endY = height;
                break;
            case 3:
                endX = width;
                endY = random.nextInt(height);
                break;
        }
        return new Point(endX, endY);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
        initStars();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (handler == null) {
            return;
        }
        if (stars == null || stars.size() == 0) {
            return;
        }
        for (int i = 0; i < stars.size(); i++) {
            Star star = stars.get(i);
            Point pos = star.getCurrentPos();
//            Point endPos = star.getEndPos();
            int radius = star.getRadius();
            paint.setAlpha(255);
            canvas.drawCircle(pos.x, pos.y, radius, paint);
//            paint.setColor(Color.BLACK);
//            canvas.drawCircle(endPos.x, endPos.y, radius, paint);
            for (int j = i + 1; j < stars.size(); j++) {
                Star otherStar = stars.get(j);
                Point otherPos = otherStar.getCurrentPos();
                drawStarLine(pos, otherPos, canvas);
            }
            if (isTouch) {
                drawStarLine(pos, touchPoint, canvas);
            }
        }
    }

    private void drawStarLine(Point pos, Point otherPos, Canvas canvas) {
        int dis = getDis(pos, otherPos);
        if (dis <= limitDis) {
            paint.setAlpha((limitDis - dis) * 255 / limitDis);
            canvas.drawLine(pos.x, pos.y, otherPos.x, otherPos.y, paint);
        }
    }

    @Override
    public void onClick(View v) {
//        Toast.makeText(getContext(), "click", Toast.LENGTH_LONG).show();
//        addStars(4);
    }

    class Star {

        public static final int TYPE_NORMAL = 0;
        public static final int TYPE_ADD = 1;

        public Star(Point startPos, Point currentPos, Point endPos, int radius, int speed, int type) {
            this.startPos = startPos;
            this.currentPos = currentPos;
            this.endPos = endPos;
            this.radius = radius;
            this.speed = speed;
            this.type = type;
        }

        private Point startPos;
        private Point endPos;
        private Point currentPos;
        private int radius;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        private int speed;
        private int type;

        public Point getStartPos() {
            return startPos;
        }

        public void setStartPos(Point startPos) {
            this.startPos = startPos;
        }

        public int getSpeed() {
            return speed;
        }

        public void setSpeed(int speed) {
            this.speed = speed;
        }

        public int getRadius() {
            return radius;
        }

        public void setRadius(int radius) {
            this.radius = radius;
        }

        public Point getCurrentPos() {
            return currentPos;
        }

        public void setCurrentPos(Point currentPos) {
            this.currentPos = currentPos;
        }

        public Point getEndPos() {
            return endPos;
        }

        public void setEndPos(Point endPos) {
            this.endPos = endPos;
        }
    }

    static class UIHandler extends Handler {

        private WeakReference<ConstellationView> reference;

        public UIHandler(WeakReference<ConstellationView> reference) {
            this.reference = reference;
        }

        @Override
        public void handleMessage(Message msg) {
            if (reference == null) {
                return;
            }
            ConstellationView object = reference.get();
            if (object == null) {
                return;
            }
            object.handleMessage(msg);
        }
    }

    public void handleMessage(Message msg) {
        if (stars == null || stars.size() == 0) {
            handler.sendEmptyMessageDelayed(0, DELAY);
            return;
        }
        for (Iterator<Star> iterator = stars.iterator(); iterator.hasNext(); ) {
            Star star = iterator.next();
            Point currentPos = star.getCurrentPos();
            Point startPos = star.getStartPos();
            Point endPos = star.getEndPos();
            if (outOfBorder(star)) {
                if (star.getType() == Star.TYPE_NORMAL) {
                    updateStar(star);
                } else {
                    iterator.remove();
                }
            } else {
                double dis = getDis(startPos, endPos);
                int speed = star.getSpeed();
                int dx = (int) (speed * (endPos.x - startPos.x) / dis);
                int dy = (int) (speed * (endPos.y - startPos.y) / dis);
                currentPos.offset(dx, dy);
                star.setCurrentPos(currentPos);
            }
        }
        invalidate();
        handler.sendEmptyMessageDelayed(0, DELAY);
    }

    private boolean outOfBorder(Star star) {
        Point point = star.getCurrentPos();
        int radius = star.getRadius();
        return point.x < 0 - radius || point.x > width + radius || point.y < 0 - radius || point.y > height + radius;
    }

    private int getDis(Point startPos, Point endPos) {
        return (int) Math.sqrt((startPos.x - endPos.x) * (startPos.x - endPos.x) +
                (startPos.y - endPos.y) * (startPos.y - endPos.y));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int touchX = (int) event.getX();
        int touchY = (int) event.getY();
        touchPoint.set(touchX, touchY);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX= (int) event.getX();
                downY= (int) event.getY();
                isTouch = true;
                move = false;
                break;
            case MotionEvent.ACTION_MOVE:
                moveX= (int) event.getX();
                moveY= (int) event.getY();
                isTouch = true;
                if (Math.abs(moveX-downX)>=3||Math.abs(moveY-downY)>=3){
                    move = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                isTouch = false;
                if (!move) {
//                    Toast.makeText(getContext(), "click", Toast.LENGTH_LONG).show();
                    addStars(4);
                }
                break;
        }
        System.out.println("move:" + move);
        invalidate();
        return true;
    }

    private void addStars(int count) {
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            Point startPos = new Point(touchPoint);
            Point endPos = getBorderPos(random.nextInt(4));
            Point currentPos = new Point(startPos);
            int radius = random.nextInt(3) + 3;
            int speed = random.nextInt(1) + 2;
            Star star = new Star(startPos, currentPos, endPos, radius, speed, Star.TYPE_ADD);
            stars.add(star);
        }
    }

}
