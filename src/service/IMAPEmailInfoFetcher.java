package service;

import dto.EmailInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static util.Decoder.decodeHeaderInfo;

public class IMAPEmailInfoFetcher {
    private ConnectionManager connectionManager;

    public IMAPEmailInfoFetcher(
            String boxType
    ) {
        this.connectionManager = ConnectionManager.getInstance();
    }

    /*
        메일의 발신자 이메일, 수신 날짜, 메일 제목, 메일 내용 가져오기
     */
    public List<EmailInfo> fetchEmailInfo(String boxType, int start, int count) {
        List<EmailInfo> emailInfoList = new ArrayList<>();


        try {
            connectionManager.connect();

            /*
                메일함 선택
                EX) INBOX는 받은 메일함, "Sent Messages"는 보낸 메일함
             */
            connectionManager.sendCommand(
                    "a" + connectionManager.getTagCounter() + " SELECT " + boxType);
            connectionManager.readResponse();
            connectionManager.setTagCounter(connectionManager.getTagCounter() + 1);

            // 메일함의 모든 이메일 ID 가져오기
            connectionManager.sendCommand(
                    "a" + connectionManager.getTagCounter() + " FETCH 1:* (UID)");
            List<String> emailIDs = new ArrayList<>();
            String line;
            while((line = connectionManager.readResponse()) != null) {
                if(line.contains(connectionManager.getTagCounter() + " OK")) {
                    break;
                }
                if (line.startsWith("*") && line.contains("FETCH")) {
                    String[] parts = line.split(" ");
                    emailIDs.add(parts[1]); // 이메일 ID 추가
                }
            }
            connectionManager.setTagCounter(connectionManager.getTagCounter() + 1);

            /*
                이메일 ID 목록으 최신 순으로 정렬하고, 요청한 범위에 따라 가져옴
                start는 페이지 번호, end는 가져올 마지막 메일 ID
                즉, emailIDs에 저장된 start * 10부터 end까지 가져옴
             */
            Collections.reverse(emailIDs);
            int end = Math.min(start * 10 + count, emailIDs.size());
            List<String> selectedEmailIDs = emailIDs.subList(start * 10, end);

            // 메일함에 따라 불러올 헤더 정보 필드 설정
            String headerFields = boxType.equalsIgnoreCase("INBOX") ? "FROM DATE SUBJECT" : "TO DATE SUBJECT";
            for (String emailID : selectedEmailIDs) {
                EmailInfo emailInfo = new EmailInfo();
                connectionManager.sendCommand(
                        "a" + connectionManager.getTagCounter() + " FETCH " + emailID + " (BODY[HEADER.FIELDS (" + headerFields + ")])");

                String[] encodedFromOrToParts = null;
                String decodedFromOrToPart = "";
                StringBuilder encodedFromOrToBuilder = new StringBuilder();
                StringBuilder decodedFromOrToBuilder = new StringBuilder();

                String date = "";

                String[] encodedSubjectParts = null;
                String decodedSubjectPart = "";
                StringBuilder encodedSubjectBuilder = new StringBuilder();
                StringBuilder decodedSubjectBuilder = new StringBuilder();

                while((line = connectionManager.readResponse()) != null) {
                    if(line.contains("a" + connectionManager.getTagCounter() + " OK")) {
                        break;
                    }

                    if ((boxType.equalsIgnoreCase("INBOX") && line.startsWith("FROM:")) ||
                            boxType.equalsIgnoreCase("\"Sent Messages\"") && line.startsWith("TO:")) {
                        encodedFromOrToParts = line.substring(5).trim().split(" ");

                        for(String encodedFromPart : encodedFromOrToParts) {
                            encodedFromOrToBuilder.append(encodedFromPart).append(" ");
                        }
                    } else if (line.startsWith("DATE:")) {
                        date = line.substring(5).trim();
                    } else if (line.startsWith("SUBJECT:")) {
                        encodedSubjectParts = line.substring(8).trim().split(" ");
                        for(String encodedSubjectpart : encodedSubjectParts) {
                            encodedSubjectBuilder.append(encodedSubjectpart).append(" ");
                        }
                    }
                }
                System.out.println("\n** 메일 헤더 정보(디코딩 전)================================");
                System.out.println("From or To: " + encodedFromOrToBuilder);
                System.out.println("Date: " + date);
                System.out.println("Subject: " + encodedSubjectBuilder);
                System.out.println("========================================================\n");

                if(encodedSubjectParts != null) {
                    for (String encodedSubjectpart : encodedSubjectParts) {
                        decodedSubjectPart = decodeHeaderInfo(encodedSubjectpart);
                        decodedSubjectBuilder.append(decodedSubjectPart);
                    }
                }
                if(encodedFromOrToParts != null) {
                    for(String encodedFromPart : encodedFromOrToParts) {
                        decodedFromOrToPart = decodeHeaderInfo(encodedFromPart);
                        decodedFromOrToBuilder.append(decodedFromOrToPart);
                    }
                }
                emailInfo.setFromOrTo(decodedFromOrToBuilder.toString());
                emailInfo.setDate(decodeHeaderInfo(date));
                emailInfo.setSubject(decodedSubjectBuilder.toString());

                System.out.println("\n** 메일 헤더 정보(디코딩 후)================================");
                System.out.println("From or To: " + emailInfo.getFromOrTo());
                System.out.println("Date: " + emailInfo.getDate());
                System.out.println("Subject: " + emailInfo.getSubject());
                System.out.println("========================================================\n");

                emailInfoList.add(emailInfo);

                connectionManager.setTagCounter(connectionManager.getTagCounter() + 1);
            }

            return emailInfoList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
