package ru.iworking.personnel.reserve.controller;

import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.iworking.personnel.reserve.ApplicationJavaFX;
import ru.iworking.personnel.reserve.component.Messager;
import ru.iworking.personnel.reserve.entity.*;
import ru.iworking.personnel.reserve.model.CompanyTypeCellFactory;
import ru.iworking.personnel.reserve.model.NumberPhoneFormatter;
import ru.iworking.personnel.reserve.service.CompanyService;
import ru.iworking.personnel.reserve.service.CompanyTypeService;
import ru.iworking.personnel.reserve.service.ImageContainerService;
import ru.iworking.personnel.reserve.utils.ImageUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

@Component
@RequiredArgsConstructor
public class CompanyEditController implements Initializable {

    private static final Logger logger = LogManager.getLogger(CompanyEditController.class);

    @FXML private VBox companyEdit;

    @FXML private ComboBox<CompanyType> companyTypeComboBox;
    @FXML private TextField nameCompanyTextField;
    @FXML private TextField numberPhoneTextField;
    @FXML private TextField webPageTextField;
    @FXML private TextField emailTextField;
    @FXML private TextField countryTextField;
    @FXML private TextField regionTextField;
    @FXML private TextField cityTextField;
    @FXML private TextField streetTextField;
    @FXML private TextField houseTextField;

    @FXML private ImageView imageView;

    private NumberPhoneFormatter numberPhoneFormatter = new NumberPhoneFormatter();

    private CompanyTypeCellFactory companyTypeCellFactory = new CompanyTypeCellFactory();

    private final CompanyTypeService companyTypeService;
    private final CompanyService companyService;
    private final ImageContainerService imageContainerService;

    private final ImageUtil imageUtil;

    @Autowired @Lazy private CompanyListViewController companyListViewController;

    private Company currentCompany = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hide();
        numberPhoneTextField.setTextFormatter(numberPhoneFormatter);

        companyTypeComboBox.setButtonCell(companyTypeCellFactory.call(null));
        companyTypeComboBox.setCellFactory(companyTypeCellFactory);
        companyTypeComboBox.setItems(FXCollections.observableList(companyTypeService.findAll()));

        final Circle clip = new Circle(75, 75, 70);
        imageView.setClip(clip);
    }

    @FXML
    public void actionSave(ActionEvent event) {
        if (isValid()) {
            Long companyId = currentCompany != null ? currentCompany.getId() : null;
            Company company = save();
            hide();
            clear();
            companyListViewController.actionUpdate(event);
            companyListViewController.selectCompany(company);
            if (companyId == null) companyListViewController.createVacancyListViewPane(company);
        } else {
            Messager.getInstance().sendMessage("Не введены обязательные поля...");
            logger.debug("Fields company edit block is not valid...");
        }
    }

    public void show() {
        companyEdit.setVisible(true);
        companyEdit.setManaged(true);
    }

    public void hide() {
        companyEdit.setVisible(false);
        companyEdit.setManaged(false);
    }

    public void setData(Company company) {
        if (company != null) {
            currentCompany = company;
            Long companyTypeId = company.getCompanyTypeId();
            if (companyTypeId != null) companyTypeComboBox.setValue(companyTypeService.findById(companyTypeId));
            nameCompanyTextField.setText(company.getName());
            NumberPhone numberPhone = company.getNumberPhone();
            if (numberPhone != null) numberPhoneTextField.setText(numberPhone.getNumber());
            webPageTextField.setText(company.getWebPage());
            emailTextField.setText(company.getEmail());
            Address address = company.getAddress();
            if (address != null) {
                countryTextField.setText(address.getCountry());
                regionTextField.setText(address.getRegion());
                cityTextField.setText(address.getCity());
                streetTextField.setText(address.getStreet());
                houseTextField.setText(address.getHouse());
            }
            if (company.getLogo() != null) {
                setLogoImage(company.getLogo());
            } else {
                setDefaultImage();
            }
        } else {
            clear();
            logger.debug("company is null..");
        }
    }

    public Company save() {
        Long companyId = null;
        if (currentCompany != null) companyId = currentCompany.getId();

        CompanyType companyType = companyTypeComboBox.getValue();
        String nameCompanyStr = nameCompanyTextField.getText();
        String numberPhoneStr = numberPhoneTextField.getText();
        String webPageStr = webPageTextField.getText();
        String emailStr = emailTextField.getText();

        String countryStr = countryTextField.getText();
        String regionStr = regionTextField.getText();
        String cityStr = cityTextField.getText();
        String streetStr = streetTextField.getText();
        String houseStr = houseTextField.getText();

        NumberPhone numberPhone = new NumberPhone();
        numberPhone.setNumber(numberPhoneStr);


        Company company;
        if (companyId == null) {
            company = new Company();
        } else {
            company = companyService.findById(companyId);
        }

        if (companyType != null) company.setCompanyTypeId(companyType.getId());
        company.setName(nameCompanyStr);
        company.setNumberPhone(numberPhone);
        company.setWebPage(webPageStr);
        company.setEmail(emailStr);
        if (company.getAddress() == null) company.setAddress(new Address());
        company.getAddress().setCountry(countryStr);
        company.getAddress().setRegion(regionStr);
        company.getAddress().setCity(cityStr);
        company.getAddress().setStreet(streetStr);
        company.getAddress().setHouse(houseStr);

        ImageContainer logo = null;

        try(ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            BufferedImage originalImage = SwingFXUtils.fromFXImage(imageView.getImage(), null);
            ImageIO.write(originalImage, "png", stream);

            logo = new ImageContainer(imageUtil.scaleToSize(stream.toByteArray(), null,200));
            company.setLogo(logo);
        } catch (IOException e) {
            logger.error(e);
        }

        if (companyId == null) {
            companyService.create(company);
        } else {
            companyService.update(company);
        }
        logger.debug("Created new company: " + company.toString());

        return company;
    }

    private Boolean isValid() {
        Boolean isValid = true;
        if (companyTypeComboBox.getValue() == null) {
            companyTypeComboBox.getStyleClass().add("has-error");
            isValid = false;
        }
        if (nameCompanyTextField.getText() == null || nameCompanyTextField.getText().length() <= 0) {
            nameCompanyTextField.getStyleClass().add("has-error");
            isValid = false;
        }
        return isValid;
    }

    public void setLogoImage(ImageContainer logo) {
        InputStream targetStream = new ByteArrayInputStream(logo.getImage());
        javafx.scene.image.Image img = new javafx.scene.image.Image(targetStream);
        imageView.setImage(img);
    }

    public void setDefaultImage() {
        byte[] imageBytes = imageUtil.getDefaultCompanyImage();
        if (Objects.nonNull(imageBytes) && imageBytes.length > 0) {
            InputStream inputStream = new ByteArrayInputStream(imageBytes);
            javafx.scene.image.Image defaultImage = new javafx.scene.image.Image(
                    inputStream,
                    150,
                    150,
                    false,
                    false);
            imageView.setImage(defaultImage);
        }
    }

    public void clear() {
        currentCompany = null;
        companyTypeComboBox.setValue(null);
        companyTypeComboBox.getStyleClass().remove("has-error");
        nameCompanyTextField.setText("");
        nameCompanyTextField.getStyleClass().remove("has-error");
        numberPhoneTextField.setText("");
        webPageTextField.setText("");
        emailTextField.setText("");
        countryTextField.setText("");
        regionTextField.setText("");
        cityTextField.setText("");
        streetTextField.setText("");
        houseTextField.setText("");
        setDefaultImage();
    }

    @FXML
    public void actionCancel(ActionEvent event) {
        hide();
        clear();
    }

    @FXML
    private void actionLoadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("GIF", "*.gif")
        );

        File file = fileChooser.showOpenDialog(ApplicationJavaFX.PARENT_STAGE);
        if (file != null) {
            try {
                javafx.scene.image.Image img = new javafx.scene.image.Image(file.toURI().toString());
                imageView.setImage(img);
            } catch (Exception ex) {
                logger.error(ex);
            }
        }
    }

    public void reload(ActionEvent event) {
        actionCancel(event);
        companyTypeComboBox.setItems(FXCollections.observableList(companyTypeService.findAll()));
    }
    
}
