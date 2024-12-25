package org.example.RootCert;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.example.utils.FileSeparator;
import org.example.utils.PathUtil;
import org.example.utils.X509CertTemplate;

import java.io.FileOutputStream;
import java.io.FileWriter;
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
            // Get the OS-specific file separator
            String separator = FileSeparator.getOSSeparator().getSeparator();
            String path = PathUtil.getCurrentFilePath();

            System.out.println("Separator " + separator + "  path : " + path);

            // Add BouncyCastle as a security provider
            Security.addProvider(new BouncyCastleProvider());

            // Generate a key pair
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();

            // get RootCert by reading RootCertTemplate.ini file
            X509v3CertificateBuilder certBuilder = X509CertTemplate.getX509Certificate("RootCertTemplate.ini", keyPair.getPublic());

            // Sign the certificate
            ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSA").build(keyPair.getPrivate());
            X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certBuilder.build(signer));

            /*// Save the certificate and private key to files
            try (FileOutputStream fos = new FileOutputStream(path + separator + "RootCert" + separator + "cert.pem")) {
                fos.write(cert.getEncoded());
            }
            try (FileOutputStream fos = new FileOutputStream(path + separator + "RootCert" + separator + "private_key.pem")) {
                fos.write(keyPair.getPrivate().getEncoded());
            }*/
            // Save the certificate in PEM format
            try (PEMWriter pemWriter = new PEMWriter(new FileWriter(path + separator + "RootCert" + separator + "cert.pem"))) {
                pemWriter.writeObject(cert);
            }

            // Save the private key in PEM format
            try (PEMWriter pemWriter = new PEMWriter(new FileWriter(path + separator + "RootCert" + separator + "private_key.pem"))) {
                pemWriter.writeObject(keyPair.getPrivate());
            }

            System.out.println("Certificate generated successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
