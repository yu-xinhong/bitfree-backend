package com.jihai.bitfree.controller;

import com.jihai.bitfree.aspect.LoggedCheck;
import com.jihai.bitfree.aspect.ParameterCheck;
import com.jihai.bitfree.base.BaseController;
import com.jihai.bitfree.base.PageResult;
import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.dto.req.*;
import com.jihai.bitfree.dto.resp.PostDetailDTO;
import com.jihai.bitfree.dto.resp.PostItemDTO;
import com.jihai.bitfree.dto.resp.ReplyListDTO;
import com.jihai.bitfree.dto.resp.UserReplyDTO;
import com.jihai.bitfree.service.PostService;
import com.jihai.bitfree.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
@CrossOrigin("*")
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
        List<PostItemDTO> postItemDTOList = postService.pageQuery(pageQueryReq.getPage(), pageQueryReq.getSize(), pageQueryReq.getTopicId());
        Integer count = postService.count(pageQueryReq.getTopicId());
        return convertSuccessResult(new PageResult<>(postItemDTOList, count));
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

    @PostMapping("/reply")
    @ParameterCheck
    @LoggedCheck
    public Result<Boolean> reply(@RequestBody ReplyReq replyReq) {
        return convertSuccessResult(replyService.reply(replyReq.getPostId(), replyReq.getReplyId(), getCurrentUser().getId(), replyReq.getReplyContent()));
    }


    @GetMapping("/replyCount")
    @ParameterCheck
    @LoggedCheck
    public Result<Integer> replyCount() {
        return convertSuccessResult(replyService.replyCount(getCurrentUser().getId()));
    }


    @PostMapping("/read")
    @ParameterCheck
    @LoggedCheck
    public Result<Boolean> read() {
        return convertSuccessResult(replyService.read(getCurrentUser().getId()));
    }


    @GetMapping("/pageQueryUserReply")
    @ParameterCheck
    @LoggedCheck
    public Result<PageResult<UserReplyDTO>> pageQueryUserReply(UserReplyReq userReplyReq) {
        // 如果传递了说明是查看别人，否则是查看自己
        Long userId = userReplyReq.getId() == null ? getCurrentUser().getId() : userReplyReq.getId();

        return convertSuccessResult(replyService.pageQueryUserReply(userReplyReq.getPage(), userReplyReq.getSize(), userId));
    }

    @PostMapping("/add")
    @ParameterCheck
    @LoggedCheck
    public Result<Boolean> add(@RequestBody AddPostReq addPostReq) {
        postService.add(addPostReq.getTitle(), addPostReq.getContent(), addPostReq.getTopicId(), getCurrentUser().getId());
        return convertSuccessResult(true);
    }
}
