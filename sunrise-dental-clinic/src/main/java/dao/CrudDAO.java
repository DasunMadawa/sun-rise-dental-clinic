package dao;

import java.util.List;

public interface CrudDAO<T> extends SuperDAO {
    public boolean add(T t) throws Exception;
    public T search(String id) throws Exception;
    public boolean update(T t) throws Exception;
    public boolean delete(String id) throws Exception;
    public List<T> getAll() throws Exception;

}
