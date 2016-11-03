/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.alfresco.po.share.usecases.search;

import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.search.FacetedSearchPage;
import org.alfresco.po.share.search.SearchSelectedItemsMenu;
import org.alfresco.po.share.site.AddUsersToSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.AbstractDocumentTest;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.steps.SiteActions;
import org.alfresco.po.share.workflow.NewWorkflowPage;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.po.share.workflow.WorkFlowType;
import org.alfresco.test.FailedTestListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify Search Bulk Actions is operating correctly.
 *
 * @author Charu
 * 
 */
@Listeners(FailedTestListener.class)
public class FacetedSearchBulkActionsE2ETest1 extends AbstractDocumentTest 
{
    private static String siteName;
    private static String siteName1;
    private static String folderName1, folderName2;
    private static String folderDescription1;
    private static String folderDescription2;
    private static FacetedSearchPage resultsPage;
    private static DocumentLibraryPage documentLibPage;
    private static StartWorkFlowPage startWorkFlowPage;
    AddUsersToSitePage addUsersToSitePage;
    private File bulkfile1;
    private File bulkfile2;
        
    private String userName1 = "user1" + System.currentTimeMillis();
    private String userName2 = "user2" + System.currentTimeMillis();
    private String userName3 = "user3" + System.currentTimeMillis();
    private String newtaskname1 = "newtask"+ System.currentTimeMillis();
    
    @Autowired SiteActions siteActions;
   
    /**
     * Pre test setup to create site and add users to site
     *
     * @throws Exception
     */
    @BeforeClass(groups = "alfresco-one")
    public void prepare() throws Exception
    {
        siteName = "Asite" + System.currentTimeMillis();
        siteName1 = "Asite1" + System.currentTimeMillis();
        folderDescription1 = String.format("Description of %s", folderName1);
        folderDescription2 = String.format("Description of %s", folderName2);
        createUser();
        
        siteUtil.createSite(driver, userName1, UNAME_PASSWORD, siteName, "description", "Public");
        SiteDashboardPage siteDashBoard = resolvePage(driver).render();        
        AddUsersToSitePage addUsersToSitePage = siteDashBoard.getSiteNav().selectAddUser().render();
        siteUtil.addUsersToSite(driver, addUsersToSitePage, userName2, UserRole.CONSUMER);        
        siteUtil.addUsersToSite(driver, addUsersToSitePage, userName3, UserRole.COLLABORATOR);
        siteUtil.createSite(driver, userName1, UNAME_PASSWORD, siteName1, "description", "Public");
              
    }

    /**
     * Create User
     *
     * @throws Exception
     */
    private void createUser() throws Exception
    {
        createEnterpriseUser(userName1);
        createEnterpriseUser(userName2);     
        createEnterpriseUser(userName3);
        loginAs(userName1, UNAME_PASSWORD);        
       
     }

    @AfterClass(groups = "alfresco-one")
    public void teardown()
    {
        siteUtil.deleteSite(username, password, siteName);
    } 
        
    /**
     * Delete and move option not displayed for Collaborator
     * when own and other user files/folders are selected
     * 
     */
   
    @Test(groups = "alfresco-one", enabled = false)
    public void BulkDeleteAndMoveTest() throws Exception
    {    	   	

    	folderName1 = "myfile11folder9"+ System.currentTimeMillis();
        folderName2 = "myfile12folder10"+ System.currentTimeMillis();
        folderDescription1 = String.format("Description of %s", folderName1);
        folderDescription2 = String.format("Description of %s", folderName2);
        bulkfile1 = siteUtil.prepareFile();
        bulkfile2 = siteUtil.prepareFile();
    	
    	//Login as user1
        loginAs(userName1, UNAME_PASSWORD);
        
        //Open user1 Site document library
    	documentLibPage = siteActions.openSitesDocumentLibrary(driver, siteName);
                
    	//Create Folder
        siteActions.createFolder(driver, folderName1, folderName1, folderDescription1);
    	
    	//Upload File
        siteActions.uploadFile(driver, bulkfile1);
        
        //Logout as user1
        logout(driver);
        
        //Login as collaborator(user3) to siteName
        loginAs(userName3, UNAME_PASSWORD);
        
        //Open site document library (siteName)
        documentLibPage = siteActions.openSitesDocumentLibrary(driver, siteName);
        
        //Create Folder
        siteActions.createFolder(driver, folderName2, folderName2, folderDescription2);
    	
    	//Upload File
        siteActions.uploadFile(driver, bulkfile2);        
               
        //Try retry search until results are displayed
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "myfile",bulkfile2.getName(), true, 3));
        
        resultsPage = siteActions.search(driver, "myfile").render();
        Assert.assertTrue(resultsPage.hasResults(),bulkfile1.getName());        
          	
    	//select the items created by user1 and user3
        resultsPage.getResultByName(bulkfile1.getName()).selectItemCheckBox();
        resultsPage.getResultByName(bulkfile2.getName()).selectItemCheckBox();
        resultsPage.getResultByName(folderName1).selectItemCheckBox();
        resultsPage.getResultByName(folderName2).selectItemCheckBox();
            	
        //verify actions Delete and Move are not displayed in the selected items drop down list
        Assert.assertFalse(resultsPage.getNavigation().isSelectedItemsOptionDisplayed(SearchSelectedItemsMenu.DELETE));
        Assert.assertFalse(resultsPage.getNavigation().isSelectedItemsOptionDisplayed(SearchSelectedItemsMenu.MOVE_TO));
                 
        logout(driver);        
        
    }    
   
    /**
     * Collaborator can delete his own file/folders in bulk    
     */
    
    @Test(groups = "alfresco-one", enabled = false)
    public void BulkDeleteOwnFileFoldersTest() throws Exception
    {    	   	
    	folderName1 = "myfile11folder11"+ System.currentTimeMillis();     
        folderDescription1 = String.format("Description of %s", folderName1);
   
        bulkfile1 = siteUtil.prepareFile();   	     
         
        //Login as collaborator(user3)
        loginAs(userName3, UNAME_PASSWORD);
        
        //Open user1 site document library (siteName)
        documentLibPage = siteActions.openSitesDocumentLibrary(driver, siteName);
        
        //Create Folder
        siteActions.createFolder(driver, folderName1, folderName1, folderDescription1);
    	
    	//Upload File
        siteActions.uploadFile(driver, bulkfile1);       
                 
        //Try retry search until results are displayed
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "myfile",bulkfile1.getName(), true, 3));
        resultsPage = siteActions.search(driver, "myfile").render();
        Assert.assertTrue(resultsPage.hasResults(),bulkfile1.getName());
        Assert.assertTrue(resultsPage.hasResults(),folderName1);        	
    	        
        String[] selectedItems = {bulkfile1.getName(), folderName1};
        String destination = "Shared";
        
        //Select the check box on files and folders and select 'Delete' from Selected Items drop down 
        siteActions.performBulkActionOnSelectedResults(driver,selectedItems, SearchSelectedItemsMenu.DELETE, destination);
               
        //open siteName document library where user3 is a collaborator
        documentLibPage = openSiteDocumentLibraryFromSearch(driver, siteName);
       
        //Verify all files and folders are deleted from siteName
        Assert.assertFalse(documentLibPage.isItemVisble(bulkfile1.getName()), "File not displayed");  
        Assert.assertFalse(documentLibPage.isItemVisble(folderName1), "File not displayed");
        
        logout(driver);    
                
    }  
        
    /**
     * Collaborator can download own and other user files and folders in bulk
     **/
    
    @Test(groups = "alfresco-one", enabled = false)
    public void CollaboratorBulkDownloadTest() throws Exception
    {    	   	
    	folderName1 = "myfile11folder13"+ System.currentTimeMillis();
        folderName2 = "myfile12folder14"+ System.currentTimeMillis(); 
        folderDescription1 = String.format("Description of %s", folderName1);
        folderDescription2 = String.format("Description of %s", folderName2);
        bulkfile1 = siteUtil.prepareFile();
        bulkfile2 = siteUtil.prepareFile();
        
    	//Login as user1 (owner to siteName)
        loginAs(userName1, UNAME_PASSWORD);    	
    	
        //open site document library
        documentLibPage = siteActions.openSitesDocumentLibrary(driver, siteName);
        
        //Create Folder
        siteActions.createFolder(driver, folderName1, folderName1, folderDescription1);
    	
    	//Upload File
        siteActions.uploadFile(driver, bulkfile1);
                        
        //Logout as user1
        logout(driver);
        
        //Login as user3 (collaborator to siteName)
        loginAs(userName3, UNAME_PASSWORD);        
        
        //open siteName document library
        documentLibPage = siteActions.openSitesDocumentLibrary(driver, siteName);
        
        //Create Folder
        siteActions.createFolder(driver, folderName2, folderName2, folderDescription2);
    	
    	//Upload File
        siteActions.uploadFile(driver, bulkfile2);
        
        //Try retry search until results are displayed
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "myfile",bulkfile2.getName(), true, 3));
        resultsPage = siteActions.search(driver, "myfile").render();
     
        String[] selectedItems = {bulkfile1.getName(), folderName1};
        String destination = "Shared";
        
        //Select the check box on files and folders and select 'DOWNLOAD_AS_ZIP' from Selected Items drop down 
        siteActions.performBulkActionOnSelectedResults(driver,selectedItems, SearchSelectedItemsMenu.DOWNLOAD_AS_ZIP, destination);
        Assert.assertTrue(resultsPage.hasResults(),bulkfile1.getName());
        Assert.assertTrue(resultsPage.hasResults(),bulkfile2.getName());
        Assert.assertTrue(resultsPage.hasResults(),folderName1);
        Assert.assertTrue(resultsPage.hasResults(),folderName2);
                      
        logout(driver);            
        
        
    }     
   
    /**
     * Collaborator can create a work flow on his own and other user files in bulk
     **/
    @Test(groups = "alfresco-one", enabled = false)
    public void bulkStartWorkFlowTest() throws Exception
    {    	   	

    	bulkfile1 = siteUtil.prepareFile();
        bulkfile2 = siteUtil.prepareFile();
        
        //Login as user1 (owner to siteName)
        loginAs(userName1, UNAME_PASSWORD);    

        //open siteName document library     
        documentLibPage = siteActions.openSitesDocumentLibrary(driver, siteName);        
       
     	//Upload File 
        siteActions.uploadFile(driver, bulkfile1);     
        
        //Logout as user1
        logout(driver);
        
        //Login as user3 (collaborator to siteName)
        loginAs(userName3, UNAME_PASSWORD);        
        
        //open siteName document library
        documentLibPage = siteActions.openSitesDocumentLibrary(driver, siteName);        
        
        //Upload File
        siteActions.uploadFile(driver, bulkfile2);    
            
        //Try retry search until results are displayed
        Assert.assertTrue(siteActions.checkSearchResultsWithRetry(driver, "myfile",bulkfile2.getName(), true, 3));
        resultsPage = siteActions.search(driver, "myfile").render();
        Assert.assertTrue(resultsPage.hasResults(),bulkfile1.getName());
        Assert.assertTrue(resultsPage.hasResults(),bulkfile2.getName());  
        	
        String[] selectedItems = {bulkfile1.getName(), bulkfile2.getName()};
        String destination = "Shared";
        
        //Select all the items check box and click on Start work flow action from selected items drop down menu
        startWorkFlowPage = siteActions.performBulkActionOnSelectedResults(driver,selectedItems, SearchSelectedItemsMenu.START_WORKFLOW, destination).render();
        Assert.assertTrue(startWorkFlowPage.isWorkFlowTextPresent());
        
        //Create a new workflow
        NewWorkflowPage newWorkflowPage = ((NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW)).render();
        List<String> reviewers = new ArrayList<String>();
        reviewers.add(username);
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails(siteName, newtaskname1, reviewers);
        newWorkflowPage.startWorkflow(formDetails).render();
        
        //Open siteName document library
        openSiteDocumentLibraryFromSearch(driver, siteName);
        
        //Verify file1 is part of workflow
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(bulkfile1.getName());
        assertTrue(thisRow.isPartOfWorkflow(), "Document should not be part of workflow.");
        
        //Verify File2 is part of workflow
        FileDirectoryInfo thisRow1 = documentLibPage.getFileDirectoryInfo(bulkfile2.getName());
        assertTrue(thisRow1.isPartOfWorkflow(), "Document should not be part of workflow.");
                            
        logout(driver);   
                
    } 
        

}