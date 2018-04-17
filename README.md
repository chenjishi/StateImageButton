# StateImageButton
一个旨在减少bitmap内存占用、减少View层级结构的按钮控件。一般来说应用中按钮有很多状态比如正常态、点击态、不可点状态，每个状态需要不同的图片去展示，导致内存中会为每一种状态都生成bitmap，从而导致内存的升高，很多情况下我们需要保持按钮的显示状态，但是其实不需要设置每种状态的图片资源，因为很多按钮的点击态其实只是将图片改变一种颜色。
StateImageButton通过PorterDuffXfermode.Mode.SRC_IN的方式，在按钮状态变化时染上不同的颜色，从而实现使用一张图片获取不同状态的效果，将原来的多份bitmap内存占用减少到一份。


##减少bitmap占用
如果使用selector来实现一个图片按钮的按下状态、不可点击状态和正常状态，需要写一个selector，通过三张图片实现，如下：

```
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:state_pressed="true" android:drawable="@drawable/star_pressed"/>
    <item android:state_enabled="false" android:drawable="@drawable/star_disabled"/>
    <item android:drawable="@drawable/star_normal"/>
</selector>
```
其中star_pressed、star_disabled和star_normal为三张png图片
