package com.jihai.bitfree.controller;

import com.jihai.bitfree.aspect.ParameterCheck;
import com.jihai.bitfree.base.BaseController;
import com.jihai.bitfree.base.PageResult;
import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.dto.req.PageQueryReq;
import com.jihai.bitfree.dto.resp.PostItemDTO;
import com.jihai.bitfree.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/post")
public class PostController extends BaseController {

    @Autowired
    private PostService postService;

    @GetMapping("/pageQuery")
    @ParameterCheck
    public Result<PageResult<PostItemDTO>> pageQuery(PageQueryReq pageQueryReq) {
        // 置顶的要减去size
        List<PostItemDTO> postItemDTOList = postService.pageQuery(pageQueryReq.getPage(), pageQueryReq.getSize());
        Integer count = postService.count();
        return convertSuccessResult(new PageResult<PostItemDTO>(postItemDTOList, count));
    }
}
