## feature_markdown
## 为了支持评论的楼中楼展示，添加评论的根节点字段，并使用存储过程更新旧数据
alter table reply add column target_reply_root_id bigint null comment '评论根节点id';

# 递归更新所有后代评论的根节点id。 幂等操作
drop procedure recursiveUpdateReplyRoot;
create procedure recursiveUpdateReplyRoot()
begin
    declare done int default false;
    declare variable_id long default 0;
    # 选取所有的根评论进行遍历
    declare reply_cursor cursor for select id from reply where target_reply_id is null;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = true;
    open reply_cursor;
    updateLoop:loop
        fetch reply_cursor into variable_id;
        if done then
            leave updateLoop;
        end if;

        with recursive temp as (
            select r1.*  from reply r1 where id = variable_id
            union all
            select r2.* from reply r2 inner join temp on r2.target_reply_id = temp.id
        )

        update reply set target_reply_root_id = variable_id where
            exists(select 1 from temp where reply.id = temp.id and reply.id != variable_id);

    end loop updateLoop;
    close reply_cursor;
end;
call recursiveUpdateReplyRoot()