package dao.custom;

import dao.CrudDAO;
import model.AppointmentModel;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentDAO extends CrudDAO<AppointmentModel> {
    public List<AppointmentModel> getByDentistAndDate(String dentistId, LocalDate date) throws Exception;
    public AppointmentModel getLatestForPatient(String patientId) throws Exception;
    public String generateNextId() throws Exception;

}
