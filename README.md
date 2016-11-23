# OSChina Android [客户端](http://www.oschina.net/app/)

**源代码请切换置对应的分支，master分支中今后不再放源代码。**

##历史分支

编号 | 标签名 | 发布版本 | 备注
------- | ------- | ------- | -------
1 | [v2.6.9](http://git.oschina.net/oschina/android-app/tree/v2.6.9/)  |v2.6.9 (1611220955)| 当前最新版
2 | [v2.6.6](http://git.oschina.net/oschina/android-app/tree/v2.6.6/)  |v2.6.6 (1609281026)|   
3 | [v2.6.5](http://git.oschina.net/oschina/android-app/tree/v2.6.5/)  |v2.6.5 (1609211120)|  
4 | [v2.6.4](http://git.oschina.net/oschina/android-app/tree/v2.6.4/)  |v2.6.4 (1608081154)|   
5 | [v2.6.3](http://git.oschina.net/oschina/android-app/tree/v2.6.3/)  |v2.6.3 (1607081128)|
6 | [v2.6.2](http://git.oschina.net/oschina/android-app/tree/v2.6.2/)  |v2.6.2(1606121625)|   
7 | [v2.4](http://git.oschina.net/oschina/android-app/tree/v2.4/) | -- | -- |
8 | [v2.3](http://git.oschina.net/oschina/android-app/tree/v2.3/) | -- | 迁移到AndroidStudio |
9 | [v2.2.1](http://git.oschina.net/oschina/android-app/tree/v2.2.1/) | -- | Eclipse可用 |


##开发环境
从2.3版本开始，项目已经完成了Gradle化，完全迁移到了Android Studio，如果想使用Eclipse进行该项目的学习，可以clone [tag v2.2.1](http://git.oschina.net/oschina/android-app/tree/v2.2.1/)，不过需要注意的是，Eclipse需要按照开发环境中提到的：进行Butterknife注解设置，详细方法参考[这里](http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0102/2247.html)


##项目简述
1. 底部导航  
    * 主界面的底部TAB导航采用[FragmentTabHost](http://git.oschina.net/oschina/osc-android-app/blob/master/osc-android-app/src/net/oschina/app/ui/MainTab.java)点击底部按钮时切换Fragment。中间的快捷操作按钮使用的是[自定义dialog](http://git.oschina.net/oschina/osc-android-app/blob/master/osc-android-app/src/net/oschina/app/ui/QuickOptionDialog.java)，通过点击时加入动画效果实现。  
2. 一级界面  
    * 包括资讯、动弹两个模块，采用[ViewPagerFragment](http://git.oschina.net/oschina/osc-android-app/blob/master/osc-android-app/src/net/oschina/app/viewpagerfragment/NewsViewPagerFragment.java)根据滑动到不同页面显示不同信息。  
3. 详情界面  
    * 详情界面包括[博客详情](http://git.oschina.net/oschina/osc-android-app/blob/master/osc-android-app/src/net/oschina/app/fragment/BlogDetailFragment.java)，[动弹详情](http://git.oschina.net/oschina/osc-android-app/blob/master/osc-android-app/src/net/oschina/app/fragment/TweetDetailFragment.java)，[新闻详情](http://git.oschina.net/oschina/osc-android-app/blob/master/osc-android-app/src/net/oschina/app/fragment/NewsDetailFragment.java)，[帖子详情](http://git.oschina.net/oschina/osc-android-app/blob/master/osc-android-app/src/net/oschina/app/fragment/PostDetailFragment.java)， [活动详情](http://git.oschina.net/oschina/osc-android-app/blob/master/osc-android-app/src/net/oschina/app/fragment/EventDetailFragment.java)等……是通过在Fragment中的WebView直接loadData()加载一段html数据并显示。  
    * 而详情Fragment的显示则是通过一个外部DetailActivity，来根据传入的参数不同来加载不同的Fragment。  
4. 链接跳转  
    * 整个应用打开链接的规则都定义在UIHelper.openBrowser()方法中，本方法会根据不同的url去解析，如果是www.oschina.net的链接，则会调用相应的界面去展示；如果是git.oschina.net我们目前会使用手机自带的浏览器打开(之后会改为使用[OscGit客户端](http://git.oschina.net/oschina/git-osc-android-project)打开)；如果不是oschina的站内链接，则使用内置浏览器打开。  
5. 侧滑菜单  
    * [侧滑菜单](http://git.oschina.net/oschina/osc-android-app/blob/master/osc-android-app/src/net/oschina/app/ui/NavigationDrawerFragment.java)采用系统的DrawerLayout实现。关于很多朋友好奇的左上角箭头，是采用的开源控件[DrawerArrowDrawable](http://git.oschina.net/oschina/osc-android-app/blob/master/osc-android-app/src/net/oschina/app/widget/DrawerArrowDrawable.java)(准确的说不应该是控件而是一个Drawable)

##依赖包介绍
1. jar包依赖  
  * 网络请求库 **android-async-http** ：http://loopj.com/android-async-http/  
  * 注解绑定控件 **butterknife** http://jakewharton.github.io/butterknife/  
  * 网络图片加载库 **KJFrameForAndroid** http://git.oschina.net/kymjs/KJFrameForAndroid  
  * XML解析库 **xstream** http://xstream.codehaus.org/  
2. 源码依赖  
  * **PhotoView-library** ：用于图片预览界面展示
  * **UmengShareLib** ：用于分享到第三方平台


##支持开源

通过以下方式对我们的项目进行支持：

1. 发现bug、确认bug、并通过**详细描述(什么操作？什么情况？什么手机？什么系统？)提出issue**
2. 如果有时间，你还可以提出Pull Request
3. 赞助开发小组童鞋们喝杯咖啡(支付宝扫描如下图片完成支付)

![支付宝](http://git.oschina.net/uploads/qrcode/qrcode_alipay_146312694382.png)

##开源协议

	The MIT License (MIT)

	Copyright (c) 2016 OSChina.net

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all
	copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
	SOFTWARE.
