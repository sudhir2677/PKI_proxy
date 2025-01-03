package org.example.utils;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class X509CertTemplate {

    public static X509v3CertificateBuilder getX509Certificate(String iniSourceFile, PublicKey publicKey) throws NoSuchAlgorithmException, IOException {
        // Add BouncyCastle as a security provider
        Security.addProvider(new BouncyCastleProvider());

        // Read the ini file and get all the details
        IniFileReader fileReader = new IniFileReader(iniSourceFile);
        Map<String, Map<String, String>> allData = fileReader.getMap();
        for(String key : allData.keySet()){
            System.out.println(allData.get(key));
        }

        // Set certificate details
        X500Name subject = getX509Subject(allData, "req_distinguished_name");

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
                publicKey                // Public key
        );
        return certBuilder;
    }

    private static X500Name getX509Subject(Map<String, Map<String, String>> iniConfig, String parameterList) {
        // Get the section "subject" from the INI file
        Map<String, String> subjectData = iniConfig.get(parameterList);

        // Extract data and create the X500Name
        String cn = subjectData.get("commonName");
        String o = subjectData.get("organizationName");
        String l = subjectData.get("localityName");
        String st = subjectData.get("stateOrProvinceName");
        String c = subjectData.get("countryName");

        // Construct the X500Name
        String distinguishedName = "CN=" + cn + ",O=" + o + ",L=" + l + ",ST=" + st + ",C=" + c;
        System.out.println("data : " + distinguishedName);
        X500Name subject = new X500Name(distinguishedName);
        // Set certificate details
//        X500Name subject = new X500Name("CN=AP cert Testing 2014,O=Msft corp,L=Noida,ST=Delhi,C=IN");
        return subject;
    }
}
