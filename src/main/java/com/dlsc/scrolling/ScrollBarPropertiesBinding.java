package com.dlsc.scrolling;

import com.dlsc.scrolling.util.Model;
import com.dlsc.scrolling.util.Model.Entry;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ScrollBarPropertiesBinding extends Application {
    private ListView<Entry> listView = new ListView<>();
    private TreeTableView<Entry> treeTableView = new TreeTableView<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Model model = new Model(treeTableView, listView);

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

        treeTableView.getColumns().add(nameColumn);
        treeTableView.setRoot(root);
        treeTableView.setShowRoot(true);
        treeTableView.setPrefWidth(300);
        treeTableView.getStyleClass().add("gantt-tree-table-view");
        treeTableView.skinProperty().addListener(it -> bindVerticalListViewScrollBarWithVerticalTreeTableScrollBar());

        listView.getStyleClass().add("graphics-list-view");
        listView.skinProperty().addListener(it -> bindVerticalListViewScrollBarWithVerticalTreeTableScrollBar());
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
            treeTableView.scrollTo(listView.getItems().size() - 1);
        });
        Button buttonScrollUpTv = new Button("Scroll up TreeTableView");
        buttonScrollUpTv.setOnAction((e) -> {
            treeTableView.scrollTo(0);
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
        HBox hBox = new HBox(treeTableView, rightHandSide, flowPane);
        HBox.setHgrow(treeTableView, Priority.NEVER);
        HBox.setHgrow(rightHandSide, Priority.ALWAYS);

        Scene scene = new Scene(hBox);
        scene.getStylesheets().add(ScrollBarPropertiesBinding.class.getResource("test.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setWidth(900);
        primaryStage.setHeight(700);
        primaryStage.centerOnScreen();
        primaryStage.setTitle("ScrollBarPropertiesBinding - JavaFX version: " + System.getProperty("javafx.runtime.version"));
        primaryStage.show();

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "This demo only works with JavaFX 16. The demo binds several properties of the two scrollbars found in the tree table view (left-hand side) and the list view (right-hand side). Syncing fails with new versions. You are using " + System.getProperty("javafx.runtime.version"));
        alert.initOwner(primaryStage);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.show();

        root.setExpanded(true);
    }

    private ScrollBar findScrollBar(Parent parent, Orientation orientation) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof ScrollBar) {
                ScrollBar b = (ScrollBar) node;
                if (b.getOrientation().equals(orientation)) {
                    return b;
                }
            }

            if (node instanceof Parent) {
                ScrollBar b = findScrollBar((Parent) node, orientation);
                if (b != null) {
                    return b;
                }
            }
        }

        return null;
    }

    protected void bindVerticalListViewScrollBarWithVerticalTreeTableScrollBar() {
        ScrollBar treeTableScrollBar = findScrollBar(treeTableView, Orientation.VERTICAL);
        ScrollBar graphicsViewScrollBar = findScrollBar(listView, Orientation.VERTICAL);

        if (treeTableScrollBar != null && graphicsViewScrollBar != null) {
            Bindings.bindBidirectional(treeTableScrollBar.valueProperty(), graphicsViewScrollBar.valueProperty());
            Bindings.bindBidirectional(treeTableScrollBar.visibleAmountProperty(), graphicsViewScrollBar.visibleAmountProperty());
            Bindings.bindBidirectional(treeTableScrollBar.blockIncrementProperty(), graphicsViewScrollBar.blockIncrementProperty());
            Bindings.bindBidirectional(treeTableScrollBar.unitIncrementProperty(), graphicsViewScrollBar.unitIncrementProperty());
            Bindings.bindBidirectional(treeTableScrollBar.minProperty(), graphicsViewScrollBar.minProperty());
            Bindings.bindBidirectional(treeTableScrollBar.maxProperty(), graphicsViewScrollBar.maxProperty());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}