package Game_Logic;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.example.sayoukouki.raceappdemo.GLRenderer;
import com.example.sayoukouki.raceappdemo.R;

import Maps.Map1;
import Maps.MapchipFields;
import Maps.SuperMap;
import OpenGLES20_furukawa.GLES;

import static java.lang.System.currentTimeMillis;

/**
 * ゲームプレイ画面での描画、音声再生、タッチイベントの処理を実行する
 * Created by SayouKouki
 */

public class PlayScene extends SuperScene implements MapchipFields {
    /**
     * Field
     */
    //コースのナンバー
    private static final int STAGE_1 = 1;
    private static final int STAGE_2 = 2;
    private static final int STAGE_3 = 3;
    private static final int STAGE_4 = 4;

    //レースの状態定数
    public int raceState = RACE_OPENING;//現在のレースの状態
    private static final int RACE_OPENING = 0;//オープニングムービー。タッチでスキップできる
    private static final int RACE_COUNTDOWN = 1;//レース前カウントダウン
    private static final int RACE_PLAY = 2;//レース中
    public static final int RACE_ENDING = 3;//エンディング

    //レースタイム計測変数
    private long currentTime = 0;//現在経過時間
    private long deltaTime = 0;//現在フレームの経過時間
    private long lastFrameTime = 0;//1F前の経過時間
    private int currentMinute = 0;//現在時間の分
    private int currentSeconds = 0;//現在時間の秒
    private int currentMillis = 0;//現在時間のミリ秒
    private int recordMinute = 0;//ゴール時の分
    private int recordSeconds = 0;//ゴール時の秒
    private int recordMillis = 0;//ゴール時のミリ秒

    //Button
    public boolean ab = false;//アクセルボタン
    public boolean bb = false;//バックボタン
    public boolean rb = false;//右ボタン
    public boolean lb = false;//左ボタン

    int debugCount = 0;
    //このシーンで使うBGMファイルの名称
    private String audioFileName = "play_bgm.mp3";
    private int se_moter_stream;
    private boolean playingSeMoter = false;
    private boolean openingTouch = false;
    private int nextSceneCount;//タッチ後、次のシーンへ行くまでのカウンタ
    public SuperMap map;//マップをアップキャストして格納

    public boolean debugRaceEnd = false;//レースをゴールするデバック変数

    /**
     * Constructor
     * @param cource_num 読み込むべきコースのナンバー
     * @param car_num 読み込むべき車のナンバー
     */
    public PlayScene(Activity activity, int cource_num, int car_num, GLSurfaceView glView){
        super(activity);

        nextSceneCount = 0;
        map = new Map1(glView);

        initSE(2);//SEの初期化
        LoadSound(activity, R.raw.se_moter1);
        AudioPlay(activity,audioFileName);
    }

    /**
     * Resume時の動作
     */
    @Override
    public void onResume(){

    }

    @Override
    public void onResume(Activity activity) {
        initSE(2);//SEの初期化
        LoadSound(activity, R.raw.se_moter1);
        AudioPlay(activity,audioFileName);
    }

    /**
     * Pause時の動作
     */
    @Override
    public void onPause() {
        soundPool.release();//解放
        AudioStop();//mediaPlayer停止処理
    }

    /**
     * SurfaceCreated時の動作
     */
    @Override
    public void onSurfaceCreated() {
        map.onSurfaceCreated();
    }

    @Override
    public void onSurfaceCreated(GLSurfaceView glView){

    }

    /**
     * 描画メソッド
     * プレイシーンではレースの状態で描画の仕方が変わるので、それに合わせて適切なメソッドを呼び出す
     */
    @Override
    public void onDraw() {
        GLES20.glClearColor(0.62745f, 0.84705f, 0.937254f, 1.0f);//背景色を空色に,rgb = 160,216,239
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|
                GLES20.GL_DEPTH_BUFFER_BIT);

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
        map.onDraw();
        if(raceState == RACE_PLAY){
            String min = Integer.toString(currentMinute);
            String sec = Integer.toString(currentSeconds);
            String mil = Integer.toString(currentMillis);
            ft.Draw(min + sec + mil,300,30,g);
        }
    }

    @Override
    public void onDraw(GLRenderer renderer) {
        GLES20.glClearColor(0.62745f, 0.84705f, 0.937254f, 1.0f);//背景色を空色に,rgb = 160,216,239
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|
                GLES20.GL_DEPTH_BUFFER_BIT);

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

        if(raceState == RACE_OPENING){
            map.onDrawBirdsView(renderer);//俯瞰視点でコースを一望する
            ft.Draw("CoursePreview",(int)(width * 0.1),(int)(height * 0.1),2,2,0.11372f,0.19215f,0.33725f,1.0f,g);
            ft.Draw("Touch",(int)(width * 0.7),(int)(height * 0.7),2,2,0.8f,0.0f,0.0392f,1.0f,g);
            ft.Draw("RaceStart!",(int)(width * 0.7),(int)(height * 0.8),2,2,0.8f,0.0f,0.0392f,1.0f,g);
        }else{
            map.onDraw(renderer);
        }

        //レース中
        if(raceState == RACE_PLAY){
            //タイムの描画
            String min = Integer.toString(currentMinute);
            String sec = Integer.toString(currentSeconds);
            String mil = Integer.toString(currentMillis);
            ft.Draw(min + "-" + sec + "-" + mil,width - 700,50,3,3,0.8f,0.0f,0.0392f,1.0f,g);
            //タイムが1秒満たないなら「START!」の描画
            if(currentSeconds < 1 && currentMinute == 0){
                ft.Draw("START!",width / 2, height / 4,3,3,0.8f,0.0f,0.0392f,1.0f,g);
            }
        }
        //カウントダウン
        else if(raceState == RACE_COUNTDOWN){
            ft.Draw(Integer.toString(3 - currentSeconds), width / 2 - 20, height / 4,5,5,0.8f,0.0f,0.0392f,1.0f,g);
        }
        //ゴール
        else if(raceState == RACE_ENDING){
            ft.Draw("GOAL!",width / 2, height / 4,3,3,0.8f,0.0f,0.0392f,1.0f,g);
        }
    }

    /**
     * 毎フレーム実行する処理
     * @return レース終了時に true
     */
    @Override
    public int onTick() {
        switch(raceState){
            case RACE_OPENING:
                if(openingTouch) {
                    lastFrameTime = currentTimeMillis();//時刻の初期化
                    raceState = RACE_COUNTDOWN;
                }
                break;
            case RACE_COUNTDOWN:
                UpdateTime();
                if(currentSeconds >= 3){
                    Log.d("RaceState", "RaceStart!");
                    currentTime = 0;
                    activity.fragment.AllViewVisible();
                    raceState = RACE_PLAY;
                }
                break;
            case RACE_PLAY:
                UpdateTime();
                if(map.onTick()){
                    activity.fragment.AllViewGone();
                    raceState = RACE_ENDING;
                    //ゴールタイムを記録
                    recordMinute = currentMinute;
                    recordSeconds =  currentSeconds;
                    recordMillis = currentMillis;
                    RecordDB();
                }
                if(debugRaceEnd){
                    activity.fragment.AllViewGone();
                    raceState = RACE_ENDING;
                    //ゴールタイムを記録
                    recordMinute = currentMinute;
                    recordSeconds =  currentSeconds;
                    recordMillis = currentMillis;
                    RecordDB();
                }
                break;
            case RACE_ENDING:
                nextSceneCount++;//次のシーンに行くカウンタ

                //カウンタが満たされたらファイル解放処理をして次のシーンのIDを返す
                if(nextSceneCount > 30){
                    soundPool.release();//解放
                    AudioStop();//mediaPlayer停止処理
                    return GO_NEXTMAP_SCENE;
                }
                break;
        }
        onButtonClick();//ボタンの入力チェック

        return SceneNumConstant.NOT_CHANGE;
    }

    /**
     * タッチイベントリスナー
     * @param x
     * @param y
     */
    @Override
    public void onTouch(int x, int y) {
        if(x > -1){
            openingTouch = true;//opening状態を終える
        }
    }

    /**
     * PlayFragmentクラスの ButtonClickEventListener から命令を受け、
     * ボタンごとのロジックを動作させる
     */
    private void onButtonClick(){
        //Accele
        if(ab){
            map.car.touchAccele = true;
            if(!(playingSeMoter)){
                se_moter_stream = soundPool.play(seId[0], 1.0f, 1.0f, 0, -1, 1.0f);//モーター音の再生
                playingSeMoter = true;
            }
        }else if(!ab){
            map.car.touchAccele = false;
            if(playingSeMoter){
                soundPool.stop(se_moter_stream);//モーター音の停止
                playingSeMoter = false;
            }
        }
        //Brake
        if(bb){
            map.car.touchBrake = true;
        }else if(!bb){
            map.car.touchBrake = false;
        }
        //Right
        if(rb){
            map.car.touchRight = true;
        }else if(!rb){
            map.car.touchRight = false;
        }
        //Left
        if(lb){
            map.car.touchLeft = true;
        }else if(!lb){
            map.car.touchLeft = false;
        }
    }

    /**
     * 現在経過時間を更新する
     */
    private void UpdateTime(){
        deltaTime = currentTimeMillis() - lastFrameTime;//前回フレームからの経過時間を出す
        currentTime += deltaTime;//それを追加
        lastFrameTime = currentTimeMillis();//現在フレームの時間に更新

        currentMinute = (int)currentTime / 60000;//現在時間の分
        currentSeconds = (int)currentTime % 60000 / 1000;//現在時間の秒
        currentMillis = (int)currentTime % 60000 % 1000;//現在時間のミリ秒

        //Log.i("currentTime", currentMinute + " : " + currentSeconds + " : " + currentMillis);//Debug message
    }

    /**
     * 今回のレースタイムをSQLiteに格納する
     */
    private void RecordDB(){
        try{
            //読み取りDBを作成
            SQLiteDatabase readdb = activity.dbHelper.getReadableDatabase();
            Cursor cursor = readdb.rawQuery("select * from record", null);
            cursor.moveToFirst();
            readdb.close();
            //データベースにレコードを追加
            SQLiteDatabase writedb = activity.dbHelper.getWritableDatabase();

            //データベースにレコードが無ければ追加して終了
            if(cursor.getCount() == 0){
                String sql = "insert into record values(" +
                        Integer.toString(currentMinute) + "," +
                        Integer.toString(currentSeconds) + "," +
                        Integer.toString(currentMillis) + "," +
                        Integer.toString(0) + "," +
                        Integer.toString(1) +
                        ")";
                writedb.execSQL(sql);
                cursor.close();
                writedb.close();
                return;
            }

            //SELECT文で得た数値を格納する配列を宣言
            int indexNum = cursor.getCount();//レコード数
            int arrayMinute[] = new int[indexNum];
            int arraySeconds[] = new int[indexNum];
            int arrayMillis[] = new int[indexNum];
            int arrayMachine[] = new int[indexNum];
            int arrayId[] = new int[indexNum];
            int dbRecordSize = 20;//データベースの最大登録件数

            //全要素を配列に格納
            for(int i = 0; i < cursor.getCount(); i++){
                arrayMinute[i] = cursor.getInt(cursor.getColumnIndex("minute"));
                arraySeconds[i] = cursor.getInt(cursor.getColumnIndex("seconds"));
                arrayMillis[i] = cursor.getInt(cursor.getColumnIndex("millis"));
                arrayMachine[i] = cursor.getInt(cursor.getColumnIndex("machine_id"));
                arrayId[i] = cursor.getInt(cursor.getColumnIndex("id"));
                cursor.moveToNext();
            }

            writedb.execSQL("delete from record");//データを削除して初期化

            for(int i = 0; i < cursor.getCount() + 1 && i < dbRecordSize; i++){
                if((arrayMinute[i] * 60000) + (arraySeconds[i] * 1000) + arrayMillis[i] > currentTime){
                    String sql = "insert into record values(" +
                            Integer.toString(currentMinute) + "," +
                            Integer.toString(currentSeconds) + "," +
                            Integer.toString(currentMillis) + "," +
                            Integer.toString(0) + "," +
                            Integer.toString(i + 1) +
                            ")";
                    writedb.execSQL(sql);
                    for(;i < cursor.getCount() && i < dbRecordSize;i++){
                        sql = "insert into record values(" +
                                Integer.toString(arrayMinute[i]) + "," +
                                Integer.toString(arraySeconds[i]) + "," +
                                Integer.toString(arrayMillis[i]) + "," +
                                Integer.toString(arrayMachine[i]) + "," +
                                Integer.toString(i + 2) +
                                ")";
                        writedb.execSQL(sql);
                        Log.i("loopCounter", Integer.toString(i));
                    }
                    break;
                }
                String sql = "insert into record values(" +
                        Integer.toString(arrayMinute[i]) + "," +
                        Integer.toString(arraySeconds[i]) + "," +
                        Integer.toString(arrayMillis[i]) + "," +
                        Integer.toString(arrayMachine[i]) + "," +
                        Integer.toString(i + 1) +
                        ")";
                writedb.execSQL(sql);
            }
            cursor.close();
            readdb.close();
            writedb.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        SQLiteDatabase readdb = activity.dbHelper.getReadableDatabase();
        Cursor cursor = readdb.rawQuery("select * from record", null);
        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++){
            Log.i("id", Integer.toString(cursor.getInt(cursor.getColumnIndex("id"))));
            Log.i("machine_id", Integer.toString(cursor.getInt(cursor.getColumnIndex("machine_id"))));
            Log.i("minute", Integer.toString(cursor.getInt(cursor.getColumnIndex("minute"))));
            Log.i("seconds", Integer.toString(cursor.getInt(cursor.getColumnIndex("seconds"))));
            Log.i("millis", Integer.toString(cursor.getInt(cursor.getColumnIndex("millis"))));
            cursor.moveToNext();
        }
        cursor.close();
        readdb.close();
    }

    /**
     * 現在時間のゲッタ
     * @return 現在時間
     */
    public int getCurrentTime(){
        return (int)currentTime;
    }
}
