package OpenGLES20_furukawa;

/**
 * Created by a50602 on 2017/03/17.
 */

public class Mesh {
    public VertexBuffer vertexBuffer;//頂点バッファ
    public IndexBuffer  indexBuffer; //インデックスバッファ
    public Material     material;    //マテリアル

    //描画
    public void draw() {
        material.bind();
        vertexBuffer.bind();
        indexBuffer.draw();
        vertexBuffer.unbind();
        material.unbind();
    }
}
