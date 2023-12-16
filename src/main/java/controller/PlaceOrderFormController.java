package controller;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import dto.OrderDetailsDto;
import dto.OrderDto;
import dto.tm.OrderTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.CustomerModel;
import model.ItemModel;
import model.OrderModel;
import model.impl.CustomerModelImpl;
import model.impl.ItemModelImpl;
import dto.CustomerDto;
import dto.ItemDto;
import model.impl.OrderModelImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PlaceOrderFormController {
    public AnchorPane placeOrderPane;
    public JFXComboBox cmbCustId;
    public JFXComboBox cmbItemCode;
    public JFXTextField txtCustName;
    public JFXTextField txtDescription;
    public JFXTextField txtUnitPrice;
    public JFXTextField txtBuyingQty;
    public TreeTableColumn colCode;
    public TreeTableColumn colDescription;
    public TreeTableColumn colQty;
    public TreeTableColumn colOption;
    public TreeTableColumn colAmount;
    public Label lblTotal;
    public JFXTreeTableView <OrderTm> tblOrder;
    public Label lblOrderId;
    private List<CustomerDto> customers;
    private List<ItemDto> items;
    private CustomerModel customerModel = new CustomerModelImpl();
    private ItemModel itemModel=new ItemModelImpl();
    private double tot = 0;
    private OrderModel orderModel= new OrderModelImpl();
    private ObservableList<OrderTm> orderTms =FXCollections.observableArrayList();
    public void initialize(){
        colCode.setCellValueFactory(new TreeItemPropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new TreeItemPropertyValueFactory<>("description"));
        colQty.setCellValueFactory(new TreeItemPropertyValueFactory<>("qty"));
        colAmount.setCellValueFactory(new TreeItemPropertyValueFactory<>("amount"));
        colOption.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));

        generatedId();
        loadCustomerIds();
        loadItemCodes();

        cmbCustId.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, id) -> {
            for (CustomerDto dto : customers) {
                if (dto.getId().equals(id)) {
                    txtCustName.setText(dto.getName());
                }
            }
        });

        cmbItemCode.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, code) -> {
            for (ItemDto dto : items) {
                if (dto.getCode().equals(code)) {
                    txtDescription.setText(dto.getDescription());
                    txtUnitPrice.setText(String.valueOf(dto.getUnitPrice()));
                }
            }
        });

    }

    private void loadItemCodes() {
        try {
            items =itemModel.allItems();
            ObservableList list= FXCollections.observableArrayList();
            for (ItemDto dto:items){
                list.add(dto.getCode());
            }
            cmbItemCode.setItems(list);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadCustomerIds() {
        try {
            customers = customerModel.allCustomer();
            ObservableList list= FXCollections.observableArrayList();
            for (CustomerDto dto:customers){
                list.add(dto.getId());
            }
            cmbCustId.setItems(list);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void addToCartButtonOnAction(ActionEvent actionEvent) {
        try {
            double amount = itemModel.getItem(cmbItemCode.getValue().toString()).getUnitPrice()* Integer.parseInt(txtBuyingQty.getText());
            JFXButton btn = new JFXButton("Delete");

            OrderTm orderTm =new OrderTm(
                    cmbItemCode.getValue().toString(),
                    txtDescription.getText(),
                    Integer.parseInt(txtBuyingQty.getText()),
                    amount,
                    btn
                    );

            btn.setOnAction(actionEvent1 ->{
                orderTms.remove(orderTm);
                tot-=orderTm.getAmount();
                tblOrder.refresh();
                lblTotal.setText(String.format("%.2f",tot));
            });

            boolean isExist = false;

            for (OrderTm order:orderTms){
                if (order.getCode().equals(orderTm.getCode())){
                    order.setQty(order.getQty()+orderTm.getQty());
                    order.setAmount(order.getAmount()+orderTm.getAmount());
                    isExist =true;
                    tot+=orderTm.getAmount();
                }
            }

            if (!isExist) {
                orderTms.add(orderTm);
                tot+=orderTm.getAmount();
            }

            RecursiveTreeItem<OrderTm> treeItem = new RecursiveTreeItem<>(orderTms, RecursiveTreeObject::getChildren);
            tblOrder.setRoot(treeItem);
            tblOrder.setShowRoot(false);
            lblTotal.setText(String.format("%.2f",tot));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void generatedId(){
        try {
            OrderDto dto = orderModel.lastOrder();
            if (dto!=null){
                String id = dto.getOrderId();
                int num = Integer.parseInt(id.split("[D]")[1]);
                num++;
                lblOrderId.setText(String.format("D%03d",num));
            }else{
               lblOrderId.setText("D001");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void placeOrderButtonOnAction(ActionEvent actionEvent) {
        List<OrderDetailsDto> list = new ArrayList<>();
        for (OrderTm tm:orderTms){
            list.add(new OrderDetailsDto(
                    lblOrderId.getText(),
                    tm.getCode(),
                    tm.getQty(),
                    tm.getAmount()/ tm.getQty()
            ));
        }
        if (!orderTms.isEmpty()){
            boolean isSaved = false;
            try {
                isSaved = orderModel.saveOrder(new OrderDto(
                        lblOrderId.getText(),
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd")).toString(),
                        cmbCustId.getValue().toString(),
                        list
                ));
                if (isSaved){
                    new Alert(Alert.AlertType.INFORMATION,"Order Saved").show();
                }else {
                    new Alert(Alert.AlertType.ERROR,"Something Went Wrong").show();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
       }else {
            new Alert(Alert.AlertType.ERROR,"Please Add to Cart.").show();
        }
    }

    public void backButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage)placeOrderPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/DashboardForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
