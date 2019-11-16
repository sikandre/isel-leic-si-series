
import javax.net.ssl.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;


public class HttpsClient {
    private static String HOST = "www.secure-server.edu";
    private static int PORT = 4433;

    private static final String clientKeyStoreFileName = "client.jks";
    private static final String trustedRootsFileName = "trustedroots.jks";

    private static char[] pw =  System.getenv("https_client_password").toCharArray();

    public static void main(String[] args) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException, URISyntaxException {

        configureSocketAndCompleteHandshake();
    }

    private static void configureSocketAndCompleteHandshake() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException, URISyntaxException {
        // configurar keystore (chaves privadas)
        KeyManagerFactory kmf = configureKeyManagerFactory();
        // configurar trust manager - trust roots
        TrustManagerFactory tmf = configureTrustManagerFactory();

        SSLContext ctx = SSLContext.getInstance("TLSv1.2");
        ctx.init(kmf.getKeyManagers(),tmf.getTrustManagers(), new java.security.SecureRandom());

        // socket factory com as raízes de confiança configuradas
        SSLSocketFactory factory = ctx.getSocketFactory();

        // socket SSL de cliente
        SSLSocket socket = (SSLSocket) factory.createSocket(HOST, PORT);

        // Mostrar certificado do servidor
        System.out.println(socket.getSession().getPeerCertificates()[0]);

        // mostrar esquemas criptográficos acordados
        System.out.println(socket.getSession().getCipherSuite());

    }

    private static TrustManagerFactory configureTrustManagerFactory() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
        KeyStore ts = KeyStore.getInstance(KeyStore.getDefaultType());
        InputStream is = new FileInputStream(getResourcePath(trustedRootsFileName).toFile());
        ts.load(is, pw);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX");
        tmf.init(ts);
        return tmf;
    }

    private static KeyManagerFactory configureKeyManagerFactory() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, URISyntaxException {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        InputStream instream = new FileInputStream(getResourcePath(clientKeyStoreFileName).toFile());
        ks.load(instream, pw);

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("PKIX");
        keyManagerFactory.init(ks,pw);
        return keyManagerFactory;
    }


    private static Path getResourcePath(String fileName) throws URISyntaxException {
        File file = new File(HttpsClient.class.getResource(fileName).getFile().replace("%20"," "));
        System.out.println(file);
        Path path = file.toPath();

        return path;
    }
}
