package com.crd.example;

import java.sql.Date;
import java.util.Calendar;

import com.crd.beans.PsReconTransactionBean;
import com.crd.client.ClientSession;
import com.crd.client.ServiceException;
import com.crd.client.TransportException;
import com.crd.posstore.core.PosStoreProcessResult;

/**
 * This example shows how to work with External Transactions in IBOR Transaction Reconciliation
 */                                                                        
public class ReconTransactions
{
    private static void populateReconTransactionBean(PsReconTransactionBean reconTransactionBean)
    {
        // populate unique key of transaction in the external system
        reconTransactionBean.setExtTxnId("ExtTxn-1");
        reconTransactionBean.setFeedCd("SampleClient");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // populate required fields
        reconTransactionBean.setAcctCd("tstacc00001");
        reconTransactionBean.setLongShtCd("L");
        reconTransactionBean.setSecId(1101121381);
        reconTransactionBean.setTxnTypeCd("BUYL");
        reconTransactionBean.setTxnDate(new Date(calendar.getTimeInMillis()));
        reconTransactionBean.setSettleDate(new Date(calendar.getTimeInMillis()));

        // populate more fields as necessary
        reconTransactionBean.setQtyChg(100.0);
        reconTransactionBean.setCrrncyCd("USD");
        reconTransactionBean.setFxRate(1.0);
    }

    private static boolean processResults(PosStoreProcessResult[] results)
    {
        // process results from the IBOR service
        boolean success = true;
        for (PosStoreProcessResult result : results) {
            if (result.isError() || result.isWarning()) {
                System.out.println(result.getSeverity() + " result received from IBOR: " + result.getReason() );
                success = false;
            }
        }
        return success;
    }

    public static void main(String[] args)
    {
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

        try {
            try {
                // Authenticate the client session on the server
                clientSession.logon(username, password);

                // create and populate external transaction object
                PsReconTransactionBean reconTransactionBean = new PsReconTransactionBean(true);
                populateReconTransactionBean(reconTransactionBean);

                ////////////////////////////////////////////
                // insert transaction
                ////////////////////////////////////////////

                PosStoreProcessResult[] iborResults = clientSession.getIBOR().insertReconTransaction(reconTransactionBean);
                boolean inserted = processResults(iborResults);

                if (inserted) {
                    System.out.println("External transaction inserted.");
                } else {
                    System.out.println("External transaction insertion failed.");
                    return;
                }

                ////////////////////////////////////////////////////////
                // reject transaction (this will keep it on the server)
                ////////////////////////////////////////////////////////

                iborResults = clientSession.getIBOR().rejectReconTransaction(reconTransactionBean.getExtTxnId(), reconTransactionBean.getFeedCd());
                boolean rejected = processResults(iborResults);

                if (rejected) {
                    System.out.println("External transaction rejected.");
                } else {
                    System.out.println("External transaction rejection failed.");
                    return;
                }

                ////////////////////////////////////////////////////////
                // delete transaction from the server
                ////////////////////////////////////////////////////////

                iborResults = clientSession.getIBOR().deleteReconTransaction(reconTransactionBean.getExtTxnId(), reconTransactionBean.getFeedCd());
                boolean deleted = processResults(iborResults);

                if (deleted) {
                    System.out.println("External transaction deleted.");
                } else {
                    System.out.println("External transaction deletion failed.");
                }

            }
            finally {
                // terminate the session
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
