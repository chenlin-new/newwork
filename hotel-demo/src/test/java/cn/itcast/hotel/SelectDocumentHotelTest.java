package cn.itcast.hotel;

import cn.itcast.hotel.pojo.HotelDoc;
import cn.itcast.hotel.service.IHotelService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.SneakyThrows;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Map;

/**
 * @Author LiChenLin
 * @Date 2022/5/5 19:44
 */
@SpringBootTest
public class SelectDocumentHotelTest {

    private RestHighLevelClient client;


    @Autowired
    private IHotelService service;

    @BeforeEach
    public void before() {
        //创建客户端
        this.client = new RestHighLevelClient(RestClient.builder(new HttpHost("192.168.200.128", 9200)));
    }

    //查询所有
    @SneakyThrows
    @Test
    public void aggsTest() {
        //创建查询对象
        SearchRequest request = new SearchRequest("hotel");

        //设置查询方式,准备dsl
        request.source().query(QueryBuilders.matchAllQuery());

        SearchResponse search = client.search(request, RequestOptions.DEFAULT);

        handleResponse(search);
    }


    //查询所有
    @SneakyThrows
    @Test
    public void matchAll() {
        //创建查询对象
        SearchRequest request = new SearchRequest("hotel");

        //设置查询方式,准备dsl
        request.source().query(QueryBuilders.matchAllQuery());

        SearchResponse search = client.search(request, RequestOptions.DEFAULT);

        handleResponse(search);
    }

    //match查询
    @SneakyThrows
    @Test
    public void match() {
        //创建查询对象
        SearchRequest request = new SearchRequest("hotel");
        //设置查询方式,准备dsl
        request.source().query(QueryBuilders
                .matchQuery("all", "如家"));
        //发送请求
        SearchResponse search = client.search(request, RequestOptions.DEFAULT);
        //处理参数
        handleResponse(search);
    }

    //match查询
    @SneakyThrows
    @Test
    public void matchi() {
        //创建查询对象
        SearchRequest request = new SearchRequest("hotel");
        //设置查询方式,准备dsl
        request.source().query(QueryBuilders
//                .multiMatchQuery("如家", "name"));
                .termQuery("city", "上海")); //3.精确查询term,
//                .rangeQuery("price").gte(100).lte(200));//3.范围查询range
        //发送请求
        SearchResponse search = client.search(request, RequestOptions.DEFAULT);
        //处理参数
        handleResponse(search);
    }

    //bool查询
    @SneakyThrows
    @Test
    public void boole() {
        //创建查询对象
        SearchRequest request = new SearchRequest("hotel");
        //设置查询方式,准备dsl
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.matchQuery("name", "如家酒店"));
        boolQuery.filter(QueryBuilders.termQuery("city", "上海"));
        boolQuery.filter(QueryBuilders.rangeQuery("price").gte(100).lte(200));
        request.source().query(boolQuery);
        SearchResponse search = client.search(request, RequestOptions.DEFAULT);
        //处理参数
        handleResponse(search);
    }

    //    5.排序、分页
    @SneakyThrows
    @Test
    public void order() {
        // 页码，每页大小
        int page = 1, size = 2;
        //创建查询对象
        SearchRequest request = new SearchRequest("hotel");
        //设置查询方式,准备dsl
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.matchQuery("name", "如家酒店"));
        boolQuery.filter(QueryBuilders.termQuery("city", "上海"));
        boolQuery.filter(QueryBuilders.rangeQuery("price").gte(100).lte(200));
        request.source().query(boolQuery)
                .sort("price", SortOrder.ASC)//排序
                .from((page - 1) * size).size(size);//分页

        SearchResponse search = client.search(request, RequestOptions.DEFAULT);
        //处理参数
        handleResponse(search);
    }

    //### 6.1.高亮请求构建高亮查询必须使用全文检索查询，并且要有搜索关键字，将来才可以对关键字高亮。
    @SneakyThrows
    @Test
    public void highlight() {
        // 1.准备Request
        SearchRequest request = new SearchRequest("hotel");
        // 2.准备DSL
        // 2.1.query
        request.source().query(QueryBuilders.matchQuery("all", "如家"));
        // 2.2.高亮
        request.source().highlighter(new HighlightBuilder().field("name")
                .preTags("<em>").postTags("</em>"));
        // 3.发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 4.解析响应
        handleResponse(response);
    }


    //参数处理
    private void handleResponse(@NotNull SearchResponse response) {

        SearchHits responseHits = response.getHits();

        long total = responseHits.getTotalHits().value;
        System.out.println("总记录数：" + total);

        SearchHit[] hits = responseHits.getHits();

        if (hits != null && hits.length > 0) {
            for (SearchHit hit : hits) {
                String json = hit.getSourceAsString();
                HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);

                HighlightField name = hit.getHighlightFields().get("name");
                if (name != null) {
                    hotelDoc.setName(name.getFragments()[0].toString());
                }

                System.out.println(hotelDoc);
            }
        }
    }


    @AfterEach
    public void after() throws IOException {
        client.close();
    }

}
