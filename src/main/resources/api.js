
### 外层统一结构

{
data : {},
code : 0,
message: ""
}

--- 
### 用户模块


- 登陆post /user/login

request: {
"email" : "",
"password" : ""
}


response: {

 id : 1,
 token : "xxxtoken"
}


- 更新用户信息 post /user/update

request: {
nickname : "",
city: "",
seniority: "",
contact: ""
}

response: {
true
}

- 校验昵称 get /user/checkName

request: {
nickName : ""
}

response: {
true
}

- 退出  post /user/logout

request : {

}

response : {
true
}


- 获取用户信息 get /user/detail

request : {
id : 1
}

response : {
nickName : xxx,
seniority : 11,
city : xxx,
position
}


- 保存用户信息 post /user/save

request : {
    name : xxx,
    city : xxx,
    position : xxxxx,
    seniority : 11,
    password : xxx,
    avatar : xxx
}

response : {
    true
}


- 是否修改过密码 post /user/hadModifyPassword

request : {}

response : {
    true
}


- 修改密码 post /user/updatePassword

request : {
    oldPwd: '',
    pwd: ''
}

response : {
    true
}

- 后门工具 post /user/addUser

request : {
email : "xxx",
"secret" : xxx
}

response : {
"password" : xxx
}

- 今日活跃用户 get user/getActivityList

request {}

response [{
    name : xxx,
    count : 1
}]


- 重置用户密码 post user/resetpassword

request {
    id : 11,
    secret : xxx
}

response {
    true
}


--- 
### 帖子

- 分页获取帖子列表get /post/pageQuery

request : {
page : 1,
size : 20
}

response : {
total : 1000,
list : [
{
id: 1,
avatar : xxx
title : xxx,
creatorName: xxx,
updateTime: xxx,
repleyCount: 11,
updateUserName : "xxx"
}
]
}


- 帖子详情get /post/getDetail

request : {
id : 1
}

response : {
id : 1,
avatar : xxx,
content : xxx,
level : 1,
creatorName : xxx,
creatorId : 11,
createTime: xxx,
updateTime: xxx,
viewCount : 1,
poster: xxx,
likeCount : 1,
likePost : true
}


- 获取视频列表  get/post/pageQueryVideoList

request : {
}

response : {
id : 1,
poster : xxxx,
createTime : xxx,
title : xxx,
creatorName : 
}

- 获取评论列表get /post/getReplyList

request : {
id: 1 // 帖子id
}

response : [
{
id : 222,
replyContent: xxx,
name : xxx,
createTime : xx,
creatorId : 1,
like : true,
subReplyList : [
{
id : 444,
replyContent : xxx,
name : xxx,
createTime
}
]
}
]

- 添加评论 post /post/reply

request : {
postId : 1,
replyId : 2, // 如果是回复子评论
replyContent : xxx
}

response : {
true
}


- 当前收到的回复 get /post/replyCount

request : {}

response : {11}

- 点击查看，已读 post /post/read

request : {}

response : {true}


- 获取某个用户的回复 get /post/pageQueryUserReply

request : {
id : 11,
page : 1,
size : 20
}

response : {
total : 2222,
list : [
{
id : 11,  //哪一个帖子id
content : xxx,  // 回复的内容
reply : xxx,  //回复对象
createTime : xx,
sendUserName : xxx,
postId : 1
}
]
}

- 获取某个用户发布的帖子 /post/getByUserId

request {
  id: 1 // 不传取当前
}

response : {
    total : 100,
    list : [

 
    ] 
}

- 发布帖子post  /post/add

request : {
title: xx
content: xxx,
topicId : 1
}

response : {
true
}


- 帖子日榜post /post/dayRankList

request {
}

response {
    id : 1,
    title : xxx,
    replyCount: xx
}

--- 
### topic
- 获取置topic get  /topic/getAllTopic

request : {}

response : [
{
id : 111,
name : xxx
}
]


---
config
获取配置  config get /config/getDefaultPoster

request {
}

response {
   xxxx
}


-- 
file 文件接口
file get /file/getById

request {
    id
}

response {
    url: xx,
    type : xx,  // 1-video, 2-image
    poster : xxx,
    id : xxx
}


# 签到功能
-- 查询 get  user/getCheckIn

request {}

response {
    true
}

-- 签到 post user/checkIn

request {}

response {}


# 私信功能
-- 查询当前最近20条消息
get message/getRecentList

request : {
    page : 1,
    size : 2
}

response : [
    {
        id : xx,
        userId : xxx,
        userName : xxx,
        create_time : xxx,
        avatar : xxx
    }
]


-- 发送消息
request
post message/sendMessage
{
    content: xxx
}

response : {
    true
}

-- 查询在线人员
request post message/getLiveUserList
{
}

response [
    {
        id : 1,
        name : xxx,
        avatar : xxx
    }
]

