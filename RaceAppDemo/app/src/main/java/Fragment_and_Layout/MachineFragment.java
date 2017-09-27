package Fragment_and_Layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.sayoukouki.raceappdemo.R;

import Game_Logic.MachineScene;
import Game_Logic.SuperScene;

/**
 * マシン選択場面で必要なViewの管理を行うフラグメントクラス
 * Created by SayouKouki on 2017/06/20.
 */

public class MachineFragment extends MyFragment {
    /**
     * Field
     */
    private MachineScene gameScene;//現在ゲーム場面のインスタンス

    //android view.
    private ImageButton rightButton;//右ボタン
    private ImageButton leftButton;//左ボタン
    private Button nextButton;//決定ボタン
    //private TextView machineName;//マシン名称

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
        return inflater.inflate(R.layout.machine_fragment, container, false);
    }

    /**
     * Viewが生成し終わった時に呼ばれるメソッド
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // RightButton.
        /*rightButton = (ImageButton)view.findViewById(R.id.rightButton);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameScene.rb = gameScene.buttonClick = true;
            }
        });

        // LeftButton.
        leftButton = (ImageButton)view.findViewById(R.id.leftButton);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameScene.lb = gameScene.buttonClick = true;
            }
        });*/


        // NextSceneButton.
        nextButton = (Button)view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameScene.nb = gameScene.buttonClick = true;
            }
        });

        // MachineNameText.
        /*machineName = (TextView)view.findViewById(R.id.machine_name);
        machineName.setText("MachineNameOnDraw!");*/
    }

    /**
     * gameSceneのセッタ
     * @param gameScene 使用中のゲームシーンインスタンス
     */
    @Override
    public void setGameScene(SuperScene gameScene){
        try{
            this.gameScene = (MachineScene)gameScene;
        }catch (ClassCastException e){

        }
    }
}
