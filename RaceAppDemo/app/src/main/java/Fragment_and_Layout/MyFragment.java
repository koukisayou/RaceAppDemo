package Fragment_and_Layout;

import android.support.v4.app.Fragment;

import Game_Logic.SuperScene;

/**
 * ゲーム場面ごとに作成されるFragmentのスーパークラス
 * Created by SayouKouki on 2017/07/07.
 */

public abstract class MyFragment extends Fragment {
    /**
     * ゲームシーンをアップキャストして格納する。
     * @param gameScene 使用中のゲームシーンインスタンス
     */
    public abstract void setGameScene(SuperScene gameScene);

    /**
     * 全てのViewをVisibleにする
     */
    public void AllViewVisible(){

    }

    /**
     * 全てのViewをGoneにする
     */
    public void AllViewGone(){

    }
}
