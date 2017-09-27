package Fragment_and_Layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sayoukouki.raceappdemo.R;

import Game_Logic.SuperScene;

/**
 * タイトル場面で必要なViewの管理を行うフラグメントクラス
 * Created by SayouKouki on 2017/06/19.
 */

public class TitleFragment extends MyFragment {
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
        // 先ほどのレイアウトをここでViewとして作成します
        return inflater.inflate(R.layout.title_fragment, container, false);
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

    }
}
