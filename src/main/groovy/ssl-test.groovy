#! /usr/bin/env groovy

import javax.net.ssl.*
import java.io.*
import java.security.*
import java.security.cert.*
import groovy.cli.picocli.*

/**
 * @author Jonatan Ivanov
 */
CliBuilder cli = new CliBuilder(usage: "./${this.class.simpleName} [options] <host>:<port>", width: 250)
cli.with {
    h longOpt: 'help', 'Show usage information'
    _ longOpt: 'trustStore', args: 1, required: false, argName: 'trustStore', ''
    _ longOpt: 'trustStorePassword', args: 1, required: false, argName: 'trustStorePassword', ''
    v longOpt: 'verbose', ''
    _ longOpt: 'insecure', 'Disabe ssl verification and proceed without checking'
}

OptionAccessor options = cli.parse(args)
if (options == null || options.h || options.arguments().size() != 1) {
    cli.usage()
    System.exit(1)
}

def hostAndPort = options.arguments().first().split(':')
String host = hostAndPort[0]
int port = Integer.parseInt(hostAndPort[1])

if (options.trustStore) {
    System.setProperty('javax.net.ssl.trustStore', options.trustStore)
    System.setProperty('javax.net.ssl.trustStorePassword', options.trustStorePassword)
}
if (options.verbose) System.setProperty('javax.net.debug', 'all')

println "Using Java ${System.properties.'java.runtime.version'} from ${System.properties.'java.home'}\n"

class TrustAllX509TrustManager implements X509TrustManager {
    public void checkClientTrusted(X509Certificate[] chain, String authType) {}
    public void checkServerTrusted(X509Certificate[] chain, String authType) {}
    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0] }
}

SSLSocketFactory sslSocketFactory;
if (options.insecure) {
    println 'INSECURE CONNECTION!!!'
    SSLContext sslcontext = SSLContext.getInstance('SSL')
    sslcontext.init(null, new X509TrustManager[] { new TrustAllX509TrustManager() }, new SecureRandom())
    sslSocketFactory = sslcontext.getSocketFactory()
}
else {
    sslSocketFactory = SSLSocketFactory.getDefault()
}

SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(host, port)

println "Client Supported Protocols($sslSocket.supportedProtocols.length): $sslSocket.supportedProtocols"
println "Client Enabled Protocols($sslSocket.enabledProtocols.length): $sslSocket.enabledProtocols"
if (options.verbose) println "Client Enabled Ciphers($sslSocket.supportedCipherSuites.length): $sslSocket.supportedCipherSuites"

println "\nConnecting: $host:$port"
InputStream inputStream = sslSocket.getInputStream()
OutputStream outputStream = sslSocket.getOutputStream()

outputStream.write(1) // Sending a test byte to get some reaction :)
while (inputStream.available() > 0) {
    println inputStream.read()
}

println 'Successfully connected'
