package com.example.elasticsearch.controller;

import com.example.elasticsearch.model.ElasticsearchPost;
import com.example.elasticsearch.repository.ElasticsearchPostRepository;
import com.example.elasticsearch.service.ElasticsearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(value = "/elastic", method = RequestMethod.GET)
@Api(value = "search", description = "Operations pertaining to searching the Elasticsearch index")
public class ElasticsearchPostController {

    private ElasticsearchPostRepository elasticsearchPostRepository;
    private ElasticsearchService elasticsearchService;

    public ElasticsearchPostController(ElasticsearchPostRepository elasticsearchPostRepository,
                                       ElasticsearchService elasticsearchService) {

        this.elasticsearchPostRepository = elasticsearchPostRepository;
        this.elasticsearchService = elasticsearchService;
    }

    @RequestMapping(value = "/")
    @ApiOperation(value = "Returns indexed posts")
    public Map<String, List<ElasticsearchPost>> findAll() {

        Iterable<ElasticsearchPost> elasticsearchPosts = elasticsearchPostRepository.findAll();
        final List content = ((AggregatedPageImpl) elasticsearchPosts).getContent();
        Map<String, List<ElasticsearchPost>> elasticsearchPostMap = new HashMap<>();
        elasticsearchPostMap.put("ElasticsearchPosts", content);

        return elasticsearchPostMap;
    }

    @RequestMapping(value = "/{id}")
    @ApiOperation(value = "Returns a post by id")
    public Optional<ElasticsearchPost> findById(@PathVariable("id") long id) {

        Optional<ElasticsearchPost> elasticsearchPost = elasticsearchPostRepository.findById(id);

        return elasticsearchPost;
    }

    /**
     * Performs search within the field input with value input
     *
     * @param field the document field to search
     * @param value the string to search for
     * @return
     */
    @RequestMapping(value = "/simple-search")
    @ApiOperation(value = "Performs search within the field input with value input")
    public Map<String, List<ElasticsearchPost>> fieldContains(@RequestParam("field") String field,
                                                              @RequestParam("value") String value) {

        List<ElasticsearchPost> elasticsearchPosts = elasticsearchService.fieldContains(field, value);
        Map<String, List<ElasticsearchPost>> elasticsearchPostMap = new HashMap<>();
        elasticsearchPostMap.put("ElasticsearchPosts", elasticsearchPosts);

        return elasticsearchPostMap;
    }

    /**
     * Performs dismax search and returns List<ElasticsearchPost> containing the value
     *
     * @param value the string to search for
     * @param size  the number of results to return
     * @return
     */
    @RequestMapping(value = "/dismax-search")
    @ApiOperation(value = "Performs dismax search and returns a list of posts containing the value input")
    public Map<String, List<ElasticsearchPost>> dismaxSearch(@RequestParam("value") String value,
                                                             @RequestParam("size") int size) {

        List<ElasticsearchPost> elasticsearchPosts = elasticsearchService.dismaxSearch(value, size);
        Map<String, List<ElasticsearchPost>> elasticsearchPostMap = new HashMap<>();
        elasticsearchPostMap.put("ElasticsearchPosts", elasticsearchPosts);

        return elasticsearchPostMap;
    }

    /**
     * Performs dismax search and returns count of posts containing value
     *
     * @param value the string to search for
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @RequestMapping(value = "/dismax-search/hits")
    @ApiOperation(value = "Performs dismax search and returns count of posts containing the value input")
    public long dismaxSearchHits(@RequestParam("value") String value) throws ExecutionException, InterruptedException {

        long hits = elasticsearchService.dismaxSearchHits(value);

        return hits;
    }
}
