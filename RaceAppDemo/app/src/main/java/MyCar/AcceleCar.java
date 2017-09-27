package MyCar;

import android.opengl.GLSurfaceView;

/**
 * 加速値が高い代わりに最高速度が低い車
 * Created by SayouKouki
 */

public class AcceleCar extends SuperCar {
    public AcceleCar(GLSurfaceView glView) {
        super(glView);
    }

    public AcceleCar(String objname) {
        super(objname);
    }

    /**
     * ステータスフィールドの初期化を行う
     */
    @Override
    protected void initFields() {
        A = 0.4;//加速度
        F = 0.35;//摩擦力、減速度
        TOP_SPEED = 2.5;//最高速度
        BACK_MAX_SPEED = -2.0;//バック時の最高速度
        CURVE_ANGLE = 1.5;//1Fごとに増えるカーブの角度
        MAX_CURVE_ANGLE = 11.0;//カーブの限界角
    }
}