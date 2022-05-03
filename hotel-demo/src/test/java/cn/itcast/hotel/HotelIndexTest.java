package cn.itcast.hotel;

import cn.itcast.hotel.constants.HotelIndexConstants;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

/**
 * @Author LiChenLin
 * @Date 2022/5/3 15:30
 */
@SpringBootTest(classes = HotelDemoApplication.class)
public class HotelIndexTest {
    private RestHighLevelClient client;

    @BeforeEach
    public void before() {
        //创建客户端
        this.client = new RestHighLevelClient(RestClient.builder(new HttpHost("192.168.200.128", 9200)));
    }

    //创建索引库
    @Test
    public void indexCreate() throws IOException {
//        //创建索引库对象
//        CreateIndexRequest request = new CreateIndexRequest("hotel");
//
//        //准备请求的参数
//        request.source(HotelIndexConstants.MAPPING_TEMPLATE, XContentType.JSON);
//        //发送请求
//        client.indices().create(request, RequestOptions.DEFAULT);
        //创建爱你请求对象
        CreateIndexRequest request = new CreateIndexRequest("hotel");
        //准备参数
        request.source(HotelIndexConstants.MAPPING_TEMPLATE, XContentType.JSON);
        //发送请求
        client.indices().create(request, RequestOptions.DEFAULT);
    }

    //删除索引库
    @Test
    public void deletelIndex() throws IOException {
        //获取连接
            DeleteIndexRequest request=new DeleteIndexRequest("hotel");

        //发送删除请求

        client.indices().delete(request,RequestOptions.DEFAULT);
    }


    @AfterEach
    public void after() throws IOException {
        client.close();
    }
}
