# ANShyogiO

- 将棋ソフトをJavaなどで作ってみました。
- 対戦できるように資料を作ります。

## JavaDoc

- [ANShogiOのJavaDoc](./javadoc/index.html)



## より詳細なドキュメント

- [ANShogiOのドキュメンテーション](https://amzn.to/4n1xsUR)

## 動かすにはどうすれば良いか？

- postgres をインストールする
- python/flutter/java をいい感じにインストールする
- pip3 で以下をインストールする
    - numpy
    - tensorflow
    - psycopg
    - fastapi
    - uvicorn
- lib/ANShogiF に移動し、以下を実行する。
    - flutter build web --base-href "/flutter/"
- lib/setting.json の設定を変更する。
- gradle を使って必要な Javaのライブラリを入手する。
- Java を実行する。
    - com.github.tand0.anshogio.ANShogiO
- 以下にアクセスし /download を実行する(対戦データを Floodgate からダウンロードします)。
    - http://localhost:8080/flutter
- 以下にアクセスし /postgres を実行する(ダウンロードした対戦データを postgres に保存します)。
    - http://localhost:8080/flutter
- lib/ANSiblePy に移動し、以下を実行する。
    - `python next.py load`
        - postgresに保存したデータを、NumPy(*.npz)で保存します。。
    - `python next.py eval`
        - NumPyで保存したデータを、TensorFlowの学習済データ(*.keras)で保存します。
    - `python next_fastapi.py`
        - サーバとして動かします。
- 以下にアクセスし /server を実行する。
    - http://localhost:8080/flutter
- 以下にアクセスし /connectOne を実行する。
    - http://localhost:8080/flutter
- 対戦ができる。

ね。簡単でしょ？(自分で書いててひどいな……。 将来的には ansible 化しないと……)



