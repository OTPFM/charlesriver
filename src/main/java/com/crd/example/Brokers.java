package com.crd.example;

import com.crd.client.ClientSession;
import com.crd.client.ServiceException;
import com.crd.client.TransportException;
import com.crd.client.util.ResultSetRequest;
import com.crd.reference.ReferenceData;
import com.crd.beans.BrokerBeanStream;
import com.crd.beans.BrokerBean;

import java.io.IOException;

/**
 * This example shows how to fetch broker beans using the ReferenceData web service.
 *
 * This example relies on predefined result set queries that must be present in the pdf_result_set database table.
 * (see the SYSTEM ADMINISTRATION > Administrative Tasks > Result Sets topic in Charles River IMS online help)
 * <pre>
 * Required Result Set Queries
 *
 * I.  Name: com.crd.myws.Broker
 *     Sql: <parameters><parameter name="BkrCd"/></parameters> SELECT <columns/> FROM CS_BROKER B WHERE B.BKR_CD=<lookup name="BkrCd"/> <auth table="CS_BROKER"/>
 *     Description: This query expects a parameter named 'BkrCd' for an exact match on that column.
 *
 * II. Name: com.crd.myws.Brokers
 *     sql:  SELECT <columns/> FROM CS_BROKER B WHERE 1=1 <predicate/> <auth table="CS_BROKER"/>
 *     Description: This query accepts a predicate for more flexibility in building the where clause for the query.
 * </pre>
 */
public class Brokers
{
    public static void fetch(ClientSession clientSession, String brokerCode) throws ServiceException, TransportException, IOException
    {
        // create a convenience object that assists in building the arguments to a fetch result set call
        ResultSetRequest resultSetRequest;
        if (brokerCode.indexOf('%') > 0) {
            // a wild card is present
            // use the correct result set and arguments for retrieving multiple rows
            resultSetRequest = new ResultSetRequest("com.crd.myws.Brokers", "BkrCd LIKE '" + brokerCode + "' ");
        }
        else {
            // use the correct result set and arguments for a single row with an exact match
            resultSetRequest = new ResultSetRequest("com.crd.myws.Broker");
            resultSetRequest.addParameter("BkrCd", brokerCode);
        }
        // only retrive the columns that will be used
        resultSetRequest.addRequestedColumn("BkrCd");
        resultSetRequest.addRequestedColumn("BkrName");

        // create a web service proxy from the authenticated session
        ReferenceData referenceData = clientSession.getReferenceData();

        // fetch a result bean stream
        BrokerBeanStream brokerBeanStream = referenceData.fetchBroker(
                resultSetRequest.getResultSetName(),
                resultSetRequest.getRequestedColumns(),
                resultSetRequest.getPredicate(),
                resultSetRequest.getStoredQueryId(),
                resultSetRequest.getParameters());

        // process each bean from the stream
        boolean noneFound = true;
        System.out.println("Brokers that match broker code: " + brokerCode);
        BrokerBean brokerBean;
        try {
            while ((brokerBean=brokerBeanStream.read())!=null) {
                noneFound = false;
                System.out.println("    BkrCd: " + brokerBean.getBkrCd() + ", BkrName: " + brokerBean.getBkrName());
            }
        } finally {
            // close the stream in a finally block
            brokerBeanStream.close();
        }
        if (noneFound) {
            System.out.println("    none found");
        }
    }

    public static void main(String[] args)
    {
        // define the host server
        final String protocol = "http";
        final String hostname = "localhost";
        final int port = 80;

        // define the authentication info
        final String username = "tm_dev";
        final String password = "tm_dev";

        // define the search string
        final String brokerCode = "AB%";

        // create a client side session and disable compression for testing
        ClientSession clientSession = new ClientSession(protocol, hostname, port);
        clientSession.setGzipRequestEnabled(false);
        clientSession.setGzipResponseEnabled(false);

        try {
            // authenticate the session
            clientSession.logon(username, password);

            try {
                // fetch the brokers
                fetch(clientSession, brokerCode);
            }
            finally {
                // terminate the session
                clientSession.logout();
            }
        }
        catch (IOException e) {
            System.out.println("Error reading result stream: " + e.getLocalizedMessage());
        }
        catch (ServiceException e) {
            System.out.println("Error message received from server: " + e.getFaultString());
        }
        catch (TransportException e) {
            System.out.println("Error communicating with server: " + e.getLocalizedMessage());
        }
    }
}
