import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:url_strategy/url_strategy.dart';

void main() {
  setPathUrlStrategy();  // Call setPathUrlStrategy() here
  runApp(const MaterialApp(home: Scaffold(body: MyApp())));
}

class MyApp extends StatefulWidget {
  //
  const MyApp({Key? key}) : super(key: key);

  @override
  // ignore: library_private_types_in_public_api
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String baseUrl = "http://" + Uri.base.host + ":" + Uri.base.port.toString(); //'http://localhost:8080';
  String stateString = '/state';
  String logString = '/log';
  String connectOneString = '/connectOne';
  String connectString = '/connect';
  String stopString = '/stop';
  String postgresString = '/postgres';
  String cleardbString = '/cleardb';
  String downloadString = '/download';
  String serverString = '/server';
  int state = 0;
  Map<String, dynamic> result = {};

  final ScrollController _scrollController = ScrollController();

  @override
  void dispose() {
    _scrollController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {

    List<Widget> buttonList = [];
    buttonList.add(TextFormField(
      initialValue: baseUrl,
      onChanged: (value) {
        setState(() {
          baseUrl = value;
        });
      },
    ));

    // デフォルト表示
    buttonList.add(TextButton(
      onPressed: () {
        nextState(stateString,{});
      },
      child: Text(stateString))); // 状態表示
    buttonList.add(TextButton(
      onPressed: () {
        nextState(logString,{});
      },
      child: Text(logString))); // ログ表示
    
        //
    if (result.containsKey('log')) {
      // ログ表示はめいっぱい出したい
      buttonList.add(TextField(
          controller: TextEditingController(text: result["log"]),
          keyboardType: TextInputType.multiline,
          maxLines: 20));
    } else {
      if ((state == 1) || (state == 3)) {
        buttonList.add(TextButton(
           onPressed: () {
             nextState(connectOneString,{});
        },
        child: Text(connectOneString)));
        //
        buttonList.add(TextButton(
          onPressed: () {
            nextState(connectString,{});
          },
          child: Text(connectString)));
      }
      if (state == 1) {
        buttonList.add(TextButton(
          onPressed: () {
            nextState(postgresString,{});
          },
          child: Text(postgresString)));
        buttonList.add(TextButton(
          onPressed: () {
            nextState(cleardbString,{});
          },
          child: Text(cleardbString)));
        buttonList.add(TextButton(
          onPressed: () {
            nextState(downloadString,{});
          },
          child: Text(downloadString)));
        buttonList.add(TextButton(
          onPressed: () {
            nextState(serverString,{});
          },
          child: Text(serverString)));
      }
      if (2 <= state) {
        buttonList.add(TextButton(
          onPressed: () {
            nextState(stopString,{});
          },
          child: Text(stopString)));
      }
      // メッセージ表示
      buttonList.add(Text('${stateString}: ${state}'));
      if (result.containsKey('stateString')) {
        buttonList.add(Text('${stateString} (String): ${result["stateString"]}}'));
      }
      buttonList.add(Text(result.toString()));

      // 盤面を表示する
      if (result.containsKey("result")) {
          var x = result["result"];
          buttonList.add(Text(x.toString()));
          if ((!(x is String)) && x.containsKey("myTurn")) {
            var z = x["myTurn"];
            String teban = (z == 0) ? "先手" : "後手";
            buttonList.add(Text("自分の手番:" + teban));
          }
          if ((!(x is String)) && x.containsKey("banmen")) {
              var y = x["banmen"];
              if (y.containsKey("teban")) {
                  var z = y["teban"];
                  String teban = (z == 0) ? "先手" : "後手";
                  buttonList.add(Text("現在の手番:" + teban));
              }
              if (y.containsKey("gote")) {
                  var z = y["gote"];
                  buttonList.add(temochi("後手:",z));
              }
              if (y.containsKey("banmen")) {
                  var z = y["banmen"];
                  buttonList.add(getBanmen(z));
              }
              if (y.containsKey("sente")) {
                  var z = y["sente"];
                  buttonList.add(temochi("先手:",z));
              }
          }
      }
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text("ANShogiO"),
      ),
      body: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: buttonList));
  }

  void nextState(String folder, Map<String, dynamic> sendData) async {
    try {
      result = {}; // 状態をクリアします
      var uri = Uri.parse(baseUrl + folder);
      String body = json.encode({'name': 'moke'});
      var response = await http.post(
        uri,
        headers: <String, String>{
          'Content-Type': 'application/json; charset=UTF-8',
        },
        body: body);
      if (response.statusCode == 200) {
        state = 0;
        result = json.decode(response.body);
        if (result.containsKey('state')) {
          state = result["state"];
        }

      } else {
        state = -1;
        result["error1"] = response.body;
      }
    } catch (e) {
      // flutter run --device-id chrome --web-browser-flag "--disable-web-security"
      // or
      // flutter run --device-id windows 
      state = -2;
      result["error2"] = '$e';
    }
    setState(() {});
  }
  /** 手持ちの表示をする */
  Widget temochi(String name,var y) {
    String res = name;
    int i = 0;
    List<String> message = ["歩","香","桂","銀","金","角","飛","王"];
    int flag = 0;
    for (final int z in y) {
      for (int n = 0 ; n < z ; n++) {
          res = res + message[i];
          flag = 1;
      }
      i++;
    }
    if (flag == 0) {
      res = res + "なし";
    }
    return Text(res);
  }
  Widget getBanmen(var result) {
    List<TableRow> tableRow = [];
    for (int y = 0; y < 10; y++) {
        List<Widget> tableChildren = [];
        for (int x = 0 ; x < 10 ; x++) {
          int nowX = 9 - x;
          if ((y == 0) && (x == 0)) {
            tableChildren.add(Text("--"));
          } else if (x == 0) {
            tableChildren.add(Text(y.toString()));
          } else if (y == 0) {
            tableChildren.add(Text((nowX + 1).toString()));
          } else {
              tableChildren.add(changeIntToText(result[y-1][nowX]));
          }
        }
        tableRow.add(TableRow(children: tableChildren));
    }
    return ConstrainedBox(
        constraints: BoxConstraints(
            minHeight: 100/*最小の高さ*/,
            minWidth: 100/*最小の横幅*/,
        ),
        child: Table(
            border: TableBorder.all(),
            children: tableRow));
  }
  Widget changeIntToText(int ans) {
    int nari = 0x10 & ans;
    int koma = 0x1F & ans;
    int mochi = 0x20 & ans;
    String ansString;
    if (koma == 0) {
      ansString = " ";
    } else if (koma == 1) {
      ansString = "歩";
    } else if (koma == 2) {
      ansString = "香";
    } else if (koma == 3) {
      ansString = "桂";
    } else if (koma == 4) {
      ansString = "銀";
    } else if (koma == 5) {
      ansString = "金";
    } else if (koma == 6) {
      ansString = "角";
    } else if (koma == 7) {
      ansString = "飛";
    } else if (koma == 8) {
      if (mochi == 0) {
        ansString = "王";
      } else {
        ansString = "玉";
      }
    } else if (koma == (1 + 0x10)) {
      ansString = "と";
    } else if (koma == (2 + 0x10)) {
      ansString = "成香";
    } else if (koma == (3 + 0x10)) {
      ansString = "成桂";
    } else if (koma == (4 + 0x10)) {
      ansString = "成銀";
    } else if (koma == (6 + 0x10)) {
      ansString = "馬";
    } else if (koma == (7 + 0x10)) {
      ansString = "龍";
    } else {
      ansString = "不明" + koma.toString();
    }
    Text text;
    if (nari != 0) {
      text = Text(ansString,
        style: TextStyle(
          backgroundColor: Colors.orange[100],
          color: Colors.red));
    } else {
      text = Text(ansString,
        style: TextStyle(
          backgroundColor: Colors.orange[100],
          color: Colors.black));
    }
    if (mochi != 0) {
      return RotationTransition(
        turns: AlwaysStoppedAnimation(180 / 360),
        child: text);
    }
    return text;
  }
}
