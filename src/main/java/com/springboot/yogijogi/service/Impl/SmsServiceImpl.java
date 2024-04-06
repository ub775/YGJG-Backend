package com.springboot.yogijogi.service.Impl;

import com.springboot.yogijogi.dto.SignUpIn.SmsCertificationDto;
import com.springboot.yogijogi.redis.SmsCertification;
import com.springboot.yogijogi.service.SmsService;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Random;

@Service
public class SmsServiceImpl implements SmsService {
    private Logger logger = LoggerFactory.getLogger(SmsService.class);

    private final SmsCertification smsCertification;

    public SmsServiceImpl(SmsCertification smsCertification){
        this.smsCertification = smsCertification;
    }

    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String secretKey;

    @Value("${coolsms.fromnumber}")
    private String fromNumber;

    @Override
    public String createRandomNumber() {
        Random random = new Random();
        String randomNum = "";
        for(int i =0;i<4;i++){
            String rand = Integer.toString(random.nextInt(10));
            randomNum += rand;
        }
        return randomNum;
    }

    @Override
    public HashMap<String, String> makeprams(String to, String randomNum) {
        HashMap<String, String> params = new HashMap<String ,String>();
        params.put("to",to);
        params.put("from",fromNumber);
        params.put("type","SMS");
        params.put("text", "[요기조기] 인증번호 "+randomNum+" 를 입력하세요.");
        params.put("app_version","yogijogiTest1.1");
        System.out.println(params);
        return params;
    }

    //인증번호 전송하기
    @Override
    public String sendSMS(String phone_num) {
        Message message = new Message(apiKey,secretKey);

        //랜덤한 인증 번호 생성
        String randomNum = createRandomNumber();
        System.out.println(randomNum);



        //발신 정보 설정
        HashMap<String,String> params = makeprams(phone_num,randomNum);

        try{
            JSONObject obj = (JSONObject) message.send(params);
            System.out.println(obj.toString());
        }catch (CoolsmsException e){
            System.out.println(e.getMessage());
            System.out.println(e.getCode());
        }
        // 데이터베이스에 발송한 인증번호 저장
        smsCertification.createSmsCertification(phone_num, String.valueOf(randomNum));
        logger.info("[sms] phone_num : {} , randomNum : {} ",phone_num,randomNum);
        return randomNum;
    }

    // 인증 번호 검증
// 인증 번호 검증
    public boolean verifySms(SmsCertificationDto smsCertificationDto) {
        if (smsCertification.hasKey(smsCertificationDto.getPhone_num()) &&
                smsCertification.getSmsCertification(smsCertificationDto.getPhone_num()).equals(smsCertificationDto.getCertification_num())) {
            // 인증 성공 시 인증 정보 삭제
            smsCertification.deleteSmsCertification(smsCertificationDto.getPhone_num());
            return true; // 인증 성공
        } else {
            return false; // 인증 실패
        }
    }


}
