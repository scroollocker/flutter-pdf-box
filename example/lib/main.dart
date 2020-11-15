import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_pdf_box/flutter_pdf_box.dart';
import 'package:path_provider/path_provider.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
    FlutterPdfBox.init().then((_) => testPdf());
  }

  Future<void> testPdf() async {
    var dir = await getTemporaryDirectory();
    String path =
        '${dir.path}/${DateTime.now().millisecondsSinceEpoch}_ass.pdf';

    var page = PDFPage.create()
      ..drawText('Ass', fontSize: 26, fontPath: 'assets/fonts/ass.ttf')
      ..drawRectangle(color: '#000000', x: 100, y: 100, height: 50, width: 50)
      ..drawImage(
          x: 0,
          y: 50,
          imageType: PDFActions.PDFActionImageTypeJpg,
          imagePath: 'assets/img/sign.jpg',
          imageSource: PDFActions.PDFActionImageSourceAssets);

    var mPage = PDFPage.modify(0)
      ..drawText('AssMass', fontSize: 26, fontPath: 'assets/fonts/ass.ttf')
      ..drawRectangle(color: '#000000', x: 100, y: 100, height: 50, width: 50)
      ..drawImage(
          x: 0,
          y: 50,
          imageType: PDFActions.PDFActionImageTypeJpg,
          imagePath: 'assets/img/sign.jpg',
          imageSource: PDFActions.PDFActionImageSourceAssets);

    var pdf = PDFDocument.create(path)..addPage(page);
    String modifyPath = await pdf.write();

    var modifyPdf = PDFDocument.modify(modifyPath)..modifyPage(mPage);

    print(await modifyPdf.write());
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await FlutterPdfBox.platformVersion;
    } catch (err) {
      print(err);
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Text('Running on: $_platformVersion\n'),
        ),
      ),
    );
  }
}
