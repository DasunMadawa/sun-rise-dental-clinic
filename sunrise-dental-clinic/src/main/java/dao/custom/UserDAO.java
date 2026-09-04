package dao.custom;

import dao.CrudDAO;
import model.DentistModel;
import model.UserModel;

import java.util.List;

public interface UserDAO extends CrudDAO<UserModel> {
    public List<DentistModel> getAllDentists() throws Exception;
    public String generateNextUserId() throws Exception;
    public String generateNextStaffId() throws Exception;
    public String generateNextDentistId() throws Exception;

}
