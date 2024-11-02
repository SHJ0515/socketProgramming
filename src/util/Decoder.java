package util;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Decoder {

    public static String decodeMimeEncodedText(String text) {
        StringBuilder decodedText = new StringBuilder();

        /*
            Base64 또는 Quoted-Printable로 인코딩된 텍스트를 찾기 위해 패턴 사용
            =?charset?encodingType?encodedText?= 형식
         */
        Pattern pattern = Pattern.compile("=\\?(.*?)\\?([BQ])\\?(.+?)\\?=", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        int lastEnd = 0;

        while (matcher.find()) {
            decodedText.append(text, lastEnd, matcher.start());

            /*
                매칭된 그룹에서 charset, 인코딩 타입, 인코딩된 텍스트 추출
             */
            String charsetName = matcher.group(1);
            String encodingType = matcher.group(2);
            String encodedText = matcher.group(3);

            Charset charset = Charset.forName(charsetName);
            byte[] decodedBytes;

            try {
                // 인코딩 타입이 'B"인 경우 Base64로 디코딩
                if (encodingType.equalsIgnoreCase("B")) {
                    decodedBytes = Base64.getDecoder().decode(encodedText);
                } else {
                    // "Q"인 경우 Quoted-Printable로 디코딩
                    // decodeQuotedPrintable 메소드에서 charset을 사용하여 디코딩
                    decodedBytes = decodeQuotedPrintable(encodedText).getBytes(charset);
                }
                // 디코딩된 바이트 배열을 charset으로 변환하여 문자열을 추가
                decodedText.append(new String(decodedBytes, charset));
            } catch (Exception e) {
                // 디코딩 실패 시 원본 텍스트 그대로 추가
                decodedText.append(matcher.group(0));
            }
            // 마지막 매칭 위치를 현재 매칭 끝 위치로 업데이트
            lastEnd = matcher.end();
        }
        // 마지막 남은 텍스트 추가
        decodedText.append(text.substring(lastEnd));
        return decodedText.toString();
    }

    /*
        Quoted-Printable 디코딩
     */
    public static String decodeQuotedPrintable(String text) {
        // 줄바꿈과 이어진 `=` 기호 제거
        text = text.replaceAll("=\r?\n", "");

        StringBuilder decoded = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '=' && i + 2 < text.length()) {
                String hex = text.substring(i + 1, i + 3);
                try {
                    int value = Integer.parseInt(hex, 16);
                    decoded.append((char) value);
                    i += 2;
                } catch (NumberFormatException e) {
                    decoded.append(c); // 실패 시 '=' 그대로 추가
                }
            } else {
                decoded.append(c);
            }
        }
        return decoded.toString();
    }

}
