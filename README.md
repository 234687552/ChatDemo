一：android 以module导入 easeui 并修复其gradle依赖错误
=====
### 步骤：
	1.File——new——impor Module
	2.source directory：选择在sdk/example/easeui文件 一路next到finish；
	3.导入完后File——Project Structure 里面的左下modules选择app进行设置
		1)选择右边Dependencies然后选+号
		2)选择Moudle dependency 
		3)把easeui选择导入作为自己的lib。
	3.设置build.gradle(Module:easeui) 
		1)compilesdkversion 、buildtoolsversion 、defaultcofig 的参数设置与build.gradle(Module:app)一致
		2)dependencies 里面添加 compile 'com.android.support:appcompat-v7:23.4.0'(从appde gradle拷贝过来)
		3)删除easeui文件夹里面support-v4.jar库包，因为上一步已经帮导入(v7包含v4)，重点是避免重复搞混method。
	4.设置build.gradle(Module:app)
		1)注释掉 //compile 'com.android.support:appcompat-v7:23.4.0' 因为lib包easeui帮忙导入了。避免重复依赖
		2)确保含有compile project(':easeui')保证第二步导入成功。
		3)去掉 compile files('libs/hyphenatechat_3.1.4.jar') 因为easeui工程lib文件里面已经增加/hyphenatechat_3.1.4.jar。
	5.去自己项目文件夹里(如我的ChatDemo文件夹中) 的lib文件夹删除hyphenatechat_3.1.4.jar。因为easeui文件里面有。
		
### 参考网址：
	http://stackoverflow.com/questions/32807587/com-android-build-transform-api-transformexception/32811133#32811133
	
	http://blog.sina.com.cn/s/blog_6f3828770102w30b.html
	
	http://stackoverflow.com/questions/32798816/unexpected-top-level-exception-com-android-dex-dexexception-multiple-dex-files/37202393#37202393
	
### 心得总结：
	
	依赖包中 不可重复依赖或者 上下含有关系。 如support-v7含有v4。