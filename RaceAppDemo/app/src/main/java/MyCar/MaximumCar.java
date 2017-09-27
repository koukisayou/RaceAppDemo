package MyCar;

import android.opengl.GLSurfaceView;

/**
 * 最高速度が高い代わりに加速値が低い車
 * Created by SayouKouki
 */
public class MaximumCar extends SuperCar {
    public MaximumCar(GLSurfaceView glView) {
        super(glView);
    }

    public MaximumCar(String objname) {
        super(objname);
    }

    /**
     * ステータスフィールドの初期化を行う
     */
    @Override
    protected void initFields() {
        A = 0.25;//加速度
        F = 0.3;//摩擦力、減速度
        TOP_SPEED = 4.0;//最高速度
        BACK_MAX_SPEED = -2.0;//バック時の最高速度
        CURVE_ANGLE = 0.8;//1Fごとに増えるカーブの角度
        MAX_CURVE_ANGLE = 9.0;//カーブの限界角
    }
}
