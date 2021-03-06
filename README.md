# RaceAppDemo
![Title](https://github.com/koukisayou/RaceAppDemo/wiki/images/panel_title2.png)

## Overview
Androidで動作するタイムアタックレースゲームアプリです。レースのスタートからゴールまでの一周を、どれだけ早く走れるかを競います。
Javaソースファイルは/RaceAppDemo/app/src/main/java/に存在します。

## Description
### ゲームの画面ごとの説明と遷移
![Title](https://github.com/koukisayou/RaceAppDemo/wiki/images/RaceAppDemo_std.png)

***タイトル:***
アプリ起動時に表示される画像。簡単なアニメーション(キャラクターが道を走る)が動いている。
画面のどこかをタッチすると、タッチ音とともにチュートリアル画面へ進む。

***チュートリアル:***
操作説明画面です。画面右側に表示されている画像は、実際のレース場面のスクリーンショットであり、UI部分を赤枠で囲いナンバリングし、それぞれの役割を説明しています。
「START RACE!!」ボタンでレース画面に遷移します。

***レース:***
レース画面であり、このゲームのプレイシーンでもあります。レースの開始前にコースのマップを俯瞰で表示して、プレイヤーにコースの確認をしてもらいます。タッチでレースを開始。
画面下部にコントロールボタン、右上にレースの経過時間を表示。また、コースのカーブ手前では曲がる方向へのナビゲーションの矢印が表示されます。
ゴールをしてレースを終えるとレコード画面に遷移します。

***レコード:***
これまでのレースタイムを順位形式で表示する画面。
レース画面でゴールした後、遷移してくるので画面左側にはその際の記録を表示。右側にはローカルデータベースに保存されているレコードのうち、早いものから順位づけをして表示しています。
「GO NEXT」ボタンを押すとエンディング画面を挟んで、タイトル画面へ戻ります。

## Requirement

- API 22 Android 5.1 (Lollipop)以上

## Usage

RaceAppDemo-release.apkを実機にインストールすることで動作の確認を行えます。
また、このページにAndroid実機よりアクセスすれば [こちら](http://sayou.s2.xrea.com/RaceAppDemo-release.apk) からインストールすることができます。

## Author

Kouki.Sayou

## License

MIT