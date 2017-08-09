package com.crd.example;

import com.crd.beans.FundBean;
import com.crd.beans.ModelTreeNodeBean;
import com.crd.client.ClientSession;
import com.crd.client.ServiceException;
import com.crd.client.TransportException;
import com.crd.modeling.services.modeling.AssetAllocationModel;
import com.crd.modeling.services.modeling.ModelValidationError;
import com.crd.modeling.services.modeling.ModelingErrorCode;

/**
 * This example shows how to manipulate Asset Allocation Models.
 */
public class ManageAssetAllocationModel
{
    public static void main(String[] args)
    {
        // define the host server
        final String protocol = "http";
        final String hostname = "localhost";
        final int port = 8082;

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
                final String AAM_MODEL_CODE = "DEMO_AAM";

                // Test that the model with AAM_MODEL_CODE model code does not exist
                // We expect a empty array back meaning there are no Asset Allocation Models with such model code
                AssetAllocationModel[] aams = clientSession.getModeling().fetchAssetAllocationModels(new String[]{AAM_MODEL_CODE});
                if (aams.length == 0) {
                    // Create an empty new Asset Allocation Model with AAM_MODEL_CODE model code
                    AssetAllocationModel assetAllocationModel = new AssetAllocationModel();
                    assetAllocationModel.setModel(new FundBean(true));
                    assetAllocationModel.setIsNew(true);
                    assetAllocationModel.getModel().setAcctId(-1L);
                    assetAllocationModel.getModel().setCrrncyCd("USD");
                    assetAllocationModel.getModel().setAcctName("A demo Asset Allocation Model");
                    assetAllocationModel.getModel().setAcctTypCd("A");
                    assetAllocationModel.getModel().setMdlTypInd("A");
                    assetAllocationModel.getModel().setAcctCd(AAM_MODEL_CODE);
                    assetAllocationModel.getModel().setManager("tm_dev");
                    assetAllocationModel.getModel().setAcctShtName(AAM_MODEL_CODE);

                    // The validation is expected to fail since the model does not have any categories
                    ModelValidationError[] validationResults = clientSession.getModeling().validateAssetAllocationModels(new AssetAllocationModel[] {assetAllocationModel});
                    if (validationResults.length == 1 && validationResults[0].getModelError() == ModelingErrorCode.NULL_TREE_NODE_BEANS_PRESENT) {
                        ModelTreeNodeBean rootNode = new ModelTreeNodeBean(true);
                        rootNode.setAcctCd(assetAllocationModel.getModel().getAcctCd());
                        rootNode.setDisplayName(assetAllocationModel.getModel().getAcctName());
                        rootNode.setObjType("M");
                        rootNode.setEntityType("A");
                        rootNode.setRefEntityRootTreeId(-1L);
                        rootNode.setRootTreeId(0);
                        rootNode.setMdlTreeId(0);
                        rootNode.setTreeLevel(0);
                        rootNode.setWeightDir("U");
                        rootNode.setStatus("A");
                        rootNode.setWeightSrc("C");
                        rootNode.setFreeze("N");

                        ModelTreeNodeBean classificationScheme = new ModelTreeNodeBean(true);
                        classificationScheme.setDisplayName("Investment Class");
                        classificationScheme.setRootTreeId(rootNode.getRootTreeId());
                        classificationScheme.setParentTreeId(rootNode.getMdlTreeId());
                        classificationScheme.setMdlTreeId(rootNode.getMdlTreeId() + 1);
                        classificationScheme.setTreeLevel(rootNode.getTreeLevel() + 1);
                        classificationScheme.setObjType("S");
                        classificationScheme.setRootTreeId(rootNode.getRootTreeId());
                        classificationScheme.setWeightSrc("C");
                        classificationScheme.setFreeze("N");
                        classificationScheme.setClsfScheme("Investment Class");//has to be one of 'SELECT CLASSIFICATION_SCHEME FROM TS_MDL_CLSF_SCHEME'
                        classificationScheme.setRefTable("csm_investment_class");//maps to the value of 'TABLE_1' from above table 'TS_MDL_CLSF_SCHEME'
                        classificationScheme.setKey_1("inv_class_cd");//maps to the column of the same name for the table identified above (in this case 'INV_CLASS_CD' on the 'CSM_INVESTMENT_CLASS' table)
                        

                        ModelTreeNodeBean firstCategory = new ModelTreeNodeBean(true);
                        firstCategory.setDisplayName("Equity");
                        firstCategory.setRootTreeId(classificationScheme.getRootTreeId());
                        firstCategory.setParentTreeId(classificationScheme.getMdlTreeId());
                        firstCategory.setMdlTreeId(classificationScheme.getMdlTreeId() + 1);
                        firstCategory.setObjType("C");
                        firstCategory.setRefTable("csm_investment_class");//maps to the value of 'TABLE_1' from above table 'TS_MDL_CLSF_SCHEME'
                        firstCategory.setKey_1("EQTY");//in this case for Investment Class scheme has to be one of the values from 'select INV_CLASS_CD from CSM_INVESTMENT_CLASS'
                        firstCategory.setTreeLevel(classificationScheme.getTreeLevel()+1);
                        firstCategory.setWeightSrc("U");
                        firstCategory.setFreeze("N");

                        ModelTreeNodeBean secondCategory = new ModelTreeNodeBean(true);
                        secondCategory.setDisplayName("Other");
                        secondCategory.setRootTreeId(classificationScheme.getRootTreeId());
                        secondCategory.setParentTreeId(classificationScheme.getMdlTreeId());
                        secondCategory.setMdlTreeId(classificationScheme.getMdlTreeId() + 2);
                        secondCategory.setObjType("C");
                        secondCategory.setRefTable("csm_investment_class");//maps to the value of 'TABLE_1' from above table 'TS_MDL_CLSF_SCHEME'
                        secondCategory.setKey_1("OTHER");//in this case for Investment Class scheme has to be one of the values from 'select INV_CLASS_CD from CSM_INVESTMENT_CLASS'
                        secondCategory.setTreeLevel(classificationScheme.getTreeLevel()+1);
                        secondCategory.setWeightSrc("U");
                        secondCategory.setFreeze("N");

                        assetAllocationModel.setNodes(new ModelTreeNodeBean[] {rootNode, classificationScheme, firstCategory, secondCategory});
                        // Insert the new AAM to the database
                        validationResults = clientSession.getModeling().insertAssetAllocationModels(new AssetAllocationModel[] {assetAllocationModel});
                        if (validationResults.length != 0) {
                            System.out.println(String.format("Error: Could not save a new Asset Allocation Model '%1$s'.", AAM_MODEL_CODE));
                        } else {
                            // Fetch saved model
                            aams = clientSession.getModeling().fetchAssetAllocationModels(new String[] { AAM_MODEL_CODE });
                            if (aams.length == 1) {
                                assetAllocationModel = aams[0];                                
                                //Fix the model weights for the categories - Equity and Other                              
                                ModelTreeNodeBean equityCategoryNode = null;
                                ModelTreeNodeBean otherCategoryNode = null;
                                
                                for (ModelTreeNodeBean treeNodeBean : assetAllocationModel.getNodes()) {
                                	if (treeNodeBean.getDisplayName().equals("Equity")) {
                                		equityCategoryNode = treeNodeBean;
                                	}
                                	else if (treeNodeBean.getDisplayName().equals("Other")) {
                                		otherCategoryNode = treeNodeBean;
                                	}
                                }
                                
                                //set the weights for Equity and Other Category as below (total them up to 100.0)
                                equityCategoryNode.setWeight(70.0);
                                otherCategoryNode.setWeight(30.0);
                              
                                // Save the new AAM weight to the database
                                validationResults =
                                        clientSession.getModeling().updateAssetAllocationModels(new AssetAllocationModel[] {assetAllocationModel}, "Weights updated");
                                if (validationResults.length != 0) {
                                    System.out.println(String.format("Error: Could not save a new Asset Allocation Model '%1$s'.", AAM_MODEL_CODE));
                                } else {
                                    // Retrieve updated Asset Allocation Model
                                    aams = clientSession.getModeling().fetchAssetAllocationModels(new String[]{AAM_MODEL_CODE});
                                    if (aams.length != 0) {
                                        validationResults =
                                                clientSession.getModeling().deleteAssetAllocationModels(new String[]{AAM_MODEL_CODE});
                                        if (validationResults.length != 0) {
                                            System.out.println(String.format("Error: Could not delete an Asset Allocation Model '%1$s'.", AAM_MODEL_CODE));
                                        }
                                    } else {
                                        System.out.println(String.format("Error: Could not retrieve a model '%1$s' that should have existed.", AAM_MODEL_CODE));
                                    }
                                }
                            }
                        }
                    } else {
                        System.out.println("Error: Validation error expected.");
                    }
                } else {
                    System.out.println(String.format("Error: Could not create a new Asset Allocation Model with code  '%1$s'.", AAM_MODEL_CODE));
                }
            } catch (Exception e) {
                System.out.println("Error message received from server: " + e.getLocalizedMessage());
            } finally {
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
