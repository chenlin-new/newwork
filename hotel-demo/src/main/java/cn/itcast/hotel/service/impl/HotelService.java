package cn.itcast.hotel.service.impl;

import cn.itcast.hotel.mapper.HotelMapper;
import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.pojo.HotelDoc;
import cn.itcast.hotel.pojo.PageResult;
import cn.itcast.hotel.pojo.RequestParams;
import cn.itcast.hotel.service.IHotelService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HotelService extends ServiceImpl<HotelMapper, Hotel> implements IHotelService {

    @Autowired
    private RestHighLevelClient client;

    //根据条件搜索
    @SneakyThrows
    @Override
    public PageResult list(RequestParams params) {
        //1. 创建请求对象
        SearchRequest request = new SearchRequest("hotel");

        //2. 设置查询参数   DSL
        //String key = params.getKey();
        //if (StringUtils.isNotBlank(key)) {
        //request.source().query(QueryBuilders.matchQuery("name", key));
        //} else {
        //    request.source().query(QueryBuilders.matchAllQuery());
        //}
        handlerBasicParam(request, params);//处理查询的参数

        //分页
        int page = params.getPage();
        int size = params.getSize();
        request.source().from((page - 1) * size);
        request.source().size(size);

        //进行地理位置的排序
        if (StringUtils.isNotBlank(params.getLocation())) {
            request.source().sort(SortBuilders
                    .geoDistanceSort("location", new GeoPoint(params.getLocation()))
                    .order(SortOrder.ASC)
                    .unit(DistanceUnit.KILOMETERS)
            );
        }

        //3. 发请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        //4. 解析响应的结果
        SearchHits responseHits = response.getHits();

        //总记录数
        Long total = responseHits.getTotalHits().value;

        List<HotelDoc> list = new ArrayList<>();

        //获取hits
        SearchHit[] hits = responseHits.getHits();
        //遍历
        for (SearchHit hit : hits) {
            //获取json数据
            String json = hit.getSourceAsString();
            //把json转为对象
            HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);

            Object[] sortValues = hit.getSortValues();
            if (sortValues.length > 0) {
                hotelDoc.setDistance(sortValues[0]);
            }

            //设置到list集合中
            list.add(hotelDoc);
        }

        return new PageResult(Math.toIntExact(total), list);
    }

    //聚合查询
    @SneakyThrows
    @Override
    public Map<String, List<String>> filters(RequestParams params) {
        //1. 创建请求对象
        SearchRequest request = new SearchRequest("hotel");

        //2. 设置请求参数
        //2.1 设置查询条件  需要对查询的结果进行聚合
        handlerBasicParam(request, params);

        //文档的查询结果设置为0   为了节省网络资源
        request.source().size(0);

        //2.2 聚合操作
        //城市聚合
        request.source().aggregation(AggregationBuilders
                .terms("cityAggsName")
                .field("city")
                .order(BucketOrder.count(false))
                .size(10)
        );

        //星级聚合
        request.source().aggregation(AggregationBuilders
                .terms("starNameAggsName")
                .field("starName")
                .order(BucketOrder.count(false))
                .size(10)
        );

        //品牌聚合
        request.source().aggregation(AggregationBuilders
                .terms("brandAggsName")
                .field("brand")
                .order(BucketOrder.count(false))
                .size(10)
        );


        //3. 发起请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        //4. 解析聚合的结果
        Map<String, List<String>> map = new HashMap<>();
        map.put("city", getByAggName(response, "cityAggsName"));
        map.put("starName", getByAggName(response, "starNameAggsName"));
        map.put("brand", getByAggName(response, "brandAggsName"));

        return map;
    }

    @SneakyThrows
    @Override
    public List<String> getSuggestions(String prefix) {
        //1. 创建请求对象
        SearchRequest request = new SearchRequest("hotel");

        //2. 设置请求参数
        request.source().suggest(new SuggestBuilder()
                .addSuggestion("suggestionName",
                        SuggestBuilders.completionSuggestion("suggestion")
                                .prefix(prefix)//补全查询的关键字
                                .skipDuplicates(true)
                                .size(10)
                ));


        //3. 发起请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        //4. 解析补全的结果
        //获取补全数据
        CompletionSuggestion suggestion = response.getSuggest().getSuggestion("suggestionName");

        List<CompletionSuggestion.Entry.Option> options = suggestion.getOptions();

        //声明存放结果的结合
        List<String> list = new ArrayList<>();
        for (CompletionSuggestion.Entry.Option option : options) {
            list.add(option.getText().toString());
        }

        return list;
    }

    //新增或修改
    @SneakyThrows
    @Override
    public void insertOrUpdate(Long id) {
        //1. 根据id从MySQL查询酒店数据
        Hotel hotel = getById(id);

        //2. 把MySQL的查询结果转为hotelDoc对象
        HotelDoc hotelDoc = new HotelDoc(hotel);

        //3. 转为json数据
        String json = JSON.toJSONString(hotelDoc);

        //4. 创建请求对象
        IndexRequest request = new IndexRequest("hotel").id(id.toString());

        //5. 设置新增或者修改的参数
        request.source(json, XContentType.JSON);

        //6. 发起请求
        client.index(request, RequestOptions.DEFAULT);
    }

    //删除
    @SneakyThrows
    @Override
    public void deleteById(Long id) {
        //1 创建请求对象
        DeleteRequest request = new DeleteRequest("hotel").id(id.toString());
        //2 发起请求
        client.delete(request, RequestOptions.DEFAULT);
    }



        @Override
        public void insertById(Long id) {
            try {
                // 0.根据id查询酒店数据
                Hotel hotel = getById(id);
                // 转换为文档类型
                HotelDoc hotelDoc = new HotelDoc(hotel);

                // 1.准备Request对象
                IndexRequest request = new IndexRequest("hotel").id(hotel.getId().toString());
                // 2.准备Json文档
                request.source(JSON.toJSONString(hotelDoc), XContentType.JSON);
                // 3.发送请求
                client.index(request, RequestOptions.DEFAULT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


    //解析结果，获取聚合数据
    private List<String> getByAggName(SearchResponse response, String aggName) {
        //获取聚合结果
        Terms terms = response.getAggregations().get(aggName);

        //获取Buckets
        List<? extends Terms.Bucket> buckets = terms.getBuckets();

        //遍历，封装聚合结果
        List<String> list = new ArrayList<>();
        for (Terms.Bucket bucket : buckets) {
            list.add(bucket.getKeyAsString());
        }

        return list;
    }

    //处理查询的参数
    private void handlerBasicParam(SearchRequest request, RequestParams params) {
        //创建bool查询对象
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();

        //关键词查询
        String key = params.getKey();
        if (StringUtils.isNotBlank(key)) {
            boolBuilder.must(QueryBuilders.matchQuery("name", key));
        } else {
            boolBuilder.must(QueryBuilders.matchAllQuery());
        }

        //品牌过滤：是keyword类型，用term查询
        if (StringUtils.isNotBlank(params.getBrand())) {
            boolBuilder.filter(QueryBuilders.termQuery("brand", params.getBrand()));
        }

        //星级过滤：是keyword类型，用term查询
        if (StringUtils.isNotBlank(params.getStarName())) {
            boolBuilder.filter(QueryBuilders.termQuery("starName", params.getStarName()));
        }

        //价格过滤：是数值类型，用range查询
        if (params.getMinPrice() != null && params.getMaxPrice() != null) {
            boolBuilder.filter(QueryBuilders
                    .rangeQuery("price")
                    .gte(params.getMinPrice())
                    .lte(params.getMaxPrice())
            );
        }

        //城市过滤：是keyword类型，用term查询
        if (StringUtils.isNotBlank(params.getCity())) {
            boolBuilder.filter(QueryBuilders.termQuery("city", params.getCity()));
        }

        //算分函数
        FunctionScoreQueryBuilder scoreQueryBuilder = QueryBuilders.functionScoreQuery(
                //原始查询条件
                boolBuilder,
                //过滤数据   数组
                new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                        new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                QueryBuilders.termQuery("isAD", true),//创建过滤的条件
                                ScoreFunctionBuilders.weightFactorFunction(10000)//计算分值
                        )
                }
        );

        //设置加权模式
        scoreQueryBuilder.scoreMode(FunctionScoreQuery.ScoreMode.SUM);

        //设置查询条件到  request对象中
        request.source().query(scoreQueryBuilder);
    }
}
