# YanImageload
一、项目介绍

本项目用于加载网络图片，其中包含图片的三级缓存、圆角图片、圆形图片等

![Alt text](/img/效果图.png)


上图中第二张为本地图片


二、依赖
	
	dependencies {
	       implementation 'com.github.yanshao:YanImageload:v2.2'
	}
  
  
  	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
 三、使用
 
 在需要的地方直接调用下面代码

		YanImageLoad.getInstance(MainActivity.this).disPlay(imageview, "http://xxxx",R.mipmap.icon, 2);


第一个参数  imagview  为imagview控件  

第二个参数 图片url 、图片本地路径、资源图片

第三个参数 当加载失败时显示的资源图片

第四个参数  1 圆角 2 圆形  其他值为正常显示
