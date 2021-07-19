package pub.silence.papi.expansion;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.Configurable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class StrExpansion extends PlaceholderExpansion implements Configurable {
    private static final Logger LOGGER = PlaceholderAPIPlugin.getInstance().getLogger();
    
    private static final Pattern TRANSFORMED_PARAM = Pattern.compile("(?<!\\\\)<(.+)(?<!\\\\)>");
    private static final Pattern INTEGER_PARAM = Pattern.compile("\\d+");
    private static final Pattern FORCE_INTEGER_PARAM = Pattern.compile("\\d*\\.?\\d*(?=i)");
    private static final Pattern DOUBLE_PARAM = Pattern.compile("\\d*\\.\\d*");
    private static final Pattern FORCE_DOUBLE_PARAM = Pattern.compile("\\d*\\.?\\d*(?=d)");
    private static final Pattern ASCII_PARAM_DECIMAL = Pattern.compile("\\d+(?=a)");
    private static final Pattern ASCII_PARAM_HEXADECIMAL = Pattern.compile("\\d+(?=A)");
    
    private static String booleanToString(boolean b){
        return b ? booleanToStringTrue : booleanToStringFalse;
    }
    
    protected static Object handleParam(OfflinePlayer player, String item){
        item = PlaceholderAPI.setBracketPlaceholders(player, item);
        Matcher transformMatcher = TRANSFORMED_PARAM.matcher(item);
        if(transformMatcher.find()){
            String param = transformMatcher.group(1);
            String tmp;
            Matcher matcher = INTEGER_PARAM.matcher(param);
            if(matcher.matches()){
                return Integer.parseInt(matcher.group(0));
            }
            matcher = FORCE_INTEGER_PARAM.matcher(param);
            if(matcher.find()){
                tmp = matcher.group(0);
                return (".".equals(tmp) | tmp.length() == 0) ?
                       0 : ((Number)Double.parseDouble(tmp)).intValue();
            }
            matcher = DOUBLE_PARAM.matcher(param);
            if(matcher.find()){
                tmp = matcher.group(0);
                return ".".equals(tmp) ? 0d : Double.parseDouble(tmp);
                
            }
            matcher = FORCE_DOUBLE_PARAM.matcher(param);
            if(matcher.find()){
                tmp = matcher.group(0);
                return ".".equals(tmp) ? 0d : Double.parseDouble(tmp);
            }
            matcher = ASCII_PARAM_DECIMAL.matcher(param);
            if(matcher.find()){
                return (char)Integer.parseInt(matcher.group(0));
            }
            matcher = ASCII_PARAM_HEXADECIMAL.matcher(param);
            if(matcher.find()){
                return (char)Integer.valueOf(matcher.group(0), 16).intValue();
            }
            if("t".equals(param)){
                return true;
            }
            if("f".equals(param)){
                return false;
            }
        }
        return item.replaceAll("(?<!\\\\)\\$", "%")
                   .replaceAll("\\\\\\$", "\\$")
                   .replaceAll("\\\\<", "<")
                   .replaceAll("\\\\>", ">")
                   .replaceAll("\\\\,", ",")
                   .replaceAll("\\\\\\\\", "\\\\");
    }
    protected static Object handleParamDebugged(OfflinePlayer player, String item){
        StringBuilder debug = new StringBuilder("[String] Parameter: " + item + "    >    ");
        item = PlaceholderAPI.setBracketPlaceholders(player, item);
        debug.append(item).append("(BracketPlaceholders)    >    ");
        Matcher transformMatcher = TRANSFORMED_PARAM.matcher(item);
        if(transformMatcher.find()){
            String param = transformMatcher.group(1);
            String tmp;
            debug.append(param).append("    >    ");
            /*
             * <32> -> 32
             * */
            Matcher matcher = INTEGER_PARAM.matcher(param);
            if(matcher.matches()){
                int v = Integer.parseInt(matcher.group(0));
                LOGGER.info(debug.append(v).append("(Integer)").toString());
                return v;
            }
            /*
             * <32.56i> -> 32
             * <32i>    -> 32
             * */
            matcher = FORCE_INTEGER_PARAM.matcher(param);
            if(matcher.find()){
                tmp = matcher.group(0);
                int v = (".".equals(tmp) | tmp.length() == 0) ?
                        0 : ((Number)Double.parseDouble(tmp)).intValue();
                LOGGER.info(debug.append(v).append("(Force Integer)").toString());
                return v;
            }
            /*
             * <23.02> -> 23.02
             * <23.>   -> 23.0
             * <.02>   -> 0.02
             * <.>     -> 0.0 (".".equals(tmp))
             * */
            matcher = DOUBLE_PARAM.matcher(param);
            if(matcher.find()){
                tmp = matcher.group(0);
                double v = ".".equals(tmp) ? 0d : Double.parseDouble(tmp);
                LOGGER.info(debug.append(v).append("(Double)").toString());
                return v;
            }
            /*
             * <23.02f> -> 23.02
             * <23f>    -> 23.0
             * */
            matcher = FORCE_DOUBLE_PARAM.matcher(param);
            if(matcher.find()){
                tmp = matcher.group(0);
                double v = ".".equals(tmp) ? 0d : Double.parseDouble(tmp);
                LOGGER.info(debug.append(v).append("(Force Double)").toString());
                return v;
            }
            /*
             * <73a> -> I
             * <74a> -> J
             * */
            matcher = ASCII_PARAM_DECIMAL.matcher(param);
            if(matcher.find()){
                char v = (char)Integer.parseInt(matcher.group(0));
                LOGGER.info(debug.append(v).append("(Character)").toString());
                return v;
            }
            /*
             * <49A> -> I
             * <4aA> -> J
             * */
            matcher = ASCII_PARAM_HEXADECIMAL.matcher(param);
            if(matcher.find()){
                char v = (char)Integer.valueOf(matcher.group(0), 16).intValue();
                LOGGER.info(debug.append(v).append("(Character - HEX)").toString());
                return v;
            }
            
            if("t".equals(param)){
                LOGGER.info(debug.append("true").toString());
                return true;
            }
            if("f".equals(param)){
                LOGGER.info(debug.append("false").toString());
                return false;
            }
        }
        
        String result = item.replaceAll("(?<!\\\\)\\$", "%")
                            .replaceAll("\\\\\\$", "\\$")
                            .replaceAll("\\\\<", "<")
                            .replaceAll("\\\\>", ">")
                            .replaceAll("\\\\,", ",")
                            .replaceAll("\\\\\\\\", "\\\\");
        debug.append(result);
        LOGGER.info(debug.toString());
        return result;
    }
    private static BiFunction<OfflinePlayer, String, Object> handleParamFunc = StrExpansion::handleParam;
    private static String trueBoolean = "&a⚫";
    private static String falseBoolean = "&7⚪";
    private static String booleanToStringTrue = "<t>";
    private static String booleanToStringFalse = "<f>";
    @Override
    public boolean canRegister() {
        handleParamFunc = "true".equals(getString("debug", "false")) ?
                          StrExpansion::handleParamDebugged :
                          StrExpansion::handleParam;
        trueBoolean = getString("boolean.format.true", "&a⚫");
        falseBoolean = getString("boolean.format.false", "&7⚪");
        if(!"true".equals(getString("boolean.output-parameter-format", "true"))){
            booleanToStringTrue = trueBoolean;
            booleanToStringFalse = falseBoolean;
        }
        return true;
    }
    @Override
    public String getAuthor() {
        return "Silence";
    }
    @Override
    public String getIdentifier() {
        return "str";
    }
    @Override
    public String getName() {
        return "String";
    }
    @Override
    public String getVersion() {
        return "1.0.0";
    }

    
    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        /*
        * Usage: %str_<method>_param1,param2%
        * Parameter type: default as String.
        * <number> as Number (auto transform to int or double by dot)
        * <numberi> force as int    eg.<2i>,<2.1i> -> 2
        * <numberd> force as double eg.<2d>,<2.0d> -> 2.0
        * <numbera> as ascii char   eg.<37a> -> %; <37A> -> 7 (UpperCase => Hexadecimal)
        * <t> as boolean true
        * <f> as boolean false
        * $  => %
        * \$ => $
        * \< => <
        * \> => >
        * \, => ,
        * \\ => \
        * */
        String[] values = identifier.split("_", 2);
        List<Object> params = Arrays.stream(values[1].split("(?<!\\\\),(?![^{}]*+})")).map(
            param -> handleParamFunc.apply(player, param)
        ).collect(Collectors.toList());
        try{
            switch (values[0]){
                // Java String Methods.
                case "equals":
                case "eq":
                case "=":
                    /* %str_equal_str1,str2% */
                    return booleanToString(
                        params.get(0).toString().equals(params.get(1).toString())
                    );
                case "indexof":
                case "index":
                    /* %str_indexof_str,subStr% */
                    /* %str_indexof_str,subStr,<fromIndex>% */
                    return Integer.toString(
                        params.get(2) instanceof Integer ? params.get(0).toString().indexOf(
                            params.get(1).toString(), (Integer)params.get(2)
                        ) : params.get(0).toString().indexOf(params.get(1).toString())
                    );
                case "lastindexof":
                case "lastindex":
                    /* %str_lastindexof_str,subStr% */
                    /* %str_lastindexof_str,subStr,<fromIndexi>% */
                    return Integer.toString(
                        params.get(2) instanceof Integer ? params.get(0).toString().lastIndexOf(
                            params.get(1).toString(), (Integer)params.get(2)
                        ) : params.get(0).toString().lastIndexOf(params.get(1).toString())
                    );
                case "startswith":
                case "start":
                    /* %str_startswith_str,prefix% */
                    /* %str_startswith_str,prefix,<offSeti>% */
                    return booleanToString(
                        params.get(2) instanceof Integer ?
                        params.get(0).toString().startsWith(params.get(1).toString(), (Integer)params.get(2)) :
                        params.get(0).toString().startsWith(params.get(1).toString())
                    );
                case "endswith":
                case "ends":
                    /* %str_endswith_str,prefix% */
                    /* %str_endswith_str,prefix,<offSeti>% */
                    return booleanToString(params.get(0).toString().endsWith(params.get(1).toString()));
                // TODO: String.replace()
                case "substring":
                case "sub":
                    /* %str_substring_<beginIndexi>% */
                    /* %str_substring_<beginIndexi>,<endIndexi>% */
                    return params.get(2) instanceof Integer ?
                           params.get(0).toString().substring((Integer)params.get(1), (Integer)params.get(2)) :
                           params.get(0).toString().substring((Integer)params.get(1));
                case "format":
                case "fmt":
                    /* %str_format_format,args1,args2...% */
                    return String.format(
                        params.get(0).toString(),
                        params.subList(1, params.size()).toArray()
                    );
                case "length":
                case "len":
                    /* %str_length_str% */
                    return Integer.toString(params.get(0).toString().length());
                case "trim":
                    /* %str_trim_str% */
                    return params.get(0).toString().trim();
                case "uppercase":
                case "upper":
                    /* %str_uppercase_str% */
                    return params.get(0).toString().toUpperCase();
                case "lowercase":
                case "lower":
                    /* %str_uppercase_str% */
                    return params.get(0).toString().toLowerCase();
                // Costume Methods.
                case "boolean":
                case "bool":
                    return (boolean)params.get(0) ? trueBoolean : falseBoolean;
            }
        }
        catch (Exception e){
            LOGGER.warning("Your placeholder went wrong: %" + getIdentifier() + '_' + identifier + '%');
            LOGGER.warning("Method: [" + values[0] + "], Parameters: [" + values[1] + "]");
            handleParamDebugged(player, identifier);
            e.printStackTrace();
            return "[Error, Check Console! ]";
        }
        return null;
    }
    
    @Override
    public Map<String, Object> getDefaults() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("debug", false);
        defaults.put("boolean.output-parameter-format", true);
        defaults.put("boolean.format.true", "&a⚫");
        defaults.put("boolean.format.false", "&7⚪");
        return defaults;
    }
}
