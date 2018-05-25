# sailer-android
Mobile Hybrid Framework Sailer Android Container base on crosswalk-project/XWalkView.
## Sailer简介
Sailer 是一个针对移动端的混合开发框架。sailer-android是android端sailer的实现,  
由于早期Android原生WebView性能和兼容性方面做的不够完善。所以引用了英特尔在2013年启动的Crosswalk项目。  
CrossWalk官方网站[https://crosswalk-project.org](https://crosswalk-project.org)
但是改项目在Crosswalk23之后就官方声明不在维护，原因是Android本身自带的WebView已经越来越好，具体参见[https://crosswalk-project.org/blog/crosswalk-final-release.html](https://crosswalk-project.org/blog/crosswalk-final-release.html)


Sailer 包含三个库：

1. Sailer H5 ：[https://github.com/PalmSugarMedicine/sailer-h5](https://github.com/PalmSugarMedicine/sailer-h5)。
2. Sailer iOS：[https://github.com/PalmSugarMedicine/sailer-ios](https://github.com/PalmSugarMedicine/sailer-ios)。
3. Sailer Android：[https://github.com/PalmSugarMedicine/sailer-android](https://github.com/PalmSugarMedicine/sailer-android)。

## 使用的介绍
app中的gradle依赖
````Gradle
dependencies {
    implementation 'com.github.PalmSugarMedicine:sailer-android:1.0.4'
}
````
root中的gradle依赖
````
allprojects {
    repositories {
        google()
        jcenter()
        maven {
            url 'https://jitpack.io'
        }
    }
}
````
ps: 建议还是git clone 该项目。
## TODO
1. 后续维护文档。写一些JS demo.
2. 目前只是从原有的项目的移植，还没有做到精简。后期再做，先提交。