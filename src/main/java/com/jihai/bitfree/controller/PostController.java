package com.jihai.bitfree.controller;

import com.jihai.bitfree.aspect.LoggedCheck;
import com.jihai.bitfree.aspect.ParameterCheck;
import com.jihai.bitfree.base.BaseController;
import com.jihai.bitfree.base.PageResult;
import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.dto.req.*;
import com.jihai.bitfree.dto.resp.*;
import com.jihai.bitfree.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
public class PostController extends BaseController {

    @Autowired
    private PostService postService;

    @Autowired
    private ReplyService replyService;

    @Autowired
    private UserLikeService userLikeService;

    @Autowired
    private CollectService collectService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/pageQuery")
    @ParameterCheck
    @LoggedCheck
    public Result<PageResult<PostItemResp>> pageQuery(PageQueryReq pageQueryReq) {
        // 置顶的要减去size
        List<PostItemResp> postItemRespList = postService.pageQuery(pageQueryReq.getPage(), pageQueryReq.getSize(), pageQueryReq.getTopicId(), pageQueryReq.getSearchText(), null, true);
        Integer count = postService.count(pageQueryReq.getTopicId());
        return convertSuccessResult(new PageResult<>(postItemRespList, count));
    }

    @GetMapping("/getDetail")
    @LoggedCheck
    @ParameterCheck
    public Result<PostDetailResp> getDetail(PostDetailReq postDetailReq) {
        PostDetailResp detail = postService.getDetail(postDetailReq.getId());
        detail.setLikePost(userLikeService.isLikePost(postDetailReq.getId(), getCurrentUser().getId()));
        return convertSuccessResult(detail);
    }

    @GetMapping("/getReplyList")
    @ParameterCheck
    @LoggedCheck
    public Result<List<ReplyListResp>> getReplyList(ReplyListReq replyListReq) {
        List<ReplyListResp> replyList = replyService.getReplyList(replyListReq.getId(), replyListReq.getOrder());
        userLikeService.fillUserLike(replyList, getCurrentUser().getId());
        return convertSuccessResult(replyList);
    }

    @PostMapping("/reply")
    @ParameterCheck
    @LoggedCheck
    public Result<Boolean> reply(@RequestBody ReplyReq replyReq) {
        return convertSuccessResult(replyService.reply(replyReq.getPostId(), replyReq.getReplyId(), getCurrentUser().getId(), replyReq.getReplyContent()));
    }


    @GetMapping("/msgCount")
    @ParameterCheck
    @LoggedCheck
    public Result<Integer> msgCount() {
        Integer replyCount = replyService.replyCount(getCurrentUser().getId());
        Integer notificationCount = notificationService.unReadNotificationCount(getCurrentUser().getId());
        return convertSuccessResult(replyCount + notificationCount);
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
    public Result<PageResult<PostItemResp>> getByUserId(UserPostReq userPostReq) {
        Long userId = userPostReq.getId() != null ? userPostReq.getId() : getCurrentUser().getId();
        List<PostItemResp> postItemRespList = postService.pageQuery(userPostReq.getPage(), userPostReq.getSize(), null, null, userId, false);
        Integer count = postService.countByUserId(userId);
        return convertSuccessResult(new PageResult<>(postItemRespList, count));
    }

    @GetMapping("/getRankList")
    @ParameterCheck
    @LoggedCheck
    public Result<List<RankPostItemResp>> getRankList() {
        return convertSuccessResult(postService.getRankList());
    }



    @PostMapping("/deletePost")
    @ParameterCheck
    public Result<Boolean> deletedPost(@RequestBody DeletePostReq deletePostReq) {
        super.checkSecret(deletePostReq.getSecret());
        return convertSuccessResult(postService.deletePost(deletePostReq.getPostId()));
    }

    @PostMapping("/deleteReply")
    @ParameterCheck
    public Result<Boolean> deleteReply(@RequestBody DeleteReplyReq deleteReplyReq) {
        // 超级管理员 可以不登陆，但是得提供密钥
        if (getCurrentUser() == null) {
            checkSecret(deleteReplyReq.getSecret());
        } else {
            // 只能删除自己得回复
            postService.checkIsCurUserReply(deleteReplyReq.getId(), getCurrentUser().getId());
        }
        return convertSuccessResult(postService.deleteReply(deleteReplyReq.getId()));
    }


    @GetMapping("/pageQueryVideoList")
    @LoggedCheck
    public Result<PageResult<VideoListResp>> pageQueryVideoList(PageQueryReq pageQueryReq) {
        return convertSuccessResult(postService.pageQueryVideoList(pageQueryReq.getPage(), pageQueryReq.getSize()));
    }

    @PostMapping("/collect")
    @LoggedCheck
    public Result<Boolean> collect(@RequestBody CollectReq collectReq) {
        return convertSuccessResult(collectService.collect(collectReq.getPostId(), getCurrentUser().getId(), CollectService.CollectTypeEnum.POST.getType()));
    }

    @PostMapping("/cancelCollect")
    @LoggedCheck
    public Result<Boolean> cancelCollect(@RequestBody CancelCollectReq cancelCollectReq) {
        return convertSuccessResult(collectService.cancelCollect(cancelCollectReq.getPostId(), getCurrentUser().getId(), CollectService.CollectTypeEnum.POST.getType()));
    }

    @GetMapping("/getCollectList")
    @LoggedCheck
    public Result<PageResult<CollectResp>> getCollectList(PageQueryReq pageQueryReq) {
        List<CollectResp> collectRespList = collectService.getCollectList(getCurrentUser().getId(), pageQueryReq.getPage(), pageQueryReq.getSize());
        Integer total = collectService.countTotal(getCurrentUser().getId());
        return convertSuccessResult(new PageResult<>(collectRespList, total));
    }

    @GetMapping("/hasCollected")
    @LoggedCheck
    public Result<Boolean> hasCollected(HasCollectReq hasCollectReq) {
        return convertSuccessResult(collectService.hasCollected(hasCollectReq.getPostId(), getCurrentUser().getId()));
    }
}
