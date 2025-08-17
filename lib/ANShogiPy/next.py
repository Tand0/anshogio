import numpy as np
import tensorflow as tf
import psycopg
import os
import time
import sys

keras_model_path = 'next_model.keras'
np_savez_comp = 'np_savez_comp'

#
# キーデータの分割
def split_data(my_data : str):
    data_list = []
    while True:
            if (len(my_data) == 0):
                break
            next = int(my_data[:1], 16)
            #
            next4 = int(next/8)%2 != 0
            next3 = int((next/4))%2 != 0
            next2 = int((next/2))%2 != 0
            next1 = int(next)%2 != 0
            data_list = data_list + [next4,next3,next2,next1]
            my_data = my_data[1:]
            # print(f"data_list={data_list} size={len(data_list)}")
    return data_list

#
# テーブルから表を読み込んで評価値を返す
class CustomDataset():
    def __init__(self, host, dbname, user, password):
        #
        # 有効となる発生局面数
        self.min_kyokumen = 5
        #
        # データベースとのコネクションを確立します。
        string = f"host={host} dbname={dbname} user={user} password={password}"
        #print(string)
        #self.connection = psycopg.connect("host=localhost dbname=anshogio user=postgres password=postgres")
        self.connection = psycopg.connect(string)
        #
        # カーソルをオープンします
        self.cursor = self.connection.cursor()
        #
    def __len__(self):
        # データセットのサイズを返す
        self.cursor.execute(f"SELECT count(*) FROM keytable WHERE (win + loss) > {self.min_kyokumen};")
        self.query_result = self.cursor.fetchall()
        #print(type(self.query_result[0][0]))
        return self.query_result[0][0]

    
    def getKey(self, key : str ,win : int,loss :int):
        ans = 0.0
        if ((win + loss) == 0):
            ans = 0.0
        else:
            ans = ((win / (win + loss)) - 0.5) * 2.0
        #
        data_list = split_data(key) #文字列に変換後に2進数に変換
        return (data_list, ans)

    def __getitem__(self, idx):
        self.cursor.execute(f"SELECT * FROM keytable WHERE (win + loss) > {self.min_kyokumen} LIMIT 1 OFFSET {idx} ;")
        self.query_result = self.cursor.fetchall()
        key = self.query_result[0][0]
        win = self.query_result[0][1]
        loss = self.query_result[0][2]
        #
        return self.getKey(key,win,loss)

    def getitem_all(self):
        self.cursor.execute(f"SELECT * FROM keytable WHERE (win + loss) > {self.min_kyokumen};")
        self.query_result = self.cursor.fetchall()
        idx = 0
        for quest_one in self.query_result:
            key = quest_one[0]
            win = quest_one[1]
            loss = quest_one[2]
            data_list, result = self.getKey(key,win,loss)
            if idx == 0:
                x = np.array([data_list], dtype=np.bool)
                y = np.array(result, dtype=np.float32)
            else:
                x = np.append(x, [data_list], axis=0)
                y = np.append(y, result)
            idx = idx + 1
        return (x,y)
        #
#
# 評価用に 16進数を2進に変更するポリシーかましたら動くか確認する
class IntMyDataset():
    def __init__(self):
        self.len = 16
        #

    def __len__(self):
        # データセットのサイズを返す
        return self.len

    def getIndexToKey(self, idx: int) -> str:
        return str(format(idx, '064x'))

    def __getitem__(self, idx):
        #
        idx = idx % self.len
        data_list = split_data(self.getIndexToKey(idx)) #文字列に変換後に2進数に変換
        #
        return (data_list, idx)

#
# モデルの生成またはロード
if os.path.isfile(keras_model_path):
    model = tf.keras.models.load_model(keras_model_path)
else:
    model = tf.keras.Sequential(name="next_model")
    model.add(tf.keras.layers.Input(shape=(256,),name="input_model"))
    model.add(tf.keras.layers.Dense(units=1024, activation='sigmoid',name="sigmoid_model"))
    model.add(tf.keras.layers.Dense(units=64, activation='tanh',name="tanh_model"))
    model.add(tf.keras.layers.Dense(units=1,name="all_model"))
    model.compile(optimizer="adam", loss="mse", metrics=["mae"])
#
# メイン処理
def main2():
    dataset = CustomDataset("localhost","anshogio","postgres","postgres")
    dataset_len = dataset.__len__()
    old_time = 0
    now_time = time.time()
    idx = 0
    #
    while idx < dataset_len:
        (data_list, result) = dataset.__getitem__(idx)
        if idx == 0: # 最初はxyをクリアする(0をbach_stepにすると毎回クリアされるが…)
            x = np.array([data_list], dtype=np.bool)
            y = np.array(result, dtype=np.float32)
        else:
            x = np.append(x, [data_list], axis=0)
            y = np.append(y, result)
        #
        # インデックス加算
        idx = idx + 1
        #
        # 時刻更新
        now_time = time.time()
        #
        # 時間表示
        if ((old_time + 60) < now_time): # x秒以上経過したら
            # 表示に使用する
            rate = int(idx/dataset_len * 100)
            old_time = now_time
            time_string = time.strftime('%Y/%m/%d %H:%M:%S')
            print(f"{time_string} rate={idx}/{dataset_len}({rate}%)")
    # 保存
    np.savez_compressed(np_savez_comp, x, y)

#
# メイン処理
def main3():
    dataset = CustomDataset("localhost","anshogio","postgres","postgres")
    dataset_len = dataset.__len__()
    #
    # 時刻表示
    now_time = time.time()
    time_string = time.strftime('%Y/%m/%d %H:%M:%S')
    print(f"{time_string} start len={dataset_len} kyokumen={dataset.min_kyokumen}")
    #
    # 取得
    x,y = dataset.getitem_all()
    # 保存
    np.savez_compressed(np_savez_comp, x, y)
    #
    now_time = time.time()
    time_string = time.strftime('%Y/%m/%d %H:%M:%S')
    print(f"{time_string} end")

def main4():
    # データロード
    npz_comp = np.load(np_savez_comp + ".npz")
    # 処理
    model.fit(npz_comp['arr_0'], npz_comp['arr_1'], epochs=200)
    # データを保存します
    print(f"data saving")
    #tf.saved_model.save(model, keras_model_path)
    model.save(keras_model_path)


#
# メイン処理
def main():
    #
    # 初期時間の設定
    old_time = 0
    end_time = time.time() + (2* 60 * 60) # 2時間経過したら
    #
    # バッチの長さ
    bach_step = 0
    #
    # データセットを用意する
    #dataset = IntMyDataset()
    dataset = CustomDataset("localhost","anshogio","postgres","postgres")
    dataset_len = dataset.__len__()
    print(f"DataSet len={dataset_len}")
    #
    for epoch in range(10000):
        #
        # 作業用のデータを作成する
        idx = bach_step
        now_time = time.time()
        epo_time = now_time
        #
        # 最大まで行ったときはクリアする
        if dataset_len <= idx:
            idx = 0
            bach_step = 0
        #
        while idx < dataset_len:
            (data_list, result) = dataset.__getitem__(idx)
            if idx == 0: # 最初はxyをクリアする(0をbach_stepにすると毎回クリアされるが…)
                x = np.array([data_list], dtype=np.bool)
                y = np.array(result, dtype=np.float32)
            else:
                x = np.append(x, [data_list], axis=0)
                y = np.append(y, result)
            #
            # インデックス加算
            idx = idx + 1
            #
            # 時刻更新
            now_time = time.time()
            #
            if ((old_time + 30) < now_time): # x秒以上経過したら
                # 表示に使用する
                rate = int(idx/dataset_len * 100)
                old_time = now_time
                time_string = time.strftime('%Y/%m/%d %H:%M:%S')
                print(f"{time_string} epoch={epoch} rate={idx}/{dataset_len}({rate}%)")
            #
            if ((epo_time + (5 * 60)) < now_time): # x秒以上経過したら一端終了する
                epo_time = now_time
                bach_step = idx
                break
            #
            if (end_time < now_time): # 時間経過したら//終わる
                bach_step = idx
                break
        #
        # 学習する
        model.fit(x, y, epochs=400)
        #
        if (end_time < now_time): # 時間経過したら//終わる
           break
        #
    #
    # データを保存します
    print(f"data saving")
    #tf.saved_model.save(model, keras_model_path)
    model.save(keras_model_path)
    #
    # (テスト)計算予想の行です
    a = np.array([x[5]])
    b = np.array([y[5]])
    predictions = model(a)
    print(f"a={a}, b={b} predeiction={predictions[0]}")



if __name__ == "__main__":
    if 2 <= len(sys.argv):
        if sys.argv[1] == "load":
            main3()
        elif sys.argv[1] == "eval":
            main4()    
        else:
            print('Command error')
    else:
        print('Arguments are too short')
#