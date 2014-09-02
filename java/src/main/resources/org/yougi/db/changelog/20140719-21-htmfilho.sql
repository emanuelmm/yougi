--liquibase formatted sql

create table user_session (
  id            char(32)     not null,
  session_id    varchar(100) not null,
  start_session timestamp    not null,
  end_session   timestamp        null,
  user_account  char(32)         null,
  ip_address    varchar(64)      null
) engine = innodb;

alter table user_session add constraint pk_user_session primary key (id);
create index idx_user_account_session on user_session (user_account);
alter table user_session add constraint fk_user_account_session foreign key (user_account) references user_account (id) on delete cascade;