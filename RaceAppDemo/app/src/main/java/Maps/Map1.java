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

import OpenGLES20_furukawa.GLES;
import OpenGLES20_furukawa.Texture;

import static com.example.sayoukouki.raceappdemo.FontTexture.g;

/**
 *
 * Created by SayouKouki
 */
public class Map1 extends SuperMap {
    /**
     * Field
     */
    private static int naviTime = 0;//カーブナビゲーションの表示時間

    /**
     * Constructor
     * @param glView
     */
    public Map1(GLSurfaceView glView) {
        super(glView);
    }

    public Map1(String fileName) {
        super(fileName);
    }

    /**
     * サーフェイスビューを作成時に動作
     */
    @Override
    public void onSurfaceCreated() {

    }

    /**
     * 定期処理
     */
    @Override
    public boolean onTick() {
        //カーブナビゲーションの表示時間を更新
        if(navir || navil){
            naviTime++;
            if(naviTime > 20){
                navir = navil = false;//表示を止める
                naviTime = 0;
            }
        }

        CarMoving();
        if(car.goal){
            return true;
        }
        return false;
    }

    /**
     * Drawing methods
     */
    @Override
    public void onDraw() {

    }

    @Override
    public void onDraw(GLRenderer renderer){
        //ビュー変換
        Matrix.setIdentityM(GLES.mMatrix,0);
        camera_x = car.coord.x + 5.0f * (float)Math.cos(car.angle * Math.PI / 180);
        camera_y = 1.5f;
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
                car.coord.x, car.coord.y + 1.5f, car.coord.z, //カメラの焦点
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

        //カーブナビゲーションの描画
        g.init();
        if(navil){
            GLES20.glUniform4f(GLES.colorHandle, 1.0f, 1.0f, 0.3f,1.0f);
            int texWidth = g.getScreenW() / 10;//テクスチャの大きさ
            g.drawImage(navi, g.getScreenW() / 2 - texWidth / 2, g.getScreenH() / 10, texWidth, texWidth, 180, 1.0f);
        }else if(navir) {
            GLES20.glUniform4f(GLES.colorHandle, 1.0f, 1.0f, 0.3f,1.0f);
            int texWidth = g.getScreenW() / 10;//テクスチャの大きさ
            g.drawImage(navi,g.getScreenW() / 2 - texWidth / 2, g.getScreenH() / 10, texWidth,texWidth,0,1.0f);
        }
    }

    /**
     * マップを俯瞰する
     * レース開始前のマップ全体表示に使用する
     * @param renderer
     */
    @Override
    public void onDrawBirdsView(GLRenderer renderer){
        //ビュー変換
        Matrix.setIdentityM(GLES.mMatrix,0);

        float camerax = 150.0f;
        float cameraz = 230.0f;
        //カメラをマップの上空に配置する
        GLES.gluLookAt(GLES.mMatrix,
                camerax, 600.0f,cameraz, //カメラの視点
                camerax, 0.0f, cameraz, //カメラの焦点
                0.0f, 0.0f, 1.0f);//カメラの上方向

        float[] lightPos = {999.0f, 999.0f, 0.0f, 1.0f};//光源の位置
        //光源位置の指定
        float[] resultM = new float[4];
        Matrix.multiplyMV(resultM, 0, GLES.mMatrix, 0, lightPos, 0);
        GLES20.glUniform4f(GLES.lightPosHandle, resultM[0], resultM[1], resultM[2],resultM[3]);

        GLES20.glUniform1i(GLES.useLightHandle,0);

        //地形の描画
        if(loadComplete) {
            for (int i = 0; i < mapList.size(); i++) {
                for (int j = 0; j < mapList.get(i).size(); j++) {
                    switch (mapList.get(i).get(j).indexNum) {
                        case 0://road
                            mapList.get(i).get(j).Draw(asphalt1);
                            break;
                        case 1://green
                            mapList.get(i).get(j).Draw(green);
                            break;
                        case 2://wall
                            mapList.get(i).get(j).Draw(asphalt2);
                            break;
                        case 3://goalLine
                            mapList.get(i).get(j).Draw();
                            break;
                        case 4://goalCheck
                        case 5://checkPoint
                        case 6://Left navi
                        case 7://Right navi
                            mapList.get(i).get(j).Draw(asphalt1);
                            break;
                    }
                }
            }
        }

        GLES20.glUniform1i(GLES.useLightHandle,1);
        GLES20.glEnableVertexAttribArray(GLES.positionHandle);
        //車の描画
        GLES.glPushMatrix();
        car.Draw(renderer);
        GLES.glPopMatrix();
        GLES20.glDisableVertexAttribArray(GLES.positionHandle);
    }

    /**
     * Mapchip の読み込み
     * @param fileName
     */
    @Override
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
                        //1文字ずつロード
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
     * LoadAllMapchip内で実行される。
     * switch文でmapchipNumを判定し、適切なマップチップインスタンスをリターンする
     * @return 読み込んだマップチップインスタンス
     * @param x マップチップのX軸
     * @param z マップチップのZ軸
     * @param mapchipNum マップチップファイルの行解析で得た値
     */
    protected SuperMapchip LoadMapchip(int x, int z, int mapchipNum){
        switch(mapchipNum){
            //0 = white, 1 = red, 2 = blue, 3 = green
            case 0://road
                return new SuperMapchip(MAPCHIP_SIZE_Xf * (x), 0.0f, MAPCHIP_SIZE_Zf * (z), 0);
            case 1://green
                return new SuperMapchip(MAPCHIP_SIZE_Xf * (x), 0.0f, MAPCHIP_SIZE_Zf * (z), 1);
            case 2://wall
                return new Mapchip_Wall(MAPCHIP_SIZE_Xf * (x), 0.0f, MAPCHIP_SIZE_Zf * (z), 2);
            case 3://goal line
                return new Mapchip_goalline(MAPCHIP_SIZE_Xf * (x), 0.0f, MAPCHIP_SIZE_Zf * (z), 3);
            case 4://goal check
                return new Mapchip_goalCheck(MAPCHIP_SIZE_Xf * (x), 0.0f, MAPCHIP_SIZE_Zf * (z), 4);
            case 5://check point
                return new Mapchip_checkPoint(MAPCHIP_SIZE_Xf * (x), 0.0f, MAPCHIP_SIZE_Zf * (z), 5);
            case 6://left navi
                return new Mapchip_Left_Navi(MAPCHIP_SIZE_Xf * (x), 0.0f, MAPCHIP_SIZE_Zf * (z), 6);
            case 7://right navi
                return new Mapchip_Right_Navi(MAPCHIP_SIZE_Xf * (x), 0.0f, MAPCHIP_SIZE_Zf * (z), 7);
            default:
                return null;
        }
    }

    /**
     * テクスチャの読み込み
     * @param glView
     */
    @Override
    public void LoadMaterial(GLSurfaceView glView){
        glView.queueEvent(new Runnable(){
            public void run(){
                try{
                    asphalt1 = Texture.createTextureFromAsset("asphalt1.jpg");
                    asphalt2 = Texture.createTextureFromAsset("asphalt2.png");
                    green = Texture.createTextureFromAsset("green.png");
                    navi = Texture.createTextureFromAsset("navi-w.png");
                }catch(Exception e){
                    android.util.Log.e("debug", e.toString());
                    for(StackTraceElement ste:e.getStackTrace()){
                        android.util.Log.e("debug","    " + ste);
                    }
                }
                loadComplete = true;//素材のロード完了を通知
            }
        });
    }

    /**
     * マップ上の全ての車を動かす
     */
    @Override
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
    @Override
    public boolean WallCollision(){
        double minx,maxx,minz,maxz;//現在座標と移動先座標の値を大小でわける
        int min_index_x, max_index_x, min_index_z, max_index_z;//上記変数の値に対応するマップチップ

        //ゴール判定で使用するローカル変数
        int nowIndexx = (int)(car.coord.x / MAPCHIP_SIZE_Xd);
        int nowIndexz = (int)(car.coord.z / MAPCHIP_SIZE_Zd);
        int nextIndexx = (int)(car.nextCoord.x / MAPCHIP_SIZE_Xd);
        int nextIndexz = (int)(car.nextCoord.z / MAPCHIP_SIZE_Zd);
        //現在位置がgoalCheckであるか
        if(mapList.get(nowIndexz).get(nowIndexx) instanceof Mapchip_goalCheck){
            //次回位置がgoalであるか
            if(mapList.get(nextIndexz).get(nextIndexx) instanceof Mapchip_goalline){
                //ゴール判定
                if(car.checkPoint){
                    car.goal = true;
                    Log.d("debug", "ゴールしました");
                    return false;
                }
            }
            //次回位置がcheckPointであるか
            else if(mapList.get(nextIndexz).get(nextIndexx) instanceof Mapchip_checkPoint){
                //チェックポイント判定
                car.checkPoint = true;
                return false;
            }
        }
        //goalCheckを経由せずにcheckPointを跨いだ場合
        else if(mapList.get(nextIndexz).get(nextIndexx) instanceof Mapchip_checkPoint){
            //チェックポイントを減らす
            car.checkPoint = false;
        }
        //Left naviにいる場合
        else if(mapList.get(nowIndexz).get(nowIndexx) instanceof Mapchip_Left_Navi){
            navil = true;
            navir = false;
            Log.i("Navigation", "Navi left!");
        }
        //Right navi
        else if(mapList.get(nowIndexz).get(nowIndexx) instanceof Mapchip_Right_Navi){
            navir = true;
            navil = false;
            Log.i("Navigation", "Navi right!");
        }

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
    @Override
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
                        switch(mapList.get(carz_index).get(carx_index).indexNum){
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
                            case 4://goalLine
                            case 5://goalCheck
                            case 6://CheckPoint
                            case 7://Right navi
                                mapList.get(carz_index).get(carx_index).Draw(asphalt1);
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
        /*
        for(int i = 0; i < mapList.size(); i++){
            for(int j = 0; j < mapList.get(i).size(); j++){
                mapList.get(i).get(j).Draw(renderer);
            }
        }*/
    }
}
