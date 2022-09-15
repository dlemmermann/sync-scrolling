package com.dlsc.scrolling;

import com.dlsc.scrolling.util.Model;
import com.dlsc.scrolling.util.Model.Entry;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Cell;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.TreeTableViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ScrollToMethods extends Application {
    private ListView<Entry> listView = new ListView<>();
    private TreeTableView<Entry> tableView = new TreeTableView<>();

    private VirtualFlow<ListCell<Entry>> listVirtualFlow;
    private VirtualFlow<TreeTableRow<Entry>> tableVirtualFlow;

    @Override
    public void start(Stage primaryStage) {
        Model model = new Model(tableView, listView);

        primaryStage.setTitle(this.getClass().getName());
        TreeTableColumn<Entry, String> nameColumn = new TreeTableColumn<>("Name");
        nameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(170);
        nameColumn.setCellFactory(column -> new TreeTableCell<>() {
            {
                setAlignment(Pos.CENTER_LEFT);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    TreeItem<Entry> treeItem = getTreeTableRow().getTreeItem();
                    if (treeItem != null) {
                        Entry entry = treeItem.getValue();
                        setMinHeight(entry.getHeight());
                        setPrefHeight(entry.getHeight());
                        setMaxHeight(entry.getHeight());
                        setText(item);
                    }
                } else {
                    setText("");
                }
            }
        });

        TreeItem<Entry> root = model.createRows();

        tableView.setSkin(new TreeTableViewSkin<>(tableView) {
            @Override
            protected VirtualFlow<TreeTableRow<Entry>> createVirtualFlow() {
                tableVirtualFlow = new VirtualFlow<>() {
                };
                return tableVirtualFlow;
            }
        });
        tableView.getColumns().add(nameColumn);
        tableView.setRoot(root);
        tableView.setShowRoot(true);
        tableView.setPrefWidth(300);
        tableView.getStyleClass().add("gantt-tree-table-view");
        listView.setSkin(new ListViewSkin<>(listView) {
                             @Override
                             protected VirtualFlow<ListCell<Entry>> createVirtualFlow() {
                                 listVirtualFlow = new VirtualFlow<>() {
                                 };
                                 listVirtualFlow.positionProperty().addListener(o -> {
                                     int ls = listView.getItems().size();
                                     int i = 0;
                                     Cell me = null;
                                     while ((me == null) && (i < ls)) {
                                         me = listVirtualFlow.getVisibleCell(i);
                                         i++;
                                     }
                                     if (me != null) {
                                         i = i - 1;
                                         double delta = -1 * me.getLayoutY();
                                         tableView.scrollTo(i);
                                         tableView.layout();
                                         tableVirtualFlow.scrollPixels(delta);
                                         tableView.layout();
                                     }
                                 });
                                 return listVirtualFlow;
                             }
                         }
        );
        listView.getStyleClass().add("graphics-list-view");
        listView.setCellFactory(listView -> new ListCell<>() {

            @Override
            protected void updateItem(Entry item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setMinHeight(item.getHeight());
                    setPrefHeight(item.getHeight());
                    setMaxHeight(item.getHeight());
                    setText(item.getName());
                } else {
                    setText("");
                }
            }
        });
        Region spacer = new Region();
        spacer.getStyleClass().add("spacer");
        spacer.setStyle("-fx-background-color: lightgrey;");
        spacer.setPrefHeight(24);

        VBox rightHandSide = new VBox(spacer, listView);
        VBox.setVgrow(listView, Priority.ALWAYS);

        FlowPane flowPane = new FlowPane();
        flowPane.setMaxWidth(30);
        Button buttonScrollDownTv = new Button("Scroll down TreeTableView");
        buttonScrollDownTv.setOnAction((e) -> {
            tableView.scrollTo(listView.getItems().size() - 1);
        });
        Button buttonScrollUpTv = new Button("Scroll up TreeTableView");
        buttonScrollUpTv.setOnAction((e) -> {
            tableView.scrollTo(0);
        });

        Button buttonScrollDownLv = new Button("Scroll down ListView");
        buttonScrollDownLv.setOnAction((e) -> {
            listView.scrollTo(listView.getItems().size() - 1);
        });
        Button buttonScrollUpLv = new Button("Scroll up ListView");
        buttonScrollUpLv.setOnAction((e) -> {
            listView.scrollTo(0);
        });

        flowPane.getChildren().addAll(buttonScrollDownTv, buttonScrollUpTv, buttonScrollDownLv, buttonScrollUpLv);
        HBox hBox = new HBox(tableView, rightHandSide, flowPane);
        HBox.setHgrow(tableView, Priority.NEVER);
        HBox.setHgrow(rightHandSide, Priority.ALWAYS);

        Scene scene = new Scene(hBox);
        scene.getStylesheets().add(ScrollToMethods.class.getResource("test.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setWidth(900);
        primaryStage.setHeight(700);
        primaryStage.centerOnScreen();
        primaryStage.setTitle("ScrollToMethods - JavaFX version: " + System.getProperty("javafx.runtime.version"));
        primaryStage.show();

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "This demo only supports scrolling via the right-hand side scrollbar (for now). The demo tries to 'calculate' the scroll position for the left-hand side (the tree table view). You should be using JavaFX version 19 for testing. You are using " + System.getProperty("javafx.runtime.version"));
        alert.initOwner(primaryStage);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.show();

        root.setExpanded(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}