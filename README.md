# StateImageButton
一个旨在减少bitmap内存占用、减少View层级结构的按钮控件。一般来说应用中按钮有很多状态比如正常态、点击态、不可点状态，每个状态需要不同的图片去展示，导致内存中会为每一种状态都生成bitmap，从而导致内存的升高，很多情况下我们需要保持按钮的显示状态，但是其实不需要设置每种状态的图片资源，因为很多按钮的点击态其实只是将图片改变一种颜色。
StateImageButton通过PorterDuffXfermode.Mode.SRC_IN的方式，在按钮状态变化时染上不同的颜色，从而实现使用一张图片获取不同状态的效果，将原来的多份bitmap内存占用减少到一份。


## 1.减少bitmap资源占用

通常为了实现图片按钮的状态切换，我们需要写一个selector来完成。如果我们想实现按钮的三种状态切换(pressed,disabled,normal)，需要三张png图片，如下：

```
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:state_pressed="true" android:drawable="@drawable/star_pressed"/>
    <item android:state_enabled="false" android:drawable="@drawable/star_disabled"/>
    <item android:drawable="@drawable/star_normal"/>
</selector>
```
star_pressed、star_disabled和star_normal分别代表正常态、按下态和不可点态的图片资源

![](/stateimagebutton/images/star_normal.png)![](/stateimagebutton/images/star_pressed.png)![](/stateimagebutton/images/star_disabled.png)

使用StateImageButton，我们可以仅使用一张图片从而实现三种状态，只需设置正常态的图片资源，按下态和不可点态通过设置颜色实现。


```
<com.miscell.stateimage.StateImageButton
                android:id="@+id/star_btn"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:clickable="true"
                state:color_selected="#3697FD"
                state:color_disabled="#CACACA"
                state:image_normal="@drawable/star_normal"/>
```

color_selected为按下态的颜色，color_disabled为不可点态的颜色。这样star_pressed.png和star_disabled.png就不需要了。

## 2.减少写selector使用

## 1.减少bitmap资源占用
