package Maps;

/**
 * 左カーブのナビゲーション画像を表示する判定を行うマップチップ
 * Created by SayouKouki on 2017/09/07.
 */

public class Mapchip_Left_Navi extends SuperMapchip {
    /**
     * コンストラクタ
     * y座標0.0のx,z平面上のパネルを用意する
     * オーバーライド後にカラーの数値を設定したり、テクスチャを設定したりすること
     * @param x 基準点x座標
     * @param y 基準点y座標
     * @param z 基準点z座標
     */
    public Mapchip_Left_Navi(float x, float y, float z) {
        super(x, y, z);
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
    public Mapchip_Left_Navi(float x, float y, float z, int indexNum) {
        super(x, y, z, indexNum);
    }
}
