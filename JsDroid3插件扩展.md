### JsDroid3插件扩展

JsDroid3的插件扩展有2种，一种是jar插件，一种是jsd插件。

#### jar插件

jar插件就是个java开发编译的产物，将jar包放到安装路径的plugin路径，就可以像java一样调用里面的类了。

#### jsd插件

jsd插件是jsdroid3特有的插件，将工程编译后得到jsd文件，将jsd文件放到安装路径的plugin路径就能使用了。



#### 举个例子

1.创建plugin工程，路径D:/project/plugin

```shell
jsd create D:/project/plugin
```



2.新建文件D:/project/plugin/src/Plugin.groovy，输入代码：

```groovy
def hello(){
    return "hello"
}
```

3.使用命令编译

```shell
jsd build D:/project/plugin
```

得到文件D:/project/plugin/dist/plugin.jsd



4.将D:/project/plugin/dist/plugin.jsd放到D:/JsDroid3/plugin/plugin.jsd(注意D:/JsDroid3为安装路径)



5.创建host工程,路径D:/project/host

```shell
jsd create D:/project/host
```

6.打开文件D:/project/host/src/MainScript.groovy，输入

```groovy
def plugin = load "Plugin"
print plugin.hello()
```

#### 再举个例子

1.在上述例子中的Plugin.groovy中输入以下代码

```groovy
global.hello="hello"
```



2.在上述例子中的MainScript.groovy中输入以下代码调用

```groovy
load "Plugin"
print hello
```

