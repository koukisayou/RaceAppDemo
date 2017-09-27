package MyCar;

import android.opengl.GLSurfaceView;

/**
 * 最高速度も加速度も平均的な車
 * Created by SayouKouki
 */

public class NormalCar extends SuperCar {
    public NormalCar(GLSurfaceView glView) {
        super(glView);
    }

    public NormalCar(String objname) {
        super(objname);
    }

    /**
     * ステータスフィールドの初期化を行う
     */
    @Override
    protected void initFields() {
        A = 0.3;//加速度
        F = 0.35;//摩擦力、減速度
        TOP_SPEED = 3.0;//最高速度
        BACK_MAX_SPEED = -2.0;//バック時の最高速度
        CURVE_ANGLE = 1.0;//1Fごとに増えるカーブの角度
        MAX_CURVE_ANGLE = 10.0;//カーブの限界角
        objname = "droidjet.obj";
    }
}
