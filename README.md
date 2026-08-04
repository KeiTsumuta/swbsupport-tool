# STAMP Workbench Support

## 概要
STAMP Workbenchを外部サポートするツールです。

<img src="https://github.com/KeiTsumuta/swbsupport-tool/blob/main/toolimage1.png">

本ツールは、STAMP Workbenchのプロジェクトファイルである「*.stmp」ファイルを読みとり、CA（Control Action）に関連するUCA（Unsafe Control Action）、UCAに関連するHCF(Hazard Causal Factor)、HCFに対応するシナリオと対策をツリー状に関連付けて指定した形式で出力するためのツールです。

出力形式は、以下に示す４種類です。
* Excelファイル
* マークダウン形式のテキストファイル
* CSV形式のテキストファイル
* タブ形式のテキストファイル

## 使用方法
### コンパイル、実行環境
Module Systemを使用した環境とし、JDK25以降となります。

### ビルド方法
```
mvn clean install
```

### 起動方法
Mavenを用いて起動する場合は以下となります。
```
mvn javafx:run
```
Javaの実行環境を直接用いて起動する場合は以下となります。
```
java --module-path target/lib;target/swbsupport-1.1.jar -m tmu.fs.swbs/tmu.fs.swbs.swbsupport.App
```
## Windowsインストーラによる実行パッケージの生成
Windowsインストーラは他の方法でも可能ですが、ここではJDKに付属したツール（jpackage）を用います。

実行パッケージを生成する際には、JDKとしてJavaFX（OpenJFX）が組み込まれたもの（例えば https://gluonhq.com/products/javafx/ 等）を使用してください。

生成コマンドの例を以下に示します。

```
jpackage --type msi --win-shortcut --name swbsupport --win-dir-chooser  --input "swsrun\lib" --dest "release" --main-jar swbsupport-1.1.jar --main-class "tmu.fs.swbs.swbsupport.App"
```
なお、この例では、swsrun/lib下のフォルダに本体（swbsupport-1.1.jar）とその依存するファイル（ビルドで生成されたtarget/libフォルダ下）のすべてをコピーして置いて置き、それを用いて作成しています。


## ライセンス

ソースコードのライセンスは「GPL-v3」です。

