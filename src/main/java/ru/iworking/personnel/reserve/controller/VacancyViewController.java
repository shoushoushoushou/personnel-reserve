package ru.iworking.personnel.reserve.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.iworking.personnel.reserve.entity.Currency;
import ru.iworking.personnel.reserve.entity.Vacancy;
import ru.iworking.personnel.reserve.entity.Wage;
import ru.iworking.personnel.reserve.service.CurrencyService;
import ru.iworking.personnel.reserve.service.EducationService;
import ru.iworking.personnel.reserve.service.ProfFieldService;
import ru.iworking.personnel.reserve.service.WorkTypeService;

import java.net.URL;
import java.util.ResourceBundle;

public class VacancyViewController extends FxmlController{

    private static final Logger logger = LogManager.getLogger(VacancyViewController.class);

    @FXML private VBox vacancyView;

    @FXML private Label professionLabel;
    @FXML private Label profFieldLabel;
    @FXML private Label workTypeLabel;
    @FXML private Label educationLabel;
    @FXML private Label wageLabel;
    @FXML private Label addressLabel;

    private ProfFieldService profFieldService = ProfFieldService.INSTANCE;
    private WorkTypeService workTypeService = WorkTypeService.INSTANCE;
    private EducationService educationService = EducationService.INSTANCE;
    private CurrencyService currencyService = CurrencyService.INSTANCE;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hide();
    }

    public void show() {
        vacancyView.setVisible(true);
        vacancyView.setManaged(true);
    }

    public void hide() {
        vacancyView.setVisible(false);
        vacancyView.setManaged(false);
    }

    public void setData(Vacancy vacancy) {
        if (vacancy != null) {
            String prefixProfession = "Профессия: ";
            String prefixProfField = "Проф. область: ";
            String prefixWorkType = "График работы: ";
            String prefixEducation = "Образование: ";
            String prefixWage = "Зарплата: ";
            String prefixAddress = "Адрес: ";

            professionLabel.setText(prefixProfession + vacancy.getProfession());

            Long profFieldId = vacancy.getProfFieldId();
            profFieldLabel.setText(
                    profFieldId != null ?
                            prefixProfField + profFieldService.findById(profFieldId).getNameView().getName() :
                            prefixProfField + "не указана");

            Long workTypeId = vacancy.getWorkTypeId();
            workTypeLabel.setText(
                    workTypeId != null ?
                            prefixWorkType + workTypeService.findById(workTypeId).getNameView().getName() :
                            prefixWorkType + "не указан");

            Long educationId = vacancy.getEducationId();
            educationLabel.setText(
                    educationId != null ?
                            prefixEducation + educationService.findById(educationId).getNameView().getName() :
                            prefixEducation + "не указано");

            Wage wage = vacancy.getWage();
            if (wage != null) {
                String wageStr = prefixWage + wage.getCount();
                Long currencyId = vacancy.getWage().getCurrencyId();
                if (currencyId != null) {
                    Currency currency = currencyService.findById(currencyId);
                    wageStr += " " + currency.getNameView().getName();
                }
                wageLabel.setText(wageStr);
            }

            addressLabel.setText(
                    vacancy.getAddress() != null ?
                        prefixAddress + vacancy.getAddress().getStreet() :
                        prefixAddress + "не указан");

        } else {
            logger.debug("Vacancy is null. We can't view vacancy...");
        }
    }

}
