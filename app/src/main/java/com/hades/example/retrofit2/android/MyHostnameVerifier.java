package com.hades.example.retrofit2.android;

import android.util.Log;

import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;

// https://developer.android.google.cn/training/articles/security-ssl?hl=en
// making sure the server you are talking to presents the right certificate. When it doesn't : java.io.IOException: Hostname 'example.com' was not verified
// One reason this can happen is due to a server configuration error. The server is configured with a certificate that does not have a subject or subject alternative name fields that match the server you are trying to reach. It is possible to have one certificate be used with many different servers.
// Unfortunately this can happen for another reason as well: virtual hosting
public class MyHostnameVerifier implements HostnameVerifier {
    private static final String TAG = MyHostnameVerifier.class.getName();
    private Map<String, List<String>> mHost_hashOfPublicKey_map = new HashMap<>();

    public MyHostnameVerifier() {
        List<String> hashedPublicKeys = new ArrayList<>();
        hashedPublicKeys.add("[-117, -84, -46, 2, -24, -15, 95, -94, -128, 45, -30, -32, -26, -41, 10, -98, -13, -120, -97, -29, -20, -95, 69, -56, -128, 64, -10, 76, 109, -48, 96, -104]");
        hashedPublicKeys.add("[22, -105, -115, 22, -81, 28, 113, 35, 31, 63, 10, -39, -105, -62, 25, -90, -16, -64, 126, 38, -72, -66, -65, 123, 37, -99, 96, 66, -117, 16, 116, 16]");
        mHost_hashOfPublicKey_map.put("gitee.com", hashedPublicKeys);

        List<String> hashedPublicKeys2 = new ArrayList<>();
        hashedPublicKeys2.add("[-43, -116, -68, -98, -9, 112, -113, 125, 110, -12, -118, -107, -4, -98, -24, -53, 72, 61, 25, -21, 17, 66, -123, -41, 69, -58, 21, -63, 41, 23, 19, 97]");
        mHost_hashOfPublicKey_map.put("blog.csdn.net", hashedPublicKeys2);

        /*
        ERROR:
        2020-06-16 14:03:06.359 17676-17676/com.example.hades.retrofit2 E/MainActivity: Hostname yingvickycao.github.io not verified:
        certificate: sha256/xlDAST56PmiT3SR0WdFOR3dghwJrQ8yXx6JLSqTIRpk=
        DN: CN=www.github.com,O=GitHub\, Inc.,L=San Francisco,ST=California,C=US
        subjectAltNames: [www.github.com, *.github.com, github.com, *.github.io, github.io, *.githubusercontent.com, githubusercontent.com]
         */
        List<String> github = new ArrayList<>();
        github.add("[-58, 80, -64, 73, 62, 122, 62, 104, -109, -35, 36, 116, 89, -47, 78, 71, 119, 96, -121, 2, 107, 67, -52, -105, -57, -94, 75, 74, -92, -56, 70, -103]");
        mHost_hashOfPublicKey_map.put("yingvickycao.github.io", github);
    }

    @Override
    public boolean verify(String hostname, SSLSession session) {
        try {
            // TODO: 2020/6/15  SSLSession.getPeerCertificates()返回多个
            Certificate[] certificates = session.getPeerCertificates();
            if (certificates != null && certificates.length > 1) {
                byte[] publick_key = certificates[0].getPublicKey().getEncoded();
                Log.e(TAG, "verify:publick_key:" + Arrays.toString(publick_key));
                byte[] dest = SHATool.getInstance().digest_bytes2bytes(publick_key);
                String hashedPublicKey = Arrays.toString(dest);
                Log.d(TAG, "verify: hashedPublicKey=" + hashedPublicKey);
                List<String> hashedPublicKeys = mHost_hashOfPublicKey_map.get(hostname);
                if (null == hashedPublicKeys) {
                    return false;
                }
                for (String item : hashedPublicKeys) {
                    if (item.equalsIgnoreCase(hashedPublicKey)) {
                        return true;
                    }
                }
            }
            return false;
        } catch (SSLPeerUnverifiedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
