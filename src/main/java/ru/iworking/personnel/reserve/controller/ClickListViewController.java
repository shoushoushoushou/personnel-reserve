package ru.iworking.personnel.reserve.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.iworking.personnel.reserve.component.list.view.cell.ClickCell;
import ru.iworking.personnel.reserve.component.list.view.factory.ClickCellControllerFactory;
import ru.iworking.personnel.reserve.entity.Click;
import ru.iworking.personnel.reserve.entity.Resume;
import ru.iworking.personnel.reserve.service.ClickService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ClickListViewController implements Initializable {

    private static final Logger logger = LogManager.getLogger(ClickListViewController.class);

    private final ClickService clickService;

    @Autowired @Lazy private ClickCellControllerFactory clickCellControllerFactory;

    @Autowired @Lazy private ResumeEditController resumeEditController;
    @Autowired @Lazy private ResumeViewController resumeViewController;
    @Autowired @Lazy private VacancyTabContentController vacanciesPaneController;
    @Autowired @Lazy private VacancyListViewController vacancyListViewController;

    @FXML private Pane parent;

    @FXML private ListView<Click> clickListView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hide();
        clickListView.setCellFactory(listView -> {
            ClickCell cell = new ClickCell(clickCellControllerFactory);
            return cell;
        });
        clickListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null && newSelection.getResume() != null) {
                resumeViewController.setData(newSelection.getResume());
                resumeViewController.show();
                resumeEditController.hide();
                resumeEditController.clear();
                vacanciesPaneController.downClientSrollPane();
            }
        });
    }

    public void setData(List<Click> list) {
        ObservableList<Click> observableList = FXCollections.observableList(list);
        clickListView.setItems(observableList);
    }

    public void clear() {
        clickListView.setItems(null);
    }

    public void clearSelection() {
        if (clickListView.getSelectionModel() != null) clickListView.getSelectionModel().clearSelection();
    }

    public void selectionItem(Resume resume) {
        if (clickListView != null && clickListView.getItems() != null && clickListView.getItems().size() > 0) {
            List<Click> list = clickListView.getItems().stream()
                    .filter(click -> click.getResume().getId().equals(resume.getId()))
                    .collect(Collectors.toList());
            if (clickListView.getSelectionModel() != null) {
                if (list.size() > 0) clickListView.getSelectionModel().select(list.get(0)); else this.clearSelection();
            }
        }
    }

    public void unfastenItem(Click click) {
        clickListView.getItems().remove(click);
        clickService.deleteById(click.getId());
        vacancyListViewController.actionUpdate(null);
    }

    public void show() {
        parent.setVisible(true);
        parent.setManaged(true);
    }

    public void hide() {
        parent.setVisible(false);
        parent.setManaged(false);
    }

}
