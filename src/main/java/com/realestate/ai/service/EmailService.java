package com.realestate.ai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.resend.Resend;
import com.resend.services.emails.Emails;
import com.resend.services.emails.model.CreateEmailOptions;

@Service
public class EmailService {

    private final Resend resend;

    public EmailService(
            @Value("${resend.api.key}") String apiKey
    ){
        this.resend = new Resend(apiKey);
    }

    // ================= OTP =================
    public void sendOtp(String to,String otp){

        try{

            Emails emails = resend.emails();

            CreateEmailOptions params =
                    CreateEmailOptions.builder()
                            .from("Manortha CRM <onboarding@resend.dev>")
                            .to(to)
                            .subject("Manortha OTP Verification")
                            .html(
                                    "<h3>Your OTP is: "+otp+"</h3>"+
                                    "<p>Valid for 5 minutes</p>"
                            )
                            .build();

            emails.send(params);

            System.out.println("✅ OTP EMAIL SENT");

        }catch(Exception e){

            System.out.println("❌ OTP EMAIL FAILED");
            e.printStackTrace();
        }
    }

    // ================= PARTNER ACTIVATION =================
    public void sendActivationLink(
            String to,
            String link
    ){

        try{

            Emails emails = resend.emails();

            CreateEmailOptions params =
                    CreateEmailOptions.builder()
                            .from("Manortha CRM <onboarding@resend.dev>")
                            .to(to)
                            .subject("Activate Partner Account")
                            .html(
                                    "<h2>Welcome Partner</h2>"+
                                    "<p>Click below to set password</p>"+
                                    "<a href='"+link+"'>Activate Account</a>"
                            )
                            .build();

            emails.send(params);

            System.out.println("✅ ACTIVATION EMAIL SENT");

        }catch(Exception e){

            System.out.println("❌ ACTIVATION EMAIL FAILED");
            e.printStackTrace();
        }
    }
}