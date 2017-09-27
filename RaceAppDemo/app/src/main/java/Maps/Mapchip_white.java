package Maps;

import android.opengl.GLES20;

import com.example.sayoukouki.raceappdemo.GLRenderer;

import OpenGLES20_furukawa.GLES;

/**
 * Created by SayouKouki on 2017/05/19.
 */

public class Mapchip_white extends SuperMapchip {
    /**
     * コンストラクタ
     * y座標0.0のx,z平面上のパネルを用意する
     * オーバーライド後にカラーの数値を設定したり、テクスチャを設定したりすること
     * @param x 基準点x座標
     * @param y 基準点y座標
     * @param z 基準点z座標
     */
    public Mapchip_white(float x, float y, float z) {
        super(x, y, z);
    }

    /**
     * コンストラクタ
     * y座標0.0のx,z平面上のパネルを用意する
     * 引数にてRGB配色の設定を行う
     * @param x 基準点x座標
     * @param y 基準点y座標
     * @param z 基準点z座標
     * @param r 赤 0.0f ~ 1.0f
     * @param g 緑 0.0f ~ 1.0f
     * @param b 青 0.0f ~ 1.0f
     */
    public Mapchip_white(float x, float y, float z, float r, float g, float b) {
        super(x, y, z, r, g, b);
    }

    /**
     * コンストラクタ
     * y座標0.0のx,z平面上のパネルを用意する
     * 引数にてRGB配色の設定を行う
     * マップチップIDの保存を行う
     * @param x 基準点x座標
     * @param y 基準点y座標
     * @param z 基準点z座標
     * @param r 赤 0.0f ~ 1.0f
     * @param g 緑 0.0f ~ 1.0f
     * @param b 青 0.0f ~ 1.0f
     * @param indexNum マップチップID
     */
    public Mapchip_white(float x, float y, float z, float r, float g, float b, int indexNum) {
        super(x, y, z, r, g, b, indexNum);
    }

    /**
     * コンストラクタ
     * y座標0.0のx,z平面上のパネルを用意する
     * マップチップIDが 2 の場合のみ、パネルでなくボックスを用意する
     * マップチップIDの保存を行う
     * @param x 基準点x座標
     * @param y 基準点y座標
     * @param z 基準点z座標
     * @param indexNum マップチップID
     */
    public Mapchip_white(float x, float y, float z, int indexNum) {
        super(x, y, z, indexNum);
    }

    public void Draw(GLRenderer renderer) {
        GLES20.glVertexAttribPointer(GLES.positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glUniform4f(GLES.colorHandle,1.0f,1.0f,1.0f,1.0f);
        indexBuffer.position(0);//最初の頂点を指定,インデックスナンバーを指定している
        /**
         * void glDrawElements(int mode, int count, int type, Buffer buffer)
         * 描画メソッド
         * 引数
         *      mode. プリミティブ種別
         *      count. 頂点数
         *      type. データ型
         *      buffer. インデックスバッファ
         */
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 4, GLES20.GL_UNSIGNED_BYTE, indexBuffer);
    }

}
