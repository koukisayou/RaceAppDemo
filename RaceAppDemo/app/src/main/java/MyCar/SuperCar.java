package MyCar;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.example.sayoukouki.raceappdemo.GLRenderer;

import OpenGLES20_furukawa.GLES;
import OpenGLES20_furukawa.ObjLoader;
import OpenGLES20_furukawa.Object3D;
import OpenGLES20_furukawa.Vector3;

/**
 * プレイヤーの操作する車のスーパークラス
 * モデルデータの描画と、ボタン操作に応じた移動を行う
 */
public abstract class SuperCar {
    /**
     * Field
     */
    //グラフィックス関係のフィールド
    public String objname;//オブジェクトファイルの名称
    protected Object3D model;
    public Vector3 coord;//現在x,y,z座標
    public Vector3 nextCoord;//移動先x,y,z座標

    //車の動きに関するフィールド
    protected double A;//加速度
    protected double F;//摩擦力、減速度
    protected double speed;//現在速度
    protected double TOP_SPEED;//最高速度
    protected double BACK_MAX_SPEED;//バック時の最高速度
    public double angle;//xz平面前方角度, デグリー角
    protected double curve;//カーブ時の増減角
    protected double CURVE_ANGLE;//1Fごとに増えるカーブの角度
    protected double MAX_CURVE_ANGLE;//カーブの限界角

    //プレイヤーのタッチイベントを、このクラスが取得するためのフィールド
    public boolean touchLeft = false;
    public boolean touchRight = false;
    public boolean touchAccele = false;
    public boolean touchBrake = false;

    public boolean checkDraw = false;

    //レースに関するフィールド
    public boolean checkPoint = false;
    public boolean goal = false;

    /**
     * Constructor.
     */
    public SuperCar(GLSurfaceView glView){
        //座標の初期化
        angle = 270;
        coord = new Vector3(30.0f, 0.0f, 210.0f);
        nextCoord = new Vector3(30.0f, 0.0f, 210.0f);
        curve = 0.0;
        initFields();//フィールドの初期化

        //モデルデータのロード
        model = new Object3D();
        LoadObj(glView);
    }

    public SuperCar(String objname){
        //フィールドの初期化
        angle = 0;
        coord = new Vector3(0.0f, 0.0f, 0.0f);

        //Load 3D model
        try{
            model.figure = ObjLoader.load(objname);
        }catch(Exception e){
            android.util.Log.e("debug", e.toString());
            for(StackTraceElement ste:e.getStackTrace()){
                android.util.Log.e("debug", "    " + ste);
            }
        }
    }

    /**
     * ステータスフィールドの初期化を行う
     */
    protected abstract void initFields();

    /**
     * objファイルの読み込みを行う
     * @param glView GLSurfaceView
     */
    protected void LoadObj(GLSurfaceView glView){
        glView.queueEvent(new Runnable(){
            public void run(){
                try{
                    model.figure = ObjLoader.load(objname);
                    checkDraw = true;
                }catch(Exception e) {
                    android.util.Log.e("debug", e.toString());
                    for (StackTraceElement ste : e.getStackTrace()) {
                        android.util.Log.e("debug", "    " + ste);
                    }
                }
            }
        });
    }

    /**
     * Drawing methods.
     */
    public void Draw(){
        model.draw();
    }
    public void Draw(GLRenderer renderer){
        PositionSet(coord);
        RotateSet(0.0f, -90.0f - (float)angle,0.0f);
        if(checkDraw){
            GLES20.glUniform1i(GLES.useLightHandle,1);
            model.draw();
        }
    }

    /**
     * モデルデータの回転
     * @param x
     * @param y
     * @param z
     */
    public void RotateSet(float x, float y, float z){model.rotate.set(x, y, z);}
    public void RotateSet(Vector3 origin){model.rotate.set(origin);}

    /**
     * モデルデータの平行移動
     * @param x
     * @param y
     * @param z
     */
    public void PositionSet(float x, float y, float z){model.position.set(x, y, z);}
    public void PositionSet(Vector3 origin){model.position.set(origin);}

    /**
     * 1F内の動きを管理。
     * 次回座標を設定。
     */
    public void checkMove(){
        //@TODO マルチタッチへの対応が取れていないif文
        if(touchAccele) {
            Accele();
        }else if(touchBrake) {
            Brake();
        }else{
            //徐々に車を停止させる
            //前進を止める
            if(speed > 0){
                speed -= F;
                if(speed < 0){
                    speed = 0;
                }
            }
            //後退を止める
            else if(speed < 0){
                speed += F;
                if(speed > 0){
                    speed = 0;
                }
            }
        }

        //カーブをさせる
        if(touchLeft) {
            LeftCurve();
        }else if(touchRight){
            RightCurve();
        }else{
            curve = 0;
        }

        nextCoord.x = (float) (coord.x + -speed * Math.cos(angle * Math.PI / 180.0));//x座標更新
        nextCoord.z = (float) (coord.z + -speed * Math.sin(angle * Math.PI / 180.0));//z座標更新
    }

    /**
     * 実際に移動させる
     */
    public void Moving(){
        coord.x = nextCoord.x;
        coord.z = nextCoord.z;
    }

    /**
     * アクセル動作
     */
    public void Accele(){
        if(TOP_SPEED > speed)speed += A;//現在速度に加速
        else speed = TOP_SPEED;//最高速状態
    }

    /**
     * ブレーキ動作
     * 停車状態でバックができる
     */
    public void Brake(){
        if(BACK_MAX_SPEED < speed)speed -= A;//現在速度に加速
        else speed = BACK_MAX_SPEED;//最高速状態
    }

    /**
     * 左カーブ
     */
    public void LeftCurve(){
        if(MAX_CURVE_ANGLE > curve)curve += CURVE_ANGLE;//現在速度に加速
        else curve = MAX_CURVE_ANGLE;//最高速状態
        angle -= curve;//角度の更新
        if(angle < 0){angle += 360;}//360超えないようにする
    }

    /**
     * 右カーブ
     */
    public void RightCurve(){
        if(MAX_CURVE_ANGLE > curve)curve += CURVE_ANGLE;//現在速度に加速
        else curve = MAX_CURVE_ANGLE;//最高速状態
        angle += curve;//角度の更新
        if(angle > 360){angle -= 360;}//360超えないようにする
    }
}
