--liquibase formatted sql

create table community (
  id          char(32)     not null,
  name        varchar(255) not null,
  description text             null
) engine = innodb;

alter table community add constraint pk_community primary key (id);

create table community_member (
  id        char(32)   not null,
  community char(32)   not null,
  member    char(32)   not null,
  since     date           null,
  leader    tinyint(1)     null
) engine = innodb;

alter table community_member add constraint pk_community_member primary key (id);
create index idx_member_community on community_member (community);
create index idx_community_member on community_member (member);
alter table community_member add constraint fk_member_community foreign key (community) references community(id) on delete cascade;
alter table community_member add constraint fk_community_member foreign key (member) references user_account(id) on delete cascade;