package top.zhixingege.security.entity;

import top.zhixingege.common.utils.StringUtils;
import top.zhixingege.security.utils.JwtTokenUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.MultiValueMap;

public class MyUserDetails extends UsernamePasswordAuthenticationToken {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;


    public MyUserDetails(String username, String password) {
        super(username, password);
        this.username = username;
        this.password = password;

    }

    public static MyUserDetails unauthenticated(String username, String password) {
        return new MyUserDetails(username, password);
    }



    public static MyUserDetails createAuthentication(MultiValueMap<String, String> data) {
        String username = data.getFirst(JwtTokenUtils.USER_NAME);
        String password = data.getFirst(JwtTokenUtils.PASS_WORD);
        return MyUserDetails.unauthenticated(username, password);
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
