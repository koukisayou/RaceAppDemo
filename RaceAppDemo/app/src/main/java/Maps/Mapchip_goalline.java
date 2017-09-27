package Maps;

/**
 * ゴール判定を行うマップチップ
 * Created by SayouKouki on 2017/07/19.
 */

public class Mapchip_goalline extends SuperMapchip {
    /**
     * コンストラクタ
     * y座標0.0のx,z平面上のパネルを用意する
     * オーバーライド後にカラーの数値を設定したり、テクスチャを設定したりすること
     * @param x 基準点x座標
     * @param y 基準点y座標
     * @param z 基準点z座標
     */
    public Mapchip_goalline(float x, float y, float z){
        super(x,y,z);
        color_r = 1.0f;
        color_g = 0.0f;
        color_b = 0.0f;
        indexNum = 4;
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
    public Mapchip_goalline(float x, float y, float z, int indexNum){
        super(x,y,z,indexNum);
        color_r = 1.0f;
        color_g = 1.0f;
        color_b = 1.0f;
    }
}
