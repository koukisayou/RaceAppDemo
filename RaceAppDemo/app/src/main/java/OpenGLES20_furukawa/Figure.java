package OpenGLES20_furukawa;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by a50602 on 2017/03/17.
 */

public class Figure {
    public HashMap<String,Material> materials;//マテリアル群
    public ArrayList<Mesh> meshs;    //メッシュ群

    //描画
    public void draw() {
        for (Mesh mesh:meshs) mesh.draw();
    }
}
