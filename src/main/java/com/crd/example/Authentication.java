package com.crd.example;

import com.crd.client.ClientSession;
import com.crd.client.DefaultLogonHandler;
import com.crd.client.KerberosLogonHandler;
import com.crd.client.ServiceException;
import com.crd.client.TransportException;
import com.crd.session.LogonInfo;
import com.crd.reference.DatabaseInfo;

/**
 * This example shows how to login to a Charles River IMS server, make some web service calls, and logout.
 *
 * The logon is accomplished through the following steps.
 *   1.  Create a client side session object - ClientSession.
 *   2.  Initialize the authenticator with a logon handler implementation.
 *   3.  Logon to the server by calling the logon method.  This step authenticates the client side session
 *       by requesting a server side session and storing its resulting session id in the ClientSession object.
 *
 * Once a client session has been authenticated all web services may be used.
 *   1.  Get the last logon date time by calling the Logon web service.
 *   2.  Get the server side session key by calling the Logon web service.
 *   3.  Get the server database name by calling the IMSUtilities web service.
 *
 * After using the web services, the server side session must be terminated by calling
 * the logout() web service method.  This method call should be placed in a finally block to
 * insure that it will be called even when an exception is thrown.
 */
public class Authentication
{
    enum AuthenticationMode { Standard, Kerberos }

    private static void initializeAuthentication(ClientSession clientSession, AuthenticationMode mode)
    {
        switch (mode) {
            case Standard:
                // Standard authentication is the default mode, making the following line optional
                clientSession.setLogonHandler(new DefaultLogonHandler(clientSession, "username", "password"));
                break;
            case Kerberos:
                // initialize the client side session with a Kerberos authenticator, kdc, and realm
                clientSession.setLogonHandler(new KerberosLogonHandler(clientSession, "engineering.crd.com", "ENGINEERING.CRD.COM"));
                break;
        }
    }

    public static void main(String[] args)
    {
        // define the host server
        final String protocol = "http";
        final String hostname = "localhost";
        final int port = 80;

        // create a client side session that will maintain the server side session id
        ClientSession clientSession = new ClientSession(protocol, hostname, port);
        initializeAuthentication(clientSession, AuthenticationMode.Standard);

        // A ClientSession uses gzip compression by default.
        // Disable gzip compression for easier viewing of intercepted http traffic during testing
        clientSession.setGzipRequestEnabled(false);
        clientSession.setGzipResponseEnabled(false);

        try {
            // create an authenticated session on the server
            clientSession.logon();
            try {
                // At this point, the client side session is authenticated.

                // output the date time of the last logon for this user
                LogonInfo logonInfo = clientSession.getLogon().getLogonInfo();
                if (logonInfo.getLastLogonTime()!=null) {
                    System.out.println("Last Logon Time: " + logonInfo.getLastLogonTime().getTime());
                }

                // output the server side session key
                String sessionKey = clientSession.getLogon().getSessionKey();
                System.out.println("Session Key: " + sessionKey);

                // output the database name that is being used by the server
                DatabaseInfo databaseInfo = clientSession.getIMSUtilities().getDatabaseInfo();
                System.out.println("Database Name:" + databaseInfo.getDatabaseName());
            }
            finally {
                // logout in a finally block, so that the session's resources can be released
                clientSession.logout();
            }
        }
        catch (ServiceException e) {
            System.out.println("Error message received from server: " + e.getFaultString());
        }
        catch (TransportException e) {
            System.out.println("Error communicating with server: " + e.getLocalizedMessage());
        }
    }
}
