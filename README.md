# String Expansion

###### English / [中文]( https://github.com/sileence114/Str-Expansion/edit/master/README_cn.md )

This is an extension of [PlaceHolderAPI](http://placeholderapi.com/) that provides string modification support.
The case conversion, counting the number of characters, or string cutting, etc. can be well supported. 

> If you don't have any programming language foundation, this may be a little difficult for you, but this document explains the function in detail, please read it patiently.

## How It works

There is a string class inside Java, and most of the functions of this extension are to pass parameters to its functions for processing. 

> Most of the functions refer to the design of the String class. After the incoming string is parsed, it is converted into function and call parameters. 

## Way to use

```
%str_function_parameter1,parameter2……%
```
* Extension ID, function name and parameters are splited by `_`. 
* The parameters part consists of multiple variables, separated by a comma `,`. 
* Function names see [Available placeholders and functions]( #available-placeholders-and-functions ), Parameter types see [Parameters and data types]( #parameters-and-data-types )。
* Other placeholders can be used in the parameters, use curly braces to call them.
  * The operation results of other placeholders can be provided as parameters to the function of this extension, which allows you to dynamically process the strings returned by other extensions. 
  * For Example, `%player_name%` should called with `{player_name}`. 
  * The comma in the braces will not be treated as a parameter separator. 
  * Braces can be called nested, and inner braces are processed first. 
  The following examples will demonstrate these features. 
    
> Example: Make the player name lowercase. (assuming the player name is **`Silence114`**) 
> ```
> %str_lowercase_{player_name}%
> ``` 
> Functions: `lowercase`  
> Parameter: `{player_name}` -> **`Silence114`**  
> `%str_lowercase_{player_name}%`  
> = `%str_lowercase_Silence114%`  
> = `silence114`  
>   
> If you execute it in the console and set `expansions.str.debug` to `true` in the PlaceHolderAPI configuration file, you will get the following result. 
> ```
> > papi parse Silence114 %str_lowercase_{player_name}%
> [22:01:32 INFO]: [PlaceholderAPI] [String] Parameter: {player_name}    > [Parse bracket placeholder.]
> [22:01:32 INFO]: [PlaceholderAPI] [String] [Parse finished, continue.] > Silence114(BracketPlaceholders) > Silence114
> [22:01:32 INFO]: silence114
> ```
  
> Example: Get the first letter of the player ID and capitalize it (assuming the player's name is **`Silence114`**) 
> ```
> %str_uppercase_{str_charat_{player_name},<0i>}%
> ``` 
> Functions: `uppercase`  
> Parameter: `{str_charat_{player_name},<0i>}`  
> > Functions: `charat`  
> > Parameter: `{player_name}` -> **`Silence114`**  
> > Parameter: `<0i>` -> `0`  
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
> In this case `uppercase` seems to have no effect, but it still cannot be ignored. That's because different players have different IDs, so it is impossible to assert the first letter is uppercase if didn't judgment.

## Parameters and data types

Because Java functions are directly called, Java is a strongly typed language, and certain types of parameters must be passed in to call some functions, so this extension needs to strictly define the parameter types. 

> Due to the C language style `format` function, the parameters of the format string must match the type of the identifier. 

| Java data type | Parameter format in this expansion | Example | Illustrate |
| :----: | :----: | :---- | :---- |
| String | | Minecraft | String type, unrecognized parameters will be treated as strings. For example, the player's ID and the server's name are all string types. |
| int | \<number`i`\> | `<2i>`=2 `<2.4i>`=2 `<2.9i>`=2 | Integer type, surrounded by angle brackets `<>` and used `i` to emphasize that it is an int: integer. If this number has a decimal part, it will be rounded down. For example, the player's level, the player's health value is integer. |
| double | \<number`d`\> | `<2d>`=2.0 `<2.4d>`=2.4 `<.9d>`=0.9 | Double float point number type, the number is wrapped by angle brackets `<>`, and `d` is used to emphasize that it is double: Floating point type. If this number has a decimal part, it will be rounded down. Such as the position of the player are all floating-point types. |
| Number | \<number\> | `<2>`=2 `<2.4>`=2.4 `<2.9>`=2.9 | Number type, it contains integer type and floating point type, angle bracket `<>` wraps the number, it will be automatically converted to integer or floating point according to whether there is a decimal point. This is just convenient for writing. It can be written like this for hard-coded parameters, but for data fetched from other placeholders, it is safer to restrict the suffixes `i` and `d`. |
| char | \<number`a`\> \<number`A`\> | `<37a>`=% `<37A>`=7 | Character type, only supports ASCII characters, number represents the code of the character in the ASCII code table, lowercase `a` indicates that the code is decimal, and uppercase `A` indicates that the code is hexadecimal. **Note: The "7" in the example represents the character `'7'`, not the number seven.** |
| boolean | \<`t`\|`f`\> | `<t>`=true `<f>`=false | Boolean type, there are only two values, indicating right and wrong, yes or not, and it is often used to indicate the result of judgment. For example, "the player is in the main world", the value is true when he is in and the value is false when he is not. |

**Note: Considering that it is difficult to input ASCII code to represent character parameters in the function, all character type parameters can be passed in a string of length 1, which will be automatically converted into characters. ** (The first character is taken if the length is greater than 1)  
Because some format characters are already occupied, the following characters need to be escaped. Only when it is confirmed that they cannot be converted to other types, the escape will be restored and recognized as a string. 

| Character | Escape expression | Reason for occupation | 
| :----: | :----: | :---- |
| `%` | `$` | PlaceHolder Identification symbol. |
| `$` | `\$` | Escaping `%`, the percent sign will be used a lot in the format function. |
| `<` | `\<` | Type conversion recognition. |
| `>` | `\>` | Type conversion recognition. |
| `,` | `\,` | Parameter interval identifier. |
| `\` | `\\` | Escape flag. |

Because braces are used to mark embedded placeholders, escaping the braces will complicate the parsing when recognizing embedded placeholders, thereby affecting performance.
But if you really need to use braces, you can use the braces to call the extended `char` function and pass in their ASCII code numbers.  
`{str_char_<123>}` -> `{`  
`{str_char_<125>}` -> `}`

## Available placeholders and functions 

### Java String Built-in function

| Function | Placeholder format | Parameter type | Description | Abbreviation |
| :----: | :---- | :---- | :---- | :----: |
| charat| `%str_charat_string,<0i>%`<br>`%str_charat_string,<1i>%` | String, int | Returns the character in the `0` position in `string`. **The position of the string is counted from 0, that is, the leftmost end of the string is the 0th character, the same below. ** | |
| equal | `%str_equals_str1,str2%`<br>`%str_equals_str1,str1%` | String, String | Determine whether `str1` and `str2` are the same. | eq, = |
| indexof | `%str_indexof_targetStr,Str%`<br>`%str_indexof_targetStr,get%` | String, String | Search for `Str` in `targetStr`, return the matching sequence number, and return `-1` if not found. | index |
| indexof | `%str_indexof_targetStr,Str,<7i>%` | String, String, int | Search for `Str` in `targetStr` from the position of `7`, return the matching sequence number, and return `-1` if it is not found. | index |
| lastindexof | `%str_lastindexof_targetStr,Str%` | String, String | Reverse search for `Str` in `targetStr`, return the matching sequence number, return `-1` if not found. | last |
| lastindexof | `%str_lastindexof_targetStr,Str,<7i>%` | String, String, int | In `targetStr`, search for `Str` from `7` in the reverse direction, return the matching sequence number, and return `-1` if it is not found. | last |
| startswith | `%str_startswith_prefixStr,prefix%`<br>`%str_startswith_prefixStr,fix%` | String, String | Determine whether `prefixStr` starts with `prefix`. | start |
| startswith | `%str_startswith_prefixStr,prefix,<3i>%`<br>`%str_startswith_prefixStr,fix,<3i>%` | String, String, int | Starting from the `3`(rd) character, determine whether the following half of `prefixStr` starts with `prefix`. | start |
| endswith | `%str_endswith_strSuffix,Suffix%` | String, String | Determine whether `strSuffix` ends with `Suffix`. | ends |
| endswith | `%str_endswith_strSuffix,Suffix,<3i>%` | String, String, int | Starting from the `3` character, determine whether the first half of `strSuffix` ends with `Suffix`. | ends |
| replace | `%str_replace_minecraft_mine_our%` | String, String, String | Replace all `mine` in `minecraft` with `our`, ** is case sensitive. ** | |
| substring | `%str_substring_string,<3i>%` | String, int | Returns the part of `string` from `3` to the end. | sub |
| substring | `%str_substring_string,<2i>,<4i>%` | String, int, int | Returns the part of `string` from `2` to `4`. Note that the left end point is included, the right end point is not included, a left-closed right-open interval. | sub |
| format | `%str_format_template_,args1,args2,...%`<br>`%str_format_TPS:$.2f,<{server_tps_1}d>%` | String, Object... | Format the string, use parameters such as `arg1` and `arg2` to fill in the `template` template. This is an advanced function that requires you to understand the knowledge of formatting strings in [C programming language]( https://www.cplusplus.com/reference/cstdio/printf/ ) or [Java]( http://www.java2s.com/Tutorials/Java/Java_Format/0050__Java_Printf_Style_Overview.htm ). **Use ** `$` ** instead of ** `%` in the template. | fmt |
| length | `%str_length_string%`<br>`%str_length_minecraft%` | String | Return length of `string`. | len |
| trim | `%str_trim_string%` | String | Delete the spaces at the left and right ends of `string` and return. | |
| uppercase | `%str_uppercase_string%`<br>`%str_uppercase_StRing%` | String | All letters in `string` are converted to uppercase. | upper |
| lowercase | `%str_lowercase_string%`<br>`%str_lowercase_StRing%` | String | All letters in `string` are converted to lowercase. | lower |

> Parse these Placeholaders on console.
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

### Expand data output functions
| Function | Placeholder format | Parameter type | Description | Abbreviation |
| :----: | :---- | :---- | :---- | :----: |
| boolean | `%str_boolean_<t>%`<br>`%str_boolean_<f>%` | boolean | Use the format in the configuration to output the Boolean type defined by this extension. | bool |
| char | `%str_char_<37a>%`<br>`%str_char_<37>%` | int or char | Returns the character corresponding to `<37a>`, this example returns `%`. | |

> Parse these Placeholaders on console.
> ```
> > papi parse Silence114 %str_boolean_<t>%
> [17:39:15 INFO]: O
> > papi parse Silence114 %str_boolean_<f>%
> [17:39:18 INFO]: X
> > papi parse Silence114 %str_char_<37a>%
> [17:39:26 INFO]: %
> > papi parse Silence114 %str_char_<37>%
> [17:39:29 INFO]: %
> ```

### Python partial str function implementation

| Function | Placeholder format | Parameter type | Description |
| :----: | :---- | :---- | :---- |
| capitalize | `%str_capitalize_alice%` | String | Capitalize the first letter, if the first character is not a lowercase letter, return it as it is. |
| center | `%str_center_str,<10i>%` | String, int | Center, fill the left and right ends of `str` with spaces until the length is `10`. |
| center | `%str_center_str,<10i>,-%` | String, int, char | Center, fill the left and right ends of `str` with `-` until the length is `10`. |
| ljust | `%str_ljust_str,<10i>%` | String, int | Align left, padded spaces at the right end of `str` until the length is `10`. |
| ljust | `%str_ljust_str,<10i>,-%` | String, int, char | Align left, fill the right end of `str` with `-` until the length is `10`. |
| rjust | `%str_rjust_str,<10i>%` | String, int | Align to the right, padded spaces at the left end of `str` until the length is `10`. |
| rjust | `%str_rjust_str,<10i>,-`% | String, int, char | Align right, fill the left end of `str` with `-` until the length is `10`. |

> `center`, `ljust` and `rjust` can easily make neat and beautiful lists, such as chat area lists, scoreboard lists, and even Tab lists. 

> Parse these Placeholaders on console.
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
    # After opening, more information will be output from the console during analysis for debugging. 
    debug: false
    boolean:
      # Define the output of Boolean values, you can use color characters. 
      format:
        'true': '&aO'
        'false': '&7X'
      # After opening, all functions that output Boolean values will output <t> or <f> so that the results can be input to other functions as parameters. 
      # If closed, the format defined in format will be output directly. 
      # But in any case, the boolean function will output as defined in format. 
      output-parameter-format: true
    # Used to add suffixes to the blanks generated by `center`, `ljust` and `rjust` functions. 
    blank:
      head:
        suffix: ''
        prefix: ''
      tail:
        suffix: ''
        prefix: ''
```

### Blank

In some versions of the Minecraft client, the space width is inconsistent with the width of other letters, which leads to confusion in the layout of the table. Therefore, you can use characters such as `-`, `_`, `◼️️`, and set their colors to black, `&0`, and gray. `&7` or dark gray `&8` make them less visible and can take up the width of a letter.  
Other colors can refer to [Minecraft Wiki - Formatting codes]( https://minecraft.fandom.com/wiki/Formatting_codes ).  
Use `%str_center_abc,<10i>,-%`, `%str_ljust_abc,<10i>,-%` and `%str_rjust_abc,<10i>,-%` to show the operation principle of these three functions, and introduce the `head` And the position of the `tail`. 
First use the `/papi parse` command to view the execution result: 
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
        head
        |       tail
        |       |
      [---]abc[----]
      ^   ^   ^    ^
      |   |   |    tail suffix
      |   |   tail prefix
      |   |
      |   head suffix
      head prefix
```
**The brackets are used to identify the position, in fact, it does not exist in the result of this example, the same below.**
#### ljust
```
             tail
             |
      abc[-------]
         ^       ^
         |       tail suffix
         tail prefix
```
#### rjust
```
          head
          |
      [-------]abc
      ^       ^
      |       tail suffix
      tail prefix
```

#### Perfix & Suffix 

The original intention of adding the prefix and suffix is to change the color of the blank at the beginning and the end through the color code, so the prefix and the suffix are not included in the total string length.  
For example, still the above `%str_center_abc,<10i>,-%`, if we set this in the configuration file: 
```yaml
blank:
  head:
    suffix: '&0'
    prefix: '&f'
  tail:
    suffix: '&0'
    prefix: '&f'
```
Then its execution result is
```
&0---&fabc&0----&f
```
The same goes for `ljust` and `rjust`. 

## Actual combat: Improve the server's Tab list

* Server: [Mohist]( https://mohistmc.com/ ) 1.16.5-727
* Plugins：
  * [TabListPro]( https://www.spigotmc.org/resources/%E2%98%85-tablistpro-%E2%98%85-name-animations-sorting-header-footer-easy-setup-1-8-1-16.21532/ ) 1.2.2
  * [PlaceholderAPI]( https://www.spigotmc.org/resources/placeholderapi.6245/ ) 2.10.9
  * [spark]( https://www.spigotmc.org/resources/spark.57242/ ) 1.6.1
* Expansions：
  * str
  * math (`/papi ecloud download math`)
  * spark
  * player (`/papi ecloud download player`)
  * server (`/papi ecloud download server`)


> The version of Mohist used in the demonstration may have a problem, causing the TPS obtained with `%server_tps_1_colored%` to be inaccurate.  
> So use `%spark_tps_5s` provided by spark-Bukkit.

* Goals:
  * Show on the head of tablist：
    1. CPU useage (percentage)
    2. Memory (used, total and useage percentage)
    3. TPS
    4. Time
  * Show on the player-line of tablist：
    1. Name
    2. Ping
    3. Health
    4. Level
    5. World and position
      * World name
      * x
      * y
      * z

By querying [PlaceholderAPI Wiki - Placeholders]( https://github.com/PlaceholderAPI/PlaceholderAPI/wiki/Placeholders ), we can get the information we want through the following variables: 

| Placeholder | Returns |
| ---- | ---- |
| %spark_cpu_system_10s% | Average CPU usage in 10 seconds. |
| %server_ram_used% | Used memory. |
| %server_ram_max% | Maximum memory available. |
| %spark_tps_5s% | Average TPS in 5 seconds. |
| %server_time_yyyy/MM/dd hh:mm:ss% | Server time format, `Year/Month/Day Hour:Minute:Second`. |

| Placeholder | Returns |
| ---- | ---- |
| %player_name% | Player name. |
| %player_ping% | Player ping. |
| %player_health_rounded% | Player health rounded. |
| %player_max_health_rounded% | Player max health rounded. |
| %player_level% | Player level. |
| %player_world% | Player world. |
| %player_x% | Player position x. |
| %player_y% | Player position y. |
| %player_z% | Player position z. |

We also need to calculate the percentage of memory usage, which can be calculated by the following method:  
![latex]( https://latex.codecogs.com/svg.latex?\\frac{\\mathit{server\\_ram\\_used}}{\\mathit{server\\_ram\\_max}}\\times100\\% )  
The above expression is written in a form acceptable to `math`, `%math_0_100*{server_ram_used}/{server_ram_max}%`, This result has no unit, you need to add the unit: `%`, but `%` is occupied by PlaceholderAPI, so use the `char` function to return `%`, `%str_char_<37>%`. 

So the complete memory usage percentage is expressed as: `%math_0_100*{server_ram_used}/{server_ram_max}%%str_char_<37>%`. Assuming that the memory has used 1024M, totaling 2048M, the following figure explains its operating logic. 

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

In summary, write out the content that needs to be displayed at the head of the list  
`CPU %spark_cpu_system_10s% Mem %math_1_100*{server_ram_used}/{server_ram_max}%%ascii_37%(%server_ram_used%/%server_ram_max%MB) TPS %spark_tps_5s% Time %server_time_yyyy/MM/dd hh:mm:ss%`  

> A version with appropriate line breaks for the convenience of reading: 
> ```
> CPU %spark_cpu_system_10s%
> Mem %math_1_100*{server_ram_used}/{server_ram_max}%%ascii_37%(%server_ram_used%/%server_ram_max%MB)
> TPS %spark_tps_5s%
> Time %server_time_yyyy/MM/dd hh:mm:ss%
> ```
> Execute by `/papi parse`. 
> ![无样式列表头]( https://github.com/sileence114/String-Expansion/blob/master/document/head-parse-1.png )

Add a little formatting code and special symbols as decoration.  
`&6CPU&e%spark_cpu_system_10s% &f| &6Mem&e%math_1_100*{server_ram_used}/{server_ram_max}%%ascii_37%&7(&e%server_ram_used%&7/&e%server_ram_max%&6MB&7) &f| &6TPS%spark_tps_5s%&r\n&6Time: &e%server_time_yyyy/MM/dd hh:mm:ss% &6UTC+8&r`  

> A version with appropriate line breaks for the convenience of reading: 
> ```
> (line 1)
> &6CPU&e%spark_cpu_system_10s% &f| 
> &6Mem&e%math_1_100*{server_ram_used}/{server_ram_max}%%ascii_37%&7(&e%server_ram_used%&7/&e%server_ram_max%&6MB&7) &f| 
> &6TPS%spark_tps_5s%&r\n
> (line 2)
> &6Time: &e%server_time_yyyy/MM/dd hh:mm:ss% &6UTC+8&r
> ```
> Execute by `/papi parse`. 
> ![有样式列表头]( https://github.com/sileence114/String-Expansion/blob/master/document/head-parse-1.png )

### Player line

Write out the content to be displayed on each line of the player list  
`%player_name% &e%player_ping%&7ms &e%player_health_rounded%&7/&e%player_max_health_rounded%&c♥ &7lev.&e%player_level% &e%player_world%&7(&e%player_x%&7,&e%player_y%&7,&e%player_z%&7)&r`

> A version with appropriate line breaks for the convenience of reading: 
> ```
> %player_name%
> &e%player_ping%&7ms
> &e%player_health_rounded%&7/&e%player_max_health_rounded%&c♥
> &7lev.&e%player_level%
> &e%player_world%&7(&e%player_x%&7,&e%player_y%&7,&e%player_z%&7)&r
> ```

If you want the player list to be organized like a table, each column needs to specify the output length, with a black underscore (`_`), please refer to the [Perfix & Suffix](#perfix--suffix) mentioned Configuration file.  

| Column | Specify Length | Alignment | Placeholder after Alignment |
| ---- | ---- | ---- | ---- |
| %player_name% | Player ID, up to 16. | Align left. | %str_ljust_{player_name},<16i>,_% |
| %player_ping% | Player delay, the longest is 4, players with delay greater than 9999ms will leave the server soon. | Align right. | %str_rjust_{player_ping},<4i>,_% |
| %player_health_rounded% | The player’s current health value, the longest is 2, if the player’s maximum health value is >99, please modify it by yourself. | Align right. | %str_rjust_{player_health_rounded},<2i>,_% |
| %player_max_health_rounded% | The player’s maximum health, the longest is 2. If the player’s maximum health is >99, please modify it by yourself. | Align left. | %str_ljust_{player_max_health_rounded},<2i>,_% |
| %player_level% | Player level, the longest is 4, normal survival is difficult to obtain experience after level 300. According to [MineCraft Wiki]( https://minecraft.fandom.com/wiki/Experience#Useful_numbers ), the highest level of survival mode theory is `238,609,312` for reference. | Align right. | %str_rjust_{player_level},<4i>,_% |
| %player_world% | The maximum length of the world the player is in is set to the maximum length of the world name in the world that the player can reach. In this example, the longest world name is `twilightforest`, which is 14. | Align right. | %str_rjust_{player_world},<14i>,_% |
| %player_x% | Player position x, the server opens a radius of 5000, plus the minus sign, the longest is 5. | Align right. | %str_rjust_{player_x},<5i>,_% |
| %player_y% | Player position y, max 255, longest 3. | Align right. | %str_rjust_{player_y},<3i>,_% |
| %player_z% | Player position y, the server opens a radius of 5000, plus the minus sign, the longest is 5. | Align right. | %str_rjust_{player_z},<5i>,_% |

Add color codes and special symbols as decorations on top of this, because the formatting characters of the prefix will reset the color, so the color code should also be added to the front of the parameter, and the length needs to be increased by 2.  
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
> ![Console Parse]( https://github.com/sileence114/String-Expansion/blob/master/document/body-parse-console.png )

So far, the preparations have been completed. You only need to modify the configuration file and reload it with `/tablistpro reload`. The tabList screenshot: 
![TabList Screenshot]( https://github.com/sileence114/String-Expansion/blob/master/document/tab-list.png )

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
