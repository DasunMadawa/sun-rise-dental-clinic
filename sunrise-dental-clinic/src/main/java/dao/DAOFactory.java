package dao;

import dao.custom.impl.*;

public class DAOFactory {
    private static DAOFactory daoFactory;

    private DAOFactory() {

    }

    public static DAOFactory getDAOFactory() {
        return daoFactory == null ? daoFactory = new DAOFactory() : daoFactory;
    }

    public enum DAOTypes {
        PATIENT, USER, APPOINTMENT, PAYMENT, QUERY, TREATMENT
    }

    public SuperDAO getDAO(DAOTypes daoTypes) {
        switch (daoTypes) {
            case PATIENT: return new PatientDAOImpl();
            case USER: return new UserDAOImpl();
            case APPOINTMENT: return new AppointmentDAOImpl();
            case PAYMENT: return new PaymentDAOImpl();
            case QUERY: return new QueryDAOImpl();
            case TREATMENT: return new TreatmentTypeDAOImpl();
            default: return null;

        }

    }

}
