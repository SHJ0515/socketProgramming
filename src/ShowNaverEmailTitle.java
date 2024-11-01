import util.Decoder;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.nio.charset.Charset;


public class ShowNaverEmailTitle {

    private static final String DIVIDER = "------------------------------";

    private static final String HOST = "imap.naver.com";  // 네이버 IMAP 서버
    private static final int PORT = 993;  // SSL 포트
    private String id;                    // 네이버 아이디
    private String password;              // 네이버 비밀번호
    private String flag;
    private String boxType;
    private String[] emailIds;            // 메일 ID 리스트를 저장할 필드
    private SSLSocket socket;             // 소켓 필드로 선언
    private BufferedReader reader;        // BufferedReader 필드로 선언
    private BufferedWriter writer;        // BufferedWriter 필드로 선언

    public ShowNaverEmailTitle() {}
    public ShowNaverEmailTitle(
            String id, String password, String flag, String boxType, String[] emailIds,
            SSLSocket socket, BufferedReader reader, BufferedWriter writer) {
        this.id = id;
        this.password = password;
        this.flag = flag;
        this.boxType = boxType;
        this.emailIds = emailIds;
        this.socket = socket;
        this.reader = reader;
        this.writer = writer;
    }

    public ShowNaverEmailTitle(ShowNaverEmailList showNaverEmailList){
        this.id = showNaverEmailList.getId();
        this.password = showNaverEmailList.getPassword();
        this.flag = showNaverEmailList.getFlag();
        this.boxType = showNaverEmailList.getBoxType();
        this.emailIds = showNaverEmailList.getEmailIds();
        this.socket = showNaverEmailList.getSocket();
        this.reader = showNaverEmailList.getReader();
        this.writer = showNaverEmailList.getWriter();
    }

    //화면에 메일 id 와 메일 제목, 발신자 프린트
    public void displayAllEmailTitles() {
        for(String emailId : emailIds) {
            System.out.println("메일 id : " + emailId);
            String details = fetchEmailDetails(emailId);
            printDetails(details);
        }

    }

    //화면에 출력
    public void printDetails(String details) {
        try {
            if(details != null) {
                String decodedDetails = Decoder.decodeMimeEncodedText(details);
                System.out.println("메일 세부 정보 : " + decodedDetails);
            }
            System.out.println(DIVIDER);
        } catch (IllegalArgumentException e){
            e.printStackTrace();
            throw e;
        }
    }

    public String fetchEmailDetails(String emailId) {
        try {
            sendFetchCommand(emailId);  // FETCH 명령어로 제목과 발신자 요청
            return readServerResponse();    //서버로부터 응답을 읽고 반환

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //서버에 FETCH 명령어 전송
    private void sendFetchCommand(String emailId) {
        try {
            writer.write("A004 FETCH " + emailId + " (BODY[HEADER.FIELDS (SUBJECT FROM)])\r\n");
            writer.flush();
            System.out.println("C: A004 FETCH " + emailId + " (BODY[HEADER.FIELDS (SUBJECT FROM)])");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //서버로부터 응답을 읽고 반환
    private String readServerResponse() {
        StringBuilder emailDetails = new StringBuilder();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("S: " + line);
                emailDetails.append(line).append("\n");

                // 응답 완료 조건 확인
                if (line.startsWith("A004 OK")) break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return emailDetails.toString();
    }
}
