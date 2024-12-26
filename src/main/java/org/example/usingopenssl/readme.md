To create a **root certificate (RootCert)**, a **CA (Certificate Authority) certificate**, and a **machine certificate** signed by the CA using OpenSSL, while implementing DNS verification with a specific pattern (`*.AP.*`), you need to follow these steps:

We'll go through the process of:
1. Generating the Root Certificate (RootCert).
2. Creating the CA certificate (signed by RootCert).
3. Creating the machine certificate (signed by the CA).
4. Including DNS-based subject alternative names (SAN) in the machine certificate.
5. Validating the DNS pattern `*.AP.*`.

### Step 1: Set Up OpenSSL Configuration Files

Before starting, make sure you have OpenSSL installed on your system.

1. **Create a directory structure for your certificates**:
   ```
   mkdir -p ~/certs/{root,ca,machine}
   cd ~/certs
   ```

2. **Create a configuration file for OpenSSL**. This will include settings for DNS verification and other necessary fields.

   Create a `openssl.cnf` configuration file (or modify the default one) and ensure it includes SAN (Subject Alternative Name) for DNS validation.

   Example `openssl.cnf`:
   ```ini
   [ req ]
   default_bits        = 2048
   default_keyfile     = privkey.pem
   distinguished_name   = req_distinguished_name
   x509_extensions      = v3_ca

   [ req_distinguished_name ]
   countryName           = Country Name (2 letter code)
   countryName_default   = US
   stateOrProvinceName   = State or Province Name (full name)
   stateOrProvinceName_default = California
   localityName          = Locality Name (eg, city)
   localityName_default  = San Francisco
   0.organizationName    = Organization Name (eg, company)
   0.organizationName_default = MyCompany
   commonName            = Common Name (eg, server FQDN or YOUR name)
   commonName_default    = RootCert

   [ v3_ca ]
   basicConstraints = critical,CA:true
   keyUsage = digitalSignature, keyCertSign, cRLSign
   subjectAltName = @alt_names

   [ alt_names ]
   DNS.1 = *.AP.*
   ```

### Step 2: Generate the Root Certificate (RootCert)

The Root Certificate is a self-signed certificate, and it will be used to sign other certificates (CA and machine).

1. **Generate the root private key**:
   ```bash
   openssl genpkey -algorithm RSA -out root/root.key -aes256
   ```

2. **Generate the root certificate (RootCert)**:
   ```bash
   openssl req -new -x509 -key root/root.key -out root/root.crt -days 3650 -config openssl.cnf
   ```

   This will prompt you for details (such as country, state, organization, etc.) to be filled in the certificate.

3. Verify the generated root certificate:
   ```bash
   openssl x509 -noout -text -in root/root.crt
   ```

### Step 3: Create the CA Certificate (Signed by RootCert)

The CA certificate is issued by the root certificate and will be used to sign other certificates (including the machine certificate).

1. **Generate the CA private key**:
   ```bash
   openssl genpkey -algorithm RSA -out ca/ca.key -aes256
   ```

2. **Create a certificate signing request (CSR) for the CA**:
   ```bash
   openssl req -new -key ca/ca.key -out ca/ca.csr -config openssl.cnf
   ```

3. **Sign the CSR with the root certificate to create the CA certificate**:
   ```bash
   openssl x509 -req -in ca/ca.csr -CA root/root.crt -CAkey root/root.key -CAcreateserial -out ca/ca.crt -days 3650 -extensions v3_ca -extfile openssl.cnf
   ```

4. Verify the generated CA certificate:
   ```bash
   openssl x509 -noout -text -in ca/ca.crt
   ```

### Step 4: Create the Machine Certificate (Signed by CA)

The machine certificate will be signed by the CA, and it will include DNS-based Subject Alternative Names (SANs).

1. **Generate the machine private key**:
   ```bash
   openssl genpkey -algorithm RSA -out machine/machine.key -aes256
   ```

2. **Create a certificate signing request (CSR) for the machine**:
   Create a configuration file (e.g., `machine_csr.cnf`) for the machine certificate to specify SANs.

   Example `machine_csr.cnf`:
   ```ini
   [ req ]
   default_bits        = 2048
   default_keyfile     = privkey.pem
   distinguished_name   = req_distinguished_name
   x509_extensions      = v3_req

   [ req_distinguished_name ]
   countryName           = Country Name (2 letter code)
   countryName_default   = US
   stateOrProvinceName   = State or Province Name (full name)
   stateOrProvinceName_default = California
   localityName          = Locality Name (eg, city)
   localityName_default  = San Francisco
   0.organizationName    = Organization Name (eg, company)
   0.organizationName_default = MyCompany
   commonName            = Common Name (eg, server FQDN or YOUR name)
   commonName_default    = machine1.mycompany.com

   [ v3_req ]
   basicConstraints = CA:FALSE
   keyUsage = digitalSignature, keyEncipherment
   subjectAltName = @alt_names

   [ alt_names ]
   DNS.1 = *.AP.*
   ```

3. **Generate the machine CSR**:
   ```bash
   openssl req -new -key machine/machine.key -out machine/machine.csr -config machine_csr.cnf
   ```

4. **Sign the CSR with the CA certificate**:
   ```bash
   openssl x509 -req -in machine/machine.csr -CA ca/ca.crt -CAkey ca/ca.key -CAcreateserial -out machine/machine.crt -days 3650 -extensions v3_req -extfile machine_csr.cnf
   ```

5. Verify the machine certificate:
   ```bash
   openssl x509 -noout -text -in machine/machine.crt
   ```

### Step 5: Certificate Chaining and DNS Verification

When you verify the machine certificate, the DNS pattern `*.AP.*` will match any domain name that follows the pattern (e.g., `Cluster.AP.ServiceName`, `Cluster.AP.OtherServiceName`, etc.).

#### To verify the certificate chain:
```bash
openssl verify -CAfile root/root.crt -untrusted ca/ca.crt machine/machine.crt
```

This verifies that the machine certificate is valid and signed by the CA, and the CA certificate is signed by the root certificate.

#### To test DNS SAN verification:
You can use the following command to check if the DNS SAN (`*.AP.*`) is included in the machine certificate:

```bash
openssl x509 -in machine/machine.crt -noout -text | grep DNS
```

If the DNS SAN is correctly set, it should show something like:
```
X509v3 Subject Alternative Name:
    DNS:*.AP.*
```

### Summary of the Process:

1. **Root Certificate (RootCert)**: Generated and self-signed.
2. **CA Certificate**: Signed by the root certificate to act as a signing authority.
3. **Machine Certificate**: Signed by the CA certificate, with DNS Subject Alternative Names (SAN) like `*.AP.*` for matching DNS names in the format `Cluster.AP.ServiceName`.
4. **DNS Pattern Matching**: The `*.AP.*` pattern is used to match any subdomain where the second part is `AP`, and it can be used for validating services in different clusters and environments.

This process creates a chain of trust between the root certificate, the CA, and the machine certificate, while also implementing DNS verification for a specific pattern.