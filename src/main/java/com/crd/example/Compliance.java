package com.crd.example;

import com.crd.beans.ViolationBean;
import com.crd.beans.TestBean;
import com.crd.client.ClientSession;
import com.crd.client.ServiceException;
import com.crd.client.TransportException;
import com.crd.compliancesvc.processor.CPLResult;
import com.crd.compliancesvc.processor.CPLResultWrapper;
import com.crd.compliancesvc.processor.CPLSelectiveRuleFilter;
import com.crd.util.MessageDetailImpl;
import java.util.UUID;

/**
 * This example shows how to run compliance through web services.
 */
public class Compliance
{
    /**
     * 
     * A static method to run a various types of compliance web services.
     * 
     * 
     * @param clientSession
     * @throws ServiceException
     * @throws TransportException
     */
    public static void compliance(ClientSession clientSession) throws ServiceException, TransportException
    {
       //retrieve compliance service object.
       com.crd.compliancesvc.Compliance cplSvc = clientSession.getCompliance();
       
       //set a custom context id in the request
       cplSvc.setReferenceRequestIdHeader(UUID.randomUUID().toString());

       /* 
          1. Make sure the account "myAccount" exists before the following web service is executed.
          2. Make sure the account "myAccount" has some positions.
          3. Make sure there are some compliance tests associated with the account "myAccount".
          4. Run a batch compliance on the account "myAccount". 
        */
       System.out.print("RUNNING BATCH COMPLIANCE WITH MYACCOUNT: ");
       CPLResult result = cplSvc.runCompliance("Compliance123456", "batch", "account", "myAccount", true);
       printResult(result);
       
       /* 
          1. Make sure the account "myAccount" exists before the following web service is executed.
          2. Make sure the account "myAccount" has some positions.
          3. Make sure there are some compliance tests which are associated with the account "myAccount" and 
             have the values "CNTRY" or "DUR" in the category 1. 
          4. Run a selective rule batch compliance on the account "myAccount". 
        */
       CPLSelectiveRuleFilter category1Filter = new CPLSelectiveRuleFilter();    
       category1Filter.setName("CATEGORY_1");
       category1Filter.setValues(new String[] {"CNTRY", "DUR"});
       System.out.print("RUNNING BATCH COMPLIANCE WITH MYACCOUNT AND SELECTIVE RULE <CATEGORY_1 : CNTRY,DUR> : ");
       CPLResultWrapper wrapper = cplSvc.runComplianceSelectiveRules("Compliance123456", "batch", "account", "myAccount", true, new CPLSelectiveRuleFilter[] {category1Filter});
       printResult(wrapper);
       
    
       /*
          1. Make sure the account "myAccount" exists before the following web service is executed.
          2. Make sure the account "myAccount" has some positions.
          3. Make sure there are some compliance tests which are associated with the account "myAccount" and 
             have the values "CNTRY" or "DUR" in the category 1 and the values "MA" or "OH" in category 2 and 
             the value "SIS" in regulation.
          4. Make sure there are some manager orders using account "myAccount" and scenario ID 1414594196.
          5. Run a Pretrade Compliance with scenario ID 1414594196 and the selective Rules for category 1, category 2 and regulation. 
        */ 
       CPLSelectiveRuleFilter category2Filter = new CPLSelectiveRuleFilter();  
       category2Filter.setName("CATEGORY_2");
       category2Filter.setValues(new String[] {"MA", "OH"});
       CPLSelectiveRuleFilter regulationFilter = new CPLSelectiveRuleFilter(); 
       regulationFilter.setName("REGL_CD");
       regulationFilter.setValues(new String[] {"SIS"});
       System.out.print("RUNNING PRETRADE COMPLIANCE WITH SCENARIO ID 1414594196 AND SELECTIVE RULES <CATEGORY_1 : CNTRY,DUR>, <CATEGORY_2 : MA,OH> and <REGL_CD : SIS> : ");
       wrapper = cplSvc.runComplianceSelectiveRules("Compliance123456", "pretrade", null, "1414594196", true, new CPLSelectiveRuleFilter[] {category1Filter, category2Filter, regulationFilter});
       printResult(wrapper);
       
       
       /*
          1. Make sure the account "myAccount" exists before the following web service is executed.
          2. Make sure the account "myAccount" has some positions.
          3. Make sure there are some compliance tests with the following preconditions
                a. The tests are associated with the account "myAccount" 
                b. In order to generate placement violations, the test types have to be excluding tests.
                c. The tests have the values "USA" or "AUSTRALIA" in the region and the value "RDCLASS" in the test class.
                d. The tests set "PLACEMENT_MODE_IND" to 'Y' in cs_test_v2 table.
          4. Make sure there are some trade orders using account "myAccount" and place IDs 1414853762 and 1414622122 
          5. Run a Placement Compliance with placement ids 1414853762 and 1414622122 and the selective rules for region and test class.
        */ 
       CPLSelectiveRuleFilter regionFilter = new CPLSelectiveRuleFilter();    
       regionFilter.setName("REGL_RGN");
       regionFilter.setValues(new String[]{"USA", "AUSTRALIA"});
       CPLSelectiveRuleFilter testClassFilter = new CPLSelectiveRuleFilter();    
       testClassFilter.setName("TEST_CLASS");
       testClassFilter.setValues(new String[]{"CRDCLASS"});
       System.out.print("RUNNING PLACEMENT COMPLIANCE WITH PLACE ID 1414853762 and 1414622122 AND SELECTIVE RULES <REGL_RGN : USA, AUSTRALIA> and <TEST_CLASS : CRDCLASS> : ");
       long[] placeIds = new long[] {1414853762, 1414622122};
       wrapper = cplSvc.runPlacementComplianceSelectiveRules("Compliance123456", true, placeIds, new CPLSelectiveRuleFilter[] {regionFilter});
       printResult(wrapper);
       
    }

    /**
     * Create and return a StringBuffer object displaying the compliance result in the given CPLResult object.
     */
    private static StringBuffer prepareResult(CPLResult result) {
        StringBuffer sb = new StringBuffer();
        sb.append("Compliance run ");
        sb.append(result.isSuccess() ? "successful." : "failed.");
        sb.append(" Alerts Generated: ").append(result.getAlertCount()).append(" ");
        sb.append(" Warnings Generated: ").append(result.getWarningCount()).append(" ");
        sb.append(" Data Exceptions Generated: ").append(result.getDataExceptionCount()).append(" ");
        return sb;
    }
    
    /**
     * Print out CPLResult to System.out.
     * 
     * @param result
     */
    private static void printResult(CPLResult result) 
    {
        System.out.println(prepareResult(result).toString());        
    }
    
    /**
     * Print out CPLResultWrapper to System.out.
     * 
     * @param wrapper
     */
    static private void printResult(CPLResultWrapper wrapper)
    {        
    	StringBuffer sb = prepareResult(wrapper.getResult());      
        ViolationBean[] violations = wrapper.getViolations();
        sb.append("  Number of Violations : ").append(violations.length).append("  ");        
        TestBean[] tests = wrapper.getTests();
        sb.append("Number of Tests : ").append(tests.length);        
        System.out.println(sb.toString());
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
                // run the various compliance tests.
                Compliance.compliance(clientSession);
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
