package proxydummies.utilities

import java.security.MessageDigest

class Utils {

    static def generateMD5( s ) {
        String a = s
        MessageDigest digest = MessageDigest.getInstance("MD5")
        digest.update(a.bytes);
        new BigInteger(1, digest.digest()).toString(16).padLeft(32, '0')
    }

}
