package ru.iworking.personnel.reserve.component.layout;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.springframework.context.ApplicationContext;
import ru.iworking.personnel.reserve.ApplicationContextProvider;
import ru.iworking.personnel.reserve.entity.Education;
import ru.iworking.personnel.reserve.entity.LearningHistory;
import ru.iworking.personnel.reserve.model.EducationCellFactory;
import ru.iworking.personnel.reserve.service.EducationServiceImpl;
import ru.iworking.personnel.reserve.utils.FXMLUtil;

public class LearningHistoryEditPane extends VBox {

    @FXML private Pane parent;

    @FXML private ComboBox<Education> educationComboBox;
    @FXML private TextArea editableText;

    private Long learningHistoryId;

    private  final EducationServiceImpl educationService;

    private EducationCellFactory educationCellFactory = new EducationCellFactory();

    public LearningHistoryEditPane() {
        ApplicationContext context = ApplicationContextProvider.getApplicationContext();
        this.educationService = context.getBean(EducationServiceImpl.class);
        FXMLUtil.load("/fxml/LearningHistoryEditBlock.fxml", this, this);
        initData();
    }

    public void initData() {
        educationComboBox.setButtonCell(educationCellFactory.call(null));
        educationComboBox.setCellFactory(educationCellFactory);
        educationComboBox.setItems(FXCollections.observableList(educationService.findAll()));
    }

    public void setLearningHistory(LearningHistory learningHistory) {
        this.setLearningHistoryId(learningHistory.getId());
        this.setEducation(learningHistory.getEducation());
        this.setDescription(learningHistory.getDescription());
    }
    public LearningHistory getLearningHistory() {
        LearningHistory learningHistory = new LearningHistory();
        learningHistory.setId(this.getLearningHistoryId());
        learningHistory.setEducation(this.getEducation());
        if (learningHistory.getEducation() == null) return null;
        learningHistory.setDescription(this.getDescription());
        return learningHistory;
    }

    public Long getLearningHistoryId() {
        return learningHistoryId;
    }
    public void setLearningHistoryId(Long learningHistoryId) {
        this.learningHistoryId = learningHistoryId;
    }

    public void setEducation(Education education) {
        educationComboBox.setValue(education);
    }
    public Education getEducation() {
        return educationComboBox.getValue();
    }

    public void setDescription(String description) {
        editableText.setText(description);
    }
    public String getDescription() {
        return editableText.getText();
    }

    @FXML
    public void actionDelete(ActionEvent event) {
        ((Pane)parent.getParent()).getChildren().remove(parent);
    }

}
