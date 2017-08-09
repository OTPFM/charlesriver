package com.crd.example;

import com.crd.beans.OrderBean;
import com.crd.beans.OrderAllocationBean;
import com.crd.beans.lookup.OrderLookupSupport;
import com.crd.beans.lookup.SecurityLookup;
import com.crd.client.ClientSession;
import com.crd.client.ServiceException;
import com.crd.client.TransportException;
import com.crd.ordersvc.core.OrderSvcReturn;
import com.crd.ordersvc.core.OrderProcessResult;


/**
 * This example show how to create a simple FX Order
 */                                                                        
public class Orders
{
    private static void populateOrderBean(OrderBean orderBean)
    {
        SecurityLookup secLkp = new SecurityLookup();
        secLkp.setTicker("MSFT"); // 1101121381

        OrderLookupSupport ordSupt = new OrderLookupSupport();
        ordSupt.setSecId(secLkp);

        orderBean.setLookupSupportBean(ordSupt);

        orderBean.setScenarioId(700500003);
        orderBean.setBasketId(700500003);
        orderBean.setTransType("BUYL");
        orderBean.setFromCrrncy("EUR");
        orderBean.setToCrrncy("USD");
        orderBean.setTrader("TM_DEV");
        orderBean.setExecBroker("BEAR");
        
    }

    private static void populateOrderAllocationBean(OrderAllocationBean orderAllocationBean)
    {
        orderAllocationBean.setAcctCd("tstacc00001");
        orderAllocationBean.setTargetQty(100);
    }

    public static void main(String[] args)
    {
        // define the host server
        final String protocol = "http";
        final String hostname = "localhost";
        final int port = 8082;

        // define the authentication info
        final String username = "tm_dev ";
        final String password = "tm_dev";

        // create a client side session and disable compression for testing
        ClientSession clientSession = new ClientSession(protocol, hostname, port);
        clientSession.setGzipRequestEnabled(false);
        clientSession.setGzipResponseEnabled(false);

        try {
            try {
                // Authenticate the client session on the server
                clientSession.logon(username, password);

                // create and populate an order object
                OrderBean orderBean = new OrderBean(true);
                populateOrderBean(orderBean);

                // create and populate an allocation on the order
                OrderAllocationBean orderAllocationBean = new OrderAllocationBean(true);
                populateOrderAllocationBean(orderAllocationBean);
                orderBean.setAllocationBeans(new OrderAllocationBean[] {orderAllocationBean});

                // send the order to the server
                OrderBean[] orders = new OrderBean[] {orderBean};
                OrderSvcReturn[] orderSvcReturns = clientSession.getTrading().createOrder(orders,  null);

                // process the results
                for (int i = 0; i < orderSvcReturns.length; i++) {
                    OrderSvcReturn orderSvcReturn = orderSvcReturns[i];
                    if (orderSvcReturn.getHasError()) {
                        System.out.println("Error creating order");
                        OrderProcessResult[] orderProcessResults = orderSvcReturn.getResultArray();
                        if (orderProcessResults!=null) {
                            for (int j = 0; j < orderProcessResults.length; j++) {
                                OrderProcessResult orderProcessResult = orderProcessResults[j];
                                System.out.println("    " + orderProcessResult.getMsgCode() + " : " + orderProcessResult.getReason());
                            }
                        }
                    }
                    else {
                        System.out.println("Successfully created order " + orderSvcReturn.getRefId());
                    }
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
