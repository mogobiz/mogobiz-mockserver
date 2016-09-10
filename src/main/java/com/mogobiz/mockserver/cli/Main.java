package com.mogobiz.mockserver.cli;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.mogobiz.mockserver.spi.MockService;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.configuration.IntegerStringListParser;
import org.mockserver.mockserver.MockServerBuilder;
import org.mockserver.proxy.ProxyBuilder;
import org.mockserver.stop.StopEventQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.util.*;

/**
 *
 * Created by smanciot on 14/04/16.
 */
public class Main {

    private static final String LF = System.getProperty("line.separator");
    public static final String SERVER_PORT_KEY = "serverPort";
    public static final String PROXY_PORT_KEY = "proxyPort";
    public static final String PROXY_REMOTE_PORT_KEY = "proxyRemotePort";
    public static final String PROXY_REMOTE_HOST_KEY = "proxyRemoteHost";
    public static final String USAGE = "" +
            "   java -jar <path to mockserver-jetty-jar-with-dependencies.jar> [-serverPort <port>] [-proxyPort <port>] [-proxyRemotePort <port>] [-proxyRemoteHost <hostname>]" + System.getProperty("line.separator") +
            "                                                                                       " + LF +
            "     valid options are:                                                                " + LF +
            "        -serverPort <port>           Specifies the HTTP and HTTPS port for the         " + LF +
            "                                     MockServer. Port unification is used to           " + LF +
            "                                     support HTTP and HTTPS on the same port.          " + LF +
            "                                                                                       " + LF +
            "        -proxyPort <port>            Specifies the HTTP, HTTPS, SOCKS and HTTP         " + LF +
            "                                     CONNECT port for proxy. Port unification          " + LF +
            "                                     supports for all protocols on the same port.      " + LF +
            "                                                                                       " + LF +
            "        -proxyRemotePort <port>      Specifies the port to forward all proxy           " + LF +
            "                                     requests to (i.e. all requests received on        " + LF +
            "                                     portPort). This setting is used to enable         " + LF +
            "                                     the port forwarding mode therefore this           " + LF +
            "                                     option disables the HTTP, HTTPS, SOCKS and        " + LF +
            "                                     HTTP CONNECT support.                             " + LF +
            "                                                                                       " + LF +
            "        -proxyRemoteHost <hostname>  Specified the host to forward all proxy           " + LF +
            "                                     requests to (i.e. all requests received on        " + LF +
            "                                     portPort). This setting is ignored unless         " + LF +
            "                                     proxyRemotePort has been specified. If no         " + LF +
            "                                     value is provided for proxyRemoteHost when        " + LF +
            "                                     proxyRemotePort has been specified,               " + LF +
            "                                     proxyRemoteHost will default to \"localhost\".    " + LF +
            "                                                                                       " + LF +
            "   i.e. java -jar ./mockserver-jetty-jar-with-dependencies.jar -serverPort 1080 -proxyPort 1090 -proxyRemotePort 80 -proxyRemoteHost www.mock-server.com" + LF +
            "                                                                                       " + LF;

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final IntegerStringListParser INTEGER_STRING_LIST_PARSER = new IntegerStringListParser();
    @VisibleForTesting
    static ProxyBuilder httpProxyBuilder = new ProxyBuilder();
    @VisibleForTesting
    static MockServerBuilder mockServerBuilder = new MockServerBuilder();
    @VisibleForTesting
    static StopEventQueue stopEventQueue = new StopEventQueue();
    @VisibleForTesting
    static PrintStream outputPrintStream = System.out;
    @VisibleForTesting
    static Runtime runtime = Runtime.getRuntime();
    private static boolean usagePrinted = false;


    /**
     * Run the MockServer directly providing the parseArguments for the server and httpProxyBuilder as the only input parameters (if not provided the server port defaults to 8080 and the httpProxyBuilder is not started).
     *
     * @param arguments the entries are in pairs:
     *                  - "-serverPort"       followed by the server          port if not provided the MockServer is not started,
     *                  - "-proxyPort"        followed by the proxy           port if not provided the Proxy is not started,
     *                  - "-proxyRemotePort"  followed by the proxyRemotePort port,
     *                  - "-proxyRemoteHost"  followed by the proxyRemoteHost port
     */
    public static void main(String... arguments) {
        usagePrinted = false;

        Map<String, String> parsedArguments = parseArguments(arguments);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(LF + LF + "Using command line options: " +
                    Joiner.on(", ").withKeyValueSeparator("=").join(parsedArguments) + LF);
        }

        if (parsedArguments.size() > 0 && validateArguments(parsedArguments)) {
            if (parsedArguments.containsKey(SERVER_PORT_KEY)) {
                mockServerBuilder.withStopEventQueue(stopEventQueue).withHTTPPort(INTEGER_STRING_LIST_PARSER.toArray(parsedArguments.get(SERVER_PORT_KEY))).build();
                final MockServerClient mockServerClient = new MockServerClient("127.0.0.1", Integer.parseInt(parsedArguments.get(SERVER_PORT_KEY)));
                ServiceLoader<MockService> mockServices = ServiceLoader.load(MockService.class);
                for (MockService mockService : mockServices) {
                    mockService.initializeExpectations(mockServerClient);
                }
            }
            if (parsedArguments.containsKey(PROXY_PORT_KEY)) {
                ProxyBuilder proxyBuilder = httpProxyBuilder.withStopEventQueue(stopEventQueue).withLocalPort(Integer.parseInt(parsedArguments.get(PROXY_PORT_KEY)));
                if (parsedArguments.containsKey(PROXY_REMOTE_PORT_KEY)) {
                    String remoteHost = parsedArguments.get(PROXY_REMOTE_HOST_KEY);
                    if (Strings.isNullOrEmpty(remoteHost)) {
                        remoteHost = "localhost";
                    }
                    proxyBuilder.withDirect(remoteHost, Integer.parseInt(parsedArguments.get(PROXY_REMOTE_PORT_KEY)));
                }
                proxyBuilder.build();
            }
        } else {
            showUsage();
        }
    }

    private static boolean validateArguments(Map<String, String> parsedArguments) {
        List<String> errorMessages = new ArrayList<String>();
        validatePortListArgument(parsedArguments, SERVER_PORT_KEY, errorMessages);
        validatePortArgument(parsedArguments, PROXY_PORT_KEY, errorMessages);
        validatePortArgument(parsedArguments, PROXY_REMOTE_PORT_KEY, errorMessages);
        validateHostnameArgument(parsedArguments, PROXY_REMOTE_HOST_KEY, errorMessages);

        if (!errorMessages.isEmpty()) {
            int maxLengthMessage = 0;
            for (String errorMessage : errorMessages) {
                if (errorMessage.length() > maxLengthMessage) {
                    maxLengthMessage = errorMessage.length();
                }
            }
            outputPrintStream.println(LF + "   " + Strings.padEnd("", maxLengthMessage, '='));
            for (String errorMessage : errorMessages) {
                outputPrintStream.println("   " + errorMessage);
            }
            outputPrintStream.println("   " + Strings.padEnd("", maxLengthMessage, '=') + LF);
            return false;
        }
        return true;
    }

    private static void validatePortArgument(Map<String, String> parsedArguments, String argumentKey, List<String> errorMessages) {
        if (parsedArguments.containsKey(argumentKey) && !parsedArguments.get(argumentKey).matches("^\\d+$")) {
            errorMessages.add(argumentKey + " value \"" + parsedArguments.get(argumentKey) + "\" is invalid, please specify a port i.e. \"1080\"");
        }
    }

    private static void validatePortListArgument(Map<String, String> parsedArguments, String argumentKey, List<String> errorMessages) {
        if (parsedArguments.containsKey(argumentKey) && !parsedArguments.get(argumentKey).matches("^\\d+(,\\d+)*$")) {
            errorMessages.add(argumentKey + " value \"" + parsedArguments.get(argumentKey) + "\" is invalid, please specify a comma separated list of ports i.e. \"1080,1081,1082\"");
        }
    }

    private static void validateHostnameArgument(Map<String, String> parsedArguments, String argumentKey, List<String> errorMessages) {
        String validIpAddressRegex = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
        String validHostnameRegex = "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$";
        if (parsedArguments.containsKey(argumentKey) && !(parsedArguments.get(argumentKey).matches(validIpAddressRegex) || parsedArguments.get(argumentKey).matches(validHostnameRegex))) {
            errorMessages.add(argumentKey + " value \"" + parsedArguments.get(argumentKey) + "\" is invalid, please specify a host name i.e. \"localhost\" or \"127.0.0.1\"");
        }
    }

    private static Map<String, String> parseArguments(String... arguments) {
        Map<String, String> parsedArguments = new HashMap<String, String>();
        Iterator<String> argumentsIterator = Arrays.asList(arguments).iterator();
        while (argumentsIterator.hasNext()) {
            String argumentName = argumentsIterator.next();
            if (argumentsIterator.hasNext()) {
                String argumentValue = argumentsIterator.next();
                if (!parsePort(parsedArguments, SERVER_PORT_KEY, argumentName, argumentValue)
                        && !parsePort(parsedArguments, PROXY_PORT_KEY, argumentName, argumentValue)
                        && !parsePort(parsedArguments, PROXY_REMOTE_PORT_KEY, argumentName, argumentValue)
                        && !("-" + PROXY_REMOTE_HOST_KEY).equalsIgnoreCase(argumentName)) {
                    showUsage();
                    break;
                }
                if (("-" + PROXY_REMOTE_HOST_KEY).equalsIgnoreCase(argumentName)) {
                    parsedArguments.put(PROXY_REMOTE_HOST_KEY, argumentValue);
                }
            } else {
                showUsage();
                break;
            }
        }
        return parsedArguments;
    }

    private static boolean parsePort(Map<String, String> parsedArguments, final String key, final String argumentName, final String argumentValue) {
        if (argumentName.equals("-" + key)) {
            parsedArguments.put(key, argumentValue);
            return true;
        }
        return false;
    }

    private static void showUsage() {
        if (!usagePrinted) {
            outputPrintStream.print(USAGE);
            runtime.exit(1);
            usagePrinted = true;
        }
    }
}
