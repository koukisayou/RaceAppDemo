package Maps;

/**
 * 壁マップチップ
 * このマップチップ上をプレイヤーは進行できない
 * Created by SayouKouki
 */

public class Mapchip_Wall extends SuperMapchip {
    /**
     * コンストラクタ
     * y座標0.0のx,z平面上のパネルを用意する
     * オーバーライド後にカラーの数値を設定したり、テクスチャを設定したりすること
     * @param x 基準点x座標
     * @param y 基準点y座標
     * @param z 基準点z座標
     */
    public Mapchip_Wall(float x, float y, float z) {
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
    public Mapchip_Wall(float x, float y, float z, float r, float g, float b) {
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
    public Mapchip_Wall(float x, float y, float z, float r, float g, float b, int indexNum) {
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
    public Mapchip_Wall(float x, float y, float z, int indexNum) {
        super(x, y, z, indexNum);
    }

    public void ReadFile(){

    }
}
