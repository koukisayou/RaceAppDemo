package Game_Logic;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.example.sayoukouki.raceappdemo.GLRenderer;
import com.example.sayoukouki.raceappdemo.R;

import OpenGLES20_furukawa.GLES;
import OpenGLES20_furukawa.Graphics;
import OpenGLES20_furukawa.ObjLoader;
import OpenGLES20_furukawa.Object3D;
import OpenGLES20_furukawa.Texture;

/**
 * ゲームオーバー画面での描画、音声再生、タッチイベントの処理を実行する
 * Created by SayouKouki
 */

public class GameoverScene extends SuperScene {
    /**
     * Field
     */
    private Texture backTexture;//背景画像
    public Object3D objCar = new Object3D();//車の3Dモデル
    private String audioFileName = "gameover_bgm.mp3";//BGMファイルの名称
    public boolean textureLoadComplete = false;//テクスチャのロード確認
    public boolean objLoadComp = false;//オブジェクトのロード確認
    private int nextSceneCount;//タッチ後、次のシーンへ行くまでのカウンタ
    private float machineAngle;//オブジェクトの向き

    /**
     * Constructor.
     */
    public GameoverScene() {
        super();
    }

    public GameoverScene(Activity activity, GLSurfaceView glView){
        super(activity);
        //背景画像の読み込み
        glView.queueEvent(new Runnable(){
            public void run(){
                try{
                    backTexture = Texture.createTextureFromAsset("garage_sample.jpg");

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

        LoadObj(glView, "droidjet.obj");//オブジェクトのロード
        //オーディオ関係の初期化
        initSE(2);//SEの初期化
        LoadSound(activity, R.raw.click2);//クリック音読み込み
        AudioPlay(activity,audioFileName);//BGM再生
    }

    /**
     * 毎フレーム実行する処理
     * @return
     */
    @Override
    public int onTick() {
        //車オブジェクトを回転させる
        machineAngle++;
        if(machineAngle > 360.0f){
            machineAngle = 0.0f;
        }
        //次のシーンへ行くかの確認if
        if(nextScene){
            nextSceneCount++;//次のシーンに行くカウンタ

            //カウンタが満たされたらファイル解放処理をして次のシーンのIDを返す
            if(nextSceneCount > 30){
                nextSceneCount = 0;
                soundPool.release();//SEファイルの解放
                AudioStop();//BGMファイルの解放
                return SceneNumConstant.GO_TITLE_SCENE; //次のシーンへ行く
            }
        }
        return NOT_CHANGE;//次のフレームもこのシーン
    }

    /**
     * タッチイベントリスナー
     * @param x
     * @param y
     */
    @Override
    public void onTouch(int x, int y) {
        if(!(x == -1)) {
            Log.d("touchListener", "onTouch!");
            /**SoundPoolでSE再生
             * public final int play (int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate)
             * argument:
             *      soundID:再生するSEのID
             *      leftVolume, rightVolume:左右の音量
             *      priority:再生優先度(0が一番高い)
             *      loop:ループ回数。-1 = 無限ループ, 0 = ループしない
             *      rate:再生速度。0.5~2.0(0.5倍~2倍)
             */
            soundPool.play(seId[0], 1.0f, 1.0f, 0, 0, 1.0f);//タッチ音の再生
            nextScene = true;//次のシーンへ行く
        }
    }

    /**
     * Resume時の動作
     */
    @Override
    public void onResume(){

    }

    @Override
    public void onResume(Activity activity) {
        initSE(2);//SEの初期化
        LoadSound(activity, R.raw.click_sample1);//クリック音の読み込み
        AudioPlay(activity,audioFileName);//BGM再生
    }

    /**
     * Pause時の動作
     */
    @Override
    public void onPause() {
        soundPool.release();//SE解放
        AudioStop();//BGM停止処理
    }

    /**
     * サーフェイス作成時
     */
    @Override
    public void onSurfaceCreated() {

    }

    @Override
    public void onSurfaceCreated(GLSurfaceView glView){

    }

    /**
     * 画面サイズ変更時
     * @param w
     * @param h
     * @return
     */
    @Override
    public Graphics onSurfaceChanged(int w, int h) {
        return null;
    }

    /**
     * 描画を行う。
     * 描画対象は
     *      背景画像
     *      オブジェクト表示
     *      "Thankyou for Playing!"の文字描画
     */
    @Override
    public void onDraw() {
        initDraw();

        if(textureLoadComplete) {
            g.init();
            g.drawImage(backTexture, 0, 0, width, height);
        }
        init3D();

        if(objLoadComp) {
            objCar.rotate.set(0, machineAngle, 0);
            objCar.position.set(5.0f, -0.5f, -1.9f);
            GLES20.glEnableVertexAttribArray(GLES.positionHandle);
            GLES.glPushMatrix();
            GLES20.glUniform1i(GLES.useLightHandle,1);
            objCar.draw();
            GLES.glPopMatrix();
            GLES20.glDisableVertexAttribArray(GLES.positionHandle);
        }

        ft.Draw("ThankyouforPlaying!", 100,height / 5,3,3,0.95f,0.95f,0.95f,1.0f, g);
    }

    @Override
    public void onDraw(GLRenderer renderer) {

    }

    /**
     * このゲームシーンで使用するobjファイルを読み込む
     * @param glView
     * @param fileName
     */
    private void LoadObj(GLSurfaceView glView, final String fileName){
        glView.queueEvent(new Runnable(){
            public void run(){
                try{
                    objCar.figure = ObjLoader.load(fileName);
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
}
