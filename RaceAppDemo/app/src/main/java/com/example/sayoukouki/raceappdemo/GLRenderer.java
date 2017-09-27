package com.example.sayoukouki.raceappdemo;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import Game_Logic.GameoverScene;
import Game_Logic.MachineScene;
import Game_Logic.NextMapScene;
import Game_Logic.PlayScene;
import Game_Logic.SceneNumConstant;
import Game_Logic.SuperScene;
import Game_Logic.TitleScene;
import OpenGLES20_furukawa.GLES;
import OpenGLES20_furukawa.Graphics;

import static OpenGLES20_furukawa.GLES.context;

/**
 * 基本的にゲームの動作の全てを管理するクラス。
 *
 * 機能一覧
 *      ・OpenGL ES 2.0 を使用し描画を行う
 *      ・タッチリスナーにより、タッチイベントを取得する
 *      ・場面ごとのゲームの動作を行う
 */
public class GLRenderer implements GLSurfaceView.Renderer, View.OnTouchListener, SceneNumConstant {
    /**
     * field
     */
    private static Graphics g;
    private int touchX, touchY;//タッチイベント発生時の座標
    private int nextGameScene = 0;//次に向かうべきゲーム場面
    private GLSurfaceView glView;//@TODO テクスチャ読み込みのために用意
    private SuperScene gameScene;//ゲーム場面

    /**
     * Constructor
     */
    public GLRenderer(Activity activity, GLSurfaceView glView) {
        context = activity;
        this.glView = glView;
        gameScene = new TitleScene(activity);//起動時のゲーム場面の生成
    }

    /**
     * サーフェイス生成時に呼ばれる
     * GLESクラスの初期化とOpenGLES関連の初期化、ゲーム場面ごとの動作を行う
     * @param gl10
     * @param eglConfig
     */
    @Override
    public void onSurfaceCreated(GL10 gl10,EGLConfig eglConfig) {
        GLES.makeProgram();//プログラムの生成

        //デプステストと光源の有効化
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glUniform1i(GLES.useLightHandle,1);

        //光源色の指定
        GLES20.glUniform4f(GLES.lightAmbientHandle,0.2f,0.2f,0.2f,1.0f);
        GLES20.glUniform4f(GLES.lightDiffuseHandle,0.7f,0.7f,0.7f,1.0f);
        GLES20.glUniform4f(GLES.lightSpecularHandle,0.9f,0.9f,0.9f,1.0f);

        //カリング指定
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glFrontFace(GLES20.GL_CCW);//反時計回りを表面とする
        GLES20.glCullFace(GLES20.GL_BACK);//ポリゴンの裏面を描画しない

        gameScene.onSurfaceCreated(glView);//ゲームシーンの同名メソッドを実行
    }

    /**
     * 画面サイズの変更時に呼ばれる
     * @param gl10
     * @param w 画面サイズ横幅
     * @param h 画面サイズ縦幅
     */
    @Override
    public void onSurfaceChanged(GL10 gl10,int w,int h) {
        //ビューポート変換
        GLES20.glViewport(0,0,w,h);
        g = new Graphics(w, h);
        gameScene.onSurfaceChanged(w,h,g);
    }

    /**
     * onResume
     */
    public void onResume(Activity activity){
        gameScene.onResume(activity);
    }

    /**
     * onPause
     */
    public void onPause(){
        gameScene.onPause();
    }

    /**
     * 描画スレッドで呼び出される描画メソッド。
     */
    @Override
    public synchronized void onDrawFrame(GL10 gl10) {
        if(gameScene instanceof PlayScene){
            gameScene.onDraw(this);//PlaySceneである時のみ、このクラスのインスタンスが必要
        }else {
            gameScene.onDraw();
        }
    }

    /**
     * 場面ごとに必要な計算を行うメソッド。
     * 毎フレームしなければならないプログラム処理を書く場所
     * @param activity
     * @return 次回のゲームシーン
     */
    public synchronized int onTick(MainActivity activity){
        nextGameScene = gameScene.onTick();//現在場面の処理を行い、場面転換の指示を受け取る

        //場面を変更する場合に実行
        if(nextGameScene != NOT_CHANGE) {
            switch (nextGameScene) {
                case GO_TITLE_SCENE:
                    gameScene = new TitleScene(activity, glView);
                    break;
                case GO_MACHINE_CHOISE_SCENE:
                    gameScene = new MachineScene(activity, glView);
                    break;
                case GO_PLAY_SCENE:
                    gameScene = new PlayScene(activity, 0, 0, glView);
                    break;
                case GO_NEXTMAP_SCENE:
                    if(gameScene instanceof PlayScene){
                        gameScene = new NextMapScene(activity,((PlayScene) gameScene).getCurrentTime());
                    }else{
                        gameScene = new NextMapScene(activity);
                    }
                    break;
                case GO_GAMEOVER_SCENE:
                    gameScene = new GameoverScene(activity, glView);
                    break;
            }
            return nextGameScene;
        }
        return 0;
    }

    /**
     * タッチイベントのリスナー
     * @param v
     * @param event
     * @return
     */
    public boolean onTouch(View v, MotionEvent event){
        if(g == null) return false;
        int action = event.getAction();
        switch(action){
            case MotionEvent.ACTION_DOWN://タッチ時
                touchX = (int)event.getX();
                touchY = (int)event.getY();
                gameScene.onTouch(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP://タッチをやめた時
            case MotionEvent.ACTION_CANCEL:
                //座標を -1 に初期化
                touchX = -1;
                touchY = -1;
                break;
        }
        return true;
    }

    /**
     * ゲームシーンのインスタンスを渡す
     * 主にFragmentクラスに渡すために使う
     * @return gameScene
     */
    public SuperScene getGameScene(){
        return gameScene;
    }

    public void setGlView(GLSurfaceView glView){
        this.glView = glView;
    }

}