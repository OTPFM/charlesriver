package com.crd.example;

import com.crd.client.ClientSession;
import com.crd.client.ServiceException;
import com.crd.client.TransportException;
import com.crd.command.Result;
import com.crd.cronjobs.CrdCronExpressionHolder;
import com.crd.reference.IMSUtilities;
import com.crd.util.Parameter;

import java.util.Calendar;
import java.util.Date;

/**
 * This example shows how to login to a Charles River IMS server, make a web service call
 * to launch a batch export command, wait for command results, then logout.
 * <p/>
 * Note than in order for this example to run successfully you need to use CRIMS client user interface
 * to authorize this command for execution.
 */
public class Commands
{
    public static void main(String[] args)
    {
        // define the host server
        final String protocol = "http";
        final String hostname = "localhost";
        final int port = 80;

        // define the authentication info
        final String username = "tm_dev";
        final String password = "tm_dev";

        // create a client side session that will maintain the server side session id
        ClientSession clientSession = new ClientSession(protocol, hostname, port);

        // A ClientSession uses gzip compression by default.
        // Disable gzip compression for easier viewing of intercepted http traffic during testing
        clientSession.setGzipRequestEnabled(false);
        clientSession.setGzipResponseEnabled(false);

        try {
            // create an authenticated session on the server
            clientSession.logon(username, password);
            try {
                // create web service proxies
                IMSUtilities utilSvc = clientSession.getIMSUtilities();

                // At this point, the client side session is authenticated, submit a command
                String command = "com.crd.exporter.processor.BatchEventCommand";
                String baseId = "ID:batchexport";

                Parameter[] params = new Parameter[1];
                params[0] = new Parameter();
                params[0].setName("command");
                params[0].setStringValue("-F extfeed -E 2");

                String corrId = utilSvc.submitCommand(command, params, baseId);
                System.out.println("Command launched, correlation id: " + corrId);

                //wait for command results
                Result[] results = utilSvc.checkForCommandResult(command, corrId);
                int attemptCnt = 1;
                while (results == null && attemptCnt < 10) {
                    try {
                        Thread.sleep(1000); // delay for a second
                    }
                    catch (InterruptedException ignore) {
                    }
                    results = utilSvc.checkForCommandResult(command, corrId);
                    attemptCnt++;
                }
                if (results!=null) {
                    System.out.println("Command results obtained, result array size: " + results.length);
                    for (int i = 0; i < results.length; i++) {
                        System.out.println("Result# " + i
                                + ": id=" + results[i].getId()
                                + ", desc=" + results[i].getDescription()
                                + ", msgCode=" + results[i].getMessageCode());
                    }
                }
                else {
                    System.out.println("Command results could not be obtained after " + attemptCnt + " attempts.");
                }

                //schedule command to start immediately and execute 3 times in 10 second intervals within a next minute
                //expecting one execution to take place before command is unscheduled
                String jobName = "batchexport";
                String jobGroup = "rest";

                Date start = new Date();
                Calendar startCal = Calendar.getInstance();
                startCal.setTime(start);

                Date end = new Date(start.getTime() + 1000 * 60);
                Calendar endCal = Calendar.getInstance();
                endCal.setTime(end);

                int repeatCount = 3;
                long repeatInterval = 1000 * 10; //every 10 seconds

                String scheduledJobName = utilSvc.scheduleCommand(command, params, jobName, jobGroup, startCal, endCal, repeatCount, repeatInterval);

                //give command a chance to execute once than unschedule it
                Thread.sleep(3000);
                utilSvc.unscheduleCommand(command, scheduledJobName, jobGroup);

                //schedule same command via cron entry setup to execute every 2 seconds
                //expecting at least one execution to take place before command is unscheduled
                CrdCronExpressionHolder cronEntry = new CrdCronExpressionHolder();
                cronEntry.setSeconds("0/2");

                scheduledJobName = utilSvc.scheduleCronCommand(command, params, jobName, jobGroup, startCal, endCal, cronEntry);

                //give command a chance to execute at least once than unschedule it
                Thread.sleep(3000);
                utilSvc.unscheduleCommand(command, scheduledJobName, jobGroup);

            }
            finally {
                // logout in a finally block, so that the session's resources can be released
                clientSession.logout();
            }
        }
        catch (ServiceException e) {
            System.out.println("Error message received from server: " + e.getFaultString());
        }
        catch (TransportException e) {
            System.out.println("Error communicating with server: " + e.getLocalizedMessage());
        }
        catch (Exception e) {
            System.out.println("Eror during general processing: " + e.getLocalizedMessage());
        }

    }

}
