package com.fajar.android.chatting1.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fajar.android.chatting1.models.NewsPost;
import com.fajar.android.chatting1.models.Post;
import com.fajar.android.chatting1.models.PostResponse;
import com.fajar.android.chatting1.util.Logs;
import com.fajar.android.chatting1.util.MapUtil;

public class NewsService {

    private static NewsService instance = null;
    private ObjectMapper objectMapper = new ObjectMapper();
    final RestTemplate restTemplate = new RestTemplate();
    private NewsService(){
        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);

        restTemplate.setErrorHandler(errorHandler());
    }


    private ResponseErrorHandler errorHandler() {
        return new CustomErrorHandler();
    }

    public static  NewsService instance(){
        if(instance == null){
            instance = new NewsService();
        }
        return instance;
    }

    public PostResponse getAgenda()  {
        Logs.log("Call get agenda");
        PostResponse response = callGetAgenda();

        if (response.getPosts() instanceof List) {
            List rawPosts = (List) response.getPosts();
            List<Post> posts = (MapUtil.convertMapList(rawPosts, Post.class));
            response.setAgendas(posts);

        }
        response.setLastUpdated(new Date());
        return response;
    }

    public PostResponse getNews(int page) {
        PostResponse response = callGetNews(page);
        try{if(null!= response.getPosts()) {

            Logs.log("response.getPosts()", response.getPosts().getClass());
            if (response.getPosts() instanceof Map  ) {

                String json = objectMapper.writeValueAsString(response.getPosts());
                NewsPost newsPost = objectMapper.readValue(json, NewsPost.class);
                response.setNewsPost(newsPost);
            }
            Logs.log("current page: ", response.getCurrentPage());
        }}catch (Exception e){

        }
        response.setLastUpdated(new Date());
        return response;
    }


   private HttpEntity<String> httpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setConnection("close");
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//        headers.add("Accept-Encoding", "identity");
        headers.add("Accept", "application/json");
        headers.add("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        HttpEntity<String> entity = new HttpEntity<String>( headers);
        return entity;

    }

    private PostResponse callGetAgenda()   {
        String endPoint = "http://chatting.sch.id/index.php/api/homepage/agenda";
        System.out.println("callGetAgenda to: "+endPoint);
        try {
            ResponseEntity<PostResponse> response = restTemplate.exchange(endPoint, HttpMethod.GET, httpEntity(),
                    PostResponse.class);
            return   response.getBody();
        }catch ( Exception ex){
            Logs.log("ERROR get agenda: ", ex);
            throw ex;
        }
    }

    public PostResponse callGetNews(int page) {
        String endPoint = "http://chatting.sch.id/index.php/api/homepage/news?page=" + page;
        Logs.log("call service get news ", endPoint);
        try{
//        String endPoint = "http://192.168.0.103/chatting/get_news.json";
        ResponseEntity<PostResponse> response = restTemplate.exchange(endPoint, HttpMethod.GET, httpEntity(),
                PostResponse.class);
        return response.getBody();
    }catch (Exception ex){
        Logs.log("ERROR get news: ", ex);
        throw ex;
    }
    }


}
