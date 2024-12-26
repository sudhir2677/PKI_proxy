package org.example.verification;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChainVerification {

    public void validate() throws InvalidAlgorithmParameterException, CertificateException, NoSuchAlgorithmException, FileNotFoundException {
        CertificateFactory certFactory = null;
        X509Certificate childCert = (X509Certificate) certFactory.generateCertificate(new FileInputStream("child_cert.pem"));
        X509Certificate rootCert = (X509Certificate) certFactory.generateCertificate(new FileInputStream("root_cert.pem"));

        List<X509Certificate> certChain = new ArrayList<>();
        certChain.add(childCert);  // The child certificate
        certChain.add(rootCert);   // The root certificate

        // Create the CertPathValidator to validate the certificate chain
        CertPathValidator validator = CertPathValidator.getInstance("PKIX");
        CertPath certPath = CertificateFactory.getInstance("X.509").generateCertPath(certChain);
        PKIXParameters params = new PKIXParameters(Collections.singleton(new TrustAnchor(rootCert, null)));
        params.setRevocationEnabled(false);  // Disable CRL check for simplicity

        try {
            validator.validate(certPath, params);  // Validate the chain
            System.out.println("Certificate chain is valid.");
        } catch (CertPathValidatorException e) {
            System.err.println("Certificate chain is not valid: " + e.getMessage());
        }

    }
}
