package Game_Logic;

/**
 * タイトルシーン
 * Created by SayouKouki on 2017/04/13.
 */

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.example.sayoukouki.raceappdemo.FontTexture;
import com.example.sayoukouki.raceappdemo.GLRenderer;
import com.example.sayoukouki.raceappdemo.R;

import OpenGLES20_furukawa.GLES;
import OpenGLES20_furukawa.Graphics;
import OpenGLES20_furukawa.ObjLoader;
import OpenGLES20_furukawa.Object3D;
import OpenGLES20_furukawa.Texture;

/**
 * タイトル画面での処理
 * Created by SayouKouki
 */
public class TitleScene extends SuperScene {
    /**
     * Field
     */
    private String audioFileName = "hidden_title_bgm.mp3";//BGMファイル名

    //オープニングアニメーション関係
    private Object3D obj = new Object3D();//3Dモデル
    private float objCoordY = 0.0f;
    private float objCoordZ = -12.0f;
    private float objRotateY = 0.0f;
    private float objRotateZ = 0.0f;
    private Texture asphalt;
    private Texture green;
    private Texture logo;
    private int texCoord;//道テクスチャ描画時の基準x座標
    private static final int TEX_COUNT = 8;
    private int maxTexCoord;

    private boolean soundLoadComplete = false;
    private boolean textureLoadComplete = false;
    public boolean objLoadComp = false;

    private int nextSceneCount;//タッチ後、次のシーンへ行くまでのカウンタ

    /**
     * Constructor
     */
    public TitleScene(Activity activity){
        super(activity);
    }

    public TitleScene(Activity activity, GLSurfaceView glView){
        super(activity);
        texCoord = width / TEX_COUNT;
        maxTexCoord = texCoord;
        //テクスチャの読み込み
        glView.queueEvent(new Runnable(){
            public void run(){
                try{
                    asphalt = LoadTexture("asphalt1.jpg");
                    green = LoadTexture("green.png");
                    logo = LoadTexture("logo.png");
                    Log.i("Loading", "texture load complete!!");
                }catch(Exception e){
                    android.util.Log.e("debug", e.toString());
                    for(StackTraceElement ste:e.getStackTrace()){
                        android.util.Log.e("debug","    " + ste);
                    }
                }
                textureLoadComplete = true;//素材のロード完了を通知
            }
        });

        LoadAllObj(glView);//オブジェクトの読み込み

        //サウンドの読み込み
        if(!(soundLoadComplete)){
            initSE(2);//SEの初期化
            LoadSound(activity, R.raw.titleclick);
            AudioPlay(activity,audioFileName);
            soundLoadComplete = true;
        }

        nextSceneCount = 0;//初期化
    }

    /**
     * 再開時の動作
     */
    @Override
    public void onResume(){

    }

    @Override
    public void onResume(Activity activity) {
        //サウンドの読み込み
        if(!(soundLoadComplete)){
            initSE(2);//SEの初期化
            LoadSound(activity, R.raw.titleclick);
            AudioPlay(activity,audioFileName);
            soundLoadComplete = true;
        }
    }

    /**
     * 停止時の動作
     */
    @Override
    public void onPause() {
        soundPool.release();//解放
        AudioStop();//mediaPlayer停止処理
        soundLoadComplete = false;
    }

    /**
     * サーフェイス生成時に呼ばれる
     */
    @Override
    public void onSurfaceCreated() {

    }

    @Override
    public void onSurfaceCreated(GLSurfaceView glView) {
        //テクスチャの読み込み
        glView.queueEvent(new Runnable(){
            public void run(){
                try{
                    asphalt = LoadTexture("asphalt1.jpg");
                    green = LoadTexture("green.png");
                    logo = LoadTexture("logo.png");
                    Log.i("Loading", "texture load complete!!");
                }catch(Exception e){
                    android.util.Log.e("debug", e.toString());
                    for(StackTraceElement ste:e.getStackTrace()){
                        android.util.Log.e("debug","    " + ste);
                    }
                }
                textureLoadComplete = true;//素材のロード完了を通知
            }
        });

        LoadAllObj(glView);//オブジェクトの読み込み
    }

    /**
     * 画面サイズ変更時に動作する
     * @param w
     * @param h
     * @param g
     */
    @Override
    public void onSurfaceChanged(int w, int h, Graphics g){
        width = w;
        height = h;
        aspect = (float)w / (float)h;
        this.g = g;
        ft = new FontTexture(activity,g);
        texCoord = width / TEX_COUNT;
        maxTexCoord = texCoord;
    }

    /**
     * 毎フレーム実行する処理
     */
    @Override
    public int onTick(){
        //次のシーンへ行くかの確認if
        if(nextScene){
            nextSceneCount++;//次のシーンに行くカウンタ

            //カウンタが満たされたらデストラクタ的処理をして次のシーンのIDを返す
            if(nextSceneCount > 30){
                nextScene = false;
                nextSceneCount = 0;
                soundPool.release();//SEファイルの解放
                AudioStop();//BGMファイルの解放
                soundLoadComplete = false;
                return GO_MACHINE_CHOISE_SCENE; //次のシーンへ行く
           }
        }

        //地面テクスチャの移動
        texCoord -= 8;
        if(texCoord < 0){
            texCoord = maxTexCoord;
        }

        //車オブジェクトの移動
        //画面中央(z = 0.0f)に移動させる。Z値を増やすことで画面内の車が右へ進む
        if(objCoordZ < 0.0f){
            objCoordZ += 0.05f;

            //車が -3.0f を超えたら
            if(objCoordZ > -3.0f){
                //車が右折しているように動かす
                if(objCoordY > -1.0f){
                    objCoordY -= 0.05f;//Y座標移動
                    if(objRotateZ < 30.0f){
                        objRotateZ += 3.0f;//車を右に傾ける
                    }
                }else{
                    objRotateZ = CoordAdjust(objRotateZ,3.0f);//車の傾きを戻す
                }
            }
            //車が -6.0f を超えたら
            else if(objCoordZ > -6.0f){
                //車が左折しているように動かす
                if(objCoordY < 1.0f){
                    objCoordY += 0.05f;//Y座標移動
                    if(objRotateZ > -30.0f){
                        objRotateZ -= 3.0f;//車を左に傾ける
                    }
                }else{
                    objRotateZ = CoordAdjust(objRotateZ,3.0f);//車の傾きを戻す
                }
            }
        }else{
            //画面中央に来ているので車の傾き角度を調整する
            objCoordY = CoordAdjust(objCoordY,0.05f);
            objRotateY = CoordAdjust(objRotateY,3.0f);
            objRotateZ = CoordAdjust(objRotateZ,3.0f);
        }

        return SceneNumConstant.NOT_CHANGE;//次のフレームもこのシーンを実行
    }

    /**
     * Drawing methods.
     */
    @Override
    public void onDraw(){
        initDraw();
        g.init();
        //テクスチャの描画
        if(textureLoadComplete){
            GLES20.glUniform4f(GLES.colorHandle, 1.0f, 1.0f, 1.0f,1.0f);
            int texWidth = width / TEX_COUNT;
            int texHeight = height / 6;
            for(int i = 0; i <= TEX_COUNT; i++){
                //芝生の描画
                g.drawImage(green,texCoord + texWidth * i - texWidth,(int)(height * 0.5), texWidth, height / 2);
            }
            for(int i = 0; i <= TEX_COUNT; i++){
                //アスファルトの描画
                g.drawImage(asphalt,texCoord + texWidth * i - texWidth,(int)(height * 0.7),texWidth,texHeight);
            }
            g.drawImage(logo,(int)(width * 0.5 - 631),(int)(height * 0.06),1262,575);//タイトルロゴの描画
        }

        //3D描画
        if(objLoadComp) {
            init3D();
            //ビュー変換
            Matrix.setIdentityM(GLES.mMatrix,0);
            GLES.gluLookAt(GLES.mMatrix,
                    0.0f, 2.0f, 0.0f, //カメラの視点
                    1.0f, 2.0f, 0.0f, //カメラの焦点
                    0.0f, 1.0f, 0.0f);//カメラの上方向

            float[] lightPos = {0.0f, 999.0f, 0.0f, 1.0f};//光源の位置
            //光源位置の指定
            float[] resultM = new float[4];
            Matrix.multiplyMV(resultM, 0, GLES.mMatrix, 0, lightPos, 0);
            GLES20.glUniform4f(GLES.lightPosHandle, resultM[0], resultM[1], resultM[2],resultM[3]);

            obj.rotate.set(0, objRotateY, objRotateZ);
            obj.position.set(10.0f, objCoordY, objCoordZ);
            GLES20.glEnableVertexAttribArray(GLES.positionHandle);
            GLES.glPushMatrix();
            GLES20.glUniform1i(GLES.useLightHandle,1);
            obj.draw();//車の描画
            GLES.glPopMatrix();
            GLES20.glDisableVertexAttribArray(GLES.positionHandle);
        }
    }

    @Override
    public void onDraw(GLRenderer renderer) {

    }

    /**
     * Touch Event Listener
     */
    @Override
    public void onTouch(int x, int y){
        if(!(x == -1)) {
            soundPool.play(seId[0], 1.0f, 1.0f, 0, 0, 1.0f);//タッチ音の再生
            nextScene = true;
        }
    }

    /**
     * このゲームシーンで使用するobjファイルを全て読み込む
     * @param glView
     */
    private void LoadAllObj(GLSurfaceView glView){
        glView.queueEvent(new Runnable(){
            public void run(){
                try{
                    //objHashMap.put("NomalCar", LoadObj("droidjet.obj"));
                    obj = LoadObj("droidjet.obj");
                    objLoadComp = true;
                }catch(Exception e){
                    android.util.Log.e("debug", e.toString());
                    for(StackTraceElement ste:e.getStackTrace()){
                        android.util.Log.e("debug","    " + ste);
                    }
                }
            }
        });
    }

    /**
     * objファイルを読み込み、Object3Dクラスのインスタンスを作成する
     * @param fileName ファイル名
     * @return objファイルのモデルデータ
     */
    public Object3D LoadObj(String fileName){
        Object3D model = new Object3D();
        try{
            model.figure = ObjLoader.load(fileName);
        }catch(Exception e){
            android.util.Log.e("debug", e.toString());
            for(StackTraceElement ste:e.getStackTrace()){
                android.util.Log.e("debug","    " + ste);
            }
        }
        return model;
    }

    /**
     * アニメーションを行う車オブジェクトの傾きを緩やかに平行に戻す
     * @param coord 現在の車の角度
     * @param value アニメーションの増減角度(傾けていた角度値)
     * @return 計算後の角度
     */
    private float CoordAdjust(float coord, float value) {
        if(coord < value + value && coord > -(value + value)){
            return 0;//完全に平行に戻す
        }else if(coord > 0){
            return coord -= value;
        }else if(coord < 0){
            return coord += value;
        }
        return coord;//変更なし
    }
}
