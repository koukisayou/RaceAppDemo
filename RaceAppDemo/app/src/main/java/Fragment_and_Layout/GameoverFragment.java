package Fragment_and_Layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sayoukouki.raceappdemo.R;

import Game_Logic.GameoverScene;
import Game_Logic.SuperScene;

/**
 * ゲームオーバー場面で必要なViewの管理を行うフラグメントクラス
 * Created by SayouKouki on 2017/06/20.
 */

public class GameoverFragment extends MyFragment {
    /**
     * Field
     */
    private GameoverScene gameScene;//現在ゲーム場面のインスタンス

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
        return inflater.inflate(R.layout.gameover_fragment, container, false);
    }

    /**
     * Viewが生成し終わった時に呼ばれるメソッド
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * gameSceneのセッタ
     * @param gameScene 使用中のゲームシーンインスタンス
     */
    @Override
    public void setGameScene(SuperScene gameScene) {
        try{
            this.gameScene = (GameoverScene)gameScene;
        }catch (ClassCastException e){

        }
    }
}
