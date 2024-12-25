package org.example.RootCert;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

public class RootCert {

    public void createCertTest() {
        try {
            // Add BouncyCastle as a security provider
            Security.addProvider(new BouncyCastleProvider());

            // Generate a key pair
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();

            // Set certificate details
            X500Name subject = new X500Name("CN=AP cert Testing 2014,O=Msft corp,L=Noida,ST=Delhi,C=IN");

            // Certificate valid from now to one year
            Date startDate = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, 2);
            Date endDate = calendar.getTime();

            // Build the certificate
            BigInteger serialNumber = new BigInteger(64, new java.security.SecureRandom());
            X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                    subject,                 // Issuer (self-signed, so same as subject)
                    serialNumber,            // Serial number
                    startDate,               // Validity start date
                    endDate,                 // Validity end date
                    subject,                 // Subject
                    keyPair.getPublic()      // Public key
            );

            // Sign the certificate
            ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSA").build(keyPair.getPrivate());
            X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certBuilder.build(signer));

            // Save the certificate and private key to files
            try (FileOutputStream fos = new FileOutputStream("src\\main\\java\\org\\example\\RootCert\\cert.pem")) {
                fos.write(cert.getEncoded());
            }
            try (FileOutputStream fos = new FileOutputStream("src\\main\\java\\org\\example\\RootCert\\private_key.pem")) {
                fos.write(keyPair.getPrivate().getEncoded());
            }

            System.out.println("Certificate generated successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
