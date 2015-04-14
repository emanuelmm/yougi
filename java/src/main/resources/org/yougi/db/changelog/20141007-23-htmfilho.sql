--liquibase formatted sql

alter table message_template add format varchar(15) null;