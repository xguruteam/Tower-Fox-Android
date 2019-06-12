package com.foxridge.towerfox.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface AppDatabaseDao {
    @Query("SELECT p.*, (select count(*) from Photos i1 where i1.projectid=p.projectid) Total, (select count(*) from Photos i1 where status" +
            " in (1, 7) and i1.projectid=p.projectid) requiredCount, " +
            "(select count(*) from Photos i1 where status in (2, 3) and i1.projectid=p.projectid) takenCount, " +
            "(select count(*) from Photos i1 where status = 4 and i1.projectid=p.projectid) ApprovedCount, (select count(*) from Photos i1 " +
            "where status = 0 and i1.projectid=p.projectid) rejectedCount, (select count(*) from Photos i1 where status = 5 " +
            "and i1.projectid=p.projectid) OutOfScopeCount, ROUND(CAST((select count(*) from Photos i1 where status in (1, 7 ) " +
            "and i1.projectid=p.projectid) as float)*100/(select count(*) from Photos i1 where i1.projectid=p.projectid), 2) Required, ROUND(CAST((select count(*) from Photos i1 where status " +
            "in (2, 3) and i1.projectid=p.projectid) as float)*100/(select count(*) from Photos i1 where i1.projectid=p.projectid), 2) " +
            "Taken, ROUND(CAST((select count(*) from Photos i1 where status = 4 and i1.projectid=p.projectid) as float)*100/(select count(*) from Photos i1 " +
            "where i1.projectid=p.projectid), 2) Approved, ROUND(CAST((select count(*) from Photos i1 where status = 0 and i1.projectid=p.projectid) " +
            "as float)*100/(select count(*) from Photos i1 where i1.projectid=p.projectid), 2) Rejected FROM projects p left outer join Photos i on p.projectid=i.projectid group by p.projectid")
    List<ProjectDisplayModel> displayProject();

    @Query("Select distinct(itemid) AS Sample,  CategoryName, P.* from photos P, categories C where  P.status = 0 and P.categoryid=C.categoryID  Union All Select distinct(itemid) AS Sample, '' AS CategoryName," +
            " * from Photos where Status = 0 and CategoryId = 0 Order by P.ProjectID, P.CategoryID, P.SortOrder")
    List<RejectDisplayModel> displayReject();


    @Query("SELECT * FROM Photos WHERE Status in (2, 7)")
    List<Photos> getCapturedImages();


    @Query("SELECT p.*, (select count(*) from Photos i1 where ProjectID = :projectID) Total, (select count(*) from Photos i1 where status in (1, 7) and ProjectID = :projectID) requiredCount, " +
            "(select count(*) from Photos i1 where status in (2, 3) and ProjectID = :projectID) takenCount, (select count(*) from Photos i1 where status = 4 and ProjectID = :projectID) ApprovedCount, " +
            "(select count(*) from Photos i1 where status = 0 and ProjectID = :projectID) rejectedCount, (select count(*) from Photos i1 where status = 5 and ProjectID = :projectID) OutOfScopeCount, " +
            "ROUND(CAST((select count(*) from Photos i1 where status in (1, 7 ) and ProjectID = :projectID) as float)*100/(select count(*) from Photos i1 where ProjectID = :projectID), 2) Required, " +
            "ROUND(CAST((select count(*) from Photos i1 where status in (2, 3) and ProjectID = :projectID) as float)*100/(select count(*) from Photos i1 where ProjectID = :projectID), 2) Taken, " +
            "ROUND(CAST((select count(*) from Photos i1 where status = 4 and ProjectID = :projectID) as float)*100/(select count(*) from Photos i1 where ProjectID = :projectID), 2) Approved, " +
            "ROUND(CAST((select count(*) from Photos i1 where status = 0 and ProjectID = :projectID) as float)*100/(select count(*) from Photos i1 where ProjectID = :projectID), 2) Rejected " +
            "FROM projects p where ProjectID = :projectID")
    List<PhotoRemainingModel> getRemainingCount(String projectID);

    @Query("SELECT Count(*) Total, (Select count(*) from Photos P where status  in (1, 7) and P.ParentCategoryID like :likes and ProjectID = :projectID) Required," +
            " (Select count(*) from Photos P where status in (2, 3) and P.ParentCategoryID like :likes and ProjectID = :projectID) Taken, (Select count(*) from Photos P where status = 4 and P.ParentCategoryID " +
            "like :likes and ProjectID = :projectID) Approved, (Select count(*) from Photos P where status = 0 and P.ParentCategoryID like :likes and ProjectID = :projectID) Rejected " +
            "FROM Photos P where P.ParentCategoryID like :likes and ProjectID = :projectID and Status Not in (5, 999)")
    List<CategoryHeaderModel> getCategoryHeaderCount1(String projectID, String likes);

    @Query("SELECT Count(Distinct(ItemID)) Total, (Select count(*) from Photos P where status in (1, 7 ) and (P.SectorID = :sectorID OR P.SectorID = 99999) and P.ParentCategoryID like :likes and ProjectID = :projectID) Required, " +
            "(Select count(*) from Photos P where status in (2, 3) and P.SectorID = :sectorID and P.ParentCategoryID like :likes and ProjectID =:projectID) Taken, (Select count(*) from Photos P where status = 4 and " +
            "P.SectorID = :sectorID and P.ParentCategoryID like :likes and ProjectID = :projectID) Approved, (Select count(*) from Photos P where status = 0 and (P.SectorID = :sectorID OR P.SectorID = 99999) and" +
            " P.ParentCategoryID like :likes and ProjectID = :projectID) Rejected FROM Photos P where (P.SectorID = :sectorID OR P.SectorID = 99999) and P.ParentCategoryID like :likes and ProjectID = :projectID " +
            "and Status Not in (5, 999)")
    List<CategoryHeaderModel> getCategoryHeaderCount2(String projectID, String sectorID, String likes);

    @Query("SELECT Count(Distinct(ItemID)) Total, (Select count(*) from Photos P where status in (1, 7) and (P.PositionID = :positionID OR P.PositionID = 99999) and (P.SectorID = :sectorID OR P.SectorID = 99999) " +
            "and P.ParentCategoryID like :likes and ProjectID = :projectID) Required, (Select count(Distinct(ItemID)) from Photos P where status in (2, 3) and P.PositionID = :positionID and P.SectorID = :sectorID and " +
            "P.ParentCategoryID like :likes and ProjectID = :projectID) Taken, (Select count(Distinct(ItemID)) from Photos P where status = 4 and P.PositionID = :positionID and P.SectorID = :sectorID and P.ParentCategoryID" +
            " like :likes and ProjectID = :projectID) Approved, (Select count(Distinct(ItemID)) from Photos P where status = 0 and (P.PositionID = :positionID OR P.PositionID = 99999) and (P.SectorID = :sectorID OR P.SectorID = 99999)" +
            " and P.ParentCategoryID like :likes and ProjectID = :projectID) Rejected FROM Photos P where (P.PositionID = :positionID OR P.PositionID = 99999) and (P.SectorID = :sectorID OR P.SectorID = 99999) " +
            "and P.ParentCategoryID like :likes and ProjectID = :projectID and Status Not in (5, 999)")
    List<CategoryHeaderModel> getCategoryHeaderCount3(String projectID, String positionID, String sectorID, String likes);

    @Query("Select 0 AS TempSort, Null AS PCategoryID, Null AS ProjectID, Null AS CategoryName, RequireSectorPosition AS RequireSectorPosition, Null AS Description, Null AS Status, Null AS IParentCategoryID," +
            " ItemID AS IItemID, ProjectID AS IProjectID, CategoryID AS ICategoryID, ItemName AS ItemName , Description AS IDescription, ReferenceImageName AS IReferenceImageName, CapturedImageName AS ICapturedImageName, " +
            "TakenBy AS ITakenBy, TakenDate AS ITakenDate, CASE Status WHEN 7 THEN 1 ELSE Status END AS IStatus, Comments AS IComments, SectorID AS ISectorID, PositionID AS IPositionID, 'Items' AS Type, SortOrder, " +
            "AdhocPhotoID AS AdhocPhotoID from Photos where CategoryID = :parentID and ProjectID = :projectID Union All Select 1 AS TempSort, CategoryID AS PCategoryID, ProjectID AS PProjectID, " +
            "CategoryName AS CategoryName, ContainsSectorPosition AS RequireSectorPosition, Null AS Description, Null AS Status, ParentCategoryID AS ParentCategoryID, Null AS ItemID, Null AS ProjectID," +
            " Null AS CategoryID, Null AS ItemName , Null AS Description, Null AS ReferenceImageName, Null AS CapturedImageName, Null AS TakenBy, Null AS TakenDate, Null AS IStatus, Null AS Comments, '0' AS ISectorID," +
            " '0' AS IPositionID, 'Category' AS Type, SortOrder, Null AS AdhocPhotoID from Categories where ParentCategoryID = :parentID and ProjectID = :projectID order by TempSort, IStatus, SortOrder")
    List<CategoryDisplayModel> getCategoryDisplay1(String projectID, String parentID);

    @Query("Select 1 AS TempSort, CategoryID AS PCategoryID, ProjectID AS PProjectID, CategoryName AS CategoryName, ContainsSectorPosition AS RequireSectorPosition, Null AS Description, Null AS Status," +
            " ParentCategoryID AS ParentCategoryID, Null AS ItemID,Null AS ProjectID, Null AS CategoryID, Null AS ItemName , Null AS Description, Null AS ReferenceImageName, Null AS CapturedImageName, Null AS TakenBy," +
            " Null AS TakenDate, Null AS IStatus, Null AS Comments, '0' AS ISectorID, '0' AS IPositionID, 'Category' AS Type, SortOrder, Null AS AdhocPhotoID from Categories " +
            "where ParentCategoryID = :parentID and ProjectID = :projectID Union All Select 2 AS TempSort, Null AS PCategoryID, Null AS ProjectID, Name AS CategoryName, Null AS RequireSectorPosition, " +
            "Null AS Description, Null AS IStatus, Null AS IParentCategoryID, Null AS IItemID, Null AS IProjectID, Null AS ICategoryID, Null AS IItemName , Null AS IDescription, Null AS IReferenceImageName, " +
            "Null AS ICapturedImageName, Null AS ITakenBy, Null AS ITakenDate, Null AS IStatus, Null AS IComments, SectorID AS ISectorID, '0' AS IPositionID, 'Sector' AS Type, Null AS SortOrder, Null AS AdhocPhotoID from Sector" +
            " Union All Select 0 AS TempSort, Null AS PCategoryID, Null AS ProjectID, Null AS CategoryName, RequireSectorPosition AS RequireSectorPosition, Null AS Description, Null AS Status, Null AS IParentCategoryID," +
            " ItemID AS ItemID, ProjectID AS IProjectID, CategoryID AS ICategoryID, ItemName AS ItemName , Description AS IDescription,  ReferenceImageName AS IReferenceImageName, CapturedImageName AS ICapturedImageName," +
            " TakenBy AS ITakenBy, TakenDate AS ITakenDate, CASE Status WHEN 7 THEN 1 ELSE Status END AS IStatus, Comments AS IComments, SectorID AS ISectorID, PositionID AS IPositionID, 'Items' AS Type, SortOrder," +
            " AdhocPhotoID AS AdhocPhotoID from Photos where CategoryID = :parentID and ProjectID = :projectID and SectorID = '0' and PositionID = '0' order by TempSort, IStatus, SortOrder")
    List<CategoryDisplayModel> getCategoryDisplay2(String projectID, String parentID);

    @Query("Select 0 AS TempSort, Null AS PCategoryID, Null AS ProjectID, Null AS CategoryName, RequireSectorPosition AS RequireSectorPosition, Null AS Description, Null AS Status, Null AS IParentCategoryID," +
            " ItemID AS ItemID, ProjectID AS IProjectID, CategoryID AS ICategoryID, ItemName AS ItemName , Description AS IDescription, ReferenceImageName AS IReferenceImageName, CapturedImageName AS ICapturedImageName," +
            " TakenBy AS ITakenBy, TakenDate AS ITakenDate, CASE Status WHEN 7 THEN 1 ELSE Status END AS IStatus, Comments AS IComments, SectorID AS ISectorID, PositionID AS IPositionID, 'Items' AS Type, SortOrder," +
            " AdhocPhotoID AS AdhocPhotoID from Photos where CategoryID = :parentID and ProjectID = :projectID and SectorID = :sectorID and PositionID = '0' Union All Select 1 AS TempSort, Null AS CategoryID, Null AS ProjectID," +
            " Name AS CategoryName, Null AS RequireSectorPosition, Null AS Description, Null AS IStatus,  Null AS IParentCategoryID, Null AS IItemID, Null AS IProjectID, Null AS ICategoryID, Null AS IItemName ," +
            " Null AS IDescription, Null AS IReferenceImageName,  Null AS ICapturedImageName, Null AS ITakenBy, Null AS ITakenDate, Null AS IStatus, Null AS IComments, '0' AS ISectorID, PositionID AS IPositionID, 'Position' AS Type," +
            " Null AS SortOrder, Null AS AdhocPhotoID from Position order by TempSort, IStatus, SortOrder")
    List<CategoryDisplayModel> getCategoryDisplay3(String projectID, String parentID, String sectorID);

    @Query("Select Distinct ItemID AS ItemID, Null AS PCategoryID, Null AS ProjectID, Null AS CategoryName, RequireSectorPosition AS RequireSectorPosition, Null AS Description, Null AS Status, Null AS IParentCategoryID," +
            "  ProjectID AS IProjectID, CategoryID AS ICategoryID, ItemName AS ItemName , Description AS IDescription, ReferenceImageName AS IReferenceImageName, CapturedImageName AS ICapturedImageName," +
            " TakenBy AS ITakenBy, TakenDate AS ITakenDate, CASE Status WHEN 7 THEN 1 ELSE Status END AS IStatus, Comments AS IComments, SectorID AS ISectorID, PositionID AS IPositionID, 'Items' AS Type, " +
            "SortOrder, min(AdhocPhotoID) AS AdhocPhotoID from Photos where CategoryID = :parentID and ProjectID = :projectID and (SectorID = :sectorID) and (PositionID = :positionID) group by ItemID union all Select Distinct" +
            " ItemID AS ItemID, Null AS PCategoryID, Null AS ProjectID, Null AS CategoryName, RequireSectorPosition AS RequireSectorPosition, Null AS Description, Null AS Status,  Null AS IParentCategoryID, " +
            " ProjectID AS IProjectID, CategoryID AS ICategoryID, ItemName AS ItemName , Description AS IDescription,  ReferenceImageName AS IReferenceImageName, CapturedImageName AS ICapturedImageName, " +
            "TakenBy AS ITakenBy, TakenDate AS ITakenDate, Status AS IStatus,  Comments AS IComments, SectorID AS ISectorID, PositionID AS IPositionID, 'Items' AS Type, SortOrder, min(AdhocPhotoID) AS AdhocPhotoID " +
            "from Photos where  ICategoryID = :parentID and IProjectID = :projectID  and (ISectorID = 99999) and (IPositionID = 99999) and ItemId Not in (Select Distinct ItemID from Photos where ProjectID= :projectID and " +
            "CategoryID= :parentID and SectorID = :sectorID and PositionID= :positionID) group by ItemID order by IStatus, SortOrder")
    List<CategoryDisplayModel> getCategoryDisplay4(String projectID, String parentID, String sectorID, String positionID);

    @Query("SELECT Count(Distinct(ItemID)) Total, SectorID, (Select count(*) from Photos P where status in (1 , 7) and (P.SectorID = :catID or P.SectorID = 99999) and CategoryID = :parentID and ProjectID = :projectID)" +
            " Required, (Select count(*) from Photos P where status in (2, 3) and P.SectorID = :catID and CategoryID = :parentID and ProjectID = :projectID) Taken, (Select count(*) from Photos P where status = 4" +
            " and P.SectorID = :catID and CategoryID = :parentID and ProjectID = :projectID) Approved, (Select count(*) from Photos P where status = 0 and (P.SectorID = :catID or P.SectorID = 99999) and CategoryID = :parentID" +
            " and ProjectID = :projectID) Rejected FROM Photos P where (P.SectorID = :catID or P.SectorID = 99999) and CategoryID = :parentID and ProjectID = :projectID and Status Not in (5, 999)")
    List<CategoryHeaderModel> getCategoryCount1(String projectID, String parentID, String catID);

    @Query("SELECT Count(Distinct(ItemID)) Total, PositionID, (Select count(*) from Photos P where status in (1, 7) and (P.PositionID = :catID or P.PositionID = 99999) and P.SectorID = :sectorID and CategoryID = :parentID" +
            " and ProjectID = :projectID) Required, (Select count(Distinct(ItemID)) from Photos P where status in (2, 3) and P.PositionID = :catID and P.SectorID = :sectorID and CategoryID = :parentID and ProjectID = :projectID) " +
            "Taken, (Select count(Distinct(ItemID)) from Photos P where status = 4 and P.PositionID = :catID and P.SectorID = :sectorID and CategoryID = :parentID and ProjectID = :projectID) Approved," +
            " (Select count(Distinct(ItemID)) from Photos P where status = 0 and (P.PositionID = :catID or P.PositionID = 99999) and (P.SectorID = :sectorID or P.SectorID = 99999) and CategoryID = :parentID and" +
            " ProjectID = :projectID) Rejected FROM Photos P where (P.PositionID = :catID or P.PositionID = 99999) and (P.SectorID = :sectorID or P.SectorID = 99999) and CategoryID = :parentID and ProjectID = :projectID" +
            " and Status Not in (5, 999)")
    List<CategoryHeaderModel> getCategoryCount2(String projectID, String parentID, String sectorID, String catID);

    @Query("SELECT Count(*) Total, (Select count(*) from Photos P where status in (1 , 7) and P.ParentCategoryID like :likes and ProjectID = :projectID) Required, (Select count(*) from Photos P where status in (2, 3)" +
            " and P.ParentCategoryID like :likes and ProjectID = :projectID) Taken, (Select count(*) from Photos P where status = 4 and P.ParentCategoryID like :likes and ProjectID = :projectID) Approved, " +
            "(Select count(*) from Photos P where status = 0 and P.ParentCategoryID like :likes and ProjectID = :projectID) Rejected FROM Photos P where P.ParentCategoryID like :likes and ProjectID = :projectID" +
            " and Status Not in (5, 999)")
    List<CategoryHeaderModel> getCategoryCount3(String projectID, String likes);

    @Query("select count( Distinct (ItemID)) AS ItemsCount, ItemID from photos where ProjectID = :projectID and CategoryID = :parentID and  SectorID = 99999 and  PositionID = 99999  and Status in (1,7,0) and ItemId " +
            "Not in (Select Distinct ItemID from Photos where ProjectID = :projectID and CategoryID = :parentID and  SectorID = :sectorID and PositionID = :positionID)")
    List<ItemCountModel> getItemsCountInSelectedCategory1(String projectID, String parentID, String sectorID, String positionID);

    @Query("select count(ItemID) AS ItemsCount, ItemID, AdhocPhotoID from photos where ProjectID = :projectID and CategoryID = :parentID and (SectorID = :sectorID) and (PositionID = :positionID) and Status in (1,7,0)")
    List<ItemCountModel> getItemsCountInSelectedCategory2(String projectID, String parentID, String sectorID, String positionID);

    @Query("select * from Photos where AdhocPhotoID = :adhocPhotoID")
    List<Photos> getPhotoDetail(String adhocPhotoID);

    @Query("select * from Photos where Status in (2, 7)")
    List<Photos> getUpdatePhotos();

    @Query("update Photos set Status=3 where Status=2")
    void updatePhotos1();

    @Query("update Photos set Status=1 where Status=7")
    void updatePhotos2();

    @Query("Update Photos set CapturedImageName=:imageName, Status=2, Longitude=:longitude, Latitude=:latitude, TakenBy=:userName, TakenDate=:takenDate, SectorID=:sectorID, PositionID=:positionID where " +
            "AdhocPhotoID=:adhocPhotoID")
    void updateCapturedPhoto(String imageName, String latitude, String longitude, String userName, String takenDate, String sectorID, String positionID, String adhocPhotoID);

    @Query("select DISTINCT(ItemID) AS ItemID, AdhocPhotoID from photos where ProjectID = :projectID and CategoryID = :parentID and (SectorID = 99999) and (PositionID = 99999) and Status in (1,7,0)  " +
            "and ItemID Not in (Select Distinct ItemID from Photos where ProjectID = :projectID and CategoryID = :parentID and  SectorID = :sectorID and PositionID = :positionID) order by SortOrder")
    List<NextItemModel> getNextItemToDisplay1(String projectID, String parentID, String sectorID, String positionID);

    @Query("select ItemID, AdhocPhotoID from photos where ProjectID = :projectID and CategoryID = :parentID and (SectorID = :sectorID) and (PositionID = :positionID) and Status in (1,7,0) order by SortOrder")
    List<NextItemModel> getNextItemToDisplay2(String projectID, String parentID, String sectorID, String positionID);

    @Query("Update Photos set Status=7 , TakenBy=:userName, TakenDate=:takenDate where AdhocPhotoID = :adhocPhotoID" )
    void resetPhoto(String userName, String takenDate, String adhocPhotoID);

    @Query("Select ProjectName AS ProjectName, ProjectName AS CategoryName from Projects where ProjectID = ( Select ProjectID from Categories where categoryId  in " +
            "(:categoryIDList)) Union All SELECT ProjectID AS ProjectName, CategoryName FROM Categories where categoryId in (:categoryIDList) Union All Select Null AS ProjectName, Name AS CategoryName " +
            "from Sector where SectorID = (select SectorID from Photos where adhocPhotoID = :adhocPhotoID) Union All Select Null AS ProjectID,  Name AS CategoryName from Position where " +
            "PositionID = (select PositionID from Photos where adhocPhotoID = :adhocPhotoID)")
    List<LocationMatrixModel> getLocationMatrix(String adhocPhotoID, String categoryIDList);

//    @Query("INSERT INTO Photos (ProjectPhotoID, ProjectID, CategoryID, ItemID, SectorID, PositionID, ItemName, Description, TakenBy, TakenDate, Status, CapturedImageName, Quantity, Latitude, Longitude, IsAdhoc, " +
//            "AdhocPhotoID, ParentCategoryID) values ('0', :projectID, :parentID, '0', :sectorID, :positionID, :itemName, :description, :userName, :takenDate, 2, :imageName, '1', :latitude, :longitude, 'true', " +
//            ":adhocPhotoID, :parentCategoryID)")
//    void insertAdhocPhotos(String projectID, String parentID, String sectorID, String positionID, String itemName, String description, String userName, String takenDate, String imageName, String latitude, String longitude, String adhocPhotoID, String parentCategoryID);
//
//    @Query("INSERT OR REPLACE INTO Photos (ProjectPhotoID, ProjectID, CategoryID, ItemID, SectorID, PositionID, ItemName, Description, Comments, TakenBy, TakenDate, Status, ReferenceImageName, CapturedImageName," +
//            " Quantity, RequireSectorPosition, ParentCategoryID, Latitude, Longitude, IsAdhoc, AdhocPhotoID, SortOrder) values (:projectPhotoID, :projectID, :categoryID, :itemID, :sectorID, :positionID, :itemName, :description, " +
//            ":comments, :takenBy, :convertedDate, :approvalStatus, :referenceImageName, :imagePath, :quantity, :requiredSectorPosition, :categoryRelations,:latitude, :longitude, :isAdhoc, :adhocPhotoID, :sortOrder)" )
//    void insertAndUpdatePhotos(String projectPhotoID, String projectID, String categoryID, String itemID, String sectorID, String positionID, String itemName, String description, String comments, String takenBy, String convertedDate,
//                               Integer approvalStatus, String referenceImageName, String imagePath, Integer quantity, Boolean requiredSectorPosition, String categoryRelations, String latitude, String longitude, Boolean isAdhoc,
//                               String adhocPhotoID, Integer sortOrder);

}
