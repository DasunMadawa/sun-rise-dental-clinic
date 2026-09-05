package model;

import dao.DAOFactory;
import dao.custom.TreatmentTypeDAO;

import java.util.List;

public class TreatmentTypeModel {
    private static final TreatmentTypeDAO treatmentTypeDAO = (TreatmentTypeDAO) DAOFactory.getDAOFactory().getDAO(DAOFactory.DAOTypes.TREATMENT);

    private String code;
    private String name;
    private double unitCost;
    private boolean isPerTooth;
    private int durationMinutes;

    public TreatmentTypeModel() {

    }

    public TreatmentTypeModel(String code, String name, double unitCost, boolean isPerTooth, int durationMinutes) {
        this.code = code;
        this.name = name;
        this.unitCost = unitCost;
        this.isPerTooth = isPerTooth;
        this.durationMinutes = durationMinutes;
    }

    public static TreatmentTypeModel search(String code) throws Exception {
        return treatmentTypeDAO.search(code);
    }

    public static List<TreatmentTypeModel> getAll() throws Exception {
        return treatmentTypeDAO.getAll();
    }

    public static boolean delete(String code) throws Exception {
        return treatmentTypeDAO.delete(code);
    }

    public static TreatmentTypeModel fromCodeOrDefault(String code) throws Exception {
        if (code != null) {
            TreatmentTypeModel found = search(code);
            if (found != null) {
                return found;
            }
        }
        return search("CHECKING");
    }

    public boolean save() throws Exception {
        return treatmentTypeDAO.add(this);
    }

    public boolean update() throws Exception {
        return treatmentTypeDAO.update(this);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
    }

    public boolean isPerTooth() {
        return isPerTooth;
    }

    public void setPerTooth(boolean perTooth) {
        isPerTooth = perTooth;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

}
