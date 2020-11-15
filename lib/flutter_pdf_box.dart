import 'dart:async';
import 'dart:convert';

import 'package:flutter/services.dart';

class PDFMediaBox {
  int x;
  int y;
  int width;
  int height;

  PDFMediaBox({
    this.x = 0,
    this.y = 0,
    this.width = 250,
    this.height = 250,
  });

  Map<String, dynamic> toMap() {
    return {
      'x': x,
      'y': y,
      'width': width,
      'height': height,
    };
  }

  factory PDFMediaBox.fromMap(Map<String, dynamic> map) {
    if (map == null) return null;

    return PDFMediaBox(
      x: map['x'],
      y: map['y'],
      width: map['width'],
      height: map['height'],
    );
  }

  String toJson() => json.encode(toMap());

  factory PDFMediaBox.fromJson(String source) =>
      PDFMediaBox.fromMap(json.decode(source));
}

class PDFActions extends PDFMediaBox {
  static const String PDFActionTypeText = 'text';
  static const String PDFActionTypeRect = 'rectangle';
  static const String PDFActionTypeImage = 'image';
  static const String PDFActionImageTypePng = 'png';
  static const String PDFActionImageTypeJpg = 'jpg';
  static const String PDFActionImageSourcePath = 'path';
  static const String PDFActionImageSourceAssets = 'assets';

  String type;

  String value;
  int fontSize;
  String fontPath;
  String color;
  String imageType = PDFActionImageTypePng;
  String imagePath;
  String imageSource = PDFActionImageSourcePath;

  PDFActions(
      {this.type,
      this.value,
      this.fontSize,
      this.fontPath,
      this.color,
      this.imageSource,
      this.imageType,
      this.imagePath,
      int x,
      int y,
      int width,
      int height})
      : super(height: height, width: width, x: x, y: y);

  Map<String, dynamic> toMap() {
    return {
      'type': type,
      'value': value,
      'fontSize': fontSize,
      'fontPath': fontPath,
      'color': color,
      'imagePath': imagePath,
      'source': imageSource,
      'imageType': imageType,
      ...super.toMap()
    };
  }

  factory PDFActions.fromMap(Map<String, dynamic> map) {
    if (map == null) return null;

    return PDFActions(
        type: map['type'],
        value: map['value'],
        fontSize: map['fontSize'],
        fontPath: map['fontPath'],
        color: map['color'],
        imageSource: map['imageSource'],
        imagePath: map['imagePath'],
        imageType: map['imageSource'],
        x: map['x'],
        y: map['y'],
        height: map['height'],
        width: map['width']);
  }

  String toJson() => json.encode(toMap());

  factory PDFActions.fromJson(String source) =>
      PDFActions.fromMap(json.decode(source));
}

class PDFPage {
  int pageIndex;
  PDFMediaBox mediaBox;
  List<PDFActions> actions;

  PDFPage({
    this.pageIndex,
    this.mediaBox,
    this.actions,
  });

  factory PDFPage.create() {
    PDFPage page = PDFPage(
      actions: List(),
      mediaBox: PDFMediaBox(height: 500, width: 250, x: 0, y: 0),
    );
    return page;
  }

  factory PDFPage.modify(int pageIndex) {
    PDFPage page = PDFPage(
      pageIndex: pageIndex,
      actions: List(),
    );
    return page;
  }

  void setMediaBox(PDFMediaBox mediaBox) {
    mediaBox = mediaBox;
  }

  PDFPage drawText(String value,
      {int x = 0,
      int y = 0,
      String color = '#000000',
      int fontSize = 12,
      String fontPath}) {
    var txtAction = PDFActions(
        color: color,
        fontSize: fontSize,
        fontPath: fontPath,
        x: x,
        y: y,
        type: PDFActions.PDFActionTypeText,
        value: value);

    actions.add(txtAction);
    return this;
  }

  PDFPage drawRectangle(
      {int x = 0,
      int y = 0,
      String color = '#000000',
      int width: 50,
      int height: 50}) {
    var recAction = PDFActions(
      color: color,
      width: width,
      height: height,
      x: x,
      y: y,
      type: PDFActions.PDFActionTypeRect,
    );

    actions.add(recAction);
    return this;
  }

  PDFPage drawImage(
      {int x = 0,
      int y = 0,
      String imagePath,
      String imageSource = PDFActions.PDFActionImageSourcePath,
      String imageType = PDFActions.PDFActionImageTypePng,
      int width,
      int height}) {
    var imgAction = PDFActions(
      width: width,
      height: height,
      imageSource: imageSource,
      imageType: imageType,
      imagePath: imagePath,
      x: x,
      y: y,
      type: PDFActions.PDFActionTypeImage,
    );

    actions.add(imgAction);
    return this;
  }

  Map<String, dynamic> toMap() {
    return {
      'pageIndex': pageIndex,
      'mediaBox': mediaBox?.toMap(),
      'actions': actions?.map((x) => x?.toMap())?.toList(),
    };
  }

  factory PDFPage.fromMap(Map<String, dynamic> map) {
    if (map == null) return null;

    return PDFPage(
      pageIndex: map['pageIndex'],
      mediaBox: PDFMediaBox.fromMap(map['mediaBox']),
      actions: List<PDFActions>.from(
          map['actions']?.map((x) => PDFActions.fromMap(x))),
    );
  }

  String toJson() => json.encode(toMap());

  factory PDFPage.fromJson(String source) =>
      PDFPage.fromMap(json.decode(source));
}

class PDFDocument {
  String path;
  List<PDFPage> pages;
  List<PDFPage> modifyPages;
  PDFDocument({
    this.path,
    this.pages,
    this.modifyPages,
  });

  Map<String, dynamic> toMap() {
    return {
      'path': path,
      'pages': pages?.map((x) => x?.toMap())?.toList(),
      'modifyPages': modifyPages?.map((x) => x?.toMap())?.toList(),
    };
  }

  factory PDFDocument.fromMap(Map<String, dynamic> map) {
    if (map == null) return null;

    return PDFDocument(
      path: map['path'],
      pages: List<PDFPage>.from(map['pages']?.map((x) => PDFPage.fromMap(x))),
      modifyPages: List<PDFPage>.from(
          map['modifyPages']?.map((x) => PDFPage.fromMap(x))),
    );
  }

  String toJson() => json.encode(toMap());

  factory PDFDocument.fromJson(String source) =>
      PDFDocument.fromMap(json.decode(source));

  factory PDFDocument.create(String path) {
    return PDFDocument(path: path, pages: List(), modifyPages: List());
  }

  factory PDFDocument.modify(String path) {
    return PDFDocument(path: path, pages: List(), modifyPages: List());
  }

  PDFDocument setPath(String path) {
    path = path;
    return this;
  }

  PDFDocument addPage(PDFPage page) {
    pages.add(page);
    return this;
  }

  PDFDocument modifyPage(PDFPage page) {
    modifyPages.add(page);
    return this;
  }

  Future<String> write() async {
    assert(path != null);

    if (pages.isNotEmpty) {
      return FlutterPdfBox.createPdf(this);
    }

    if (modifyPages.isNotEmpty) {
      return FlutterPdfBox.modifyPdf(this);
    }

    return null;
  }
}

class FlutterPdfBox {
  static const MethodChannel _channel = const MethodChannel('flutter_pdf_box');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<void> init() async {
    await _channel.invokeMethod('init');
  }

  static Future<String> modifyPdf(PDFDocument document) async {
    return await _channel.invokeMethod('modifyPdf', document.toMap());
  }

  static Future<String> createPdf(PDFDocument document) async {
    return await _channel.invokeMethod('createPdf', document.toMap());
  }
}
