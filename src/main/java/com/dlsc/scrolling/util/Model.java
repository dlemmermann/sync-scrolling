package com.dlsc.scrolling.util;

import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private final TreeTableView<Entry> treeTableView;
    private final ListView<Entry> listView;

    public Model(TreeTableView<Entry> treeTableView, ListView<Entry> listView) {
        this.treeTableView = treeTableView;
        this.listView = listView;
    }

    public TreeItem<Entry> createRows() {
        TreeItem<Entry> root = new TreeItem<>(new Entry("Root", 25));
        for (int i = 0; i < 10; i++) {
            TreeItem<Entry> parent = new TreeItem<>(new Entry("Parent " + i, 25));
            parent.setExpanded(true);
            root.getChildren().add(parent);
            parent.expandedProperty().addListener(it -> updateRows());
            for (int j = 0; j < 10 + i; j++) {
                TreeItem<Entry> treeItem = new TreeItem<>(new Entry("Child " + i + "/" + j));
                parent.getChildren().add(treeItem);
            }
        }
        root.expandedProperty().addListener(it -> updateRows());
        return root;
    }

    private void updateRows() {
        List<Entry> rows = new ArrayList<>();
        TreeItem<Entry> root = treeTableView.getRoot();
        rows.add(root.getValue());
        if (root.isExpanded()) {
            for (int i = 0; i < root.getChildren().size(); i++) {
                TreeItem<Entry> parent = root.getChildren().get(i);
                rows.add(parent.getValue());
                if (parent.isExpanded()) {
                    for (int j = 0; j < parent.getChildren().size(); j++) {
                        rows.add(parent.getChildren().get(j).getValue());
                    }
                }
            }
        }
        listView.getItems().setAll(rows);
    }

    public static class Entry {
        static int index = -1;
        private String name;
        private double height = 40;

        public Entry(String name, int height) {
            this.name = name + " h: " + height;
            this.height = height;
        }

        public Entry(String name) {

            switch (++index % 4) {
                case 0:
                    setHeight(50);
                    break;
                case 1:
                    setHeight(30);
                    break;
                case 2:
                    setHeight(70);
                    break;
                case 3:
                    setHeight(100);
                    break;
            }
            this.name = name + " h: " + getHeight();
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getHeight() {
            return height;
        }

        public void setHeight(double height) {
            this.height = height;
        }
    }
}
