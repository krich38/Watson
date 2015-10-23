package org.watson.protocol.net;

/**
 * @author Kyle Richards
 * @version 1.0
 */

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactorySpi;
import javax.net.ssl.X509TrustManager;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;

/**
 * @author Kyle Richards
 * @version 2.0
 */
public final class SSLTrustFactory extends TrustManagerFactorySpi {
    private static final TrustManager DUMMY_TRUST_MANAGER = new X509TrustManager() {
        @Override
        public final X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        @Override
        public final void checkClientTrusted(X509Certificate[] chain, String authType) {
            System.err.println("UNKNOWN CLIENT CERTIFICATE: " + chain[0].getSubjectDN());
        }

        @Override
        public final void checkServerTrusted(X509Certificate[] chain, String authType) {
            //System.err.println("UNKNOWN SERVER CERTIFICATE: " + chain[0].getSubjectDN());
        }
    };

    public static TrustManager[] getTrustManagers() {
        return new TrustManager[]{DUMMY_TRUST_MANAGER};
    }

    @Override
    protected final TrustManager[] engineGetTrustManagers() {
        return getTrustManagers();
    }

    @Override
    protected final void engineInit(KeyStore keystore) throws KeyStoreException {
        // Unused
    }

    @Override
    protected final void engineInit(ManagerFactoryParameters managerFactoryParameters) throws InvalidAlgorithmParameterException {
        // Unused
    }
}
