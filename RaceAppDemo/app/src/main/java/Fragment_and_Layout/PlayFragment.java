package Fragment_and_Layout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.sayoukouki.raceappdemo.R;

import Game_Logic.PlayScene;
import Game_Logic.SuperScene;

/**
 * プレイ場面で必要なViewの管理を行うフラグメントクラス
 * Created by SayouKouki on 2017/06/19.
 */

public class PlayFragment extends MyFragment {
    /**
     * Field
     */
    private PlayScene gameScene;//ボタンイベント時に使用する
    //Button
    private ImageButton acceleButton;//アクセルボタン
    private ImageButton backButton;//ブレーキ・バックボタン
    private ImageButton rightButton;//右折ボタン
    private ImageButton leftButton;//左折ボタン

    private Button debugNextButton;//@TODO Debug mode

    /**
     * Fragmentで表示するViewを作成するメソッド
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.play_fragment, container, false);
    }

    /**
     * Viewが生成し終わった時に呼ばれるメソッド
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        acceleButton = (ImageButton)view.findViewById(R.id.AcceleButton);
        acceleButton.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event){
                //押したときの動作
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    gameScene.ab = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    gameScene.ab = false;
                }
                return false; //trueにすると他のリスナーが呼ばれない
            }
        });

        backButton = (ImageButton)view.findViewById(R.id.BackButton);
        backButton.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event){
                //押したときの動作
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    gameScene.bb = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    gameScene.bb = false;
                }
                return false; //trueにすると他のリスナーが呼ばれない
            }
        });

        rightButton = (ImageButton)view.findViewById(R.id.RightButton);
        rightButton.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event){
                //押したときの動作
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    gameScene.rb = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    gameScene.rb = false;
                }
                return false; //trueにすると他のリスナーが呼ばれない
            }
        });

        leftButton = (ImageButton)view.findViewById(R.id.LeftButton);
        leftButton.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event){
                //押したときの動作
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    gameScene.lb = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    gameScene.lb = false;
                }
                return false; //trueにすると他のリスナーが呼ばれない
            }
        });

        /*debugNextButton = (Button)view.findViewById(R.id.debug_next_button);
        debugNextButton.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event){
                //押したときの動作
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    gameScene.debugRaceEnd = true;
                }
                return false; //trueにすると他のリスナーが呼ばれない
            }
        });*/

        acceleButton.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);
        rightButton.setVisibility(View.GONE);
        leftButton.setVisibility(View.GONE);
        //AllViewGone();//全て非表示にする
    }

    /**
     * gameSceneのセッタ
     * @param gameScene 使用中のゲームシーンインスタンス
     */
    @Override
    public void setGameScene(SuperScene gameScene) {
        try{
            this.gameScene = (PlayScene)gameScene;
        }catch (ClassCastException e){

        }
    }

    /**
     * 全てのViewをVisibleにする
     */
    @Override
    public void AllViewVisible(){
        // 別スレ生成 -> 開始
        final Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {
            @Override
            public void run() {
                acceleButton.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.VISIBLE);
                rightButton.setVisibility(View.VISIBLE);
                leftButton.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 全てのViewをGoneにする
     */
    @Override
    public void AllViewGone(){
        // 別スレ生成 -> 開始
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                acceleButton.setVisibility(View.GONE);
                backButton.setVisibility(View.GONE);
                rightButton.setVisibility(View.GONE);
                leftButton.setVisibility(View.GONE);
            }
        });
    }
}