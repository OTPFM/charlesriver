package com.crd.example;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Map;

import com.crd.client.ClientSession;
import com.crd.client.ResultSetStream;
import com.crd.client.ServiceException;
import com.crd.client.TransportException;
import com.crd.reference.IMSUtilities;
import com.crd.util.Parameter;

/**
 * This is a sample client to get PMAR calculation results using the Charles River Web Services Framework.
 * Clients will use the PMAR Data Export function in the CRIMS client to create data export profiles saved to PMA_EXPORT_PROFILE table.
 * The web service client example depends on the existence of a data export profile that specifies the types of calculations required.
 */
public class GetPMAData {
    // This is the named ResultSet for PMAR data
    private static final String RESULTSET_NAME = "Client.PMAR.Result";

    @SuppressWarnings("rawtypes")
    public static void main(String[] args) {
        // define the host server
        final String protocol = "http";
        final String hostname = "localhost";
        final int port = 8081;

        // define the authentication info
        final String username = "tm_dev";
        final String password = "tm_dev";

        // create a client side session and disable compression for testing
        ClientSession clientSession = new ClientSession(protocol, hostname, port);
        clientSession.setGzipRequestEnabled(false);
        clientSession.setGzipResponseEnabled(false);

        IMSUtilities imsUtilities = clientSession.getIMSUtilities();

        try {
            // authenticate the session
            clientSession.logon(username, password);

            // These are parameters needed to execute the PMAR query.
            Parameter[] parameters = getParameters();

            ResultSetStream resultStream = imsUtilities.fetchResultSet(RESULTSET_NAME, null, null, 0, parameters);

            try {

                // Read each row as a hashtable of column/value pairs.
                // The export profile dictates which calculation fields are returned.
                // For example, if the profile says to include performance fields, all performance calculation fields will be returned.
                // Each row contains the same set of fields, with different values for the specific security.
                // The following sample table contains sample fields that may be returned
                //
                //   accountCode label   beginWeight totalBaseReturn totalLocalReturn   ...
                //   -------------------------------------------------------------------------
                //     ABC        IBM     .123          8.71              9.26
                //     ABC        GOOG    .234          6.92              7.35
                //   -------------------------------------------------------------------------
                //
                // The Map would have entries that look like:
                //     accountCode=ABC, label=IBM, beginWeight=.123 ...
                //     accountCode=ABC, label=GOOG, beginWeight=.234 ...

                System.out.println("=========================================================================");
                System.out.println("                      PMA Calculation Results");
                System.out.println("=========================================================================");

                Map<String,String> resultMap = resultStream.read();


                while (resultMap != null && ! resultMap.isEmpty()) {
                    for (Map.Entry<String,String> entry : resultMap.entrySet()) {
                        System.out.println(String.format("%s: %s", entry.getKey(), entry.getValue()));
                    }
                    System.out.println("---------------------------------");
                    resultMap = resultStream.read();
                }
                System.out.println("=========================================================================");

            } finally {
                resultStream.close();
                clientSession.logout();
            }

        } catch (ServiceException e) {
            System.out.println("Error message received from server: " + e.getMessage());
        } catch (TransportException e) {
            System.out.println("Error communicating with server: " + e.getMessage());
        } catch (IOException e){
            System.out.println("Error message received from server: " + e.getMessage());
        }
    }


    /*
     * This method creates an array of five Parameter objects to hold query parameters.
     *    accountCode: The account whose PMAR data is requested.
     *    benchmarkCode: The benchmark code whose PMAR data is to be compared with the account.
     *    startDate: Starting date for the calculations.
     *    endDate: Ending date for the calculations.
     *    exportProfileID: The PMAR data export profile ID. This corresponds to the PMA_PROFILE_ID primary key in the PMA_EXPORT_PROFILE table.
     *  Each Parameter object is a name/value pair. The name is a key field in the query handling class and needs to match exactly.
     *
     *  @return An array of five {@link Parameter} objects to hold query parameters.
     */
    private static Parameter[] getParameters() {
        Parameter[] parameters = new Parameter[5];
        int index = 0;
        parameters[index++] = new Parameter("accountCode", "PMA-CRDINTAC");
        parameters[index++] = new Parameter("benchmarkCodes", "PMA-CRDINTACD");
        parameters[index++] = new Parameter("startDate", Date.valueOf("2002-01-01"));
        parameters[index++] = new Parameter("endDate", Date.valueOf("2002-01-31"));
        parameters[index] = new Parameter("exportProfileID", BigDecimal.valueOf(1));
        return parameters;
    }

}
