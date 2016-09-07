# CompatToast
android 5.0 增加的消息中心。当用户去消息中心里面关闭了你的项目app所有通知时，你的app无法显示Toast通知。因此CompatToast就出现了。

CompatToast是依照Toast源码进行修改，原生的Toast是运用了IBinder的进程机制，将Toast交给NotificationManager，这就意味着Toast和Notification是在同一层次的，因此通知被关闭，Toast也会被关闭。个人是觉得这样设计不是特别合理，毕竟Toast作为轻量级的通知控件，不会特别干扰到用户的使用，没必要和Notification放在同一层次里面。不过原生的Toast毕竟是个老控件，当初设计的时候设计者应该有别的考虑，这权当是我自己这样想吧。

设计思路：<br/>
 1、Toast的视图添加到屏幕上是由WindowManager进行管理的，因此Toast通知的显示就由这个来控制。<br/>
 2、原生的Toast IBinder里面有管理Toast的队列，用来控制多个Toast显示,这里我们CompatToast需要创建一个队列来管理。<br/>
 3、Toast显示有一定的延迟，我们用Handler来控制toast何时显示，显示时的持续时间，当然我们也可以控制Toast通知的显示频率。<br/>
 
代码没有特别的注释。在你阅读代码之前，建议你去阅读原生的Toast的源码，这样对WindowManager有个新的认识。我这里的Toast只是很接近原生的显示。如果你对Toast要求很高，需要显示很漂亮，可以去看看SuperToast之类的源码，他的实现完全摆脱原生Toast的实现，自己设计一个悬浮的视图，这算是自定义的视图高级用法吧。
 
如果有什么问题，欢迎交流。
