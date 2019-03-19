package ru.ak.ldap;

import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.util.Base64;

/**
 * @author a.kakushin
 */
public class AttributeReader {

    public static String read(Attribute attr) {

        String result = null;
        String name = attr.getName();

        if (name.equalsIgnoreCase("gecos")) {
            result = readGECOS(attr);

        } else  if (name.equalsIgnoreCase("thumbnailPhoto")) {
            result = readThumbnailPhoto(attr);

        } else if (name.equalsIgnoreCase("objectGUID")) {
            result = readObjectGuid(attr);

        } else if (name.equalsIgnoreCase("description")) {
            result = readDescription(attr);

        } else if (name.equalsIgnoreCase("title")) {
            result = readTitle(attr);

        } else if (!attr.needsBase64Encoding()) {
            result = attr.getValue();
        }

        return result;
    }

    private static String readDescription(Attribute attr) {
        return attr.getValue();
    }

    private static String readGECOS(Attribute attr) {
        return attr.getValue();
    }

    private static String readThumbnailPhoto(Attribute attr) {
        return Base64.encode(attr.getValueByteArray());
    }

    private static String readObjectGuid(Attribute attr) {
        return UUIDUtils.bytesToUUID(attr.getValueByteArray()).toString();
    }

    private static String readTitle(Attribute attr) {
        return attr.getValue();
    }
}
