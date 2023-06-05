package com.jihai.bitfree.controller;

import com.jihai.bitfree.aspect.LoggedCheck;
import com.jihai.bitfree.aspect.ParameterCheck;
import com.jihai.bitfree.base.BaseController;
import com.jihai.bitfree.base.PageResult;
import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.dto.req.*;
import com.jihai.bitfree.dto.resp.PostDetailResp;
import com.jihai.bitfree.dto.resp.PostItemResp;
import com.jihai.bitfree.dto.resp.ReplyListResp;
import com.jihai.bitfree.dto.resp.UserReplyResp;
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
    public Result<PageResult<PostItemResp>> pageQuery(PageQueryReq pageQueryReq) {
        // 置顶的要减去size
        List<PostItemResp> postItemRespList = postService.pageQuery(pageQueryReq.getPage(), pageQueryReq.getSize(), pageQueryReq.getTopicId(), null, true);
        Integer count = postService.count(pageQueryReq.getTopicId());
        return convertSuccessResult(new PageResult<>(postItemRespList, count));
    }

    @GetMapping("/getDetail")
    @LoggedCheck
    @ParameterCheck
    public Result<PostDetailResp> getDetail(PostDetailReq postDetailReq) {
        return convertSuccessResult(postService.getDetail(postDetailReq.getId()));
    }

    @GetMapping("/getReplyList")
    @ParameterCheck
    @LoggedCheck
    public Result<List<ReplyListResp>> getReplyList(ReplyListReq replyListReq) {
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
    public Result<PageResult<UserReplyResp>> pageQueryUserReply(UserReplyReq userReplyReq) {
        // 如果传递了说明是查看别人，否则是查看自己
        Long userId = userReplyReq.getId() == null ? getCurrentUser().getId() : userReplyReq.getId();

        return convertSuccessResult(replyService.pageQueryUserReplyBySendUserId(userReplyReq.getPage(), userReplyReq.getSize(), userId));
    }

    @PostMapping("/add")
    @ParameterCheck
    @LoggedCheck
    public Result<Boolean> add(@RequestBody AddPostReq addPostReq) {
        postService.add(addPostReq.getTitle(), addPostReq.getContent(), addPostReq.getTopicId(), getCurrentUser().getId());
        return convertSuccessResult(true);
    }


    @GetMapping("/getUserMessageList")
    @ParameterCheck
    @LoggedCheck
    public Result<PageResult<UserReplyResp>> getUserMessageList(MessageReplyReq messageReplyReq) {
        Long userId = messageReplyReq.getId() != null ? messageReplyReq.getId() : getCurrentUser().getId();
        return convertSuccessResult(replyService.pageQueryUserReplyByReceiverId(messageReplyReq.getPage(), messageReplyReq.getSize(), userId));
    }

    @GetMapping("/getByUserId")
    @ParameterCheck
    @LoggedCheck
    public Result<PageResult<PostItemResp>> pageQueryUserPost(UserPostReq userPostReq) {
        Long userId = userPostReq.getId() != null ? userPostReq.getId() : getCurrentUser().getId();
        List<PostItemResp> postItemRespList = postService.pageQuery(userPostReq.getPage(), userPostReq.getSize(), null, userId, false);
        Integer count = postService.countByUserId(userId);
        return convertSuccessResult(new PageResult<>(postItemRespList, count));
    }

    @GetMapping("/getRankList")
    @ParameterCheck
    @LoggedCheck
    public Result<List<RankPostItemResp>> getRankList() {
        return convertSuccessResult(postService.getRankList());
    }
}
