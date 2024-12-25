package org.example;

import org.example.RootCert.RootCert;

public class Main {
    public static void main(String[] args) {

        try {
//            IniFileReader file = new IniFileReader();
//            System.out.println(file.getMap());
            RootCert rootCert = new RootCert();
            rootCert.createCertTest();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}