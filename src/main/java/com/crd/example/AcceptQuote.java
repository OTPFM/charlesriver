package com.crd.example;

import com.crd.client.ClientSession;
import com.crd.client.ServiceException;
import com.crd.client.TransportException;
import com.crd.ordersvc.core.OrderProcessResult;
import com.crd.ordersvc.core.OrderSvcReturn;

/**
 * This example shows how to call acceptQuote on the Trading service.
 */
public class AcceptQuote
{
    public static void acceptQuote(ClientSession clientSession, long orderId, long placeId) throws ServiceException, TransportException
    {
        OrderSvcReturn[] orderSvcReturns = clientSession.getTrading().acceptQuote(orderId, placeId);

        // process the results
        for (int i = 0; i < orderSvcReturns.length; i++) {
            OrderSvcReturn orderSvcReturn = orderSvcReturns[i];
            if (orderSvcReturn.getHasError()) {
                System.out.println("Error calling acceptQuote");
                OrderProcessResult[] orderProcessResults = orderSvcReturn.getResultArray();
                if (orderProcessResults!=null) {
                    for (int j = 0; j < orderProcessResults.length; j++) {
                        OrderProcessResult orderProcessResult = orderProcessResults[j];
                        System.out.println("    " + orderProcessResult.getMsgCode() + " : " + orderProcessResult.getReason());
                    }
                }
            }
            else {
                System.out.println("Successfully ran acceptQuote: " + orderSvcReturn.getRefId());
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

        long orderId = 10092136;
        long placeId = 10092216;

        // create a client side session and disable compression for testing
        ClientSession clientSession = new ClientSession(protocol, hostname, port);
        clientSession.setGzipRequestEnabled(false);
        clientSession.setGzipResponseEnabled(false);

        try {
            // authenticate the session
            clientSession.logon(username, password);

            try {
                // call accept quote
                AcceptQuote.acceptQuote(clientSession, orderId, placeId);
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
