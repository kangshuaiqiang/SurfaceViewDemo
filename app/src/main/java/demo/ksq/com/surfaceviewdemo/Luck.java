package demo.ksq.com.surfaceviewdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by 黑白 on 2017/12/9.
 * 基本编写方法
 * 编写转盘
 */

public class Luck extends SurfaceView implements SurfaceHolder.Callback {

    /**
     * holder管理surfaceview的生命周期还
     */
    private SurfaceHolder kHolder;
    private Canvas kCanvas;

    /**
     * 用于绘制线程
     */

    /**
     * 线程控制开关
     */
    private boolean isRuning;

    //对应的奖项
    private String[] kStr = {"单反相机", "IPAD", "恭喜发财", "IPHONE", "服装一套", "恭喜发财"};
    //盘快图片
    private int[] kImgs = {R.mipmap.danfan, R.mipmap.ipad, R.mipmap.f040, R.mipmap.iphone, R.mipmap.meizi, R.mipmap.f040};
    //对于图片的bitmap
    private Bitmap[] kImgsBitmap;

    private Bitmap kBgBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.bg2);
    //盘快颜色
    private int[] kColor = {0xfffc300, 0xfff17e01, 0xfffc300, 0xfff17e01, 0xfffc300, 0xfff17e01};

    private int kItemCount = 6;

    //盘快
    private RectF krange = new RectF();

    //整个盘快的直径
    private int kRadius;

    //绘制盘快
    private Paint kArcPaint;
    //绘制文本
    private Paint kTextPaint;
    //字体大小
    private float kTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics());

    //滚动速度
    private double kSpeed;
    //volatile  因为可能两个线程修改 保证可见性
    private volatile float kStart = 0;
    //判断是否点击停止按钮
    private boolean isShouldEnd;

    //转盘衷心位置
    private int kCanter;
    //这里我们的padding直接以paddinglift为准
    private int kPadding;


    public Luck(Context context) {
        this(context, null);
    }

    public Luck(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Luck(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        kHolder = getHolder();
        //实现callback
        kHolder.addCallback(this);
        //可获得焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        //设置常量
        setKeepScreenOn(true);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = Math.min(getMeasuredWidth(), getMeasuredHeight());
        kPadding = getPaddingLeft();
        //半径
        kRadius = width - kPadding * 2;
        //中心点
        kCanter = width / 2;
        //设置为正方形
        setMeasuredDimension(width, width);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        //初始化好绘制盘快的画笔
        kArcPaint = new Paint();
        kArcPaint.setAntiAlias(true);
        kArcPaint.setDither(true);

        //初始化绘制文本的
        kTextPaint = new Paint();
        kTextPaint.setColor(Color.WHITE);
        kTextPaint.setTextSize(kTextSize);

        //初始化盘快绘制的范围
        krange = new RectF(kPadding, kPadding, kPadding + kRadius, kPadding + kRadius);

        //初始化图片
        kImgsBitmap = new Bitmap[kItemCount];
        for (int i = 0; i < kItemCount; i++) {
            kImgsBitmap[i] = BitmapFactory.decodeResource(getResources(), kImgs[i]);
        }
        isRuning = true;
        //创建线程
        new Thread() {
            @Override
            public void run() {
                super.run();
                //开始绘制
                while (isRuning) {

                    //强制规定时间
                    long star = System.currentTimeMillis();
                    draw();
                    long end = System.currentTimeMillis();
                    //如果在50毫秒绘制完成  就行休眠一会
//                    if (end - star < 50) {
//                        try {
//                            Thread.sleep(50 - (end - star));
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
                }
            }
        }.start();
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRuning = false;
    }


    /**
     * 核心
     */
    private void draw() {

        try {
            //获取canvas
            kCanvas = kHolder.lockCanvas();
            if (kCanvas != null) {
                //开始绘制
                drawBg();
                //绘制盘快
                float tmpAngle = kStart;
                float sweep = 360 / kItemCount;
                for (int i = 0; i < kItemCount; i++) {
                    kArcPaint.setColor(kColor[i]);
                    //绘制盘快
                    kCanvas.drawArc(krange, tmpAngle, sweep, true, kArcPaint);
                    //绘制文本
                    drawText(tmpAngle, sweep, kStr[i]);

                    drawIcon(tmpAngle, kImgsBitmap[i]);
                    tmpAngle += sweep;
                }

                kStart += kSpeed;
                Log.d("zzzzzz", "" + kSpeed + isShouldEnd + "====" + kStart);
                if (isShouldEnd) {
                    kSpeed -= 1;
                }
                if (kSpeed < 0) {
                    kSpeed = 0;
//                    isRuning = false;
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (kCanvas != null) {
                //释放资源
                kHolder.unlockCanvasAndPost(kCanvas);
            }
        }
    }

    private void drawIcon(float tmpAngle, Bitmap bitmap) {

        //设置图片的宽度  为直径的1/8 解决屏幕适配问题
        int imgWidth = kRadius / 8;

        float angle = (float) ((tmpAngle + 360 / kItemCount / 2) * Math.PI / 180);

        int x = (int) (kCanter + kRadius / 2 / 2 * Math.cos(angle));
        int y = (int) (kCanter + kRadius / 2 / 2 * Math.sin(angle));
        //确定图片的位置
        Rect rect = new Rect(x - imgWidth / 2, y - imgWidth / 2, x + imgWidth / 2, y + imgWidth / 2);

        kCanvas.drawBitmap(bitmap, null, rect, null);


    }

    /**
     * 绘制每个盘块的文本
     *
     * @param tmpAngle
     * @param sweep
     * @param s
     */
    private void drawText(float tmpAngle, float sweep, String s) {
        Path path = new Path();
        path.addArc(krange, tmpAngle, sweep);

        //利用水平偏移量计算文职位置
        float textWidth = kTextPaint.measureText(s);
        int hOffset = (int) (kRadius * Math.PI / kItemCount / 2 - textWidth / 2);

        int vOffset = kRadius / 2 / 6;//垂直偏移量

        kCanvas.drawTextOnPath(s, path, hOffset, vOffset, kTextPaint);

    }

    private void drawBg() {
        kCanvas.drawColor(Color.WHITE);
        //转盘底盘
        kCanvas.drawBitmap(kBgBitmap, null, new Rect(kPadding / 2, kPadding / 2, getMeasuredWidth() - kPadding / 2, getMeasuredHeight() - kPadding / 2), null);
    }


    /**
     * 点击启动旋转
     */
    public void luckStart(int index) {

        //计算每一项的角度
        int angle = 360 / kItemCount;
        //计算每一项的概率
        //150 ~210  1
        //210~270  0

        float from = 270 - (index + 1) * angle;
        float end = from + angle;

        //设置停下来需要旋转的距离
        float targerFrom = 4 * 360 + from;
        float targetEnd = 4 * 360 + end;

        /**
         * v1 ~ 0
         * 每次减一
         * （v1+0）*（v1+1）/2 = targerFrom
         *
         */
        float v1 = (float) ((-1 + Math.sqrt(1 + 8 * targerFrom)) / 2);
        float v2 = (float) ((-1 + Math.sqrt(1 + 8 * targetEnd)) / 2);

//        kSpeed = v1 + Math.random() * (v2 - v1);
        kSpeed = v1;
        isShouldEnd = false;
    }

    //点击停止旋转
    public void luckEnd() {
        kStart = 0;
        isShouldEnd = true;
    }

    /**
     * 判断乱跑是够旋转   如果是0 就停止旋转
     *
     * @return
     */
    public boolean isStarte() {
        return kSpeed != 0;
    }

    public boolean isShouldEnd() {
        return isShouldEnd;
    }


}
