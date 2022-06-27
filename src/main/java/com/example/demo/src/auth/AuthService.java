package com.example.demo.src.auth;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.auth.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Service Create, Update, Delete 의 로직 처리
@Service
public class AuthService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AuthDao authDao;
    private final AuthProvider authProvider;
    private final JwtService jwtService;


    @Autowired
    public AuthService(AuthDao authDao, AuthProvider authProvider, JwtService jwtService) {
        this.authDao = authDao;
        this.authProvider = authProvider;
        this.jwtService = jwtService;

    }
            //리턴값 자리
    public PostLoginRes login(PostLoginReq postLoginReq) throws BaseException{
        User user = authDao.getPwd(postLoginReq);
        String encryptPwd;

        try{
            encryptPwd = new SHA256().encrypt(postLoginReq.getPwd()); //utils패키지에 SHA256에서 암호화 진행

        }catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.PASSWORD_ENCRYPTION_ERROR);
        }
        if(user.getPwd().equals(encryptPwd)){ //비번 맞는지 확인 맞다면 jwt 발급
            int userIdx = user.getUserIdx();
            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginRes(userIdx, jwt);
        }else
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
    }

}
