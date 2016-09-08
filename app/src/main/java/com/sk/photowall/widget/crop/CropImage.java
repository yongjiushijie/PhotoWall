package com.sk.photowall.widget.crop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;


import com.sk.photowall.R;
import com.sk.photowall.utils.camera.ImageUtils;
import com.sk.photowall.utils.camera.ScreenSizeUtils;
import com.sk.photowall.utils.file.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * crop imageview
 * Created by sk on 2016/7/25.
 */
public class CropImage extends ImageView {

    private static final double maxPrecent = 16 * 1f / 9;
    private static final double minPrecent = 9 * 1f / 16;

    private static final float lineStoke = 5f;
    private static final float MARGIN = 10f;
    private float bottomMargin;

    //canvas's width
    private float mWdith, mHeight;
    //底部旋转按钮部分的高， 比例的高
    private float lcrHeight, preHeight, cropHeight;

    //gray area and line paint
    private Paint outPaint, linePaint, prePaint, bottomPaint, bgPaint;


    //crop area view rect
    private Rect cropAreaRect;
    private float wCrop, hCrop;

    //bottom view
    private Rect leftRect, centerRect, rightRect, bottomRect;
    private Bitmap bLeft, bCenter, bRight;
    private int wLeft, wCenter, wRight, hLeft, hCenter, hRight;

    //precent view
    private Rect n2sRect, t2fRect, f2fRect, f2tRect, s2nRect, preRect;
    private Bitmap bN2S, bT2F, bF2F, bF2T, bS2N;
    private int wNS, wTF, wFF, wFT, wSN, hNS, hTF, hFF, hFT, hSN;

    //Touch start
    PointF midPoint, oldPoint, cropmidPoint;
    private PreBean preBean;
    List<PreBean> preBeans = new ArrayList<>();
    boolean isFrist = true;
    private Matrix mMatrix, tMatrix, sMatrix;


    //手势操作类型
    private static final int NONE = 0;
    private static final int DRAG = 1;//平移
    private static final int ZOOM = 2;//缩放
    private static final int SWITCH = 3;
    int mode = NONE;

    private Bitmap mBitmap, originBitmap;
    //两指间距离
    private float oldDist = 1f;

    private CropListener listener;
    private float mScale;


    public CropImage(Context context) {
        super(context);
        init();
    }

    public CropImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CropImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setListener(CropListener listener) {
        this.listener = listener;
    }


    /**
     * 初始化view（底部旋转按钮， 底部比例按钮，灰色边框画笔，高亮框边框画笔）
     */
    private void init() {

        bottomMargin = ScreenSizeUtils.dp2px(getContext(), MARGIN);
        //裁切框
        cropAreaRect = new Rect();
        //比例框
        n2sRect = new Rect();
        t2fRect = new Rect();
        f2fRect = new Rect();
        f2tRect = new Rect();
        s2nRect = new Rect();
        preRect = new Rect();

        preBeans.add(new PreBean(n2sRect, 9, 16));
        preBeans.add(new PreBean(t2fRect, 3, 4));
        preBeans.add(new PreBean(f2fRect, 1, 1));
        preBeans.add(new PreBean(f2tRect, 4, 3));
        preBeans.add(new PreBean(s2nRect, 16, 9));


        bN2S = BitmapFactory.decodeResource(getResources(), R.mipmap.pre_ns);
        bT2F = BitmapFactory.decodeResource(getResources(), R.mipmap.pre_tf);
        bF2F = BitmapFactory.decodeResource(getResources(), R.mipmap.pre_ff);
        bF2T = BitmapFactory.decodeResource(getResources(), R.mipmap.pre_ft);
        bS2N = BitmapFactory.decodeResource(getResources(), R.mipmap.pre_sn);
        wNS = bN2S.getWidth();
        hNS = bN2S.getHeight();
        wTF = bT2F.getWidth();
        hTF = bT2F.getHeight();
        wFF = bF2F.getWidth();
        hFF = bF2F.getHeight();
        wFT = bF2T.getWidth();
        hFT = bF2T.getHeight();
        wSN = bS2N.getWidth();
        hSN = bS2N.getHeight();
        //底部操作框
        leftRect = new Rect();
        centerRect = new Rect();
        rightRect = new Rect();
        bottomRect = new Rect();
        bLeft = BitmapFactory.decodeResource(getResources(), R.mipmap.left_rotate);
        bCenter = BitmapFactory.decodeResource(getResources(), R.mipmap.btn_complement_up);
        bRight = BitmapFactory.decodeResource(getResources(), R.mipmap.right_rotare);
        wLeft = bLeft.getWidth();
        hLeft = bLeft.getHeight();
        wCenter = bCenter.getWidth();
        hCenter = bCenter.getHeight();
        wRight = bRight.getWidth();
        hRight = bRight.getHeight();

        bgPaint = new Paint();
        bgPaint.setColor(Color.WHITE);

        //裁剪框周边画笔
        outPaint = new Paint();
        outPaint.setARGB(255, 46, 44, 48);
//        outPaint.setColor(getResources().getColor(R.color.bg_shared));
        //比例框画笔
        prePaint = new Paint();
        prePaint.setStyle(Paint.Style.FILL);
        prePaint.setARGB(255, 50, 50, 50);
        //底部框画笔
        bottomPaint = new Paint();
        bottomPaint.setStyle(Paint.Style.FILL);
        bottomPaint.setARGB(255, 60, 60, 60);
        //红线
        linePaint = new Paint();
        linePaint.setColor(Color.RED);
        linePaint.setStrokeWidth(lineStoke);
        linePaint.setStyle(Paint.Style.STROKE);
        midPoint = new PointF();
        oldPoint = new PointF();
        cropmidPoint = new PointF();

        mMatrix = new Matrix();
        tMatrix = new Matrix();
        sMatrix = new Matrix();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        drawOutRect(canvas);
        canvas.restore();
    }

    private void drawOutRect(Canvas canvas) {
        float width = canvas.getWidth();
        float height = canvas.getHeight();

        canvas.drawRect(0, 0, width, height, bgPaint);
        setBottomArea(width, height);
        setPrecentArea(width, height, preBean.getRect());
        drawGrayArea(canvas, width, height, preBean.getPreWidth(), preBean.getPreHeight());
        drawBottomArea(canvas);
        drawPrecentArea(canvas);
    }


    /**
     * 绘制灰色区域
     *
     * @param canvas
     * @param width  画布的宽
     * @param height 画布的高
     */
    private void drawGrayArea(Canvas canvas, float width, float height, int pWidth, int pHeight) {
        cropHeight = (float) (height - Math.ceil(lcrHeight) - Math.ceil(preHeight));
        float x = width / 2;
        float y = cropHeight / 2;

        cropmidPoint.set(x, y);

        float dx = y / pHeight * pWidth;
        float dy = x / pWidth * pHeight;

        if (dy > y) {
            cropAreaRect.left = (int) (x - dx);
            cropAreaRect.top = 0 + 3;
            cropAreaRect.right = (int) (x + dx);
            cropAreaRect.bottom = (int) cropHeight - 2;

            wCrop = cropAreaRect.right - cropAreaRect.left;
            hCrop = cropAreaRect.bottom - cropAreaRect.top;

            drawBitmap(canvas, isFrist);
            canvas.drawRect(0, 0, cropAreaRect.left, cropHeight, outPaint);//左侧
            canvas.drawRect(cropAreaRect.right, 0, width, cropHeight, outPaint);//右侧
        } else {
            cropAreaRect.left = 0;
            cropAreaRect.top = (int) (y - dy);
            cropAreaRect.right = (int) width;
            cropAreaRect.bottom = (int) (y + dy);

            wCrop = cropAreaRect.right - cropAreaRect.left;
            hCrop = cropAreaRect.bottom - cropAreaRect.top;

            drawBitmap(canvas, isFrist);

            //绘制灰色部分
            canvas.drawRect(0, 0, width, cropAreaRect.top, outPaint);
            canvas.drawRect(0, cropAreaRect.bottom, width, cropHeight, outPaint);
        }
        /**
         * 绘制红色边框
         */
        Path path = new Path();
        path.addRect(new RectF(cropAreaRect), Path.Direction.CCW);
        canvas.drawPath(path, linePaint);
    }


    /**
     * 绘制比例框view
     *
     * @param width
     * @param height
     */
    private void setPrecentArea(float width, float height, Rect rect) {
        setPreCheck(rect);
        preHeight = hFF + bottomMargin * 2;
        float x = width / 2;
        float y = height - lcrHeight - hFF / 2 - bottomMargin;
        float dx = x * 0.9f;

        preRect.left = 0;
        preRect.top = (int) (height - lcrHeight - preHeight);
        preRect.right = (int) width;
        preRect.bottom = (int) (height - lcrHeight);

        //1;1
        f2fRect.left = (int) (x - wFF / 2);
        f2fRect.top = (int) (y - hFF / 2);
        f2fRect.right = (int) (x + wFF / 2);
        f2fRect.bottom = (int) (y + hFF / 2);

        //3：4
        float tf_x = x - dx / 3;
        t2fRect.left = (int) (tf_x - wTF / 2);
        t2fRect.top = (int) (y - hTF / 2);
        t2fRect.right = (int) (tf_x + wTF / 2);
        t2fRect.bottom = (int) (y + hTF / 2);

        //4：3
        float ft_x = x + dx / 3;
        f2tRect.left = (int) (ft_x - wFT / 2);
        f2tRect.top = (int) (y - hFT / 2);
        f2tRect.right = (int) (ft_x + wFT / 2);
        f2tRect.bottom = (int) (y + hFT / 2);

        //9:16
        float ns_x = x - dx / 3 * 2;
        n2sRect.left = (int) (ns_x - wNS / 2);
        n2sRect.top = (int) (y - hNS / 2);
        n2sRect.right = (int) (ns_x + wNS / 2);
        n2sRect.bottom = (int) (y + hNS / 2);

        //16:9
        float sn_x = x + dx / 3 * 2;
        s2nRect.left = (int) (sn_x - wSN / 2);
        s2nRect.top = (int) (y - hSN / 2);
        s2nRect.right = (int) (sn_x + wSN / 2);
        s2nRect.bottom = (int) (y + hSN / 2);
    }

    private void drawPrecentArea(Canvas canvas) {
        canvas.drawRect(new RectF(preRect), prePaint);
        canvas.drawBitmap(bN2S, null, n2sRect, null);
        canvas.drawBitmap(bT2F, null, t2fRect, null);
        canvas.drawBitmap(bF2F, null, f2fRect, null);
        canvas.drawBitmap(bF2T, null, f2tRect, null);
        canvas.drawBitmap(bS2N, null, s2nRect, null);
    }


    /**
     * 为点击的view 改变底色
     *
     * @param rect
     */
    private void setPreCheck(Rect rect) {
        bN2S = BitmapFactory.decodeResource(getResources(), R.mipmap.pre_ns);
        bT2F = BitmapFactory.decodeResource(getResources(), R.mipmap.pre_tf);
        bF2F = BitmapFactory.decodeResource(getResources(), R.mipmap.pre_ff);
        bF2T = BitmapFactory.decodeResource(getResources(), R.mipmap.pre_ft);
        bS2N = BitmapFactory.decodeResource(getResources(), R.mipmap.pre_sn);

        if (rect == n2sRect)
            bN2S = BitmapFactory.decodeResource(getResources(), R.mipmap.pre_ns_check);
        else if (rect == t2fRect)
            bT2F = BitmapFactory.decodeResource(getResources(), R.mipmap.pre_tf_check);
        else if (rect == f2fRect)
            bF2F = BitmapFactory.decodeResource(getResources(), R.mipmap.pre_ff_check);
        else if (rect == f2tRect)
            bF2T = BitmapFactory.decodeResource(getResources(), R.mipmap.pre_ft_check);
        else if (rect == s2nRect)
            bS2N = BitmapFactory.decodeResource(getResources(), R.mipmap.pre_sn_check);
    }

    /**
     * 绘制底部区域
     *
     * @param width
     * @param height
     */
    private void setBottomArea(float width, float height) {
        lcrHeight = hCenter + bottomMargin * 2;
        float mid_x = width / 2;
        float mid_y = height - hCenter / 2 - bottomMargin;

        bottomRect.left = 0;
        bottomRect.top = (int) (height - lcrHeight);
        bottomRect.right = (int) width;
        bottomRect.bottom = (int) height;


        //中间确定按钮
        centerRect.left = (int) (mid_x - wCenter / 2);
        centerRect.top = (int) (mid_y - hCenter / 2);
        centerRect.right = (int) (mid_x + wCenter / 2);
        centerRect.bottom = (int) (mid_y + hCenter / 2);
        //左侧旋转按钮
        leftRect.left = (int) ((mid_x - wLeft) / 2);
        leftRect.top = (int) (mid_y - hLeft / 2);
        leftRect.right = (int) ((mid_x + wLeft) / 2);
        leftRect.bottom = (int) (mid_y + hLeft / 2);

        //右侧旋转按钮
        rightRect.left = (int) ((width + mid_x - wRight) / 2);
        rightRect.top = (int) (mid_y - hRight / 2);
        rightRect.right = (int) ((width + mid_x + wRight) / 2);
        rightRect.bottom = (int) (mid_y + hRight / 2);

    }

    private void drawBottomArea(Canvas canvas) {
        canvas.drawRect(bottomRect, bottomPaint);
        canvas.drawRect(new RectF(bottomRect), bottomPaint);
        canvas.drawBitmap(bLeft, null, leftRect, null);
        canvas.drawBitmap(bCenter, null, centerRect, null);
        canvas.drawBitmap(bRight, null, rightRect, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                actionDown(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (inButton(event, cropAreaRect)) {
                    mode = ZOOM;
                    oldDist = spacing(event);
                    midPoint(midPoint, event);
                    sMatrix.set(mMatrix);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                tMatrix.set(sMatrix);
                if (mode == ZOOM) {
                    float curDist = spacing(event);
                    float scale = curDist / oldDist;
                    mScale = scale;
                    tMatrix.postScale(scale, scale, midPoint.x, midPoint.y);
                    if (matrixCheck()) {
                        mMatrix.set(tMatrix);
                        invalidate();
                    }
//                    invalidate();
                } else if (mode == DRAG) {
                    float dx = event.getX() - oldPoint.x;
                    float dy = event.getY() - oldPoint.y;
//                    float offsetx = getHorizonOffset(dx);
//                    float foosety = getVerizonOffset(dy);
                    tMatrix.postTranslate(dx, dy);// 平移
                    mMatrix.set(tMatrix);
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (mode == SWITCH) {
                    invalidate();
                }
                mode = NONE;
                break;
        }
        return true;
    }

    /**
     * 单指操作
     *
     * @param event
     */
    private void actionDown(MotionEvent event) {
        if (inButton(event, cropAreaRect)) {
            mode = DRAG;
            float down_x = event.getX();
            float down_y = event.getY();

            oldPoint.set(down_x, down_y);
            sMatrix.set(mMatrix);
        } else if (inButton(event, leftRect)) {
            if (listener != null)
                listener.onLeft();
        } else if (inButton(event, centerRect)) {
            if (listener != null)
                listener.onCenter();
        } else if (inButton(event, rightRect)) {
            if (listener != null)
                listener.onRight();
        } else if (inButton(event, n2sRect)) {//9:16
            mode = SWITCH;
            isFrist = true;
            setPreBean(n2sRect, 9, 16);
        } else if (inButton(event, t2fRect)) {//3:4
            mode = SWITCH;
            isFrist = true;
            setPreBean(t2fRect, 3, 4);
        } else if (inButton(event, f2fRect)) {//1;1
            mode = SWITCH;
            isFrist = true;
            setPreBean(f2fRect, 1, 1);
        } else if (inButton(event, f2tRect)) {//4:3
            mode = SWITCH;
            isFrist = true;
            setPreBean(f2tRect, 4, 3);
        } else if (inButton(event, s2nRect)) {//16:9
            mode = SWITCH;
            isFrist = true;
            setPreBean(s2nRect, 16, 9);
        }
    }

    /**
     * 为PreBean赋予值
     *
     * @param rect
     * @param preWidth
     * @param preHeight
     */
    private void setPreBean(Rect rect, int preWidth, int preHeight) {
        if (preBean == null)
            return;
        preBean.setRect(rect);
        preBean.setPreWidth(preWidth);
        preBean.setPreHeight(preHeight);
    }

    /**
     * 设置背景图片
     *
     * @param url
     */
    public void setImageUrl(String url) {
        Bitmap bm = ImageUtils.getSmallBitmap(getContext(), url);
        int degree = ImageUtils.readPictureDegree(url);
        Bitmap bitmap = ImageUtils.rotaingImageView(degree, bm);
        float btmPrecent = bitmap.getWidth() * 1f / bitmap.getHeight();
        setDefPre(btmPrecent);
        mMatrix.reset();
        originBitmap = bitmap;
        mBitmap = originBitmap;
        invalidate();
    }

    /**
     * 设置默认比例
     */
    public void setDefPre(float btmPrecent) {
        float min = btmPrecent;
        PreBean curBean;
        int index = 0;
        for (int i = 0; i < preBeans.size(); i++) {
            curBean = preBeans.get(i);
            float precent;
            if (btmPrecent > maxPrecent || btmPrecent < minPrecent) {
                precent = Math.abs(btmPrecent - curBean.getPreWidth() / (curBean.getPreHeight() * 1f));
                if (min > precent) {
                    min = precent;
                    index = i;
                }
            } else if (btmPrecent >= 1 && btmPrecent <= maxPrecent) {
                //1:1 - 16:9
                precent = btmPrecent - curBean.getPreWidth() / (curBean.getPreHeight() * 1.0f);
                if (precent >= 0 && min > Math.abs(precent)) {
                    min = precent;
                    index = i;
                }
            } else if (btmPrecent <= 1 && btmPrecent >= minPrecent) {
                //9:16-1:1
                precent = btmPrecent - curBean.getPreWidth() / (curBean.getPreHeight() * 1.0f);
                if (precent <= 0 && min > Math.abs(precent)) {
                    min = precent;
                    index = i;
                }
            }
        }
        preBean = preBeans.get(index);
    }


    public void drawBitmap(Canvas canvas, boolean isFrist) {

        if (mBitmap == null)
            return;
        if (isFrist) {
            float scale, wscale, hscale;
            float screenWdith = ScreenSizeUtils.getScreenWidth(getContext());
            float bWidth = mBitmap.getWidth();
            float bHeight = mBitmap.getHeight();
            float cropWidth = wCrop;
            float cropHeight = hCrop;

            if (bWidth > cropWidth && bHeight > cropHeight) {
                wscale = cropWidth / bWidth;
                hscale = cropHeight / bHeight;
                scale = wscale > hscale ? hscale : wscale;
            } else if (bWidth > cropWidth && bHeight < cropHeight) {
                scale = cropWidth / bWidth;
            } else if (bWidth < cropWidth && bHeight > cropHeight) {
                scale = cropHeight / bHeight;
            } else {
                wscale = cropWidth / bWidth;
                hscale = cropHeight / bHeight;
                scale = wscale > hscale ? hscale : wscale;
            }
            mMatrix.setScale(scale, scale);
            mMatrix.postTranslate((screenWdith - bWidth * scale) / 2, (this.cropHeight - bHeight * scale) / 2);
            this.isFrist = false;
        }
        canvas.drawBitmap(mBitmap, mMatrix, null);

    }


    /**
     * 触摸是否在某个button范围
     *
     * @param event
     * @param obj
     * @return
     */
    private boolean inButton(MotionEvent event, Object obj) {
        Rect rect = null;
        if (obj instanceof Rect)
            rect = (Rect) obj;
        else if (obj instanceof PreBean)
            rect = ((PreBean) obj).getRect();
        int left = rect.left;
        int right = rect.right;
        int top = rect.top;
        int bottom = rect.bottom;
        return event.getX(0) >= left && event.getX(0) <= right && event.getY(0) >= top && event.getY(0) <= bottom;
    }

    /**
     * 计算双指之间的距离
     */
    private float spacing(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);
        } else {
            return 0;
        }
    }

    /**
     * 获取手势中心点
     *
     * @param point 中心点
     * @param event
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * 获取偏移值
     *
     * @return
     */
    private float getHorizonOffset(float offset) {
        float[] f = new float[9];
        tMatrix.getValues(f);
        // 图片4个顶点的坐标
        float x1 = f[0] * 0 + f[1] * 0 + f[2];
        float x2 = f[0] * mBitmap.getWidth() + f[1] * 0 + f[2];
        float x3 = f[0] * 0 + f[1] * mBitmap.getHeight() + f[2];
        float x4 = f[0] * mBitmap.getWidth() + f[1] * mBitmap.getHeight() + f[2];

        int currentLeft = cropAreaRect.left;
        int currentRight = cropAreaRect.right;

        if (offset < 0) {
            if ((x2 >= currentRight) && (x4 >= currentRight)) {
                float x = x2 - currentRight;
                if (Math.abs(offset) > x)
                    return -x;
                else
                    return offset;
            }
        } else {
            if ((x1 <= currentLeft) && (x3 <= currentLeft)) {
                float x = currentLeft - x1;
                if (Math.abs(offset) > x)
                    return x;
                else
                    return offset;
            }
        }
        return 0;
    }


    private float getVerizonOffset(float offset) {
        float[] f = new float[9];
        tMatrix.getValues(f);
        // 图片4个顶点的坐标
        float y1 = f[3] * 0 + f[4] * 0 + f[5];
        float y2 = f[3] * mBitmap.getWidth() + f[4] * 0 + f[5];
        float y3 = f[3] * 0 + f[4] * mBitmap.getHeight() + f[5];
        float y4 = f[3] * mBitmap.getWidth() + f[4] * mBitmap.getHeight() + f[5];

        int currentTop = cropAreaRect.top;
        int currentBotton = cropAreaRect.bottom;


        if (offset < 0) {//向上滑动
            if ((y3 >= currentBotton) && (y4 >= currentBotton)) {
                float y = y3 - currentBotton;
                if (Math.abs(offset) > y)
                    return -y;
                else
                    return offset;
            }
        } else {//向下滑动
            if ((y1 <= currentTop) && (y2 <= currentTop)) {
                float y = currentTop - y1;
                if (Math.abs(offset) > y)
                    return y;
                else
                    return offset;
            }
        }
        return 0;
    }

    /**
     * 获取当前矩阵的宽
     *
     * @return
     */
    private double getCurrentWidth() {
        float[] f = new float[9];
        mMatrix.getValues(f);
        // 图片4个顶点的坐标
        float x1 = f[0] * 0 + f[1] * 0 + f[2];
        float y1 = f[3] * 0 + f[4] * 0 + f[5];
        float x2 = f[0] * mBitmap.getWidth() + f[1] * 0 + f[2];
        float y2 = f[3] * mBitmap.getWidth() + f[4] * 0 + f[5];
        float x3 = f[0] * 0 + f[1] * mBitmap.getHeight() + f[2];
        float y3 = f[3] * 0 + f[4] * mBitmap.getHeight() + f[5];
        float x4 = f[0] * mBitmap.getWidth() + f[1] * mBitmap.getHeight() + f[2];
        float y4 = f[3] * mBitmap.getWidth() + f[4] * mBitmap.getHeight() + f[5];

        // 图片现宽度
        double width = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        return width;
    }

    /**
     * @return
     */
    private boolean matrixCheck() {
        // 图片现宽度
        double width = getCurrentWidth();
        if ((width > wCrop * 3 && mScale >= 1) || (width < wCrop / 3 && mScale < 1)) {
            return false;
        }
        return true;
    }

    /**
     * 图片旋转
     *
     * @param degree
     */
    public void setRotate(float degree) {
        if (mMatrix == null)
            return;
        mMatrix.setScale(1, 1);
        mMatrix.postRotate(degree);
        Bitmap bitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), mMatrix, true);
        mBitmap = bitmap;
        isFrist = true;
        postInvalidate();
    }


    /**
     * 将移动，缩放以及旋转后的图层保存为新图片
     *
     * @param callBack
     */
    public void CreatNewPhoto(CallBack callBack) {
        Bitmap bitmap = getWindowCacheBitmap();
        String fileName = System.currentTimeMillis() + ".jpg";
        saveBitmap(new FileUtil().getFile(getContext(), "crop", fileName), bitmap, callBack);
    }

    /**
     * 状态栏的高度
     *
     * @param context
     * @return
     */
    @Deprecated
    private int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }


    /**
     * 获取屏幕缓存图形bitmpa
     *
     * @return
     */
    public Bitmap getWindowCacheBitmap() {
        View view = this;
        view.buildDrawingCache();
        view.setDrawingCacheEnabled(true);
        int start = (int) (cropAreaRect.left + lineStoke);
        int top = (int) (cropAreaRect.top + lineStoke);
        int width = (int) (cropAreaRect.right - start - lineStoke);
        int height = (int) (cropAreaRect.bottom - top - lineStoke);
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache(), start, top, width, height);
        // 销毁缓存信息
        view.destroyDrawingCache();
        return bmp;
    }


    /**
     * 存储bitmap到本地文件
     *
     * @param file
     * @param bitmap
     */
    public void saveBitmap(File file, Bitmap bitmap, CallBack callBack) {
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            callBack.goEditImg(file.getAbsolutePath());
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public interface CallBack {
        void goEditImg(String url);
    }

}
