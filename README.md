# CodeEditText

![preview](https://user-images.githubusercontent.com/14817735/198873382-74161f3c-9faa-4ba0-86db-5b57b16cf695.jpg)

## 声明依赖项

在项目根目录的 build.gradle 中添加 JitPack 仓库

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

添加依赖

```groovy
dependencies {
    implementation 'com.github.aitsuki:CodeEditText:1.0.1'
}
```

## 使用方式

```xml

<com.aitsuki.widget.CodeEditText android:layout_width="wrap_content"
    android:layout_height="wrap_content" android:inputType="number" android:maxLength="4" />
```

使用方式和 EditText 相同，CodeEditText 只是改变了它的测量方式和绘制方式，暂不支持 Password 类型的 inputType。 除此之外，提供了几个自定义属性用来调整样式。

```xml

<declare-styleable name="CodeEditText">
    <!--  方块之间的间距  -->
    <attr name="boxSpacing" format="dimension" />

    <!--  方块内间距，仅控件宽度为 wrap_content 时有效  -->
    <attr name="boxPadding" format="dimension" />

    <!--  方块的圆角属性  -->
    <attr name="boxRadius" format="dimension" />

    <!--  方块的宽高比，仅控件高度为 wrap_content 时有效  -->
    <attr name="boxRatio" format="float" />

    <!--  方块描边粗细  -->
    <attr name="boxStroke" format="dimension" />

    <!--  方块颜色  -->
    <attr name="boxColor" format="color" />

    <!--  方块描边颜色  -->
    <attr name="boxStrokeColor" format="color" />

    <!--  方块描边颜色（有焦点时）  -->
    <attr name="boxStrokeFocusedColor" format="color" />
</declare-styleable>
```

## License

[MIT](LICENSE) © Aitsuki

