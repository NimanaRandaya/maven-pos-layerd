package dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class ItemDto {
    private String code;
    private String description;
    private double unitPrice;
    private int qty;

}
