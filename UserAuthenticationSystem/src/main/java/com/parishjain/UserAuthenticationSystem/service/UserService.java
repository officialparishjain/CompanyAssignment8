package com.parishjain.UserAuthenticationSystem.service;

import com.parishjain.UserAuthenticationSystem.model.User;
import com.parishjain.UserAuthenticationSystem.repo.UserRepo;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class UserService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    private JavaMailSender mailSender;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 7;

    public static String generateTemporaryPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(randomIndex));
        }

        return password.toString();
    }

    // FIND USER BY EMAIL
    public User findByEmail(String email) {
       return userRepo.findByEmail(email);
    }

    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // SAVING THE USER
    public User createUser(User user) {
        String encryptPassword = bCryptPasswordEncoder().encode(user.getPassword());
        user.setPassword(encryptPassword);

        // Check if the email contains "@company.com" to determine the role
        if (user.getEmail().endsWith("@company.com")) {
            user.setRole("ROLE_ADMIN");
        } else {
            user.setRole("ROLE_USER");
        }
        return userRepo.save(user);
    }

    public String requestForgotPassLink(String email) {
        User user = findByEmail(email);

        if (user != null) {
            String tempPassword = generateTemporaryPassword();
            String encryptedTempPassword = bCryptPasswordEncoder().encode(tempPassword);

            // Update user's password with the temporary password
            user.setPassword(encryptedTempPassword);
            userRepo.save(user);

            // Send an email with the temporary password
            sendMail(user, tempPassword);

            return "Temporary Password has been sent to you on email";
        } else {
            return "User not found";
        }
    }


    public void sendMail(User user,String tempPass){

        String from = "officialparishjain@gmail.com";
        String to = user.getEmail();
        String subject = "Temporary Password ! Kindly Reset Using this ...";

        try{
            // Create a new MimeMessage object
            MimeMessage message = mailSender.createMimeMessage();
            // Wrap the MimeMessage object in a MimeMessageHelper object
            // It provides various methods to create the mime message
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from,"Parish Jain");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(tempPass,true);
            mailSender.send(message);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public String updatePassword(String email, String temp, String newPassword) {
        User user = findByEmail(email);

        if (user != null) {
            BCryptPasswordEncoder encoder = bCryptPasswordEncoder();

            // Check if the provided temporary password matches the stored hashed password
            if (encoder.matches(temp, user.getPassword())) {
                String encryptedPassword = encoder.encode(newPassword);
                user.setPassword(encryptedPassword);
                userRepo.save(user);
                return "Password updated";
            } else {
                return "Temporary Password Not Valid";
            }
        } else {
            return "User not found";
        }
    }


}
