# 字符串拓展
这是一个 [PlaceHolderAPI](http://placeholderapi.com/) 的拓展，提供了字符串修改支持。
小如大小写转换，字符个数统计；大如字符串切割，格式化字符串都能胜任。

> 如果您没有任何编程语言基础，这对于您来说可能回有一点难，别害怕，我会从最基础的开始介绍。
> 请耐心看完，正如当年学会启动 Minecraft 服务器一样，困难就是被用来克服的，加油！

## 它是怎样工作的

Java 内部有个字符串类，该拓展的大部分功能都是将参数传递给他的方法来处理。

> 大部分功能都参考了 String 类的设计，将传入的字符串解析后，
> 转换为方法，以及调用这个方法所需要的参数。
> 
> 后续会用 Java 实现一些其他编程语言的字符串处理工具，如 Python 的首字母大写等。

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
> 
> -> `S`  
> `%str_uppercase_{str_charat_{player_name},<0i>}%`  
> = `%str_uppercase_{str_charat_Sileence114,<0i>}%`  
> = `%str_uppercase_S%`  
> = `S`  
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

> 主要是由于C语言风格的 `String.format()` 格式化字符串必须要参数与标示符类型相匹配。

| Java 数据类型 | 扩展参数格式 | 示例 | 说明 |
|  :----:  | :----:  |  :----  | :----  |
| String | | Minecraft | 字符串类型，无法识别类型的参数都会被视作字符串。如，玩家的 ID，服务器的名称都是字符串类型。 |
| int | \<number`i`\> | `<2i>`=2 `<2.4i>`=2 `<2.9i>`=2 | 整数型，由尖括号`<>`包裹数字，并用`i`强调它是 int：整型。若这个数字有小数部分，会被向下取整。如玩家的等级，玩家的生命值都是整型 |
| double | \<number`d`\> | `<2d>`=2.0 `<2.4d>`=2.4 `<.9d>`=0.9 | 浮点数类型~~（可以简单的理解为有小数的类型，但实际上是**双**精度浮点型）~~，由尖括号`<>`包裹数字，并用`d`强调它是 double：浮点型。若这个数字有小数部分，会被向下取整。如玩家的位置等都是浮点型。 |
| Number | \<number\> | `<2>`=2 `<2.4>`=2.4 `<2.9>`=2.9 | 数字型，他包含了整数类型和浮点数类型，尖括号`<>`包裹数字，会自动根据是否有小数点转为整型或浮点型。~~（实际上Java并不能实例化Number类型）~~ |
| char | \<number`a`\> \<number`A`\> | `<37a>`=% `<37A>`=7 | 字符型，仅支持 ASCII 字符，number 表示字符在[ ASCII 码表]( https://tool.ip138.com/ascii_code/ )中的编码，小写的`a`表示这个编码是十进制的，大写的`A`表示这个编码是十六进制的。**注意：示例中的“7”表示的是`'7'`这个字符，而不是7这个数字。**|
| boolean | \<`t`/`f`\> | `<t>`=true `<f>`=false | 布尔型，只有两种值，表示对与错，是与否，常用于表示判断结果。例如，“玩家在主世界”，当他在值就为true，不在值为false |


## 可用占位符与方法
| 方法 | 占位符格式 | 说明 | 简写 |
| ---- | ---- | ---- | ---- |
| charat| `%str_charat_string_<0i>%` | 返回 `string` 中在 `0` 号位的字符。**字符串位置是从0开始计数的，即字符串最左端是第0个字符，下同。** | `%str_char_string_<0i>%` |
| equal | `%str_equal_str1,str2%` | 判断 `str1` 和 `str2` 是否一样。 | `%str_eq_str1,str2%` `%str_=_str1,str2%` |
| indexof | `%str_indexof_targetStr,Str%` | 在 `targetStr` 中查找 `Str`，返回匹配序号，没找到返回`-1`。  | `%str_index_Str,Str%` |
| indexof | `%str_indexof_targetStr,Str,<7i>%` | 在 `targetStr` 中从 `7` 开始查找 `Str`，返回匹配序号，没找到返回 `-1`。 | `%str_index_Str,Str,<7i>%` |
| lastindexof | `%str_lastindexof_targetStr,Str%` | 在 `targetStr` 中反向查找 `Str`，返回匹配序号，没找到返回 `-1`。  | `%str_lastindexof_Str,Str%` |
| lastindexof | `%str_lastindexof_targetStr,Str,<7i>%` | 在 `targetStr` 中反向从 `7` 开始查找 `Str`，返回匹配序号，没找到返回 `-1`。 | `%str_lastindexof_Str,Str,<7i>%` |
| startswith | `%str_startswith_prefixStr,prefix%` | 判断 `prefixStr` 是否以 `prefix` 开头 | `%str_start_prefixStr,prefix%` |
| startswith | `%str_startswith_prefixStr,prefix,<3i>%` | 从第 `3` 个字符开始，判断后面的半段 `prefixStr` 是否以 `prefix` 开头 | `%str_start_prefixStr,prefix,<3i>%` |
| endswith | `%str_endswith_strSuffix,Suffix%` | 判断 `strSuffix` 是否以 `Suffix` 结尾 | `%str_ends_strSuffix,Suffix%` |
| endswith | `%str_endswith_strSuffix,Suffix,<3i>%` | 从第 `3` 个字符开始，判断前面的半段 `strSuffix` 是否以 `Suffix` 结尾 | `%str_ends_strSuffix,Suffix,<3i>%` |
| substring | `%str_substring_string,<3i>%` | 返回 `string` 从 `3` 到结尾的部分 | `%str_sub_string,<3i>%` |
| substring | `%str_substring_string,<2i>,<4i>%` | 返回 `string` 从 `2` 到 `4` 的部分 | `%str_sub_string,<2i>,<4i>%` |
| format | `%str_format_template_,args1,args2,...%` | 格式化字符串，使用 `arg1`，`arg2` 等参数填入 `template` 模板。 | `%str_format_template_,args1,args2,...%` |
| length | `%str_length_string%` | 返回 `string` 的长度 | `%str_len_string%` |
| trim | `%str_trim_string%` | 删除 `string` 左右两端的空格返回 | |
| uppercase | `%str_uppercase_string%` | 将 `string` 内所有字母转为大写 | `%str_upper_string%` |
| lowercase | `%str_lowercase_string%` | 将 `string` 内所有字母转为小写 | `%str_lower_string%` |
| boolean | `%str_boolean_<t>%` | 使用配置中的格式输出本拓展定义的布尔类型 | `%str_boolean_<t>%` |
