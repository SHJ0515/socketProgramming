package util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Decoder {
    /*
        이메일 헤더 정보(FROM, DATE, SUBJECT)를 디코딩하는 역할
     */
    public static String decodeHeaderInfo(String encodedText) {
        StringBuilder decodedText = new StringBuilder();
        try {
            // MIME 인코딩 형식을 감지하기 위한 패턴 (예: =?UTF-8?B?...?= 또는 =?UTF-8?Q?...?=)
            Pattern pattern = Pattern.compile("=\\?([^?]+)\\?(B|Q)\\?([^?]+)\\?=");
            Matcher matcher = pattern.matcher(encodedText);

            int lastEnd = 0;
            while (matcher.find()) {
                // 일반 텍스트 추가
                decodedText.append(encodedText, lastEnd, matcher.start());

                String charset = matcher.group(1); // UTF-8
                String encoding = matcher.group(2); // B 또는 Q
                String encodedPart = matcher.group(3); // 인코딩된 텍스트 부분

                if ("B".equalsIgnoreCase(encoding)) {
                    // Base64 인코딩 디코딩
                    byte[] decodedBytes = Base64.getDecoder().decode(encodedPart);
                    decodedText.append(new String(decodedBytes, Charset.forName(charset)));
                } else if ("Q".equalsIgnoreCase(encoding)) {
                    // Quoted-Printable 인코딩 디코딩
                    decodedText.append(decodeQuotedPrintable(encodedPart, charset));
                }

                lastEnd = matcher.end();
            }

            // 마지막으로 남은 일반 텍스트 추가
            decodedText.append(encodedText.substring(lastEnd));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decodedText.toString();
    }

    public static String decodeQuotedPrintable(String encodedText, String charset) {
        StringBuilder decodedText = new StringBuilder();

        for (int i = 0; i < encodedText.length(); i++) {
            char ch = encodedText.charAt(i);

            if (ch == '=') {
                // '=' 문자 발견 시 다음 두 문자를 16진수로 해석하여 디코딩
                if (i + 2 < encodedText.length()) {
                    String hexValue = encodedText.substring(i + 1, i + 3);
                    try {
                        int decodedValue = Integer.parseInt(hexValue, 16);
                        decodedText.append((char) decodedValue);
                    } catch (NumberFormatException e) {
                        // 형식이 잘못된 경우 '='와 다음 두 문자 그대로 추가
                        decodedText.append('=').append(hexValue);
                    }
                    i += 2; // 16진수 문자 두 개 건너뜀
                }
            } else if (ch == '_') {
                // '_'는 공백(' ')으로 대체
                decodedText.append(' ');
            } else {
                // 일반 문자는 그대로 추가
                decodedText.append(ch);
            }
        }

        // 디코딩된 텍스트를 지정된 charset으로 변환
        return new String(decodedText.toString().getBytes(StandardCharsets.ISO_8859_1), Charset.forName(charset));
    }
}
