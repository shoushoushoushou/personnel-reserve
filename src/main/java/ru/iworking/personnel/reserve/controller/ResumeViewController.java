package ru.iworking.personnel.reserve.controller;

import com.google.common.base.Strings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.iworking.personnel.reserve.ApplicationJavaFX;
import ru.iworking.personnel.reserve.component.Messager;
import ru.iworking.personnel.reserve.entity.Currency;
import ru.iworking.personnel.reserve.entity.*;
import ru.iworking.personnel.reserve.interfaces.AppFunctionalInterface;
import ru.iworking.personnel.reserve.service.*;
import ru.iworking.personnel.reserve.utils.ImageUtil;
import ru.iworking.personnel.reserve.utils.TextUtil;
import ru.iworking.personnel.reserve.utils.TimeUtil;
import ru.iworking.personnel.reserve.utils.docs.pdf.PdfResumeWriter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ResumeViewController implements Initializable {

    private static final Logger logger = LogManager.getLogger(ResumeViewController.class);

    private final ImageContainerService imageContainerService;
    private final ProfFieldService profFieldService;
    private final CurrencyService currencyService;
    private final WorkTypeService workTypeService;
    private final EducationService educationService;
    private final ResumeService resumeService;
    private final ClickService clickService;
    private final ResumeStateService resumeStateService;
    private final VacancyService vacancyService;
    private final PdfResumeWriter pdfResumeWriter;

    private final ImageUtil imageUtil;

    @Autowired @Lazy private ResumeEditController resumeEditController;
    @Autowired @Lazy private VacancyViewController vacancyViewController;
    @Autowired @Lazy private VacancyListViewController vacancyListViewController;
    @Autowired @Lazy private ResumeListViewController resumeListViewController;
    @Autowired @Lazy private ClickListViewController clickListViewController;

    private DecimalFormat decimalFormat = new DecimalFormat("0.00");

    @FXML private Pane resumePaneView;

    @FXML private Label lastNameLabel;
    @FXML private Label firstNameLabel;
    @FXML private Label middleNameLabel;
    @FXML private Label numberPhone;
    @FXML private Label email;
    @FXML private Label education;
    @FXML private Label experience;
    @FXML private Label address;
    @FXML private Label profession;
    @FXML private Label profField;
    @FXML private Label wage;
    @FXML private Label workType;

    @FXML private ScrollPane learningHistoryPane;
    @FXML private ScrollPane experienceHistoryPane;

    @FXML private TabPane resumeViewTabPane;

    @FXML private ImageView photoImageView;

    @FXML private Button buttonCancel;
    @FXML private Button clickButton;

    private Resume currentResume = null;
    private boolean isShow = false;

    public Button getButtonCancel() {
        return buttonCancel;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hide();
    }

    public void show() {
        isShow = true;
        resumePaneView.setVisible(true);
        resumePaneView.setManaged(true);
    }

    public boolean isShow() {
        return isShow;
    }

    public void show(AppFunctionalInterface function) {
        function.execute();
        this.show();
    }

    public void hide() {
        isShow = false;
        resumePaneView.setVisible(false);
        resumePaneView.setManaged(false);
    }

    public void hide(AppFunctionalInterface function) {
        function.execute();
        this.hide();
    }

    public void isDisableClickButton(boolean isDisable) {
        clickButton.setDisable(isDisable);
    }

    public void setData(Resume resume) {
        clear();
        if (resume != null) {
            this.currentResume = resume;
            String prefixNumberPhone = "Номер тел.: ";
            String prefixEmail = "Эл. почта: ";
            String prefixProfession = "Профессия: ";
            String prefixProfField = "Проф. область: ";
            String prefixWage = "Зарплата: ";
            String prefixWorkType = "График: ";
            String prefixEducation = "Образование: ";
            String prefixExperience = "Опыт паботы: ";
            String prefixAddress = "Адрес: ";

            Profile profile = resume.getProfile();
            if (profile != null) {
                lastNameLabel.setText(profile.getLastName());
                firstNameLabel.setText(profile.getFirstName());
                middleNameLabel.setText(profile.getMiddleName());
            }
            if (resume.getNumberPhone() != null) numberPhone.setText(prefixNumberPhone + resume.getNumberPhone().getNumber());
            email.setText(prefixEmail + resume.getEmail());
            if (!Strings.isNullOrEmpty(resume.getProfession())) {
                profession.setText(prefixProfession + resume.getProfession());
            } else {
                profession.setText(prefixProfession + "не указана");
            }

            if (resume.getProfField() != null) {
                profField.setText(prefixProfField + resume.getProfField().getNameView().getName());
            } else {
                profField.setText(prefixProfField + "не указана");
            }
            if (resume.getWage() != null) {
                String wageString = prefixWage;
                if (Objects.nonNull(resume.getWage().getCount())) {
                    if(resume.getWage().getCurrency() != null) {
                        Currency currency = resume.getWage().getCurrency();
                        wageString += decimalFormat.format(resume.getWage().getCountBigDecimal()) + " " + currency.getNameView().getName();
                    } else {
                        wageString += decimalFormat.format(resume.getWage().getCountBigDecimal());
                    }
                }
                if (wageString.length() <= prefixWage.length()) wageString += "не указана";
                wage.setText(wageString);
            } else {
                wage.setText(prefixWage + "не указана");
            }

            if (resume.getWorkType() != null) {
                WorkType workType1 = resume.getWorkType();
                workType.setText(prefixWorkType + workType1.getNameView().getName());
            } else {
                workType.setText(prefixWorkType + "не указан");
            }
            address.setText(prefixAddress + resume.getAddress().getHouse());
            if (Objects.nonNull(resume.getPhoto())) {
                setPhotoImage(resume.getPhoto().getImage());
            } else {
                setDefaultImage();
            }

            setDataLearningHistory(resume.getLearningHistoryList());
            setDataExperienceHistory(resume.getExperienceHistoryList());
        } else {
            logger.debug("Resume is null. We can't view resume...");
        }
    }

    public void setPhotoImage(byte[] image) {
        InputStream targetStream = new ByteArrayInputStream(image);
        javafx.scene.image.Image img = new javafx.scene.image.Image(targetStream);
        photoImageView.setImage(img);
    }

    public void setPhotoImageById(Long id) {
        ImageContainer imageContainer = imageContainerService.findById(id);
        this.setPhotoImage(imageContainer.getImage());
    }

    public void setDefaultImage() {
        byte[] imageBytes = imageUtil.getDefaultResumeImage();
        if (Objects.nonNull(imageBytes) && imageBytes.length > 0) {
            InputStream inputStream = new ByteArrayInputStream(imageBytes);
            javafx.scene.image.Image defaultImage = new javafx.scene.image.Image(
                    inputStream,
                    300,
                    300,
                    false,
                    false);
            photoImageView.setImage(defaultImage);
        }
    }

    private void setDataLearningHistory(List<LearningHistory> list) {
        VBox pane = new VBox();
        list.stream().forEach(learningHistory -> {
            if (pane.getChildren().size() > 0) {
                final Separator separator = new Separator();
                pane.getChildren().add(separator);
            }
            VBox wrap = new VBox();
            VBox.setMargin(wrap, new Insets(10.0, 0.0, 10.0, 0.0));
            wrap.getChildren().add(new Label(learningHistory.getEducation().getNameView().getName()));
            wrap.getChildren().add(new Label(learningHistory.getDescription()));
            pane.getChildren().add(wrap);
        });
        learningHistoryPane.setContent(pane);
    }

    private void setDataExperienceHistory(List<ExperienceHistory> list) {
        VBox pane = new VBox();
        list.stream().forEach(experienceHistory -> {
            if (pane.getChildren().size() > 0) {
                final Separator separator = new Separator();
                pane.getChildren().add(separator);
            }
            VBox wrap = new VBox();
            VBox.setMargin(wrap, new Insets(10.0, 0.0, 10.0, 0.0));

            Integer age;
            if (Objects.nonNull(experienceHistory.getDateStart())) {
                LocalDate start = experienceHistory.getDateStart();
                LocalDate end = Objects.nonNull(experienceHistory.getDateEnd()) ? experienceHistory.getDateEnd() : null;
                age = TimeUtil.calAge(start, end);
            } else {
                age = null;
            }

            String experience = age == null || age <= 0 ? "без опыта" : age + " " + TextUtil.nameForYears(age);

            wrap.getChildren().add(new Label("Опыт работы: " + experience));
            wrap.getChildren().add(new Label(experienceHistory.getDescription()));
            pane.getChildren().add(wrap);
        });
        experienceHistoryPane.setContent(pane);
    }

    private Long getResumeId() {
        return currentResume.getId();
    }

    @FXML
    public void actionCancel(ActionEvent event) {
        resumeListViewController.clearSelection();
        clickListViewController.clearSelection();
        hide();
    }

    @FXML
    public void actionSavePdf(ActionEvent event) {
        final DateTimeFormatter formatter = DateTimeFormat.forPattern("YYYYMMddhhmmss");

        Resume resume = resumeService.findById(getResumeId());
        FileChooser fileChooser = new FileChooser();
        final Long resumeId = Objects.nonNull(resume.getId()) ? resume.getId() : 1L;
        final String currentDateTime = LocalDateTime.now().toString(formatter);
        final String fileName = String.format("resume_%s_%s.pdf", resumeId, currentDateTime);
        fileChooser.setInitialFileName(fileName);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("PDF", "*.pdf")
        );

        File file = fileChooser.showSaveDialog(ApplicationJavaFX.PARENT_STAGE);
        if (file != null) {
            String path = file.getAbsoluteFile().getAbsolutePath();
            Map props = new HashMap<>();
            props.put(PdfResumeWriter.props.PATH, path);
            props.put(PdfResumeWriter.props.RESUME, resume);
            pdfResumeWriter.write(props);
        }
    }

    @FXML
    public void actionEdit(ActionEvent event) throws Exception {
        Resume resume = resumeService.findById(getResumeId());
        resumeEditController.setData(resume);
        resumeEditController.show();
    }

    @FXML
    public void actionDelete(ActionEvent event) {
        resumeService.deleteById(currentResume.getId());
        resumeListViewController.actionUpdate(event);
        if (vacancyListViewController != null) vacancyListViewController.actionUpdate(event);
        hide();
        clear();
    }

    @FXML
    public void actionClick(ActionEvent event) {
        ResumeState resumeState = resumeStateService.findById(1L);
        Vacancy vacancy = vacancyViewController.getCurrentVacancy();

        Set<Click> clicks = vacancy.getClicks().stream()
                .filter(Objects::nonNull)
                .filter(click1 -> Objects.nonNull(click1.getResume()))
                .filter(click1 -> click1.getResume().getId().equals(this.currentResume.getId()))
                .collect(Collectors.toSet());

        if (vacancy != null && clicks.isEmpty()) {
            Click click = Click.builder()
                    .resume(this.currentResume)
                    .vacancy(vacancy)
                    .resumeState(resumeState)
                    .build();
            clickService.create(click);
            vacancyListViewController.actionUpdate(event);
        } else {
            Messager.getInstance().sendMessage("Резюме уже прикреплено к данной вакансии...");
        }
    }

    public void clear() {
        this.currentResume = null;
        resumeViewTabPane.getSelectionModel().select(0);
        setDefaultImage();
    }

}
