# KRecyclerView
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-KRecyclerView-green.svg?style=true)](https://android-arsenal.com/details/1/4442) [![Build Status](https://travis-ci.org/Khang-NT/KRecyclerView.svg?branch=master)](https://travis-ci.org/Khang-NT/KRecyclerView) [ ![Download](https://api.bintray.com/packages/khang-nt/maven/krv/images/download.svg) ](https://bintray.com/khang-nt/maven/krv/_latestVersion)

# Demo
![](gif/1.gif) ![](gif/2.gif)

# Install
```groovy
dependencies {
    compile 'org.k.recyclerview:krv:$paste-latest-version-here$'
}
```

# How to use:
## Add KRecyclerView to your layout:
```xml
<org.k.recyclerview.KRecyclerView
        android:id="@+id/krv"
        app:focusedItemHeight="200dp"
        app:defaultItemHeight="80dp"
        app:updateWhenPixelChangedLargerThan="1dp"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```
## Create your adapter inherits `KRecyclerView.EndlessAdapter`
[See my example code](app/src/main/java/org/k/recyclerview/demo/MainActivity.java)
**Important** KRecyclerView only accepts adapter extends `KRecyclerView.EndlessAdapter` class, otherwise you will get an
`IllegalArgumentException` at method `KRecyclerView.setAdapter()`.
## Customize:
Attribute | Format | Default Value | Description
--------- | ------ | ------------- | -----------
`focusedItemHeight` | dimension | 400px | Max height of focused items
`defaultItemHeight` | dimension | 200px | Min height of default items
`flingScrollSpeedFactor` | float | 1.0f | Decrease or increase scrolling speed of recycler view. Default is 1.0f, the higher the faster, and vice versa.
`updateWhenPixelChangedLargerThan` | dimension | 2px | Working like FPS, 0 is smoothest but lowest performance. Recommended value: between 2 and 10.