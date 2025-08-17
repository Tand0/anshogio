package com.github.tand0.anshogio.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;


/** コマの移動に使うクラス（コマの移動先を得る） */
public class MoveKoma {
	private final int koma;
	private final ArrayList<XYFlag> xYFlag = new ArrayList<>();
	
	public MoveKoma(int koma) {
		this.koma = koma;
	}
	private void add(XYFlag xYFlag) {
		this.xYFlag.add(xYFlag);
	}
	public ArrayList<XYFlag> getXYFlag() {
		return this.xYFlag;
	}

	/**
	 * コマの種類からどこに移動するかのリストを得る。
	 */
	public static final Map<Integer,MoveKoma> moverKomaMap = 
			KomaMove.movers //moversのリスト
			.stream() // ストリームに変換
			.flatMap(mover->Arrays.stream(mover.koma).boxed()) // 駒のリスト
			.distinct() // コマのリストから重複を排除
			.map(komaX->new MoveKoma(komaX)) // コマの番号から駒用クラスに変換
			.peek(moveKoma->{ //中間処理を実行
				//
				// コマに移動先情報を追加
				KomaMove.movers.stream().forEach(mover-> {
					for (Integer targetKoma : mover.koma) {
						if (targetKoma != moveKoma.koma) {
							continue; // 目標の駒ではない
						}
						moveKoma.add(mover.xYFlag);
					}
				});
			})
			.collect(Collectors.toMap(moveKoma->moveKoma.koma, moveKoma-> moveKoma));

}
