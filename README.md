# 字符串拓展
这是一个 [PlaceHolderAPI](http://placeholderapi.com/) 的拓展，提供了字符串修改支持。
小如大小写转换，字符个数统计；大如字符串切割，格式化字符串都能胜任。

> 如果您没有任何编程语言基础，这对于您来说可能回有一点难，别害怕，我会从最基础的开始介绍。
> 请耐心看完，正如你当时学会启动 Minecraft 服务器一样，困难就是被用来克服的，加油！

## 它是怎样工作的

Java 内部有个字符串类，该拓展的大部分功能都是将参数传递给他的方法来处理。

> 大部分功能都参考了 String 类的设计，将传入的字符串解析后，
> 转换为方法，以及调用这个方法所需要的参数。
> 
> 后续会用 Java 实现一些其他编程语言的字符串处理工具，如 Python 的首字母大写等。

## 参数与数据类型

因为直接调用到了 Java 的方法，Java 是强类型语言，调用一些方法必须要传入一些类型确定的参数，所以本拓展需要严格定义参数类型。

> 主要是由于C语言风格的 `String.format()` 格式化字符串必须要参数与标示符类型相匹配。

| Java 数据类型 | 扩展参数格式 | 示例 | 说明 |
|  :----:  | :----:  |  :----  | :----  |
| String | | Minecraft | 字符串类型，无法识别类型的参数都会被视作字符串。如，玩家的 ID，服务器的名称都是字符串类型。 |
| int | \<number`i`\> | `<2i>`=2 `<2.4i>`=2 `<2.9i>`=2 | 整数类型，由尖括号`<>`包裹数字，并用`i`强调它是 int：整型。若这个数字有小数部分，会被向下取整。如玩家的等级，玩家的生命值都是整型 |
| double | \<number`d`\> | `<2d>`=2.0 `<2.4d>`=2.4 `<.9d>`=0.9 | 浮点数类型~~（可以简单的理解为有小数的类型，但实际上是**双**精度浮点型）~~，由尖括号`<>`包裹数字，并用`d`强调它是 double：浮点型。若这个数字有小数部分，会被向下取整。如玩家的位置等都是浮点型。 |
| Number | \<number\> | `<2>`=2 `<2.4>`=2.4 `<2.9>`=2.9 | 数字型，他包含了整数类型和浮点数类型，尖括号`<>`包裹数字，会自动根据是否有小数点转为整型或浮点型。~~（实际上Java并不能实例化Number类型）~~ |
| char | \<number`a`\> \<number`A`\> | `<37a>`=% `<37A>`=7 | 字符型，仅支持 ASCII 字符，number 表示字符在[ ASCII 码表]( https://tool.ip138.com/ascii_code/ )中的编码，小写的`a`表示这个编码是十进制的，大写的`A`表示这个编码是十六进制的。**注意：示例中的“7”表示的是`'7'`这个字符，而不是7这个数字。**|
| boolean | \<`t`/`f`\> | `<t>`=true `<f>`=false | 布尔型，只有两种值，表示对与错，是与否，常用于表示判断结果。例如，“玩家在主世界”，当他在值就为true，不在值为false |

## 可用占位符与方法
| 方法 | 占位符格式 | 说明 | 简写 |
| ---- | ---- | ---- | ---- |
| equal | `%str_equal_str1,str2%` | 判断 `str1` 和 `str2` 是否一样。 | `%str_eq_str1,str2%` `%str_=_str1,str2%` |
| indexof | `%str_indexof_targetStr,Str%` | 在 `targetStr` 中查找 `Str`，返回匹配序号，没找到返回`-1`。  | `%str_index_Str,Str%` |
| indexof | `%str_indexof_targetStr,Str,<7i>%` | 在 `targetStr` 中从`7`开始查找 `Str`，返回匹配序号，没找到返回 `-1`。 | `%str_index_Str,Str,<7i>%` |
| lastindexof | `%str_lastindexof_targetStr,Str%` | 在 `targetStr` 中反向查找 `Str`，返回匹配序号，没找到返回 `-1`。  | `%str_lastindexof_Str,Str%` |
| lastindexof | `%str_lastindexof_targetStr,Str,<7i>%` | 在 `targetStr` 中反向从 `7` 开始查找 `Str`，返回匹配序号，没找到返回 `-1`。 | `%str_lastindexof_Str,Str,<7i>%` |
| startswith | `%str_startswith_prefixStr,prefix%` | 判断 `prefixStr` 是否以 `prefix` 开头 | `%str_start_prefixStr,prefix%` |
| startswith | `%str_startswith_prefixStr,prefix,<3i>%` | 从第 `3` 个字符开始，判断后面的半段 `prefixStr` 是否以 `prefix` 开头 | `%str_start_prefixStr,prefix,<3i>%` |
| endswith | `%str_endswith_strSuffix,Suffix%` | 判断 `strSuffix` 是否以 `Suffix` 结尾 | `%str_ends_strSuffix,Suffix%` |
| endswith | `%str_endswith_strSuffix,Suffix,<3i>%` | 从第 `3` 个字符开始，判断前面的半段 `strSuffix` 是否以 `Suffix` 结尾 | `%str_ends_strSuffix,Suffix,<3i>%` |

