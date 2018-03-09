package vn.axonactive.authentication.domain.utils;

import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.Base64;

/**
 * ConvertUtf8ToNormalUtilTest
 * 
 * @author thsang Remark: Convert a UTF-8 string into normal string
 */
public class ConvertUtils {

    private static final String EMPTY_NULL_STRING_MESSAGE = "String should not be empty or null";

    private static ConvertUtils instance;

    private ConvertUtils() {
    }

    public static ConvertUtils getInstance() {
        if (null == instance) {
            instance = new ConvertUtils();
        }
        return instance;
    }

    public String convertUft8String(String originString) {
        String normalizeString = Normalizer.normalize(originString, Normalizer.Form.NFD);
        normalizeString = normalizeString.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                                            .replace("\u0111", "d")
                                            .replace("\u00d0", "D")
                                            .replace("\u0110", "D")
                                            .replace("\u0189", "D");
        return normalizeString;
    }

    public String encodeBase64ToString(String originalString) {
        if (originalString == null) {
            throw new IllegalArgumentException(EMPTY_NULL_STRING_MESSAGE);
        }
        byte[] encodedBase64String = Base64.getEncoder().encode(originalString.getBytes(Charset.forName("UTF-8")));
        return new String(encodedBase64String);
    }

    public String decodeBase64ToString(String originalString) {
        if (originalString == null) {
            throw new IllegalArgumentException(EMPTY_NULL_STRING_MESSAGE);
        }
        byte[] decodedBase64String = Base64.getDecoder().decode(originalString.getBytes());
        return new String(decodedBase64String);
    }
}
