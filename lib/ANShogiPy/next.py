import numpy as np
import tensorflow as tf
import psycopg
import os
import time
import sys

keras_model_path  = 'next_model'
tflite_model_path = 'tflite_model'
np_savez_path = 'np_savez_comp'
binal_flag = ''

#
# キーデータの分割
def split_data(my_data : str):
    data_list = []
    while True:
            if (len(my_data) == 0):
                break
            next = int(my_data[:1], 16)
            #
            # 4bitの1か0を生成する
            next4 = float(int(next/8)%2)
            next3 = float(int((next/4))%2)
            next2 = float(int((next/2))%2)
            next1 = float(int(next)%2)
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
        dataset_len = self.__len__()
        x = np.zeros(shape=(dataset_len,256), dtype = np.float32)
        y = np.zeros(shape=(dataset_len,1), dtype = np.float32)
        print("np end!")
        self.cursor.execute(f"SELECT * FROM keytable WHERE (win + loss) > {self.min_kyokumen};")
        self.query_result = self.cursor.fetchall()
        print("query end!")
        idx = 0
        old_time = 0
        now_time = time.time()
        for quest_one in self.query_result:
            key = quest_one[0]
            win = quest_one[1]
            loss = quest_one[2]
            data_list, result = self.getKey(key,win,loss)
            #
            # こちらを採用します！
            x[idx] = np.array([data_list], dtype=np.float32)
            y[idx] = result
            #
            # ↓は遅いのでリストラしました
            #if idx == 0:
            #    x = np.array([data_list], dtype=np.float32)
            #    y = np.array(result, dtype=np.float32)
            #else:
            #    x = np.append(x, [data_list], axis=0)
            #    y = np.append(y, result)
            #
            idx = idx + 1
            now_time = time.time()
            if ((old_time + 15) < now_time): # x秒以上経過したら
                old_time = now_time
                rate = int(idx/dataset_len * 100)
                time_string = time.strftime('%Y/%m/%d %H:%M:%S')
                print(f"{time_string} idx={idx}/{dataset_len}={rate}%")
        return (x,y)
        #
#
# 評価用に 16進数を2進に変更するポリシーかましたら動くか確認する
class IntMyDataset():
    def __init__(self):
        self.len = 1024
        #
        # 有効となる発生局面数(IntMyDatasetでは使わない)
        self.min_kyokumen = self.len
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

    def getitem_all(self):
        dataset_len = self.__len__()
        x = np.zeros(shape=(dataset_len,256), dtype = np.float32)
        y = np.zeros(shape=(dataset_len,1), dtype = np.float32)
        print("np end!")
        old_time = 0
        now_time = time.time()
        for idx in range(dataset_len):
            (data_list, result) = self.__getitem__(idx)
            #
            # こちらを採用します！
            x[idx] = np.array([data_list], dtype=np.float32)
            y[idx] = result
        return (x,y)
        #
#
# モデルの生成またはロード
def load_model():
    if os.path.isfile(keras_model_path + binal_flag + '.keras'):
        model = tf.keras.models.load_model(keras_model_path + binal_flag + '.keras')
    else:
        #
        model = tf.keras.Sequential(name="next_model")
        model.add(tf.keras.layers.Input(shape=(256,),name="input_model"))
        #
        # ここをreluにしたら精度が悪くなったのでsigmoidに変えた
        model.add(tf.keras.layers.Dense(units=1024, activation='sigmoid',name="sigmoid1_model"))
        model.add(tf.keras.layers.Dense(units=64, activation='tanh',name="sigmoid2_model"))
        #model.add(tf.keras.layers.Dense(units=16, activation='tanh',name="tanh_model"))
        model.add(tf.keras.layers.Dense(units=1,name="all_model"))
        model.compile(optimizer="adam", loss="mse", metrics=["mae"])
    return model

#
# メイン処理
def mainLoad():
    if binal_flag == '':
        dataset = CustomDataset("localhost","anshogio","postgres","postgres")
    else:
        dataset = IntMyDataset()
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
    np.savez_compressed(np_savez_path + binal_flag, x, y)
    #
    now_time = time.time()
    time_string = time.strftime('%Y/%m/%d %H:%M:%S')
    print(f"{time_string} end")

def mainEval():
    print(tf.version.VERSION)
    model = load_model()
    # データロード
    npz_comp = np.load(np_savez_path + binal_flag + ".npz")
    #
    # 繰り返し評価数
    epochs = 1
    #
    # loaderによって処理を変える
    if binal_flag == '':
        epochs = 30 # 本番
    else:
        epochs = 800 # テスト
    #
    model.fit(npz_comp['arr_0'], npz_comp['arr_1'], epochs=epochs)
    # データを保存します
    print(f"data saving start")
    model.save(keras_model_path + binal_flag + '.keras')
    print(f"data saving end")
    #
    # save for .pb file
    model.export(keras_model_path + binal_flag)
    #
    # TensorFlow のデータを lite に変更します。
    #print(f"data convert start")
    #converter = tf.lite.TFLiteConverter.from_keras_model(model)
    #converter.experimental_new_converter = True
    #tflite_model = converter.convert()
    #print(f"data convert end")
    #print(f"data convert saving start")
    #with open(tflite_model_path + binal_flag + ".tflite", 'wb') as o:
    #    o.write(tflite_model)
    #print(f"data convert saving end")


def mainTest():
    model = load_model()
    # (テスト)計算予想の行です
    for idx in range(16):
        data_list = split_data(format(idx, '064x'))
        a = np.array([data_list])
        b = np.array([idx])
        predictions = model(a)
        print(f"a={a}, b={b} predeiction={predictions[0]}")

if __name__ == "__main__":
    if 2 <= len(sys.argv):
        if sys.argv[1] == "load":
            mainLoad()
        elif sys.argv[1] == "eval":
            mainEval()
        elif sys.argv[1] == "load_binal":
            binal_flag = '_binal'
            mainLoad()
        elif sys.argv[1] == "eval_binal":
            binal_flag = '_binal'
            mainEval()            
        elif sys.argv[1] == "test_binal":
            binal_flag = '_binal'
            mainTest()
        else:
            print(f"Command error c={sys.argv[1]}")
            #
    else:
        print('Arguments are too short')
#