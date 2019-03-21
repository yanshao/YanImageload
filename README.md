# YanImageload
一、项目介绍
本项目用于加载网络图片，其中包含图片的三级缓存、圆角图片、圆形图片等

二、依赖
	dependencies {
	        implementation 'com.github.yanshao:YanImageload:v1.0'
	}
  
  
  	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
 三、使用
 在需要的地方直接调用下面代码
   YanImageLoad.getInstance().disPlay(imageview, "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1553058539054&di=0713321ef5ba49d6983b996061cda040&imgtype=0&src=http%3A%2F%2Fh.hiphotos.baidu.com%2Fzhidao%2Fpic%2Fitem%2Fac6eddc451da81cb167b12945466d016082431cd.jpg", 2);
第一个参数  imagview  为imagview控件   第二个参数 图片url  第三个参数  1 圆角 2 圆形  其他值为正常显示
