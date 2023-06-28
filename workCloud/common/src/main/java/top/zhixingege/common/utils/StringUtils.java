package top.zhixingege.common.utils;



public class StringUtils {

    public static final String BLANK = " ";
    public static final String EMPTY = "";
    public static final String DEFAULT_LOGOUT_SUCCESS_URL = "/web/webLogin/isLogout";
    public static final String DEFAULT_LOGIN_URL = "/web/webLogin/emailLogin";
    public static final String DEFAULT_LOGOUT_URL_1 = "/web/webLogin/logout";


    public static final String WILDCARD = "**";
    //访问名单，不用登陆
    public static final String[] REQUEST_RUL_WHITE_S = {
            "/web/webLogin/register",
            "/web/company/**"
    };

}