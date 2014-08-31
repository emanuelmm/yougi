--liquibase formatted sql

alter table event change start_time tinyint(1) null;