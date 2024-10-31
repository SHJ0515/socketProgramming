package Sender;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

public class MailHeader {
    public String from; // 여기에 보내는 사람 이메일을 넣으세요
    public String to; // 여기에 받는사람 이메일을 넣으세요
    public String subject; // 메일 제목을 입력하세요
    public String date; // 메일 보내는 시간을 입력하세요
    public String messageId; // 안건드려도 됩니다
    private int fileFlag;

    // RFC 5322 형식에 맞는 헤더 선언
    String header;

    MailHeader (String from, String to, String subject, int fileFlag) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.fileFlag = fileFlag;

        // 현재 시간을 가져옵니다.
        LocalDateTime now = LocalDateTime.now();

        // RFC 5322 형식을 위한 DateTimeFormatter를 정의합니다.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

        // 현재 시간을 원하는 형식으로 포맷합니다.
        this.date = now.format(formatter);

        // 고유한 ID를 생성합니다.
        String uniqueID = UUID.randomUUID().toString();

        // 도메인을 설정합니다.
        String domain = "naver.com"; // 이 부분을 실제 도메인으로 바꿔야 합니다.

        // Message-ID 생성
        this.messageId = "<" + uniqueID + "@" + domain + ">";

        this.header = "From: " + this.from + "\r\n"
                + "To: " + this.to + "\r\n"
                + "Subject: " + this.subject + "\r\n"
                + "Date: " + this.date + "\r\n"
                + "Message-ID: " + this.messageId + "\r\n"
                + "MIME-Version: 1.0" + "\r\n"
                + "Content-Type: multipart/mixed; boundary=\"boundary\"" + "\r\n"
                + "\r\n";  // 헤더와 본문을 구분하는 빈 줄
    }
}