package Maps;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.example.sayoukouki.raceappdemo.GLRenderer;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import MyCar.NormalCar;
import MyCar.SuperCar;
import OpenGLES20_furukawa.GLES;
import OpenGLES20_furukawa.Texture;

/**
 * コースの地形データやレース、車の状態を管理する
 * Created by SayouKouki
 */
public abstract class SuperMap implements MapchipFields {
    /**
     * Field
     */
    public ArrayList<ArrayList<SuperMapchip>> mapList = new ArrayList();//マップチップの情報群
    public int mapLength = -1;//マップのテキストファイルの1行長
    protected String fileName;//読み込むマップチップファイル

    public SuperCar car;//Car をアップキャストして格納

    //カメラの座標
    protected float camera_x;
    protected float camera_y;
    protected float camera_z;

    //テクスチャ
    public Texture asphalt1;
    public Texture asphalt2;
    public Texture green;
    public Texture navi;
    public boolean loadComplete = false;//テクスチャデータのロード完了通知

    //カーブナビゲーション表示スイッチ
    public boolean navil = false;
    public boolean navir = false;

    /**
     * Constructor
     */
    public SuperMap(GLSurfaceView glView){
        fileName = new String("mapchip_stage_1ver2.txt");//読み込むマップチップファイル名
        LoadAllMapchip(fileName);
        LoadMaterial(glView);
        car = new NormalCar(glView);
    }

    public SuperMap(String fileName){

    }

    /**
     * サーフェイス作成時に動作する
     */
    public void onSurfaceCreated() {

    }

    /**
     * 毎フレームの定期処理
     */
    public boolean onTick() {
        CarMoving();
        return false;
    }

    /**
     * Drawing methods
     */
    public void onDraw() {

    }

    public void onDraw(GLRenderer renderer){
        //ビュー変換
        Matrix.setIdentityM(GLES.mMatrix,0);
        camera_x = car.coord.x + 5.0f * (float)Math.cos(car.angle * Math.PI / 180);
        camera_y = 1.0f;
        camera_z = car.coord.z + 5.0f * (float)Math.sin(car.angle * Math.PI / 180);
        /*@TODO test定点カメラ
        GLES.gluLookAt(GLES.mMatrix,
                2.0f, 10.0f,2.0f, //カメラの視点
                car.coord.x, 0.0f, car.coord.z, //カメラの焦点
                0.0f, 1.0f, 0.0f);//カメラの上方向
        */
        //player背後カメラ
        GLES.gluLookAt(GLES.mMatrix,
                camera_x, camera_y,camera_z, //カメラの視点
                car.coord.x, car.coord.y, car.coord.z, //カメラの焦点
                0.0f, 1.0f, 0.0f);//カメラの上方向

        //光源位置の指定
        float[] lightPos = {999.0f, 999.0f, 0.0f, 1.0f};//光源の位置
        float[] resultM = new float[4];
        Matrix.multiplyMV(resultM, 0, GLES.mMatrix, 0, lightPos, 0);
        GLES20.glUniform4f(GLES.lightPosHandle, resultM[0], resultM[1], resultM[2],resultM[3]);
        GLES20.glUniform1i(GLES.useLightHandle,0);

        if(loadComplete)MapDrawing(renderer);//Mapの描画

        //車の描画
        GLES20.glUniform1i(GLES.useLightHandle,1);
        GLES20.glEnableVertexAttribArray(GLES.positionHandle);
        GLES.glPushMatrix();
        car.Draw(renderer);
        GLES.glPopMatrix();
        GLES20.glDisableVertexAttribArray(GLES.positionHandle);
    }

    /**
     * マップを俯瞰する
     * @param renderer
     */
    public void onDrawBirdsView(GLRenderer renderer){

    }

    /**
     * Mapchip の読み込み
     * マップチップのテキストファイルを読み込み、行解析を行う。
     * @param fileName
     */
    protected void LoadAllMapchip(String fileName){
        int rowCount = 0;//マップ読み込み時、行のカウンタ

        //マップチップの読み込み
        try{
            DataInputStream in=new DataInputStream(GLES.context.getAssets().open(fileName));
            BufferedReader mcBr = new BufferedReader(new InputStreamReader((InputStream)in));

            String mcStr;//mapchipをストリング形式で入れる
            //行ごとに読み込んでいく
            while((mcStr = mcBr.readLine()) != null){
                /**
                 * ファイル読み込みのルール
                 * 1. 先頭行の読み込みを行う -> コメントアウト機能をつけたいから -> コメントアウトには[#]を使用する
                 * 2. 一つずつ読み込んで処理していくが、区切り文字を判断できるようにする -> space
                 * 3. 文字列型を整数型etcに変換
                 * 4. 適切なクラスorメソッドを配列に代入
                 */
                String[] word = mcStr.split(" ",0);//正規表現を使って分割する

                //ファイルの読み込み
                if (!(word[0].equals("#"))) {
                    //テキストファイルの1行長を取得する
                    if(!(mapLength == -1)){
                        mapLength = word.length;
                    }
                    mapList.add(new ArrayList<SuperMapchip>());

                    //1行読み込みループ
                    for(int i = 0; i < word.length; i++){
                        mapList.get(rowCount).add(LoadMapchip(i, rowCount, Integer.parseInt(word[i])));
                    }
                    rowCount++;//次の行番号へ更新
                }
            }

            mcBr.close();//ファイルクローズ
            rowCount = 0;//Counter init.
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * マップチップを読み込む
     * LoadAllMapchip内で実行される。
     * switch文でmapchipNumを判定し、
     * @return 読み込んだマップチップインスタンス
     * @param x マップチップのX軸
     * @param z マップチップのZ軸
     * @param mapchipNum マップチップファイルの行解析で得た値
     */
    protected abstract SuperMapchip LoadMapchip(int x, int z, int mapchipNum);

    /**
     * 描画で使用する画像やオブジェクトファイルをロードする
     * GLSurfaceView#queueEvent
     * @param glView
     */
    public abstract void LoadMaterial(GLSurfaceView glView);

    /**
     * マップ上の全ての車を動かす
     */
    public void CarMoving(){
        car.checkMove();
        if(!(WallCollision())){
            car.Moving();
        }
    }

    /**
     * 車と壁との当たり判定
     * 移動時に通過したマップチップを調べ、壁があれば衝突とする
     * @return true = 衝突, false = 非衝突
     */
    public boolean WallCollision(){
        double minx,maxx,minz,maxz;//現在座標と移動先座標の値を大小でわける
        int min_index_x, max_index_x, min_index_z, max_index_z;//上記変数の値に対応するマップチップ

        //移動時の座標の大小を分ける
        if(car.coord.x > car.nextCoord.x){
            maxx = car.coord.x;
            minx = car.nextCoord.x;
        }else{
            maxx = car.nextCoord.x;
            minx = car.coord.x;
        }
        if(car.coord.z > car.nextCoord.z){
            maxz = car.coord.z;
            minz = car.nextCoord.z;
        }else{
            maxz = car.nextCoord.z;
            minz = car.coord.z;
        }

        //最小・最大インデックスを出す
        min_index_x = (int)(minx / MAPCHIP_SIZE_Xd);
        max_index_x = (int)(maxx / MAPCHIP_SIZE_Xd);
        min_index_z = (int)(minz / MAPCHIP_SIZE_Zd);
        max_index_z = (int)(maxz / MAPCHIP_SIZE_Zd);

        //z軸
        for(int i = 0; i <= max_index_z - min_index_z; i++){
            //x軸
            for(int j = 0; j <= max_index_x - min_index_x; j++){
                if(mapList.get(i + min_index_z).get(j + min_index_x) instanceof Mapchip_Wall) {return true;}
            }
        }
        return false;
    }

    /**
     * マップの描画
     * ゲームの動作を軽くするため、プレイヤー周囲のマップチップのみを対象にして描画を行う
     * @param renderer
     */
    public void MapDrawing(GLRenderer renderer){
        //マップの描画範囲を限定するロジックで使用する変数
        int carx = (int)(car.coord.x / MAPCHIP_SIZE_Xd);//車が存在するマップチップインデックスx
        int carz = (int)(car.coord.z / MAPCHIP_SIZE_Zd);//車が存在するマップチップインデックスz
        int loop_range = 32;//車を中心とした正方形の描画範囲
        int carz_index = 0;//mapList.get()の参照インデックス
        int carx_index = 0;//mapList.get().get()の参照インデックス

        try {
            //mapList.get(z)
            for (int i = 0; i < loop_range; i++) {
                carz_index = carz + -loop_range / 2 + i;//mapListの参照インデックスを得る
                //mapListのインデックスが存在しないならスキップ
                if (carz_index < 0 || carz_index >= mapList.size()) {
                    continue;
                }

                //mapList.get(x)
                for (int j = 0; j < loop_range; j++) {
                    carx_index = carx - loop_range / 2 + j;//mapList.get(z)の参照インデックスを得る
                    //mapList.get(z)のインデックスが存在しないならスキップ
                    if (carx_index < 0 || carx_index >= mapList.get(carz_index).size()) {
                        continue;
                    } else {
                        //地形描画
                        //マップチップのIDを参照し、描画する際の適切なテクスチャ画像を渡す
                        switch( mapList.get(carz_index).get(carx_index).indexNum){
                            case 0:
                                mapList.get(carz_index).get(carx_index).Draw(asphalt1);
                                break;
                            case 1:
                                mapList.get(carz_index).get(carx_index).Draw(green);
                                break;
                            case 2:
                                mapList.get(carz_index).get(carx_index).Draw(asphalt2);
                                break;
                            case 3:
                                mapList.get(carz_index).get(carx_index).Draw();
                                break;
                        }
                    }
                }
            }
        }catch(IndexOutOfBoundsException e){
            Log.e("IndexOutOfBoundsExc", "マップ描画時に配列の要素数を超えました");
            Log.e("carx", Integer.toString(carx));
            Log.e("carz", Integer.toString(carz));
            Log.e("carx_index", Integer.toString(carx_index));
            Log.e("carz_index", Integer.toString(carz_index));
        }
        //全てのマップチップを描画する
        /*for(int i = 0; i < mapList.size(); i++){
            for(int j = 0; j < mapList.get(i).size(); j++){
                mapList.get(i).get(j).Draw(renderer);
            }
        }*/
    }
}
