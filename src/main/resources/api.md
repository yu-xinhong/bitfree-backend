
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


- 后门工具 post /user/addUser

request : {
email : "xxx",
"secret" : xxx
}

response : {
"password" : xxx
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
title : xxx,
creatorName: xxx,
updateTime: xxx,
repleyCount: 11
}
]
}


- 帖子详情get /post/detail

request : {
id : 1
}

response : {
id : 1,
content : xxx
creatorName : xxx,
creatorId : 11,
createTime: xxx,
updateTime: xxx
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
createTime,
creatorId,
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


- 
