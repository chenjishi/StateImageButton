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

如果图片按钮的每种状态相差很大，无法通过染色实现的话，我们可以采用另一种方法使用StateImageButton。比如，三种状态图片如下

![](/stateimagebutton/images/share_normal.png)![](/stateimagebutton/images/share_pressed.png)![](/stateimagebutton/images/share_disabled.png)

这一种情况我们通过设置StateImageButton的image_normal，image_select和image_disabled属性设置，无需再写selector文件。

```
<com.miscell.stateimage.StateImageButton
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:clickable="true"
                state:image_normal="@drawable/share_normal"
                state:image_selected="@drawable/share_pressed"
                state:image_disabled="@drawable/share_disabled"/>
```

## 3.减少View的嵌套层次及View的数量

如果需要实现一个下图示例的按钮，有图片、文字、小红点，我们需要通过ViewGroup+View嵌套的方式实现。

![](/stateimagebutton/images/novel.png)

代码：

```
 <RelativeLayout
                android:layout_width="58dp"
                android:layout_height="46dp"
                android:clickable="true">
            <ImageView
                    android:id="@+id/image_view"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:src="@drawable/ic_novel"
                    android:layout_centerHorizontal="true"
                    android:layout_marginTop="5dp"/>
            <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_below="@id/image_view"
                    android:layout_centerHorizontal="true"
                    android:text="小说"
                    android:textColor="@android:color/white"
                    android:textSize="11sp"
                    android:layout_marginTop="2dp"/>
            <View android:layout_width="4dp" android:layout_height="4dp"
                  android:background="@drawable/red_dot"
                  android:layout_toRightOf="@id/image_view"
                  android:layout_marginTop="2dp"/>
        </RelativeLayout>
```

如果使用StateImageButton，则我们只需要一张图片即可，设置图片、文字和小红点的属性如下：

```
<com.miscell.stateimage.StateImageButton
                android:id="@+id/btn_novel"
                android:layout_width="58dp"
                android:layout_height="46dp"
                android:layout_marginLeft="8dp"
                state:text="小说"
                state:text_color="#FFF"
                state:text_size="11sp"
                state:indicator_align_image="true"
                state:indicator_color="#E14127"
                state:indicator_radius="2dp"
                state:indicator_margin_top="2dp"
                state:indicator_horizontal_padding="0dp"
                state:image_normal="@drawable/ic_novel"
                state:text_margin_top="2dp"
                state:image_margin_top="5dp"/>
```

```
StateImageButton button = (StateImageButton) findViewById(R.id.btn_novel);
button.showIndicator(true);
```

作为对比我们打开“显示布局边界”查看两种方式实现后View的情况，左边为RelativeLayout实现，右边为StateImageButton实现，可以看出使用StateImageButton实现将原有的4个View减少到了一个。

![](/stateimagebutton/images/novel_before.png) ![](/stateimagebutton/images/novel_after.png)

另外，如果需要点击时有点击状态背景，我们可以给StateImageButton设置属性highlight_color，同样也不需要使用selector。

```
state:highlight_color="#1AFFFFFF"
```
![](/stateimagebutton/images/novel_highlight.png)

## 4.与Fresco或者Glide配合使用获取网络图片

如果按钮的icon需要从网络获取，比如下图布局


![](/stateimagebutton/images/novel_1.png)


这种情况，需要通过ViewGroup+View组合的方式实现，至少需要3个View(一个RelativeLayout作为容器，一个ImageView显示网络图片，一个TextView显示文字)，如果使用StateImageButton只需要一个View即可实现，代码如下：

```
 <com.miscell.stateimage.StateImageButton android:id="@+id/btn_novel"
                                                 android:layout_width="58dp"
                                                 android:layout_height="46dp"
                                                 state:text="小说"
                                                 state:text_color="#FFF"
                                                 state:text_size="11sp"
                                                 state:text_margin_top="2dp"
                                                 state:image_margin_top="5dp"/>
```

```
String url = "https://dlmse.sogoucdn.com/uploadImage/novel2018@1080_20180103_1514951106.png";
final StateImageButton button = (StateImageButton) findViewById(R.id.btn_novel);
Glide.with(this).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
    @Override
    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
        if (null != resource) button.setImageBitmap(resource);}
});
```










