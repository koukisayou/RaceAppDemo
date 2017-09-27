package Fragment_and_Layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.sayoukouki.raceappdemo.R;

import Game_Logic.NextMapScene;
import Game_Logic.SuperScene;

/**
 * 次コース場面で必要なViewの管理を行うフラグメントクラス
 * Created by SayouKouki on 2017/06/20.
 */

public class NextMapFragment extends MyFragment {
    /**
     * Field
     */
    private NextMapScene gameScene;//現在ゲーム場面のインスタンス

    //android view.
    private Button nextButton;//決定ボタン
    //private TextView courseName;//次のコースの名称

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
        return inflater.inflate(R.layout.nextmap_fragment, container, false);
    }

    /**
     * Viewが生成し終わった時に呼ばれるメソッド
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // NextSceneButton.
        nextButton = (Button)view.findViewById(R.id.next_map_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameScene.nb = true;
            }
        });

        //CourseNameText.
        //courseName = (TextView)view.findViewById(R.id.course_name);
        //courseName.setText("NextCourseNameOnDraw!");
    }

    /**
     * gameSceneのセッタ
     * @param gameScene 使用中のゲームシーンインスタンス
     */
    @Override
    public void setGameScene(SuperScene gameScene){
        try{
            this.gameScene = (NextMapScene)gameScene;
        }catch (ClassCastException e){

        }
    }
}
