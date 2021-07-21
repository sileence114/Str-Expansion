# 字符串拓展

###### [English]() / 中文

这是一个 [PlaceHolderAPI](http://placeholderapi.com/) 的拓展，提供了字符串修改支持。
小如大小写转换，字符个数统计；大如字符串切割，格式化字符串都能胜任。

> 如果您没有任何编程语言基础，这对于您来说可能回有一点难，但也别害怕，我会从最基础的开始介绍。
> 请耐心看完，正如当年从 0 开始学会学会启动 Minecraft 服务器一样，困难就是被用来克服的，加油！

## 它是怎样工作的

Java 内部有个字符串类，该拓展的大部分功能都是将参数传递给他的方法来处理。

> 大部分功能都参考了 String 类的设计，将传入的字符串解析后，
> 转换为方法和调用这个方法所需要的参数。

## 调用方式

```
%str_方法名_参数1,参数2……%
```
* 拓展 ID、方法名、参数之间用下划线`_`分隔。
* 参数部分由多个变量组成，互相之间用半角逗号`,`分隔。  
  （请注意：全角逗号`，`与半角逗号`,`是不一样的。）
* 方法名见[可用占位符与方法]( #可用占位符与方法 )，参类型数严格限制见[参数与数据类型]( #参数与数据类型 )。
* 在参数中可用其他的占位符，使用大括号调用它们。
  * 向本拓展的方法提供其他占位符的运算结果作为参数是基本操作，这能让你动态处理其他扩展返回的字符串。
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

因为直接调用到了 Java 的方法，Java 是强类型语言，调用一些方法必须要传入一些类型确定的参数，所以本拓展需要严格定义参数类型。

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
但倘若您真的需要使用大括号，可以使用大括号调用本拓展的 `char` 方法，并传入它们的 ASCII 编码数字。  
`{str_char_<123>}` -> `{`  
`{str_char_<125>}` -> `}`

## 可用占位符与方法

### Java内建方法

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

### 拓展数据输出方法
| 方法 | 占位符格式 | 参数类型 | 说明 | 简写 |
| :----: | :---- | :---- | :---- | :----: |
| boolean | `%str_boolean_<t>%`<br>`%str_boolean_<f>%` | boolean | 使用配置中的格式输出本拓展定义的布尔类型。 | bool |
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

### Python部分字符串方法实现
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
**中括号是用来标识位置，实际上本例中并不存在，下同。**
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

因前后缀的长度并不会导致一列的数据参差不齐，而且添加前后缀的动机就是为了能通过颜色代码改变头尾空白的颜色，所以头尾前后缀不会计入总字符串长度。  
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

