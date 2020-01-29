package ru.iworking.personnel.reserve.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.iworking.personnel.reserve.MainApp;
import ru.iworking.personnel.reserve.dao.*;
import ru.iworking.personnel.reserve.entity.*;
import ru.iworking.personnel.reserve.model.*;
import ru.iworking.personnel.reserve.utils.AppUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class ModalEditResumeFxmlController implements Initializable {

    private static final Logger logger = LogManager.getLogger(ModalEditResumeFxmlController.class);

    private BigDecimalFormatter bigDecimalFormatter = new BigDecimalFormatter();
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");

    @FXML private TextField lastNameTextField;
    @FXML private TextField firstNameTextField;
    @FXML private TextField middleNameTextField;
    @FXML private TextField numberPhoneTextField;
    @FXML private TextField emailTextField;
    @FXML private TextField professionTextField;
    @FXML private ComboBox<ProfField> profFieldComboBox;
    @FXML private TextField wageTextField;
    @FXML private ComboBox<Currency> currencyComboBox;
    @FXML private ComboBox<WorkType> workTypeComboBox;
    @FXML private ComboBox<Education> educationComboBox;
    @FXML private TextArea addressTextArea;
    @FXML private DatePicker experienceDateStartDatePicker;
    @FXML private DatePicker experienceDateEndDatePicker;

    @FXML private ImageView photoImageView;

    @FXML private Button buttonCancel;

    private ObservableList<Resume> resumeObservableList;
    private ProfField currentProfField;
    
    private ProfFieldDao profFieldDao = ProfFieldDao.getInstance();
    private WorkTypeDao workTypeDao = WorkTypeDao.getInstance();
    private EducationDao educationDao = EducationDao.getInstance();
    private ResumeDao resumeDao = ResumeDao.getInstance();
    private CurrencyDao currencyDao = CurrencyDao.getInstance();
    
    private ProfFieldCellFactory profFieldCellFactory = new ProfFieldCellFactory();
    private WorkTypeCellFactory workTypeCellFactory = new WorkTypeCellFactory();
    private EducationCellFactory educationCellFactory = new EducationCellFactory();
    private CurrencyCellFactory currencyCellFactory = new CurrencyCellFactory();
    
    private Resume resume;

    public void setResumeObservableList(ObservableList<Resume> tvObservableList) {
        this.resumeObservableList = tvObservableList;
    }

    public ObservableList<Resume> getResumeObservableList() {
        return resumeObservableList;
    }

    public void setResume(Resume resume) {
        this.resume = resume;
        this.setValues(this.resume);
    }

    public Resume getResume() {
        return resume;
    }

    public ProfField getCurrentProfField() {
        return currentProfField;
    }

    public void setCurrentProfField(ProfField currentProfField) {
        this.currentProfField = currentProfField;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        wageTextField.setTextFormatter(bigDecimalFormatter);

        profFieldComboBox.setButtonCell(profFieldCellFactory.call(null));
        profFieldComboBox.setCellFactory(profFieldCellFactory);
        profFieldComboBox.setItems(FXCollections.observableList(profFieldDao.findAll()));

        workTypeComboBox.setButtonCell(workTypeCellFactory.call(null));
        workTypeComboBox.setCellFactory(workTypeCellFactory);
        workTypeComboBox.setItems(FXCollections.observableList(workTypeDao.findAll()));

        educationComboBox.setButtonCell(educationCellFactory.call(null));
        educationComboBox.setCellFactory(educationCellFactory);
        educationComboBox.setItems(FXCollections.observableList(educationDao.findAll()));

        currencyComboBox.setButtonCell(currencyCellFactory.call(null));
        currencyComboBox.setCellFactory(currencyCellFactory);
        currencyComboBox.setItems(FXCollections.observableList(currencyDao.findAll()));
    }

    public void setValues(Resume resume) {
        lastNameTextField.setText(resume.getLastName());
        firstNameTextField.setText(resume.getFirstName());
        middleNameTextField.setText(resume.getMiddleName());
        numberPhoneTextField.setText(resume.getNumberPhone());
        emailTextField.setText(resume.getEmail());
        professionTextField.setText(resume.getProfession());
        profFieldComboBox.setValue(resume.getProfField());
        if (resume.getWage() != null) wageTextField.setText(decimalFormat.format(resume.getWage()));
        currencyComboBox.setValue(resume.getCurrency());
        workTypeComboBox.setValue(resume.getWorkType());
        educationComboBox.setValue(resume.getEducation());
        experienceDateStartDatePicker.setValue(resume.getExperience().getDateStart());
        experienceDateEndDatePicker.setValue(resume.getExperience().getDateEnd());
        
        addressTextArea.setText(resume.getAddress());

        if (resume.getPhoto() != null) {
            InputStream targetStream = new ByteArrayInputStream(resume.getPhoto());
            Image img = new Image(targetStream);
            photoImageView.setImage(img);
        } else {
            Image defaultImage = new Image(getClass().getClassLoader().getResourceAsStream("images/default.resume.jpg"));
            photoImageView.setImage(defaultImage);
        }
    }

    public void showAndWait(Parent parent) {
        Stage primaryStage = MainApp.PARENT_STAGE;

        Scene scene = new Scene(parent);
        scene.getStylesheets().add("/styles/main.css");
        scene.getStylesheets().add("/styles/button.css");
        scene.getStylesheets().add("/styles/text.field.css");
        scene.getStylesheets().add("/styles/combo.box.css");
        scene.getStylesheets().add("/styles/date.picker.css");
        scene.getStylesheets().add("/styles/text.area.css");
        scene.getStylesheets().add("/styles/modal.css");

        Stage modal = new Stage();
        modal.setTitle("Изменить");
        AppUtil.setIcon(modal);
        modal.setScene(scene);
        modal.initModality(Modality.WINDOW_MODAL);
        modal.initOwner(primaryStage);
        modal.showAndWait();
    }

    @FXML
    private void actionButtonImageReplace(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("GIF", "*.gif")
        );

        File file = fileChooser.showOpenDialog(getStage(event));
        if (file != null) {
            try {
                Image img = new Image(file.toURI().toString());
                photoImageView.setImage(img);
            } catch (Exception ex) {
                logger.error(ex);
            }
        }
    }

    @FXML
    private void actionButtonCancel(ActionEvent event) {
        this.closeStage(event);
    }

    @FXML
    private void actionButtonEdit(ActionEvent event) {
        if (currentProfField == null || currentProfField.equals(resume.getProfField())) {
            this.resumeObservableList.remove(resume);
        } else {
            resumeDao.delete(resume);
        }

        resume.setLastName(lastNameTextField.getText());
        resume.setFirstName(firstNameTextField.getText());
        resume.setMiddleName(middleNameTextField.getText());
        resume.setNumberPhone(numberPhoneTextField.getText());
        resume.setEmail(emailTextField.getText());
        resume.setProfession(professionTextField.getText());
        resume.setProfField(profFieldComboBox.getValue());
        if (!wageTextField.getText().isEmpty()) {
            try {
                resume.setWage(new BigDecimal(wageTextField.getText().replaceAll(",",".")));
            } catch (Exception e) {
                logger.error(e);
            }
        }
        resume.setCurrency(currencyComboBox.getValue());
        resume.setWorkType(workTypeComboBox.getValue());
        resume.setEducation(educationComboBox.getValue());
        
        
        Experience exp = new Experience();
        exp.setDateStart(experienceDateStartDatePicker.getValue());
        exp.setDateEnd(experienceDateEndDatePicker.getValue());
        
        resume.setExperience(exp);
        resume.setAddress(addressTextArea.getText());

        try(ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            BufferedImage originalImage = SwingFXUtils.fromFXImage(photoImageView.getImage(), null);
            ImageIO.write(originalImage, "png", stream);
            resume.setPhoto(stream.toByteArray());
        } catch (IOException e) {
            logger.error(e);
        }

        if (currentProfField == null || currentProfField.equals(resume.getProfField())) {
            this.resumeObservableList.add(resume);
        } else {
            resumeDao.create(resume);
        }
        this.closeStage(event);
    }

    private Stage getStage(ActionEvent event) {
        Node source = (Node)  event.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        return stage;
    }

    private void closeStage(ActionEvent event) {
        getStage(event).close();
    }

}
