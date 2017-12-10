package demo.ksq.com.surfaceviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by 黑白 on 2017/12/9.
 * 基本编写方法
 */

public class SurfaceViewTempalte extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    /**
     * holder管理surfaceview的生命周期还
     */
    private SurfaceHolder kHolder;
    private Canvas kCanvas;

    /**
     * 用于绘制线程
     */
    private Thread t;
    /**
     * 线程控制开关
     */
    private boolean isRuning;

    public SurfaceViewTempalte(Context context) {
        this(context, null);
    }

    public SurfaceViewTempalte(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SurfaceViewTempalte(Context context, AttributeSet attrs, int defStyleAttr) {
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
    public void surfaceCreated(SurfaceHolder holder) {
        isRuning = true;
        //创建线程
        t = new Thread(this);
        //开启线程
        t.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRuning = false;
    }

    @Override
    public void run() {
        //开始绘制
        while (isRuning) {
            draw();
        }
    }

    private void draw() {

        try {
            //获取canvas
            kCanvas = kHolder.lockCanvas();
            if (kCanvas != null) {
                //开始绘制
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
}
