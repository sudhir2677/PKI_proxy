package org.example.utils;

import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class IniFileReader {

    /**
     * Usage :
     * assertThat(ini.get("fonts", "letter"))
     *   .isEqualTo("bold");
     */
    private Ini ini;

    public IniFileReader() throws IOException {
        initialize();
    }
    public IniFileReader(String filename) throws IOException {
        initialize(filename);
    }

    public IniFileReader(File fileToParse) throws IOException {
        this.ini = new Ini(fileToParse);
    }

    private void initialize() throws IOException {

        File iniFile = new File(IniFileReader.class.getClassLoader().getResource("CertTemplate.ini").getFile());
        initialize(iniFile);

    }

    private void initialize(String filename) throws IOException {
        initialize(new File(IniFileReader.class.getClassLoader().getResource(filename).getFile()));
    }

    private void initialize(File fileToParse) throws IOException {
        this.ini = new Ini(fileToParse);
    }

    public Ini getIni() { return ini; }

    public Map<String, Map<String, String>> getMap() {

        return this.ini.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
