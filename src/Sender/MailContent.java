package Sender;

import java.nio.file.Files;
import java.util.Base64;
import java.io.*;

public class MailContent {
    String result;

    MailContent(String contentText, Boolean multiBoolean, String filePath) {
        if(multiBoolean) {
            result = new multiPartTextMailContent(contentText).body;
        } else {
            try {
                result = new multiPartfileMailContent(contentText, filePath).body;
            } catch (Exception e) {
                System.out.println("파일을 포함한 Mail Content가 만들어지지 않았습니다.");
                System.out.println(e.toString());
            };
        }
    }
}

class multiPartTextMailContent { // 메일이 문자열만 있는 경우의 body 생성
    String body;

    multiPartTextMailContent(String contentText) {
        this.body = "--boundary" + "\r\n"
                + "Content-Type: text/plain; charset=UTF-8" + "\r\n"
                + "\r\n"
                + contentText + "\r\n";
    }
}

class multiPartfileMailContent { // 메일이 텍스트와 첨부파일 1개와 같이 있는 경우 body 생성
    String body;

    multiPartfileMailContent(String contentText, String filePath) throws Exception{
        multiPartTextMailContent plainText = new multiPartTextMailContent(contentText);

        // 파일 내용을 Base64로 인코딩하여 전송
        byte[] fileContent = Files.readAllBytes(new File(filePath).toPath());
        String encodedFile = Base64.getEncoder().encodeToString(fileContent);

        this.body = plainText.body
                + "--boundary" + "\r\n"
                + "Content-Type: application/octet-stream; name=\"" + new File(filePath).getName() + "\"" + "\r\n"
                + "Content-Transfer-Encoding: base64" + "\r\n"
                + "\r\n"
                + encodedFile + "\r\n"
                + "\r\n"
                + "--boundary--" + "\r\n";
    }
}
