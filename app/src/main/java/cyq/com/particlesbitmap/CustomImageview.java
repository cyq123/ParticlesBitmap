package cyq.com.particlesbitmap;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@SuppressLint("AppCompatCustomView")
public class CustomImageview extends ImageView {
    private List<PixVO> list = new ArrayList<PixVO>();
    private int pixWidth = 6;//复制的像素边长
    private Paint paint = new Paint();
    private ValueAnimator valueAnimator;

    public CustomImageview(final Context context, AttributeSet attrs) {
        super(context, attrs);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.btn_rating_star_on_selected);
        initParticles(bitmap);
        initValueAniater();

        bitmap = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (PixVO vo:list) {
            paint.setColor(vo.getColor());
            canvas.drawRect(vo.getX(),vo.getY(),vo.getX()+ pixWidth,vo.getY()+ pixWidth,paint);
        }
    }
    //1.初始化粒子数据
    private void initParticles(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        for (int i=0;i<width;i++){//行
            for (int j=0;j<height;j++){//列
                int color = bitmap.getPixel(i,j);
                PixVO vo = new PixVO();
                vo.setX(i* pixWidth);//记录每个像素矩阵的左上角x
                vo.setY(j* pixWidth);//y
                vo.setColor(color);
                vo.setVx((int) (Math.pow(-1, Math.ceil(Math.random() * 1000)) * 20 * Math.random()));
                vo.setVy((int) (Math.random()*20+5));

                list.add(vo);
            }
        }
    }

    //2.更新粒子数据
    private void updateParticles(){
        Iterator<PixVO> iterator = list.iterator();
        while(iterator.hasNext()){
            PixVO vo = iterator.next();
            vo.setX(vo.getX()+vo.getVx());
            vo.setY(vo.getY()+vo.getVy());

            int w = this.getWidth();
            int h = this.getHeight();
            if (vo.getX()>w || vo.getY()>h) {//移除视图外的数据
                iterator.remove();
            }
        }
    }

    private void initValueAniater(){
        valueAnimator = ValueAnimator.ofFloat(0,1);// 设置值从0~1递增
        valueAnimator.setDuration(1000);//无论设置多长时间，监听执行的间隔都一样
        valueAnimator.setRepeatCount(-1);//循环次数
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(animatorUpdateListener);//监听大约16~18毫秒执行一次，与duration无关
    }

    boolean state = false;
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (!state){
                    valueAnimator.start();
                    state = true;
                }
                break;
        }
        return true;
    }

    ValueAnimator.AnimatorUpdateListener animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {//执行频率大概16~18毫秒一次
            if (list.size()==0){
                valueAnimator.removeUpdateListener(animatorUpdateListener);
                valueAnimator.cancel();
                state = false;//停止animator

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.btn_rating_star_on_selected);
                initParticles(bitmap);
                invalidate();
                initValueAniater();
                bitmap = null;
                return;
            }
            updateParticles();
            invalidate();

        }
    };

}
