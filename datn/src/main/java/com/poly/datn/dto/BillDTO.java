package com.poly.datn.dto;

import java.time.LocalDateTime;
import java.util.List;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class BillDTO {
    private Integer id;
    private LocalDateTime date;
    private Integer userId;
    private Double total;
    private String statusName;
    private String customerName;
    private String numberPhone;
    private String address;
    private List<BillDetailDTO> billDetails;
}

