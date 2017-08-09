package com.crd.example;

import java.util.ArrayList;
import java.util.List;
import com.crd.client.ClientSession;
import com.crd.client.ServiceException;
import com.crd.client.TransportException;
import com.crd.systemadmin.SystemAdmin;
import com.crd.systemadmin.SystemAdminImpl;
import com.crd.systemadmin.GroupData;
import com.crd.systemadmin.UserData;
import com.crd.systemadmin.AuthorizationData;

import java.io.IOException;

/**
 * This example shows how to create user, group and add authorizations.
 *
 */
public class SystemAdminExample
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

        // create a client side session and disable compression for testing
        ClientSession clientSession = new ClientSession(protocol, hostname, port);
        clientSession.setGzipRequestEnabled(false);
        clientSession.setGzipResponseEnabled(false);

        GroupData[] templateGroupMap = new GroupData[3];
        GroupData gd1 = initGroupData("UT_DATA_ADMIN", "Unit Test Data Admin Group", "", "CRD_DATAADMIN");
        GroupData gd2 = initGroupData("UT_EQ_MANAGER", "Unit Test Equity Manager Group", "", "CRD_EQ_MANAGER");
        GroupData gd3 = initGroupData("UT_EQ_TRADER", "Unit Test Equity Trader Group", "", "CRD_EQ_TRADER");
        templateGroupMap[0] = gd1;
        templateGroupMap[1] = gd2;
        templateGroupMap[2] = gd3;

        try {
            // authenticate the session
            clientSession.logon(username, password);

            try {
                // fetch the funds
                SystemAdmin sa = new SystemAdminImpl(clientSession);
                createUsersAndGroups(sa, templateGroupMap);
            }
            catch (Exception e)
            {
                System.out.println("Error creating users and groups: " + e.getLocalizedMessage());
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

    private static void createUsersAndGroups(SystemAdmin sai, GroupData[] templateGroupMap) throws Exception
    {
        // Group creation
        GroupData[] grpData = new GroupData[3];
        GroupData data1 = initGroupData("UTGRP201", "Test Group 201", "Test Group Description 201", null);
        GroupData data2 = initGroupData("UTGRP202", "Test Group 202", "Test Group Description 202", "CRD_FI_MANAGER");
        GroupData data3 = initGroupData("UTGRP203", "Test Group 203", "Test Group Description 203", "CRD_FI_TRADER");
        grpData[0] = data1;
        grpData[1] = data2;
        grpData[2] = data3;
        sai.createGroups(grpData, templateGroupMap);
        System.out.println("Created groups: UTGRP201, UTGRP202, UTGRP203");
    
        // Add authorization
        List<AuthorizationData> authData = prepareAuthorizationData();
        sai.addAuthorizations("G", "UTGRP201", authData.toArray(new AuthorizationData[authData.size()]));
        System.out.println("Added authorization to group: UTGRP201");

        // User creation
        UserData[] usrData = new UserData[3];
        UserData data4 = initUserData("BUTUSER201", "Test User 201", "BUT201",
                                   "utuser201@abc.com", "UTUSER2011", null);
        data4.setParentGroups(new String[] {"UTGRP201"});
        UserData data5 = initUserData("BUTUSER202", "Test User 202", "BUT202",
                                   "utuser202@abc.com", "UTUSER2021", null);
        data5.setParentGroups(new String[] {"UTGRP202"});
        UserData data6 = initUserData("BUTUSER203", "Test User 203", "BUT203",
                                   "utuser203@abc.com", "UTUSER2031", "CRD_FIDESK");
        usrData[0] = data4;
        usrData[1] = data5;
        usrData[2] = data6;
        String[] usrList = new String[3];
        usrList[0] = "BUTUSER201"; 
        usrList[1] = "BUTUSER202";
        usrList[2] = "BUTUSER203";
        sai.createUsers(usrData, templateGroupMap);
        sai.setUsersActive(usrList, true);
        System.out.println("Created users: BUTUSER201, BUTUSER202, BUTUSER203");

        // Add all users to a group
	sai.addUsersToGroup(usrList, "UTGRP203");
        System.out.println("Added users: BUTUSER201, BUTUSER202, BUTUSER203 to Group UTGRP203");
        
        // Delete all users from a group
        sai.deleteUsersFromGroup(usrList, "UTGRP203");
        System.out.println("Deleted users: BUTUSER201, BUTUSER202, BUTUSER203 from Group UTGRP203");
        
        // Copy authorizations. It would remove existing authorizations
        sai.copyAuthorizations("G", "UTGRP201", "G", "UTGRP202", templateGroupMap);
        System.out.println("Copy authorizations from Group UTGRP201 to Group UTGRP202");
    }

    private static List<AuthorizationData> prepareAuthorizationData() throws Exception
    {
        AuthorizationData authData1 = initAuthorization(AuthorizationIdentifiers.QKSEC_INP, null, "Y");
        AuthorizationData authData2 = initAuthorization(AuthorizationIdentifiers.PRETRD_CPL, null, "Y");
        AuthorizationData authData3 = initAuthorization(AuthorizationIdentifiers.ACCT_VIEW, "CRD_EQ_MANAGER", "Y");
        AuthorizationData authData4 = initAuthorization(AuthorizationIdentifiers.ACCT_TRADE, "CRD_EQ_MANAGER", "Y");
        AuthorizationData authData5 = initAuthorization(AuthorizationIdentifiers.MWB_PROF_VIEW, "CRD_EQ_MANAGER", "Y");
        AuthorizationData authData6 = initAuthorization(AuthorizationIdentifiers.ORD_VIEW, "CRD_EQ_MANAGER", "Y");
        AuthorizationData authData7 = initAuthorization(AuthorizationIdentifiers.ORD_OWNRASGN, "[owner]", "Y");
        AuthorizationData authData8 = initAuthorization(AuthorizationIdentifiers.ORD_VIEW_ASSOC, null, "Y");
        AuthorizationData authData9 = initAuthorization(AuthorizationIdentifiers.MGR_ORD_EDIT, null, "Y");
        AuthorizationData authData10 = initAuthorization(AuthorizationIdentifiers.MGR_ORD_SND_TRDG, "[owner]", "Y");
        AuthorizationData authData11 = initAuthorization(AuthorizationIdentifiers.TDR_ORD_EDIT, "CRD_EQ_TRADER", "Y");
        AuthorizationData authData12 = initAuthorization(AuthorizationIdentifiers.TDR_ORD_SND_ACCTG, "CRD_EQ_TRADER", "Y");
        AuthorizationData authData13 = initAuthorization(AuthorizationIdentifiers.UNASGN_ORD_VIEW, null, "Y");
        AuthorizationData authData14 = initAuthorization(AuthorizationIdentifiers.CPL_OVRD_NA, null, "Y");
        AuthorizationData authData15 = initAuthorization(AuthorizationIdentifiers.COMMAND, "EXP_IMPORT", "Y");
        AuthorizationData authData16 = initAuthorization(AuthorizationIdentifiers.INVCL_ALL_INIT_TRADE, null, "Y");
        AuthorizationData authData17 = initAuthorization(AuthorizationIdentifiers.INV_CLASS_EXEC_TRADE, "DEBT", "Y");
        
        List<AuthorizationData> authData = new ArrayList<AuthorizationData>();
        authData.add(authData1);
        authData.add(authData2);
        authData.add(authData3);
        authData.add(authData4);
        authData.add(authData5);
        authData.add(authData6);
        authData.add(authData7);
        authData.add(authData8);
        authData.add(authData9);
        authData.add(authData10);
        authData.add(authData11);
        authData.add(authData12);
        authData.add(authData13);
        authData.add(authData14);
        authData.add(authData15);
        authData.add(authData16);
        authData.add(authData17);

        return authData;
    }

    private static UserData initUserData(String userCd, String userName, String userInit, String userEmail, String userPassword, String templateUser)
    {
        UserData userData = new UserData();
        userData.setUserCd(userCd);
        userData.setUserName(userName);
        userData.setUserInit(userInit);
        userData.setUserEmail(userEmail);
        userData.setUserPassword(userPassword);
        userData.setTemplateUser(templateUser);
        return userData;
    }

    private static GroupData initGroupData(String groupCd, String groupName, String groupDesc, String templateGroup)
    {
        GroupData groupData = new GroupData();
        groupData.setGroupCd(groupCd);
        groupData.setGroupName(groupName);
        groupData.setGroupDesc(groupDesc);
        groupData.setTemplateGroup(templateGroup);
        return groupData;
    }

    private static AuthorizationData initAuthorization(String privilegeId, String targetName, String privilegeValue)
    {
        AuthorizationData authData = new AuthorizationData();
        authData.setPrivilegeId(privilegeId);
        authData.setTargetName(targetName);
        authData.setPrivilegeValue(privilegeValue);
        return authData;
    } 
}
