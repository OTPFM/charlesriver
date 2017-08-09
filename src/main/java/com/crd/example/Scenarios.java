package com.crd.example;

import com.crd.client.ClientSession;
import com.crd.client.ServiceException;
import com.crd.client.TransportException;
import com.crd.client.util.ResultSetRequest;
import com.crd.beans.ScenarioBeanStream;
import com.crd.beans.ScenarioBean;
import com.crd.compliancesvc.Compliance;

import java.io.IOException;

/**
 * This example show how to fetch scenario beans using the Compliance web service.
 *
 * This example relies on predefined result set queries that must be present in the pdf_result_set database table.
 * (see the SYSTEM ADMINISTRATION > Administrative Tasks > Result Sets topic in Charles River IMS online help)
 * <pre>
 * Required Result Set Queries
 *
 * I.  Name: com.crd.myws.Scenarios
 *     sql: SELECT <columns/> FROM TS_SCENARIO SC WHERE 1=1 <predicate/> <auth table="TS_SCENARIO"/>
 *     Description: This query accepts a predicate for more flexibility in building the where clause for the query.
 * </pre>
 */
public class Scenarios
{
    public static void fetch(ClientSession clientSession, String scenarioCode) throws ServiceException, TransportException, IOException
    {
        // create a convenience object that assists in building the arguments to a fetch result set call
        ResultSetRequest resultSetRequest;
        if (scenarioCode.indexOf('%') > 0) {
            // a wild card is present
            // use the correct result set and arguments for retrieving multiple rows
            resultSetRequest = new ResultSetRequest("com.crd.myws.Scenarios", "ScenarioCd LIKE '" + scenarioCode + "'");
        }
        else {
            // use the correct result set and arguments for a single row with an exact match
            resultSetRequest = new ResultSetRequest("com.crd.myws.Scenarios", "ScenarioCd = '" + scenarioCode + "'");
        }
        // only retrive the columns that will be used
        resultSetRequest.addRequestedColumn("ScenarioCd");
        resultSetRequest.addRequestedColumn("ScenarioId");

        // create a web service proxy from the authenticated session
        Compliance compliance = clientSession.getCompliance();

        // fetch a result bean stream
        ScenarioBeanStream scenarioBeanStream = compliance.fetchScenario(
                resultSetRequest.getResultSetName(),
                resultSetRequest.getRequestedColumns(),
                resultSetRequest.getPredicate(),
                resultSetRequest.getStoredQueryId(),
                resultSetRequest.getParameters());

        // process each bean from the stream
        boolean noneFound = true;
        System.out.println("Scenarios that match scenario code: " + scenarioCode);
        ScenarioBean scenarioBean;
        try {
            while ((scenarioBean =scenarioBeanStream.read())!=null) {
                noneFound = false;
                System.out.println("    ScenarioCd: " + scenarioBean.getScenarioCd() + ", ScenarioId: " + scenarioBean.getScenarioId());
            }
        } finally {
            // close the stream in a finally block
            scenarioBeanStream.close();
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
        final String scenarioCode = "A%";

        // create a client side session and disable compression for testing
        ClientSession clientSession = new ClientSession(protocol, hostname, port);
        clientSession.setGzipRequestEnabled(false);
        clientSession.setGzipResponseEnabled(false);

        try {
            // authenticate the session
            clientSession.logon(username, password);

            try {
                // fetch the scenarios
                Scenarios.fetch(clientSession, scenarioCode);
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
