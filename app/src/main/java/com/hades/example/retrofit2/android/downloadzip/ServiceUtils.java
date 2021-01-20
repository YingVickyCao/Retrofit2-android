package com.hades.example.retrofit2.android.downloadzip;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ServiceUtils {
    public static <T> T createService(Class<T> serviceClass, String baseUrl, final IDownloadProgress callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(createOkHttpClient(callback))
//                .client(new OkHttpClient())
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())      // return String:Call<String>
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(serviceClass);
    }

    private static OkHttpClient createOkHttpClient(final IDownloadProgress callback) {
        /**
         * // 先填写一个错的hash值，然后根据随后的exception的stack trace message，得到对应的hash值。
         *        Certificate pinning failure!
         *       Peer certificate chain:
         *         sha256/i6zSAujxX6KALeLg5tcKnvOIn+PsoUXIgED2TG3QYJg=: CN=*.gitee.com
         *         sha256/jzqM6/58ozsPRvxUzg0hzjM+GcfwhTbU/G0TCDvL7hU=: CN=TrustAsia TLS RSA CA,OU=Domain Validated SSL,O=TrustAsia Technologies\, Inc.,C=CN
         *         sha256/r/mIkG3eEpVdm+u/ko/cwxzOMo1bk4TyHIlByibiA5E=: CN=DigiCert Global Root CA,OU=www.digicert.com,O=DigiCert Inc,C=US
         *       Pinned certificates for gitee.com:
         *         sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=
         */

        String hostname = "gitee.com";
        CertificatePinner certificatePinner = new CertificatePinner.Builder()
//                .add(hostname, "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
                .add(hostname, "sha256/i6zSAujxX6KALeLg5tcKnvOIn+PsoUXIgED2TG3QYJg=")
                .add(hostname, "sha256/i6zSAujxX6KALeLg5tcKnvOIn+PsoUXIgED2TG3QYJg=")
                .build();

        return new OkHttpClient.Builder()
//                .certificatePinner(certificatePinner)
                .hostnameVerifier(new MyHostnameVerifier())
//                .addInterceptor(new Interceptor() {
//                    @Override
//                    public okhttp3.Response intercept(Chain chain) throws IOException {
//                        okhttp3.Response originResponse = chain.proceed(chain.request());
//                        return originResponse.newBuilder().body(new FileResponseBody(originResponse.body(), callback)).build();
//                    }
//                })
                .addInterceptor(new CustomInterceptor())
//                .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS,ConnectionSpec.COMPATIBLE_TLS))
                .build();

    }
}
