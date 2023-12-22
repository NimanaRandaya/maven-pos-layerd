package controller;

import bo.BoFactory;
import bo.custom.ItemBo;
import bo.custom.impl.ItemBoImpl;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import dao.util.BoType;
import dto.ItemDto;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import dto.tm.ItemTm;
import dao.custom.ItemDao;
import dao.custom.impl.ItemDaoImpl;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.function.Predicate;

public class ItemFormController {

    public JFXTextField txtSearch;
    @FXML
    private BorderPane itemPane;

    @FXML
    private JFXTreeTableView<ItemTm> tblItem;

    @FXML
    private JFXTextField txtDescription;

    @FXML
    private JFXTextField txtUnitPrice;

    @FXML
    private JFXTextField txtItemCode;

    @FXML
    private JFXTextField txtQty;

    @FXML
    private TreeTableColumn colItemCode;

    @FXML
    private TreeTableColumn colDescription;

    @FXML
    private TreeTableColumn colUnitPrice;

    @FXML
    private TreeTableColumn colQtyOnHand;

    @FXML
    private TreeTableColumn colOption;

    private ItemBo itemBo = BoFactory.getInstance().getBo(BoType.ITEM);

    public void initialize(){
        colItemCode.setCellValueFactory(new TreeItemPropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new TreeItemPropertyValueFactory<>("description"));
        colUnitPrice.setCellValueFactory(new TreeItemPropertyValueFactory<>("unitPrice"));
        colQtyOnHand.setCellValueFactory(new TreeItemPropertyValueFactory<>("qty"));
        colOption.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));
        loadItemTable();

        tblItem.getSelectionModel().selectedItemProperty().addListener((ObservableValue,oldValue,newValue) ->{
            setData(newValue);
        });

        txtSearch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                tblItem.setPredicate(new Predicate<TreeItem<ItemTm>>() {
                    @Override
                    public boolean test(TreeItem<ItemTm> treeItem) {
                        return treeItem.getValue().getCode().contains(newValue) ||
                                treeItem.getValue().getDescription().contains(newValue);
                    }
                });
            }

        });
    }

    private void setData(TreeItem<ItemTm> newValue) {
        if (newValue!= null){
            txtItemCode.setEditable(false);
            txtItemCode.setText(newValue.getValue().getCode());
            txtDescription.setText(newValue.getValue().getDescription());
            txtUnitPrice.setText(String.valueOf(newValue.getValue().getUnitPrice()));
            txtQty.setText(String.valueOf(newValue.getValue().getQty()));
        }
    }

    private void loadItemTable() {
        ObservableList<ItemTm> tmList = FXCollections.observableArrayList();
        try {
            List<ItemDto> dtoList = itemBo.allItems();
            for (ItemDto dto:dtoList){
                JFXButton btn = new JFXButton("Delete");
                ItemTm tm = new ItemTm(
                        dto.getCode(),
                        dto.getDescription(),
                        dto.getUnitPrice(),
                        dto.getQty(),
                        btn
                );
                btn.setOnAction(actionEvent ->{
                  deleteItem(tm.getCode());
                });
                tmList.add(tm);
            }
            RecursiveTreeItem <ItemTm> treeItem = new RecursiveTreeItem<ItemTm>(tmList, RecursiveTreeObject::getChildren);
            tblItem.setRoot(treeItem);
            tblItem.setShowRoot(false);

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteItem(String code) {
        try {
            boolean isDeleted = itemBo.deleteItem(code);
            if (isDeleted){
                new Alert(Alert.AlertType.INFORMATION,"Item Deleted!").show();
                loadItemTable();
            }else{
                new Alert(Alert.AlertType.ERROR,"Something Went Wrong!").show();
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void clearFields() {
        tblItem.refresh();
        txtItemCode.clear();
        txtDescription.clear();
        txtUnitPrice.clear();
        txtQty.clear();
        txtItemCode.setEditable(true);
    }

    @FXML
    void backButtonOnAction(ActionEvent event) {
        Stage stage = (Stage)itemPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/DashboardForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void saveButtonOnAction(ActionEvent event) {
        try {
            boolean isSaved = itemBo.saveItem(new ItemDto(txtItemCode.getText(),
                    txtDescription.getText(),
                    Double.parseDouble(txtUnitPrice.getText()),
                    Integer.parseInt(txtQty.getText()))
            );
            if (isSaved) {
                new Alert(Alert.AlertType.INFORMATION, "Item saved!").show();
                loadItemTable();
                clearFields();
            }
        }catch (SQLIntegrityConstraintViolationException ex){
            new Alert(Alert.AlertType.ERROR, "Duplicate Entry!").show();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void updateButtonOnAction(ActionEvent event) {
        try {
            boolean isUpdated = itemBo.updateItem( new ItemDto(txtItemCode.getText(),
                    txtDescription.getText(),
                    Double.parseDouble(txtUnitPrice.getText()),
                    Integer.parseInt(txtQty.getText())
            ));
            if (isUpdated) {
                new Alert(Alert.AlertType.INFORMATION, "Customer Updated!").show();
                loadItemTable();
                clearFields();
            }
        }catch (SQLIntegrityConstraintViolationException ex){
            new Alert(Alert.AlertType.ERROR, "Duplicate Entry!").show();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
