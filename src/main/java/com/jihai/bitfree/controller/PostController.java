package com.jihai.bitfree.controller;

import com.jihai.bitfree.aspect.LoggedCheck;
import com.jihai.bitfree.aspect.ParameterCheck;
import com.jihai.bitfree.base.BaseController;
import com.jihai.bitfree.base.PageResult;
import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.dto.req.PageQueryReq;
import com.jihai.bitfree.dto.req.PostDetailReq;
import com.jihai.bitfree.dto.req.ReplyListReq;
import com.jihai.bitfree.dto.resp.PostDetailDTO;
import com.jihai.bitfree.dto.resp.PostItemDTO;
import com.jihai.bitfree.dto.resp.ReplyListDTO;
import com.jihai.bitfree.service.PostService;
import com.jihai.bitfree.service.ReplyService;
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

    @Autowired
    private ReplyService replyService;

    @GetMapping("/pageQuery")
    @ParameterCheck
    @LoggedCheck
    public Result<PageResult<PostItemDTO>> pageQuery(PageQueryReq pageQueryReq) {
        // 置顶的要减去size
        List<PostItemDTO> postItemDTOList = postService.pageQuery(pageQueryReq.getPage(), pageQueryReq.getSize());
        Integer count = postService.count();
        return convertSuccessResult(new PageResult<PostItemDTO>(postItemDTOList, count));
    }

    @GetMapping("/getDetail")
    @LoggedCheck
    @ParameterCheck
    public Result<PostDetailDTO> getDetail(PostDetailReq postDetailReq) {
        return convertSuccessResult(postService.getDetail(postDetailReq.getId()));
    }

    @GetMapping("/getReplyList")
    @ParameterCheck
    @LoggedCheck
    public Result<List<ReplyListDTO>> getReplyList(ReplyListReq replyListReq) {
        return convertSuccessResult(replyService.getReplyList(replyListReq.getId()));
    }
}
