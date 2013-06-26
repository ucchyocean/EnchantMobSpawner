EnchantMobSpawner
=================

<h2><a href="https://github.com/ucchyocean/EnchantMobSpawner/blob/master/release/EnchantMobSpawner_v030.zip?raw=true">ダウンロード v0.3</a></h2>

<h2>概要</h2>

あらかじめ指定したエンチャント装備を装備したMOBが、
スポナーから沸くようにするプラグインです。

<h2>導入方法</h2>

上のリンクから、EnchantMobSpawner.zip をダウンロードし、解凍して EnchantMobSpawner.jar を plugins フォルダに突っ込む。<br>


<h2>使用方法</h2>

plugins/EnchantMobSpawner/config.yml の設定でプロファイル名を指定し、次の行に mob と、その次の行に kit、effect を指定します。<br>
mob の指定は必須です。<br>

<p><pre>
# (ProfileName).mob: スポーンさせるMOBの種類を設定します。
#       指定できる種類は、zombie, skeleton, creeper, ... など。
# (ProfileName).kit: スポーンするMOBに装備させるアイテムを指定します。
#       指定の順序は、(武器),(頭防具),(体防具),(レギンス防具),(足防具)
#       指定する形式は、(アイテムID)^(E-ID)-(E-Level)@(Durability)
#       「^(E-ID)-(E-Level)」の部分は、複数回指定可能です。
#       「@(Durability)」のDurability（耐久消耗度）は、省略可能です。
#       「0」を指定すると、その部分は装備をしません。
# (ProfileName).effect: スポーンするMOBに設定するポーション効果を指定します。
#       指定する形式は、(エフェクトID)-(エフェクトレベル)
#       コンマで複数指定可能です。例）1-10,8-10  → スピードlv10と跳躍lv10
#       エフェクトIDは、Wikiなどを参照してください。
#       <a href="http://ja.minecraftwiki.net/wiki/%E3%82%B9%E3%83%86%E3%83%BC%E3%82%BF%E3%82%B9%E5%8A%B9%E6%9E%9C">http://ja.minecraftwiki.net/wiki/%E3%82%B9%E3%83%86%E3%83%BC%E3%82%BF%E3%82%B9%E5%8A%B9%E6%9E%9C</a>
</pre></p>

設定例：<br>
<pre>
SkeletonMonk:
  mob: skeleton
  kit: 0,0,299,300,0
GreatZombie:
  mob: zombie
  kit: 267^16-5^17-5^18-5^19-2^20-2^34-3,306^0-4^1-4^3-4^4-4,307^0-4^1-4^3-4^4-4,308^0-4^1-4^3-4^4-4,309^0-4^1-4^3-4^4-4
  effect: 5-3
HighSpeedCreeper:
  mob: creeper
  effect: 1-10,8-10
</pre>

<h2>コマンド</h2>

コマンドは、OPならデフォルトで使用可能です。<br>
必要であれば、パーミッションノード enchantmobspawner を付与してください。<br>
<br>
/ems reload - config.yml を再読み込みする<br>
/ems list - 使用可能なプロファイル名を一覧を表示する<br>
/ems get (ProfileName) - プロファイル名の設定を持ったMobを生成するスポナーを取得する。<br>
/ems get (MobType) - skeleton, zombie, creeper などを指定可能。指定したMOBが生成される、普通のスポナーを取得する。<br>


<h2>スクリーンショット</h2>

<img src="2013-03-27_01.39.37.png" width="854" height="480" border="0" />

<h2>ソースコード</h2>

ソースコードは <a href="https://github.com/ucchyocean/EnchantMobSpawner">ここ</a> においています。<br>
ライセンスは GPLv3 を適用します。


<h2>更新履歴</h2>

v0.3 : 仕様追加。effectを指定可能に。<br>
v0.2 : EnchantMobSpawner から生成されたMOBは、倒されたときに装備品を落とさないように変更。<br>
v0.1 : 初回公開バージョン<br>

