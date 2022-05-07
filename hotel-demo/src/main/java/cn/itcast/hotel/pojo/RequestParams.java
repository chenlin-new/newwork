package cn.itcast.hotel.pojo;

import lombok.Data;

/**
 * @Author LiChenLin
 * @Date 2022/5/5 21:19
 */
@Data
public class RequestParams {
    private String  key;
    private  Integer page;
    private  Integer size;
    private String sortBy;
    //多条件查询,新增字段
    private String city;
    private String brand;
    private  String starName;
    private Integer maxPrice;
    private Integer minPrice;

    // 我当前的地理坐标
    private String location;
}
