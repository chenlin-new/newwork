package cn.itcast.hotel.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author LiChenLin
 * @Date 2022/5/5 21:21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult {
    private  Integer total;
    private List<HotelDoc> hotels;


}
