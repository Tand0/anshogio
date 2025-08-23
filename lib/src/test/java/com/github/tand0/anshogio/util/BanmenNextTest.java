package com.github.tand0.anshogio.util;

import static com.github.tand0.anshogio.util.BanmenDefine.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 盤面用のテスト
 */
class BanmenNextTest {
    /** ログ */
	private final static Logger logger = LoggerFactory.getLogger(BanmenNextTest.class);

	/** コンストラクタ */
	public BanmenNextTest() {
	}
	/** 先手のコマの移動が正しいか確認する */
	@Test
	public void komaSenteTest() {
		//
        find(pP, 5 - 1, 5 - 1,"+5554FU","+1112OU","+1121OU","+1122OU");
        find(pP, 5 - 1, 4 - 1,"+5453TO","+1112OU","+1121OU","+1122OU");
        find(pP, 5 - 1, 3 - 1,"+5352TO","+1112OU","+1121OU","+1122OU");
        find(pP, 5 - 1, 2 - 1,"+5251TO","+1112OU","+1121OU","+1122OU");
        //
        find(pL, 5 - 1, 2 - 1,"+5251NY","+1112OU","+1121OU","+1122OU");
        find(pL, 5 - 1, 3 - 1,"+5351NY","+5352NY","+1112OU","+1121OU","+1122OU");
        find(pL, 5 - 1, 4 - 1,"+5451NY","+5452NY","+5453KY","+5453NY","+1112OU","+1121OU","+1122OU");
        find(pL, 5 - 1, 9 - 1,"+5951NY","+5952NY","+5953NY","+5953KY","+5954KY","+5955KY","+5956KY","+5957KY","+5958KY","+1112OU","+1121OU","+1122OU");
        //
        find(pN, 5 - 1, 3 - 1,"+5341NK","+5361NK","+1112OU","+1121OU","+1122OU");
        find(pN, 5 - 1, 4 - 1,"+5442NK","+5462NK","+1112OU","+1121OU","+1122OU");
        find(pN, 5 - 1, 5 - 1,"+5543KE","+5543NK","+5563KE","+5563NK","+1112OU","+1121OU","+1122OU");
        find(pN, 5 - 1, 6 - 1,"+5644KE","+5664KE","+1112OU","+1121OU","+1122OU");
        //
        find(pS, 5 - 1, 2 - 1,"+5241GI","+5241NG","+5243GI","+5243NG","+5251GI","+5251NG","+5261GI","+5261NG","+5263GI","+5263NG","+1112OU","+1121OU","+1122OU");
        find(pS, 5 - 1, 3 - 1,"+5342GI","+5342NG","+5344GI","+5344NG","+5352GI","+5352NG","+5362GI","+5362NG","+5364GI","+5364NG","+1112OU","+1121OU","+1122OU");
        find(pS, 5 - 1, 4 - 1,"+5443GI","+5443NG","+5445GI","+5453GI","+5453NG","+5463GI","+5463NG","+5465GI","+1112OU","+1121OU","+1122OU");
        find(pS, 5 - 1, 5 - 1,"+5544GI","+5546GI","+5554GI","+5564GI","+5566GI","+1112OU","+1121OU","+1122OU");
        //
        find(pG, 5 - 1, 2 - 1,"+5241KI","+5242KI","+5251KI","+5253KI","+5261KI","+5262KI","+1112OU","+1121OU","+1122OU");
        find(pG, 5 - 1, 3 - 1,"+5342KI","+5343KI","+5352KI","+5354KI","+5362KI","+5363KI","+1112OU","+1121OU","+1122OU");
        find(pG, 5 - 1, 4 - 1,"+5443KI","+5444KI","+5453KI","+5455KI","+5463KI","+5464KI","+1112OU","+1121OU","+1122OU");
        find(pG, 5 - 1, 5 - 1,"+5544KI","+5545KI","+5554KI","+5556KI","+5564KI","+5565KI","+1112OU","+1121OU","+1122OU");
        //
        find(pB, 5 - 1, 2 - 1,"+5216UM","+5225UM","+5234UM","+5241UM","+5243UM","+5261UM","+5263UM","+5274UM","+5285UM","+5296UM","+1112OU","+1121OU","+1122OU");
        find(pB, 5 - 1, 3 - 1,"+5317UM","+5326UM","+5331UM","+5335UM","+5342UM","+5344UM","+5362UM","+5364UM","+5371UM","+5375UM","+5386UM","+5397UM","+1112OU","+1121OU","+1122OU");
        find(pB, 5 - 1, 4 - 1,"+5418KA","+5421UM","+5427KA","+5432UM","+5436KA","+5443UM","+5445KA","+5463UM","+5465KA","+5472UM","+5476KA","+5481UM","+5487KA","+5498KA","+1112OU","+1121OU","+1122OU");
        find(pB, 5 - 1, 5 - 1,"+5519KA","+5522UM","+5528KA","+5533UM","+5537KA","+5544KA","+5546KA","+5564KA","+5566KA","+5573UM","+5577KA","+5582UM","+5591UM","+5588KA","+1112OU","+1121OU","+1122OU");
        //
        find(pR, 5 - 1, 2 - 1,"+5212RY","+5222RY","+5232RY","+5242RY","+5251RY","+5253RY","+5254RY","+5255RY","+5256RY","+5257RY","+5258RY","+5259RY","+5262RY","+5272RY","+5282RY","+5292RY","+1112OU","+1121OU","+1122OU");
        find(pR, 5 - 1, 3 - 1,"+5313RY","+5323RY","+5333RY","+5343RY","+5351RY","+5352RY","+5354RY","+5355RY","+5356RY","+5357RY","+5358RY","+5359RY","+5363RY","+5373RY","+5383RY","+5393RY","+1112OU","+1121OU","+1122OU");
        find(pR, 5 - 1, 4 - 1,"+5414HI","+5424HI","+5434HI","+5444HI","+5451RY","+5452RY","+5453RY","+5455HI","+5456HI","+5457HI","+5458HI","+5459HI","+5464HI","+5474HI","+5484HI","+5494HI","+1112OU","+1121OU","+1122OU");
        find(pR, 5 - 1, 5 - 1,"+5515HI","+5525HI","+5535HI","+5545HI","+5551RY","+5552RY","+5553RY","+5554HI","+5556HI","+5557HI","+5558HI","+5559HI","+5565HI","+5575HI","+5585HI","+5595HI","+1112OU","+1121OU","+1122OU");
        //
        find(pK, 5 - 1, 3 - 1,"+5342OU","+5343OU","+5344OU","+5352OU","+5354OU","+5362OU","+5363OU","+5364OU");
        //
        find(ppP, 5 - 1, 3 - 1,"+5342TO","+5343TO","+5352TO","+5354TO","+5362TO","+5363TO","+1112OU","+1121OU","+1122OU");
        find(ppN, 5 - 1, 3 - 1,"+5342NK","+5343NK","+5352NK","+5354NK","+5362NK","+5363NK","+1112OU","+1121OU","+1122OU");
        find(ppL, 5 - 1, 3 - 1,"+5342NY","+5343NY","+5352NY","+5354NY","+5362NY","+5363NY","+1112OU","+1121OU","+1122OU");
        find(ppS, 5 - 1, 3 - 1,"+5342NG","+5343NG","+5352NG","+5354NG","+5362NG","+5363NG","+1112OU","+1121OU","+1122OU");
        find(ppB, 5 - 1, 3 - 1,"+5317UM","+5326UM","+5331UM","+5335UM","+5342UM","+5343UM","+5344UM","+5352UM","+5354UM","+5362UM","+5363UM","+5364UM","+5371UM","+5375UM","+5386UM","+5397UM","+1112OU","+1121OU","+1122OU");
        find(ppR, 5 - 1, 3 - 1,"+5313RY","+5323RY","+5333RY","+5342RY","+5343RY","+5344RY","+5351RY","+5352RY","+5354RY","+5355RY","+5356RY","+5357RY","+5358RY","+5359RY","+5362RY","+5363RY","+5364RY","+5373RY","+5383RY","+5393RY","+1112OU","+1121OU","+1122OU");
	}
	
    /** 後手のコマの移動が正しいか確認する */
	@Test
	public void komaGoteTest() {
		//
        find(pp, 5 - 1, 5 - 1,"-5556FU","-9989OU","-9988OU","-9998OU");
        find(pp, 5 - 1, 6 - 1,"-5657TO","-9989OU","-9988OU","-9998OU");
        find(pp, 5 - 1, 7 - 1,"-5758TO","-9989OU","-9988OU","-9998OU");
        find(pp, 5 - 1, 8 - 1,"-5859TO","-9989OU","-9988OU","-9998OU");
        //
        find(pl, 5 - 1, 7 - 1,"-5758NY","-5759NY","-9989OU","-9988OU","-9998OU");
        find(pl, 5 - 1, 6 - 1,"-5657KY","-5657NY","-5658NY","-5659NY","-9989OU","-9988OU","-9998OU");
        find(pl, 5 - 1, 5 - 1,"-5556KY","-5557KY","-5557NY","-5558NY","-5559NY","-9989OU","-9988OU","-9998OU");
        find(pl, 5 - 1, 1 - 1,"-5152KY","-5153KY","-5154KY","-5155KY","-5156KY","-5157KY","-5157NY","-5158NY","-5159NY","-9989OU","-9988OU","-9998OU");
        //
        find(pn, 5 - 1, 7 - 1,"-5749NK","-5769NK","-9989OU","-9988OU","-9998OU");
        find(pn, 5 - 1, 6 - 1,"-5648NK","-5668NK","-9989OU","-9988OU","-9998OU");
        find(pn, 5 - 1, 5 - 1,"-5547KE","-5547NK","-5567KE","-5567NK","-9989OU","-9988OU","-9998OU");
        find(pn, 5 - 1, 4 - 1,"-5446KE","-5466KE","-9989OU","-9988OU","-9998OU");
        //
        find(ps, 5 - 1, 8 - 1,"-5847GI","-5847NG","-5849GI","-5849NG","-5859GI","-5859NG","-5867GI","-5867NG","-5869GI","-5869NG","-9989OU","-9988OU","-9998OU");
        find(ps, 5 - 1, 7 - 1,"-5746GI","-5746NG","-5748GI","-5748NG","-5758GI","-5758NG","-5766GI","-5766NG","-5768GI","-5768NG","-9989OU","-9988OU","-9998OU");
        find(ps, 5 - 1, 6 - 1,"-5645GI","-5647GI","-5647NG","-5657GI","-5657NG","-5665GI","-5667GI","-5667NG","-9989OU","-9988OU","-9998OU");
        find(ps, 5 - 1, 5 - 1,"-5544GI","-5546GI","-5556GI","-5564GI","-5566GI","-9989OU","-9988OU","-9998OU");
        //
        find(pg, 5 - 1, 8 - 1,"-5848KI","-5849KI","-5857KI","-5859KI","-5868KI","-5869KI","-9989OU","-9988OU","-9998OU");
        find(pg, 5 - 1, 5 - 1,"-5545KI","-5546KI","-5554KI","-5556KI","-5565KI","-5566KI","-9989OU","-9988OU","-9998OU");
        //
        find(pb, 5 - 1, 8 - 1,"-5814UM","-5825UM","-5836UM","-5847UM","-5849UM","-5867UM","-5869UM","-5876UM","-5885UM","-5894UM","-9989OU","-9988OU","-9998OU");
        find(pb, 5 - 1, 7 - 1,"-5713UM","-5724UM","-5735UM","-5739UM","-5746UM","-5748UM","-5766UM","-5768UM","-5775UM","-5779UM","-5784UM","-5793UM","-9989OU","-9988OU","-9998OU");
        find(pb, 5 - 1, 6 - 1,"-5612KA","-5623KA","-5629UM","-5634KA","-5638UM","-5645KA","-5647UM","-5665KA","-5667UM","-5674KA","-5678UM","-5683KA","-5689UM","-5692KA","-9989OU","-9988OU","-9998OU");
        find(pb, 5 - 1, 5 - 1,"-5519UM","-5522KA","-5528UM","-5533KA","-5537UM","-5544KA","-5546KA","-5564KA","-5566KA","-5573KA","-5577UM","-5582KA","-5588UM","-5591KA","-9989OU","-9988OU","-9998OU");
        //
        find(pr, 5 - 1, 8 - 1,"-5818RY","-5828RY","-5838RY","-5848RY","-5851RY","-5852RY","-5853RY","-5854RY","-5855RY","-5856RY","-5857RY","-5859RY","-5868RY","-5878RY","-5888RY","-5898RY","-9989OU","-9988OU","-9998OU");
        find(pr, 5 - 1, 7 - 1,"-5717RY","-5727RY","-5737RY","-5747RY","-5751RY","-5752RY","-5753RY","-5754RY","-5755RY","-5756RY","-5758RY","-5759RY","-5767RY","-5777RY","-5787RY","-5797RY","-9989OU","-9988OU","-9998OU");
        find(pr, 5 - 1, 6 - 1,"-5616HI","-5626HI","-5636HI","-5646HI","-5651HI","-5652HI","-5653HI","-5654HI","-5655HI","-5657RY","-5658RY","-5659RY","-5666HI","-5676HI","-5686HI","-5696HI","-9989OU","-9988OU","-9998OU");
        find(pr, 5 - 1, 5 - 1,"-5515HI","-5525HI","-5535HI","-5545HI","-5551HI","-5552HI","-5553HI","-5554HI","-5556HI","-5557RY","-5558RY","-5559RY","-5565HI","-5575HI","-5585HI","-5595HI","-9989OU","-9988OU","-9998OU");
        //
        find((byte)(NARI|pp), 5 - 1, 7 - 1,"-5747TO","-5748TO","-5756TO","-5758TO","-5767TO","-5768TO","-9989OU","-9988OU","-9998OU");
        find((byte)(NARI|pn), 5 - 1, 7 - 1,"-5747NK","-5748NK","-5756NK","-5758NK","-5767NK","-5768NK","-9989OU","-9988OU","-9998OU");
        find((byte)(NARI|pl), 5 - 1, 7 - 1,"-5747NY","-5748NY","-5756NY","-5758NY","-5767NY","-5768NY","-9989OU","-9988OU","-9998OU");
        find((byte)(NARI|ps), 5 - 1, 7 - 1,"-5747NG","-5748NG","-5756NG","-5758NG","-5767NG","-5768NG","-9989OU","-9988OU","-9998OU");
        find((byte)(NARI|pb), 5 - 1, 7 - 1,"-5713UM","-5724UM","-5735UM","-5739UM","-5746UM","-5747UM","-5748UM","-5756UM","-5758UM","-5766UM","-5767UM","-5768UM","-5775UM","-5779UM","-5784UM","-5793UM","-9989OU","-9988OU","-9998OU");
        find((byte)(NARI|pr), 5 - 1, 7 - 1,"-5717RY","-5727RY","-5737RY","-5746RY","-5747RY","-5748RY","-5751RY","-5752RY","-5753RY","-5754RY","-5755RY","-5756RY","-5758RY","-5759RY","-5766RY","-5767RY","-5768RY","-5777RY","-5787RY","-5797RY","-9989OU","-9988OU","-9998OU");
	}

	/** 王が８方向に動くことができるか */
	@Test
	public void komaGotePkTest() {
        //
        find(pK, 5 - 1, 7 - 1,"+5746OU","+5747OU","+5748OU","+5756OU","+5758OU","+5766OU","+5767OU","+5768OU");		
        //
        find(pk, 5 - 1, 7 - 1,"-5746OU","-5747OU","-5748OU","-5756OU","-5758OU","-5766OU","-5767OU","-5768OU");		
	}
	

    /**
     *  該当の設定があるか確認する
     * @param koma コマ
     * @param x コマのx
     * @param y コマのy
     * @param teStrings 移動先のリスト
     */
	private void find(byte koma,int x,int y,String... teStrings) {
		BanmenFactory factory = new BanmenFactory();

		// 初期情報を作成する
		BanmenOnly b = new BanmenOnly();
		logger.debug(b.toString());
		b.clearForCSAProtocol();// 盤面を全消しする
        b.setTegoma(koma, 1, Math.max(0, b.getTegoma(koma, 1) - 1)); // 後手から１枚とって
		b.setKoma(koma, x, y); // 盤面に置く
		logger.debug(b.toString());
		if ((koma & ENEMY) != 0) {
			b.setTeban(1);//後手
	        // 先手にコマを寄せる
	        b.setTegoma(BanmenDefine.pP, 0, b.getTegoma(BanmenDefine.pP,1));
	        b.setTegoma(BanmenDefine.pL, 0, b.getTegoma(BanmenDefine.pL,1));
	        b.setTegoma(BanmenDefine.pN, 0, b.getTegoma(BanmenDefine.pN,1));
	        b.setTegoma(BanmenDefine.pS, 0, b.getTegoma(BanmenDefine.pS,1));
	        b.setTegoma(BanmenDefine.pG, 0, b.getTegoma(BanmenDefine.pG,1));
	        b.setTegoma(BanmenDefine.pB, 0, b.getTegoma(BanmenDefine.pB,1));
	        b.setTegoma(BanmenDefine.pR, 0, b.getTegoma(BanmenDefine.pR,1));
	        //
	        b.setTegoma(BanmenDefine.pP, 1, 0);
	        b.setTegoma(BanmenDefine.pL, 1, 0);
	        b.setTegoma(BanmenDefine.pN, 1, 0);
	        b.setTegoma(BanmenDefine.pS, 1, 0);
	        b.setTegoma(BanmenDefine.pG, 1, 0);
	        b.setTegoma(BanmenDefine.pB, 1, 0);
	        b.setTegoma(BanmenDefine.pR, 1, 0);
	        
		}
        b.endForCSAProtocol();// 王がいなかったら適当に置く
		logger.debug(b.toString());
		BanmenKey key = new BanmenKey(b);
		BanmenNext after = factory.create(null, key);
		//
		//
		List<ChildTeNext> childMap = after.getChild(factory);
		logger.debug(after.toString());
		for (ChildTeNext teNext : childMap) {
            int te = teNext.getTe();
            boolean flag = false;
            for (String teString : teStrings) {
            	int target = BanmenDefine.changeTeStringToInt(teString);
                if (te == target) {
                	flag = true;
                    break;
                }
            }
            if (!flag) {
            	String teString = BanmenDefine.changeTeIntToString(te);
                fail("NG key=" + getKomaToString(koma) + " x=" + (x+1) + " y=" + (y+1) + " te=" + teString);            	
            }
		}
        if (teStrings.length != childMap.size()) {
            fail("NG key=" + getKomaToString(koma) + " x=" + (x+1) + " y=" + (y+1)
            		+ " size=" + childMap.size() + " sum=" + teStrings.length);
        }
	}
	
	/**
	 * 7手詰み確認
	 */
	@Test
	void tumiTest() {
        BanmenFactory factory = new BanmenFactory();

        // 初期情報を作成する
	    BanmenKey topKey = new BanmenKey(new BanmenOnly());
		BanmenNext next = factory.create(null,topKey);
       //
		logger.debug(next.toString());
		next = next.decisionTe(factory,changeTeStringToInt("+2726FU"));
		logger.debug(next.toString());
		next = next.decisionTe(factory,changeTeStringToInt("-5142OU"));
		logger.debug(next.toString());
        next = next.decisionTe(factory,changeTeStringToInt("+2625FU"));
		logger.debug(next.toString());
        next = next.decisionTe(factory,changeTeStringToInt("-4232OU"));
		logger.debug(next.toString());
        next = next.decisionTe(factory,changeTeStringToInt("+2524FU"));
		logger.debug(next.toString());
        next = next.decisionTe(factory,changeTeStringToInt("-8242HI"));
		logger.debug(next.toString());
        next = next.decisionTe(factory,changeTeStringToInt("+2423TO"));
		logger.debug(next.toString());
        //
		List<ChildTeNext> child = next.getChild(factory);
        assertEquals(child.size(),0); // もう手はない
        //
		// 初期情報を作成する
        factory.clearAllHash();
		next = factory.create(null,topKey);
       //
		logger.debug(next.toString());
		next = next.decisionTe(factory,changeTeStringToInt("+5968OU"));
		logger.debug(next.toString());
		next = next.decisionTe(factory,changeTeStringToInt("-8384FU"));
		logger.debug(next.toString());
        next = next.decisionTe(factory,changeTeStringToInt("+6878OU"));
		logger.debug(next.toString());
        next = next.decisionTe(factory,changeTeStringToInt("-8485FU"));
		logger.debug(next.toString());
        next = next.decisionTe(factory,changeTeStringToInt("+2868HI"));
		logger.debug(next.toString());
        next = next.decisionTe(factory,changeTeStringToInt("-8586FU"));
		logger.debug(next.toString());
        next = next.decisionTe(factory,changeTeStringToInt("+9796FU"));
		logger.debug(next.toString());
        next = next.decisionTe(factory,changeTeStringToInt("-8687TO"));
		logger.debug(next.toString());
        //
		child = next.getChild(factory);
        assertEquals(child.size(),0); // もう手はない
        
		logger.debug("end");
	}


	/** メインのテスト。
	 * 盤面からいくつか動かして正しい動作をするか確認する
	 */
	@Test
	void mainTest() {
        BanmenFactory factory = new BanmenFactory();

        // 初期情報を作成する
	    BanmenKey topKey = new BanmenKey(new BanmenOnly());
		BanmenNext banmenNext = factory.create(null,topKey);
		BanmenOnly banmen = banmenNext.getMyKey().createBanmenOnly();
		logger.debug(banmen.toString());
		//
		logger.debug(banmenNext.getMyKey().createBanmenOnly().toString());
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+7776FU"));
		logger.debug(banmenNext.getMyKey().createBanmenOnly().toString());
		banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("-1112KY"));
		logger.debug(banmenNext.getMyKey().createBanmenOnly().toString());
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+2726FU"));
		logger.debug(banmenNext.getMyKey().createBanmenOnly().toString());
		List<ChildTeNext> child = banmenNext.getChild(factory);
		for (ChildTeNext teNext : child) {
			String sKey = changeTeIntToString(teNext.getTe());
            assertNotEquals(sKey,"-2112KA");
            assertNotEquals(sKey,"-2132KA");
            assertNotEquals(sKey,"-2132UM");
            assertNotEquals(sKey,"-2211UM");
        }
		logger.debug("end");
	}
	
	
	/** 初手で何を指せるかの確認 */
	@Test
	void shokiTest() {
        BanmenFactory factory = new BanmenFactory();

        for (int i = 0 ; i < 100 ; i++) {
			// 繰り返して問題ないか確認する
			//

			factory.clearAllHash();
		    BanmenKey topKey = new BanmenKey(new BanmenOnly());
			BanmenNext next = factory.create(null,topKey);
			//
			List<ChildTeNext> teNextList = next.getChild(factory);
			for (ChildTeNext teNext : teNextList) {
				logger.debug(BanmenDefine.changeTeIntToString(teNext.getTe()));
			}
			assertEquals(teNextList.size(), 30); // 30局面
		}
	}
	
	/** 成り関係のテストをする */
	@Test
	void hinariTest() {
        BanmenFactory factory = new BanmenFactory();

        // 初期情報を作成する
	    BanmenKey topKey = new BanmenKey(new BanmenOnly());
		BanmenNext banmenNext = factory.create(null,topKey);
		BanmenOnly banmen = banmenNext.getMyKey().createBanmenOnly();
		logger.debug(banmen.toString());
		//
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+2818HI"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("-8292HI"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+1716FU"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("-9394FU"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+1615FU"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("-9495FU"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+1514FU"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("-9596FU"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+1413TO"));
        //
        List<ChildTeNext> child;
		child = banmenNext.getChild(factory);
		for (ChildTeNext teNextList : child) {
			String sKey = changeTeIntToString(teNextList.getTe());
            assertNotEquals(sKey,"-0099FU");
            assertNotEquals(sKey,"+0011FU");
        }
        //
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("-9697TO"));
		child = banmenNext.getChild(factory);
		for (ChildTeNext teNextList : child) {
			String sKey = changeTeIntToString(teNextList.getTe());
            assertNotEquals(sKey,"-0099FU");
            assertNotEquals(sKey,"+0011FU");
        }
        //
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+0095FU"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("-0015FU"));
		logger.debug(banmenNext.getMyKey().createBanmenOnly().toString());
        //
		child = banmenNext.getChild(factory);
		for (ChildTeNext teNextList : child) {
			String sKey = changeTeIntToString(teNextList.getTe());
            assertNotEquals(sKey,"+1814RY");
            assertNotEquals(sKey,"+1815RY");
            assertNotEquals(sKey,"+1816RY");
            assertNotEquals(sKey,"+1817RY");
            assertNotEquals(sKey,"-9293RY");
            assertNotEquals(sKey,"-9294RY");
            assertNotEquals(sKey,"-9295RY");
            assertNotEquals(sKey,"-9296RY");
        }
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+1312TO"));
		logger.debug(banmenNext.getMyKey().createBanmenOnly().toString());
		child = banmenNext.getChild(factory);
		for (ChildTeNext teNextList : child) {
			String sKey = changeTeIntToString(teNextList.getTe());
            assertNotEquals(sKey,"+1814RY");
            assertNotEquals(sKey,"+1815RY");
            assertNotEquals(sKey,"+1816RY");
            assertNotEquals(sKey,"+1817RY");
            assertNotEquals(sKey,"-9293RY");
            assertNotEquals(sKey,"-9294RY");
            assertNotEquals(sKey,"-9295RY");
            assertNotEquals(sKey,"-9296RY");
        }
		logger.debug("end");
	}
	
	
	/** 利きなしテスト */
	@Test
	void testKiKiNaShi() {
        BanmenFactory factory = new BanmenFactory();

        // 初期情報を作成する
	    BanmenKey topKey = new BanmenKey(new BanmenOnly());
		BanmenNext banmenNext = factory.create(null,topKey);
		BanmenOnly banmen = banmenNext.getMyKey().createBanmenOnly();
		logger.debug(banmen.toString());
		//
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+1716FU"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("-9394FU"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+1615FU"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("-9495FU"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+1514FU"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("-9596FU"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+1413TO"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("-9697TO"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+1312TO"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("-9798TO"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+1211TO"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("-9899TO"));
        //
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+1121TO"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("-9989TO"));
		logger.debug(banmenNext.getMyKey().createBanmenOnly().toString());
		List<ChildTeNext> child;
		child = banmenNext.getChild(factory);
		for (ChildTeNext teNextList : child) {
			String sKey = changeTeIntToString(teNextList.getTe());
            assertNotEquals(sKey,"+0011FU");
            assertNotEquals(sKey,"+0011KY");
            assertNotEquals(sKey,"+0011KE");
            assertNotEquals(sKey,"+0012KE");
            assertNotEquals(sKey,"-0099FU");
            assertNotEquals(sKey,"-0099KY");
            assertNotEquals(sKey,"-0099KE");
            assertNotEquals(sKey,"-0098KE");
        }
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+2131TO"));
		logger.debug(banmenNext.getMyKey().createBanmenOnly().toString());
		child = banmenNext.getChild(factory);
		for (ChildTeNext teNextList : child) {
			String sKey = changeTeIntToString(teNextList.getTe());
            assertNotEquals(sKey,"+0011FU");
            assertNotEquals(sKey,"+0011KY");
            assertNotEquals(sKey,"+0011KE");
            assertNotEquals(sKey,"+0012KE");
            assertNotEquals(sKey,"-0099FU");
            assertNotEquals(sKey,"-0099KY");
            assertNotEquals(sKey,"-0099KE");
            assertNotEquals(sKey,"-0098KE");
        }
		logger.debug("end");
		
		
		
	}
	
	/** 利きなしテスト、その2 */
	@Test
	void testKiKiNaShi2() {
        BanmenFactory factory = new BanmenFactory();

        // 初期情報を作成する
	    BanmenKey topKey = new BanmenKey(new BanmenOnly());
		BanmenNext banmenNext = factory.create(null,topKey);
		BanmenOnly banmen = banmenNext.getMyKey().createBanmenOnly();
		logger.debug(banmen.toString());
		//
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+1716FU"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("-9394FU"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+1615FU"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("-9495FU"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+1514FU"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("-9596FU"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+1413TO"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("-9697TO"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+1312TO"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("-9798TO"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+1211TO"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("-9899TO"));
        //
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+1121TO"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("-9989TO"));
        //
		logger.debug(banmenNext.getMyKey().createBanmenOnly().toString());
		//
		List<ChildTeNext> child;
		child = banmenNext.getChild(factory);
		for (ChildTeNext teNextList : child) {
			String sKey = changeTeIntToString(teNextList.getTe());
            assertNotEquals(sKey,"+9991KY");
            assertNotEquals(sKey,"+1911KY");
        }
        //
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+0092FU"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("-0018FU"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+0094KE"));
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("-0016KE"));
        //
		logger.debug(banmenNext.getMyKey().createBanmenOnly().toString());
		child = banmenNext.getChild(factory);
		for (ChildTeNext teNextList : child) {
			String sKey = changeTeIntToString(teNextList.getTe());
            assertNotEquals(sKey,"+9291FU");
            assertNotEquals(sKey,"+1819FU");
            assertNotEquals(sKey,"+9482KE");
            assertNotEquals(sKey,"+1628KE");
        }
	}
	
	/** 手コマのテスト */
	@Test
	void tegomaTest() {
        BanmenFactory factory = new BanmenFactory();

        // 初期情報を作成する
	    BanmenKey topKey = new BanmenKey(new BanmenOnly());
		BanmenNext banmenNext = factory.create(null,topKey);
		BanmenOnly banmen = banmenNext.getMyKey().createBanmenOnly();
		logger.debug(banmen.toString());
		//
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+7776FU"));
		logger.debug(banmenNext.getMyKey().createBanmenOnly().toString());
        banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("-3334FU"));
		logger.debug(banmenNext.getMyKey().createBanmenOnly().toString());
        //
		assertEquals(0 , banmenNext.getMyKey().createBanmenOnly().getTegoma(pB, 0));
		assertEquals(0 , banmenNext.getMyKey().createBanmenOnly().getTegoma(pB, 0));
		banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+8822UM"));
		logger.debug(banmenNext.getMyKey().createBanmenOnly().toString());
		assertEquals(1 , banmenNext.getMyKey().createBanmenOnly().getTegoma(pB, 0));
		assertEquals(0 , banmenNext.getMyKey().createBanmenOnly().getTegoma(pB, 1));
        //
		banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("-3435FU"));
		assertEquals(0 , banmenNext.getMyKey().createBanmenOnly().getTegoma(pG, 0));
		assertEquals(0 , banmenNext.getMyKey().createBanmenOnly().getTegoma(pS, 0));
		banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+2231UM"));
		logger.debug(banmenNext.getMyKey().createBanmenOnly().toString());
		assertEquals(0 , banmenNext.getMyKey().createBanmenOnly().getTegoma(pG, 0));
		assertEquals(1 , banmenNext.getMyKey().createBanmenOnly().getTegoma(pS, 0));
		banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("-3536FU"));
		banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+3121UM"));
		//
        assertEquals(0 , banmenNext.getMyKey().createBanmenOnly().getTegoma(pP, 1));
		banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("-3637TO"));
		assertEquals(1 , banmenNext.getMyKey().createBanmenOnly().getTegoma(pP, 1));
		//
        assertEquals(0 , banmenNext.getMyKey().createBanmenOnly().getTegoma(pP, 0));
		banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+2143UM"));
        assertEquals(1 , banmenNext.getMyKey().createBanmenOnly().getTegoma(pP, 0));
		logger.debug(banmenNext.getMyKey().createBanmenOnly().toString());
		//
		assertEquals(0 , banmenNext.getMyKey().createBanmenOnly().getTegoma(pR, 1));
		banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("-3728TO"));
		assertEquals(1 , banmenNext.getMyKey().createBanmenOnly().getTegoma(pR, 1));
		logger.debug(banmenNext.getMyKey().createBanmenOnly().toString());
		//
        //
        assertEquals(1 , banmenNext.getMyKey().createBanmenOnly().getTegoma(pP, 0));
		banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+0042FU"));
        assertEquals(0 , banmenNext.getMyKey().createBanmenOnly().getTegoma(pP, 0));
        //
        assertEquals(1 , banmenNext.getMyKey().createBanmenOnly().getTegoma(pP, 1));
		banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("-0037FU"));
        assertEquals(0 , banmenNext.getMyKey().createBanmenOnly().getTegoma(pP, 1));
		logger.debug(banmenNext.getMyKey().createBanmenOnly().toString());
        //
        assertEquals(1 , banmenNext.getMyKey().createBanmenOnly().getTegoma(pS, 0));
		banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("+0021GI"));
        assertEquals(0 , banmenNext.getMyKey().createBanmenOnly().getTegoma(pS, 0));
		logger.debug(banmenNext.getMyKey().createBanmenOnly().toString());
        //
        assertEquals(1 , banmenNext.getMyKey().createBanmenOnly().getTegoma(pR, 1));
		banmenNext = banmenNext.decisionTe(factory,changeTeStringToInt("-0055HI"));
        assertEquals(0 , banmenNext.getMyKey().createBanmenOnly().getTegoma(pR, 1));
		logger.debug(banmenNext.getMyKey().createBanmenOnly().toString());
		//
	}
	
	
	/** 文字の手から手に変更することのテスト */
	@Test
	void changeTeStringToIntTest() {
        String moveString;
        int move;
        String result;
        //
        moveString = "+2315FU";
        move = changeTeStringToInt(moveString);
        logger.debug(String.format("0x%08x", move));
        result = changeTeIntToString(move);
        assertEquals(moveString,result);
        //
        moveString = "-2345OU";
        move = changeTeStringToInt(moveString);
        logger.debug(String.format("0x%08x", move));
        result = changeTeIntToString(move);
        assertEquals(moveString,result); 
        //
        moveString = "-0067KI";
        move = changeTeStringToInt(moveString);
        logger.debug(String.format("0x%08x", move));
        result = changeTeIntToString(move);
        assertEquals(moveString,result);
	}
	
	/** 子作りのテスト */
	@Test
	void createNextListTest() {
        BanmenFactory factory = new BanmenFactory();

        logger.debug("testCreateNextList start");
		// 初期情報を作成する
        BanmenKey topKey = new BanmenKey(new BanmenOnly());
		BanmenNext banmenNext = factory.create(null,topKey);
		BanmenOnly banmen = banmenNext.getMyKey().createBanmenOnly();
		logger.debug(banmen.toString());
		//
		List<ChildTeNext> child;
		child = banmenNext.getChild(factory);
		assertEquals(child.size(),30); // 初期盤面の合法手
        //
		BanmenNext nextEntry = banmenNext.decisionTe(factory,changeTeStringToInt("+7776FU"));
		logger.debug(nextEntry.toString());
		child = nextEntry.getChild(factory);
		for (ChildTeNext x : child) {
			logger.debug("  " + BanmenDefine.changeTeIntToString(x.getTe()));
		}
		assertEquals(child.size(),30); // 初期盤面の合法手
		//
        logger.debug("testCreateNextList end");
	}
	
	/** 次の手を指せるか */
	@Test
	void keyTest() {
	    BanmenFactory factory = new BanmenFactory();

		factory.clearAllHash();//一回消す
        String[] ansList = {
                "+7776FU",
                "-3334FU",
                "+8822UM"};
        sasu(ansList);

	      // 打ち込み系に問題がありそうか？
		String[] ansList0 = {
                "+3938GI",
                "-1112KY",
                "+4939KI",
                "-2211KA",
                "+3949KI",
                "-1314FU",
                "+4939KI",
                "-2113KE",
                "+2726FU",
                "-1415FU",
                "+2625FU",
                "-1122KA",
                "+2524FU",
                "-2211KA",
                "+2423TO"};
        sasu(ansList0);
        
		// 打ち込み系に問題がありそうか？
		String[] ansList1 = {
				"+7776FU", "-8384FU", "+8833UM", "-2133KE"};
		sasu(ansList1);
		
		// 打ち込み系に問題がありそうか？
		String[] ansList2 = {
				"+7776FU", "-8384FU", "+6978KI", "-8485FU", "+8877KA", "-3334FU", 
				"+7968GI", "-4132KI", "+2726FU", "-4344FU", "+3948GI", "-3142GI",
				"+2625FU", "-2233KA", "+3736FU", "-7162GI", "+5969OU", "-4243GI",
				"+4837GI", "-6152KI", "+6979OU", "-5354FU", "+3746GI", "-7374FU",
				"+3635FU", "-8173KE", "+4959KI", "-5142OU", "+1716FU", "-4445FU",
				"+4645GI", "-3435FU", "+4746FU", "-8281HI", "+4556GI", "-9394FU",
				"+7675FU", "-8184HI", "+7733UM", "-2133KE", "+0061KA", "-8586FU",
				"+8786FU", "-8486HI", "+0087FU", "-8682HI", "+8977KE", "-0044FU",
				"+0034FU"};
		sasu(ansList2);

        
		String[] ansList3 = {"+7776FU", "-8384FU", "+6978KI", "-3334FU", "+2726FU", "-8485FU", "+2625FU", "-4132KI", "+2524FU", "-2324FU", "+2824HI", "-8586FU", "+8786FU", "-8286HI", "+2434HI", "-2233KA", "+5958OU", "-5141OU", "+3736FU", "-3142GI", "+3938GI", "-6151KI", "+3435HI", "-7162GI", "+2937KE", "-7374FU", "+0087FU", "-8676HI", "+8833UM", "-2133KE", "+0077FU", "-7675HI", "+3575HI", "-7475FU", "+0021HI", "-4152OU", "+0046KA", "-0073KA", "+4673UM", "-8173KE", "+2111RY", "-0026FU", "+1121RY", "-0022HI", "+2122RY", "-3222KI", "+0028FU", "-0035FU", "+0034KY", "-0088FU", "+7888KI", "-3536FU", "+3433NY", "-4233GI", "+3725KE", "-3324GI", "+0074KE", "-2627TO", "+7462NK", "-5162KI", "+2827FU", "-2425GI", "+0011KA", "-0037KE", "+4939KI", "-0021FU", "+1122UM", "-2122FU", "+0032HI", "-0042KE", "+3231RY", "-0061HI", "+3132RY", "-0014KA", "+3222RY", "-0021KY", "+2213RY", "-0035KA", "+1333RY", "-3524KA", "+3324RY", "-2124KY", "+0033KA", "-0034HI", "+3355UM", "-6131HI", "+0048KI", "-3729NK", "+3929KI", "-3637TO", "+3837GI", "-2536GI", "+3736GI", "-3436HI", "+0038GI", "-3635HI", "+5546UM", "-2427NY", "+3827GI", "-0037GI", "+4635UM", "-3135HI", "+0026GI", "-3748GI", "+5848OU", "-0059KA", "+4859OU", "-1447UM", "+5968OU"};
		sasu(ansList3);
		
		// 打ち込み系に問題がありそうか？
        String[] ansList4 = {
                "+2726FU",
                "-4132KI",
                "+4746FU",
                "-3334FU",
                "+3938GI",
                "-8384FU",
                "+7776FU",
                "-4344FU",
                "+4958KI",
                "-3142GI",
                "+3847GI",
                "-2233KA"};
        sasu(ansList4);
	}
	
	/**
	 * 容疑者を探す
	 * @param ansList 合法手のリスト
	 */
	public void sasu(String [] ansList) {
        BanmenFactory factory = new BanmenFactory();

        BanmenKey topKey = new BanmenKey(new BanmenOnly());
		BanmenNext banmenTopNext = factory.create(null,topKey);
		for (String ans : ansList) {
			logger.debug("sasu " + ans);
			BanmenOnly banmenTopOnly = banmenTopNext.getMyKey().createBanmenOnly();
			int te = BanmenDefine.changeTeStringToInt(ans);
			BanmenOnly banmenTeOnly = new BanmenOnly(banmenTopOnly, te);
			BanmenKey key = new BanmenKey(banmenTeOnly);
			BanmenNext banmenTeNext = factory.create(null, key); // ここで生成ミスってないか調べる
			//
			// 次の手とのキー同士の比較は当然異なるはず
			assertNotEquals(banmenTopNext.getMyKey(), banmenTeNext.getMyKey());
			//
			// 打ったあとにキー値を使って同じか確認する
			String q = banmenTeNext.getMyKey().toString(); // キーを文字にする
			BanmenKey banmenTeKey = new BanmenKey(q); // 文字からキーを作る
			BanmenOnly banmenTeOnly2 = banmenTeKey.createBanmenOnly(); // キーから盤面を作る

			// 同じキーのはずだから、同じキーになるはずだ
			assertEquals(q,banmenTeKey.toString());
			//
			// 同じキーから作ったものだから、同じ盤面になるはずだ
			if (!banmenTeOnly.equals(banmenTeOnly2)) {
				logger.error("NG!");
			}
			assertEquals(banmenTeOnly,banmenTeOnly2);
			//
			// 同じ盤面からキーを作れば、同じキーが得られるはずだ
			BanmenKey banmenTeKey2 = new BanmenKey(banmenTeOnly2);
			assertEquals(banmenTeKey,banmenTeKey2);
			//
			// 次の手にする
			banmenTopNext = banmenTeNext;
		}
	}
	
}
