# 字符串扩展

###### [English]( #string-expansion ) / 中文

这是一个 [PlaceHolderAPI](http://placeholderapi.com/) 的扩展，提供了字符串修改支持。
小如大小写转换、数字符个数，大如字符串切割等都能很好的支持。

> 如果您没有任何编程语言基础，这对于您来说可能回有一点难，但这篇文档详细的说明了使用的方法，请耐心看完。

## 它是怎样工作的

Java 内部有个字符串类，该扩展的大部分功能都是将参数传递给他的方法来处理。

> 大部分功能都参考了 String 类的设计，将传入的字符串解析后，转换为方法和调用的参数。

## 调用方式

```
%str_方法名_参数1,参数2……%
```
* 扩展 ID、方法名、参数之间用下划线`_`分隔。
* 参数部分由多个变量组成，互相之间用半角逗号`,`分隔。  
  （请注意：全角逗号`，`与半角逗号`,`是不一样的。）
* 方法名见[可用占位符与方法]( #可用占位符与方法 )，参类型数严格限制见[参数与数据类型]( #参数与数据类型 )。
* 在参数中可用其他的占位符，使用大括号调用它们。
  * 向本扩展的方法提供其他占位符的运算结果作为参数是基本操作，这能让你动态处理其他扩展返回的字符串。
  * 如占位符 `%player_name%` 应当使用 `{player_name}` 调用。
  * 不用担心大括号内的逗号会引起参数识别问题，大括号能嵌套调用，优先处理内层的大括号。下面**获取玩家ID的首字母并将其大写**的实例将展示这些特点。
    
> 实例：使玩家名称小写（假设玩家名为 **`Silence114`**）
> ```
> %str_lowercase_{player_name}%
> ``` 
> 方法：`lowercase`  
> 参数：`{player_name}` -> **`Silence114`**  
> `%str_lowercase_{player_name}%`  
> = `%str_lowercase_Silence114%`  
> = `silence114`  
>   
> 如果你在控制台执行，且 `PlaceHolderAPI` 配置文件内将 `expansions.str.debug` 设置为 `true`，会得到如下结果。
> ```
> > papi parse Silence114 %str_lowercase_{player_name}%
> [22:01:32 INFO]: [PlaceholderAPI] [String] Parameter: {player_name}    > [Parse bracket placeholder.]
> [22:01:32 INFO]: [PlaceholderAPI] [String] [Parse finished, continue.] > Silence114(BracketPlaceholders) > Silence114
> [22:01:32 INFO]: silence114
> ```
  
> 实例：获取玩家ID的首字母并将其大写（假设玩家名为 **`Silence114`**）
> ```
> %str_uppercase_{str_charat_{player_name},<0i>}%
> ``` 
> 方法：`uppercase`  
> 参数：`{str_charat_{player_name},<0i>}`  
> > 方法：`charat`  
> > 参数：`{player_name}` -> **`Silence114`**  
> > 参数：`<0i>` -> `0`  
> -> `S`  
>   
> `%str_uppercase_{str_charat_{player_name},<0i>}%`  
> = `%str_uppercase_{str_charat_Sileence114,<0i>}%`  
> = `%str_uppercase_S%`  
> = `S`  
> 
> ```
> > papi parse Silence114 %str_uppercase_{str_charat_{player_name},<0i>}%
> [22:02:56 INFO]: [PlaceholderAPI] [String] Parameter: {str_charat_{player_name},<0i>} > [Parse bracket placeholder.]
> [22:02:56 INFO]: [PlaceholderAPI] [String] Parameter: Silence114 > Silence114
> [22:02:56 INFO]: [PlaceholderAPI] [String] Parameter: <0i>       > 0i(TypeTransform) > 0(Force Integer)
> [22:02:56 INFO]: [PlaceholderAPI] [String] [Parse finished, continue.]                > S(BracketPlaceholders) > S
> [22:02:56 INFO]: S
> ```
> 本例中 `uppercase` 似乎没有什么效果，但仍然不能忽略。那是因为不同玩家的ID不同，无法确定首字母是否一定是大写。

## 参数与数据类型

因为直接调用到了 Java 的方法，Java 是强类型语言，调用一些方法必须要传入一些类型确定的参数，所以本扩展需要严格定义参数类型。

> 由于 C 语言风格的 `format` 方法，格式化字符串的参数必须要参数与标示符类型相匹配。

| Java 数据类型 | 扩展参数格式 | 示例 | 说明 |
| :----: | :----: | :---- | :---- |
| String | | Minecraft | 字符串类型，无法识别类型的参数都会被视作字符串。如，玩家的 ID，服务器的名称都是字符串类型。 |
| int | \<number`i`\> | `<2i>`=2 `<2.4i>`=2 `<2.9i>`=2 | 整数型，由尖括号`<>`包裹数字，并用`i`强调它是 int：整型。若这个数字有小数部分，会被向下取整。如玩家的等级，玩家的生命值都是整型 |
| double | \<number`d`\> | `<2d>`=2.0 `<2.4d>`=2.4 `<.9d>`=0.9 | 浮点数类型（可以简单的理解为有小数的类型，但实际上d ouble 是取自**双**精度浮点型），由尖括号`<>`包裹数字，并用`d`强调它是 double：浮点型。若这个数字有小数部分，会被向下取整。如玩家的位置等都是浮点型。 |
| Number | \<number\> | `<2>`=2 `<2.4>`=2.4 `<2.9>`=2.9 | 数字型，他包含了整数类型和浮点数类型，尖括号`<>`包裹数字，会自动根据是否有小数点转为整型或浮点型。这只是方便编写用的，对于写死的参数可以这样写，但是对于从其他占位符取来的数据还是通过后缀 `i` 和 `d` 限制比较稳妥。 |
| char | \<number`a`\> \<number`A`\> | `<37a>`=% `<37A>`=7 | 字符型，仅支持 ASCII 字符，number 表示字符在[ ASCII 码表]( https://tool.ip138.com/ascii_code/ )中的编码，小写的`a`表示这个编码是十进制的，大写的`A`表示这个编码是十六进制的。**注意：示例中的“7”表示的是`'7'`这个字符，而不是7这个数字。**|
| boolean | \<`t`\|`f`\> | `<t>`=true `<f>`=false | 布尔型，只有两种值，表示对与错，是与否，常用于表示判断结果。例如，“玩家在主世界”，当他在值就为true，不在值为false。 |

**注意：考虑到方法中输入 ASCII 码表示字符参数比较困难，所有字符类型的参数都可以传入长度为1的字符串，会自动转为字符。**（长度大于1则取首字符）  
由于一些格式字符已经被占用，下列字符需要转义，仅在确认无法转换成其他类型时才会还原转义，并将其识别为字符串。

| 字符 | 转义表达 | 占用原因 |
| :----: | :----: | :---- |
| `%` | `$` | PlaceHolder 标识符 |
| `$` | `\$` | 转义 `%`，format 方法中会大量用到百分号。 |
| `<` | `\<` | 类型转换识别 |
| `>` | `\>` | 类型转换识别 |
| `,` | `\,` | 参数间隔标识 |
| `\` | `\\` | 转义标识 |

由于大括号被用来标记内嵌占位符，而将大括号转义会在识别内嵌占位符时使得解析变得复杂，从而影响性能。
但倘若您真的需要使用大括号，可以使用大括号调用本扩展的 `char` 方法，并传入它们的 ASCII 编码数字。  
`{str_char_<123>}` -> `{`  
`{str_char_<125>}` -> `}`

## 可用占位符与方法

### Java String 内建方法

| 方法 | 占位符格式 | 参数类型 | 说明 | 简写 |
| :----: | :---- | :---- | :---- | :----: |
| charat| `%str_charat_string,<0i>%`<br>`%str_charat_string,<1i>%` | String, int | 返回 `string` 中在 `0` 号位的字符。**字符串位置是从0开始计数的，即字符串最左端是第0个字符，下同。** | |
| equal | `%str_equals_str1,str2%`<br>`%str_equals_str1,str1%` | String, String | 判断 `str1` 和 `str2` 是否一样。 | eq, = |
| indexof | `%str_indexof_targetStr,Str%`<br>`%str_indexof_targetStr,get%` | String, String | 在 `targetStr` 中查找 `Str`，返回匹配序号，没找到返回`-1`。 | index |
| indexof | `%str_indexof_targetStr,Str,<7i>%` | String, String, int | 在 `targetStr` 中从 `7` 开始查找 `Str`，返回匹配序号，没找到返回 `-1`。 | index |
| lastindexof | `%str_lastindexof_targetStr,Str%` | String, String | 在 `targetStr` 中反向查找 `Str`，返回匹配序号，没找到返回 `-1`。 | last |
| lastindexof | `%str_lastindexof_targetStr,Str,<7i>%` | String, String, int | 在 `targetStr` 中反向从 `7` 开始查找 `Str`，返回匹配序号，没找到返回 `-1`。 | last |
| startswith | `%str_startswith_prefixStr,prefix%`<br>`%str_startswith_prefixStr,fix%` | String, String | 判断 `prefixStr` 是否以 `prefix` 开头。 | start |
| startswith | `%str_startswith_prefixStr,prefix,<3i>%`<br>`%str_startswith_prefixStr,fix,<3i>%` | String, String, int | 从第 `3` 个字符开始，判断后面的半段 `prefixStr` 是否以 `prefix` 开头。 | start |
| endswith | `%str_endswith_strSuffix,Suffix%` | String, String | 判断 `strSuffix` 是否以 `Suffix` 结尾。 | ends |
| endswith | `%str_endswith_strSuffix,Suffix,<3i>%` | String, String, int | 从第 `3` 个字符开始，判断前面的半段 `strSuffix` 是否以 `Suffix` 结尾。 | ends |
| replace | `%str_replace_minecraft_mine_our%` | String, String, String | 将 `minecraft` 中所有 `mine` 替换为 `our`，**区分大小写。** | |
| substring | `%str_substring_string,<3i>%` | String, int | 返回 `string` 从 `3` 到结尾的部分。 | sub |
| substring | `%str_substring_string,<2i>,<4i>%` | String, int, int | 返回 `string` 从 `2` 到 `4` 的部分。注意，包含左端点，不包含右端点，左闭右开。 | sub |
| format | `%str_format_template_,args1,args2,...%`<br>`%str_format_TPS:$.2f,<{server_tps_1}d>%` | String, Object... | 格式化字符串，使用 `arg1`，`arg2` 等参数填入 `template` 模板。这是一个高级方法，需要您了解 [C 语言]( https://www.cplusplus.com/reference/cstdio/printf/ )或 [Java]( http://www.java2s.com/Tutorials/Java/Java_Format/0050__Java_Printf_Style_Overview.htm ) 中关于格式化字符串相关的知识。<br>**使用** `$` **代替模板中的** `%` | fmt |
| length | `%str_length_string%`<br>`%str_length_minecraft%` | String | 返回 `string` 的长度。 | len |
| trim | `%str_trim_string%` | String | 删除 `string` 左右两端的空格返回。 | |
| uppercase | `%str_uppercase_string%`<br>`%str_uppercase_StRing%` | String | 将 `string` 内所有字母转为大写。 | upper |
| lowercase | `%str_lowercase_string%`<br>`%str_lowercase_StRing%` | String | 将 `string` 内所有字母转为小写。 | lower |

> 在控制台解析这些占位符
> ```
> > papi parse Silence114 %str_charat_string,<0i>%
> [17:47:12 INFO]: s
> > papi parse Silence114 %str_charat_string,<1i>%
> [17:47:27 INFO]: t
> > papi parse Silence114 %str_equals_str1,str2%
> [17:49:19 INFO]: <f>
> > papi parse Silence114 %str_equals_str1,str1%
> [17:49:21 INFO]: <t>
> > papi parse Silence114 %str_indexof_targetStr,Str%
> [17:49:58 INFO]: 6
> > papi parse Silence114 %str_indexof_targetStr,get%
> [17:50:10 INFO]: 3
> > papi parse Silence114 %str_indexof_targetStr,Str,<7i>%
> [17:50:50 INFO]: -1
> > papi parse Silence114 %str_lastindexof_targetStr,Str%
> [17:51:19 INFO]: 6
> > papi parse Silence114 %str_lastindexof_targetStr,Str,<7i>%
> [17:51:36 INFO]: 6
> > papi parse Silence114 %str_startswith_prefixStr,prefix%
> [17:52:30 INFO]: <t>
> > papi parse Silence114 %str_startswith_prefixStr,fix%
> [17:52:34 INFO]: <f>
> > papi parse Silence114 %str_startswith_prefixStr,prefix,<3i>%
> [17:52:57 INFO]: <f>
> > papi parse Silence114 %str_startswith_prefixStr,fix,<3i>%
> [17:53:10 INFO]: <t>
> > papi parse Silence114 %str_endswith_strSuffix,Suffix%
> [17:53:45 INFO]: <t>
> > papi parse Silence114 %str_endswith_strSuffix,Suffix,<3i>%
> [17:53:57 INFO]: <t>
> > papi parse Silence114 %str_replace_minecraft,mine,our%
> [17:55:12 INFO]: ourcraft
> > papi parse Silence114 %str_substring_string,<3i>%
> [17:55:33 INFO]: ing
> > papi parse Silence114 %str_substring_string,<2i>,<4i>%
> [17:55:46 INFO]: ri
> > papi parse Silence114 %str_format_TPS:$.2f,<{server_tps_1}d>%
> [18:51:14 INFO]: TPS:20.00
> > papi parse Silence114 %str_length_string%
> [19:04:22 INFO]: 6
> > papi parse Silence114 %str_length_minecraft%
> [19:04:29 INFO]: 9
> > papi parse Silence114 %str_trim_ Hi! %
> [19:05:39 INFO]: Hi!
> > papi parse Silence114 %str_uppercase_string%
> [19:07:12 INFO]: STRING
> > papi parse Silence114 %str_uppercase_StRing%
> [19:07:17 INFO]: STRING
> > papi parse Silence114 %str_lowercase_string%
> [19:07:53 INFO]: string
> > papi parse Silence114 %str_lowercase_StRing%
> [19:08:08 INFO]: string
> ```

### 扩展数据输出方法
| 方法 | 占位符格式 | 参数类型 | 说明 | 简写 |
| :----: | :---- | :---- | :---- | :----: |
| boolean | `%str_boolean_<t>%`<br>`%str_boolean_<f>%` | boolean | 使用配置中的格式输出本扩展定义的布尔类型。 | bool |
| char | `%str_char_<37a>%`<br>`%str_char_<37>%` | int or char | 返回 `<37a>` 对应的字符，本例返回 `%` | |

> 在控制台解析这些占位符
> ```
> > papi parse Silence114 %str_boolean_<t>%
> [17:39:15 INFO]: O
> > papi parse Silence114 %str_boolean_<f>%
> [17:39:18 INFO]: x
> > papi parse Silence114 %str_char_<37a>%
> [17:39:26 INFO]: %
> > papi parse Silence114 %str_char_<37>%
> [17:39:29 INFO]: %
> ```

### Python str 部分方法实现
| 方法 | 占位符格式 | 参数类型 | 说明 |
| :----: | :---- | :---- | :---- |
| capitalize | `%str_capitalize_alice%` | String | 首字母大写，若首字符不是小写字母则原样返回 |
| center | `%str_center_str,<10i>%` | String, int | 剧中，将 `str` 左右两端填充空格，直至长度为 `10` |
| center | `%str_center_str,<10i>,-%` | String, int, char | 剧中，将 `str` 左右两端填充 `-`，直至长度为 `10` |
| ljust | `%str_ljust_str,<10i>%` | String, int | 左对齐，将 `str` 右端填充空格，直至长度为 `10` |
| ljust | `%str_ljust_str,<10i>,-%` | String, int, char | 左对齐，将 `str` 右端填充 `-`，直至长度为 `10` |
| rjust | `%str_rjust_str,<10i>%` | String, int | 右对齐，将 `str` 左端填充空格，直至长度为 `10` |
| rjust | `%str_rjust_str,<10i>,-`% | String, int, char | 右对齐，将 `str` 左端填充 `-`，直至长度为 `10` |

> `center`，`ljust` 和 `rjust` 可以很方便地制作整齐、漂亮的列表，如聊天区域的列表，记分板列表，甚至 Tab 列表。

> 在控制台解析这些占位符
> ```
> > papi parse Silence114 %str_capitalize_alice%
> [17:16:46 INFO]: Alice
> > papi parse Silence114 %str_center_str,<10i>%
> [17:17:03 INFO]:    str
> > papi parse Silence114 %str_center_str,<10i>,-%
> [17:17:21 INFO]: ---str----
> > papi parse Silence114 %str_ljust_str,<10i>%
> [17:18:53 INFO]: str
> > papi parse Silence114 %str_ljust_str,<10i>,-%
> [17:19:01 INFO]: str-------
> > papi parse Silence114 %str_rjust_str,<10i>%
> [17:19:08 INFO]:        str
> > papi parse Silence114 %str_rjust_str,<10i>,-%
> [17:19:13 INFO]: -------str
> ```

## 配置文件

```yaml
expansions:
  str:
    # 开启后会在解析时从控制台输出更多的信息以供调试使用。
    debug: false
    boolean:
      # 定义布尔值的输出，可以用颜色字符。
      format:
        'true': '&aO'
        'false': '&7x'
      # 开启后，所有输出布尔值的方法都会输出 <t> 或 <f> 以便将结果作为参数输入给其他方法。
      # 若关闭，则将直接输出 format 中定义的格式。
      # 但无论如何，boolean 方法都会按 format 中的定义输出。
      output-parameter-format: true
    # 用于向 `center`，`ljust` 和 `rjust` 方法产生的空白添加前后缀。
    blank:
      head:
        suffix: ''
        prefix: ''
      tail:
        suffix: ''
        prefix: ''
```

### 空白

部分版本的 Minecraft 客户端的空格宽度和其他字母的宽度不一致，导致表格排版混乱，因此可以用 `-`、`_`、`◼️️` 等字符，并将他们颜色设置为黑色 `&0` 、灰色 `&7` 或者深灰色 `&8` 使他们不那么容易被看出来，还能占一个字母的宽度。  其他颜色可用参考 [Minecraft Wiki - 格式化代码]( https://minecraft.fandom.com/zh/wiki/%E6%A0%BC%E5%BC%8F%E5%8C%96%E4%BB%A3%E7%A0%81 )。  
通过 `%str_center_abc,<10i>,-%`、`%str_ljust_abc,<10i>,-%` 和 `%str_rjust_abc,<10i>,-%` 展示这三个方法的运作原理，并介绍 `head` 和 `tail` 的位置。  
首先通过 `/papi parse` 命令查看执行结果如何：
```
> papi parse Silence114 %str_center_abc,<10i>,-%
[19:48:22 INFO]: ---abc----
> papi parse Silence114 %str_ljust_abc,<10i>,-%
[19:48:40 INFO]: abc-------
> papi parse Silence114 %str_rjust_abc,<10i>,-%
[19:48:45 INFO]: -------abc
```
#### center
```
        head  头
        |       tail 尾
        |       |
      [---]abc[----]
      ^   ^   ^    ^
      |   |   |    tail suffix 尾后缀
      |   |   tail prefix 尾前缀
      |   |
      |   head suffix 头后缀
      head prefix 头前缀
```
**中括号是用来标识位置，实际上本例的结果中并不存在，下同。**
#### ljust
```
             tail 尾
             |
      abc[-------]
         ^       ^
         |       tail suffix 尾后缀
         tail prefix 尾前缀
```
#### rjust
```
          head 头
          |
      [-------]abc
      ^       ^
      |       tail suffix 尾后缀
      tail prefix 尾前缀
```

#### 前后缀

添加前后缀的本意是为了能通过颜色代码改变头尾空白的颜色，所以头尾前后缀不会计入总字符串长度。  
例如，还是上面那个 `%str_center_abc,<10i>,-%`，如果我们在配置文件中这样设置：
```yaml
blank:
  head:
    suffix: '&0'
    prefix: '&f'
  tail:
    suffix: '&0'
    prefix: '&f'
```
那它的执行结果为
```
&0---&fabc&0----&f
```
`ljust` 和 `rjust` 也是同理。

## 实战：完善服务器的 Tab 列表

* 服务端：[Mohist]( https://mohistmc.com/ ) 1.16.5-727
* 插件：
  * [TabListPro]( https://www.spigotmc.org/resources/%E2%98%85-tablistpro-%E2%98%85-name-animations-sorting-header-footer-easy-setup-1-8-1-16.21532/ ) 1.2.2
  * [PlaceholderAPI]( https://www.spigotmc.org/resources/placeholderapi.6245/ ) 2.10.9
  * [spark]( https://www.spigotmc.org/resources/spark.57242/ ) 1.6.1
* 扩展：
  * str
  * math (`/papi ecloud download math`)
  * spark
  * player (`/papi ecloud download player`)
  * server (`/papi ecloud download server`)


> 演示所用的 Mohist 版本可能有问题，导致使用 `%server_tps_1_colored%` 获取的 TPS 不是很准确，
> 因此使用 spark-Bukkit 提供的 `%spark_tps_5s`

* 目标：
  * 在列表的头部显示：
    1. CPU 占用（百分比）
    2. 内存占用（已用、总计、百分比）
    3. TPS
    4. 时间
  * 在玩家列表内显示：
    1. 名字
    2. 网络延迟
    3. 生命值
    4. 等级
    5. 世界及位置
      * 世界名称
      * x
      * y
      * z

通过查询 PlaceholderAPI Wiki： [Placeholders]( https://github.com/PlaceholderAPI/PlaceholderAPI/wiki/Placeholders )，可用通过以下变量获取我们想要的信息

| Placeholder | 返回数据 |
| ---- | ---- |
| %spark_cpu_system_10s% | 10 秒内 CPU占 用的平均值 |
| %server_ram_used% | 已用内存 |
| %server_ram_max% | 内存最大可用 |
| %spark_tps_5s% | 5 秒内 TPS 平均值 |
| %server_time_yyyy/MM/dd hh:mm:ss% | 服务器时间格式化，`年/月/日 时:分:秒` |

| Placeholder | 返回数据 |
| ---- | ---- |
| %player_name% | 玩家名字 |
| %player_ping% | 玩家延迟 |
| %player_health_rounded% | 玩家生命值四舍五入取整 |
| %player_max_health_rounded% | 玩家最大生命值四舍五入取整 |
| %player_level% | 玩家等级 |
| %player_world% | 所处世界 |
| %player_x% | 玩家位置x |
| %player_y% | 玩家位置y |
| %player_z% | 玩家位置z |

我们还需要计算内存占用的百分比，可以通过下面的方法计算：
$$\frac{server\_ram\_used}{server\_ram\_max}×100\%$$
上述表达式写成 `math` 能接受的形式，`%math_0_100*{server_ram_used}/{server_ram_max}%`，这个结果是没有单位的，需要自行添加单位：`%`，但由于 `%` 被 PlaceholderAPI 占用，所以使用 `char` 方法返回 `%`，`%str_char_<37>%`。

所以完整的内存占用百分比表示为：`%math_0_100*{server_ram_used}/{server_ram_max}%%str_char_<37>%`。假设内存已用 1024M、总计 2048M，下图解释了它的运行逻辑。

```
              ____1024_____     ____2048____
             /             \   /            \
%math_0_100*{server_ram_used}/{server_ram_max}%%str_char_<37>%
 \____________________  ____________________/   \_____ _____/
                      50                              %
                       \_____________   _____________/
                                     50%
```

### 列表头部

综上，写出列表头部需要显示的内容  
`CPU %spark_cpu_system_10s% Mem %math_1_100*{server_ram_used}/{server_ram_max}%%ascii_37%(%server_ram_used%/%server_ram_max%MB) TPS %spark_tps_5s% Time %server_time_yyyy/MM/dd hh:mm:ss%`  

> 为了阅读方便适当换行的版本：
> ```
> CPU %spark_cpu_system_10s%
> Mem %math_1_100*{server_ram_used}/{server_ram_max}%%ascii_37%(%server_ram_used%/%server_ram_max%MB)
> TPS %spark_tps_5s%
> Time %server_time_yyyy/MM/dd hh:mm:ss%
> ```
> 通过 `/papi parse` 执行
> [!无样式列表头]( https://github.com/sileence114/String-Expansion/blob/master/document/head-parse-1.png )

添加一点格式化代码、特殊符号作为装饰  
`&6CPU&e%spark_cpu_system_10s% &f| &6Mem&e%math_1_100*{server_ram_used}/{server_ram_max}%%ascii_37%&7(&e%server_ram_used%&7/&e%server_ram_max%&6MB&7) &f| &6TPS%spark_tps_5s%&r\n&6Time: &e%server_time_yyyy/MM/dd hh:mm:ss% &6UTC+8&r`  

> 为了阅读方便适当换行的版本：
> ```
> （第一行）
> &6CPU&e%spark_cpu_system_10s% &f| 
> &6Mem&e%math_1_100*{server_ram_used}/{server_ram_max}%%ascii_37%&7(&e%server_ram_used%&7/&e%server_ram_max%&6MB&7) &f| 
> &6TPS%spark_tps_5s%&r\n
> （第二行）
> &6Time: &e%server_time_yyyy/MM/dd hh:mm:ss% &6UTC+8&r
> ```
> 通过 `/papi parse` 执行
> [!有样式列表头]( https://github.com/sileence114/String-Expansion/blob/master/document/head-parse-1.png )

### 列表中的玩家行

写出玩家列表每行要显示的内容  
`%player_name% &e%player_ping%&7ms &e%player_health_rounded%&7/&e%player_max_health_rounded%&c♥ &7lev.&e%player_level% &e%player_world%&7(&e%player_x%&7,&e%player_y%&7,&e%player_z%&7)&r`

> 为了阅读方便适当换行的版本：
> ```
> %player_name%
> &e%player_ping%&7ms
> &e%player_health_rounded%&7/&e%player_max_health_rounded%&c♥
> &7lev.&e%player_level%
> &e%player_world%&7(&e%player_x%&7,&e%player_y%&7,&e%player_z%&7)&r
> ```

若想让玩家列表像个表格一样井井有条，每一列都需要指定的输出长度，用黑色的下划线占位（`_`），请参考[前后缀]( ####前后缀 ) 中提及的配置文件。
| 列 | 指定长度 | 对齐方式 | 对齐后占位符 |
| ---- | ---- | ---- | ---- |
| %player_name% | 玩家 ID，最长为 16 | 左对齐 | %str_ljust_{player_name},<16i>,_% |
| %player_ping% | 玩家延迟，最长为 4，延迟 >9999ms 的玩家会很快离开服务器 | 右对齐 | %str_rjust_{player_ping},<4i>,_% |
| %player_health_rounded% | 玩家当前生命值，最长为 2，若玩家最大生命值 >99 请服主自行修改 | 右对齐 | %str_rjust_{player_health_rounded},<2i>,_% |
| %player_max_health_rounded% | 玩家最大生命值，最长为 2，若玩家最大生命值 >99 请服主自行修改 | 左对齐 | %str_ljust_{player_max_health_rounded},<2i>,_% |
| %player_level% | 玩家等级，最长为 4，就算通过僵尸猪灵刷怪塔，三五百级也顶了天了，根据MineCraft Wiki，生存模式理论最高等级为 `238,609,312`，供参考 | 右对齐 | %str_rjust_{player_level},<4i>,_% |
| %player_world% | 玩家所处的世界，最大长度设置为玩家能到达的世界中，世界名的长度的最大值，本例中最长的世界名为 `twilightforest` 即 14 | 右对齐 | %str_rjust_{player_world},<14i>,_% |
| %player_x% | 玩家位置 x，服务器开放了半径 5000，算上负号，最长为 5 | 右对齐 | %str_rjust_{player_x},<5i>,_% |
| %player_y% | 玩家位置 y，最大 255，最长为 3 | 右对齐 | %str_rjust_{player_y},<3i>,_% |
| %player_z% | 玩家位置 x，服务器开放了半径 5000，算上负号，最长为 5 | 右对齐 | %str_rjust_{player_z},<5i>,_% |
在此之上添加颜色代码、特殊符号作为装饰，因为前缀的格式化字符会重置颜色，因此要将颜色代码也添加到参数前面，长度也需要 +2  
`%str_ljust_{player_name},<16i>,_% &f| %str_rjust_&e{player_ping},<6i>,_%&7ms &f| %str_rjust_&e{player_health_rounded},<4i>,_%&7/%str_ljust_&e{player_max_health_rounded},<4i>,_%&c♥ &f| &7lev.%str_rjust_&e{player_level},<6i>,_% &f| %str_rjust_&e{player_world},<16i>,_%&7(%str_rjust_&e{player_x},<7i>,_%&7,%str_rjust_&e{player_y},<5i>,_%&7,%str_rjust_&e{player_z},<7i>,_%&7)&r`

> 为了阅读方便适当换行的版本：
> ```
> %str_ljust_{player_name},<16i>,_% &f|
> %str_rjust_&e{player_ping},<6i>,_%&7ms &f|
> %str_rjust_&e{player_health_rounded},<4i>,_%&7/%str_ljust_&e{player_max_health_rounded},<4i>,_%&c♥ &f|
> &7lev.%str_rjust_&e{player_level},<6i>,_% &f|
> %str_rjust_&e{player_world},<16i>,_%
> &7(%str_rjust_&e{player_x},<7i>,_%&7,%str_rjust_&e{player_y},<5i>,_%&7,%str_rjust_&e{player_z},<7i>,_%&7)&r
> ```
> 在控制台通过 `/papi parse` 执行多次
> [!控制台执行]( https://github.com/sileence114/String-Expansion/blob/master/document/body-parse-console.png )

至此已经完成准备工作，只需要修改配置文件并重载即可 `/tablistpro reload`，效果图：
[!效果图]( https://github.com/sileence114/String-Expansion/blob/master/document/tab-list.png )

> 备注：
> * MineCraft自带的字体中每个字母并不等宽，显示效果可能并不好，可以尝试将“**语言**”中的“**强制使用Unicode字体**”，关闭，或通过资源包加载一个等宽字体；
> * 效果图有些许溢出，请根据实际情况增减需要展示的列，以及每一列的列宽。

### 配置文件参考

```yaml
header-enabled: true
header:
  - '&6CPU&e%spark_cpu_system_10s% &f| &6Mem&e%math_1_100*{server_ram_used}/{server_ram_max}%%ascii_37%&7(&e%server_ram_used%&7/&e%server_ram_max%&6MB&7) &f| &6TPS%spark_tps_5s%&r\n&6Time: &e%server_time_yyyy/MM/dd hh:mm:ss% &6UTC+8&r'
header-interval: 20
footer-enabled: false
footer:
footer-interval: 20
sortByPerms: []
update-sorting-and-groups: 300
use-displayname: false
prefix: '&8[&bCLAN&8] &f'
suffix: '&f | &7&lNOVICE'
default-group: 'player'
groups:
  # 请参照 TabListPro 的配置文件说明，修改权限组。
  player:
    orHasPermission: 'player.nomal'
    display:
      - '%str_ljust_{player_name},<16i>,_% &f| %str_rjust_&e{player_ping},<6i>,_%&7ms &f| %str_rjust_&e{player_health_rounded},<4i>,_%&7/%str_ljust_&e{player_max_health_rounded},<4i>,_%&c♥ &f| &7lev.%str_rjust_&e{player_level},<6i>,_% &f| %str_rjust_&e{player_world},<16i>,_%&7(%str_rjust_&e{player_x},<7i>,_%&7,%str_rjust_&e{player_y},<5i>,_%&7,%str_rjust_&e{player_z},<7i>,_%&7)&r'
  op:
    orHasPermission: 'op.disable'
    display:
      - '%str_ljust_{player_name},<16i>,_% &f| %str_rjust_&e{player_ping},<6i>,_%&7ms &f| %str_rjust_&e{player_health_rounded},<4i>,_%&7/%str_ljust_&e{player_max_health_rounded},<4i>,_%&c♥ &f| &7lev.%str_rjust_&e{player_level},<6i>,_% &f| %str_rjust_&e{player_world},<16i>,_%&7(%str_rjust_&e{player_x},<7i>,_%&7,%str_rjust_&e{player_y},<5i>,_%&7,%str_rjust_&e{player_z},<7i>,_%&7)&r'
name-animation: 10
no-permission: '&8[&b&lTabList&9&lPro&8] &bYou do not have permission to do that!'
```











# String Expansion

###### English / [中文]( #字符串扩展 )

This is an extension of [PlaceHolderAPI](http://placeholderapi.com/) that provides string modification support.
The case conversion, counting the number of characters, or string cutting, etc. can be well supported. 

> If you don't have any programming language foundation, this may be a little difficult for you, but this document explains the method in detail, please read it patiently.

## How It works

There is a string class inside Java, and most of the functions of this extension are to pass parameters to its methods for processing. 

> Most of the functions refer to the design of the String class. After the incoming string is parsed, it is converted into method and call parameters. 

## Way to use

```
%str_function_parameter1,parameter2……%
```
* Extension ID, function name and parameters are splited by `_`.
* The parameters part consists of multiple variables, separated by a comma `,`. 
* 方法名见[可用占位符与方法]( #可用占位符与方法 )，参类型数严格限制见[参数与数据类型]( #参数与数据类型 )。
* 在参数中可用其他的占位符，使用大括号调用它们。
  * 向本扩展的方法提供其他占位符的运算结果作为参数是基本操作，这能让你动态处理其他扩展返回的字符串。
  * 如占位符 `%player_name%` 应当使用 `{player_name}` 调用。
  * 不用担心大括号内的逗号会引起参数识别问题，大括号能嵌套调用，优先处理内层的大括号。下面**获取玩家ID的首字母并将其大写**的实例将展示这些特点。
    
> 实例：使玩家名称小写（假设玩家名为 **`Silence114`**）
> ```
> %str_lowercase_{player_name}%
> ``` 
> 方法：`lowercase`  
> 参数：`{player_name}` -> **`Silence114`**  
> `%str_lowercase_{player_name}%`  
> = `%str_lowercase_Silence114%`  
> = `silence114`  
>   
> 如果你在控制台执行，且 `PlaceHolderAPI` 配置文件内将 `expansions.str.debug` 设置为 `true`，会得到如下结果。
> ```
> > papi parse Silence114 %str_lowercase_{player_name}%
> [22:01:32 INFO]: [PlaceholderAPI] [String] Parameter: {player_name}    > [Parse bracket placeholder.]
> [22:01:32 INFO]: [PlaceholderAPI] [String] [Parse finished, continue.] > Silence114(BracketPlaceholders) > Silence114
> [22:01:32 INFO]: silence114
> ```
  
> 实例：获取玩家ID的首字母并将其大写（假设玩家名为 **`Silence114`**）
> ```
> %str_uppercase_{str_charat_{player_name},<0i>}%
> ``` 
> 方法：`uppercase`  
> 参数：`{str_charat_{player_name},<0i>}`  
> > 方法：`charat`  
> > 参数：`{player_name}` -> **`Silence114`**  
> > 参数：`<0i>` -> `0`  
> -> `S`  
>   
> `%str_uppercase_{str_charat_{player_name},<0i>}%`  
> = `%str_uppercase_{str_charat_Sileence114,<0i>}%`  
> = `%str_uppercase_S%`  
> = `S`  
> 
> ```
> > papi parse Silence114 %str_uppercase_{str_charat_{player_name},<0i>}%
> [22:02:56 INFO]: [PlaceholderAPI] [String] Parameter: {str_charat_{player_name},<0i>} > [Parse bracket placeholder.]
> [22:02:56 INFO]: [PlaceholderAPI] [String] Parameter: Silence114 > Silence114
> [22:02:56 INFO]: [PlaceholderAPI] [String] Parameter: <0i>       > 0i(TypeTransform) > 0(Force Integer)
> [22:02:56 INFO]: [PlaceholderAPI] [String] [Parse finished, continue.]                > S(BracketPlaceholders) > S
> [22:02:56 INFO]: S
> ```
> 本例中 `uppercase` 似乎没有什么效果，但仍然不能忽略。那是因为不同玩家的ID不同，无法确定首字母是否一定是大写。

## Parameters and data types

因为直接调用到了 Java 的方法，Java 是强类型语言，调用一些方法必须要传入一些类型确定的参数，所以本扩展需要严格定义参数类型。

> 由于 C 语言风格的 `format` 方法，格式化字符串的参数必须要参数与标示符类型相匹配。

| Java 数据类型 | 扩展参数格式 | 示例 | 说明 |
| :----: | :----: | :---- | :---- |
| String | | Minecraft | 字符串类型，无法识别类型的参数都会被视作字符串。如，玩家的 ID，服务器的名称都是字符串类型。 |
| int | \<number`i`\> | `<2i>`=2 `<2.4i>`=2 `<2.9i>`=2 | 整数型，由尖括号`<>`包裹数字，并用`i`强调它是 int：整型。若这个数字有小数部分，会被向下取整。如玩家的等级，玩家的生命值都是整型 |
| double | \<number`d`\> | `<2d>`=2.0 `<2.4d>`=2.4 `<.9d>`=0.9 | 浮点数类型（可以简单的理解为有小数的类型，但实际上d ouble 是取自**双**精度浮点型），由尖括号`<>`包裹数字，并用`d`强调它是 double：浮点型。若这个数字有小数部分，会被向下取整。如玩家的位置等都是浮点型。 |
| Number | \<number\> | `<2>`=2 `<2.4>`=2.4 `<2.9>`=2.9 | 数字型，他包含了整数类型和浮点数类型，尖括号`<>`包裹数字，会自动根据是否有小数点转为整型或浮点型。这只是方便编写用的，对于写死的参数可以这样写，但是对于从其他占位符取来的数据还是通过后缀 `i` 和 `d` 限制比较稳妥。 |
| char | \<number`a`\> \<number`A`\> | `<37a>`=% `<37A>`=7 | 字符型，仅支持 ASCII 字符，number 表示字符在[ ASCII 码表]( https://tool.ip138.com/ascii_code/ )中的编码，小写的`a`表示这个编码是十进制的，大写的`A`表示这个编码是十六进制的。**注意：示例中的“7”表示的是`'7'`这个字符，而不是7这个数字。**|
| boolean | \<`t`\|`f`\> | `<t>`=true `<f>`=false | 布尔型，只有两种值，表示对与错，是与否，常用于表示判断结果。例如，“玩家在主世界”，当他在值就为true，不在值为false。 |

**注意：考虑到方法中输入 ASCII 码表示字符参数比较困难，所有字符类型的参数都可以传入长度为1的字符串，会自动转为字符。**（长度大于1则取首字符）  
由于一些格式字符已经被占用，下列字符需要转义，仅在确认无法转换成其他类型时才会还原转义，并将其识别为字符串。

| 字符 | 转义表达 | 占用原因 |
| :----: | :----: | :---- |
| `%` | `$` | PlaceHolder 标识符 |
| `$` | `\$` | 转义 `%`，format 方法中会大量用到百分号。 |
| `<` | `\<` | 类型转换识别 |
| `>` | `\>` | 类型转换识别 |
| `,` | `\,` | 参数间隔标识 |
| `\` | `\\` | 转义标识 |

由于大括号被用来标记内嵌占位符，而将大括号转义会在识别内嵌占位符时使得解析变得复杂，从而影响性能。
但倘若您真的需要使用大括号，可以使用大括号调用本扩展的 `char` 方法，并传入它们的 ASCII 编码数字。  
`{str_char_<123>}` -> `{`  
`{str_char_<125>}` -> `}`

## Available placeholders and methods 

### Java String Built-in method

| 方法 | 占位符格式 | 参数类型 | 说明 | 简写 |
| :----: | :---- | :---- | :---- | :----: |
| charat| `%str_charat_string,<0i>%`<br>`%str_charat_string,<1i>%` | String, int | 返回 `string` 中在 `0` 号位的字符。**字符串位置是从0开始计数的，即字符串最左端是第0个字符，下同。** | |
| equal | `%str_equals_str1,str2%`<br>`%str_equals_str1,str1%` | String, String | 判断 `str1` 和 `str2` 是否一样。 | eq, = |
| indexof | `%str_indexof_targetStr,Str%`<br>`%str_indexof_targetStr,get%` | String, String | 在 `targetStr` 中查找 `Str`，返回匹配序号，没找到返回`-1`。 | index |
| indexof | `%str_indexof_targetStr,Str,<7i>%` | String, String, int | 在 `targetStr` 中从 `7` 开始查找 `Str`，返回匹配序号，没找到返回 `-1`。 | index |
| lastindexof | `%str_lastindexof_targetStr,Str%` | String, String | 在 `targetStr` 中反向查找 `Str`，返回匹配序号，没找到返回 `-1`。 | last |
| lastindexof | `%str_lastindexof_targetStr,Str,<7i>%` | String, String, int | 在 `targetStr` 中反向从 `7` 开始查找 `Str`，返回匹配序号，没找到返回 `-1`。 | last |
| startswith | `%str_startswith_prefixStr,prefix%`<br>`%str_startswith_prefixStr,fix%` | String, String | 判断 `prefixStr` 是否以 `prefix` 开头。 | start |
| startswith | `%str_startswith_prefixStr,prefix,<3i>%`<br>`%str_startswith_prefixStr,fix,<3i>%` | String, String, int | 从第 `3` 个字符开始，判断后面的半段 `prefixStr` 是否以 `prefix` 开头。 | start |
| endswith | `%str_endswith_strSuffix,Suffix%` | String, String | 判断 `strSuffix` 是否以 `Suffix` 结尾。 | ends |
| endswith | `%str_endswith_strSuffix,Suffix,<3i>%` | String, String, int | 从第 `3` 个字符开始，判断前面的半段 `strSuffix` 是否以 `Suffix` 结尾。 | ends |
| replace | `%str_replace_minecraft_mine_our%` | String, String, String | 将 `minecraft` 中所有 `mine` 替换为 `our`，**区分大小写。** | |
| substring | `%str_substring_string,<3i>%` | String, int | 返回 `string` 从 `3` 到结尾的部分。 | sub |
| substring | `%str_substring_string,<2i>,<4i>%` | String, int, int | 返回 `string` 从 `2` 到 `4` 的部分。注意，包含左端点，不包含右端点，左闭右开。 | sub |
| format | `%str_format_template_,args1,args2,...%`<br>`%str_format_TPS:$.2f,<{server_tps_1}d>%` | String, Object... | 格式化字符串，使用 `arg1`，`arg2` 等参数填入 `template` 模板。这是一个高级方法，需要您了解 [C 语言]( https://www.cplusplus.com/reference/cstdio/printf/ )或 [Java]( http://www.java2s.com/Tutorials/Java/Java_Format/0050__Java_Printf_Style_Overview.htm ) 中关于格式化字符串相关的知识。<br>**使用** `$` **代替模板中的** `%` | fmt |
| length | `%str_length_string%`<br>`%str_length_minecraft%` | String | 返回 `string` 的长度。 | len |
| trim | `%str_trim_string%` | String | 删除 `string` 左右两端的空格返回。 | |
| uppercase | `%str_uppercase_string%`<br>`%str_uppercase_StRing%` | String | 将 `string` 内所有字母转为大写。 | upper |
| lowercase | `%str_lowercase_string%`<br>`%str_lowercase_StRing%` | String | 将 `string` 内所有字母转为小写。 | lower |

> 在控制台解析这些占位符
> ```
> > papi parse Silence114 %str_charat_string,<0i>%
> [17:47:12 INFO]: s
> > papi parse Silence114 %str_charat_string,<1i>%
> [17:47:27 INFO]: t
> > papi parse Silence114 %str_equals_str1,str2%
> [17:49:19 INFO]: <f>
> > papi parse Silence114 %str_equals_str1,str1%
> [17:49:21 INFO]: <t>
> > papi parse Silence114 %str_indexof_targetStr,Str%
> [17:49:58 INFO]: 6
> > papi parse Silence114 %str_indexof_targetStr,get%
> [17:50:10 INFO]: 3
> > papi parse Silence114 %str_indexof_targetStr,Str,<7i>%
> [17:50:50 INFO]: -1
> > papi parse Silence114 %str_lastindexof_targetStr,Str%
> [17:51:19 INFO]: 6
> > papi parse Silence114 %str_lastindexof_targetStr,Str,<7i>%
> [17:51:36 INFO]: 6
> > papi parse Silence114 %str_startswith_prefixStr,prefix%
> [17:52:30 INFO]: <t>
> > papi parse Silence114 %str_startswith_prefixStr,fix%
> [17:52:34 INFO]: <f>
> > papi parse Silence114 %str_startswith_prefixStr,prefix,<3i>%
> [17:52:57 INFO]: <f>
> > papi parse Silence114 %str_startswith_prefixStr,fix,<3i>%
> [17:53:10 INFO]: <t>
> > papi parse Silence114 %str_endswith_strSuffix,Suffix%
> [17:53:45 INFO]: <t>
> > papi parse Silence114 %str_endswith_strSuffix,Suffix,<3i>%
> [17:53:57 INFO]: <t>
> > papi parse Silence114 %str_replace_minecraft,mine,our%
> [17:55:12 INFO]: ourcraft
> > papi parse Silence114 %str_substring_string,<3i>%
> [17:55:33 INFO]: ing
> > papi parse Silence114 %str_substring_string,<2i>,<4i>%
> [17:55:46 INFO]: ri
> > papi parse Silence114 %str_format_TPS:$.2f,<{server_tps_1}d>%
> [18:51:14 INFO]: TPS:20.00
> > papi parse Silence114 %str_length_string%
> [19:04:22 INFO]: 6
> > papi parse Silence114 %str_length_minecraft%
> [19:04:29 INFO]: 9
> > papi parse Silence114 %str_trim_ Hi! %
> [19:05:39 INFO]: Hi!
> > papi parse Silence114 %str_uppercase_string%
> [19:07:12 INFO]: STRING
> > papi parse Silence114 %str_uppercase_StRing%
> [19:07:17 INFO]: STRING
> > papi parse Silence114 %str_lowercase_string%
> [19:07:53 INFO]: string
> > papi parse Silence114 %str_lowercase_StRing%
> [19:08:08 INFO]: string
> ```

### Expand data output methods
| 方法 | 占位符格式 | 参数类型 | 说明 | 简写 |
| :----: | :---- | :---- | :---- | :----: |
| boolean | `%str_boolean_<t>%`<br>`%str_boolean_<f>%` | boolean | 使用配置中的格式输出本扩展定义的布尔类型。 | bool |
| char | `%str_char_<37a>%`<br>`%str_char_<37>%` | int or char | 返回 `<37a>` 对应的字符，本例返回 `%` | |

> 在控制台解析这些占位符
> ```
> > papi parse Silence114 %str_boolean_<t>%
> [17:39:15 INFO]: O
> > papi parse Silence114 %str_boolean_<f>%
> [17:39:18 INFO]: x
> > papi parse Silence114 %str_char_<37a>%
> [17:39:26 INFO]: %
> > papi parse Silence114 %str_char_<37>%
> [17:39:29 INFO]: %
> ```

### Python partial str method implementation
| 方法 | 占位符格式 | 参数类型 | 说明 |
| :----: | :---- | :---- | :---- |
| capitalize | `%str_capitalize_alice%` | String | 首字母大写，若首字符不是小写字母则原样返回 |
| center | `%str_center_str,<10i>%` | String, int | 剧中，将 `str` 左右两端填充空格，直至长度为 `10` |
| center | `%str_center_str,<10i>,-%` | String, int, char | 剧中，将 `str` 左右两端填充 `-`，直至长度为 `10` |
| ljust | `%str_ljust_str,<10i>%` | String, int | 左对齐，将 `str` 右端填充空格，直至长度为 `10` |
| ljust | `%str_ljust_str,<10i>,-%` | String, int, char | 左对齐，将 `str` 右端填充 `-`，直至长度为 `10` |
| rjust | `%str_rjust_str,<10i>%` | String, int | 右对齐，将 `str` 左端填充空格，直至长度为 `10` |
| rjust | `%str_rjust_str,<10i>,-`% | String, int, char | 右对齐，将 `str` 左端填充 `-`，直至长度为 `10` |

> `center`，`ljust` 和 `rjust` 可以很方便地制作整齐、漂亮的列表，如聊天区域的列表，记分板列表，甚至 Tab 列表。

> 在控制台解析这些占位符
> ```
> > papi parse Silence114 %str_capitalize_alice%
> [17:16:46 INFO]: Alice
> > papi parse Silence114 %str_center_str,<10i>%
> [17:17:03 INFO]:    str
> > papi parse Silence114 %str_center_str,<10i>,-%
> [17:17:21 INFO]: ---str----
> > papi parse Silence114 %str_ljust_str,<10i>%
> [17:18:53 INFO]: str
> > papi parse Silence114 %str_ljust_str,<10i>,-%
> [17:19:01 INFO]: str-------
> > papi parse Silence114 %str_rjust_str,<10i>%
> [17:19:08 INFO]:        str
> > papi parse Silence114 %str_rjust_str,<10i>,-%
> [17:19:13 INFO]: -------str
> ```

## Config File

```yaml
expansions:
  str:
    # 开启后会在解析时从控制台输出更多的信息以供调试使用。
    debug: false
    boolean:
      # 定义布尔值的输出，可以用颜色字符。
      format:
        'true': '&aO'
        'false': '&7x'
      # 开启后，所有输出布尔值的方法都会输出 <t> 或 <f> 以便将结果作为参数输入给其他方法。
      # 若关闭，则将直接输出 format 中定义的格式。
      # 但无论如何，boolean 方法都会按 format 中的定义输出。
      output-parameter-format: true
    # 用于向 `center`，`ljust` 和 `rjust` 方法产生的空白添加前后缀。
    blank:
      head:
        suffix: ''
        prefix: ''
      tail:
        suffix: ''
        prefix: ''
```

### Blank

部分版本的 Minecraft 客户端的空格宽度和其他字母的宽度不一致，导致表格排版混乱，因此可以用 `-`、`_`、`◼️️` 等字符，并将他们颜色设置为黑色 `&0` 、灰色 `&7` 或者深灰色 `&8` 使他们不那么容易被看出来，还能占一个字母的宽度。  其他颜色可用参考 [Minecraft Wiki - 格式化代码]( https://minecraft.fandom.com/zh/wiki/%E6%A0%BC%E5%BC%8F%E5%8C%96%E4%BB%A3%E7%A0%81 )。  
通过 `%str_center_abc,<10i>,-%`、`%str_ljust_abc,<10i>,-%` 和 `%str_rjust_abc,<10i>,-%` 展示这三个方法的运作原理，并介绍 `head` 和 `tail` 的位置。  
首先通过 `/papi parse` 命令查看执行结果如何：
```
> papi parse Silence114 %str_center_abc,<10i>,-%
[19:48:22 INFO]: ---abc----
> papi parse Silence114 %str_ljust_abc,<10i>,-%
[19:48:40 INFO]: abc-------
> papi parse Silence114 %str_rjust_abc,<10i>,-%
[19:48:45 INFO]: -------abc
```
#### center
```
        head  头
        |       tail 尾
        |       |
      [---]abc[----]
      ^   ^   ^    ^
      |   |   |    tail suffix 尾后缀
      |   |   tail prefix 尾前缀
      |   |
      |   head suffix 头后缀
      head prefix 头前缀
```
**中括号是用来标识位置，实际上本例的结果中并不存在，下同。**
#### ljust
```
             tail 尾
             |
      abc[-------]
         ^       ^
         |       tail suffix 尾后缀
         tail prefix 尾前缀
```
#### rjust
```
          head 头
          |
      [-------]abc
      ^       ^
      |       tail suffix 尾后缀
      tail prefix 尾前缀
```

#### Perfix & Suffix 

添加前后缀的本意是为了能通过颜色代码改变头尾空白的颜色，所以头尾前后缀不会计入总字符串长度。  
例如，还是上面那个 `%str_center_abc,<10i>,-%`，如果我们在配置文件中这样设置：
```yaml
blank:
  head:
    suffix: '&0'
    prefix: '&f'
  tail:
    suffix: '&0'
    prefix: '&f'
```
那它的执行结果为
```
&0---&fabc&0----&f
```
`ljust` 和 `rjust` 也是同理。

## Actual combat: Improve the server's Tab list

* 服务端：[Mohist]( https://mohistmc.com/ ) 1.16.5-727
* 插件：
  * [TabListPro]( https://www.spigotmc.org/resources/%E2%98%85-tablistpro-%E2%98%85-name-animations-sorting-header-footer-easy-setup-1-8-1-16.21532/ ) 1.2.2
  * [PlaceholderAPI]( https://www.spigotmc.org/resources/placeholderapi.6245/ ) 2.10.9
  * [spark]( https://www.spigotmc.org/resources/spark.57242/ ) 1.6.1
* 扩展：
  * str
  * math (`/papi ecloud download math`)
  * spark
  * player (`/papi ecloud download player`)
  * server (`/papi ecloud download server`)


> 演示所用的 Mohist 版本可能有问题，导致使用 `%server_tps_1_colored%` 获取的 TPS 不是很准确，
> 因此使用 spark-Bukkit 提供的 `%spark_tps_5s`

* 目标：
  * 在列表的头部显示：
    1. CPU 占用（百分比）
    2. 内存占用（已用、总计、百分比）
    3. TPS
    4. 时间
  * 在玩家列表内显示：
    1. 名字
    2. 网络延迟
    3. 生命值
    4. 等级
    5. 世界及位置
      * 世界名称
      * x
      * y
      * z

通过查询 PlaceholderAPI Wiki： [Placeholders]( https://github.com/PlaceholderAPI/PlaceholderAPI/wiki/Placeholders )，可用通过以下变量获取我们想要的信息

| Placeholder | 返回数据 |
| ---- | ---- |
| %spark_cpu_system_10s% | 10 秒内 CPU占 用的平均值 |
| %server_ram_used% | 已用内存 |
| %server_ram_max% | 内存最大可用 |
| %spark_tps_5s% | 5 秒内 TPS 平均值 |
| %server_time_yyyy/MM/dd hh:mm:ss% | 服务器时间格式化，`年/月/日 时:分:秒` |

| Placeholder | 返回数据 |
| ---- | ---- |
| %player_name% | 玩家名字 |
| %player_ping% | 玩家延迟 |
| %player_health_rounded% | 玩家生命值四舍五入取整 |
| %player_max_health_rounded% | 玩家最大生命值四舍五入取整 |
| %player_level% | 玩家等级 |
| %player_world% | 所处世界 |
| %player_x% | 玩家位置x |
| %player_y% | 玩家位置y |
| %player_z% | 玩家位置z |

我们还需要计算内存占用的百分比，可以通过下面的方法计算：
$$\frac{server\_ram\_used}{server\_ram\_max}×100\%$$
上述表达式写成 `math` 能接受的形式，`%math_0_100*{server_ram_used}/{server_ram_max}%`，这个结果是没有单位的，需要自行添加单位：`%`，但由于 `%` 被 PlaceholderAPI 占用，所以使用 `char` 方法返回 `%`，`%str_char_<37>%`。

所以完整的内存占用百分比表示为：`%math_0_100*{server_ram_used}/{server_ram_max}%%str_char_<37>%`。假设内存已用 1024M、总计 2048M，下图解释了它的运行逻辑。

```
              ____1024_____     ____2048____
             /             \   /            \
%math_0_100*{server_ram_used}/{server_ram_max}%%str_char_<37>%
 \____________________  ____________________/   \_____ _____/
                      50                              %
                       \_____________   _____________/
                                     50%
```

### Head of Tablist

综上，写出列表头部需要显示的内容  
`CPU %spark_cpu_system_10s% Mem %math_1_100*{server_ram_used}/{server_ram_max}%%ascii_37%(%server_ram_used%/%server_ram_max%MB) TPS %spark_tps_5s% Time %server_time_yyyy/MM/dd hh:mm:ss%`  

> 为了阅读方便适当换行的版本：
> ```
> CPU %spark_cpu_system_10s%
> Mem %math_1_100*{server_ram_used}/{server_ram_max}%%ascii_37%(%server_ram_used%/%server_ram_max%MB)
> TPS %spark_tps_5s%
> Time %server_time_yyyy/MM/dd hh:mm:ss%
> ```
> 通过 `/papi parse` 执行
> [!无样式列表头]( https://github.com/sileence114/String-Expansion/blob/master/document/head-parse-1.png )

添加一点格式化代码、特殊符号作为装饰  
`&6CPU&e%spark_cpu_system_10s% &f| &6Mem&e%math_1_100*{server_ram_used}/{server_ram_max}%%ascii_37%&7(&e%server_ram_used%&7/&e%server_ram_max%&6MB&7) &f| &6TPS%spark_tps_5s%&r\n&6Time: &e%server_time_yyyy/MM/dd hh:mm:ss% &6UTC+8&r`  

> A version with appropriate line breaks for the convenience of reading: 
> ```
> （第一行）
> &6CPU&e%spark_cpu_system_10s% &f| 
> &6Mem&e%math_1_100*{server_ram_used}/{server_ram_max}%%ascii_37%&7(&e%server_ram_used%&7/&e%server_ram_max%&6MB&7) &f| 
> &6TPS%spark_tps_5s%&r\n
> （第二行）
> &6Time: &e%server_time_yyyy/MM/dd hh:mm:ss% &6UTC+8&r
> ```
> 通过 `/papi parse` 执行
> [!有样式列表头]( https://github.com/sileence114/String-Expansion/blob/master/document/head-parse-1.png )

### Player line

写出玩家列表每行要显示的内容  
`%player_name% &e%player_ping%&7ms &e%player_health_rounded%&7/&e%player_max_health_rounded%&c♥ &7lev.&e%player_level% &e%player_world%&7(&e%player_x%&7,&e%player_y%&7,&e%player_z%&7)&r`

> A version with appropriate line breaks for the convenience of reading: 
> ```
> %player_name%
> &e%player_ping%&7ms
> &e%player_health_rounded%&7/&e%player_max_health_rounded%&c♥
> &7lev.&e%player_level%
> &e%player_world%&7(&e%player_x%&7,&e%player_y%&7,&e%player_z%&7)&r
> ```

若想让玩家列表像个表格一样井井有条，每一列都需要指定的输出长度，用黑色的下划线占位（`_`），请参考[前后缀]( ####前后缀 ) 中提及的配置文件。
| 列 | 指定长度 | 对齐方式 | 对齐后占位符 |
| ---- | ---- | ---- | ---- |
| %player_name% | 玩家 ID，最长为 16 | 左对齐 | %str_ljust_{player_name},<16i>,_% |
| %player_ping% | 玩家延迟，最长为 4，延迟 >9999ms 的玩家会很快离开服务器 | 右对齐 | %str_rjust_{player_ping},<4i>,_% |
| %player_health_rounded% | 玩家当前生命值，最长为 2，若玩家最大生命值 >99 请服主自行修改 | 右对齐 | %str_rjust_{player_health_rounded},<2i>,_% |
| %player_max_health_rounded% | 玩家最大生命值，最长为 2，若玩家最大生命值 >99 请服主自行修改 | 左对齐 | %str_ljust_{player_max_health_rounded},<2i>,_% |
| %player_level% | 玩家等级，最长为 4，就算通过僵尸猪灵刷怪塔，三五百级也顶了天了，根据MineCraft Wiki，生存模式理论最高等级为 `238,609,312`，供参考 | 右对齐 | %str_rjust_{player_level},<4i>,_% |
| %player_world% | 玩家所处的世界，最大长度设置为玩家能到达的世界中，世界名的长度的最大值，本例中最长的世界名为 `twilightforest` 即 14 | 右对齐 | %str_rjust_{player_world},<14i>,_% |
| %player_x% | 玩家位置 x，服务器开放了半径 5000，算上负号，最长为 5 | 右对齐 | %str_rjust_{player_x},<5i>,_% |
| %player_y% | 玩家位置 y，最大 255，最长为 3 | 右对齐 | %str_rjust_{player_y},<3i>,_% |
| %player_z% | 玩家位置 x，服务器开放了半径 5000，算上负号，最长为 5 | 右对齐 | %str_rjust_{player_z},<5i>,_% |
在此之上添加颜色代码、特殊符号作为装饰，因为前缀的格式化字符会重置颜色，因此要将颜色代码也添加到参数前面，长度也需要 +2  
`%str_ljust_{player_name},<16i>,_% &f| %str_rjust_&e{player_ping},<6i>,_%&7ms &f| %str_rjust_&e{player_health_rounded},<4i>,_%&7/%str_ljust_&e{player_max_health_rounded},<4i>,_%&c♥ &f| &7lev.%str_rjust_&e{player_level},<6i>,_% &f| %str_rjust_&e{player_world},<16i>,_%&7(%str_rjust_&e{player_x},<7i>,_%&7,%str_rjust_&e{player_y},<5i>,_%&7,%str_rjust_&e{player_z},<7i>,_%&7)&r`

> A version with appropriate line breaks for the convenience of reading: 
> ```
> %str_ljust_{player_name},<16i>,_% &f|
> %str_rjust_&e{player_ping},<6i>,_%&7ms &f|
> %str_rjust_&e{player_health_rounded},<4i>,_%&7/%str_ljust_&e{player_max_health_rounded},<4i>,_%&c♥ &f|
> &7lev.%str_rjust_&e{player_level},<6i>,_% &f|
> %str_rjust_&e{player_world},<16i>,_%
> &7(%str_rjust_&e{player_x},<7i>,_%&7,%str_rjust_&e{player_y},<5i>,_%&7,%str_rjust_&e{player_z},<7i>,_%&7)&r
> ```
> Use `/papi parse` on console.
> [!Console Parse]( https://github.com/sileence114/String-Expansion/blob/master/document/body-parse-console.png )

So far, the preparations have been completed. You only need to modify the configuration file and reload it with `/tablistpro reload`. The tabList screenshot: 
[!TabList Screenshot]( https://github.com/sileence114/String-Expansion/blob/master/document/tab-list.png )

> Note：
> * Letter of MineCraft’s build-in fonts is not equal in width, so the display effect may not be good. You can try to turn off "**Force Unicode Font**" in "**Language**", or load a monospace font through the resource pack.
> * There is a slight overflow in the rendering. Please increase or decrease the columns to be displayed and the column width of each column according to the actual situation.

### Configuration file reference

```yaml
header-enabled: true
header:
  - '&6CPU&e%spark_cpu_system_10s% &f| &6Mem&e%math_1_100*{server_ram_used}/{server_ram_max}%%ascii_37%&7(&e%server_ram_used%&7/&e%server_ram_max%&6MB&7) &f| &6TPS%spark_tps_5s%&r\n&6Time: &e%server_time_yyyy/MM/dd hh:mm:ss% &6UTC+8&r'
header-interval: 20
footer-enabled: false
footer:
footer-interval: 20
sortByPerms: []
update-sorting-and-groups: 300
use-displayname: false
prefix: '&8[&bCLAN&8] &f'
suffix: '&f | &7&lNOVICE'
default-group: 'player'
groups:
  # Please refer TabListPro profile comments, modify the permission group.
  player:
    orHasPermission: 'player.nomal'
    display:
      - '%str_ljust_{player_name},<16i>,_% &f| %str_rjust_&e{player_ping},<6i>,_%&7ms &f| %str_rjust_&e{player_health_rounded},<4i>,_%&7/%str_ljust_&e{player_max_health_rounded},<4i>,_%&c♥ &f| &7lev.%str_rjust_&e{player_level},<6i>,_% &f| %str_rjust_&e{player_world},<16i>,_%&7(%str_rjust_&e{player_x},<7i>,_%&7,%str_rjust_&e{player_y},<5i>,_%&7,%str_rjust_&e{player_z},<7i>,_%&7)&r'
  op:
    orHasPermission: 'op.disable'
    display:
      - '%str_ljust_{player_name},<16i>,_% &f| %str_rjust_&e{player_ping},<6i>,_%&7ms &f| %str_rjust_&e{player_health_rounded},<4i>,_%&7/%str_ljust_&e{player_max_health_rounded},<4i>,_%&c♥ &f| &7lev.%str_rjust_&e{player_level},<6i>,_% &f| %str_rjust_&e{player_world},<16i>,_%&7(%str_rjust_&e{player_x},<7i>,_%&7,%str_rjust_&e{player_y},<5i>,_%&7,%str_rjust_&e{player_z},<7i>,_%&7)&r'
name-animation: 10
no-permission: '&8[&b&lTabList&9&lPro&8] &bYou do not have permission to do that!'
```
