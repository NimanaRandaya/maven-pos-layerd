package bo.custom.impl;

import bo.custom.ItemBo;
import dao.custom.ItemDao;
import dao.custom.impl.ItemDaoImpl;
import dto.ItemDto;
import entity.Item;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemBoImpl implements ItemBo<ItemDto>{
    private ItemDao itemDao = new ItemDaoImpl();

    @Override
    public boolean saveItem(ItemDto dto) throws SQLException, ClassNotFoundException {
        return itemDao.save(new Item(
                dto.getCode(),
                dto.getDescription(),
                dto.getUnitPrice(),
                dto.getQty()
        ));
    }

    @Override
    public boolean updateItem(ItemDto dto) throws SQLException, ClassNotFoundException {
        return itemDao.update(new Item(
                dto.getCode(),
                dto.getDescription(),
                dto.getUnitPrice(),
                dto.getQty()
        ));
    }

    @Override
    public boolean deleteItem(String code) throws SQLException, ClassNotFoundException {
        return itemDao.delete(code);
    }

    @Override
    public List<ItemDto> allItems() throws SQLException, ClassNotFoundException {
        List<Item> itemlist = itemDao.getAll();
        List<ItemDto> list = new ArrayList<>();
        for (Item item:itemlist){
            list.add(new ItemDto(
                    item.getCode(),
                    item.getDescription(),
                    item.getUnitPrice(),
                    item.getQtyOnHand()
            ));
        }
        return list;
    }

    @Override
    public ItemDto getItem(String code) {
        return null;
    }
}
