package org.example.CACert;

import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.example.utils.FileSeparator;
import org.example.utils.PathUtil;
import org.example.utils.X509CertTemplate;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;

public class CACert {

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

            // Load the root certificate and its private key (cert and private_key files)
            FileInputStream certFile = new FileInputStream("path_to_cert.pem");
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate rootCert = (X509Certificate) certFactory.generateCertificate(certFile);

            PrivateKey rootPrivateKey = (PrivateKey) KeyFactory.getInstance("RSA").generatePrivate(
                    new PKCS8EncodedKeySpec(Files.readAllBytes(Paths.get("path_to_private_key.pem")))
            );

            // Sign the child certificate with the root private key
            ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSA").build(rootPrivateKey);
            X509Certificate caCert = new JcaX509CertificateConverter().setProvider("BC")
                    .getCertificate(certBuilder.build(signer));


            // Save the certificate in PEM format
            try (PEMWriter pemWriter = new PEMWriter(new FileWriter(path + separator + "RootCert" + separator + "cert.pem"))) {
                pemWriter.writeObject(caCert);
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
