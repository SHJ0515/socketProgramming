import util.Decoder;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IMAPEmailInfoFetcher {
    String email, password, emailID, server, boxType;
    int port;

    public IMAPEmailInfoFetcher(
            String email, String password, String emailID,
            String server, int port, String boxType
    ) {
        this.email = email;
        this.password = password;
        this.emailID = emailID;
        this.server = server;
        this.port = port;
        this.boxType = boxType;
    }

    /*
        특정 메일의 발신자 이메일, 수신 날짜, 메일 제목, 메일 내용 가져오기
     */
    public EmailInfo fetchEmailInfo() {
        EmailInfo emailInfo = new EmailInfo();
        try {
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            try (SSLSocket socket = (SSLSocket) factory.createSocket(server, port);
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "euc-kr"))) {

                /*
                    서버 초기 응답
                 */
                System.out.println("S: " + reader.readLine());

                /*
                    로그인 명령어 전송 및 응답 확인
                  */
                writer.println("a1 LOGIN " + email + " " + password);
                System.out.println("C: a1 LOGIN " + email + " " + password);

                System.out.println("S: " + reader.readLine());

                /*
                    메일함 선택
                    EX) INBOX는 수신메일함, SENT는 발신메일함
                 */
                String line;
                writer.println("a2 SELECT " + boxType);
                System.out.println("C: a2 SELECT " + boxType);
                while ((line = reader.readLine()) != null) {
                    System.out.println("S: " + line);
                    if (line.contains("a2 OK")) {
                        break;
                    }
                }

                /*
                    메일의 헤더 정보를 가져와 인코딩 형식 확인
                 */
                writer.println("a4 FETCH " + emailID + " BODY[HEADER]");
                System.out.println("C: a4 FETCH " + emailID + " BODY[HEADER]");
                StringBuilder headerContent = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    System.out.println("S: " + line);
                    headerContent.append(line).append("\n");
                    if (line.contains("a4 OK")) {
                        break;
                    }
                }

                // 헤더에서 Content-Type 및 Content-Transfer-Encoding 추출
                String contentType = extractHeaderInfo(headerContent.toString(), "Content-Type");
                String encoding = extractHeaderInfo(headerContent.toString(), "Content-Transfer-Encoding");
                System.out.println("==============================");
                System.out.println("Content-Type: " + contentType);
                System.out.println("Content-Transfer-Encoding: " + encoding);
                System.out.println("==============================\n");


                // 이메일 헤더 가져오기
                writer.println("a3 FETCH " + emailID + " (BODY[HEADER.FIELDS (FROM DATE SUBJECT)])");
                System.out.println("C: a3 FETCH " + emailID + " (BODY[HEADER.FIELDS (FROM DATE SUBJECT)])");
                StringBuilder headerResponse = new StringBuilder();
                while((line = reader.readLine()) != null) {
                    headerResponse.append(line).append("\n");
                    if(line.contains("a3 OK")) {
                        break;
                    }
                }
                System.out.println("** 이메일 헤더(디코딩 전) ===========================================");
                System.out.print(headerResponse);
                System.out.println("** ==============================================================");

                String decodedHeader = Decoder.decodeMimeEncodedText(headerResponse.toString());
                System.out.println("메일 헤더 정보(디코딩 후):\n" + decodedHeader);
                System.out.println("==============================\n");

                /*
                    이메일 헤더에서 '발신자 이메일', '수신 및 발신 날짜', '메일 제목 정보' 가져오기
                 */
                writer.println("a4 FETCH " + emailID + " (BODY[HEADER.FIELDS (FROM DATE SUBJECT)])");
                System.out.println("C: a4 FETCH " + emailID + " (BODY[HEADER.FIELDS (FROM DATE SUBJECT)])");

                String from = "", date = "", subject = "";
                while((line = reader.readLine()) != null) {
                    if (line.startsWith("FROM:")) {
                        from = line.substring(5).trim();
                    } else if (line.startsWith("DATE:")) {
                        date = line.substring(5).trim();
                    } else if (line.startsWith("SUBJECT:")) {
                        subject = line.substring(8).trim();
                    }
                    if (line.contains("a4 OK")) break;
                }
                System.out.println("** 메일 헤더 정보(디코딩 전)============================================");
                System.out.println("From: " + from);
                System.out.println("Date: " + date);
                System.out.println("Subject: " + subject);
                System.out.println("========================================================");

                System.out.println("** 메일 헤더 정보(디코딩 후)============================================");
                System.out.println("From: " + Decoder.decodeMimeEncodedText(from));
                System.out.println("Date: " + Decoder.decodeMimeEncodedText(date));
                System.out.println("Subject: " + Decoder.decodeMimeEncodedText(subject));
                System.out.println("========================================================");

                emailInfo.setFrom(Decoder.decodeMimeEncodedText(from));
                emailInfo.setDate(Decoder.decodeMimeEncodedText(date));
                emailInfo.setSubject(Decoder.decodeMimeEncodedText(subject));

                /*
                    이메일 본문 가져오기
                 */
                writer.println("a4 FETCH " + emailID + " BODY[TEXT]");
                System.out.println("C: a4 FETCH " + emailID + " BODY[TEXT]");
                StringBuilder bodyResponse = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    // `* 1000 FETCH (BODY[TEXT] {...}`로 시작하는 줄은 건너뜀
                    if (line.startsWith("* " + emailID + " FETCH (BODY[TEXT]")) {
                        continue;
                    }
                    if (line.contains(")")) {
                        break;
                    }
                    bodyResponse.append(line).append("\n");
                }

                String decodedBody = bodyResponse.toString();
                if(encoding.equalsIgnoreCase("base64")) {
                    decodedBody = decodeBase64(bodyResponse.toString(), Charset.forName(getCharset(contentType)));
                }
                emailInfo.setBody(decodedBody);

                System.out.println("========================================================");
                System.out.println(emailInfo);
                System.out.println("========================================================");

                /*
                    르그아웃
                 */
                writer.println("a6 LOGOUT\r\n");
                System.out.println("C: a6 LOGOUT");

                System.out.println("S: " + reader.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return emailInfo;
    }

    private String extractHeaderInfo(String header, String fieldName) {
        String regex = "(?m)^" + fieldName + "\\s*:\\s*(.*?)(?=(\\r?\\n[^\\s]|\\z))";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(header);

        if (matcher.find()) {
            return matcher.group(1).replaceAll("\\s+", " ").trim(); // 여백은 한 칸으로 통일
        }
        return ""; // 필드를 찾지 못한 경우 빈 문자열 반환
    }

    private String decodeBase64(String encoded, Charset charset) {
        byte[] decodedBytes = Base64.getMimeDecoder().decode(encoded);
        return new String(decodedBytes, charset);
    }

    private String getCharset(String contentType) {
        Pattern charsetPattern = Pattern.compile("charset=([\\w-]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = charsetPattern.matcher(contentType);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "UTF-8"; // 기본값
    }

    public static void main(String[] args) {
        // 네이버 IMAP 서버 테스트
        String naverEmail = "audtn0099@naver.com";
        String naverPassword = "msjw0706";
        String emailID = "1";
        String server = "imap.naver.com";
        int port = 993;
        String boxType = "INBOX";

        for (int i = 1000; i >= 990; i--) {
            IMAPEmailInfoFetcher naverEmailInfoFetcher = new IMAPEmailInfoFetcher(
                    naverEmail, naverPassword, Integer.toString(i),
                    server, port, boxType
            );
            EmailInfo emailInfo = naverEmailInfoFetcher.fetchEmailInfo(); // 이메일 정보 가져오기
            System.out.println(emailInfo); // 이메일 정보 출력
        }
    }
}
