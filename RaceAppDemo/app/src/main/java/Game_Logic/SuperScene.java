package Game_Logic;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.example.sayoukouki.raceappdemo.FontTexture;
import com.example.sayoukouki.raceappdemo.GLRenderer;
import com.example.sayoukouki.raceappdemo.MainActivity;

import java.io.IOException;

import OpenGLES20_furukawa.GLES;
import OpenGLES20_furukawa.Graphics;
import OpenGLES20_furukawa.Texture;

import static OpenGLES20_furukawa.GLES.context;

/**
 * シーンクラスのスーパークラス
 * Created by SayouKouki
 */

public abstract class SuperScene implements SceneNumConstant {
    /**
     * Field
     */
    protected static MainActivity activity;
    protected boolean nextScene = false;//trueになると次のシーンに行く

    //2Dのグラフィックス関係
    protected static int width, height;
    protected static float aspect;
    protected static Graphics g;
    protected static FontTexture ft;

    //MediaPlayer関係, BGMファイルを扱う
    protected static MediaPlayer mediaPlayer = null;

    //SoundPool関係, SEファイルを扱う
    protected AudioAttributes audioAttributes;//サウンドプールへの初期設定に使用する
    protected SoundPool soundPool;//サウンドプールの実体
    protected int[] seId;//SEファイルのID
    protected int seNum;//必要なSEファイルの個数

    /**
     * Constructor
     */
    public SuperScene(){

    }

    public SuperScene(Activity activity){
        this.activity = (MainActivity)activity;
    }

    /**
     * 再開時の動作
     * 音楽ファイルを再読み込み
     */
    public abstract void onResume();
    public abstract void onResume(Activity activity);

    /**
     * 停止時の動作
     * 音楽ファイルを解放
     */
    public abstract void onPause();

    /**
     * サーフェイス生成時に呼ばれる
     */
    public abstract void onSurfaceCreated();
    public abstract void onSurfaceCreated(GLSurfaceView glView);

    /**
     * 画面サイズ変更時の動作
     * グラフィックスオブジェクトの生成
     */
    public Graphics onSurfaceChanged(int w, int h){
        width = w;
        height = h;
        aspect = (float)w / (float)h;
        g = new Graphics(w, h);
        ft = new FontTexture(activity,g);
        return g;
    }

    public void onSurfaceChanged(int w, int h, Graphics g){
        width = w;
        height = h;
        aspect = (float)w / (float)h;
        this.g = g;
        ft = new FontTexture(activity,g);
    }

    /**
     * 描画
     */
    public abstract void onDraw();
    public abstract void onDraw(GLRenderer renderer);

    /**
     * 描画のための初期化メソッドの呼び出し
     *      背景色を設定
     *      カラーバッファの設定
     *      デプスバッファの設定
     */
    protected void initDraw(){
        GLES20.glClearColor(0.62745f, 0.84705f, 0.937254f, 1.0f);//背景色を空色に,rgb = 160,216,239
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|
                GLES20.GL_DEPTH_BUFFER_BIT);
    }

    /**
     * 3D描画のための初期化メソッドの呼び出し
     *      光源設定
     *      射影変換
     */
    protected void init3D(){
        //デプステストと光源の有効化
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glUniform1i(GLES.useLightHandle,1);

        //射影変換
        Matrix.setIdentityM(GLES.pMatrix,0);
        GLES.gluPerspective(GLES.pMatrix,
                45.0f,  //Y方向の画角
                aspect, //アスペクト比
                0.01f,  //ニアクリップ
                1000.0f);//ファークリップ

        //光源位置の指定
        GLES20.glUniform4f(GLES.lightPosHandle,5.0f,5.0f,5.0f,1.0f);
        GLES20.glUniform1i(GLES.useLightHandle,1);
    }

    /**
     * 毎フレーム実行する処理
     * @return 遷移先のSuperSceneクラス
     */
    public abstract int onTick();

    /**
     * タッチイベントリスナー
     * @param x
     * @param y
     */
    public abstract void onTouch(int x, int y);

    /**
     * オーディオのセットアップ
     * @param activity
     * @param filePath
     * @return true = success, false = failure
     */
    protected boolean AudioSetup(Activity activity, String filePath){
        // インタンスを生成
        mediaPlayer = new MediaPlayer();

        try {
            // assetsから mp3 ファイルを読み込み
            AssetFileDescriptor afdescripter = context.getAssets().openFd(filePath);
            // MediaPlayerに読み込んだ音楽ファイルを指定
            mediaPlayer.setDataSource(afdescripter.getFileDescriptor(),
                    afdescripter.getStartOffset(),
                    afdescripter.getLength());
            // 音量調整を端末のボタンに任せる
            activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer.setLooping(true);//ループ設定
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * BGM再生開始
     * @param activity
     * @param filePath 読み込みたいファイルの名称、もしくはパス
     */
    protected void AudioPlay(Activity activity, String filePath) {
        if (mediaPlayer == null) {
            // audio ファイルを読出し
            AudioSetup(activity, filePath);
        }else{
            // 繰り返し再生する場合
            mediaPlayer.stop();
            mediaPlayer.reset();
            // リソースの解放
            mediaPlayer.release();
        }
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });

        // 終了を検知するリスナー
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                Log.d("debug","end of audio");
            }
        });
    }

    /**
     * 音楽停止
     */
    protected void AudioStop() {
        if(mediaPlayer != null) {
            mediaPlayer.stop();// 再生終了
            mediaPlayer.reset();// リセット
            mediaPlayer.release();// リソースの解放
            mediaPlayer = null;//インスタンスを削除
        }
    }

    /**
     * SEロードメソッド
     * @param context
     * @param soundRId 読み込みたいサウンドファイルの R ファイルのID
     * @return 読み込んだサウンドのID
     */
    public void LoadSound(Context context, int soundRId){
        //サウンドロードメソッド。returnはSoundID
        for(int i = 0; i < seId.length; i++){
            //配列の初期価値(0)であるなら
            if(seId[i] == 0){
                //空いているインデックスに要素を加える
                seId[i] = soundPool.load(context, soundRId, 1);//タッチ音をロード
                break;
            }
        }
        // load が終わったか確認する場合
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if(status == 0) {
                    Log.d("debug", "sampleId=" + sampleId);
                    Log.d("debug", "status=" + status);
                }else Log.e("error", "SoundPool Not Found.");
            }
        });
    }

    /**
     * SoundPoolクラスの初期化
     * @param num このゲームシーンに必要なSEの数
     */
    public void initSE(int num){
        seNum = num;
        seId = new int[seNum];

        //音の出し方設定を作る
        audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)//ゲームサウンドに設定
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();
        //SoundPoolのインスタンス
        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                // ストリーム数に応じて数値を変更
                .setMaxStreams(seNum)
                .build();
    }

    /**
     * テクスチャをロードする
     * @param filePath ロードするファイル名。assetsフォルダ内を検索する
     * @return ロードしたテクスチャのインスタンス
     */
    protected Texture LoadTexture(String filePath){
        Texture texture = null;
        // 2Dテクスチャ読み込み
        try{
            texture = Texture.createTextureFromAsset(filePath);
        }catch(Exception e){
            android.util.Log.e("debug", e.toString());
            for(StackTraceElement ste:e.getStackTrace()){
                android.util.Log.e("debug","    " + ste);
            }
        }
        return texture;
    }
}
