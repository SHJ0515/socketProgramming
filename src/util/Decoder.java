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
    public static String decodeHeaderInfo(String encodedHeader) {
        StringBuilder decodedString = new StringBuilder();

        // RFC 2047에 따른 인코딩 형식 : =?charset?encoding?encoded text?=
        Pattern rfc2047Pattern = Pattern.compile("=\\?([^?]+)\\?(B|Q)\\?([^?]+)\\?=", Pattern.CASE_INSENSITIVE);
        Matcher matcher = rfc2047Pattern.matcher(encodedHeader);

        while (matcher.find()) {
            String charset = matcher.group(1); // 문자셋, 예: "UTF-8"
            String encoding = matcher.group(2); // 인코딩 방식, "B" or "Q"
            String encodedText = matcher.group(3); // 인코딩된 텍스트

            try {
                if ("B".equalsIgnoreCase(encoding)) { // Base64 인코딩 방식
                    byte[] decodedBytes = Base64.getDecoder().decode(encodedText);
                    decodedString.append(new String(decodedBytes, charset));
                } else if ("Q".equalsIgnoreCase(encoding)) { // Quoted-Printable 인코딩 방식
                    decodedString.append(quotedPrintableDecode(encodedText, charset));
                }
            } catch (Exception e) {
                decodedString.append(encodedHeader); // 디코딩 실패 시 원본 사용
            }
        }

        // 디코딩 결과가 없으면 원본 텍스트 반환
        return decodedString.length() > 0 ? decodedString.toString() : encodedHeader;
    }

    private static String quotedPrintableDecode(String encodedText, String charset) throws Exception {
        // Q 인코딩 문자열을 디코딩
        StringBuilder result = new StringBuilder();

        // Q인코딩에서 "_"는 공백으로 변환
        byte[] bytes = encodedText.replace("_", " ").getBytes(StandardCharsets.US_ASCII);

        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == '=') { // "="는 16진수 값을 의미
                String hex = new String(bytes, i + 1, 2); // 다음 두 문자를 16진수로 읽음
                result.append((char) Integer.parseInt(hex, 16)); // 16진수 값 디코딩
                i += 2;
            } else {
                result.append((char) bytes[i]); // 다른 경우 그대로 추가
            }
        }

        // 결과 문자열을 지정된 문자셋으로 변환
        return new String(result.toString().getBytes(StandardCharsets.ISO_8859_1), charset);
    }
}
