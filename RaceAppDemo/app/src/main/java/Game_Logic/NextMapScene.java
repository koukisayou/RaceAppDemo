package Game_Logic;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.example.sayoukouki.raceappdemo.GLRenderer;
import com.example.sayoukouki.raceappdemo.MainActivity;
import com.example.sayoukouki.raceappdemo.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import OpenGLES20_furukawa.GLES;
import OpenGLES20_furukawa.Graphics;

/**
 * 次のコースや現在順位表示部分
 *
 * 対戦相手を実装していないので、順位部分はタイムレコードの表示としている
 * コースを複数実装していないので、次のコースマップ画像部分は今回タイムを表示する
 *
 * Created by SayouKouki
 */

public class NextMapScene extends SuperScene {
    /**
     * Field
     */
    private String audioFileName = "menu.mp3";//音楽ファイル名

    public boolean nb = false;//次のシーンへのボタン
    private static float basePointx = 0;//背景描画の際の基準点x
    private static float basePointy = 0;//背景描画の際の基準点y
    private int[][] dbRecord;//データベースから持ってきたレコード
    private int newRecordMinute;//今回タイム分
    private int newRecordSeconds;//今回タイム秒
    private int newRecordMillis;//今回タイムミリ秒

    //レコードを配列に格納する時のID定数
    private static final int DB_ID = 0;
    private static final int DB_MACHINE_ID = 1;
    private static final int DB_MINUTE = 2;
    private static final int DB_SECONDS = 3;
    private static final int DB_MILLIS = 4;

    /**
     * Constructor
     */
    public NextMapScene(MainActivity activity){
        super(activity);

        //SQLiteデータベースを操作
        //データベースの記録を読み込む
        SQLiteDatabase readdb = activity.dbHelper.getReadableDatabase();
        Cursor cursor = readdb.rawQuery("select * from record where id > 0 and id <= 5", null);
        cursor.moveToFirst();
        dbRecord = new int[5][5];
        for(int i = 0; i < dbRecord.length; i++){
            dbRecord[i][DB_ID] = cursor.getInt(cursor.getColumnIndex("id"));
            dbRecord[i][DB_MACHINE_ID] = cursor.getInt(cursor.getColumnIndex("machine_id"));
            dbRecord[i][DB_MINUTE] = cursor.getInt(cursor.getColumnIndex("minute"));
            dbRecord[i][DB_SECONDS] = cursor.getInt(cursor.getColumnIndex("seconds"));
            dbRecord[i][DB_MILLIS] = cursor.getInt(cursor.getColumnIndex("millis"));
            cursor.moveToNext();
        }
        cursor.close();
        readdb.close();//DB操作終了
    }

    public NextMapScene(MainActivity activity, int currentRecord){
        super(activity);
        newRecordMinute = currentRecord / 60000;//今回タイムの分
        newRecordSeconds = currentRecord % 60000 / 1000;//今回タイムの秒
        newRecordMillis = currentRecord % 60000 % 1000;//今回タイムのミリ秒

        //SQLiteデータベースを操作
        //データベースの記録を読み込む
        SQLiteDatabase readdb = activity.dbHelper.getReadableDatabase();
        Cursor cursor = readdb.rawQuery("select * from record where id > 0 and id <= 5", null);
        cursor.moveToFirst();
        dbRecord = new int[5][5];
        for(int i = 0; i < dbRecord.length; i++){
            dbRecord[i][DB_ID] = cursor.getInt(cursor.getColumnIndex("id"));
            dbRecord[i][DB_MACHINE_ID] = cursor.getInt(cursor.getColumnIndex("machine_id"));
            dbRecord[i][DB_MINUTE] = cursor.getInt(cursor.getColumnIndex("minute"));
            dbRecord[i][DB_SECONDS] = cursor.getInt(cursor.getColumnIndex("seconds"));
            dbRecord[i][DB_MILLIS] = cursor.getInt(cursor.getColumnIndex("millis"));
            cursor.moveToNext();
        }
        cursor.close();
        readdb.close();//DB操作終了

        initSE(2);//SEの初期化
        LoadSound(activity, R.raw.click_sample1);//クリック音読み込み
        AudioPlay(activity,audioFileName);//BGM再生
    }

    /**
     * 毎フレーム実行する処理
     * @return
     */
    @Override
    public int onTick() {
        if(nb){
            soundPool.release();//SEファイルの解放
            AudioStop();//BGMファイルの解放
            return GO_GAMEOVER_SCENE;
        }
        return NOT_CHANGE;
    }

    /**
     * タッチイベントリスナー
     * @param x
     * @param y
     */
    @Override
    public void onTouch(int x, int y) {

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
        LoadSound(activity, R.raw.click_sample1);//クリック音読み込み
        AudioPlay(activity,audioFileName);//BGM再生
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
     * サーフェイス作成時
     */
    @Override
    public void onSurfaceCreated() {

    }

    @Override
    public void onSurfaceCreated(GLSurfaceView glView){

    }

    /**
     * 画面サイズ変更時
     * @param w
     * @param h
     * @return
     */
    @Override
    public Graphics onSurfaceChanged(int w, int h) {
        return null;
    }

    /**
     * 描画を行う。
     * 描画対象は
     *      背景描画
     *      レコードの描画
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

        //背景のチェック柄の描画
        int backSize = 300;//背景の一マスあたりのサイズ
        //int loopCounter = 0;//@TODO debug
        float angle = 45.0f;//背景の流れる方向 @TODO 45の倍数でないと表示が崩れる
        int slideSpeed = 4;//背景が流れる速さ

        //基準点を移動
        basePointx += slideSpeed * (float)Math.cos(angle * Math.PI / 180);
        basePointy += slideSpeed * (float)Math.sin(angle * Math.PI / 180);
        if(basePointx <= 0)basePointx += backSize;
        else if(basePointx >= backSize) basePointx -= backSize;
        if(basePointy <= 0)basePointy += backSize;
        else if(basePointy >= backSize) basePointy -= backSize;

        GLES20.glClearColor(1.0f,1.0f,1.0f,1.0f);
        g.init();
        Matrix.setIdentityM(GLES.mMatrix,0);
        GLES.updateMatrix();
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        //背景、黒と青のチェック柄
        for(int i = 0; i * backSize - backSize < height + backSize; i++){
            for(int j = 0; j * backSize - backSize < width + backSize; j++){
                if(i % 2 == 0){
                    if(j % 2 == 0){
                        GLES20.glUniform4f(GLES.colorHandle,0.0f,0.4039f,0.5882f,1.0f);//青色
                    }else {
                        GLES20.glUniform4f(GLES.colorHandle, 0.0f, 0.0f, 0.0f, 1.0f);//黒色
                    }
                }else{
                    if(j % 2 == 0){
                        GLES20.glUniform4f(GLES.colorHandle, 0.0f, 0.0f, 0.0f, 1.0f);//黒色
                    }else {
                        GLES20.glUniform4f(GLES.colorHandle,0.0f,0.4039f,0.5882f,1.0f);//青色
                    }
                }
                GLES20.glVertexAttribPointer(GLES.positionHandle, 3, GLES20.GL_FLOAT,false,0,makeVertexBuffer(j * backSize + (int)basePointx - backSize,i * backSize + (int)basePointy - backSize,backSize,backSize));
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
                //loopCounter++;//@TODO debug
            }
        }

        //画面右側、レコード表示白枠
        //配列はそれぞれの表示座標
        int[] recordX = {
                (int)(width * 0.4),
                (int)(width * 0.45),
                (int)(width * 0.5),
                (int)(width * 0.55),
                (int)(width * 0.6),
        };
        int[] recordY = {
                (int)(height * 0.02),
                (int)(height * 0.216),
                (int)(height * 0.412),
                (int)(height * 0.608),
                (int)(height * 0.804),
        };
        int recordHeight = (int)(height * 0.176);//レコード枠の高さ
        //画面のheightを1とした時に隙間 = 0.02, ブロック = 0.176の比率となる
        GLES20.glUniform4f(GLES.colorHandle,1.0f,1.0f,1.0f,1.0f);//白色
        //白枠を描画する
        for(int i = 0; i < recordX.length; i++){
            GLES20.glVertexAttribPointer(GLES.positionHandle, 3, GLES20.GL_FLOAT,false,0,makeVertexBuffer(recordX[i], recordY[i],width,recordHeight));
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
        }

        int border = height / 100;//白色線の大きさ
        recordHeight = recordHeight - border * 2;//表示部分の高さをボーダー分引く

        //レコード描画
        for(int i = 0; i < recordX.length; i++){
            //座標をボーダー分更新
            recordX[i] = recordX[i] + border;
            recordY[i] = recordY[i] + border;
            //順位ごとの背景色指定
            switch(i){
                case 0:
                    GLES20.glUniform4f(GLES.colorHandle,0.2f,0.6f,1.0f,1.0f);
                    break;
                case 1:
                    GLES20.glUniform4f(GLES.colorHandle,0.4f,0.6980392f,1.0f,1.0f);
                    break;
                case 2:
                    GLES20.glUniform4f(GLES.colorHandle,0.6f,0.8f,1.0f,1.0f);
                    break;
                case 3:
                    GLES20.glUniform4f(GLES.colorHandle,0.0f,.298039f,0.6f,1.0f);
                    break;
                case 4:
                    GLES20.glUniform4f(GLES.colorHandle,0.0f,0.4039f,0.5882f,1.0f);
                    break;
            }
            //背景描画
            GLES20.glVertexAttribPointer(GLES.positionHandle, 3, GLES20.GL_FLOAT,false,0,makeVertexBuffer(recordX[i], recordY[i],width,recordHeight));
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
        }
        for(int i = 0; i < recordX.length; i++){
            String rank = new String();
            //順位文字描画
            switch(i){
                case 0:
                    rank = new String("1st");
                    ft.Draw(rank,recordX[i] + border,recordY[i] + border * 4,3,3,0.9058f,0.7411f,0.17254f,1.0f,g);
                    break;
                case 1:
                    rank = new String("2nd");
                    ft.Draw(rank,recordX[i] + border,recordY[i] + border * 4,3,3,0.85f,0.85f,0.85f,1.0f,g);
                    break;
                case 2:
                    rank = new String("3rd");
                    ft.Draw(rank,recordX[i] + border,recordY[i] + border * 4,3,3,0.76862f,0.4392f,0.1333f,1.0f,g);
                    break;
                case 3:
                    rank = new String("4th");
                    ft.Draw(rank,recordX[i] + border,recordY[i] + border * 4,3,3,g);
                    break;
                case 4:
                    rank = new String("5th");
                    ft.Draw(rank,recordX[i] + border,recordY[i] + border * 4,3,3,g);
                    break;
            }
            //レコード描画
            GLES20.glUniform4f(GLES.colorHandle,0.0f,0.0f,0.0f,1.0f);
            String record = new String(Integer.toString(dbRecord[i][DB_MINUTE]) + "-" + Integer.toString(dbRecord[i][DB_SECONDS]) + "-" + Integer.toString(dbRecord[i][DB_MILLIS]));
            ft.Draw(record ,recordX[i] + width / 8,recordY[i] + border * 6,2,2,g);
        }
        String record = new String(Integer.toString(newRecordMinute) + "-" + Integer.toString(newRecordSeconds) + "-" + Integer.toString(newRecordMillis));
        ft.Draw("Record",(int)(width * 0.1),(int)(height * 0.2),2,2,g);
        ft.Draw(record,(int)(width * 0.1), (int)(height * 0.5),3,3,g);
        //Log.d("debug", "NMScene loop counter this " + Integer.toString(loopCounter));//@TODO debug
    }

    @Override
    public void onDraw(GLRenderer renderer) {

    }

    /**
     * 頂点バッファの生成
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     */
    private FloatBuffer makeVertexBuffer(int x, int y, int w, int h){
        //ウィンドウ座標を正規化デバイス座標に変換
        float left = ((float)x/(float)width) * 2.0f - 1.0f;
        float top = ((float)y/(float)height) * 2.0f - 1.0f;
        float right = ((float)(x+w)/(float)width) * 2.0f - 1.0f;
        float bottom = ((float)(y+h)/(float)height) * 2.0f - 1.0f;

        top = -top;
        bottom = -bottom;

        //頂点バッファの生成
        float[] vertexs = {
                left, top, 0.0f,
                left, bottom, 0.0f,
                right, top, 0.0f,
                right, bottom, 0.0f,
        };

        //頂点バッファの生成
        return makeFloatBuffer(vertexs);
    }

    /**
     * float配列をバッファに変換
     * @param array
     * @return
     */
    protected FloatBuffer makeFloatBuffer(float[] array){
        FloatBuffer fb = ByteBuffer.allocateDirect(array.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        fb.put(array).position(0);
        return fb;
    }

}
