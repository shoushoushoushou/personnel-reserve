package ru.iworking.personnel.reserve.controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.iworking.personnel.reserve.dao.CompanyDao;
import ru.iworking.personnel.reserve.dao.CompanyTypeDao;
import ru.iworking.personnel.reserve.entity.Address;
import ru.iworking.personnel.reserve.entity.Company;
import ru.iworking.personnel.reserve.entity.CompanyType;
import ru.iworking.personnel.reserve.entity.NumberPhone;
import ru.iworking.personnel.reserve.model.CompanyTypeCellFactory;
import ru.iworking.personnel.reserve.model.NumberPhoneFormatter;
import ru.iworking.service.api.utils.LocaleUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class VacanciesPaneFxmlController implements Initializable {

    private static final Logger logger = LogManager.getLogger(VacanciesPaneFxmlController.class);

    private NumberPhoneFormatter numberPhoneFormatter = new NumberPhoneFormatter();

    private CompanyTypeDao companyTypeDao = CompanyTypeDao.getInstance();
    private CompanyDao companyDao = CompanyDao.getInstance();

    @FXML private Button editCompanyButton;
    @FXML private Button deleteCompanyButton;

    @FXML private TableView<Company> tableCompanies;
    @FXML private TableColumn<Company, String> companyTypeColumn;
    @FXML private TableColumn<Company, String> companyNameColumn;

    @FXML private VBox companyEditBlock;
    @FXML private ComboBox<CompanyType> companyTypeComboBox;
    @FXML private TextField nameCompanyTextField;
    @FXML private TextField numberPhoneTextField;
    @FXML private TextField webPageTextField;
    @FXML private TextField emailTextField;
    @FXML private TextArea addressTextArea;

    private CompanyTypeCellFactory companyTypeCellFactory = new CompanyTypeCellFactory();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        numberPhoneTextField.setTextFormatter(numberPhoneFormatter);

        companyTypeComboBox.setButtonCell(companyTypeCellFactory.call(null));
        companyTypeComboBox.setCellFactory(companyTypeCellFactory);
        companyTypeComboBox.setItems(FXCollections.observableList(companyTypeDao.findAll()));

        companyTypeColumn.setCellValueFactory(cellData -> {
            CompanyType companyType = companyTypeDao.find(cellData.getValue().getCompanyTypeId());
            String textColumn = companyType != null ? companyType.getAbbreviatedNameToView(LocaleUtil.getDefault()) : "не указан";
            return new ReadOnlyStringWrapper(textColumn);
        });
        companyNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableCompanies.setItems(FXCollections.observableList(companyDao.findAll()));
        tableCompanies.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            editCompanyButton.setDisable(false);
            deleteCompanyButton.setDisable(false);
        });
    }

    @FXML
    private void actionButtonCreateCompany(ActionEvent event) {
        companyEditBlock.setVisible(true);
    }

    @FXML
    private void actionButtonUpdateCompaniesTable(ActionEvent event) {
        clearSelectionModelCompaniesTable();
        tableCompanies.setItems(FXCollections.observableList(companyDao.findAll()));
        logger.debug("Companies table has been updated...");
    }

    @FXML
    private void actionButtonCancelCreateCompany(ActionEvent event) {
        companyEditBlock.setVisible(false);
        clearCompanyEditBlock();
    }

    @FXML
    private void  actionButtonSaveCompany(ActionEvent event) {
        if (isValidFieldsCompanyEditBlock()) {
            CompanyType companyType = companyTypeComboBox.getValue();
            String nameCompanyStr = nameCompanyTextField.getText();
            String numberPhoneStr = numberPhoneTextField.getText();
            String webPageStr = webPageTextField.getText();
            String emailStr = emailTextField.getText();
            String addressStr = addressTextArea.getText();

            NumberPhone numberPhone = new NumberPhone();
            numberPhone.setNumber(numberPhoneStr);

            Address address = new Address();
            address.setStreet(addressStr);

            Company company = new Company();
            if (companyType != null) company.setCompanyTypeId(companyType.getId());
            company.setName(nameCompanyStr);
            company.setNumberPhone(numberPhone);
            company.setWebPage(webPageStr);
            company.setEmail(emailStr);
            company.setAddress(address);

            companyDao.create(company);
            logger.debug("Created new company: " + company.toString());
            companyEditBlock.setVisible(false);
            clearCompanyEditBlock();
            actionButtonUpdateCompaniesTable(event);
        } else {
            logger.debug("Fields company edit block is not valid...");
        }
    }

    public void updateCompanyTable(ActionEvent event) {
        actionButtonUpdateCompaniesTable(event);
    }

    public void updateCompanyEditBlock(ActionEvent event) {
        actionButtonCancelCreateCompany(event);
        companyTypeComboBox.setItems(FXCollections.observableList(companyTypeDao.findAll()));
    }

    private void clearSelectionModelCompaniesTable() {
        tableCompanies.getSelectionModel().clearSelection();
        editCompanyButton.setDisable(true);
        deleteCompanyButton.setDisable(true);
    }

    private Boolean isValidFieldsCompanyEditBlock() {
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

    public void clearCompanyEditBlock() {
        companyTypeComboBox.setValue(null);
        companyTypeComboBox.getStyleClass().remove("has-error");
        nameCompanyTextField.setText("");
        nameCompanyTextField.getStyleClass().remove("has-error");
        numberPhoneTextField.setText("");
        webPageTextField.setText("");
        emailTextField.setText("");
        addressTextArea.setText("");
    }

    public void reload(ActionEvent event) {
        updateCompanyTable(event);
        updateCompanyEditBlock(event);
    }

}
