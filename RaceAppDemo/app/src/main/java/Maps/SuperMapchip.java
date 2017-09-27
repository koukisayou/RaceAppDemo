package Maps;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import OpenGLES20_furukawa.GLES;
import OpenGLES20_furukawa.Texture;
import OpenGLES20_furukawa.Vector3;

/**
 * マップチップのスーパークラス
 * Created by SayouKouki
 */

public class SuperMapchip implements MapchipFields {
    /**
     * Field
     */
    protected FloatBuffer vertexBuffer;//頂点座標バッファ
    protected ByteBuffer indexBuffer;//インデックスバッファ
    protected FloatBuffer normalBuffer;//法線バッファ
    protected FloatBuffer uvBuffer;//UVバッファ
    public Vector3 coord;//基準座標
    public float color_r;//red
    public float color_g;//green
    public float color_b;//blue
    public int indexNum;//マップチップのID

    /**
     * コンストラクタ
     * y座標0.0のx,z平面上のパネルを用意する
     * オーバーライド後にカラーの数値を設定したり、テクスチャを設定したりすること
     * @param x 基準点x座標
     * @param y 基準点y座標
     * @param z 基準点z座標
     */
    public SuperMapchip(float x, float y, float z){
        coord = new Vector3(x, y, z);

        //頂点バッファの作成
        float[] vertexs = CreateXZVertex(x,y,z);
        vertexBuffer = makeFloatBuffer(vertexs);

        //インデックスバッファの作成
        byte[] indexs = {
                0,1,2,3,
        };
        indexBuffer = makeByteBuffer(indexs);

        //法線バッファの作成
        float div = (float)Math.sqrt((1.0f * 1.0f) + (1.0f * 1.0f) + (1.0f * 1.0f));
        for(int i = 0; i < vertexs.length; i++){
            vertexs[i] /= div;
        }
        normalBuffer = makeFloatBuffer(vertexs);

        //UVバッファの生成
        float[] uvs={
                0.0f,0.0f,//左上
                0.0f,1.0f,//左下
                1.0f,0.0f,//右上
                1.0f,1.0f,//右下
        };
        uvBuffer=makeFloatBuffer(uvs);
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
    public SuperMapchip(float x, float y, float z, float r, float g, float b){
        coord = new Vector3(x, y, z);

        //頂点バッファの作成
        float[] vertexs = CreateXZVertex(x,y,z);
        vertexBuffer = makeFloatBuffer(vertexs);

        //インデックスバッファの作成
        byte[] indexs = {
                0,1,2,3,
        };
        indexBuffer = makeByteBuffer(indexs);

        //法線バッファの作成
        float div = (float)Math.sqrt((1.0f * 1.0f) + (1.0f * 1.0f) + (1.0f * 1.0f));
        for(int i = 0; i < vertexs.length; i++){
            vertexs[i] /= div;
        }
        normalBuffer = makeFloatBuffer(vertexs);

        //カラー設定
        color_r = r;
        color_g = g;
        color_b = b;
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
    public SuperMapchip(float x, float y, float z, float r, float g, float b, int indexNum){
        coord = new Vector3(x, y, z);

        //頂点バッファの作成
        float[] vertexs = CreateXZVertex(x,y,z);
        vertexBuffer = makeFloatBuffer(vertexs);

        //インデックスバッファの作成
        byte[] indexs = {
                0,1,2,3,
        };
        indexBuffer = makeByteBuffer(indexs);

        //法線バッファの作成
        float div = (float)Math.sqrt((1.0f * 1.0f) + (1.0f * 1.0f) + (1.0f * 1.0f));
        for(int i = 0; i < vertexs.length; i++){
            vertexs[i] /= div;
        }
        normalBuffer = makeFloatBuffer(vertexs);

        //UVバッファの生成
        float[] uvs={
                0.0f,0.0f,//左上
                0.0f,1.0f,//左下
                1.0f,0.0f,//右上
                1.0f,1.0f,//右下
        };
        uvBuffer=makeFloatBuffer(uvs);

        //カラー設定
        color_r = r;
        color_g = g;
        color_b = b;
        this.indexNum = indexNum;//マップチップIDを保存
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
    public SuperMapchip(float x, float y, float z, int indexNum){
        switch(indexNum) {
            //パネルを作る
            case 0:
            case 1:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                //頂点バッファの作成
                coord = new Vector3(x, y, z);
                float[] vertexs = {
                        x, y, z,//頂点1
                        x, y, z + MAPCHIP_SIZE_Zf,//頂点2
                        x + MAPCHIP_SIZE_Xf, y, z,//頂点3
                        x + MAPCHIP_SIZE_Xf, y, z + MAPCHIP_SIZE_Zf,//頂点4
                };
                vertexBuffer = makeFloatBuffer(vertexs);

                //インデックスバッファの作成
                byte[] indexs = {
                        0, 1, 2, 3,
                };
                indexBuffer = makeByteBuffer(indexs);

                //法線バッファの作成
                float div = (float) Math.sqrt((1.0f * 1.0f) + (1.0f * 1.0f) + (1.0f * 1.0f));
                for (int i = 0; i < vertexs.length; i++) {
                    vertexs[i] /= div;
                }
                normalBuffer = makeFloatBuffer(vertexs);
                break;
            case 2:
                coord = new Vector3(x,y,z);
                int y_size = 1;

                //頂点バッファの作成
                float[] vertexs2 = {
                        x + MAPCHIP_SIZE_Xf, y + y_size, z + MAPCHIP_SIZE_Zf, //頂点0
                        x + MAPCHIP_SIZE_Xf, y + y_size, z, //頂点1
                        x, y + y_size, z + MAPCHIP_SIZE_Zf, //頂点2
                        x, y + y_size, z, //頂点3
                        x + MAPCHIP_SIZE_Xf, y, z + MAPCHIP_SIZE_Zf, //頂点4
                        x + MAPCHIP_SIZE_Xf, y, z, //頂点5
                        x, y, z + MAPCHIP_SIZE_Zf, //頂点6
                        x, y, z, //頂点7
                };

                //インデックスバッファの作成
                vertexBuffer = makeFloatBuffer(vertexs2);
                byte[] indexs2 = {
                        0, 1, 2, 3, 6, 7, 4, 5, 0, 1, //面0
                        1, 5, 3, 7, //面1
                        0, 2, 4, 6, //面2
                        /*
                        0,1,2,3,
                        0,4,1,5,
                        4,0,6,2,
                        6,2,7,3,
                        7,3,5,1,*/
                        /*0,1,2,
                        3,7,2,
                        1,5,3,
                        0,4,5,
                        2,6,4,*/
                };
                indexBuffer = makeByteBuffer(indexs2);

                //法線バッファの作成
                float divn = (float)Math.sqrt((1.0f * 1.0f) + (1.0f * 1.0f) + (1.0f * 1.0f));
                for(int i = 0; i < vertexs2.length; i++){
                    vertexs2[i] /= divn;
                }
                normalBuffer = makeFloatBuffer(vertexs2);
                break;
        }
        //UVバッファの生成
        float[] uvs={
                0.0f,0.0f,//左上
                0.0f,1.0f,//左下
                1.0f,0.0f,//右上
                1.0f,1.0f,//右下
        };
        uvBuffer=makeFloatBuffer(uvs);

        this.indexNum = indexNum;//マップチップIDを保存
    }

    /**
     * Drawing method
     */
    public void Draw(){
        GLES20.glUniform4f(GLES.colorHandle,color_r,color_g,color_b,1.0f);
        GLES20.glUniform4f(GLES.materialAmbientHandle,color_r,color_g,color_b,1.0f);
        GLES20.glUniform4f(GLES.materialDiffuseHandle,color_r,color_g,color_b,1.0f);
        GLES20.glUniform4f(GLES.materialSpecularHandle,color_r,color_g,color_b,1.0f);
        GLES20.glUniform1f(GLES.materialShininessHandle,80.0f);

        GLES20.glEnableVertexAttribArray(GLES.positionHandle);
        GLES20.glEnableVertexAttribArray(GLES.normalHandle);

        GLES20.glVertexAttribPointer(GLES.positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glVertexAttribPointer(GLES.normalHandle, 3, GLES20.GL_FLOAT, false, 0, normalBuffer);
        GLES.glPushMatrix();
        GLES.updateMatrix();
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

        GLES20.glDisableVertexAttribArray(GLES.positionHandle);
        GLES20.glDisableVertexAttribArray(GLES.normalHandle);
        GLES.glPopMatrix();
    }

    public void Draw(Texture texture){
        GLES20.glUniform4f(GLES.colorHandle, 1.0f, 1.0f, 1.0f,1.0f);

        GLES20.glUniform4f(GLES.materialAmbientHandle,1.0f,1.0f,1.0f,1.0f);
        GLES20.glUniform4f(GLES.materialDiffuseHandle,1.0f,1.0f,1.0f,1.0f);
        GLES20.glUniform4f(GLES.materialSpecularHandle,1.0f,1.0f,1.0f,1.0f);
        GLES20.glUniform1f(GLES.materialShininessHandle,80.0f);

        texture.bind();

        //テクスチャ行列の移動・拡縮
        Matrix.setIdentityM(GLES.texMatrix,0);
        Matrix.translateM(GLES.texMatrix,0,0,0,0.0f);
        Matrix.scaleM(GLES.texMatrix,0,1.0f,1.0f,1.0f);
        GLES20.glUniformMatrix4fv(GLES.texMatrixHandle,1,
                false,GLES.texMatrix,0);

        GLES20.glEnableVertexAttribArray(GLES.positionHandle);
        GLES20.glEnableVertexAttribArray(GLES.normalHandle);
        GLES20.glEnableVertexAttribArray(GLES.uvHandle);

        GLES20.glVertexAttribPointer(GLES.positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glVertexAttribPointer(GLES.normalHandle, 3, GLES20.GL_FLOAT, false, 0, normalBuffer);
        GLES20.glVertexAttribPointer(GLES.uvHandle,2, GLES20.GL_FLOAT,false,0,uvBuffer);
        GLES.glPushMatrix();
        GLES.updateMatrix();
        /**
         * void glDrawElements(int mode, int count, int type, Buffer buffer)
         * 描画メソッド
         * 引数
         *      mode. プリミティブ種別
         *      count. 頂点数
         *      type. データ型
         *      buffer. インデックスバッファ
         */
        switch(indexNum){
            case 0:
            case 1:
                GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 4, GLES20.GL_UNSIGNED_BYTE, indexBuffer);
                break;
            case 2:
                //ボックスを描画する
                indexBuffer.position(0);
                GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 10, GLES20.GL_UNSIGNED_BYTE, indexBuffer);
                indexBuffer.position(10);
                GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 4, GLES20.GL_UNSIGNED_BYTE, indexBuffer);
                indexBuffer.position(14);
                GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 4, GLES20.GL_UNSIGNED_BYTE, indexBuffer);
                break;
            default:
                GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 4, GLES20.GL_UNSIGNED_BYTE, indexBuffer);
                break;
        }
        GLES20.glDisableVertexAttribArray(GLES.positionHandle);
        GLES20.glDisableVertexAttribArray(GLES.normalHandle);
        GLES20.glDisableVertexAttribArray(GLES.uvHandle);
        texture.unbind();
        GLES.glPopMatrix();
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

    /**
     * //byte配列をバッファに変換
     * @param array
     * @return 変換後のByteBuffer
     */
    protected ByteBuffer makeByteBuffer(byte[] array){
        ByteBuffer bb = ByteBuffer.allocateDirect(array.length).order(ByteOrder.nativeOrder());
        bb.put(array).position(0);
        return bb;
    }

    /**
     * xz平面の頂点配列を作成する
     * @param x
     * @param y
     * @param z
     * @return
     */
    protected float[] CreateXZVertex(float x, float y, float z){
        float[] vertexs = {
                x, y, z,//頂点1
                x, y, z + MAPCHIP_SIZE_Zf,//頂点2
                x + MAPCHIP_SIZE_Xf, y, z,//頂点3
                x + MAPCHIP_SIZE_Xf, y, z + MAPCHIP_SIZE_Zf,//頂点4
        };
        return vertexs;
    }
}
