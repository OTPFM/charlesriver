package com.crd.example;

import com.crd.client.ClientSession;
import com.crd.client.ServiceException;
import com.crd.client.TransportException;
import com.crd.client.util.ResultSetRequest;
import com.crd.reference.AccountsPositions;
import com.crd.beans.FundBean;
import com.crd.beans.FundBeanStream;

import java.io.IOException;

/**
 * This example shows how to fetch fund beans using the AccountsPositions web service.
 *
 * This example relies on predefined result set queries that must be present in the pdf_result_set database table.
 * (see the SYSTEM ADMINISTRATION > Administrative Tasks > Result Sets topic in Charles River IMS online help)
 * <pre>
 * Required Result Set Queries
 *
 * I.  Name: com.crd.myws.Fund
 *     Sql: <parameters><parameter name="AcctCd"/></parameters> SELECT <columns/> FROM CS_FUND F WHERE F.ACCT_CD=<lookup name="AcctCd"/> <auth table="CS_FUND"/>
 *     Description: This query expects a parameter named 'AcctCd' for an exact match on that column.
 *
 * II. Name: com.crd.myws.Funds
 *     sql: SELECT <columns/> FROM CS_FUND F WHERE 1=1 <predicate/> <auth table="CS_FUND"/>
 *     Description: This query accepts a predicate for more flexibility in building the where clause for the query.
 * </pre>
 */
public class Funds
{
    public static void fetch(ClientSession clientSession, String accountCode) throws ServiceException, TransportException, IOException
    {
        // create a convenience object that assists in building the arguments to a fetch result set call
        ResultSetRequest resultSetRequest;
        if (accountCode.indexOf('%') > 0) {
            // a wild card is present
            // use the correct result set and arguments for retrieving multiple rows
            resultSetRequest = new ResultSetRequest("com.crd.myws.Funds", "AcctCd LIKE '" + accountCode + "' ");
        }
        else {
            // use the correct result set and arguments for a single row with an exact match
            resultSetRequest = new ResultSetRequest("com.crd.myws.Fund");
            resultSetRequest.addParameter("AcctCd", accountCode);
        }
        // only retrive the columns that will be used
        resultSetRequest.addRequestedColumn("AcctCd");
        resultSetRequest.addRequestedColumn("AcctName");

        // create a web service proxy from the authenticated session
        AccountsPositions accountsPositions = clientSession.getAccountsPositions();

        // fetch a result bean stream
        FundBeanStream fundBeanStream = accountsPositions.fetchFund(
                resultSetRequest.getResultSetName(),
                resultSetRequest.getRequestedColumns(),
                resultSetRequest.getPredicate(),
                resultSetRequest.getStoredQueryId(),
                resultSetRequest.getParameters());

        // process each bean from the stream
        boolean noneFound = true;
        System.out.println("Funds that match account code: " + accountCode);
        FundBean fundBean;
        try {
            while ((fundBean=fundBeanStream.read())!=null) {
                noneFound = false;
                System.out.println("    AcctCd: " + fundBean.getAcctCd() + ", AcctName: " + fundBean.getAcctName());
            }
        } finally {
            // close the stream in a finally block
            fundBeanStream.close();
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
        final String accountCode = "CRDXMPL";

        // create a client side session and disable compression for testing
        ClientSession clientSession = new ClientSession(protocol, hostname, port);
        clientSession.setGzipRequestEnabled(false);
        clientSession.setGzipResponseEnabled(false);

        try {
            // authenticate the session
            clientSession.logon(username, password);

            try {
                // fetch the funds
                Funds.fetch(clientSession, accountCode);
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
