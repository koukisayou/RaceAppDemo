package com.example.sayoukouki.raceappdemo;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import OpenGLES20_furukawa.GLES;
import OpenGLES20_furukawa.Graphics;
import OpenGLES20_furukawa.Texture;

/**
 * テクスチャマッピングを使用し文字の描画を行う
 *
 * 用意した文字テクスチャのpngと、各文字の座標群が記載されたjsonファイルを読み込み、
 * Drawメソッドで受け取った文字列を画面に2D描画する
 *
 * Created by SayouKouki on 2017/08/08.
 */

public class FontTexture {
    /**
     * Field
     */
    private static JSONObject jsonObj;//テクスチャ座標群のjsonファイル
    private Texture texture;//フォント画像
    private boolean textureLoadComplete = false;//フォント画像の読み込み確認
    public static Graphics g;//テクスチャ描画の際のGraphicsクラス

    /**
     * Constructor
     */
    public FontTexture(MainActivity activity){
        LoadTexture(activity.glView);//テクスチャのロード
        LoadJson(activity);//jsonファイルのロード
    }

    public FontTexture(MainActivity activity, Graphics g){
        LoadTexture(activity.glView);//テクスチャのロード
        LoadJson(activity);//jsonファイルのロード
        this.g = g;
    }

    /**
     * @deprecated str.split の際に、Javaのバージョンに応じて配列[0]にブランクができてしまう
     *      version > 8 問題なし
     *      version <= 7 配列[0]の要素がブランクになるためループカウンターに +1 が必要
     *      尚、System#getProperty("java.version") では "0" が返ってくるため確認不可(Androidであるためか？)
     *
     * 文字の描画
     * @param str 描画する文字列
     * @param x 描画の際の始点x (左上座標)
     * @param y 描画の際の始点y (左上座標)
     * @param g テクスチャ表示に必要なGraphicsクラス
     */
    public void Draw(String str, int x, int y, Graphics g){
        String[] strArray = str.split("");//1文字ずつ配列に置換
        int tx = 0, ty = 0;//テクスチャ座標
        int dx = x, dy = y;//加算されていく文字描画座標
        int width = 0, height = 0;//テクスチャ文字の幅、高さ

        if(!(textureLoadComplete))return;//テクスチャがロードされていなければ実行しない

        g.init();//テクスチャ描画のための初期化

        for(int i = 1; i < str.length() + 1; i++){
            //文字に対するテクスチャ座標値をJSONファイルより取得
            try{
                tx = jsonObj.getJSONObject(strArray[i]).getInt("x");
                ty = jsonObj.getJSONObject(strArray[i]).getInt("y");
                width = jsonObj.getJSONObject(strArray[i]).getInt("width");
                height = jsonObj.getJSONObject(strArray[i]).getInt("height");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            g.drawImage(texture, dx, dy, width, height, tx, ty, width, height);
            dx += width;//描画位置を更新
        }
    }

    /**
     *
     * 文字の描画
     * @param str 描画する文字列
     * @param x 描画の際の始点x (左上座標)
     * @param y 描画の際の始点y (左上座標)
     * @param sizex 文字のx幅の倍率。default = 1
     * @param sizey 文字のy幅の倍率。default = 1
     * @param g テクスチャ表示に必要なGraphicsクラス
     */
    public void Draw(String str, int x, int y, int sizex, int sizey, Graphics g){
        String[] strArray = str.split("");//1文字ずつ配列に置換
        int tx = 0, ty = 0;//テクスチャ座標
        int dx = x, dy = y;//加算されていく文字描画座標
        int width = 0, height = 0;//テクスチャ文字の幅、高さ

        if(!(textureLoadComplete))return;//テクスチャがロードされていなければ実行しない

        g.init();//テクスチャ描画のための初期化

        for(int i = 1; i < str.length() + 1; i++){
            //文字に対するテクスチャ座標値をJSONファイルより取得
            try{
                tx = jsonObj.getJSONObject(strArray[i]).getInt("x");
                ty = jsonObj.getJSONObject(strArray[i]).getInt("y");
                width = jsonObj.getJSONObject(strArray[i]).getInt("width");
                height = jsonObj.getJSONObject(strArray[i]).getInt("height");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            g.drawImage(texture, dx, dy, width * sizex, height * sizey, tx, ty, width, height);
            dx += width * sizex;//描画位置を更新
        }
    }

    /**
     *
     * 文字の描画
     * @param str 描画する文字列
     * @param x 描画の際の始点x (左上座標)
     * @param y 描画の際の始点y (左上座標)
     * @param sizex 文字のx幅の倍率。default = 1
     * @param sizey 文字のy幅の倍率。default = 1
     * @param r RGBAカラー R値
     * @param g RGBAカラー G値
     * @param b RGBAカラー B値
     * @param a RGBAカラー A値
     * @param graphics テクスチャ表示に必要なGraphicsクラス
     */
    public void Draw(String str, int x, int y, int sizex, int sizey, float r, float g, float b, float a, Graphics graphics){
        String[] strArray = str.split("");//1文字ずつ配列に置換
        int tx = 0, ty = 0;//テクスチャ座標
        int dx = x, dy = y;//加算されていく文字描画座標
        int width = 0, height = 0;//テクスチャ文字の幅、高さ

        if(!(textureLoadComplete))return;//テクスチャがロードされていなければ実行しない

        graphics.init();//テクスチャ描画のための初期化

        for(int i = 1; i < str.length() + 1; i++){
            //文字に対するテクスチャ座標値をJSONファイルより取得
            try{
                tx = jsonObj.getJSONObject(strArray[i]).getInt("x");
                ty = jsonObj.getJSONObject(strArray[i]).getInt("y");
                width = jsonObj.getJSONObject(strArray[i]).getInt("width");
                height = jsonObj.getJSONObject(strArray[i]).getInt("height");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            GLES20.glUniform4f(GLES.colorHandle, r,g,b,a);
            graphics.drawImage(texture, dx, dy, width * sizex, height * sizey, tx, ty, width, height);
            dx += width * sizex;//描画位置を更新
        }
    }
    /**
     * 文字テクスチャのロード
     * @param glView 画像読み込みの依頼先スレッド
     */
    private void LoadTexture(GLSurfaceView glView){
        //テクスチャのロード
        glView.queueEvent(new Runnable(){
            public void run(){
                try{
                    texture = Texture.createTextureFromAsset("font-w.png");

                    Log.i("Loading", "texture load complete!!");
                }catch(Exception e){
                    android.util.Log.e("debug", e.toString());
                    for(StackTraceElement ste:e.getStackTrace()){
                        android.util.Log.e("debug","    " + ste);
                    }
                }
                textureLoadComplete = true;//素材のロード完了を通知
            }
        });
    }

    /**
     * 座標群JSONファイルを読み込む
     * @param activity Assetsフォルダを開くために使用
     */
    private void LoadJson(MainActivity activity){
        //jsonファイルのロード
        try {
            InputStream input = activity.getAssets().open("font.json");
            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();

            // Json読み込み
            jsonObj = new JSONObject(new String(buffer));

            int value = jsonObj.getJSONObject("a").getInt("x");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
