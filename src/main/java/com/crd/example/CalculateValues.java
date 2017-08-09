package com.crd.example;

import com.crd.client.ClientSession;
import com.crd.client.ServiceException;
import com.crd.client.TransportException;
import com.crd.ordersvc.core.OrderProcessResult;
import com.crd.ordersvc.core.OrderSvcReturn;
import com.crd.ordersvc.rule.calcvalues.CalcValuesRequest;

/**
 * This example shows how to call calculateValues on the Trading service.
 */
public class CalculateValues
{
    public static void calculateValues(ClientSession clientSession, long[] orderIds) throws ServiceException, TransportException
    {
        // create CalcValuesRequest object and assign parameters for calculation
        CalcValuesRequest recalc = new CalcValuesRequest();
        recalc.setCalcBrokerageAuto(true);
        recalc.setCalcBrokerReason(true);
        recalc.setCalcBrokerWarnings(true);
        recalc.setCalcFees(true);
        recalc.setCalcCommissions(true);
        recalc.setCalcFixedAnalytics(true);
        recalc.setTrackOrderExecution(true);

        OrderSvcReturn[] orderSvcReturns = clientSession.getTrading().calculateValues(recalc, orderIds);

        // process the results
        for (int i = 0; i < orderSvcReturns.length; i++) {
            OrderSvcReturn orderSvcReturn = orderSvcReturns[i];
            if (orderSvcReturn.getHasError()) {
                System.out.println("Error calculating values");
                OrderProcessResult[] orderProcessResults = orderSvcReturn.getResultArray();
                if (orderProcessResults!=null) {
                    for (int j = 0; j < orderProcessResults.length; j++) {
                        OrderProcessResult orderProcessResult = orderProcessResults[j];
                        System.out.println("    " + orderProcessResult.getMsgCode() + " : " + orderProcessResult.getReason());
                    }
                }
            }
            else {
                System.out.println("Successfully ran calculateValues: " + orderSvcReturn.getRefId());
            }
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

        long[] orderIds = {10092136};

        // create a client side session and disable compression for testing
        ClientSession clientSession = new ClientSession(protocol, hostname, port);
        clientSession.setGzipRequestEnabled(false);
        clientSession.setGzipResponseEnabled(false);

        try {
            // authenticate the session
            clientSession.logon(username, password);

            try {
                // call calculate values
                CalculateValues.calculateValues(clientSession, orderIds);
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
