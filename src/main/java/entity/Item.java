package entity;

import dto.ItemDto;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class Item extends ItemDto {
    private String code;
    private String description;
    private double unitPrice;
    private  int qtyOnHand;
}
