package dao.custom;

import dao.CrudDAO;
import model.PatientModel;

public interface PatientDAO extends CrudDAO<PatientModel> {
    public PatientModel searchByNic(String nic) throws Exception;
    public String generateNextId() throws Exception;

}
