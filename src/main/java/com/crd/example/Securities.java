package com.crd.example;

import com.crd.client.ClientSession;
import com.crd.client.ServiceException;
import com.crd.client.TransportException;
import com.crd.client.util.ResultSetRequest;
import com.crd.reference.SecuritiesIssuers;
import com.crd.beans.SecurityBeanStream;
import com.crd.beans.SecurityBean;

import java.io.IOException;
import java.util.GregorianCalendar;

/**
 * This example show how to create, fetch, update, and delete securities using the SecuritiesIssuers web service.
 *
 * This example relies on predefined result set queries that must be present in the pdf_result_set database table.
 * (see the SYSTEM ADMINISTRATION > Administrative Tasks > Result Sets topic in Charles River IMS online help)
 * <pre>
 * Required Result Set Queries
 *
 * I.  Name: com.crd.myws.Securities
 *     sql: SELECT <columns/> FROM CSM_SECURITY S JOIN CSM_SECURITY_CUST SCST ON S.SEC_ID=SCST.SEC_ID WHERE 1=1 <predicate/>
 *     Description: This query accepts a predicate for more flexibility in building the where clause for the query.
 * </pre>
 */
public class Securities
{
    /**
     * @param securitiesIssuers a web service proxy using an authenticated client session.
     * @param identifier the search string.
     * @param identifierFieldName the column name to search for the provided identifier. (SecId, SecName, Ticker, ExtSecId, Cusip, Valoran, IsinNo, Sedol, etc.)
     */
    public static SecurityBean fetch(SecuritiesIssuers securitiesIssuers, String identifier, String identifierFieldName) throws ServiceException, TransportException, IOException
    {
        if (identifier==null || identifier.length()==0) {
            throw new IllegalArgumentException("identifier may not be null or empty");
        }
        if (identifierFieldName ==null || identifierFieldName.length()==0) {
            throw new IllegalArgumentException("identifierFieldName may not be null or empty");
        }

        // create a convenience object that assists in building the arguments to a fetch result set call
        ResultSetRequest resultSetRequest;
        resultSetRequest = new ResultSetRequest("com.crd.myws.Securities", identifierFieldName + " = '" + identifier + "'");

        // fetch a result bean stream
        SecurityBeanStream securityBeanStream = securitiesIssuers.fetchSecurity(
                resultSetRequest.getResultSetName(),
                resultSetRequest.getRequestedColumns(),
                resultSetRequest.getPredicate(),
                resultSetRequest.getStoredQueryId(),
                resultSetRequest.getParameters());

        // return the first bean from the stream (there should only be one)
        SecurityBean securityBean;
        try {
            securityBean = securityBeanStream.read();
        } finally {
            // close the stream in a finally block
            securityBeanStream.close();
        }
        return securityBean;
    }

    private static void populateSecurityBean(SecurityBean securityBean)
    {
        securityBean.setSecName("Java Example Security Test Note 6.7% 02/10");
        securityBean.setSecTypCd("CP");
        securityBean.setCouponRate(6.7);
        securityBean.setMatureDate(new java.sql.Date((new GregorianCalendar(2010, 2, 10)).getTimeInMillis()));
        securityBean.setIssuerCd("CRDISSUER"); // This issuer must already exist in the databse
        securityBean.setCusip((""+System.currentTimeMillis()).substring(4, 12));
        securityBean.setMktPrice(99.05);
        securityBean.setInterestPct(1.685416667);
        securityBean.setIssueCntryCd("USA");
        securityBean.setIssueStateCd("MA");
        securityBean.setAccrualTypCd("FIXRT");
        securityBean.setConversionFactor(1);
        securityBean.setCrrncyFxDisp("N");
        securityBean.setExdivFlag("N");
        securityBean.setFedTxbl("N");
        securityBean.setFixIoiViewed("N");
        securityBean.setListExchCd("OTC");
        securityBean.setNonMktbl("N");
        securityBean.setPaymentFreq("M");
        securityBean.setPrivPlcmnt("N");
        securityBean.setRestrResale("N");
        securityBean.setComments("create security java example");
    }

    private static void displayMessage(String message, SecurityBean securityBean)
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("    SecurityBean (");
        buffer.append("SecId:").append(securityBean.getSecId()).append(", ");
        buffer.append("SecName:").append(securityBean.getSecName()).append(", ");
        buffer.append("CUSIP:").append(securityBean.getCusip()).append(", ");
        buffer.append("Comments:").append(securityBean.getComments());
        buffer.append(")");
        System.out.println(message);
        System.out.println(buffer.toString());
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

        // create a client side session and disable compression for testing
        ClientSession clientSession = new ClientSession(protocol, hostname, port);
        clientSession.setGzipRequestEnabled(false);
        clientSession.setGzipResponseEnabled(false);

        try {
            // authenticate the session
            clientSession.logon(username, password);

            try {
                // create a web service proxy from the authenticated session
                SecuritiesIssuers securitiesIssuers = clientSession.getSecuritiesIssuers();

                // construct a security with default values and create it on the server
                SecurityBean securityBean = new SecurityBean(true);
                populateSecurityBean(securityBean);
                securitiesIssuers.insertSecurity(securityBean);
                displayMessage("Created security", securityBean);


                // fetch the security
                final String identifier = securityBean.getCusip();
                final String identifierFieldName = "Cusip";
                SecurityBean fetchedBean = Securities.fetch(securitiesIssuers, identifier, identifierFieldName);
                if (fetchedBean!=null) {
                    displayMessage("Retrieved security", fetchedBean);

                    // update the security
                    SecurityBean modifiedBean = (SecurityBean)fetchedBean.clone();
                    modifiedBean.setComments("update security java example");
                    securitiesIssuers.updateSecurity(fetchedBean, modifiedBean);
                    displayMessage("Updated security", modifiedBean);

                    // fetch the security
                    fetchedBean = Securities.fetch(securitiesIssuers, identifier, identifierFieldName);
                    displayMessage("Retrieved security", fetchedBean);

                    // delete the security
                    securitiesIssuers.deleteSecurity(fetchedBean);
                    displayMessage("Deleted security", fetchedBean);

                    // fetch the security
                    fetchedBean = Securities.fetch(securitiesIssuers, identifier, identifierFieldName);
                    if (fetchedBean != null) {
                        System.out.println("Unable to delete the security bean.");
                    }
                }
                else {
                    System.out.println("Unable to retrieve the newly created security bean.");
                }
            }
            finally {
                // terminate the session
                clientSession.logout();
            }
        }
        catch (CloneNotSupportedException e) {
            System.out.println("Error cloning object: " + e.getLocalizedMessage());
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
