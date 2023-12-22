package bo.custom.impl;

import bo.custom.OrderBo;
import dao.custom.OrderDao;
import dao.custom.impl.OrderDaoImpl;
import dto.OrderDto;

import java.sql.SQLException;

public class OrderBoImpl implements OrderBo {
    private OrderDao orderDao = new OrderDaoImpl();
    @Override
    public boolean saveOrder(OrderDto dto) throws SQLException, ClassNotFoundException {
        return orderDao.save(dto);
    }
    @Override
    public OrderDto getLastOrder() throws SQLException, ClassNotFoundException {
        return orderDao.getLastOrder();
    }
}
