#
# はじめはこれを使っていたが、リストラされた関数置き場
#
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
        (data_list, result)  = dataset.__getitem__(idx)
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
    model.save(keras_model_path + '.keras')
    #
    # (テスト)計算予想の行です
    a = np.array([x[5]])
    b = np.array([y[5]])
    predictions = model(a)
    print(f"a={a}, b={b} predeiction={predictions[0]}")

#
