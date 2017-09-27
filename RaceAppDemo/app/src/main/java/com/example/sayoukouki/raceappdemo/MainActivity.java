package com.example.sayoukouki.raceappdemo;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import Fragment_and_Layout.GameoverFragment;
import Fragment_and_Layout.MachineFragment;
import Fragment_and_Layout.MyFragment;
import Fragment_and_Layout.NextMapFragment;
import Fragment_and_Layout.PlayFragment;
import Fragment_and_Layout.TitleFragment;
import Game_Logic.SceneNumConstant;

/**
 * メインアクティビティ
 * 本ゲームは一貫してこのアクティビティ内で動作を行う
 *
 * onCreate にて、描画を行うレンダラースレッドを開始し、
 * onResume にて、座標計算やゲーム内の時間計算、場面転換などを行うスレッドを開始する。
 *
 * ゲーム内の動作は基本的に GLRenderer クラスに記述しているため、アンドロイドのライフサイクル上のイベントをキャッチし、
 * 適切な GLRenderer のメソッドを呼び出す、橋渡しを主に行う。
 *
 * Created by Kouki Sayou.
 */

public class MainActivity extends AppCompatActivity implements Runnable, SceneNumConstant{
    /**
     * field
     */
    public GLSurfaceView glView;//OpenGLESのView
    private GLRenderer renderer;//アップキャストして各レンダラーを保持
    private Thread thread;//ロジックスレッドとして利用
    public MyFragment fragment;//アップキャストしてFragmentを格納
    private int nextGameScene = 0;//renderer#onTick() のリターンを保存
    public CreateProductHelper dbHelper;//sqliteを操作するために使用

    /**
     * アプリ起動時の動作
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //フルスクリーン指定
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        //レンダラーを生成
        glView = (GLSurfaceView)findViewById(R.id.glSurfaceViewId);
        renderer = new GLRenderer(this, glView);
        glView.setEGLContextClientVersion(2);
        glView.setEGLConfigChooser(true);
        glView.setRenderer(renderer);
        glView.setOnTouchListener(renderer);
        renderer.setGlView(glView);

        //Create fragment
        ChangeFragment(GO_TITLE_SCENE);

        //データベース作成
        dbHelper = new CreateProductHelper(this);
    }

    /**
     * ライフサイクルのレジューム時に呼び出される
     */
    @Override
    public void onResume(){
        super.onResume();
        renderer.onResume(this);
        thread = new Thread(this);
        thread.start();
    }

    /**
     * ライフサイクルのポーズ時に呼び出される
     */
    @Override
    public void onPause(){
        super.onPause();
        renderer.onPause();
        thread = null;
    }

    /**
     * Run method by Runnable interface.
     * ゲームロジックを回すスレッドとして利用
     */
    public void run(){
        while(thread != null){
            nextGameScene = renderer.onTick(this);

            //場面転換が行われた際にフラグメントを変更
            if(nextGameScene != 0){
                ChangeFragment(nextGameScene);
            }
            try{
                thread.sleep(50);
            }catch(Exception e){

            }
        }
    }

    /**
     * Fragment の変更を行う
     * @param goSceneNum 変更先の画面ID
     */
    private void ChangeFragment(int goSceneNum){
        switch(goSceneNum){
            case GO_TITLE_SCENE:
                fragment = new TitleFragment();
                break;
            case GO_MACHINE_CHOISE_SCENE:
                fragment = new MachineFragment();
                break;
            case GO_PLAY_SCENE:
                fragment = new PlayFragment();
                break;
            case GO_NEXTMAP_SCENE:
                fragment = new NextMapFragment();
                break;
            case GO_GAMEOVER_SCENE:
                fragment = new GameoverFragment();
                break;
            case NOT_CHANGE:
                return;//処理の終了
        }
        //Fragmentの入れ替え
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();

        fragment.setGameScene(renderer.getGameScene());//Fragmentクラスにゲームシーンのインスタンスを渡す
    }
}
