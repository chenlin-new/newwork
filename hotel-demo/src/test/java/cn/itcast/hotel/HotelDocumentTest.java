package cn.itcast.hotel;

import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.pojo.HotelDoc;
import cn.itcast.hotel.service.IHotelService;
import com.alibaba.fastjson.JSON;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author LiChenLin
 * @Date 2022/5/3 20:51
 */
@SpringBootTest(classes = HotelDemoApplication.class)
public class HotelDocumentTest {

    private RestHighLevelClient client;


    @Autowired
    private IHotelService service;

    @BeforeEach
    public void before() {
        //创建客户端
        this.client = new RestHighLevelClient(RestClient.builder(new HttpHost("192.168.200.128", 9200)));
    }

    //添加单条索引数据
    @Test
    public void add() throws IOException {
//        //先从mysql数据库中查询数据
//        Hotel hotel = service.getById(36934);
//        //封装es的数据模型中
//        HotelDoc hotelDoc = new HotelDoc(hotel);
//        //转成json
//        String json = JSON.toJSONString(hotelDoc);
//        //获取连接对象,
//        IndexRequest request=new IndexRequest("hotel").id(hotel.getId().toString());
//        //准备数据
//        request.source(json, XContentType.JSON);
//        //发送请求
//        client.index(request, RequestOptions.DEFAULT);

        //获取mysql中的数据
        Hotel hotel = service.getById(38609);
        //封装到es对象中
        HotelDoc hotelDoc = new HotelDoc(hotel);
        //转成json格式
        String json = JSON.toJSONString(hotelDoc);
        //获取连接对象
        IndexRequest request = new IndexRequest("hotel").id(hotel.getId().toString());
        //准备数据
        request.source(json, XContentType.JSON);
        //发送请求
        client.index(request, RequestOptions.DEFAULT);
    }

    //查询数据
    @Test
    public void select() throws IOException {
        //获取请求对象
        GetRequest request = new GetRequest("hotel").id("38609");

        //发送请求
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        //解析对象成json
        String json = response.getSourceAsString();

        HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);
        System.out.println(hotelDoc);
    }

    //删除数据
    @Test
    public void delete() throws IOException {
        //获取请求对象
        DeleteRequest request = new DeleteRequest("hotel").id("38609");

        //发送请求
        DeleteResponse delete = client.delete(request, RequestOptions.DEFAULT);

        System.out.println(delete);

    }

    //修改用户数据
    @Test
    public void update() throws IOException {
        //获取请求对象
        UpdateRequest request = new UpdateRequest("hotel", "38609");
        Map map = new HashMap();
        map.put("price", "200");
        request.doc(map, XContentType.JSON);

        client.update(request, RequestOptions.DEFAULT);
        //发送请求

    }


    //批量导入用户数据
    @Test
    public void addList() throws IOException {

        List<Hotel> list = service.list();
        BulkRequest bulkRequest = new BulkRequest("hotel");
        for (Hotel hotel : list) {
            HotelDoc hotelDoc = new HotelDoc(hotel);
            String json = JSON.toJSONString(hotelDoc);
           //获取连接对象
            IndexRequest request=new IndexRequest("hotel").id(hotel.getId().toString());
            request.source(json,XContentType.JSON);
           bulkRequest.add(request);
        }

    }

    @AfterEach
    public void after() throws IOException {
        client.close();
    }
}
