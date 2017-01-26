The keys in this directory are for testing purposes only, never use them in a real application.

## RSA Test Keys

The `rsatest.priv.pem` RSA private key file was generated via the following:

    $ openssl genrsa -out rsatest.priv.pem 2048
    
That private key's corresponding `rsatest.pub.pem` public key was derived via:

    $ openssl rsa -in rsatest.priv.pem -pubout > rsatest.pub.pem
    

## Elliptic Curve Test Keys

The `secp384r1.priv.pem` Elliptic Curve private key file was generated via:

    $ openssl ecparam -name secp384r1 -genkey -noout -out secp384r1.priv.pem
    
Note that this explicitly references the EC curve name `secp384r1`.

For JWT's `ES256`, `ES384` and `ES512` signature algorithms, the 
respective OpenSSL curve names are `secp256k1`, `secp384r1` and `secp512r1`.

That private key's corresponding `secp384r1.pub.pem` public key was derived via:

    $ openssl ec -in secp384r1.priv.pem -pubout -out secp384r1.pub.pem