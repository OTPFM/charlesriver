package com.crd.example;

import com.crd.client.ClientSession;
import com.crd.client.DefaultLogonHandler;
import com.crd.client.KerberosLogonHandler;
import com.crd.client.ServiceException;
import com.crd.client.TransportException;
import com.crd.reference.IMSUtilities;
import com.crd.session.LogonInfo;
import com.crd.reference.DatabaseInfo;

public class Miscellaneous
{

    private static void customResponseTimeout(ClientSession clientSession)
            throws ServiceException, TransportException
    {
        IMSUtilities imsUtilities = clientSession.getIMSUtilities();
        // Use a custom response timeout.  The default is
        imsUtilities.setResponseTimeout(3*60*1000); // 3 minutes
        DatabaseInfo databaseInfo = imsUtilities.getDatabaseInfo();
        System.out.println("Database Name:" + databaseInfo.getDatabaseName());
    }

    public static void main(String[] args)
    {
        // define the host server
        final String protocol = "http";
        final String hostname = "localhost";
        final int port = 80;

        ClientSession clientSession = new ClientSession(protocol, hostname, port);
        try {
            clientSession.logon();
            try {
                customResponseTimeout(clientSession);
            }
            finally {
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
