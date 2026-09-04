package dao.custom;

import dao.CrudDAO;
import model.PaymentModel;

public interface PaymentDAO extends CrudDAO<PaymentModel> {
    public PaymentModel searchByAppointmentNo(String appointmentNo) throws Exception;
    public String generateNextId() throws Exception;

}
