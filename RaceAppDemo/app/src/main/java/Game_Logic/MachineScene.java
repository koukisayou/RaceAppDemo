package Game_Logic;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.example.sayoukouki.raceappdemo.GLRenderer;
import com.example.sayoukouki.raceappdemo.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;

import OpenGLES20_furukawa.GLES;
import OpenGLES20_furukawa.Graphics;
import OpenGLES20_furukawa.ObjLoader;
import OpenGLES20_furukawa.Object3D;
import OpenGLES20_furukawa.Texture;

/**
 * マシン選択画面
 * Created by SayouKouki on 2017/04/13.
 */

public class MachineScene extends SuperScene {
    /**
     * Field
     */
    //選択マシン番号
    //0 = Normal, 1 = Accele speed, 2 = Max speed.
    private int machineNum = 0;//選択中のマシン
    private static final int MACHINE_NORMAL = 0;
    private static final int MACHINE_ACCELE = 1;
    private static final int MACHINE_MAXIMUM = 2;

    public Object3D[] objArray = new Object3D[3];
    public HashMap<String, Object3D> objHashMap = new HashMap<String, Object3D>();//車のobjファイルを保持

    public boolean buttonClick = false;//なんらかのボタンがタッチされているか
    public boolean rb = false;//右ボタン
    public boolean lb = false;//左ボタン
    public boolean nb = false;//決定ボタン

    private int nextSceneCount;//タッチ後、次のシーンへ行くまでのカウンタ

    //このシーンで使うBGMファイルの名称
    private String audioFileName = "machine_bgm.mp3";

    private Texture backTexture;//背景画像
    public boolean textureLoadComplete = false;
    public boolean objLoadComp = false;

    private FloatBuffer vertexBuffer;
    private FloatBuffer uvBuffer;
    private ByteBuffer indexBuffer;
    private float machineAngle;

    /**
     *      Constructor.
     */
    public MachineScene(Activity activity){
        super(activity);
        nextSceneCount = 0;
    }

    public MachineScene(Activity activity, GLSurfaceView glView){
        super(activity);
        nextSceneCount = 0;
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
        LoadAllObj(glView);
        /*float[] vertexs = {
                60, height / 2, -width / 2,//頂点0左上
                60, -height / 2, -width / 2,//頂点1左下
                60, height / 2, width / 2,//頂点2右上
                60, -height / 2, width / 2,//頂点3右下
        };*/
        /*float[] vertexs = {
                200, height / 2 /10, -width / 2 /10,//頂点0左上
                200, -height / 2 /10, -width / 2 /10,//頂点1左下
                200, height / 2 /10, width / 2 /10,//頂点2右上
                200, -height / 2 /10, width / 2 /10,//頂点3右下
        };
        vertexBuffer = makeFloatBuffer(vertexs);

        byte[] indexs = {
                0,1,2,3,
        };
        indexBuffer = makeByteBuffer(indexs);

        //UVバッファの生成
        float[] uvs={
                0.0f,0.0f,//左上
                0.0f,1.0f,//左下
                1.0f,0.0f,//右上
                1.0f,1.0f,//右下
        };
        uvBuffer=makeFloatBuffer(uvs);*/
        machineAngle = 0.0f;
        /**
         * SoundPool instance.
         */
        initSE(2);//SEの初期化
        LoadSound(activity, R.raw.machinechoise);
        LoadSound(activity, R.raw.go_play);
        AudioPlay(activity,audioFileName);
    }

    public int getMachineNum(){
        return machineNum;
    }

    /**************************************************
     *          System methods.
     **************************************************/
    @Override
    public void onResume(){

    }

    @Override
    public void onResume(Activity activity) {
        initSE(2);//SEの初期化
        LoadSound(activity, R.raw.machinechoise);
        LoadSound(activity, R.raw.click_sample1);
        AudioPlay(activity,audioFileName);
    }

    @Override
    public void onPause() {
        soundPool.release();//解放
        AudioStop();//mediaPlayer停止処理
    }

    @Override
    public void onSurfaceCreated() {

    }

    @Override
    public void onSurfaceCreated(GLSurfaceView glView){

    }

    @Override
    public Graphics onSurfaceChanged(int w, int h) {
        return null;
    }

    @Override
    public void onDraw() {
        initDraw();

        if(textureLoadComplete) {
            g.init();
            g.drawImage(backTexture, 0, 0, width, height);
            //g.drawImage(backTexture, 0, 0, 300, 400);
        }

        init3D();
        //ビュー変換
        Matrix.setIdentityM(GLES.mMatrix,0);
        GLES.gluLookAt(GLES.mMatrix,
                0.0f, 0.0f, 0.0f, //カメラの視点
                1.0f, 0.0f, 0.0f, //カメラの焦点
                0.0f, 1.0f, 0.0f);//カメラの上方向

        float[] lightPos = {0.0f, 999.0f, 0.0f, 1.0f};//光源の位置
        //光源位置の指定
        float[] resultM = new float[4];
        Matrix.multiplyMV(resultM, 0, GLES.mMatrix, 0, lightPos, 0);
        GLES20.glUniform4f(GLES.lightPosHandle, resultM[0], resultM[1], resultM[2],resultM[3]);

        /*if(textureLoadComplete) {
            BackImageDraw(backTexture);
            //g.init();
            //g.drawImage(backTexture, 0, 0, width, height);
            //g.drawImage(backTexture, 0, 0, 300, 400);
        }*/

        if(objLoadComp) {
            objArray[0].rotate.set(0, machineAngle, 0);
            objArray[0].position.set(5.0f, -0.5f, -1.5f);
            GLES20.glEnableVertexAttribArray(GLES.positionHandle);
            GLES.glPushMatrix();
            GLES20.glUniform1i(GLES.useLightHandle,1);
            objArray[0].draw();
            GLES.glPopMatrix();
            GLES20.glDisableVertexAttribArray(GLES.positionHandle);
        }
    }

    @Override
    public void onDraw(GLRenderer renderer) {

    }

    /**************************************************
     *              定期処理
     **************************************************/
    /**
     * @return:次のシーンへ行って良いか。true = Yes, false = No.
     */
    @Override
    public int onTick() {
        machineAngle++;
        if(machineAngle > 360.0f){
            machineAngle = 0.0f;
        }

        //ボタンがクリックされていれば実行する
        if(buttonClick && !(nextScene)){onButtonClick();}

        //次のシーンへ行くかの確認if
        if(nextScene){
            nextSceneCount++;//次のシーンに行くカウンタ

            //カウンタが満たされたらデストラクタ的処理をして次のシーンのIDを返す
            if(nextSceneCount > 30){
                nextSceneCount = 0;
                soundPool.release();//SEファイルの解放
                AudioStop();//BGMファイルの解放
                return SceneNumConstant.GO_PLAY_SCENE; //次のシーンへ行く
            }
        }
        return NOT_CHANGE;
    }

    /**************************************************
     *          Touch Event Listener
     **************************************************/
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
            //soundPool.play(seId[0], 1.0f, 1.0f, 0, 0, 1.0f);//タッチ音の再生
        }

    }

    /**
     * MachineFragment の ButtonClickEventListener から命令を受け、
     * ボタンごとのロジックを動作させる
     */
    private void onButtonClick(){
        if(rb){
            soundPool.play(seId[0], 1.0f, 1.0f, 0, 0, 1.0f);//タッチ音の再生
            if(++machineNum > MACHINE_MAXIMUM){
                machineNum = MACHINE_NORMAL;
            }
        }else if(lb){
            soundPool.play(seId[0], 1.0f, 1.0f, 0, 0, 1.0f);//タッチ音の再生
            if(--machineNum < MACHINE_NORMAL){
                machineNum = MACHINE_MAXIMUM;
            }
        }else if(nb){
            soundPool.play(seId[1], 1.0f, 1.0f, 0, 0, 1.0f);//タッチ音の再生
            nextScene = true;//次のシーンへ
        }

        rb = lb = nb = buttonClick = false;//ボタンクリックフィールドを初期化
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
                    objArray[0] = LoadObj("droidjet.obj");
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

    public void BackImageDraw(Texture texture) {
        GLES20.glUniform4f(GLES.materialAmbientHandle, 1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glUniform4f(GLES.materialDiffuseHandle, 1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glUniform4f(GLES.materialSpecularHandle, 1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glUniform1f(GLES.materialShininessHandle, 80.0f);

        texture.bind();

        GLES20.glEnableVertexAttribArray(GLES.positionHandle);
        //GLES20.glEnableVertexAttribArray(GLES.normalHandle);
        GLES20.glEnableVertexAttribArray(GLES.uvHandle);

        //テクスチャ行列の移動・拡縮
        Matrix.setIdentityM(GLES.texMatrix,0);
        Matrix.translateM(GLES.texMatrix,0,1,1,0.0f);
        Matrix.scaleM(GLES.texMatrix,0,1,1,1.0f);
        GLES20.glUniformMatrix4fv(GLES.texMatrixHandle,1,
                false,GLES.texMatrix,0);

        GLES20.glVertexAttribPointer(GLES.positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        //GLES20.glVertexAttribPointer(GLES.normalHandle, 3, GLES20.GL_FLOAT, false, 0, normalBuffer);
        GLES20.glVertexAttribPointer(GLES.uvHandle,2, GLES20.GL_FLOAT,false,0,uvBuffer);
        GLES.glPushMatrix();
        GLES.updateMatrix();
        GLES20.glUniform1i(GLES.useLightHandle,0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 4, GLES20.GL_UNSIGNED_BYTE, indexBuffer);

        GLES20.glDisableVertexAttribArray(GLES.positionHandle);
        //GLES20.glDisableVertexAttribArray(GLES.normalHandle);
        GLES20.glDisableVertexAttribArray(GLES.uvHandle);
        texture.unbind();
        GLES.glPopMatrix();
    }

    //float配列をバッファに変換
    private FloatBuffer makeFloatBuffer(float[] array){
        FloatBuffer fb = ByteBuffer.allocateDirect(array.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        fb.put(array).position(0);
        return fb;
    }

    //byte配列をバッファに変換
    private ByteBuffer makeByteBuffer(byte[] array){
        ByteBuffer bb = ByteBuffer.allocateDirect(array.length).order(ByteOrder.nativeOrder());
        bb.put(array).position(0);
        return bb;
    }
}
